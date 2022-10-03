package com.me.mdm.api.command.schedule;

import java.util.Hashtable;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class ScheduleCommandLoadCommandsToResourceTask implements SchedulerExecutionInterface
{
    public static Logger logger;
    
    public void executeTask(final Properties properties) {
        final String preScheduleName = ((Hashtable<K, String>)properties).get("scheduleName");
        ScheduleCommandLoadCommandsToResourceTask.logger.log(Level.INFO, "Executing PreSchedule task for schedule:{0} and properties{1}", new Object[] { preScheduleName, properties.toString() });
        try {
            final Long preScheduleID = ScheduleRepositoryHandler.getInstance().getScheduleID(preScheduleName);
            final Long scheduleId = ScheduleMapperHandler.getInstance().getExecutionScheduleId(preScheduleID);
            ScheduledActionsUtils.addCommmandsToDeviceForSchedule(scheduleId);
        }
        catch (final Exception e) {
            ScheduleCommandLoadCommandsToResourceTask.logger.log(Level.SEVERE, "Failed to load data into mdCommandsToDevice from ScheduleCommandLoadCommandsToResourceTask", e);
        }
    }
    
    static {
        ScheduleCommandLoadCommandsToResourceTask.logger = Logger.getLogger("ActionsLogger");
    }
}
