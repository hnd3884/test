package com.adventnet.iam.security;

import java.util.HashMap;
import com.zoho.security.dos.TimeFrame;
import java.util.List;
import com.zoho.security.cache.RedisCacheAPI;
import com.zoho.security.cache.RedisLuaScript;
import java.util.Collection;
import java.util.ArrayList;
import com.zoho.security.util.HashUtil;
import java.util.Map;
import com.zoho.security.cache.CacheConfiguration;

public class DbCacheRollingAccessInfo extends DbCacheAccessInfo
{
    protected static CacheConfiguration hip_redis_cache;
    
    DbCacheRollingAccessInfo(final String throttlesKey, final ThrottlesRule throttlesRule, final CacheConfiguration cacheConfiguration) {
        super(throttlesKey, throttlesRule, cacheConfiguration);
    }
    
    public static void setRedisCacheConfigurationOfHip(final CacheConfiguration cacheConfiguration) {
        DbCacheRollingAccessInfo.hip_redis_cache = cacheConfiguration;
    }
    
    @Override
    public AccessInfoLock tryLock(final long currentAccessTimeInMillis) {
        return super.tryLock(currentAccessTimeInMillis);
    }
    
    @Override
    protected Map<String, String> fetchInfoFromRedis(final long currentAccessTimeInMillis) {
        final String hipCode = SimpleCaptchaUtil.getCaptchaString();
        final String hipDigest = HashUtil.SHA512(System.currentTimeMillis() + hipCode);
        final List<String> keys = new ArrayList<String>();
        this.addVarArgsAsStringsInList(keys, this.throttlesKey, this.throttlesLockKey);
        final List<String> args = new ArrayList<String>();
        this.addVarArgsAsStringsInList(args, currentAccessTimeInMillis, hipCode, hipDigest, this.throttlesRule.getTimeLogInterval(), this.throttlesRule.getMaxDuration());
        args.addAll(this.throttlesRule.getServiceScopeThrottleRulesAsList());
        final Object info = RedisCacheAPI.evalSHA(RedisLuaScript.ROLLING_WINDOW_VALIDATOR_SCRIPT, keys, args, this.cacheConfiguration);
        return this.listToMapConversion((List<Object>)info);
    }
    
    @Override
    protected void setLockInfo(final Map<String, String> info, final boolean isLockedNow) {
        final LockInfo lockInfo = new LockInfo((Map)info);
        final ThrottleRule violatedThrottleRule = this.throttlesRule.getThrottleRule(lockInfo.violatedThrottleDuration);
        final TimeFrame startTimeFrame = new TimeFrame(lockInfo.startTimeFrameTime, lockInfo.startTimeFrameTime + this.throttlesRule.getTimeLogInterval(), lockInfo.startTimeFrameAccessCount);
        if (violatedThrottleRule.getLockType() == ThrottleRule.LockType.HIP) {
            this.lock = new RollingWindowAccessInfoLock(this.throttlesRule, violatedThrottleRule, lockInfo.violatedTime, startTimeFrame, lockInfo.accessCountTakenFromStartTimeFrame, lockInfo.hipCode, lockInfo.hipDigest);
            if (isLockedNow) {
                this.addHIPToCache(lockInfo.hipDigest, lockInfo.hipCode);
            }
        }
        else {
            this.lock = new RollingWindowAccessInfoLock(this.throttlesRule, violatedThrottleRule, lockInfo.violatedTime, startTimeFrame, lockInfo.accessCountTakenFromStartTimeFrame, lockInfo.lockPeriod);
        }
    }
    
    @Override
    protected boolean isLockPeriodExpired(final long currentRequestArrivalTimeInMillis, final AccessInfoLock lock2) {
        return this.getLock() == null;
    }
    
    @Override
    public AccessInfoLock verifyHip(final String userEnteredHipCode) {
        return super.verifyHip(userEnteredHipCode);
    }
    
    @Override
    protected void refreshHIPLock(final RollingWindowAccessInfoLock lock) {
        final Map<String, String> hipInfoMap = new HashMap<String, String>();
        hipInfoMap.put("hc", lock.getHipCode());
        hipInfoMap.put("hd", lock.getHipDigest());
        RedisCacheAPI.putDataIntoMap(this.throttlesLockKey, hipInfoMap, this.cacheConfiguration);
    }
    
    @Override
    protected void addHIPToCache(final String hipDigest, final String hipCode) {
        RedisCacheAPI.setData(hipDigest, hipCode, DbCacheRollingAccessInfo.hip_redis_cache);
    }
    
    @Override
    protected void removeHIPFromCache(final String hipDigest) {
        RedisCacheAPI.removeData(hipDigest, DbCacheRollingAccessInfo.hip_redis_cache);
    }
    
    @Override
    public void unLock() {
        RedisCacheAPI.removeData(this.throttlesLockKey, this.cacheConfiguration);
        super.unLock();
    }
    
    static {
        DbCacheRollingAccessInfo.hip_redis_cache = null;
    }
    
    private class LockInfo
    {
        private final long violatedThrottleDuration;
        private final long violatedTime;
        private final long lockPeriod;
        private final String hipCode;
        private final String hipDigest;
        private final long startTimeFrameTime;
        private final int startTimeFrameAccessCount;
        private final int accessCountTakenFromStartTimeFrame;
        
        private LockInfo(final Map<String, String> info) {
            this.violatedThrottleDuration = Long.parseLong(info.get("vtd"));
            this.violatedTime = Long.parseLong(info.get("vt"));
            this.lockPeriod = ((info.get("lp") == null) ? -1L : Long.parseLong(info.get("lp")));
            this.hipCode = ((info.get("hc") == null) ? null : info.get("hc"));
            this.hipDigest = ((info.get("hd") == null) ? null : info.get("hd"));
            this.startTimeFrameTime = Long.parseLong(info.get("vtsatft"));
            this.startTimeFrameAccessCount = Integer.parseInt(info.get("vtsatfac"));
            this.accessCountTakenFromStartTimeFrame = Integer.parseInt(info.get("actfosatf"));
        }
    }
}
