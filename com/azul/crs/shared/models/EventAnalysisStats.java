package com.azul.crs.shared.models;

import com.azul.crs.com.fasterxml.jackson.annotation.JsonIgnore;
import com.azul.crs.com.fasterxml.jackson.annotation.JsonCreator;
import com.azul.crs.com.fasterxml.jackson.annotation.JsonProperty;

public class EventAnalysisStats
{
    private int totalEventCount;
    private int unresolvedEventCount;
    private long analysisTime;
    
    public EventAnalysisStats() {
    }
    
    @JsonCreator
    public EventAnalysisStats(@JsonProperty("totalEventCount") final int totalEventCount, @JsonProperty("unresolvedEventCount") final int unresolvedEventCount, @JsonProperty("analysisTime") final long analysisTime) {
        this.totalEventCount = totalEventCount;
        this.unresolvedEventCount = unresolvedEventCount;
        this.analysisTime = analysisTime;
    }
    
    public int getTotalEventCount() {
        return this.totalEventCount;
    }
    
    public int getUnresolvedEventCount() {
        return this.unresolvedEventCount;
    }
    
    public long getAnalysisTime() {
        return this.analysisTime;
    }
    
    public void setAnalysisTime(final long time) {
        this.analysisTime = time;
    }
    
    public void setTotalEventCount(final int count) {
        this.totalEventCount = count;
    }
    
    public void setUnresolvedEventCount(final int count) {
        this.unresolvedEventCount = count;
    }
    
    public EventAnalysisStats analysisTime(final long time) {
        this.setAnalysisTime(time);
        return this;
    }
    
    public EventAnalysisStats totalEventCount(final int count) {
        this.setTotalEventCount(count);
        return this;
    }
    
    public EventAnalysisStats unresolvedEventCount(final int count) {
        this.setUnresolvedEventCount(count);
        return this;
    }
    
    public EventAnalysisStats addTotalEventCount(final int count) {
        this.totalEventCount += count;
        return this;
    }
    
    public EventAnalysisStats addUnresolvedEventCount(final int count) {
        this.unresolvedEventCount += count;
        return this;
    }
    
    @JsonIgnore
    public boolean isEmpty() {
        return this.totalEventCount == 0;
    }
    
    public void merge(final int totalEventCount, final int unresolvedEventCount) {
        this.unresolvedEventCount += unresolvedEventCount;
        this.totalEventCount += totalEventCount;
        this.analysisTime = System.currentTimeMillis();
    }
    
    public void merge(final EventAnalysisStats stats) {
        this.unresolvedEventCount += stats.unresolvedEventCount;
        this.totalEventCount += stats.totalEventCount;
        this.analysisTime = stats.analysisTime;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final EventAnalysisStats stats = (EventAnalysisStats)o;
        return this.totalEventCount == stats.totalEventCount && this.unresolvedEventCount == stats.unresolvedEventCount && this.analysisTime == stats.analysisTime;
    }
    
    @Override
    public String toString() {
        return "Entry{totalEventCount=" + this.totalEventCount + ", unresolvedEventCount=" + this.unresolvedEventCount + ", analysisTime=" + this.analysisTime + '}';
    }
}
