package com.adventnet.taskengine;

public interface Task
{
    public static final String DEFAULT_NORMAL_POOL = "default";
    public static final String ASYNCH_POOL = "asynchThreadPool";
    
    void executeTask(final TaskContext p0) throws TaskExecutionException;
    
    void stopTask() throws TaskExecutionException;
}
