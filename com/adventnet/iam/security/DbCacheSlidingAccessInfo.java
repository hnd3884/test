package com.adventnet.iam.security;

import com.zoho.security.dos.TimeFrame;
import com.zoho.security.dos.Util;
import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import com.zoho.security.cache.RedisCacheAPI;
import com.zoho.security.cache.RedisLuaScript;
import java.util.Calendar;
import java.util.Map;
import com.zoho.security.cache.CacheConfiguration;

public class DbCacheSlidingAccessInfo extends DbCacheAccessInfo
{
    public DbCacheSlidingAccessInfo(final String throttlesKey, final ThrottlesRule throttlesRule, final CacheConfiguration cacheConfiguration) {
        super(throttlesKey, throttlesRule, cacheConfiguration);
    }
    
    @Override
    public AccessInfoLock tryLock(final long currentAccessTimeInMillis) {
        return super.tryLock(currentAccessTimeInMillis);
    }
    
    @Override
    protected Map<String, String> fetchInfoFromRedis(final long currentAccessTimeInMillis) {
        final Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTimeInMillis(currentAccessTimeInMillis);
        final Object info = RedisCacheAPI.evalSHA(RedisLuaScript.SLIDING_WINDOW_VALIDATION_SCRIPT, this.constrcutAndGetKeys(), this.constructAndGetArgs(currentAccessTimeInMillis, currentCalendar), this.cacheConfiguration);
        return this.listToMapConversion((List<Object>)info);
    }
    
    private List<String> constrcutAndGetKeys() {
        final List<String> keys = new ArrayList<String>();
        for (final ThrottleRule throttleRule : this.throttlesRule.getThrottleRuleMap().values()) {
            keys.add(this.constructThrottleUniqueKey(throttleRule));
        }
        return keys;
    }
    
    private List<String> constructAndGetArgs(final long currentAccessTimeInMillis, final Calendar currentCalendar) {
        final List<String> args = new ArrayList<String>();
        this.addVarArgsAsStringsInList(args, currentAccessTimeInMillis);
        args.addAll(this.throttlesRule.getServiceScopeThrottleRulesAsList());
        for (final ThrottleRule throttleRule : this.throttlesRule.getThrottleRuleMap().values()) {
            final long floorTime = Util.getFloorTimeInMillis(throttleRule.getDuration(), currentCalendar);
            final long windowEndTime = floorTime + throttleRule.getDuration();
            final long expireUnixTime = floorTime + 2L * throttleRule.getDuration();
            this.addVarArgsAsStringsInList(args, floorTime, windowEndTime, expireUnixTime);
        }
        return args;
    }
    
    @Override
    protected void setLockInfo(final Map<String, String> info, final boolean isLockedNow) {
        final LockInfo lockInfo = new LockInfo((Map)info);
        final ThrottleRule violatedThrottleRule = this.throttlesRule.getThrottleRule(lockInfo.violatedThrottleDuration);
        final TimeFrame violatedTimeFrame = new TimeFrame(lockInfo.violatedTimeframeEndTime - violatedThrottleRule.getDuration(), lockInfo.violatedTimeframeEndTime, lockInfo.violatedTimeframeAccessCount);
        this.lock = new SlidingWindowAccessInfoLock(this.throttlesRule, violatedThrottleRule, lockInfo.violatedTime, violatedTimeFrame, lockInfo.previousTimeFrameAccessCount, lockInfo.accessCountTakenFromPreviousTimeFrame);
    }
    
    private class LockInfo
    {
        private final long violatedThrottleDuration;
        private final long violatedTime;
        private final long violatedTimeframeEndTime;
        private final int violatedTimeframeAccessCount;
        private final int previousTimeFrameAccessCount;
        private final int accessCountTakenFromPreviousTimeFrame;
        
        private LockInfo(final Map<String, String> info) {
            this.violatedThrottleDuration = Long.parseLong(info.get("vtd"));
            this.violatedTime = Long.parseLong(info.get("vt"));
            this.violatedTimeframeEndTime = Long.parseLong(info.get("vtfet"));
            this.violatedTimeframeAccessCount = Integer.parseInt(info.get("vtfac"));
            this.previousTimeFrameAccessCount = Integer.parseInt(info.get("ptfac"));
            this.accessCountTakenFromPreviousTimeFrame = Integer.parseInt(info.get("acfptf"));
        }
    }
}
