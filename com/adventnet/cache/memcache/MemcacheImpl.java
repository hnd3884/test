package com.adventnet.cache.memcache;

import com.adventnet.persistence.PersistenceInitializer;
import com.schooner.MemCached.MemcachedItem;
import com.adventnet.cache.dataobject.CASObject;
import java.util.Date;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import com.adventnet.cache.exception.CacheException;
import com.adventnet.cache.util.CacheUtil;
import java.util.logging.Level;
import com.danga.MemCached.ErrorHandler;
import com.danga.MemCached.MemCachedClient;
import java.util.logging.Logger;
import com.adventnet.cache.BaseCache;

public class MemcacheImpl extends BaseCache
{
    private static final Logger LOG;
    private final MemCachedClient mc;
    private static String memcacheErrorHandlerClass;
    private static ErrorHandler handler;
    
    public MemcacheImpl(final String poolName, final boolean binary) {
        MemcacheImpl.LOG.log(Level.INFO, "Creating cache instance for pool {0} with protocol {1}", new Object[] { poolName, binary });
        final ErrorHandler handler = getErrorHandlerClass();
        if (handler != null) {
            this.mc = new MemCachedClient(poolName, true, binary, (ClassLoader)null, handler);
        }
        else {
            this.mc = new MemCachedClient(poolName, binary);
        }
        this.mc.setSanitizeKeys(false);
    }
    
    @Override
    public int getType() {
        return 2;
    }
    
    @Override
    public Object get(final String key) throws CacheException {
        MemcacheImpl.LOG.log(Level.FINE, "Inside get {0} ", key);
        this.validateKey(key);
        final String encodedKey = CacheUtil.encodeAndEncryptString(key);
        return this.mc.get(encodedKey);
    }
    
    @Override
    public Map<String, Object> getMulti(final String[] keys) throws CacheException {
        final String[] newKeys = new String[keys.length];
        final Map<String, String> encodedActual = new HashMap<String, String>();
        int i = 0;
        for (final String key : keys) {
            final String encodedKey = CacheUtil.encodeAndEncryptString(key);
            encodedActual.put(newKeys[i] = encodedKey, key);
            ++i;
        }
        final Map<String, Object> resultMap = this.mc.getMulti(newKeys);
        final Map<String, Object> returnMap = new HashMap<String, Object>();
        for (final Map.Entry<String, Object> entry : resultMap.entrySet()) {
            returnMap.put(encodedActual.get(entry.getKey()), entry.getValue());
        }
        return returnMap;
    }
    
    @Override
    public void put(final String key, final Object value) throws CacheException {
        this.validateKey(key);
        this.validateValue(value);
        final String encodedKey = CacheUtil.encodeAndEncryptString(key);
        final boolean bool = this.mc.set(encodedKey, value);
        if (bool) {
            MemcacheImpl.LOG.log(Level.FINE, "Successfully stored in memcache server");
            return;
        }
        MemcacheImpl.LOG.log(Level.FINE, "Not able to store in memcache server");
        throw new CacheException(100, "Unable to Store in Memcache");
    }
    
    @Override
    public void put(final String key, final Object value, final long duration) throws CacheException {
        this.validateKey(key);
        this.validateValue(value);
        this.validateDuration(duration);
        final String encodedKey = CacheUtil.encodeAndEncryptString(key);
        final boolean bool = this.mc.set(encodedKey, value, new Date(System.currentTimeMillis() + duration));
        if (!bool) {
            MemcacheImpl.LOG.log(Level.FINE, "Not able to store in memcache server");
            throw new CacheException(100, "Unable to Store in Memcache");
        }
    }
    
    @Override
    public void remove(final String key) throws CacheException {
        this.validateKey(key);
        final String encodedKey = CacheUtil.encodeAndEncryptString(key);
        final boolean bool = this.mc.delete(encodedKey);
        if (!bool) {
            MemcacheImpl.LOG.log(Level.FINE, "Not able to remove from memcache server");
            throw new CacheException(102, "Unable to remove from Memcache");
        }
    }
    
    @Override
    public Map getStats() throws CacheException {
        return this.mc.stats();
    }
    
    @Override
    public void purgeCache() throws CacheException {
        final boolean bool = this.mc.flushAll();
        if (!bool) {
            MemcacheImpl.LOG.log(Level.FINE, "Not able to Purge memcache server");
            throw new CacheException(103, "Unable to Purge Memcache");
        }
    }
    
    @Override
    public CASObject getForSet(final String key) throws CacheException {
        MemcacheImpl.LOG.log(Level.FINE, "Inside getforset {0} ", key);
        this.validateKey(key);
        final String encodedKey = CacheUtil.encodeAndEncryptString(key);
        final MemcachedItem cacheItem = this.mc.gets(encodedKey);
        if (cacheItem != null) {
            final CASObject retObj = new CASObject(cacheItem.getCasUnique(), cacheItem.getValue());
            return retObj;
        }
        return null;
    }
    
