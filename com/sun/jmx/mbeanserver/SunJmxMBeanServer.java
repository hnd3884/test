package com.sun.jmx.mbeanserver;

import javax.management.MBeanServerDelegate;
import javax.management.MBeanServer;

public interface SunJmxMBeanServer extends MBeanServer
{
    MBeanInstantiator getMBeanInstantiator();
    
    boolean interceptorsEnabled();
    
    MBeanServer getMBeanServerInterceptor();
    
    void setMBeanServerInterceptor(final MBeanServer p0);
    
    MBeanServerDelegate getMBeanServerDelegate();
}
