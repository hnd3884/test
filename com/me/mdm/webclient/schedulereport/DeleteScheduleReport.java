package com.me.mdm.webclient.schedulereport;

import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class DeleteScheduleReport implements SchedulerExecutionInterface
{
    public static Logger logger;
    
    public void executeTask(final Properties taskProps) {
        try {
            MDMScheduleReportUtil.getInstance().deleteScheduleReport();
            DeleteScheduleReport.logger.log(Level.INFO, "Schedule Report :Entered into DeleteScheduleReport().executeTask");
        }
        catch (final Exception ex) {
            DeleteScheduleReport.logger.log(Level.WARNING, "Exception in delete file class", ex);
        }
    }
    
    static {
        DeleteScheduleReport.logger = Logger.getLogger("QueryExecutorLogger");
    }
}
