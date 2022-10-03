package com.adventnet.taskengine;

public interface ScheduleRetryHandler
{
    long getNextScheduleTime(final TaskContext p0, final long p1);
}
