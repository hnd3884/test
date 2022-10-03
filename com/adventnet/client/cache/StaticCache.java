package com.adventnet.client.cache;

import java.util.List;
import com.adventnet.client.cache.web.CacheConfiguration;
import com.adventnet.persistence.cache.CacheManager;
import java.util.logging.Level;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class StaticCache
{
    private static Logger logger;
    private static ConcurrentHashMap cache;
    private static volatile int count;
    public static final boolean USE_STATIC_CACHE;
    
    public static void addToCache(final Object key, final Object value) {
        StaticCache.logger.log(Level.FINE, " Add to Cache Called with {0}", key);
        if (StaticCache.USE_STATIC_CACHE) {
            StaticCache.cache.put(key, value);
        }
        else {
            CacheManager.getCacheRepository().addToCache(key, value);
        }
    }
    
    public static Object getFromCache(final Object key) {
        if (StaticCache.USE_STATIC_CACHE) {
            StaticCache.logger.log(Level.FINE, " Get From Cache Called with {0}", key);
            return StaticCache.cache.get(key);
        }
        return CacheManager.getCacheRepository().getFromCache(key);
    }
    
    public static void removeFromCache(final CacheConfiguration config) {
        CacheManager.getCacheRepository().removeCachedData((Object)config);
    }
    
    public static void removeFromCache(final Object key) {
        if (StaticCache.USE_STATIC_CACHE) {
            StaticCache.cache.remove(key);
        }
        else {
            CacheManager.getCacheRepository().removeCachedData(key);
        }
    }
    
    public static void removeFromCache(final List tables) {
        if (!StaticCache.USE_STATIC_CACHE) {
            CacheManager.getCacheRepository().removeCachedData(tables);
        }
    }
    
    public static void clearCache() {
        StaticCache.logger.log(Level.SEVERE, "Clear Cache called!!");
        StaticCache.cache.clear();
    }
    
    public static void addToCache(final Object key, final Object cache, final List<String> tableList) {
        if (StaticCache.USE_STATIC_CACHE) {
            addToCache(key, cache);
        }
        else {
            CacheManager.getCacheRepository().addToCache(key, cache, (List)tableList);
        }
    }
    
    public static void addToCache(final Object key, final Object key1, final Object cache, final List<String> tableList) {
        if (StaticCache.USE_STATIC_CACHE) {
            addToCache(key, cache);
            addToCache(key1, cache);
        }
        else {
            CacheManager.getCacheRepository().addToCache(key, cache, (List)tableList);
            CacheManager.getCacheRepository().addToCache(key1, cache, (List)tableList);
        }
    }
    
    static {
        StaticCache.logger = Logger.getLogger(StaticCache.class.getName());
        StaticCache.cache = new ConcurrentHashMap();
        StaticCache.count = 0;
        USE_STATIC_CACHE = Boolean.getBoolean("client.use.static.cache");
    }
}
