package com.sun.jmx.mbeanserver;

import javax.management.MBeanServer;
import javax.management.MBeanServerDelegate;
import javax.management.MBeanServerBuilder;

public class JmxMBeanServerBuilder extends MBeanServerBuilder
{
    @Override
    public MBeanServerDelegate newMBeanServerDelegate() {
        return JmxMBeanServer.newMBeanServerDelegate();
    }
    
    @Override
    public MBeanServer newMBeanServer(final String s, final MBeanServer mBeanServer, final MBeanServerDelegate mBeanServerDelegate) {
        return JmxMBeanServer.newMBeanServer(s, mBeanServer, mBeanServerDelegate, true);
    }
}
