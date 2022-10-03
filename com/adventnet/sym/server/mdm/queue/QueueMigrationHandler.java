package com.adventnet.sym.server.mdm.queue;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.HashMap;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class QueueMigrationHandler implements SchedulerExecutionInterface
{
    Logger mdmLogger;
    private String migrateQueue;
    private String pollForOldQueue;
    private Long twoMinutes;
    
    public QueueMigrationHandler() {
        this.mdmLogger = Logger.getLogger("MDMLogger");
        this.migrateQueue = "MigrateQueue";
        this.pollForOldQueue = "PollQueueToResume";
        this.twoMinutes = 120000L;
    }
    
    public void executeTask(final Properties props) {
        String taskName = this.migrateQueue;
        taskName = props.getProperty("taskName");
        if (taskName.equalsIgnoreCase(this.migrateQueue)) {
            this.mdmLogger.log(Level.INFO, " QueueMigrationHandler : Executing async task of queue migration");
            this.pauseNewQueueConsumers();
        }
        else if (taskName.equalsIgnoreCase(this.pollForOldQueue)) {
            this.mdmLogger.log(Level.INFO, "QueueMigrationHandler : Polling started for watching mdm-data");
            this.checkMdmDataQueueStatus();
        }
    }
    
    public void pauseNewQueueConsumers() {
        final boolean shouldPauseQueue = MigrationHandlerUtil.getInstance().isDataPresentInQueue("mdm-data", 3);
        final boolean isQueueSplitGloballyEnabled = MDMUtil.getInstance().isMDMQueueSplitAvailableGlobally();
        final boolean isQueueSplitFeatureEnabled = MDMFeatureParamsHandler.getInstance().isFeatureEnabled("MdmDataQueueSplit");
        if (isQueueSplitFeatureEnabled && isQueueSplitGloballyEnabled) {
            if (shouldPauseQueue) {
                this.mdmLogger.log(Level.INFO, "QueueMigrationHandler : Queue data found in MDM-Data, performing migration");
                MigrationHandlerUtil.getInstance().pauseNewQueues();
                this.startQueuePolling(this.twoMinutes);
            }
            else {
                this.mdmLogger.log(Level.INFO, "QueueMigrationHandler : Queue is already migrated, no need to perform additional handling");
            }
        }
        else {
            this.mdmLogger.log(Level.INFO, "Queue split feature is not switched on for this customer");
        }
    }
    
    private void checkMdmDataQueueStatus() {
        final boolean dataPresentInQueue = MigrationHandlerUtil.getInstance().isDataPresentInQueue("mdm-data", 3);
        if (dataPresentInQueue) {
            this.startQueuePolling(this.twoMinutes);
        }
        else {
            this.mdmLogger.log(Level.INFO, "QueueMigrationHandler : The queue is empty so resuming all queues");
            MigrationHandlerUtil.getInstance().resumeNewQueues();
        }
    }
    
    private void startQueuePolling(final Long offset) {
        final HashMap taskInfoMap = new HashMap();
        final Properties myProps = new Properties();
        taskInfoMap.put("taskName", this.pollForOldQueue);
        ((Hashtable<String, String>)myProps).put("taskName", this.pollForOldQueue);
        taskInfoMap.put("schedulerTime", System.currentTimeMillis() + offset);
        ((Hashtable<String, String>)myProps).put("schedulerTime", String.valueOf(System.currentTimeMillis() + offset));
        taskInfoMap.put("poolName", "asynchThreadPool");
        ((Hashtable<String, String>)myProps).put("poolName", "asynchThreadPool");
        final int timesRun = Integer.valueOf(myProps.getProperty("timesCalled", "0"));
        ((Hashtable<String, String>)myProps).put("timesCalled", String.valueOf(timesRun + 1));
        try {
            this.mdmLogger.log(Level.INFO, "Running the polling task for {0} time", timesRun);
            ApiFactoryProvider.getSchedulerAPI().executeAsynchronously("com.adventnet.sym.server.mdm.queue.QueueMigrationHandler", taskInfoMap, myProps);
        }
        catch (final Exception exp) {
            this.mdmLogger.log(Level.WARNING, "Exception occurred when calling queue migration scheduler: {0}", exp);
        }
    }
}
