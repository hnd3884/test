package com.adventnet.iam.security;

import java.util.List;
import com.zoho.security.dos.Util;
import org.w3c.dom.Element;
import java.util.logging.Logger;

public abstract class ThrottleRule
{
    protected static final Logger LOGGER;
    protected final long duration;
    protected final int threshold;
    
    protected ThrottleRule(final Element throttleEle) {
        this(throttleEle.getAttribute("duration"), (throttleEle.getAttribute("threshold") != null) ? Integer.parseInt(throttleEle.getAttribute("threshold")) : -1);
    }
    
    protected ThrottleRule(final String duration, final int threshold) {
        this.duration = Util.getTimeInMillis(duration);
        this.threshold = threshold;
    }
    
    protected ThrottleRule(final int threshold) {
        this.duration = 0L;
        this.threshold = threshold;
    }
    
    protected abstract void validateConfiguration() throws RuntimeException;
    
    public long getDuration() {
        return this.duration;
    }
    
    public int getThreshold() {
        return this.threshold;
    }
    
    public abstract LockType getLockType();
    
    public static ThrottleRule createThrottleRule(final Element throttleEle, final ThrottlesRule.Windows throttlesWindow) throws RuntimeException {
        switch (throttlesWindow) {
            case ROLLING: {
                return new RollingWindowThrottleRule(throttleEle);
            }
            case FIXED: {
                return new FixedWindowThrottleRule(throttleEle);
            }
            case SLIDING: {
                return new SlidingWindowThrottleRule(throttleEle);
            }
            case LIVE: {
                return new LiveWindowThrottleRule(throttleEle);
            }
            default: {
                return null;
            }
        }
    }
    
    List<String> getRuleAsList() {
        return null;
    }
    
    static {
        LOGGER = Logger.getLogger(ThrottleRule.class.getName());
    }
    
    public enum LockType
    {
        TIME, 
        HIP, 
        REJECT;
    }
}
