package com.sun.corba.se.impl.monitoring;

import com.sun.corba.se.spi.monitoring.MonitoringFactories;
import com.sun.corba.se.spi.monitoring.MonitoredObject;
import com.sun.corba.se.spi.monitoring.MonitoringManager;

public class MonitoringManagerImpl implements MonitoringManager
{
    private final MonitoredObject rootMonitoredObject;
    
    MonitoringManagerImpl(final String s, final String s2) {
        this.rootMonitoredObject = MonitoringFactories.getMonitoredObjectFactory().createMonitoredObject(s, s2);
    }
    
    @Override
    public void clearState() {
        this.rootMonitoredObject.clearState();
    }
    
    @Override
    public MonitoredObject getRootMonitoredObject() {
        return this.rootMonitoredObject;
    }
    
    @Override
    public void close() {
        MonitoringFactories.getMonitoringManagerFactory().remove(this.rootMonitoredObject.getName());
    }
}
