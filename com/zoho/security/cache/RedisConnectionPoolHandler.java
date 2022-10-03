package com.zoho.security.cache;

import java.util.Iterator;
import com.zoho.instrument.redis.RedisConnectionCall;
import com.zoho.jedis.v320.exceptions.JedisException;
import com.adventnet.iam.security.SecurityFilterProperties;
import com.zoho.jedis.v320.Jedis;
import java.util.Set;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import java.util.HashMap;
import com.zoho.jedis.v320.JedisPoolConfig;
import com.zoho.jedis.v320.JedisSentinelPool;
import com.zoho.jedis.v320.JedisPool;
import java.util.Map;

public class RedisConnectionPoolHandler
{
    private static Map<CacheConfiguration, JedisPool> cacheConfigVsJedisPool;
    private static Map<CacheConfiguration, JedisSentinelPool> cacheConfigVsJedisSentinelPool;
    
    private static void createJedisPoolIfNotExist(final CacheConfiguration cacheConfiguration) {
        final JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(cacheConfiguration.getMaxTotal());
        jedisPoolConfig.setMinIdle(cacheConfiguration.getMinIdle());
        jedisPoolConfig.setMaxIdle(cacheConfiguration.getMaxIdle());
        jedisPoolConfig.setBlockWhenExhausted(true);
        jedisPoolConfig.setMaxWaitMillis((long)cacheConfiguration.getReadTimeoutInMilliSeconds());
        if (cacheConfiguration.getConfigurationType() == CacheConfiguration.ConfigurationType.REDIS_CLUSTER) {
            if (RedisConnectionPoolHandler.cacheConfigVsJedisPool == null) {
                RedisConnectionPoolHandler.cacheConfigVsJedisPool = new HashMap<CacheConfiguration, JedisPool>();
            }
            if (!RedisConnectionPoolHandler.cacheConfigVsJedisPool.containsKey(cacheConfiguration)) {
                final JedisPool jedisPool = new JedisPool((GenericObjectPoolConfig)jedisPoolConfig, cacheConfiguration.getClusterIP(), cacheConfiguration.getClusterPort(), cacheConfiguration.getReadTimeoutInMilliSeconds(), (String)null, cacheConfiguration.getDb());
                RedisConnectionPoolHandler.cacheConfigVsJedisPool.put(cacheConfiguration, jedisPool);
            }
        }
        else {
            if (RedisConnectionPoolHandler.cacheConfigVsJedisSentinelPool == null) {
                RedisConnectionPoolHandler.cacheConfigVsJedisSentinelPool = new HashMap<CacheConfiguration, JedisSentinelPool>();
            }
            if (!RedisConnectionPoolHandler.cacheConfigVsJedisSentinelPool.containsKey(cacheConfiguration)) {
                final JedisSentinelPool sentinelPool = new JedisSentinelPool(cacheConfiguration.getMasterName(), (Set)cacheConfiguration.getSentinels(), (GenericObjectPoolConfig)jedisPoolConfig, cacheConfiguration.getReadTimeoutInMilliSeconds(), (String)null, cacheConfiguration.getDb());
                RedisConnectionPoolHandler.cacheConfigVsJedisSentinelPool.put(cacheConfiguration, sentinelPool);
            }
        }
    }
    
    static Jedis getJedisObjFromJedisPool(final CacheConfiguration cacheConfiguration) {
        createJedisPoolIfNotExist(cacheConfiguration);
        final RedisConnectionCall connectionCall = SecurityFilterProperties.isRedisInstrumentationDisabled() ? null : instrumentJedisConnnectionCall(cacheConfiguration);
        Jedis jedis = null;
        try {
            jedis = ((cacheConfiguration.getConfigurationType() == CacheConfiguration.ConfigurationType.REDIS_CLUSTER) ? RedisConnectionPoolHandler.cacheConfigVsJedisPool.get(cacheConfiguration).getResource() : RedisConnectionPoolHandler.cacheConfigVsJedisSentinelPool.get(cacheConfiguration).getResource());
            if (connectionCall != null) {
                connectionCall.complete();
            }
        }
        catch (final JedisException e) {
            if (connectionCall != null) {
                connectionCall.complete((Throwable)e);
            }
            throw e;
        }
        return SecurityFilterProperties.isRedisInstrumentationDisabled() ? jedis : getInstrumentedJedis(jedis, cacheConfiguration);
    }
    
    private static RedisConnectionCall instrumentJedisConnnectionCall(final CacheConfiguration cacheConfiguration) {
        final String ip = (cacheConfiguration.getConfigurationType() == CacheConfiguration.ConfigurationType.REDIS_CLUSTER) ? cacheConfiguration.getClusterIP() : getMasterIPFromSentinelPool(cacheConfiguration);
        final RedisConnectionCall connectionCall = RedisConnectionCall.newInstance(ip, cacheConfiguration.getPoolName(), cacheConfiguration.getDb());
        connectionCall.start();
        return connectionCall;
    }
    
    private static String getMasterIPFromSentinelPool(final CacheConfiguration cacheConfiguration) {
        return RedisConnectionPoolHandler.cacheConfigVsJedisSentinelPool.get(cacheConfiguration).getCurrentHostMaster().getHost();
    }
    
    private static InstrumentedJedis getInstrumentedJedis(final Jedis jedis, final CacheConfiguration cacheConfiguration) {
        final String ip = (cacheConfiguration.getConfigurationType() == CacheConfiguration.ConfigurationType.REDIS_CLUSTER) ? cacheConfiguration.getClusterIP() : getMasterIPFromSentinelPool(cacheConfiguration);
        final InstrumentedJedis instrumentedJedis = new InstrumentedJedis(ip);
        instrumentedJedis.setWrappedJedis(jedis, ip);
        return instrumentedJedis;
    }
    
    static void returnJedisObjToPool(final Jedis jedis, final boolean isJedisConnectionBroken, final CacheConfiguration cacheConfiguration) {
        try {
            if (jedis != null) {
                jedis.close();
            }
        }
        catch (final JedisException e) {
            throw e;
        }
    }
    
    public static void destroy() {
        if (RedisConnectionPoolHandler.cacheConfigVsJedisPool != null) {
            for (final JedisPool jedisPool : RedisConnectionPoolHandler.cacheConfigVsJedisPool.values()) {
                if (!jedisPool.isClosed()) {
                    jedisPool.destroy();
                }
            }
        }
        if (RedisConnectionPoolHandler.cacheConfigVsJedisSentinelPool != null) {
            for (final JedisSentinelPool jedisSentinelPool : RedisConnectionPoolHandler.cacheConfigVsJedisSentinelPool.values()) {
                if (!jedisSentinelPool.isClosed()) {
                    jedisSentinelPool.destroy();
                }
            }
        }
    }
    
    static {
        RedisConnectionPoolHandler.cacheConfigVsJedisPool = null;
        RedisConnectionPoolHandler.cacheConfigVsJedisSentinelPool = null;
    }
}
