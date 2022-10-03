package com.adventnet.iam.security;

import java.util.Date;
import java.util.logging.Level;
import java.text.SimpleDateFormat;
import com.zoho.security.dos.TimeFrame;

public class RollingWindowAccessInfoLock extends AccessInfoLock
{
    private final TimeFrame startTimeFrame;
    private final int accessCountTakenFromOutTimeFrame;
    private final long lockPeriodInMillis;
    private String hipCode;
    private String hipDigest;
    protected boolean hipVerificationFailed;
    
    RollingWindowAccessInfoLock(final ThrottlesRule violatedThrottlesRule, final ThrottleRule violatedThrottle, final long violatedTimeInMillis, final TimeFrame startTimeFrame, final int accessCountTakenFromOutTimeFrame, final long lockPeriodInMillis, final String hipCode, final String hipDigest) {
        super(violatedThrottlesRule, violatedThrottle, violatedTimeInMillis);
        this.startTimeFrame = startTimeFrame;
        this.accessCountTakenFromOutTimeFrame = accessCountTakenFromOutTimeFrame;
        this.lockPeriodInMillis = lockPeriodInMillis;
        this.hipCode = hipCode;
        this.hipDigest = hipDigest;
    }
    
    RollingWindowAccessInfoLock(final ThrottlesRule violatedThrottlesRule, final ThrottleRule violatedThrottle, final long violatedTimeInMillis, final TimeFrame startTimeFrame, final int accessCountTakenFromOutTimeFrame, final long lockPeriodInMillis) {
        this(violatedThrottlesRule, violatedThrottle, violatedTimeInMillis, startTimeFrame, accessCountTakenFromOutTimeFrame, lockPeriodInMillis, null, null);
    }
    
    RollingWindowAccessInfoLock(final ThrottlesRule violatedThrottlesRule, final ThrottleRule violatedThrottle, final long violatedTimeInMillis, final TimeFrame startTimeFrame, final int accessCountTakenFromOutTimeFrame, final String hipCode, final String hipDigest) {
        this(violatedThrottlesRule, violatedThrottle, violatedTimeInMillis, startTimeFrame, accessCountTakenFromOutTimeFrame, -1L, hipCode, hipDigest);
    }
    
    public void setHipCodeAndDigest(final String hipCode, final String hipDigest) {
        this.hipCode = hipCode;
        this.hipDigest = hipDigest;
    }
    
    @Override
    public void logError(final long currentAccessTime) {
        final SimpleDateFormat format = new SimpleDateFormat("H:m:s:S");
        RollingWindowAccessInfoLock.LOGGER.log(Level.SEVERE, "The request is locked for throttle violation. ViolatedTimePeriod (H:M:S:MS): [{0} - {1}], Violated time (H:M:S:MS): {2}, CurrentRequestArrivalTime (H:M:S:MS): {3}, AccessCountTakenFromStartTimeFrame / StartTimeFrameAccessCount : {4} / {5}, ConsumedDuration (ms) \u2248 {6}, LockReleaseAt: {7}, RemainingLockPeriod (ms) / TotalLockPeriod (ms) : {8} / {9}, hipCode: {10}, hipDigest: {11}, ViolatedThrottleRule: {12}, ViolatedThrottlesRule: {13}", new Object[] { format.format(new Date(this.startTimeFrame.getStartTime())), format.format(new Date(this.violatedTimeInMillis)), format.format(new Date(this.violatedTimeInMillis)), format.format(new Date(currentAccessTime)), this.accessCountTakenFromOutTimeFrame, this.startTimeFrame.getAccessCount(), this.violatedTimeInMillis - this.startTimeFrame.getStartTime(), format.format(new Date(this.getLockReleasedTimeInMillis())), this.getRemainingLockPeriodInMillis(currentAccessTime), this.lockPeriodInMillis, this.hipCode, this.hipDigest, this.violatedThrottlesRule, this.violatedThrottle });
    }
    
    public TimeFrame getStartTimeFrame() {
        return this.startTimeFrame;
    }
    
    public int getAccessCountTakenFromOutTimeFrame() {
        return this.accessCountTakenFromOutTimeFrame;
    }
    
    public long getLockPeriodInMillis() {
        return this.lockPeriodInMillis;
    }
    
    public long getRemainingLockPeriodInMillis(final long currentAccessInMillis) {
        return (this.getLockType() == ThrottleRule.LockType.TIME) ? (this.violatedTimeInMillis + this.lockPeriodInMillis - currentAccessInMillis) : -1L;
    }
    
    public long getLockReleasedTimeInMillis() {
        return (this.getLockType() == ThrottleRule.LockType.TIME) ? (this.violatedTimeInMillis + this.lockPeriodInMillis) : -1L;
    }
    
    public String getHipCode() {
        return this.hipCode;
    }
    
    public String getHipDigest() {
        return this.hipDigest;
    }
}
