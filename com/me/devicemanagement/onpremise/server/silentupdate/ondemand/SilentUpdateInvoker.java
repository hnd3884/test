package com.me.devicemanagement.onpremise.server.silentupdate.ondemand;

import java.util.Hashtable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.io.File;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class SilentUpdateInvoker implements SchedulerExecutionInterface
{
    private static Logger logger;
    private static String sourceClass;
    
    public void executeTask(final Properties props) {
        try {
            SyMLogger.info(SilentUpdateInvoker.logger, SilentUpdateInvoker.sourceClass, "executeTask", SilentUpdateInvoker.sourceClass + " called...! : " + props);
            final SilentUpdateHandler silentUpdate = new SilentUpdateHandler();
            final String string = ((Hashtable<K, Object>)props).get("scheduleName").toString();
            switch (string) {
                case "SilentUpdateTaskScheduler": {
                    SyMLogger.info(SilentUpdateInvoker.logger, SilentUpdateInvoker.sourceClass, "executeTask", "Silent update sync creator task stats...!");
                    silentUpdate.syncTaskDetails();
                    SyMLogger.info(SilentUpdateInvoker.logger, SilentUpdateInvoker.sourceClass, "executeTask", "Silent update sync creator task ends...!");
                    SyMLogger.info(SilentUpdateInvoker.logger, SilentUpdateInvoker.sourceClass, "executeTask", "Start to download task dependency binarys..");
                    silentUpdate.downloadTaskDependencyBinarys();
                    SyMLogger.info(SilentUpdateInvoker.logger, SilentUpdateInvoker.sourceClass, "executeTask", "End to download task dependency binarys.");
                    SyMLogger.info(SilentUpdateInvoker.logger, SilentUpdateInvoker.sourceClass, "executeTask", "Start to process QPPMs.");
                    silentUpdate.processQPPMs();
                    SyMLogger.info(SilentUpdateInvoker.logger, SilentUpdateInvoker.sourceClass, "executeTask", "End to process QPPMs.");
                    this.createSchedulerFrequencyUpdateSchedule();
                    break;
                }
                default: {
                    SyMLogger.info(SilentUpdateInvoker.logger, SilentUpdateInvoker.sourceClass, "executeTask", "Un supported scheduler : " + ((Hashtable<K, Object>)props).get("scheduleName").toString());
                    break;
                }
            }
        }
        catch (final Exception e) {
            SyMLogger.error(SilentUpdateInvoker.logger, SilentUpdateInvoker.sourceClass, "executeTask", "Exception occurred : ", (Throwable)e);
        }
    }
    
    public void addOrUpdateSchedule() {
        final Integer newSchedulerFrequency = this.getNewSchedulerFrequencyFromFile();
        this.addOrUpdateSchedule((newSchedulerFrequency == null) ? 2L : ((long)newSchedulerFrequency));
    }
    
    public void addOrUpdateSchedule(final Long newSchedulerFrequency) {
        try {
            SyMLogger.info(SilentUpdateInvoker.logger, SilentUpdateInvoker.sourceClass, "addOrUpdateSchedule", "Silent update scheduler frequency update method called : " + newSchedulerFrequency);
            final Long schedulerLastUpdatedFrequency = ApiFactoryProvider.getSchedulerAPI().getPeriodicTimePeriod(ApiFactoryProvider.getSchedulerAPI().getTaskIDForSchedule("SilentUpdateTaskScheduler"));
            if (newSchedulerFrequency != null && (schedulerLastUpdatedFrequency == null || newSchedulerFrequency * 60L * 60L != schedulerLastUpdatedFrequency)) {
                SyMLogger.info(SilentUpdateInvoker.logger, SilentUpdateInvoker.sourceClass, "addOrUpdateSchedule", "Going to update silent update scheduler frequency as : " + newSchedulerFrequency);
                final HashMap<String, String> schedulerProps = new HashMap<String, String>();
                schedulerProps.put("workEngineId", "SilentUpdateTask");
                schedulerProps.put("operationType", String.valueOf(5001));
                schedulerProps.put("workflowName", "SilentUpdateTaskScheduler");
                schedulerProps.put("schedulerName", "SilentUpdateTaskScheduler");
                schedulerProps.put("taskName", "SilentUpdateTaskScheduler");
                schedulerProps.put("className", "com.me.devicemanagement.onpremise.server.silentupdate.ondemand.SilentUpdateInvoker");
                schedulerProps.put("description", "Silent update scheduled to execute every " + newSchedulerFrequency + " hours");
                schedulerProps.put("schType", "Hourly");
                schedulerProps.put("timePeriod", String.valueOf(newSchedulerFrequency));
                schedulerProps.put("unitOfTime", "hours");
                schedulerProps.put("skip_missed_schedule", "false");
                ApiFactoryProvider.getSchedulerAPI().createScheduler((HashMap)schedulerProps);
            }
            else {
                SyMLogger.info(SilentUpdateInvoker.logger, SilentUpdateInvoker.sourceClass, "addOrUpdateSchedule", "Silent update task scheduler already up-do-date.");
            }
        }
        catch (final Exception e) {
            SyMLogger.error(SilentUpdateInvoker.logger, SilentUpdateInvoker.sourceClass, "addOrUpdateSchedule", "Exception occurred : ", (Throwable)e);
        }
    }
    
    private Integer getNewSchedulerFrequencyFromFile() {
        try {
            final String metrackConfigPath = System.getProperty("server.home") + File.separator + "conf" + File.separator + "METracking" + File.separator + "metrack_config.properties";
            final Properties properties = FileAccessUtil.readProperties(metrackConfigPath);
            if (properties.containsKey("SilentUpdateScheduleFrequency")) {
                return Integer.parseInt(((Hashtable<K, Object>)properties).get("SilentUpdateScheduleFrequency").toString());
            }
        }
        catch (final Exception e) {
            SyMLogger.error(SilentUpdateInvoker.logger, SilentUpdateInvoker.sourceClass, "getNewSchedulerFrequencyFromFile", "Exception occurred : ", (Throwable)e);
        }
        return null;
    }
    
    private void createSchedulerFrequencyUpdateSchedule() {
        try {
            final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    SilentUpdateInvoker.this.addOrUpdateSchedule();
                }
            };
            scheduler.schedule(runnable, 5L, TimeUnit.MINUTES);
            SyMLogger.info(SilentUpdateInvoker.logger, SilentUpdateInvoker.sourceClass, "createSchedulerFrequencyUpdateSchedule", "Silent update next frequency time update scheduler configured. It will be start after 5 minutes.");
        }
        catch (final Exception e) {
            SyMLogger.error(SilentUpdateInvoker.logger, SilentUpdateInvoker.sourceClass, "createSchedulerFrequencyUpdateSchedule", "Exception occurred : ", (Throwable)e);
        }
    }
    
    static {
        SilentUpdateInvoker.logger = Logger.getLogger("SilentUpdate");
        SilentUpdateInvoker.sourceClass = "SilentUpdateInvoker";
    }
}
