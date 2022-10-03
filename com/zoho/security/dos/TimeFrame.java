package com.zoho.security.dos;

public class TimeFrame implements Cloneable
{
    public static final int STATIC_WEIGHT = 32;
    private final long startTime;
    private final long endTime;
    private int accessCount;
    
    public TimeFrame(final long intervalStartTime, final long intervalEndTime, final int accessCount) {
        this.startTime = intervalStartTime;
        this.endTime = intervalEndTime;
        this.accessCount = accessCount;
    }
    
    public int incrementAndGetAccessCount() {
        return ++this.accessCount;
    }
    
    public void setAccessCount(final int accessCount) {
        this.accessCount = accessCount;
    }
    
    public int getAccessCount() {
        return this.accessCount;
    }
    
    public boolean isTimeInRange(final long currentAccessTimeInMillis) {
        return currentAccessTimeInMillis >= this.startTime && currentAccessTimeInMillis < this.endTime;
    }
    
    public long getStartTime() {
        return this.startTime;
    }
    
    public long getEndTime() {
        return this.endTime;
    }
    
    public double calculateRemainingTimePeriodInPercentage(final long anyTimeWithinTheTimeFrame) {
        return (this.getEndTime() - (double)anyTimeWithinTheTimeFrame) / (this.getEndTime() - this.getStartTime());
    }
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
