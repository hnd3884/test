package com.me.ems.onpremise.summaryserver.probe.sync.common;

import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class SyncDataProcessor implements SchedulerExecutionInterface
{
    private static Logger logger;
    
    public void executeTask(final Properties props) {
        final int moduleId = -1;
        final long startTime = System.currentTimeMillis();
        SyncDataProcessor.logger.log(Level.INFO, "Data synchronization task has been started");
        SyncHandler.callDataSync(1, moduleId);
        final long endTime = System.currentTimeMillis();
        SyncDataProcessor.logger.log(Level.INFO, "Total Time Taken to complete the Data synchronization task: " + (endTime - startTime));
    }
    
    static {
        SyncDataProcessor.logger = Logger.getLogger("ProbeSyncLogger");
    }
}
