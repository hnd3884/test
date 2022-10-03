package com.me.devicemanagement.framework.server.cache;

import java.util.Set;
import java.util.Map;
import java.util.List;
import redis.clients.jedis.Jedis;

public abstract class RedisHashMapAPI
{
    protected Jedis getJedis() {
        return null;
    }
    
    protected void closeJedis(final Jedis jedis) {
    }
    
    protected String getKeyForCacheType(final String key, final int cacheType) {
        return key;
    }
    
    public List<String> get(String cacheKey, final List<String> fields, final int cacheType) throws Exception {
        Jedis jedisObj = null;
        List<String> returnValue = null;
        try {
            cacheKey = this.getKeyForCacheType(cacheKey, cacheType);
            jedisObj = this.getJedis();
            returnValue = jedisObj.hmget(cacheKey, (String[])fields.toArray(new String[fields.size()]));
        }
        finally {
            if (jedisObj != null) {
                this.closeJedis(jedisObj);
            }
        }
        return returnValue;
    }
    
    public String get(String cacheKey, final String mapKey, final int cacheType) throws Exception {
        Jedis jedisObj = null;
        String returnValue = null;
        try {
            cacheKey = this.getKeyForCacheType(cacheKey, cacheType);
            jedisObj = this.getJedis();
            returnValue = jedisObj.hget(cacheKey, mapKey);
        }
        finally {
            if (jedisObj != null) {
                this.closeJedis(jedisObj);
            }
        }
        return returnValue;
    }
    
    public boolean containsKey(String cacheKey, final String mapKey, final int cacheType) throws Exception {
        Jedis jedisObj = null;
        Boolean returnValue = Boolean.FALSE;
        try {
            cacheKey = this.getKeyForCacheType(cacheKey, cacheType);
            jedisObj = this.getJedis();
            returnValue = jedisObj.hexists(cacheKey, mapKey);
        }
        finally {
            if (jedisObj != null) {
                this.closeJedis(jedisObj);
            }
        }
        return returnValue;
    }
    
    public Long put(final String cacheKey, final String mapKey, final String value, final int cacheType) throws Exception {
        return this.put(cacheKey, mapKey, value, cacheType, true);
    }
    
    public Long put(String cacheKey, final String mapKey, final String value, final int cacheType, final boolean overWrite) throws Exception {
        Jedis jedisObj = null;
        long returnValue = 0L;
        try {
            cacheKey = this.getKeyForCacheType(cacheKey, cacheType);
            jedisObj = this.getJedis();
            if (overWrite) {
                returnValue = jedisObj.hset(cacheKey, mapKey, value);
            }
            else {
                returnValue = jedisObj.hsetnx(cacheKey, mapKey, value);
            }
        }
        finally {
            if (jedisObj != null) {
                this.closeJedis(jedisObj);
            }
        }
        return returnValue;
    }
    
    public String put(String cacheKey, final Map<String, String> fields, final int cacheType) throws Exception {
        Jedis jedisObj = null;
        String returnValue = null;
        try {
            cacheKey = this.getKeyForCacheType(cacheKey, cacheType);
            jedisObj = this.getJedis();
            returnValue = jedisObj.hmset(cacheKey, (Map)fields);
        }
        finally {
            if (jedisObj != null) {
                this.closeJedis(jedisObj);
            }
        }
        return returnValue;
    }
    
    public Long remove(String cacheKey, final List<String> mapKeys, final int cacheType) throws Exception {
        Jedis jedisObj = null;
        Long returnValue = 0L;
        try {
            cacheKey = this.getKeyForCacheType(cacheKey, cacheType);
            final String[] fields = mapKeys.toArray(new String[mapKeys.size()]);
            jedisObj = this.getJedis();
            returnValue = jedisObj.hdel(cacheKey, fields);
        }
        finally {
            if (jedisObj != null) {
                this.closeJedis(jedisObj);
            }
        }
        return returnValue;
    }
    
