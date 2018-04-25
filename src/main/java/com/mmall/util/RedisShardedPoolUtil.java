package com.mmall.util;

import com.mmall.common.RedisPool;
import com.mmall.common.RedisSharedPool;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ShardedJedis;

@Slf4j
public class RedisShardedPoolUtil {

    /**
     * 设置有效期，单位m
     *
     * @param key
     * @param exTime
     * @return
     */
    public static Long expire(String key, int exTime) {
        ShardedJedis jedis = null;
        Long result = null;
        try {
            jedis = RedisSharedPool.getJedis();
            result = jedis.expire(key, exTime);
        } catch (Exception e) {
            log.error("expire key:{} error", key, e);
            RedisSharedPool.returnBrokenResource(jedis);
            return null;
        }
        RedisSharedPool.returnResource(jedis);

        return result;
    }

    //exTime--单位为秒
    public static String setEx(String key, String value, int exTime) {
        ShardedJedis jedis = null;
        String result = null;
        try {
            jedis = RedisSharedPool.getJedis();
            result = jedis.setex(key, exTime, value);
        } catch (Exception e) {
            log.error("setex key:{} value:{} error", key, value, e);
            RedisSharedPool.returnBrokenResource(jedis);
            return null;
        }
        RedisSharedPool.returnResource(jedis);

        return result;
    }

    //exTime--单位为秒
    public static String set(String key, String value) {
        ShardedJedis jedis = null;
        String result = null;
        try {
            jedis = RedisSharedPool.getJedis();
            result = jedis.set(key, value);
        } catch (Exception e) {
            log.error("set key:{} value:{} error", key, value, e);
            RedisSharedPool.returnBrokenResource(jedis);
            return null;
        }
        RedisSharedPool.returnResource(jedis);

        return result;
    }

    public static String get(String key) {
        ShardedJedis jedis = null;
        String result = null;
        try {
            jedis = RedisSharedPool.getJedis();
            result = jedis.get(key);
        } catch (Exception e) {
            log.error("get key:{} error", key, e);
            RedisSharedPool.returnBrokenResource(jedis);
            return null;
        }
        RedisSharedPool.returnResource(jedis);

        return result;
    }

    public static Long del(String key) {
        ShardedJedis jedis = null;
        Long result = null;
        try {
            jedis = RedisSharedPool.getJedis();
            result = jedis.del(key);
        } catch (Exception e) {
            log.error("del key:{} error", key, e);
            RedisSharedPool.returnBrokenResource(jedis);
            return null;
        }
        RedisSharedPool.returnResource(jedis);
        return result;
    }

    public static void main(String[] args) {
        Jedis jedis = RedisPool.getJedis();
        RedisShardedPoolUtil.set("good", "bad");
        System.out.println(RedisShardedPoolUtil.get("good"));
        RedisShardedPoolUtil.setEx("no", "yes", 60 * 1);
        RedisShardedPoolUtil.expire("good", 100);
        System.out.println("program end ");

    }

    public static Long setnx(String key, String value) {
        ShardedJedis jedis = null;
        Long result = null;
        try {
            jedis = RedisSharedPool.getJedis();
            result = jedis.setnx(key, value);
        } catch (Exception e) {
            log.error("set key:{} value:{} error", key, value, e);
            RedisSharedPool.returnBrokenResource(jedis);
            return null;
        }
        RedisSharedPool.returnResource(jedis);

        return result;
    }

    public static String getset(String closeOrderTaskLock, String s) {
        ShardedJedis jedis = null;
        String result = null;
        try {
            jedis = RedisSharedPool.getJedis();
            result = jedis.getSet(closeOrderTaskLock, s);
        } catch (Exception e) {
            log.error("set key:{} value:{} error", closeOrderTaskLock, s, e);
            RedisSharedPool.returnBrokenResource(jedis);
            return null;
        }
        RedisSharedPool.returnResource(jedis);

        return result;
    }
}
