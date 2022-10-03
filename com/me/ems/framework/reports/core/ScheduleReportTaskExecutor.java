package com.me.ems.framework.reports.core;

import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;

public class ScheduleReportTaskExecutor
{
    private static Logger out;
    
    public static void executeTaskNow(final Properties hashClients) throws SyMException {
        try {
            ScheduleReportTaskExecutor.out.log(Level.INFO, "Schedule Report :Entered into executeTaskNow.ScheduleReportTaskExecutor()");
            if (hashClients != null) {
                final HashMap taskInfoMap = new HashMap();
                taskInfoMap.put("taskName", "DCScheduleReport");
                ApiFactoryProvider.getSchedulerAPI().executeAsynchronously("com.me.ems.framework.reports.core.ScheduleReportTask", taskInfoMap, hashClients, "asynchThreadPool");
            }
        }
        catch (final Exception ex) {
            throw new SyMException(1001, ex);
        }
    }
    
    static {
        ScheduleReportTaskExecutor.out = Logger.getLogger("QueryExecutorLogger");
    }
}
