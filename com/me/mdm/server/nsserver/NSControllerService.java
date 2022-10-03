package com.me.mdm.server.nsserver;

import com.me.devicemanagement.onpremise.start.util.NSStartUpUtil;
import com.me.mdm.server.nsserver.task.NSMonitorTask;
import com.me.devicemanagement.onpremise.server.factory.ApiFactoryProvider;
import java.util.logging.Level;
import java.util.Properties;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;
import com.adventnet.mfw.service.Service;

public class NSControllerService implements Service
{
    private Logger logger;
    private String sourceClass;
    
    public NSControllerService() {
        this.logger = Logger.getLogger("NSControllerLogger");
        this.sourceClass = "NSControllerService";
    }
    
    public void create(final DataObject arg0) throws Exception {
        final String sourceMethod = "create";
        SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "\n\n\n");
        SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "____________________________________");
        SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "Creating NSServerControllerService Service...");
        SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "____________________________________");
    }
    
    public void start() throws Exception {
        try {
            final String sourceMethod = "start";
            SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "Starting NSServerControllerService Service...");
            if (NSUtil.getInstance().isMDMPServerImproperShutdown()) {
                NSUtil.getInstance().resetNSStatus();
            }
            NSUtil.getInstance().updateNSCompEnableStatusInCache();
            NSUtil.getInstance().updateNSEnableStatusInCache();
            final boolean nsComponentStatus = NSUtil.getInstance().isNSComponentEnabled();
            final boolean nsEnabledStatus = NSUtil.getInstance().getNSEnabledStatusFromDB();
            SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "NS Compoment Status : " + nsComponentStatus);
            SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "NS Enabled Status : " + nsEnabledStatus);
            if (nsComponentStatus && nsEnabledStatus) {
                final HashMap taskInfoMap = new HashMap();
                taskInfoMap.put("taskName", "NSStartupTask");
                final String className = "com.me.mdm.server.nsserver.task.NSStartupTask";
                taskInfoMap.put("schedulerTime", System.currentTimeMillis());
                final Properties prop = new Properties();
                this.logger.log(Level.INFO, "Task info which is passed to Scheduler.executeAsynchronously(): {0}", taskInfoMap);
                ApiFactoryProvider.getSchedulerAPI().executeAsynchronously(className, taskInfoMap, new Properties());
            }
            else {
                if (nsEnabledStatus) {
                    NSUtil.getInstance().disableNS();
                }
                NSUtil.getInstance().updateNSEnableStatusInCache();
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception occurred while starting the NSServer", exp);
        }
    }
    
    public void stop() throws Exception {
        final String sourceMethod = "stop";
        SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "Stopping Notification Server Service...");
        final NSMonitorTask monitorTask = NSMonitorTask.getInstance();
        if (monitorTask != null && monitorTask.getMonitoringState()) {
            monitorTask.suspendTask();
        }
        final int stopResult = NSStartUpUtil.stopNSServer();
        SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "Result of Stop NS Service ( Error code ): " + stopResult);
    }
    
    public void destroy() throws Exception {
    }
}
