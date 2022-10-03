package com.sun.corba.se.impl.monitoring;

import com.sun.corba.se.spi.monitoring.MonitoringManager;
import java.util.HashMap;
import com.sun.corba.se.spi.monitoring.MonitoringManagerFactory;

public class MonitoringManagerFactoryImpl implements MonitoringManagerFactory
{
    private HashMap monitoringManagerTable;
    
    public MonitoringManagerFactoryImpl() {
        this.monitoringManagerTable = new HashMap();
    }
    
    @Override
    public synchronized MonitoringManager createMonitoringManager(final String s, final String s2) {
        MonitoringManagerImpl monitoringManagerImpl = this.monitoringManagerTable.get(s);
        if (monitoringManagerImpl == null) {
            monitoringManagerImpl = new MonitoringManagerImpl(s, s2);
            this.monitoringManagerTable.put(s, monitoringManagerImpl);
        }
        return monitoringManagerImpl;
    }
    
    @Override
    public synchronized void remove(final String s) {
        this.monitoringManagerTable.remove(s);
    }
}
