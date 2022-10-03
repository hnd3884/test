package com.adventnet.iam.security;

import com.zoho.security.dos.TimeFrame;
import java.util.Iterator;
import java.util.Calendar;

public class SlidingAccessInfo extends FixedAccessInfo
{
    private final int[] previousTimeFrameAccessCounts;
    private final long weight;
    
    protected SlidingAccessInfo(final String throttlesKey, final ThrottlesRule throttlesRule) {
        super(throttlesKey, throttlesRule);
        this.previousTimeFrameAccessCounts = new int[this.throttlesRule.getThrottleRuleMap().size()];
        this.weight = super.weight + 12 + 4 * this.previousTimeFrameAccessCounts.length;
    }
    
    @Override
    public synchronized AccessInfoLock tryLock(final long currentAccessTimeInMillis) {
        return super.tryLock(currentAccessTimeInMillis);
    }
    
    @Override
    protected AccessInfoLock lockAccessWhenViolatesThreshold(final long currentAccessTimeInMillis) {
        final Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTimeInMillis(currentAccessTimeInMillis);
        AccessInfoLock lock = null;
        int throttleTimeFrameIndex = -1;
        for (final ThrottleRule throttleRule : this.throttlesRule.getThrottleRuleMap().values()) {
            ++throttleTimeFrameIndex;
            int accessCount = 0;
            final TimeFrame throttleCurrentTimeFrame = this.slideTimeFrame(throttleRule, currentCalendar, currentAccessTimeInMillis, throttleTimeFrameIndex);
            final int accessCountTakenFromPreviousTimeFrame = (this.previousTimeFrameAccessCounts[throttleTimeFrameIndex] > 0) ? ((int)(this.previousTimeFrameAccessCounts[throttleTimeFrameIndex] * throttleCurrentTimeFrame.calculateRemainingTimePeriodInPercentage(currentAccessTimeInMillis))) : 0;
            accessCount = throttleCurrentTimeFrame.getAccessCount() + accessCountTakenFromPreviousTimeFrame;
            if (lock == null && accessCount + 1 > throttleRule.getThreshold()) {
                lock = new SlidingWindowAccessInfoLock(this.throttlesRule, throttleRule, currentAccessTimeInMillis, throttleCurrentTimeFrame, this.previousTimeFrameAccessCounts[throttleTimeFrameIndex], accessCountTakenFromPreviousTimeFrame);
            }
            this.throttleDurationVsAccessCount.put(throttleRule.getDuration(), accessCount);
        }
        return lock;
    }
    
    private TimeFrame slideTimeFrame(final ThrottleRule throttleRule, final Calendar currentCalendar, final long currentAccessTimeInMillis, final int throttleTimeFrameIndex) {
        TimeFrame throttleTimeFrame = this.throttleTimeFrameArray[throttleTimeFrameIndex];
        if (throttleTimeFrame == null) {
            throttleTimeFrame = (this.throttleTimeFrameArray[throttleTimeFrameIndex] = this.createTimeFrame(throttleRule, currentCalendar, 0));
        }
        else if (!throttleTimeFrame.isTimeInRange(currentAccessTimeInMillis)) {
            this.previousTimeFrameAccessCounts[throttleTimeFrameIndex] = ((currentAccessTimeInMillis < throttleTimeFrame.getEndTime() + throttleRule.getDuration()) ? throttleTimeFrame.getAccessCount() : 0);
            throttleTimeFrame = (this.throttleTimeFrameArray[throttleTimeFrameIndex] = this.createTimeFrame(throttleRule, currentCalendar, 0));
        }
        return throttleTimeFrame;
    }
    
    @Override
    protected void setAccessInfoExpireTime(final long currentAccessTimeInMillis) {
        int i = 0;
        for (final Long throttleDuration : this.throttlesRule.getThrottleRuleMap().keySet()) {
            final long expireTime = this.throttleTimeFrameArray[i++].getEndTime() + throttleDuration;
            if (expireTime > this.expireUnixTimeInMillis) {
                this.expireUnixTimeInMillis = expireTime;
            }
        }
    }
    
    @Override
    public long getWeight() {
        return this.weight;
    }
}
