package com.sun.corba.se.impl.monitoring;

import com.sun.corba.se.spi.monitoring.MonitoredAttributeInfo;
import com.sun.corba.se.spi.monitoring.MonitoredAttributeInfoFactory;

public class MonitoredAttributeInfoFactoryImpl implements MonitoredAttributeInfoFactory
{
    @Override
    public MonitoredAttributeInfo createMonitoredAttributeInfo(final String s, final Class clazz, final boolean b, final boolean b2) {
        return new MonitoredAttributeInfoImpl(s, clazz, b, b2);
    }
}
