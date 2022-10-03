package com.adventnet.iam.security;

import java.util.Map;
import java.util.logging.Logger;

public class AccessInfoLock
{
    protected static final String VIOLATED_THROTTLE_DURATION = "vtd";
    protected static final String VIOLATED_TIME = "vt";
    protected static final String VIOLATED_THROTTLE_START_ACCESS_TIMEFRAME_TIME = "vtsatft";
    protected static final String VIOLATED_THROTTLE_START_ACCESS_TIMEFRAME_ACCESSCOUNT = "vtsatfac";
    protected static final String ACCESSCOUNT_TAKEN_FROM_OUT_OF_START_ACCESS_TIMEFRAME = "actfosatf";
    protected static final String LOCK_PERIOD = "lp";
    protected static final String HIP_CODE = "hc";
    protected static final String HIP_DIGEST = "hd";
    protected static final String VIOLATED_WINDOW_END_TIME = "vwet";
    protected static final String VIOLATED_TIME_FRAME_END_TIME = "vtfet";
    protected static final String VIOLATED_TIME_FRAME_ACCESSCOUNT = "vtfac";
    protected static final Logger LOGGER;
    protected final ThrottlesRule violatedThrottlesRule;
    protected final ThrottleRule violatedThrottle;
    protected final long violatedTimeInMillis;
    
    public AccessInfoLock(final ThrottlesRule violatedThrottlesRule, final ThrottleRule violatedThrottle, final long violatedTimeInMillis) {
        this.violatedThrottlesRule = violatedThrottlesRule;
        this.violatedThrottle = violatedThrottle;
        this.violatedTimeInMillis = violatedTimeInMillis;
    }
    
    protected static boolean isLocked(final Map<String, String> info) {
        return info.containsKey("vtd");
    }
    
    public ThrottlesRule getViolatedThrottlesRule() {
        return this.violatedThrottlesRule;
    }
    
    public ThrottleRule getViolatedThrottle() {
        return this.violatedThrottle;
    }
    
    public long getViolatedTimeInMillis() {
        return this.violatedTimeInMillis;
    }
    
    public ThrottleRule.LockType getLockType() {
        return this.violatedThrottle.getLockType();
    }
    
    public ThrottlesRule.Windows getViolatedThrottleWindow() {
        return this.violatedThrottlesRule.getWindow();
    }
    
    public void logError(final long currentAccessTime) {
    }
    
    static {
        LOGGER = Logger.getLogger(AccessInfoLock.class.getName());
    }
}
