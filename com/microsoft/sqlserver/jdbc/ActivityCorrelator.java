package com.microsoft.sqlserver.jdbc;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

final class ActivityCorrelator
{
    private static Map<Long, ActivityId> activityIdTlsMap;
    
    static void cleanupActivityId() {
        ActivityCorrelator.activityIdTlsMap.entrySet().removeIf(e -> null == e.getValue() || null == e.getValue().getThread() || e.getValue().getThread() == Thread.currentThread() || !e.getValue().getThread().isAlive());
    }
    
    static ActivityId getCurrent() {
        final Thread thread = Thread.currentThread();
        if (!ActivityCorrelator.activityIdTlsMap.containsKey(thread.getId())) {
            ActivityCorrelator.activityIdTlsMap.put(thread.getId(), new ActivityId(thread));
        }
        return ActivityCorrelator.activityIdTlsMap.get(thread.getId());
    }
    
    static ActivityId getNext() {
        final ActivityId activityId = getCurrent();
        activityId.increment();
        return activityId;
    }
    
    static void setCurrentActivityIdSentFlag() {
        final ActivityId activityId = getCurrent();
        activityId.setSentFlag();
    }
    
    static Map<Long, ActivityId> getActivityIdTlsMap() {
        return ActivityCorrelator.activityIdTlsMap;
    }
    
    private ActivityCorrelator() {
    }
    
    static {
        ActivityCorrelator.activityIdTlsMap = new ConcurrentHashMap<Long, ActivityId>();
    }
}
