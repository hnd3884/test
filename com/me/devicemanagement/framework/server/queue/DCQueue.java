package com.me.devicemanagement.framework.server.queue;

import java.io.Reader;

public interface DCQueue
{
    void start() throws Exception;
    
    void shutdownQueue() throws Exception;
    
    void addToQueue(final DCQueueData p0) throws Exception;
    
    void addToQueue(final DCQueueData p0, final String p1) throws Exception;
    
    void addToQueue(final DCQueueData p0, final Reader p1) throws Exception;
    
    void suspendQExecution() throws Exception;
    
    void resumeQExecution() throws Exception;
    
    boolean isQueueSuspended() throws Exception;
    
    int getQueueDataCount(final int p0);
    
    void processQueueData(final DCQueueDataProcessor p0) throws Exception;
    
    String getQueueFolderPath() throws Exception;
    
    default void respawnThread(final DCQueueDataProcessor processor, final Object threadProperties) {
    }
    
    default void monitorQueue(final DCQueueDataProcessor processor) {
    }
    
    default boolean isQueueEligibleForMonitor() {
        return false;
    }
}
