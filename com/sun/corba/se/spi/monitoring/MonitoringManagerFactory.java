package com.sun.corba.se.spi.monitoring;

public interface MonitoringManagerFactory
{
    MonitoringManager createMonitoringManager(final String p0, final String p1);
    
    void remove(final String p0);
}
