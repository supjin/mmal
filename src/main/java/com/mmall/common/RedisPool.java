package com.mmall.common;

import com.mmall.util.PropertiesUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;

public class RedisPool {

    private static JedisPool pool;
    private static Integer maxTotal = Integer.valueOf(PropertiesUtil.getProperty("redis.max.total", "20")); //最大链接数
    private static Integer maxIdle = Integer.valueOf(PropertiesUtil.getProperty("redis.max.idle", "10"));//最大空闲数
    private static Integer minIdle = Integer.valueOf(PropertiesUtil.getProperty("redis.min.idle", "2"));//最小空闲
    private static Boolean testOnBorrow = Boolean.valueOf(PropertiesUtil.getProperty("redis.test.borrow", "true"));//再borrow一个jedis实例的时候，是否需要进行验证操作，只有可用才borrow
    private static Boolean testOnReturn = Boolean.valueOf(PropertiesUtil.getProperty("redis.test.return", "true"));//还，放回redispool的jedis实例

    private static String redisIp = PropertiesUtil.getProperty("redis.ip");//最大空闲数
    private static Integer redisPort = Integer.valueOf(PropertiesUtil.getProperty("redis.port", "6379"));//最大空闲数

    private static void initPool() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);
        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);

        config.setBlockWhenExhausted(true);//设置连接池里的链接都储在busy状态时候，需要阻塞吗？true--阻塞直到超时--false--抛出异常

        pool = new JedisPool(config, redisIp, redisPort, 1000 * 2);

    }

    static {//类加载的时候就初始化连接池
        initPool();

    }

    public static Jedis getJedis() {
        return pool.getResource();
    }

    public static void returnResource(Jedis jedis) {
        pool.returnResource(jedis);

    }

    public static void returnBrokenResource(Jedis jedis) {
        pool.returnBrokenResource(jedis);
    }

    public static void main(String[] args) {
        Jedis jedis = pool.getResource();
        jedis.set("lhj", "jjjjj");
        returnResource(jedis);
        pool.destroy();

        System.out.println("program is end ");
    }

}
