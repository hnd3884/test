package com.adventnet.iam.security;

import java.util.Iterator;
import java.util.HashMap;
import com.zoho.security.cache.RedisCacheAPI;
import java.util.List;
import java.util.Map;
import com.zoho.security.cache.CacheConfiguration;

public abstract class DbCacheAccessInfo extends AccessInfo
{
    protected final CacheConfiguration cacheConfiguration;
    protected final String throttlesLockKey;
    protected Exception cacheException;
    
    protected DbCacheAccessInfo(final String throttlesKey, final ThrottlesRule throttlesRule, final CacheConfiguration cacheConfiguration) {
        super(throttlesKey + "_" + throttlesRule.getWindow().ordinal(), throttlesRule);
        this.throttlesLockKey = this.throttlesKey + "_" + "l";
        this.cacheConfiguration = cacheConfiguration;
    }
    
    @Override
    public AccessInfoLock tryLock(final long currentAccessTimeInMillis) {
        if (this.getLock() != null) {
            return this.lock;
        }
        final Map<String, String> info = this.fetchInfoFromRedis(currentAccessTimeInMillis);
        this.setAccessCountsInfo(info);
        if (this.isLocked(info)) {
            this.setLockInfo(info, true);
        }
        return this.lock;
    }
    
    protected abstract Map<String, String> fetchInfoFromRedis(final long p0);
    
    private void setAccessCountsInfo(final Map<String, String> info) {
        this.throttleDurationVsAccessCount = new AccessCountsInfo(info).throttleAccessCountMap;
    }
    
    protected abstract void setLockInfo(final Map<String, String> p0, final boolean p1);
    
    private boolean isLocked(final Map<String, String> info) {
        return info.containsKey("vtd");
    }
    
    protected void addVarArgsAsStringsInList(final List<String> list, final Object... objects) {
        for (int i = 0; i < objects.length; ++i) {
            list.add(String.valueOf(objects[i]));
        }
    }
    
    @Override
    public AccessInfoLock getLock() {
        if (this.lock != null) {
            return this.lock;
        }
        final Map<String, String> info = RedisCacheAPI.getDataMap(this.throttlesLockKey, this.cacheConfiguration);
        if (this.isLocked(info)) {
            this.setLockInfo(info, false);
            this.setAccessCountsInfo(info);
        }
        return this.lock;
    }
    
    public CacheConfiguration getCacheConfiguration() {
        return this.cacheConfiguration;
    }
    
    public void setCacheException(final Exception cacheException) {
        this.cacheException = cacheException;
    }
    
    public Exception getCacheException() {
        return this.cacheException;
    }
    
    protected Map<String, String> listToMapConversion(final List<Object> list) {
        final Map<String, String> map = new HashMap<String, String>();
        for (int i = 0; i < list.size(); ++i) {
            map.put(String.valueOf(list.get(i++)), String.valueOf(list.get(i)));
        }
        return map;
    }
    
    protected String constructThrottleUniqueKey(final ThrottleRule throttleRule) {
        return this.throttlesKey + "_" + throttleRule.getDuration();
    }
    
    protected class AccessCountsInfo
    {
        protected final Map<Long, Integer> throttleAccessCountMap;
        
        protected AccessCountsInfo(final Map<String, String> info) {
            this.throttleAccessCountMap = new HashMap<Long, Integer>();
            for (final Long duration : DbCacheAccessInfo.this.throttlesRule.getThrottleRuleMap().keySet()) {
                this.throttleAccessCountMap.put(duration, Integer.valueOf(info.get(String.valueOf(duration))));
            }
        }
    }
}
