package com.adventnet.audit.internal;

public class AuditNotifyCriteriaStatus
{
    private String principal;
    private String criteria;
    private long repeatCount;
    private long currentCount;
    private long lastOccurrenceTime;
    
    public AuditNotifyCriteriaStatus(final String principal, final String criteria, final long repeatCount, final long occurTime) {
        this.currentCount = 0L;
        this.principal = principal;
        this.criteria = criteria;
        this.repeatCount = repeatCount;
        this.lastOccurrenceTime = occurTime;
        this.currentCount = 1L;
    }
    
    public String getPrincipal() {
        return this.principal;
    }
    
    public String getCriteria() {
        return this.criteria;
    }
    
    public long getRepeatCount() {
        return this.repeatCount;
    }
    
    public long getCurrentCount() {
        return this.currentCount;
    }
    
    public void setLastOccurrenceTime(final long lastOccurrenceTime) {
        this.lastOccurrenceTime = lastOccurrenceTime;
    }
    
    public long getLastOccurrenceTime() {
        return this.lastOccurrenceTime;
    }
    
    public void incrementCurrentCount(final long count, final long lastOccurrenceTime) {
        this.currentCount += count;
        this.lastOccurrenceTime = lastOccurrenceTime;
    }
    
    public void setCurrentCount(final long currentCount) {
        this.currentCount = currentCount;
    }
}
