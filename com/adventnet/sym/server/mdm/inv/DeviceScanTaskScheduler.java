package com.adventnet.sym.server.mdm.inv;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.task.DeviceMgmtTaskUtil;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.adventnet.sym.server.mdm.command.DeviceInvCommandHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.devicemanagement.framework.server.csv.CustomerParamsHandler;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class DeviceScanTaskScheduler implements SchedulerExecutionInterface
{
    private Logger logger;
    private String sourceClass;
    
    public DeviceScanTaskScheduler() {
        this.logger = Logger.getLogger("MDMLogger");
        this.sourceClass = "DeviceScanTaskScheduler";
    }
    
    public void executeTask(final Properties taskProps) {
        final String sourceMethod = "executeTask";
        try {
            final String scheduleName = ((Hashtable<K, String>)taskProps).get("scheduleName");
            final Long customerID = ApiFactoryProvider.getSchedulerAPI().getCustomerID(scheduleName);
            SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "********************Device Scan Scheduled Task Starts********************");
            final Long startTime = new Long(System.currentTimeMillis() / 1000L);
            SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "executeTask() invoked at " + startTime);
            final String userIdSystemParam = CustomerParamsHandler.getInstance().getParameterValue("SCHEDULED_SCAN_USER_ID", (long)customerID);
            Long userId;
            if (MDMUtil.isStringEmpty(userIdSystemParam)) {
                userId = MDMUtil.getAdminUserId();
            }
            else {
                userId = Long.valueOf(userIdSystemParam);
            }
            DeviceInvCommandHandler.getInstance().scanAllDevices(customerID, userId);
            MDMEventLogHandler.getInstance().MDMEventLogEntry(2041, null, DMUserHandler.getUserNameFromUserID(userId), "mdm.actionlog.inv.scheduled_scan_initiated", null, customerID);
            final Long taskID = ApiFactoryProvider.getSchedulerAPI().getTaskIDForSchedule(scheduleName);
            final Properties updateTaskProps = new Properties();
            final Long completionTime = new Long(System.currentTimeMillis());
            ((Hashtable<String, Long>)updateTaskProps).put("COMPLETIONTIME", completionTime);
            ((Hashtable<String, String>)updateTaskProps).put("STATUS", "COMPLETED");
            ((Hashtable<String, String>)updateTaskProps).put("REMARKS", "dc.db.mdm.scanStaus.Device_scan_completed_successfully");
            DeviceMgmtTaskUtil.getInstance().updateTaskDetails(taskID, updateTaskProps);
            SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "********************Device Scan Scheduled Task Ends********************");
            SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "Device Scan Task Scheduled.");
        }
        catch (final Exception ex) {
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Caught exception  while executing task: DeviceScanTaskScheduler", (Throwable)ex);
        }
        SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "Completed execution of task DeviceScanTaskScheduler.");
    }
}
