package com.me.devicemanagement.onpremise.server.cache;

import java.util.Arrays;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Set;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.List;
import com.adventnet.persistence.cache.CacheRepository;
import com.adventnet.persistence.cache.CacheManager;
import java.util.logging.Logger;
import java.util.concurrent.ConcurrentHashMap;
import com.me.devicemanagement.framework.server.cache.CacheAccessAPI;

public class CacheAccessImpl implements CacheAccessAPI
{
    private static ConcurrentHashMap<String, Object> concurrentHashMap;
    private static Logger logger;
    
    public boolean initializeCache() {
        return true;
    }
    
    public Object getCache(final String cacheName) {
        try {
            final CacheRepository repository = CacheManager.getCacheRepository();
            final Object obj = repository.getFromCache((Object)cacheName);
            return obj;
        }
        catch (final Exception e) {
            return null;
        }
    }
    
    public Object putCache(final String cacheName, final Object obj, final List tableList) {
        final CacheRepository repository = CacheManager.getCacheRepository();
        if (repository == null) {
            return null;
        }
        repository.addToCache((Object)cacheName, obj, tableList);
        return obj;
    }
    
    public Object putCache(final String cacheName, final Object obj) {
        final CacheRepository repository = CacheManager.getCacheRepository();
        if (repository == null) {
            return null;
        }
        repository.addToCache((Object)cacheName, obj);
        return obj;
    }
    
    public void removeCache(final String cacheName) {
        final CacheRepository repository = CacheManager.getCacheRepository();
        repository.removeCachedData((Object)cacheName);
    }
    
    public Object getCache(final String cacheName, final int cacheType) {
        try {
            final CacheRepository repository = CacheManager.getCacheRepository();
            final Object obj = repository.getFromCache((Object)cacheName);
            return obj;
        }
        catch (final Exception e) {
            return null;
        }
    }
    
    public Object putCache(final String cacheName, final Object obj, final List tableList, final int cacheType) {
        final CacheRepository repository = CacheManager.getCacheRepository();
        if (repository == null) {
            CacheAccessImpl.logger.log(Level.INFO, "Cache Repository is null");
            return null;
        }
        repository.addToCache((Object)cacheName, obj, tableList);
        return obj;
    }
    
    public Object putCache(final String cacheName, final Object obj, final int cacheType) {
        final CacheRepository repository = CacheManager.getCacheRepository();
        if (repository == null) {
            CacheAccessImpl.logger.log(Level.INFO, "Cache Repository is null");
            return null;
        }
        repository.addToCache((Object)cacheName, obj);
        return obj;
    }
    
    public Object putCache(final String cacheName, final Object obj, final int cacheType, final int seconds) {
        return this.putCache(cacheName, obj, cacheType);
    }
    
    public void removeCache(final String cacheName, final int cacheType) {
        final CacheRepository repository = CacheManager.getCacheRepository();
        repository.removeCachedData((Object)cacheName);
    }
    
    public void clearCachedData() {
        final CacheRepository repository = CacheManager.getCacheRepository();
        repository.clearCachedData();
    }
    
    public Hashtable<String, Set<String>> getSegmentToTableMapping() {
        return null;
    }
    
    public int incrementCache(final String cacheName, final int incrementBy) throws Exception {
        try {
            Integer cacheValue = (Integer)this.getCache(cacheName);
            cacheValue = ((cacheValue != null) ? (cacheValue + incrementBy) : incrementBy);
            this.putCache(cacheName, cacheValue);
            return cacheValue;
        }
        catch (final Exception e) {
            throw e;
        }
    }
    
    public int decrementCache(final String cacheName, final int decrementBy) throws Exception {
        final String exceptionMessage = "InvalidCacheNameException : Cannot decrement cache which does not exist : " + cacheName;
        try {
            Integer cacheValue = (Integer)this.getCache(cacheName);
            if (cacheValue != null) {
                cacheValue -= decrementBy;
                this.putCache(cacheName, cacheValue);
                return Integer.valueOf(cacheValue.toString());
            }
            throw new Exception(exceptionMessage);
        }
        catch (final NumberFormatException e) {
            throw e;
        }
        catch (final Exception e2) {
            throw e2;
        }
    }
    
