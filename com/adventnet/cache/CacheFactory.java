package com.adventnet.cache;

import com.adventnet.cache.memcache.MemCacheWrapper;
import com.adventnet.cache.exception.CacheException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public final class CacheFactory
{
    private Map<String, Cache> cacheMap;
    private static volatile CacheFactory handle;
    
    private CacheFactory() {
        this.cacheMap = null;
        this.cacheMap = new ConcurrentHashMap<String, Cache>();
    }
    
    public static CacheFactory getInstance() {
        if (CacheFactory.handle == null) {
            synchronized (CacheFactory.class) {
                if (CacheFactory.handle == null) {
                    CacheFactory.handle = new CacheFactory();
                }
            }
        }
        return CacheFactory.handle;
    }
    
    public Cache createCache(final String cacheName, final int cacheType, final String poolName) throws CacheException {
        Cache retCache = null;
        if (cacheType != 2) {
            throw new CacheException(106, "Unsupported CacheType");
        }
        if (!MemCacheWrapper.isPoolAvailable(poolName)) {
            throw new CacheException(107, poolName + " given is not initialized ");
        }
        retCache = MemCacheWrapper.getCache(poolName);
        if (retCache != null) {
            this.cacheMap.put(cacheName, retCache);
        }
        ((BaseCache)retCache).setName(cacheName);
        return retCache;
    }
    
    public Cache getCache(final String cacheName) {
        if (cacheName != null) {
            return this.cacheMap.get(cacheName);
        }
        return null;
    }
}
