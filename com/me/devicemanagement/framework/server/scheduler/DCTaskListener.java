package com.me.devicemanagement.framework.server.scheduler;

public interface DCTaskListener
{
    void taskCompleted();
    
    boolean getTaskStopped();
    
    void setTaskStopped(final boolean p0);
}
