package com.adventnet.client.cache.web;

import java.util.List;
import com.adventnet.client.cache.StaticCache;
import java.util.ArrayList;
import com.adventnet.client.util.web.WebConstants;

public class ClientDataObjectCache implements WebConstants
{
    private ClientDataObjectCache() {
    }
    
    public static void clearViewCacheForAccount(final long accountId) {
        removeFromCache(new CacheConfiguration(accountId, null, null, null, null, null, null));
    }
    
    public static void removeFromCache(final CacheConfiguration criteria) {
        StaticCache.removeFromCache(criteria);
    }
    
    public static void addToCache(final CacheConfiguration cache) {
        StaticCache.addToCache(cache.getCacheKey(), cache, cache.getTablesList());
    }
    
    public static Object getFromCache(final CacheConfiguration criteria) {
        final CacheConfiguration config = (CacheConfiguration)StaticCache.getFromCache(criteria.getCacheKey());
        return (config != null) ? config.getCachedData() : null;
    }
    
    public static void clearCacheForView(final String viewName) {
        removeFromCache(new CacheConfiguration(-1L, viewName, null, "CONFIG_DATA", "ACCOUNT", null, null));
    }
}
