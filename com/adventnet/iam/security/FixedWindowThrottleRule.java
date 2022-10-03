package com.adventnet.iam.security;

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Element;

public class FixedWindowThrottleRule extends ThrottleRule
{
    protected FixedWindowThrottleRule(final Element throttleEle) throws RuntimeException {
        super(throttleEle);
        this.validateConfiguration();
    }
    
    public FixedWindowThrottleRule(final String duration, final int threshold) throws RuntimeException {
        super(duration, threshold);
        this.validateConfiguration();
    }
    
    @Override
    protected void validateConfiguration() throws RuntimeException {
        if (this.threshold < 1) {
            throw new RuntimeException("The configuration 'threshold' is mandatory and the value is must be > 0 for FIXED window throttle.");
        }
    }
    
    @Override
    public LockType getLockType() {
        return LockType.TIME;
    }
    
    @Override
    List<String> getRuleAsList() {
        final List<String> ruleAsList = new ArrayList<String>(2);
        ruleAsList.add(String.valueOf(this.duration));
        ruleAsList.add(String.valueOf(this.threshold));
        return ruleAsList;
    }
    
    @Override
    public String toString() {
        final StringBuilder toString = new StringBuilder();
        toString.append("Duration (ms): ");
        toString.append(this.duration);
        toString.append(", Threshold: ");
        toString.append(this.threshold);
        return toString.append(".").toString();
    }
}
