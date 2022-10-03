package com.me.mdm.server.apps.scheduledapptask;

import java.util.Hashtable;
import com.me.mdm.server.constants.ScheduleRepoConstants;
import com.me.mdm.api.command.schedule.ScheduleRepositoryHandler;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class SchedulerNextExecutionTimeUpdateHandler implements SchedulerExecutionInterface
{
    Logger logger;
    
    public SchedulerNextExecutionTimeUpdateHandler() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    public void executeTask(final Properties props) {
        final String scheduleName = String.valueOf(((Hashtable<K, Object>)props).get("appScheduleName"));
        final Long nextExecutionTime = ApiFactoryProvider.getSchedulerAPI().getNextExecutionTimeForSchedule(scheduleName);
        this.logger.log(Level.INFO, "Scheduler Next Execution Time is {0} for schedule {1}", new Object[] { nextExecutionTime, scheduleName });
        ScheduleRepositoryHandler.getInstance().addSchedule(scheduleName, nextExecutionTime, ScheduleRepoConstants.APP_UPDATE_MODULE, 1);
    }
}
