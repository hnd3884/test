package com.adventnet.iam.security;

import java.util.Map;
import com.zoho.security.dos.Util;
import java.util.Iterator;
import java.util.Calendar;
import com.zoho.security.dos.TimeFrame;

public class FixedAccessInfo extends InMemCacheAccessInfo
{
    protected final TimeFrame[] throttleTimeFrameArray;
    protected final int weight;
    
    protected FixedAccessInfo(final String throttlesKey, final ThrottlesRule throttlesRule) {
        super(throttlesKey, throttlesRule);
        this.throttleTimeFrameArray = new TimeFrame[this.throttlesRule.getThrottleRuleMap().size()];
        this.weight = 12 + 32 * this.throttleTimeFrameArray.length;
    }
    
    @Override
    public synchronized AccessInfoLock tryLock(final long currentAccessTimeInMillis) {
        return super.tryLock(currentAccessTimeInMillis);
    }
    
    @Override
    protected boolean isLockReleasedNow(final long currentAccessTimeInMillis) {
        return this.isLockPeriodExpired(currentAccessTimeInMillis, this.lock);
    }
    
    @Override
    protected AccessInfoLock lockAccessWhenViolatesThreshold(final long currentAccessTimeInMillis) {
        AccessInfoLock lock = null;
        final Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTimeInMillis(currentAccessTimeInMillis);
        int throttleTimeFrameIndex = -1;
        for (final ThrottleRule throttleRule : this.throttlesRule.getThrottleRuleMap().values()) {
            ++throttleTimeFrameIndex;
            final TimeFrame throttleTimeFrame = this.slideTimeFrame(throttleRule, currentCalendar, throttleTimeFrameIndex);
            final int accessCount = throttleTimeFrame.getAccessCount();
            if (!this.isLocked() && accessCount + 1 > throttleRule.getThreshold()) {
                lock = this.lock(throttleRule, currentAccessTimeInMillis, this.throttleTimeFrameArray[throttleTimeFrameIndex]);
            }
            this.throttleDurationVsAccessCount.put(throttleRule.getDuration(), accessCount);
        }
        return lock;
    }
    
    private TimeFrame slideTimeFrame(final ThrottleRule throttleRule, final Calendar currentCalendar, final int throttleTimeFrameIndex) {
        TimeFrame throttleTimeFrame = this.throttleTimeFrameArray[throttleTimeFrameIndex];
        if (throttleTimeFrame == null || !throttleTimeFrame.isTimeInRange(currentCalendar.getTimeInMillis())) {
            throttleTimeFrame = (this.throttleTimeFrameArray[throttleTimeFrameIndex] = this.createTimeFrame(throttleRule, currentCalendar, 0));
        }
        return throttleTimeFrame;
    }
    
    protected TimeFrame createTimeFrame(final ThrottleRule throttleRule, final Calendar currentCalendar, final int accessCount) {
        final long floorTimeInMillis = Util.getFloorTimeInMillis(throttleRule.getDuration(), currentCalendar);
        return new TimeFrame(floorTimeInMillis, floorTimeInMillis + throttleRule.getDuration(), accessCount);
    }
    
    @Override
    protected void setAccessInfoExpireTime(final long currentAccessTimeInMillis) {
        for (int i = 0; i < this.throttlesRule.getThrottleRuleMap().size(); ++i) {
            final long expireTime = this.throttleTimeFrameArray[i].getEndTime();
            if (expireTime > this.expireUnixTimeInMillis) {
                this.expireUnixTimeInMillis = expireTime;
            }
        }
    }
    
    @Override
    protected void updateAccess(final long currentAccessTimeInMillis) {
        int throttleTimeFrameIndex = 0;
        for (final Map.Entry<Long, Integer> entry : this.throttleDurationVsAccessCount.entrySet()) {
            this.throttleDurationVsAccessCount.put(entry.getKey(), entry.getValue() + 1);
            this.throttleTimeFrameArray[throttleTimeFrameIndex++].incrementAndGetAccessCount();
        }
    }
    
    private AccessInfoLock lock(final ThrottleRule violatedThrottle, final long violatedTimeInMillis, final TimeFrame violatedTimeFrame) {
        return new FixedWindowAccessInfoLock(this.throttlesRule, violatedThrottle, violatedTimeInMillis, violatedTimeFrame);
    }
    
    @Override
    protected boolean isLockPeriodExpired(final long currentAccessTimeInMillis, final AccessInfoLock lock) {
        return !((FixedWindowAccessInfoLock)lock).getViolatedTimeFrame().isTimeInRange(currentAccessTimeInMillis);
    }
    
    @Override
    public long getWeight() {
        return this.weight;
    }
}
