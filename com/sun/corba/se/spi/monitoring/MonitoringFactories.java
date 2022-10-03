package com.sun.corba.se.spi.monitoring;

import com.sun.corba.se.impl.monitoring.MonitoringManagerFactoryImpl;
import com.sun.corba.se.impl.monitoring.MonitoredAttributeInfoFactoryImpl;
import com.sun.corba.se.impl.monitoring.MonitoredObjectFactoryImpl;

public class MonitoringFactories
{
    private static final MonitoredObjectFactoryImpl monitoredObjectFactory;
    private static final MonitoredAttributeInfoFactoryImpl monitoredAttributeInfoFactory;
    private static final MonitoringManagerFactoryImpl monitoringManagerFactory;
    
    public static MonitoredObjectFactory getMonitoredObjectFactory() {
        return MonitoringFactories.monitoredObjectFactory;
    }
    
    public static MonitoredAttributeInfoFactory getMonitoredAttributeInfoFactory() {
        return MonitoringFactories.monitoredAttributeInfoFactory;
    }
    
    public static MonitoringManagerFactory getMonitoringManagerFactory() {
        return MonitoringFactories.monitoringManagerFactory;
    }
    
    static {
        monitoredObjectFactory = new MonitoredObjectFactoryImpl();
        monitoredAttributeInfoFactory = new MonitoredAttributeInfoFactoryImpl();
        monitoringManagerFactory = new MonitoringManagerFactoryImpl();
    }
}
