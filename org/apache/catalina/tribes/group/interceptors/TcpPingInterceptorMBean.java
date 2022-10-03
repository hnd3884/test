package org.apache.catalina.tribes.group.interceptors;

public interface TcpPingInterceptorMBean
{
    int getOptionFlag();
    
    long getInterval();
    
    boolean getUseThread();
}
