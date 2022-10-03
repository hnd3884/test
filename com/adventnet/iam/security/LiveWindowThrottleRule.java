package com.adventnet.iam.security;

import org.w3c.dom.Element;

public class LiveWindowThrottleRule extends ThrottleRule
{
    LiveWindowThrottleRule(final Element throttleEle) throws RuntimeException {
        super((throttleEle.getAttribute("threshold") != null) ? Integer.parseInt(throttleEle.getAttribute("threshold")) : -1);
        this.validateConfiguration();
    }
    
    public LiveWindowThrottleRule(final int threshold) throws RuntimeException {
        super(threshold);
        this.validateConfiguration();
    }
    
    @Override
    protected void validateConfiguration() throws RuntimeException {
    }
    
    @Override
    public LockType getLockType() {
        return LockType.REJECT;
    }
    
    @Override
    public String toString() {
        final StringBuilder toString = new StringBuilder();
        toString.append("LiveWindowThrottleRule:: ");
        toString.append("Threshold: ");
        toString.append(this.threshold);
        return toString.append(".").toString();
    }
}
