package com.me.ems.framework.reports.core;

import java.util.Hashtable;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class ScheduleReportTask implements SchedulerExecutionInterface
{
    private static Logger out;
    
    @Override
    public void executeTask(final Properties taskProps) {
        try {
            Long taskID = null;
            taskID = ((Hashtable<K, Long>)taskProps).get("TASK_ID");
            if (taskID == null) {
                final Long schedulerClassID = Long.valueOf(((Hashtable<K, String>)taskProps).get("schedulerClassID"));
                taskID = ApiFactoryProvider.getSchedulerAPI().getTaskIDForSchedule(schedulerClassID);
            }
            if (ApiFactoryProvider.getMailSettingAPI().isMailServerConfigured()) {
                DCScheduleReportUtil.sendReportMail(taskID);
            }
            else {
                ScheduleReportTask.out.log(Level.WARNING, "Quiting ScheduleReport Execution of {0}..Mail Server Not enabled.", new Object[] { taskID });
            }
        }
        catch (final Exception e) {
            ScheduleReportTask.out.log(Level.WARNING, "Exception while bacup db operation ", e);
        }
    }
    
    static {
        ScheduleReportTask.out = Logger.getLogger("ScheduleReportLogger");
    }
}
