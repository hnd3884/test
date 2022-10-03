package org.apache.catalina.tribes;

import javax.management.MBeanRegistration;

public interface JmxChannel extends MBeanRegistration
{
    boolean isJmxEnabled();
    
    void setJmxEnabled(final boolean p0);
    
    String getJmxDomain();
    
    void setJmxDomain(final String p0);
    
    String getJmxPrefix();
    
    void setJmxPrefix(final String p0);
}
