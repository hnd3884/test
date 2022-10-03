package com.me.devicemanagement.framework.server.queue;

import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import redis.clients.jedis.Jedis;
import java.util.Properties;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.JedisPoolConfig;
import com.me.devicemanagement.framework.server.redis.RedisServerUtil;
import java.util.logging.Level;
import java.util.logging.Logger;
import redis.clients.jedis.JedisPool;

public class RedisQueueImpl implements RedisQueueAPI
{
    public static JedisPool redisQueuePool;
    private static Logger redisLogger;
    public static final int MAX_WAIT = 5000;
    public static final int MAX_IDLE = 100;
    public static final int MIN_IDLE = 1;
    public static final int DEFAULT_MAX_TOTAL = 3000;
    public static final int DEFAULT_QUEUE_TIMEOUT = 10000;
    
    @Override
    public synchronized void initQueuePool() throws Exception {
        if (RedisQueueImpl.redisQueuePool == null) {
            RedisQueueImpl.redisLogger.log(Level.INFO, "Initializing ----> REDIS QUEUE POOL");
            final Properties qProps = RedisServerUtil.getRedisServerProperties();
            int maxTotal = 3000;
            int redisQTimeout = 10000;
            if (qProps != null) {
                if (qProps.containsKey("redis.connection.limit")) {
                    maxTotal = Integer.parseInt(qProps.getProperty("redis.connection.limit"));
                }
                if (qProps.containsKey("redis.queue.timeout")) {
                    redisQTimeout = Integer.parseInt(qProps.getProperty("redis.queue.timeout"));
                }
            }
            final JedisPoolConfig jedisConfig = new JedisPoolConfig();
            jedisConfig.setMaxTotal(maxTotal);
            jedisConfig.setMaxWaitMillis(5000L);
            jedisConfig.setMaxIdle(100);
            jedisConfig.setMinIdle(1);
            final int port = RedisServerUtil.getRedisPort();
            RedisQueueImpl.redisQueuePool = new JedisPool((GenericObjectPoolConfig)jedisConfig, "localhost", port, redisQTimeout, RedisServerUtil.getPasswordFromDBorCache());
            RedisQueueImpl.redisLogger.log(Level.INFO, "Initialized Redis Pool with " + maxTotal + " Connections");
        }
    }
    
    @Override
    public void destroyQueuePool() {
        RedisQueueImpl.redisLogger.log(Level.INFO, "Destroying ----> REDIS QUEUE POOL");
        if (RedisQueueImpl.redisQueuePool != null) {
            RedisQueueImpl.redisQueuePool.close();
            RedisQueueImpl.redisLogger.log(Level.INFO, "Destroyed successfully");
        }
    }
    
    @Override
    public Jedis getJedis() throws Exception {
        if (RedisQueueImpl.redisQueuePool == null) {
            ApiFactoryProvider.getRedisQueueAPI().initQueuePool();
        }
        return RedisQueueImpl.redisQueuePool.getResource();
    }
    
    static {
        RedisQueueImpl.redisQueuePool = null;
        RedisQueueImpl.redisLogger = Logger.getLogger("RedisLogger");
    }
}
