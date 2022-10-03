package com.adventnet.iam.security;

import java.util.ArrayList;
import java.util.List;
import com.zoho.security.dos.Util;
import org.w3c.dom.Element;

public class RollingWindowThrottleRule extends ThrottleRule
{
    private long lockPeriod;
    private boolean hip;
    private long watchTime;
    private int violationLimit;
    private int lockFactor;
    
    RollingWindowThrottleRule(final Element throttleEle) throws RuntimeException {
        this(throttleEle.getAttribute("duration"), throttleEle.getAttribute("threshold"), throttleEle.getAttribute("lock-period"), "true".equals(throttleEle.getAttribute("hip")), throttleEle.getAttribute("watch-time"), throttleEle.getAttribute("violation-limit"), throttleEle.getAttribute("lock-factor"));
    }
    
    RollingWindowThrottleRule(final String duration, final String threshold, final String lockPeriod, final boolean hip, final String watchTime, final String violationLimit, final String lockFactor) throws RuntimeException {
        super(duration, Integer.parseInt(threshold));
        this.lockPeriod = -1L;
        this.watchTime = -1L;
        this.violationLimit = -1;
        this.lockFactor = -1;
        this.lockPeriod = Util.getTimeInMillis(lockPeriod);
        this.hip = hip;
        this.watchTime = Util.getTimeInMillis(watchTime);
        if (this.watchTime != -1L) {
            this.violationLimit = ((violationLimit != null) ? Integer.parseInt(violationLimit) : -1);
            this.lockFactor = ((lockFactor != null) ? Integer.parseInt(lockFactor) : -1);
        }
        this.validateConfiguration();
    }
    
    public RollingWindowThrottleRule(final String duration, final int threshold, final String lockPeriod) throws RuntimeException {
        super(duration, threshold);
        this.lockPeriod = -1L;
        this.watchTime = -1L;
        this.violationLimit = -1;
        this.lockFactor = -1;
        this.lockPeriod = Util.getTimeInMillis(lockPeriod);
        this.validateConfiguration();
    }
    
    public RollingWindowThrottleRule(final String duration, final int threshold, final boolean hip) {
        super(duration, threshold);
        this.lockPeriod = -1L;
        this.watchTime = -1L;
        this.violationLimit = -1;
        this.lockFactor = -1;
        this.hip = hip;
        this.validateConfiguration();
    }
    
    public void setProgessiveLock(final String watchTime, final int violationLimit, final int lockFactor) {
        this.watchTime = Util.getTimeInMillis(watchTime);
        this.violationLimit = violationLimit;
        this.lockFactor = lockFactor;
        this.validateProgressiveLockConfiguration();
    }
    
    @Override
    protected void validateConfiguration() throws RuntimeException {
        if (this.threshold < 1) {
            throw new RuntimeException("The configuration 'threshold' is mandatory and the value is must be > 0 for ROLLING window throttle.");
        }
        if ((this.lockPeriod == -1L && !this.hip) || (this.lockPeriod != -1L && this.hip)) {
            throw new RuntimeException("Either the configuration 'lock-period' or 'hip' is mandatory for ROLLING window throttle.");
        }
        if (this.watchTime != -1L) {
            this.validateProgressiveLockConfiguration();
        }
    }
    
    private void validateProgressiveLockConfiguration() {
        if (this.watchTime == -1L) {
            throw new RuntimeException("The configuration 'watch-time' is mandatory and the value must be a valid time format(D:H:M:S:MS).");
        }
        if (this.violationLimit < 1) {
            throw new RuntimeException("The configuration 'violation-limit' is mandatory and the value is must be > 0 for ROLLING window throttle if the throttle has the configuration 'watch-time'.");
        }
        if (this.lockFactor < 2) {
            throw new RuntimeException("The configuration 'lock-factor' is mandatory and the value is must be > 1 for ROLLING window throttle if the throttle has configuration 'watch-time'.");
        }
        if (this.hip) {
            throw new RuntimeException("Progressive lock is not supported if throttle has 'hip' lock.");
        }
    }
    
    @Override
    public LockType getLockType() {
        if (this.hip) {
            return LockType.HIP;
        }
        return LockType.TIME;
    }
    
    public long getLockPeriod() {
        return this.lockPeriod;
    }
    
    public long getWatchTime() {
        return this.watchTime;
    }
    
    public int getViolationLimit() {
        return this.violationLimit;
    }
    
    public int getLockFactor() {
        return this.lockFactor;
    }
    
    public boolean isProgressiveLockDefined() {
        return this.watchTime != -1L;
    }
    
    @Override
    List<String> getRuleAsList() {
        final List<String> ruleAsList = new ArrayList<String>(6);
        ruleAsList.add(String.valueOf(this.duration));
        ruleAsList.add(String.valueOf(this.threshold));
        ruleAsList.add(String.valueOf(this.lockPeriod));
        ruleAsList.add(String.valueOf(this.watchTime));
        ruleAsList.add(String.valueOf(this.violationLimit));
        ruleAsList.add(String.valueOf(this.lockFactor));
        return ruleAsList;
    }
    
    @Override
    public String toString() {
        final StringBuilder toString = new StringBuilder();
        toString.append("RollingWindowThrottleRule:: ");
        toString.append("Duration: ");
        toString.append(this.duration);
        toString.append(", Threshold: ");
        toString.append(this.threshold);
        toString.append(", LockPeriod: ");
        toString.append(this.lockPeriod);
        toString.append(", Hip: ");
        toString.append(this.hip);
        toString.append(", WatchTime: ");
        toString.append(this.watchTime);
        toString.append(", ViolationLimit: ");
        toString.append(this.violationLimit);
        toString.append(", LockFactor: ");
        toString.append(this.lockFactor);
        return toString.append(".").toString();
    }
}
