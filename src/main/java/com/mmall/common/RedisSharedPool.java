package com.mmall.common;

import com.mmall.util.PropertiesUtil;
import redis.clients.jedis.*;
import redis.clients.util.Hashing;
import redis.clients.util.Sharded;
import sun.security.provider.SHA;

import java.util.ArrayList;
import java.util.List;

public class RedisSharedPool {

    private static ShardedJedisPool pool;
    private static Integer maxTotal = Integer.valueOf(PropertiesUtil.getProperty("redis.max.total", "20")); //最大链接数
    private static Integer maxIdle = Integer.valueOf(PropertiesUtil.getProperty("redis.max.idle", "10"));//最大空闲数
    private static Integer minIdle = Integer.valueOf(PropertiesUtil.getProperty("redis.min.idle", "2"));//最小空闲
    private static Boolean testOnBorrow = Boolean.valueOf(PropertiesUtil.getProperty("redis.test.borrow", "true"));//再borrow一个jedis实例的时候，是否需要进行验证操作，只有可用才borrow
    private static Boolean testOnReturn = Boolean.valueOf(PropertiesUtil.getProperty("redis.test.return", "true"));//还，放回redispool的jedis实例

    private static String redis1Ip = PropertiesUtil.getProperty("redis1.ip");//最大空闲数
    private static Integer redis1Port = Integer.valueOf(PropertiesUtil.getProperty("redis1.port", "6379"));//最大空闲数

    private static String redis2Ip = PropertiesUtil.getProperty("redis2.ip");//最大空闲数
    private static Integer redis2Port = Integer.valueOf(PropertiesUtil.getProperty("redis2.port", "6380"));//最大空闲数

    private static void initPool() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);
        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);

        config.setBlockWhenExhausted(true);//设置连接池里的链接都储在busy状态时候，需要阻塞吗？true--阻塞直到超时--false--抛出异常
        JedisShardInfo info1 = new JedisShardInfo(redis1Ip, redis1Port, 2000);
        JedisShardInfo info2 = new JedisShardInfo(redis2Ip, redis2Port, 2000);

        List<JedisShardInfo> jedisShardInfoList = new ArrayList<JedisShardInfo>(2);
        jedisShardInfoList.add(info1);
        jedisShardInfoList.add(info2);
        pool = new ShardedJedisPool(config, jedisShardInfoList, Hashing.MURMUR_HASH, Sharded.DEFAULT_KEY_TAG_PATTERN);

    }

    static {//类加载的时候就初始化连接池
        initPool();

    }

    public static ShardedJedis getJedis() {
        return pool.getResource();
    }

    public static void returnResource(ShardedJedis jedis) {
        pool.returnResource(jedis);

    }

    public static void returnBrokenResource(ShardedJedis jedis) {
        pool.returnBrokenResource(jedis);
    }

    public static void main(String[] args) {
        ShardedJedis jedis = pool.getResource();
        for (int i = 0; i < 10; i++) {
            jedis.set("key" + i, "value" + i);
        }
        returnResource(jedis);
        pool.destroy();
        System.out.println("program is end ");
    }

}
