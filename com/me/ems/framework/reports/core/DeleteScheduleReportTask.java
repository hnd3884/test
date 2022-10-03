package com.me.ems.framework.reports.core;

import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class DeleteScheduleReportTask implements SchedulerExecutionInterface
{
    private static Logger out;
    
    @Override
    public void executeTask(final Properties taskProps) {
        try {
            DeleteScheduleReportTask.out.log(Level.INFO, "Schedule Report :Entered into DeleteScheduleReport().executeTask");
            final DCScheduleReportUtil dcScheduleReportUtil = new DCScheduleReportUtil();
            dcScheduleReportUtil.deleteScheduleReport();
        }
        catch (final Exception e) {
            DeleteScheduleReportTask.out.log(Level.SEVERE, "Exception occurred while deleting the schedule report stored in /webapps/DesktopCentral/server-data/schedulereport/... ", e);
        }
    }
    
    static {
        DeleteScheduleReportTask.out = Logger.getLogger("QueryExecutorLogger");
    }
}