    @Override
    public boolean checkAndSet(final String key, final long casUnique, final Object value) throws CacheException {
        MemcacheImpl.LOG.log(Level.FINE, "Inside cas1 {0} ", key);
        this.validateKey(key);
        this.validateValue(value);
        final String encodedKey = CacheUtil.encodeAndEncryptString(key);
        return this.mc.cas(encodedKey, value, casUnique);
    }
    
    @Override
    public boolean checkAndSet(final String key, final long casUnique, final Object value, final long duration) throws CacheException {
        MemcacheImpl.LOG.log(Level.FINE, "Inside cas2 {0} ", key);
        this.validateKey(key);
        this.validateValue(value);
        this.validateDuration(duration);
        final String encodedKey = CacheUtil.encodeAndEncryptString(key);
        return this.mc.cas(encodedKey, value, new Date(System.currentTimeMillis() + duration), casUnique);
    }
    
    @Override
    public long addOrIncrement(final String key, final long value) throws CacheException {
        MemcacheImpl.LOG.log(Level.FINE, "Inside addOrIncr {0} ", key);
        this.validateKey(key);
        final String encodedKey = CacheUtil.encodeAndEncryptString(key);
        return this.mc.addOrIncr(encodedKey, value);
    }
    
    @Override
    public long increment(final String key) throws CacheException {
        MemcacheImpl.LOG.log(Level.FINE, "Inside Incr1 {0} ", key);
        this.validateKey(key);
        final String encodedKey = CacheUtil.encodeAndEncryptString(key);
        return this.mc.incr(encodedKey);
    }
    
    @Override
    public long increment(final String key, final long value) throws CacheException {
        MemcacheImpl.LOG.log(Level.FINE, "Inside Incr2 {0} ", key);
        this.validateKey(key);
        final String encodedKey = CacheUtil.encodeAndEncryptString(key);
        return this.mc.incr(encodedKey, value);
    }
    
    @Override
    public void append(final String key, final String value) throws CacheException {
        MemcacheImpl.LOG.log(Level.FINE, "Inside cas1 {0} ", key);
        this.validateKey(key);
        this.validateValue(value);
        final String encodedKey = CacheUtil.encodeAndEncryptString(key);
        this.mc.append(encodedKey, (Object)value);
    }
    
    @Override
    public void prepend(final String key, final String value) throws CacheException {
        MemcacheImpl.LOG.log(Level.FINE, "Inside cas1 {0} ", key);
        this.validateKey(key);
        this.validateValue(value);
        final String encodedKey = CacheUtil.encodeAndEncryptString(key);
        this.mc.prepend(encodedKey, (Object)value);
    }
    
    @Override
    public boolean add(final String key, final Object value) throws CacheException {
        this.validateKey(key);
        this.validateValue(value);
        final String encodedKey = CacheUtil.encodeAndEncryptString(key);
        final boolean bool = this.mc.add(encodedKey, value);
        if (bool) {
            MemcacheImpl.LOG.log(Level.FINE, "Successfully stored in memcache server");
        }
        else {
            MemcacheImpl.LOG.log(Level.FINE, "Not able to store in memcache server");
        }
        return bool;
    }
    
    @Override
    public boolean add(final String key, final Object value, final long duration) throws CacheException {
        this.validateKey(key);
        this.validateValue(value);
        final String encodedKey = CacheUtil.encodeAndEncryptString(key);
        final boolean bool = this.mc.add(encodedKey, value, new Date(System.currentTimeMillis() + duration));
        if (bool) {
            MemcacheImpl.LOG.log(Level.FINE, "Successfully stored in memcache server");
        }
        else {
            MemcacheImpl.LOG.log(Level.FINE, "Not able to store in memcache server");
        }
        return bool;
    }
    
    private void validateKey(final String key) throws CacheException {
        if (key == null) {
            throw new CacheException(105, "Key cannot be null");
        }
    }
    
    private void validateValue(final Object value) throws CacheException {
        if (value == null) {
            throw new CacheException(105, "Value Cannot be null");
        }
    }
    
    private void validateDuration(final long duration) throws CacheException {
        if (duration == -1L) {
            throw new CacheException(105, "Duration Cannot be -1");
        }
    }
    
    private static ErrorHandler getErrorHandlerClass() {
        if (MemcacheImpl.memcacheErrorHandlerClass == null) {
            return null;
        }
        try {
            if (MemcacheImpl.handler == null) {
                MemcacheImpl.handler = (ErrorHandler)Class.forName(MemcacheImpl.memcacheErrorHandlerClass).newInstance();
            }
            return MemcacheImpl.handler;
        }
        catch (final Exception exc) {
            MemcacheImpl.LOG.log(Level.INFO, "MemCache ErrorHandler class not created");
            return null;
        }
    }
    
    static {
        LOG = Logger.getLogger(MemcacheImpl.class.getName());
        MemcacheImpl.memcacheErrorHandlerClass = PersistenceInitializer.getConfigurationValue("MemcacheErrorHandler");
    }
}
