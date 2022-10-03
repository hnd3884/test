package com.zoho.security.cache;

import java.util.List;
import java.util.Set;
import java.util.Map;
import com.zoho.jedis.v320.Jedis;
import com.zoho.jedis.v320.exceptions.JedisException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RedisCacheAPI
{
    private static final Logger LOGGER;
    
    private RedisCacheAPI() {
    }
    
    public static String getData(final String key, final CacheConfiguration cacheConfiguration) throws JedisException {
        if (key == null) {
            RedisCacheAPI.LOGGER.log(Level.WARNING, "Null key not sent to Redis");
            return null;
        }
        boolean isJedisConnectionBroken = false;
        Jedis jedis = null;
        try {
            jedis = RedisConnectionPoolHandler.getJedisObjFromJedisPool(cacheConfiguration);
            return jedis.get(key);
        }
        catch (final JedisException e) {
            RedisCacheAPI.LOGGER.log(Level.SEVERE, "JedisException occurred while performing the operation ''GET''. Error-msg: {0}", new Object[] { e.getMessage() });
            isJedisConnectionBroken = true;
            throw e;
        }
        finally {
            RedisConnectionPoolHandler.returnJedisObjToPool(jedis, isJedisConnectionBroken, cacheConfiguration);
        }
    }
    
    public static void setData(final String key, final String value, final CacheConfiguration cacheConfiguration) throws JedisException {
        if (key == null) {
            RedisCacheAPI.LOGGER.log(Level.WARNING, "Null key not sent to Redis");
            return;
        }
        boolean isJedisConnectionBroken = false;
        Jedis jedis = null;
        try {
            jedis = RedisConnectionPoolHandler.getJedisObjFromJedisPool(cacheConfiguration);
            jedis.set(key, value);
        }
        catch (final JedisException e) {
            RedisCacheAPI.LOGGER.log(Level.SEVERE, "JedisException occurred while performing the operation ''SET''. Error-msg: {0}", new Object[] { e.getMessage() });
            isJedisConnectionBroken = true;
            throw e;
        }
        finally {
            RedisConnectionPoolHandler.returnJedisObjToPool(jedis, isJedisConnectionBroken, cacheConfiguration);
        }
    }
    
    public static void setDataWithExpireTime(final String key, final String value, final int expireTimeInSeconds, final CacheConfiguration cacheConfiguration) throws JedisException {
        if (key == null) {
            RedisCacheAPI.LOGGER.log(Level.WARNING, "Null key not sent to Redis");
            return;
        }
        boolean isJedisConnectionBroken = false;
        Jedis jedis = null;
        try {
            jedis = RedisConnectionPoolHandler.getJedisObjFromJedisPool(cacheConfiguration);
            jedis.setex(key, expireTimeInSeconds, value);
        }
        catch (final JedisException e) {
            RedisCacheAPI.LOGGER.log(Level.SEVERE, "JedisException occurred while performing the operation ''SETEX''. Error-msg: {0}", new Object[] { e.getMessage() });
            isJedisConnectionBroken = true;
            throw e;
        }
        finally {
            RedisConnectionPoolHandler.returnJedisObjToPool(jedis, isJedisConnectionBroken, cacheConfiguration);
        }
    }
    
    public static long incrementAndGetValue(final String key, final long count, final CacheConfiguration cacheConfiguration) throws JedisException {
        if (key == null) {
            RedisCacheAPI.LOGGER.log(Level.WARNING, "Null key not sent to Redis");
            return -1L;
        }
        boolean isJedisConnectionBroken = false;
        Jedis jedis = null;
        try {
            jedis = RedisConnectionPoolHandler.getJedisObjFromJedisPool(cacheConfiguration);
            return jedis.incrBy(key, count);
        }
        catch (final JedisException e) {
            RedisCacheAPI.LOGGER.log(Level.SEVERE, "JedisException occurred while performing the operation ''INCRBY''. Error-msg: {0}", new Object[] { e.getMessage() });
            isJedisConnectionBroken = true;
            throw e;
        }
        finally {
            RedisConnectionPoolHandler.returnJedisObjToPool(jedis, isJedisConnectionBroken, cacheConfiguration);
        }
    }
    
    public static void removeData(final String key, final CacheConfiguration cacheConfiguration) throws JedisException {
        if (key == null) {
            RedisCacheAPI.LOGGER.log(Level.WARNING, "Null key not sent to Redis");
            return;
        }
        boolean isJedisConnectionBroken = false;
        Jedis jedis = null;
        try {
            jedis = RedisConnectionPoolHandler.getJedisObjFromJedisPool(cacheConfiguration);
            jedis.del(key);
        }
        catch (final JedisException e) {
            RedisCacheAPI.LOGGER.log(Level.SEVERE, "JedisException occurred while performing the operation ''DEL''. Error-msg: {0}", new Object[] { e.getMessage() });
            isJedisConnectionBroken = true;
            throw e;
        }
        finally {
            RedisConnectionPoolHandler.returnJedisObjToPool(jedis, isJedisConnectionBroken, cacheConfiguration);
        }
    }
    
    public static Map<String, String> getDataMap(final String key, final CacheConfiguration cacheConfiguration) throws JedisException {
        if (key == null) {
            RedisCacheAPI.LOGGER.log(Level.WARNING, "Null key not sent to Redis");
            return null;
        }
        boolean isJedisConnectionBroken = false;
        Jedis jedis = null;
        try {
            jedis = RedisConnectionPoolHandler.getJedisObjFromJedisPool(cacheConfiguration);
            return jedis.hgetAll(key);
        }
        catch (final JedisException e) {
            RedisCacheAPI.LOGGER.log(Level.SEVERE, "JedisException occurred while performing the operation ''HGETALL''. Error-msg: {0}", new Object[] { e.getMessage() });
            isJedisConnectionBroken = true;
            throw e;
        }
        finally {
            RedisConnectionPoolHandler.returnJedisObjToPool(jedis, isJedisConnectionBroken, cacheConfiguration);
        }
    }
    
    public static String getDataFromMap(final String key, final String field, final CacheConfiguration cacheConfiguration) throws JedisException {
        if (key == null) {
            RedisCacheAPI.LOGGER.log(Level.WARNING, "Null key not sent to Redis");
            return null;
        }
        boolean isJedisConnectionBroken = false;
        Jedis jedis = null;
        try {
            jedis = RedisConnectionPoolHandler.getJedisObjFromJedisPool(cacheConfiguration);
            return jedis.hget(key, field);
        }
        catch (final JedisException e) {
            RedisCacheAPI.LOGGER.log(Level.SEVERE, "JedisException occurred while performing the operation ''HGET''. Error-msg: {0}", new Object[] { e.getMessage() });
            isJedisConnectionBroken = true;
            throw e;
        }
        finally {
            RedisConnectionPoolHandler.returnJedisObjToPool(jedis, isJedisConnectionBroken, cacheConfiguration);
        }
    }
    
    public static void putDatumIntoMap(final String key, final String field, final String value, final CacheConfiguration cacheConfiguration) throws JedisException {
        if (key == null) {
            RedisCacheAPI.LOGGER.log(Level.WARNING, "Null key not sent to Redis");
            return;
        }
        boolean isJedisConnectionBroken = false;
        Jedis jedis = null;
        try {
            jedis = RedisConnectionPoolHandler.getJedisObjFromJedisPool(cacheConfiguration);
            jedis.hset(key, field, value);
        }
        catch (final JedisException e) {
            RedisCacheAPI.LOGGER.log(Level.SEVERE, "JedisException occurred while performing the operation ''HSET''. Error-msg: {0}", new Object[] { e.getMessage() });
            isJedisConnectionBroken = true;
            throw e;
        }
        finally {
            RedisConnectionPoolHandler.returnJedisObjToPool(jedis, isJedisConnectionBroken, cacheConfiguration);
        }
    }
    
    public static void putDataIntoMap(final String key, final Map<String, String> dataMap, final CacheConfiguration cacheConfiguration) throws JedisException {
        if (key == null) {
            RedisCacheAPI.LOGGER.log(Level.WARNING, "Null key not sent to Redis");
            return;
        }
        boolean isJedisConnectionBroken = false;
        Jedis jedis = null;
        try {
            jedis = RedisConnectionPoolHandler.getJedisObjFromJedisPool(cacheConfiguration);
            jedis.hmset(key, (Map)dataMap);
        }
        catch (final JedisException e) {
            RedisCacheAPI.LOGGER.log(Level.SEVERE, "JedisException occurred while performing the operation ''HSET''. Error-msg: {0}", new Object[] { e.getMessage() });
            isJedisConnectionBroken = true;
            throw e;
        }
        finally {
            RedisConnectionPoolHandler.returnJedisObjToPool(jedis, isJedisConnectionBroken, cacheConfiguration);
        }
    }
    
    public static long putDataIntoMapIfNotExists(final String key, final String field, final String value, final CacheConfiguration cacheConfiguration) throws JedisException {
        if (key == null || field == null) {
            RedisCacheAPI.LOGGER.log(Level.WARNING, "Null key/field not sent to Redis");
            return -1L;
        }
        boolean isJedisConnectionBroken = false;
        Jedis jedis = null;
        try {
            jedis = RedisConnectionPoolHandler.getJedisObjFromJedisPool(cacheConfiguration);
            return jedis.hsetnx(key, field, value);
        }
        catch (final JedisException e) {
            RedisCacheAPI.LOGGER.log(Level.SEVERE, "JedisException occurred while performing the operation ''HSET''. Error-msg: {0}", new Object[] { e.getMessage() });
            isJedisConnectionBroken = true;
            throw e;
        }
        finally {
            RedisConnectionPoolHandler.returnJedisObjToPool(jedis, isJedisConnectionBroken, cacheConfiguration);
        }
    }
    
    public static long addDataIntoSet(final String key, final String[] members, final CacheConfiguration cacheConfiguration) throws JedisException {
        if (key == null) {
            RedisCacheAPI.LOGGER.log(Level.WARNING, "Null key not sent to Redis");
            return -1L;
        }
        boolean isJedisConnectionBroken = false;
        Jedis jedis = null;
        try {
            jedis = RedisConnectionPoolHandler.getJedisObjFromJedisPool(cacheConfiguration);
            return jedis.sadd(key, members);
        }
        catch (final JedisException e) {
            RedisCacheAPI.LOGGER.log(Level.SEVERE, "JedisException occurred while performing the operation ''SADD''. Error-msg: {0}", new Object[] { e.getMessage() });
            isJedisConnectionBroken = true;
            throw e;
        }
        finally {
            RedisConnectionPoolHandler.returnJedisObjToPool(jedis, isJedisConnectionBroken, cacheConfiguration);
        }
    }
    
    public static Set<String> getDataSet(final String key, final CacheConfiguration cacheConfiguration) throws JedisException {
        if (key == null) {
            RedisCacheAPI.LOGGER.log(Level.WARNING, "Null key not sent to Redis");
            return null;
        }
        boolean isJedisConnectionBroken = false;
        Jedis jedis = null;
        try {
            jedis = RedisConnectionPoolHandler.getJedisObjFromJedisPool(cacheConfiguration);
            return jedis.smembers(key);
        }
        catch (final JedisException e) {
            RedisCacheAPI.LOGGER.log(Level.SEVERE, "JedisException occurred while performing the operation ''SMEMBERS''. Error-msg: {0}", new Object[] { e.getMessage() });
            isJedisConnectionBroken = true;
            throw e;
        }
        finally {
            RedisConnectionPoolHandler.returnJedisObjToPool(jedis, isJedisConnectionBroken, cacheConfiguration);
        }
    }
    
    public static long removeDataFromSet(final String key, final String[] members, final CacheConfiguration cacheConfiguration) throws JedisException {
        if (key == null) {
            RedisCacheAPI.LOGGER.log(Level.WARNING, "Null key not sent to Redis");
            return -1L;
        }
        boolean isJedisConnectionBroken = false;
        Jedis jedis = null;
        try {
            jedis = RedisConnectionPoolHandler.getJedisObjFromJedisPool(cacheConfiguration);
            return jedis.srem(key, members);
        }
        catch (final JedisException e) {
            RedisCacheAPI.LOGGER.log(Level.SEVERE, "JedisException occurred while performing the operation ''SREM''. Error-msg: {0}", new Object[] { e.getMessage() });
            isJedisConnectionBroken = true;
            throw e;
        }
        finally {
            RedisConnectionPoolHandler.returnJedisObjToPool(jedis, isJedisConnectionBroken, cacheConfiguration);
        }
    }
    
    public static Object evalSHA(final RedisLuaScript luaScript, final int keyCount, final String[] params, final CacheConfiguration cacheConfiguration) throws JedisException {
        if (luaScript == null || luaScript.getScript() == null) {
            RedisCacheAPI.LOGGER.log(Level.WARNING, "Null Lua script not sent to Redis");
            return null;
        }
        boolean isJedisConnectionBroken = false;
        Jedis jedis = null;
        try {
            jedis = RedisConnectionPoolHandler.getJedisObjFromJedisPool(cacheConfiguration);
            return jedis.evalsha(luaScript.getScriptSha1Hash(jedis), keyCount, params);
        }
        catch (final JedisException e) {
            RedisCacheAPI.LOGGER.log(Level.SEVERE, "JedisException occurred while performing the operation ''evalsha''. Error-msg: {0}", new Object[] { e.getMessage() });
            isJedisConnectionBroken = true;
            throw e;
        }
        finally {
            RedisConnectionPoolHandler.returnJedisObjToPool(jedis, isJedisConnectionBroken, cacheConfiguration);
        }
    }
    
    public static Object evalSHA(final RedisLuaScript luaScript, final List<String> keys, final List<String> args, final CacheConfiguration cacheConfiguration) {
        if (luaScript == null || luaScript.getScript() == null) {
            RedisCacheAPI.LOGGER.log(Level.WARNING, "Null Lua script not sent to Redis");
            return null;
        }
        boolean isJedisConnectionBroken = false;
        Jedis jedis = null;
        try {
            jedis = RedisConnectionPoolHandler.getJedisObjFromJedisPool(cacheConfiguration);
            return jedis.evalsha(luaScript.getScriptSha1Hash(jedis), (List)keys, (List)args);
        }
        catch (final JedisException e) {
            RedisCacheAPI.LOGGER.log(Level.SEVERE, "JedisException occurred while performing the operation ''evalsha''. Error-msg: {0}", new Object[] { e.getMessage() });
            isJedisConnectionBroken = true;
            throw e;
        }
        finally {
            RedisConnectionPoolHandler.returnJedisObjToPool(jedis, isJedisConnectionBroken, cacheConfiguration);
        }
    }
    
    public static String ping(final CacheConfiguration cacheConfiguration) {
        boolean isJedisConnectionBroken = false;
        Jedis jedis = null;
        try {
            jedis = RedisConnectionPoolHandler.getJedisObjFromJedisPool(cacheConfiguration);
            return jedis.ping();
        }
        catch (final JedisException e) {
            RedisCacheAPI.LOGGER.log(Level.SEVERE, "JedisException occurred while performing the operation ''ping''. Error-msg: {0}", new Object[] { e.getMessage() });
            isJedisConnectionBroken = true;
            throw e;
        }
        finally {
            RedisConnectionPoolHandler.returnJedisObjToPool(jedis, isJedisConnectionBroken, cacheConfiguration);
        }
    }
    
    static {
        LOGGER = Logger.getLogger(RedisCacheAPI.class.getName());
    }
}
