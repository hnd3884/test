package com.adventnet.iam.security;

import java.util.logging.Level;
import java.util.Date;
import java.text.SimpleDateFormat;
import com.zoho.security.dos.TimeFrame;

public class FixedWindowAccessInfoLock extends AccessInfoLock
{
    private final TimeFrame violatedTimeFrame;
    private final long lockPeriod;
    
    public FixedWindowAccessInfoLock(final ThrottlesRule violatedThrottlesRule, final ThrottleRule violatedThrottle, final long violatedTimeInMillis, final TimeFrame violatedTimeFrame) {
        super(violatedThrottlesRule, violatedThrottle, violatedTimeInMillis);
        this.violatedTimeFrame = violatedTimeFrame;
        this.lockPeriod = violatedTimeFrame.getEndTime() - violatedTimeInMillis;
    }
    
    public TimeFrame getViolatedTimeFrame() {
        return this.violatedTimeFrame;
    }
    
    public long getLockPeriodInMillis() {
        return this.lockPeriod;
    }
    
    public long getRemainingLockPeriodInMillis() {
        return this.violatedTimeFrame.getEndTime() - System.currentTimeMillis();
    }
    
    public long getRemainingLockPeriodInMillis(final long accessTimeInMillis) {
        return this.violatedTimeFrame.getEndTime() - accessTimeInMillis;
    }
    
    @Override
    public void logError(final long currentAccessTime) {
        final SimpleDateFormat format = new SimpleDateFormat("H:m:s:S");
        final String currentRequestArrivalTime = format.format(new Date(currentAccessTime));
        final String violatedTime = format.format(new Date(this.violatedTimeInMillis));
        final long approximateConsumedDuration = this.violatedTimeInMillis - this.violatedTimeFrame.getStartTime();
        final long remainingLockPeriod = this.getRemainingLockPeriodInMillis(currentAccessTime);
        final String lockReleaseTime = format.format(new Date(this.violatedTimeFrame.getEndTime()));
        final String timeFrameStartTime = format.format(new Date(this.violatedTimeFrame.getStartTime()));
        final String timeFrameEndTime = format.format(new Date(this.violatedTimeFrame.getEndTime()));
        FixedWindowAccessInfoLock.LOGGER.log(Level.SEVERE, "The request is locked for throttle violation. CurrentRequestArrivalTime (H:M:S:MS): {0}, ViolatedTime (H:M:S:MS): {1}, ConsumedDuration (ms) \u2248 {2}, RemainingLockPeriod (ms) / TotalLockPeriod (ms) : {3} / {4}, LockReleaseAt: {5}, ViolatedTimePeriod (H:M:S:MS): [{6} - {7}], ViolatedThrottleRule: {8}, ViolatedThrottlesRule: {9}", new Object[] { currentRequestArrivalTime, violatedTime, approximateConsumedDuration, remainingLockPeriod, this.lockPeriod, lockReleaseTime, timeFrameStartTime, timeFrameEndTime, this.violatedThrottlesRule, this.violatedThrottle });
    }
}
