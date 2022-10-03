package com.sun.corba.se.impl.monitoring;

import com.sun.corba.se.spi.monitoring.MonitoredObject;
import com.sun.corba.se.spi.monitoring.MonitoredObjectFactory;

public class MonitoredObjectFactoryImpl implements MonitoredObjectFactory
{
    @Override
    public MonitoredObject createMonitoredObject(final String s, final String s2) {
        return new MonitoredObjectImpl(s, s2);
    }
}