    public Long remove(String cacheKey, final String mapKey, final int cacheType) throws Exception {
        Jedis jedisObj = null;
        Long returnValue = 0L;
        try {
            cacheKey = this.getKeyForCacheType(cacheKey, cacheType);
            jedisObj = this.getJedis();
            returnValue = jedisObj.hdel(cacheKey, new String[] { mapKey });
        }
        finally {
            if (jedisObj != null) {
                this.closeJedis(jedisObj);
            }
        }
        return returnValue;
    }
    
    public long incrValue(String cacheKey, final String mapKey, final long incrBy, final int cacheType) throws Exception {
        Jedis jedisObj = null;
        long returnValue = 0L;
        try {
            cacheKey = this.getKeyForCacheType(cacheKey, cacheType);
            jedisObj = this.getJedis();
            returnValue = jedisObj.hincrBy(cacheKey, mapKey, incrBy);
        }
        finally {
            if (jedisObj != null) {
                this.closeJedis(jedisObj);
            }
        }
        return returnValue;
    }
    
    public double incrValueByFloat(String cacheKey, final String mapKey, final double incrValue, final int cacheType) throws Exception {
        Jedis jedisObj = null;
        double returnValue = 0.0;
        try {
            cacheKey = this.getKeyForCacheType(cacheKey, cacheType);
            jedisObj = this.getJedis();
            returnValue = jedisObj.hincrByFloat(cacheKey, mapKey, incrValue);
        }
        finally {
            if (jedisObj != null) {
                this.closeJedis(jedisObj);
            }
        }
        return returnValue;
    }
    
    public Map<String, String> getMap(String cacheKey, final int cacheType) throws Exception {
        Jedis jedisObj = null;
        Map<String, String> returnValue = null;
        try {
            cacheKey = this.getKeyForCacheType(cacheKey, cacheType);
            jedisObj = this.getJedis();
            returnValue = jedisObj.hgetAll(cacheKey);
        }
        finally {
            if (jedisObj != null) {
                this.closeJedis(jedisObj);
            }
        }
        return returnValue;
    }
    
    public List<String> getValueList(String cacheKey, final int cacheType) throws Exception {
        Jedis jedisObj = null;
        List<String> returnValue = null;
        try {
            cacheKey = this.getKeyForCacheType(cacheKey, cacheType);
            jedisObj = this.getJedis();
            returnValue = jedisObj.hvals(cacheKey);
        }
        finally {
            if (jedisObj != null) {
                this.closeJedis(jedisObj);
            }
        }
        return returnValue;
    }
    
    public long size(String cacheKey, final int cacheType) throws Exception {
        Jedis jedisObj = null;
        long returnValue = 0L;
        try {
            cacheKey = this.getKeyForCacheType(cacheKey, cacheType);
            jedisObj = this.getJedis();
            returnValue = jedisObj.hlen(cacheKey);
        }
        finally {
            if (jedisObj != null) {
                this.closeJedis(jedisObj);
            }
        }
        return returnValue;
    }
    
    public Set<String> keySet(String cacheKey, final int cacheType) throws Exception {
        Jedis jedisObj = null;
        Set<String> returnValue = null;
        try {
            cacheKey = this.getKeyForCacheType(cacheKey, cacheType);
            jedisObj = this.getJedis();
            returnValue = jedisObj.hkeys(cacheKey);
        }
        finally {
            if (jedisObj != null) {
                this.closeJedis(jedisObj);
            }
        }
        return returnValue;
    }
    
    public String put(String cacheKey, final String value, final int cacheType) {
        Jedis jedisObj = null;
        String returnValue;
        try {
            cacheKey = this.getKeyForCacheType(cacheKey, cacheType);
            jedisObj = this.getJedis();
            returnValue = jedisObj.set(cacheKey, value);
        }
        finally {
            if (jedisObj != null) {
                this.closeJedis(jedisObj);
            }
        }
        return returnValue;
    }
    
    public String get(String cacheKey, final int cacheType) {
        Jedis jedisObj = null;
        String returnValue = null;
        try {
            cacheKey = this.getKeyForCacheType(cacheKey, cacheType);
            jedisObj = this.getJedis();
            returnValue = jedisObj.get(cacheKey);
        }
        finally {
            if (jedisObj != null) {
                this.closeJedis(jedisObj);
            }
        }
        return returnValue;
    }
}
