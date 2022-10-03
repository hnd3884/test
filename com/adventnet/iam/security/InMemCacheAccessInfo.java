package com.adventnet.iam.security;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class InMemCacheAccessInfo extends AccessInfo
{
    public static final LRUCacheMap<String, InMemCacheAccessInfo> ACCESSINFO_CACHE;
    public static final LRUCacheMap<String, String> HIP_CACHE;
    public static final LRUCacheMap<String, AtomicInteger> LIVE_WINDOW_CACHE;
    protected long expireUnixTimeInMillis;
    
    protected InMemCacheAccessInfo(final String throttlesKey, final ThrottlesRule throttlesRule) {
        super(throttlesKey, throttlesRule);
        this.expireUnixTimeInMillis = -1L;
    }
    
    @Override
    public synchronized AccessInfoLock tryLock(final long currentAccessTimeInMillis) {
        if (this.isLocked()) {
            if (!this.isLockReleasedNow(currentAccessTimeInMillis)) {
                return this.lock;
            }
            this.unLock();
        }
        final AccessInfoLock lock = this.lockAccessWhenViolatesThreshold(currentAccessTimeInMillis);
        if (this.getThrottlesWindow() != ThrottlesRule.Windows.SLIDING) {
            this.lock = lock;
        }
        if (lock == null) {
            this.updateAccess(currentAccessTimeInMillis);
        }
        if (this.throttlesRule.getWindow() == ThrottlesRule.Windows.ROLLING) {
            this.cleanUpRollingWindowOutdatedEntries(currentAccessTimeInMillis);
        }
        this.setAccessInfoExpireTime(currentAccessTimeInMillis);
        return lock;
    }
    
    protected abstract boolean isLockReleasedNow(final long p0);
    
    protected abstract AccessInfoLock lockAccessWhenViolatesThreshold(final long p0);
    
    protected abstract void updateAccess(final long p0);
    
    protected abstract void setAccessInfoExpireTime(final long p0);
    
    protected void cleanUpRollingWindowOutdatedEntries(final long currentAccessTimeInMillis) {
    }
    
    public boolean isExpired(final long accessTimeInSec) {
        return this.expireUnixTimeInMillis != -1L && accessTimeInSec > this.expireUnixTimeInMillis;
    }
    
    public abstract long getWeight();
    
    static {
        ACCESSINFO_CACHE = new LRUCacheMap<String, InMemCacheAccessInfo>(1000000, 100000, 1, TimeUnit.HOURS);
        HIP_CACHE = new LRUCacheMap<String, String>(100000, 10000, 1, TimeUnit.HOURS);
        LIVE_WINDOW_CACHE = new LRUCacheMap<String, AtomicInteger>(180000, 135000, 3, TimeUnit.MINUTES);
    }
}
