package com.adventnet.iam.security;

import java.util.HashMap;
import com.zoho.security.util.HashUtil;
import java.util.Iterator;
import java.util.Map;
import com.zoho.security.dos.TimeFrame;
import java.util.LinkedList;

public class RollingAccessInfo extends InMemCacheAccessInfo
{
    protected LinkedList<TimeFrame> throttleTimeFrames;
    protected Map<Long, LinkedList<Long>> throttleDurationVsViolatedTimes;
    protected Map<Long, TimeFrame> throttleDurationVsLastViolatedTimeFrame;
    
    protected RollingAccessInfo(final String throttlesKey, final ThrottlesRule throttlesRule) {
        super(throttlesKey, throttlesRule);
        this.throttleTimeFrames = new LinkedList<TimeFrame>();
        this.throttleDurationVsViolatedTimes = null;
        this.throttleDurationVsLastViolatedTimeFrame = null;
    }
    
    @Override
    public synchronized AccessInfoLock tryLock(final long currentAccessTimeInMillis) {
        return super.tryLock(currentAccessTimeInMillis);
    }
    
    @Override
    protected boolean isLockReleasedNow(final long currentAccessTimeInMillis) {
        return this.lock.getLockType() != ThrottleRule.LockType.HIP && this.isLockPeriodExpired(currentAccessTimeInMillis, this.lock);
    }
    
    @Override
    protected AccessInfoLock lockAccessWhenViolatesThreshold(final long currentAccessTimeInMillis) throws IAMSecurityException {
        AccessInfoLock lock = null;
        for (final ThrottleRule throttleRule : this.throttlesRule.getThrottleRuleMap().values()) {
            final Object[] accessInfo = new Object[2];
            final int throttleAccessCount = this.calculateAccessCount(currentAccessTimeInMillis, throttleRule, accessInfo);
            if (!this.isLocked() && throttleAccessCount + 1 > throttleRule.getThreshold()) {
                lock = this.lock((RollingWindowThrottleRule)throttleRule, currentAccessTimeInMillis, (TimeFrame)accessInfo[0], this.throttleTimeFrames.getLast(), (int)accessInfo[1]);
            }
            this.throttleDurationVsAccessCount.put(throttleRule.getDuration(), throttleAccessCount);
        }
        return lock;
    }
    
    private int calculateAccessCount(final long currentAccessTimeInMillis, final ThrottleRule throttleRule, final Object[] accessInfo) {
        TimeFrame throttleStartTimeFrame = null;
        int throttleAccessCount = 0;
        int accessCountTakenFromOutOfDurationTimeFrame = 0;
        final Iterator<TimeFrame> timeFramesItr = this.throttleTimeFrames.descendingIterator();
        final TimeFrame violatedThrottleTimeFrame = this.getViolatedTimeFrame(throttleRule.getDuration());
        while (timeFramesItr.hasNext()) {
            final TimeFrame timeFrame = timeFramesItr.next();
            final long timeFrameStartTime = timeFrame.getStartTime();
            int timeFrameAccessCount = timeFrame.getAccessCount();
            if (currentAccessTimeInMillis - timeFrameStartTime <= throttleRule.getDuration()) {
                if (violatedThrottleTimeFrame != null && violatedThrottleTimeFrame.getStartTime() == timeFrameStartTime) {
                    throttleStartTimeFrame = timeFrame;
                    accessCountTakenFromOutOfDurationTimeFrame = timeFrameAccessCount - violatedThrottleTimeFrame.getAccessCount();
                    break;
                }
                throttleStartTimeFrame = timeFrame;
                throttleAccessCount += timeFrameAccessCount;
            }
            else {
                final long throttleWindowStartTimeInMillis = currentAccessTimeInMillis - throttleRule.getDuration();
                if (timeFrame.isTimeInRange(throttleWindowStartTimeInMillis)) {
                    timeFrameAccessCount = ((violatedThrottleTimeFrame != null && violatedThrottleTimeFrame.getStartTime() == timeFrameStartTime) ? (timeFrameAccessCount - violatedThrottleTimeFrame.getAccessCount()) : timeFrameAccessCount);
                    throttleStartTimeFrame = timeFrame;
                    accessCountTakenFromOutOfDurationTimeFrame = (int)(timeFrameAccessCount * timeFrame.calculateRemainingTimePeriodInPercentage(throttleWindowStartTimeInMillis));
                    break;
                }
                break;
            }
        }
        accessInfo[0] = throttleStartTimeFrame;
        accessInfo[1] = accessCountTakenFromOutOfDurationTimeFrame;
        return throttleAccessCount;
    }
    
