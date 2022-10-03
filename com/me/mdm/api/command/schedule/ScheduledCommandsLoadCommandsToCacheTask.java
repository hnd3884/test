package com.me.mdm.api.command.schedule;

import java.util.Hashtable;
import java.util.Iterator;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import java.util.Map;
import com.me.mdm.server.notification.NotificationHandler;
import java.util.List;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class ScheduledCommandsLoadCommandsToCacheTask implements SchedulerExecutionInterface
{
    protected static Logger logger;
    
    public void executeTask(final Properties properties) {
        try {
            final String scheduleName = ((Hashtable<K, String>)properties).get("scheduleName");
            ScheduledCommandsLoadCommandsToCacheTask.logger.log(Level.INFO, "Sending notification for scheduleID{0}", scheduleName);
            final Long scheduleId = ScheduleRepositoryHandler.getInstance().getScheduleID(scheduleName);
            final List collections = ScheduledCommandToCollectionHandler.getInstance().getCollectionsForSchedule(scheduleId);
            List resourceIds = ScheduledCollectionToResourceHandler.getInstance().getResourcesForCollections(collections);
            resourceIds = MDMGroupHandler.getInstance().getDeviceListFromResourceList(resourceIds);
            final List androidExcludedDevices = ScheduledActionsUtils.excludeAndroidDevicesForResourceList(resourceIds);
            final List statusList = new ArrayList();
            statusList.add(7);
            statusList.add(2);
            statusList.add(0);
            final String remarksText = "mdm.bulkactions.auditlog.scheduled_initated_group";
            GroupActionScheduleUtils.updateCommandHistoryStatus(collections, androidExcludedDevices, resourceIds, 1, "dc.mdm.general.command.initiated");
            NotificationHandler.getInstance().SendNotification(resourceIds);
            final Map collectionMap = GroupActionScheduleUtils.getGroupIdsForCollectionIds(collections);
            for (final Long collectionID : collections) {
                final Map items = collectionMap.get(collectionID);
                final Long customerID = items.get("CUSTOMER_ID");
                final String groupName = items.get("NAME");
                final String commandName = items.get("COMMAND_ID");
                final String remarksArgs = commandName + "@@@" + groupName;
                final List remarksArgsList = new ArrayList();
                remarksArgsList.add(remarksArgs);
                MDMEventLogHandler.getInstance().addEvent(2051, null, remarksText, remarksArgsList, customerID, new Long(System.currentTimeMillis()));
            }
        }
        catch (final Exception e) {
            ScheduledCommandsLoadCommandsToCacheTask.logger.log(Level.WARNING, "Sending notification to resources has failed");
            ScheduledCommandsLoadCommandsToCacheTask.logger.log(Level.WARNING, "Exception occurred while ScheduledCommandsLoadCommandsToCacheTask.executeTask()", e);
        }
    }
    
    static {
        ScheduledCommandsLoadCommandsToCacheTask.logger = Logger.getLogger("ActionsLogger");
    }
}
