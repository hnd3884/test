package org.apache.catalina.tribes.group.interceptors;

public interface FragmentationInterceptorMBean
{
    int getMaxSize();
    
    long getExpire();
    
    void setMaxSize(final int p0);
    
    void setExpire(final long p0);
}