    @Override
    protected void updateAccess(final long currentAccessTimeInMillis) {
        TimeFrame lastTimeFrame = (this.throttleTimeFrames.size() > 0) ? this.throttleTimeFrames.getLast() : null;
        if (lastTimeFrame == null || !lastTimeFrame.isTimeInRange(currentAccessTimeInMillis)) {
            lastTimeFrame = new TimeFrame(currentAccessTimeInMillis, currentAccessTimeInMillis + this.throttlesRule.getTimeLogInterval(), 1);
            this.throttleTimeFrames.add(lastTimeFrame);
        }
        else {
            lastTimeFrame.incrementAndGetAccessCount();
        }
        for (final Map.Entry<Long, Integer> entry : this.throttleDurationVsAccessCount.entrySet()) {
            this.throttleDurationVsAccessCount.put(entry.getKey(), entry.getValue() + 1);
        }
    }
    
    private AccessInfoLock lock(final RollingWindowThrottleRule violatedThrottle, final long violatedTimeInMillis, final TimeFrame startTimeFrame, final TimeFrame violatedTimeFrame, final int accessCountTakenFromOutTimeFrame) {
        AccessInfoLock lock = null;
        if (violatedThrottle.getLockType() == ThrottleRule.LockType.HIP) {
            final String hipCode = SimpleCaptchaUtil.getCaptchaString();
            final String hipDigest = HashUtil.SHA512(hipCode + System.currentTimeMillis());
            lock = new RollingWindowAccessInfoLock(this.throttlesRule, violatedThrottle, violatedTimeInMillis, startTimeFrame, accessCountTakenFromOutTimeFrame, hipCode, hipDigest);
            this.addHIPToCache(hipDigest, hipCode);
        }
        else {
            final long consolidatedLockPeriodInMillis = violatedThrottle.isProgressiveLockDefined() ? this.calculateProgressiveLockPeriod(violatedThrottle, violatedTimeInMillis) : violatedThrottle.getLockPeriod();
            lock = new RollingWindowAccessInfoLock(this.throttlesRule, violatedThrottle, violatedTimeInMillis, startTimeFrame, accessCountTakenFromOutTimeFrame, consolidatedLockPeriodInMillis);
        }
        this.setViolatedThrottleTimeFrame(violatedThrottle, violatedTimeFrame);
        return lock;
    }
    
    private void addThrottleViolationTime(final RollingWindowThrottleRule violatedThrottle, final long violationTime) {
        if (this.throttleDurationVsViolatedTimes == null) {
            this.throttleDurationVsViolatedTimes = new HashMap<Long, LinkedList<Long>>();
        }
        LinkedList<Long> violationTimes = this.throttleDurationVsViolatedTimes.get(violatedThrottle.getDuration());
        if (violationTimes == null) {
            violationTimes = new LinkedList<Long>();
            this.throttleDurationVsViolatedTimes.put(violatedThrottle.getDuration(), violationTimes);
        }
        violationTimes.add(violationTime);
    }
    
    private long calculateProgressiveLockPeriod(final RollingWindowThrottleRule violatedThrottle, final long violationTime) {
        this.addThrottleViolationTime(violatedThrottle, violationTime);
        final LinkedList<Long> violationTimes = this.throttleDurationVsViolatedTimes.get(violatedThrottle.getDuration());
        final Iterator<Long> urlThresholdViolationsTimesDecendingIterator = violationTimes.descendingIterator();
        long factor = 1L;
        int violationCount = 0;
        while (urlThresholdViolationsTimesDecendingIterator.hasNext()) {
            final Long previousViolationTime = urlThresholdViolationsTimesDecendingIterator.next();
            if (violationTime - previousViolationTime > violatedThrottle.getWatchTime()) {
                break;
            }
            ++violationCount;
        }
        if (violationCount > violatedThrottle.getViolationLimit()) {
            factor = DoSController.calculateLockFactor(violationCount, violatedThrottle.getViolationLimit(), violatedThrottle.getLockFactor());
        }
        return violatedThrottle.getLockPeriod() * factor;
    }
    
