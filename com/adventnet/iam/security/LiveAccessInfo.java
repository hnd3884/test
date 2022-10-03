package com.adventnet.iam.security;

import java.util.concurrent.atomic.AtomicInteger;

public class LiveAccessInfo extends InMemCacheAccessInfo
{
    private LiveWindowThrottleRule liveWindowThrottleRule;
    
    protected LiveAccessInfo(final String throttlesKey, final ThrottlesRule throttlesRule) {
        super(throttlesKey, throttlesRule);
        this.liveWindowThrottleRule = throttlesRule.getThrottleRuleMap().get(0L);
    }
    
    @Override
    public AccessInfoLock tryLock() {
        return this.accessEnter();
    }
    
    public AccessInfoLock accessEnter() {
        LiveAccessInfo.LIVE_WINDOW_CACHE.putIfAbsent(this.throttlesKey, new AtomicInteger(0));
        final int liveAccessCount = LiveAccessInfo.LIVE_WINDOW_CACHE.get(this.throttlesKey).incrementAndGet();
        if (liveAccessCount > this.liveWindowThrottleRule.getThreshold()) {
            this.lock = new LiveWindowAccessInfoLock(this.throttlesRule, this.liveWindowThrottleRule, System.currentTimeMillis());
        }
        this.throttleDurationVsAccessCount.put(this.liveWindowThrottleRule.getDuration(), liveAccessCount);
        return this.lock;
    }
    
    public void accessExit() {
        LiveAccessInfo.LIVE_WINDOW_CACHE.get(this.throttlesKey).decrementAndGet();
    }
    
    public LiveWindowThrottleRule getThrottleRule() {
        return this.liveWindowThrottleRule;
    }
    
    @Override
    protected boolean isLockReleasedNow(final long currentAccessTimeInMillis) {
        return false;
    }
    
    @Override
    protected AccessInfoLock lockAccessWhenViolatesThreshold(final long currentAccessTimeInMillis) {
        return null;
    }
    
    @Override
    protected void updateAccess(final long currentAccessTimeInMillis) {
    }
    
    @Override
    protected void setAccessInfoExpireTime(final long currentAccessTimeInMillis) {
    }
    
    @Override
    public long getWeight() {
        return 0L;
    }
}
