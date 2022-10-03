package com.me.devicemanagement.framework.server.redis;

import java.util.logging.Level;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.queue.RedisQueueImpl;
import redis.clients.jedis.Jedis;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.cache.RedisHashMapAPI;

public class RedisHashMapImpl extends RedisHashMapAPI
{
    private static Logger redisLogger;
    
    public Jedis getJedis() {
        try {
            if (RedisQueueImpl.redisQueuePool == null) {
                ApiFactoryProvider.getRedisQueueAPI().initQueuePool();
            }
            return RedisQueueImpl.redisQueuePool.getResource();
        }
        catch (final Exception e) {
            RedisHashMapImpl.redisLogger.log(Level.INFO, "Exception while getting redis connection ");
            return null;
        }
    }
    
    public void closeJedis(final Jedis jedis) {
        try {
            jedis.close();
        }
        catch (final Exception e) {
            RedisHashMapImpl.redisLogger.log(Level.INFO, "Exception while closing redis connection ");
        }
    }
    
    static {
        RedisHashMapImpl.redisLogger = Logger.getLogger("RedisLogger");
    }
}
