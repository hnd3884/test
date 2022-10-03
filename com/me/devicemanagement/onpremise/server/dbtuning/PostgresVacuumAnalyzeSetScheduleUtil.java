package com.me.devicemanagement.onpremise.server.dbtuning;

import java.util.HashMap;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PostgresVacuumAnalyzeSetScheduleUtil
{
    static Logger logger;
    private static PostgresVacuumAnalyzeSetScheduleUtil handler;
    
    public static PostgresVacuumAnalyzeSetScheduleUtil getInstance() {
        if (PostgresVacuumAnalyzeSetScheduleUtil.handler == null) {
            try {
                PostgresVacuumAnalyzeSetScheduleUtil.handler = new PostgresVacuumAnalyzeSetScheduleUtil();
            }
            catch (final Exception e) {
                PostgresVacuumAnalyzeSetScheduleUtil.logger.log(Level.SEVERE, "Exception while creating PostgresVacuumAnalyzeSetScheduleUtil obj", e);
            }
        }
        return PostgresVacuumAnalyzeSetScheduleUtil.handler;
    }
    
    public void PostgresVacuumAnalyzeSetSchedule(final int repeatFrequency) {
        PostgresVacuumAnalyzeSetScheduleUtil.logger.log(Level.INFO, "Inside PostgresVacuumAnalyzeSetSchedule");
        if (repeatFrequency == 0) {
            ApiFactoryProvider.getSchedulerAPI().removeScheduler("PostgresVacuumAnalyzeTaskScheduler");
        }
        else {
            final HashMap<String, String> schedulerProps = new HashMap<String, String>();
            schedulerProps.put("workEngineId", "PostgresVacuumAnalyze");
            schedulerProps.put("operationType", Integer.toString(5003));
            schedulerProps.put("workflowName", "PostgresVacuumAnalyzeTask");
            schedulerProps.put("schedulerName", "PostgresVacuumAnalyzeTaskScheduler");
            schedulerProps.put("taskName", "PostgresVacuumAnalyzeTask");
            schedulerProps.put("className", "com.me.devicemanagement.onpremise.server.dbtuning.PostgresVacuumAnalyzeTask");
            schedulerProps.put("description", "Postgres Clean Up Modified");
            schedulerProps.put("schType", "Hourly");
            schedulerProps.put("timePeriod", Integer.toString(repeatFrequency));
            schedulerProps.put("unitOfTime", "Hours");
            schedulerProps.put("skip_missed_schedule", "true");
            ApiFactoryProvider.getSchedulerAPI().createScheduler((HashMap)schedulerProps);
        }
        PostgresVacuumAnalyzeSetScheduleUtil.logger.log(Level.INFO, "PostgresVacuumAnalyze schedule created : " + ApiFactoryProvider.getSchedulerAPI().isScheduleCreated("PostgresVacuumAnalyzeTaskScheduler"));
    }
    
    static {
        PostgresVacuumAnalyzeSetScheduleUtil.logger = Logger.getLogger("DatabaseMaintenanceLogger");
        PostgresVacuumAnalyzeSetScheduleUtil.handler = null;
    }
}
