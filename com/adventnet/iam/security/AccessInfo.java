package com.adventnet.iam.security;

import com.zoho.security.cache.RedisCacheAPI;
import com.zoho.security.util.HashUtil;
import com.zoho.security.cache.CacheConfiguration;
import java.util.HashMap;
import java.util.Map;

public abstract class AccessInfo
{
    public static final String DELIMITER = "_";
    protected final String throttlesKey;
    protected final ThrottlesRule throttlesRule;
    protected Map<Long, Integer> throttleDurationVsAccessCount;
    protected AccessInfoLock lock;
    
    protected AccessInfo(final String throttlesKey, final ThrottlesRule throttlesRule) {
        this.throttleDurationVsAccessCount = new HashMap<Long, Integer>();
        this.throttlesKey = throttlesKey;
        this.throttlesRule = throttlesRule;
    }
    
    public static <T extends InMemCacheAccessInfo> AccessInfo getAppServerScopeInstance(final String throttlesKey, final ThrottlesRule throttlesRule) {
        final String lookUpThrottlesKey = throttlesKey + "_" + throttlesRule.getWindow().ordinal();
        InMemCacheAccessInfo info = InMemCacheAccessInfo.ACCESSINFO_CACHE.get(lookUpThrottlesKey);
        if (info != null) {
            return info;
        }
        switch (throttlesRule.getWindow()) {
            case ROLLING: {
                info = new RollingAccessInfo(throttlesKey, throttlesRule);
                break;
            }
            case FIXED: {
                info = new FixedAccessInfo(throttlesKey, throttlesRule);
                break;
            }
            case SLIDING: {
                info = new SlidingAccessInfo(throttlesKey, throttlesRule);
                break;
            }
            default: {
                throw new RuntimeException("AccessInfo instance should not be created for LIVE window");
            }
        }
        final Object infoObj = InMemCacheAccessInfo.ACCESSINFO_CACHE.putIfAbsent(lookUpThrottlesKey, info);
        return (infoObj == null) ? info : ((AccessInfo)infoObj);
    }
    
    public static AccessInfo getServiceScopeInstance(final String throttlesKey, final ThrottlesRule throttlesRule, final CacheConfiguration cacheConfiguration) {
        switch (throttlesRule.getWindow()) {
            case ROLLING: {
                return new DbCacheRollingAccessInfo(throttlesKey, throttlesRule, cacheConfiguration);
            }
            case FIXED: {
                return new DbCacheFixedAccessInfo(throttlesKey, throttlesRule, cacheConfiguration);
            }
            case SLIDING: {
                return new DbCacheSlidingAccessInfo(throttlesKey, throttlesRule, cacheConfiguration);
            }
            default: {
                throw new RuntimeException("AccessInfo instance should not be created for LIVE window");
            }
        }
    }
    
    public AccessInfoLock tryLock() {
        return this.tryLock(System.currentTimeMillis());
    }
    
    public AccessInfoLock tryLock(final long currentAccessTimeInMillis) {
        return this.getLock();
    }
    
    public int getAccessCount(final ThrottleRule throttleRule) {
        return this.getAccessCount(throttleRule.getDuration());
    }
    
    public int getAccessCount(final long throttleDuration) {
        final Integer accessCount = this.throttleDurationVsAccessCount.get(throttleDuration);
        return (accessCount != null) ? accessCount : 0;
    }
    
    public AccessInfoLock getLock() {
        return this.lock;
    }
    
    public void unLock() {
        this.lock = null;
    }
    
    protected boolean isLocked() {
        return this.getLock() != null;
    }
    
    protected boolean isLockPeriodExpired(final long currentAccessTimeInMillis, final AccessInfoLock lock) {
        return false;
    }
    
    public AccessInfoLock verifyHip(final String userEnteredHipCode) {
        if (userEnteredHipCode == null || !this.isLocked() || this.getThrottlesWindow() != ThrottlesRule.Windows.ROLLING || this.lock.getLockType() != ThrottleRule.LockType.HIP) {
            return this.lock;
        }
        final RollingWindowAccessInfoLock lock = (RollingWindowAccessInfoLock)this.getLock();
        final String oldHipDigest = lock.getHipDigest();
        if (!lock.getHipCode().equals(userEnteredHipCode)) {
            final String newHipCode = SimpleCaptchaUtil.getCaptchaString();
            final String newHipDigest = HashUtil.SHA512(newHipCode + System.currentTimeMillis());
            lock.setHipCodeAndDigest(newHipCode, newHipDigest);
            this.addHIPToCache(newHipDigest, newHipCode);
            this.refreshHIPLock(lock);
            lock.hipVerificationFailed = true;
        }
        else {
            lock.hipVerificationFailed = false;
            this.unLock();
        }
        this.removeHIPFromCache(oldHipDigest);
        return this.lock;
    }
    
    public String getHipDigest() {
        if (this.lock == null || this.getThrottlesWindow() != ThrottlesRule.Windows.ROLLING) {
            return null;
        }
        return ((RollingWindowAccessInfoLock)this.lock).getHipDigest();
    }
    
    public String getHipCode() {
        if (this.lock == null || this.getThrottlesWindow() != ThrottlesRule.Windows.ROLLING) {
            return null;
        }
        return ((RollingWindowAccessInfoLock)this.lock).getHipCode();
    }
    
    protected void addHIPToCache(final String hipDigest, final String hipCode) {
    }
    
    protected void removeHIPFromCache(final String hipDigest) {
    }
    
    protected void refreshHIPLock(final RollingWindowAccessInfoLock rLock) {
    }
    
    public static String getHipCodeFromCache(final String hipDigest) {
        if (hipDigest == null) {
            return null;
        }
        String hipCode = InMemCacheAccessInfo.HIP_CACHE.get(hipDigest);
        if (hipCode == null && DbCacheRollingAccessInfo.hip_redis_cache != null) {
            hipCode = RedisCacheAPI.getData(hipDigest, DbCacheRollingAccessInfo.hip_redis_cache);
        }
        return hipCode;
    }
    
    public String getThrottlesKey() {
        return this.throttlesKey;
    }
    
    public ThrottlesRule getThrottlesRule() {
        return this.throttlesRule;
    }
    
    public ThrottlesRule.Windows getThrottlesWindow() {
        return this.throttlesRule.getWindow();
    }
}
