package com.me.mdm.webclient.schedulereport;

import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;

public class ScheduleReportTaskExecutor
{
    private static Logger logger;
    
    public static void executeTaskNow(final Properties hashClients) throws SyMException {
        try {
            ScheduleReportTaskExecutor.logger.log(Level.INFO, "Schedule Report :Entered into executeTaskNow.ScheduleReportTaskExecutor()");
            if (hashClients != null) {
                final HashMap taskInfoMap = new HashMap();
                taskInfoMap.put("taskName", "MDMScheduleReport");
                ApiFactoryProvider.getSchedulerAPI().executeAsynchronously("com.me.mdm.webclient.schedulereport.MDMScheduleReportUtil", taskInfoMap, hashClients);
            }
        }
        catch (final Exception ex) {
            throw new SyMException(1001, (Throwable)ex);
        }
    }
    
    static {
        ScheduleReportTaskExecutor.logger = Logger.getLogger("QueryExecutorLogger");
    }
}
