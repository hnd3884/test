package com.adventnet.taskengine.internal;

import com.adventnet.taskengine.TaskContext;

public interface MonitoringScheduler
{
    void initializeInstrumentation(final TaskContext p0);
    
    void finishInstrumentation(final int p0, final Throwable p1);
}
