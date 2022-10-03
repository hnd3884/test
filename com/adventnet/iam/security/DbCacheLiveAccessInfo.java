package com.adventnet.iam.security;

import java.util.Map;
import com.zoho.security.cache.RedisCacheAPI;
import com.zoho.security.cache.RedisLuaScript;
import com.zoho.security.cache.CacheConfiguration;

public class DbCacheLiveAccessInfo extends DbCacheAccessInfo
{
    private String serviceID;
    private String appServerID;
    private String serviceLWMapName;
    private String appServerLWMapName;
    private LiveWindowThrottleRule liveWindowThrottleRule;
    
    DbCacheLiveAccessInfo(final String serviceID, final String appServerID, final String throttlesKey, final ThrottlesRule throttlesRule, final CacheConfiguration cacheConfiguration) {
        super(throttlesKey, throttlesRule, cacheConfiguration);
        this.serviceID = serviceID;
        this.appServerID = appServerID;
        this.liveWindowThrottleRule = throttlesRule.getThrottleRuleMap().get(0L);
        this.serviceLWMapName = "LW_SM_" + serviceID;
        this.appServerLWMapName = "LW_SAM_" + serviceID + "_" + appServerID;
    }
    
    @Override
    public AccessInfoLock tryLock() {
        return this.accessEnter();
    }
    
    public AccessInfoLock accessEnter() {
        final long liveAccessCount = (long)RedisCacheAPI.evalSHA(RedisLuaScript.LIVE_WINDOW_ACCESS_ENTER_HANDLER_SCRIPT, 3, new String[] { this.serviceLWMapName, this.appServerLWMapName, this.throttlesKey }, this.cacheConfiguration);
        if (liveAccessCount > this.liveWindowThrottleRule.getThreshold()) {
            this.lock = new LiveWindowAccessInfoLock(this.throttlesRule, this.liveWindowThrottleRule, System.currentTimeMillis());
        }
        this.throttleDurationVsAccessCount.put(this.liveWindowThrottleRule.getDuration(), (int)liveAccessCount);
        return this.lock;
    }
    
    public void accessExit() {
        RedisCacheAPI.evalSHA(RedisLuaScript.LIVE_WINDOW_ACCESS_EXIT_HANDLER_SCRIPT, 3, new String[] { this.serviceLWMapName, this.appServerLWMapName, this.throttlesKey }, this.cacheConfiguration);
    }
    
    @Override
    protected Map<String, String> fetchInfoFromRedis(final long currentAccessTimeInMillis) {
        return null;
    }
    
    @Override
    protected void setLockInfo(final Map<String, String> info, final boolean isLockedNow) {
    }
}
