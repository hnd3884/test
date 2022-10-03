package org.apache.catalina.tribes.group.interceptors;

public interface DomainFilterInterceptorMBean
{
    int getOptionFlag();
    
    byte[] getDomain();
    
    int getLogInterval();
    
    void setLogInterval(final int p0);
}
