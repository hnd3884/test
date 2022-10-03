package org.apache.catalina.tribes.group.interceptors;

public interface MessageDispatchInterceptorMBean
{
    int getOptionFlag();
    
    boolean isAlwaysSend();
    
    void setAlwaysSend(final boolean p0);
    
    long getMaxQueueSize();
    
    long getCurrentSize();
    
    long getKeepAliveTime();
    
    int getMaxSpareThreads();
    
    int getMaxThreads();
    
    int getPoolSize();
    
    int getActiveCount();
    
    long getTaskCount();
    
    long getCompletedTaskCount();
}
