package com.adventnet.iam.security;

import java.util.Date;
import java.util.logging.Level;
import java.text.SimpleDateFormat;
import com.zoho.security.dos.TimeFrame;

public class SlidingWindowAccessInfoLock extends AccessInfoLock
{
    protected static final String VIOLATED_TIMEFRAME_END_TIME = "vtfet";
    protected static final String VIOLATED_TIMEFRAME_ACCESSCOUNT = "vtfac";
    protected static final String PREV_TIMEFRAME_ACCESSCOUNT = "ptfac";
    protected static final String ACCESSCOUNT_FROM_PREV_TIMEFRAME = "acfptf";
    private TimeFrame violatedTimeFrame;
    private int previousTimeFrameAccessCount;
    private int accessCountTakenFromPreviousTimeFrame;
    
    public SlidingWindowAccessInfoLock(final ThrottlesRule violatedThrottlesRule, final ThrottleRule violatedThrottle, final long violatedTimeInMillis, final TimeFrame violatedTimeFrame, final int previousTimeFrameAccessCount, final int accessCountTakenFromPreviousTimeFrame) {
        super(violatedThrottlesRule, violatedThrottle, violatedTimeInMillis);
        this.previousTimeFrameAccessCount = 0;
        this.accessCountTakenFromPreviousTimeFrame = 0;
        this.violatedTimeFrame = violatedTimeFrame;
        this.previousTimeFrameAccessCount = previousTimeFrameAccessCount;
        this.accessCountTakenFromPreviousTimeFrame = accessCountTakenFromPreviousTimeFrame;
    }
    
    public TimeFrame getViolatedTimeFrame() {
        return this.violatedTimeFrame;
    }
    
    public int getPreviousTimeFrameAccessCount() {
        return this.previousTimeFrameAccessCount;
    }
    
    public int getAccessCountTakenFromPreviousTimeFrame() {
        return this.accessCountTakenFromPreviousTimeFrame;
    }
    
    @Override
    public void logError(final long currentAccessTime) {
        final SimpleDateFormat format = new SimpleDateFormat("H:m:s:S");
        final int totalAccessCount = this.violatedTimeFrame.getAccessCount() + this.accessCountTakenFromPreviousTimeFrame;
        SlidingWindowAccessInfoLock.LOGGER.log(Level.SEVERE, "The request is blocked for throttle violation. ViolatedTimePeriod (H:M:S:MS): [{0} - {1}], CurrentViolationTime (H:M:S:MS): {2}, Total Access Count: {3}, CurrentTimeFrameAccessCount: {4}, AccessCountTakenFromPreviousTimeFrame / PreviosTimeFrameAccessCount : {5} / {6}, ViolatedThrottleRule: {7}, ViolatedThrottlesRule: {8}", new Object[] { format.format(new Date(this.violatedTimeFrame.getStartTime())), format.format(new Date(this.violatedTimeFrame.getEndTime())), format.format(new Date(currentAccessTime)), totalAccessCount, this.violatedTimeFrame.getAccessCount(), this.accessCountTakenFromPreviousTimeFrame, this.previousTimeFrameAccessCount, this.violatedThrottlesRule, this.violatedThrottle });
    }
}
