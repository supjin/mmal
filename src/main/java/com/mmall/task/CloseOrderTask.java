package com.mmall.task;

import com.mmall.common.Const;
import com.mmall.service.IOrderService;
import com.mmall.util.PropertiesUtil;
import com.mmall.util.RedisShardedPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CloseOrderTask {

    @Autowired
    private IOrderService orderService;

    public void closeOrderTaskV1() {
        log.info("关闭订单定时任务启动");

        int hour = Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time"), 2);
        orderService.closeOrder(hour);
        log.info("关闭订单定时任务结束");
    }

    // @Scheduled(cron = "0 */1 * * * ?")
    public void closeOrderTaskV2() {
        log.info("关闭订单定时任务启动");
        long lockTimeout = Long.parseLong(PropertiesUtil.getProperty("lock.timeout"));
        Long setnxResult = RedisShardedPoolUtil.setnx(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK, String.valueOf(System.currentTimeMillis() + lockTimeout));
        if (setnxResult != null && setnxResult.intValue() == 1) {
            //如果返回值是1.代表设置成功，获取锁

            closeOrder(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);

        } else {
            log.info("没有获得分布式锁：{}", Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        }

        log.info("关闭订单定时任务结束");
    }

    @Scheduled(cron = "0 */1 * * * ?")
    public void closeOrderTaskV3() {
        log.info("关闭订单定时任务启动");
        long lockTimeout = Long.parseLong(PropertiesUtil.getProperty("lock.timeout"));
        Long setnxResult = RedisShardedPoolUtil.setnx(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK, String.valueOf(System.currentTimeMillis() + lockTimeout));
        if (setnxResult != null && setnxResult.intValue() == 1) {
            //如果返回值是1.代表设置成功，获取锁
            closeOrder(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        } else {
            //未获取到锁，继续判断时间戳，看看是否可以重置并获取到锁
            Long old_value = Long.parseLong(RedisShardedPoolUtil.get(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK));
            if (old_value != null || System.currentTimeMillis() > old_value) {
                Long getsetResult = Long.valueOf(RedisShardedPoolUtil.getset(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK, String.valueOf(System.currentTimeMillis() + lockTimeout)));
                if (getsetResult == null || old_value == getsetResult) {
                    orderService.closeOrder(Integer.valueOf(PropertiesUtil.getProperty("close.order.task.time")));
                } else {
                    log.info("没有获得分布式锁：{}", Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);

                }
            } else {
                log.info("没有获得分布式锁：{}", Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);

            }
        }

        log.info("关闭订单定时任务结束");
    }


    private void closeOrder(String lockName) {
        RedisShardedPoolUtil.expire(lockName, 50);//有效期50s防止死锁
        log.info("获取{},ThreadName:{}", Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK, Thread.currentThread().getName());
        int hour = Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time"));
        orderService.closeOrder(hour);
        RedisShardedPoolUtil.del(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        log.info("释放:{},ThreadName:{}", Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK, Thread.currentThread().getName());
        log.info("==============");
    }


}
