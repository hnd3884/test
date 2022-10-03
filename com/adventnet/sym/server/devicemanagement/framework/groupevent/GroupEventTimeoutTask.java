package com.adventnet.sym.server.devicemanagement.framework.groupevent;

import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class GroupEventTimeoutTask implements SchedulerExecutionInterface
{
    private static final Logger LOGGER;
    
    public void executeTask(final Properties props) {
        try {
            final long eventId = Long.parseLong(props.getProperty("eventId"));
            final String action = props.getProperty("action");
            GroupEventTimeoutTask.LOGGER.log(Level.INFO, "Executing timeout task with eventId:" + eventId + ",action:" + action + ",currentmillis:" + System.currentTimeMillis());
            try {
                GroupEventNotifier.getInstance().onEventTimeoutTask(eventId, action);
            }
            catch (final Exception e) {
                GroupEventNotifier.setGroupEventNotifierStatus(action, "failed");
                GroupEventTimeoutTask.LOGGER.log(Level.SEVERE, "Exception when executing timeout task : ", e);
            }
        }
        catch (final Exception e2) {
            GroupEventTimeoutTask.LOGGER.log(Level.SEVERE, "Exception : ", e2);
        }
    }
    
    static {
        LOGGER = Logger.getLogger("GroupEventTimeoutTask");
    }
}