    public void putCacheWithLock(final String cacheName, final CacheAccessAPI.CacheSynchronisedUpdateHandler cacheLock, final int cacheType, final HashMap<String, Object> additionalParams, final Integer counterCount, final Integer sleepCount) throws Exception {
        int now = 0;
        final int counter = (counterCount != null) ? counterCount : 5;
        final int sleep = (sleepCount != null) ? sleepCount : 500;
        boolean synchronizedBlockEntry = false;
        while (counter != now && !synchronizedBlockEntry) {
            try {
                final Object o;
                synchronized (o = ((CacheAccessImpl.concurrentHashMap.putIfAbsent(cacheName, new Object()) == null) ? CacheAccessImpl.concurrentHashMap.get(cacheName) : null)) {
                    synchronizedBlockEntry = true;
                    try {
                        Object obj = this.getCache(cacheName, cacheType);
                        obj = cacheLock.processMessage(obj, (HashMap)additionalParams);
                        this.putCache(cacheName, obj, cacheType);
                    }
                    catch (final Exception e) {
                        throw new Exception(e);
                    }
                    finally {
                        CacheAccessImpl.concurrentHashMap.remove(cacheName);
                    }
                }
            }
            catch (final Exception e2) {
                if (synchronizedBlockEntry) {
                    CacheAccessImpl.logger.log(Level.INFO, "Exception in updating cache value");
                }
                else {
                    try {
                        ++now;
                        Thread.sleep(sleep);
                        CacheAccessImpl.logger.log(Level.INFO, "Cache locked by some other thread. Retrying...");
                    }
                    catch (final Exception ex) {
                        CacheAccessImpl.logger.log(Level.INFO, "Exception while retrying to update cache");
                    }
                }
            }
        }
        if (!synchronizedBlockEntry && counter == now) {
            CacheAccessImpl.logger.log(Level.WARNING, "Too long to update cache value. ");
        }
    }
    
    public Object putIntoHashMap(final String cacheName, final HashMap obj, final int cacheType) {
        return this.putIntoHashMap(cacheName, obj, cacheType, -1);
    }
    
    public Object putIntoHashMap(final String cacheName, final HashMap obj, final int cacheType, final int expirySeconds) {
        return this.putCache(cacheName, obj, cacheType);
    }
    
    public Object putIntoHashMap(final String cacheName, final String mapKey, final Object value, final int cacheType) {
        return this.putIntoHashMap(cacheName, mapKey, value, cacheType, -1);
    }
    
    public Object putIntoHashMap(final String cacheName, final String mapKey, final Object value, final int cacheType, final int expirySeconds) {
        HashMap obj = (HashMap)this.getCache(cacheName, cacheType);
        if (obj == null) {
            obj = new HashMap();
        }
        obj.put(mapKey, value);
        return this.putCache(cacheName, obj, cacheType);
    }
    
    public Object getFromHashMap(final String cacheName, final int cacheType) {
        return this.getCache(cacheName, cacheType);
    }
    
    public Object getFromHashMap(final String cacheName, final String mapKey, final int cacheType) {
        final HashMap obj = (HashMap)this.getCache(cacheName, cacheType);
        if (obj != null) {
            return obj.get(mapKey);
        }
        return null;
    }
    
    public void removeMapKeyFromHashMap(final String cacheName, final List<String> mapKeys, final int cacheType) {
        final HashMap obj = (HashMap)this.getCache(cacheName, cacheType);
        if (obj != null) {
            for (final String key : mapKeys) {
                obj.remove(key);
            }
            this.putCache(cacheName, obj, cacheType);
        }
    }
    
    public void removeMapKeyFromHashMap(final String cacheName, final String mapKey, final int cacheType) {
        this.removeMapKeyFromHashMap(cacheName, Arrays.asList(mapKey), cacheType);
    }
    
    public void removeHashMap(final String cacheName, final int cacheType) {
        this.removeCache(cacheName, cacheType);
    }
    
    static {
        CacheAccessImpl.concurrentHashMap = new ConcurrentHashMap<String, Object>();
        CacheAccessImpl.logger = Logger.getLogger(CacheAccessImpl.class.getName());
    }
}
