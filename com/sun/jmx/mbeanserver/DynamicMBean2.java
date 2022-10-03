package com.sun.jmx.mbeanserver;

import javax.management.ObjectName;
import javax.management.MBeanServer;
import javax.management.DynamicMBean;

public interface DynamicMBean2 extends DynamicMBean
{
    Object getResource();
    
    String getClassName();
    
    void preRegister2(final MBeanServer p0, final ObjectName p1) throws Exception;
    
    void registerFailed();
}
