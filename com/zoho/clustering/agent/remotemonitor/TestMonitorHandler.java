package com.zoho.clustering.agent.remotemonitor;

import java.util.logging.Level;
import java.util.logging.Logger;

public class TestMonitorHandler implements Monitor.Handler
{
    private static Logger logger;
    
    @Override
    public boolean handleTimeout(final String slaveId) {
        TestMonitorHandler.logger.log(Level.INFO, ">>>>>>>>Monitor.Handler.handleTimeout().Slave [{0}] is down", slaveId);
        return true;
    }
    
    static {
        TestMonitorHandler.logger = Logger.getLogger(TestMonitorHandler.class.getName());
    }
}
