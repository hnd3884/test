package com.me.devicemanagement.framework.server.cache;

import java.util.HashMap;
import java.util.Set;
import java.util.Hashtable;
import java.util.List;

public interface CacheAccessAPI
{
    boolean initializeCache();
    
    Object getCache(final String p0);
    
    Object putCache(final String p0, final Object p1, final List p2);
    
    Object putCache(final String p0, final Object p1);
    
    Object getCache(final String p0, final int p1);
    
    Object putCache(final String p0, final Object p1, final List p2, final int p3);
    
    Object putCache(final String p0, final Object p1, final int p2);
    
    Object putCache(final String p0, final Object p1, final int p2, final int p3);
    
    void removeCache(final String p0, final int p1);
    
    void removeCache(final String p0);
    
    void clearCachedData();
    
    Hashtable<String, Set<String>> getSegmentToTableMapping();
    
    int incrementCache(final String p0, final int p1) throws Exception;
    
    int decrementCache(final String p0, final int p1) throws Exception;
    
    void putCacheWithLock(final String p0, final CacheSynchronisedUpdateHandler p1, final int p2, final HashMap<String, Object> p3, final Integer p4, final Integer p5) throws Exception;
    
    default Object putIntoHashMap(final String cacheKey, final HashMap value, final int cacheType) {
        return new Object();
    }
    
    default Object putIntoHashMap(final String cacheKey, final HashMap value, final int cacheType, final int expirySeconds) {
        return new Object();
    }
    
    default Object putIntoHashMap(final String cacheKey, final String mapKey, final Object value, final int cacheType, final int expirySeconds) {
        return new Object();
    }
    
    default Object putIntoHashMap(final String cacheKey, final String mapKey, final Object value, final int cacheType) {
        return new Object();
    }
    
    default Object getFromHashMap(final String cacheKey, final String mapKey, final int cacheType) {
        return new Object();
    }
    
    default Object getFromHashMap(final String cacheKey, final int cacheType) {
        return new Object();
    }
    
    default void removeMapKeyFromHashMap(final String cacheKey, final List<String> mapKeys, final int cacheType) {
    }
    
    default void removeMapKeyFromHashMap(final String cacheKey, final String mapKey, final int cacheType) {
    }
    
    default void removeHashMap(final String cacheName, final int cacheType) {
    }
    
    public interface CacheSynchronisedUpdateHandler
    {
        Object processMessage(final Object p0, final HashMap<String, Object> p1);
    }
}
