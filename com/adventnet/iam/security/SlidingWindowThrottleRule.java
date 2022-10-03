package com.adventnet.iam.security;

import org.w3c.dom.Element;

public class SlidingWindowThrottleRule extends FixedWindowThrottleRule
{
    SlidingWindowThrottleRule(final Element throttleEle) throws RuntimeException {
        super(throttleEle);
    }
    
    public SlidingWindowThrottleRule(final String duration, final int threshold) throws RuntimeException {
        super(duration, threshold);
    }
    
    @Override
    protected void validateConfiguration() throws RuntimeException {
        if (this.threshold < 1) {
            throw new RuntimeException("The configuration 'threshold' is mandatory and the value is must be > 0 for SLIDING window throttle.");
        }
    }
    
    @Override
    public LockType getLockType() {
        return LockType.REJECT;
    }
}
