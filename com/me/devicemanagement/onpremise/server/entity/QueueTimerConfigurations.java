package com.me.devicemanagement.onpremise.server.entity;

import java.util.List;

public class QueueTimerConfigurations
{
    private boolean isFeatureEnabled;
    private int threadThreshold;
    private List<String> allowedQueues;
    private long allowedTimeLimit;
    
    public QueueTimerConfigurations() {
        this.isFeatureEnabled = false;
    }
    
    public boolean isFeatureEnabled() {
        return this.isFeatureEnabled;
    }
    
    public void setFeatureEnabled(final boolean featureEnabled) {
        this.isFeatureEnabled = featureEnabled;
    }
    
    public int getThreadThreshold() {
        return this.threadThreshold;
    }
    
    public void setThreadThreshold(final int threadThreshold) {
        this.threadThreshold = threadThreshold;
    }
    
    public List<String> getAllowedQueues() {
        return this.allowedQueues;
    }
    
    public void setAllowedQueues(final List<String> allowedQueues) {
        this.allowedQueues = allowedQueues;
    }
    
    public long getAllowedTimeLimit() {
        return this.allowedTimeLimit;
    }
    
    public void setAllowedTimeLimit(final long allowedTimeLimit) {
        this.allowedTimeLimit = allowedTimeLimit;
    }
    
    @Override
    public String toString() {
        return String.format("isFeatureEnabled: %s | threadThreshold: %d | allowedModules: %s | timeLimit: %s", this.isFeatureEnabled, this.threadThreshold, this.allowedQueues, this.allowedTimeLimit);
    }
}
