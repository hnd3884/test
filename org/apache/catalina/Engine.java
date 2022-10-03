package org.apache.catalina;

public interface Engine extends Container
{
    String getDefaultHost();
    
    void setDefaultHost(final String p0);
    
    String getJvmRoute();
    
    void setJvmRoute(final String p0);
    
    Service getService();
    
    void setService(final Service p0);
}
