package com.me.devicemanagement.framework.server.queue;

import redis.clients.jedis.Jedis;

public interface RedisQueueAPI
{
    void initQueuePool() throws Exception;
    
    void destroyQueuePool();
    
    Jedis getJedis() throws Exception;
}