    private void setViolatedThrottleTimeFrame(final ThrottleRule violatedThrottle, final TimeFrame lastTimeFrame) {
        if (this.throttleDurationVsLastViolatedTimeFrame == null) {
            this.throttleDurationVsLastViolatedTimeFrame = new HashMap<Long, TimeFrame>();
        }
        try {
            this.throttleDurationVsLastViolatedTimeFrame.put(violatedThrottle.getDuration(), (TimeFrame)lastTimeFrame.clone());
        }
        catch (final CloneNotSupportedException ex) {}
    }
    
    private TimeFrame getViolatedTimeFrame(final long throttleDuration) {
        return (this.throttleDurationVsLastViolatedTimeFrame != null) ? this.throttleDurationVsLastViolatedTimeFrame.get(throttleDuration) : null;
    }
    
    @Override
    protected boolean isLockPeriodExpired(final long currentAccessTimeInMillis, final AccessInfoLock lock) {
        return currentAccessTimeInMillis - lock.getViolatedTimeInMillis() > ((RollingWindowAccessInfoLock)lock).getLockPeriodInMillis();
    }
    
    @Override
    public AccessInfoLock verifyHip(final String userEnteredHipCode) {
        return super.verifyHip(userEnteredHipCode);
    }
    
    @Override
    protected void refreshHIPLock(final RollingWindowAccessInfoLock rLock) {
        this.lock = rLock;
    }
    
    @Override
    protected void removeHIPFromCache(final String hipDigest) {
        InMemCacheAccessInfo.HIP_CACHE.remove(hipDigest);
    }
    
    @Override
    protected void addHIPToCache(final String hipDigest, final String hipCode) {
        InMemCacheAccessInfo.HIP_CACHE.put(hipDigest, hipCode);
    }
    
    @Override
    protected void cleanUpRollingWindowOutdatedEntries(final long currentAccessTimeInMillis) {
        while (this.throttleTimeFrames.size() > 0 && currentAccessTimeInMillis - this.throttleTimeFrames.getFirst().getStartTime() > this.throttlesRule.getMaxDuration()) {
            this.throttleTimeFrames.remove();
        }
        if (this.throttleDurationVsViolatedTimes != null) {
            for (final Long violatedThrottleDuration : this.throttleDurationVsViolatedTimes.keySet()) {
                final LinkedList<Long> violatedTimes = this.throttleDurationVsViolatedTimes.get(violatedThrottleDuration);
                final long violationWindowStartTime = currentAccessTimeInMillis - ((RollingWindowThrottleRule)this.throttlesRule.getThrottleRule(violatedThrottleDuration)).getWatchTime();
                while (violatedTimes.size() > 0 && violatedTimes.getFirst() < violationWindowStartTime) {
                    violatedTimes.remove();
                }
            }
        }
    }
    
    @Override
    public void unLock() {
        super.unLock();
    }
    
    @Override
    protected void setAccessInfoExpireTime(final long currentAccessTimeInMillis) {
        long expireTime;
        if (this.lock == null) {
            expireTime = currentAccessTimeInMillis + this.throttlesRule.getMaxDuration();
        }
        else {
            expireTime = ((this.lock.getLockType() == ThrottleRule.LockType.HIP) ? -1L : (this.lock.getViolatedTimeInMillis() + ((RollingWindowAccessInfoLock)this.lock).getLockPeriodInMillis()));
        }
        if (expireTime == -1L || expireTime > this.expireUnixTimeInMillis) {
            this.expireUnixTimeInMillis = expireTime;
        }
    }
    
    @Override
    public long getWeight() {
        return 12 + 32 * this.throttleTimeFrames.size();
    }
}
