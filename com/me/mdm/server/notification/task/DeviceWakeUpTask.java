package com.me.mdm.server.notification.task;

import java.util.Hashtable;
import com.me.mdm.server.notification.NotificationHandler;
import java.util.logging.Level;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class DeviceWakeUpTask implements SchedulerExecutionInterface
{
    private Logger logger;
    
    public DeviceWakeUpTask() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public void executeTask(final Properties properties) {
        final List<Long> resourceList = ((Hashtable<K, List<Long>>)properties).get("RESOURCE_LIST");
        this.logger.log(Level.INFO, "Wake Up devices List : {0}", resourceList);
        if (resourceList != null && !resourceList.isEmpty()) {
            final int platform = ((Hashtable<K, Integer>)properties).get("platform");
            final int notificationType = ((Hashtable<K, Integer>)properties).get("NotificationType");
            try {
                NotificationHandler.getInstance().SendNotification(resourceList, notificationType);
            }
            catch (final Exception e) {
                this.logger.log(Level.WARNING, "Exception occurred while DeviceWakeUpTask", e);
            }
        }
    }
}
