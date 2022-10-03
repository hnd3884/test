package com.me.mdm.server.announcement.handler;

import java.util.Hashtable;
import java.util.Properties;
import com.me.uem.announcement.AnnouncementHandler;
import com.me.mdm.server.notification.NotificationHandler;
import java.util.List;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.config.task.CollectionCommandTaskData;
import java.util.logging.Logger;

public class AndroidAnnouncementCommandTaskHandler
{
    private final Logger logger;
    
    public AndroidAnnouncementCommandTaskHandler() {
        this.logger = Logger.getLogger("MDMAnnouncementLogger");
    }
    
    public void executeInstallCommandForDevice(final CollectionCommandTaskData collectionCommandTaskData) throws Exception {
        try {
            this.logger.log(Level.INFO, "In executeInstallCommandForDevice of AndroidAnnouncementCommandTaskHandler");
            final Properties taskprop = collectionCommandTaskData.getTaskProperties();
            final List androidNotificationList = collectionCommandTaskData.getResourceList();
            final JSONObject baseJsonObject = ((Hashtable<K, JSONObject>)taskprop).get("baseJsonObject");
            final JSONArray announcementJSONArray = (JSONArray)baseJsonObject.get("announcement_list");
            final List announcementdsList = JSONUtil.getInstance().convertLongJSONArrayTOList(announcementJSONArray);
            this.logger.log(Level.INFO, "Announcement List in AndroidAnnouncementCommandTaskHandler {0}", announcementdsList.toString());
            this.logger.log(Level.INFO, "Resource List in AndroidAnnouncementCommandTaskHandler {0}", androidNotificationList.toString());
            final Boolean markForDelete = (Boolean)baseJsonObject.get("marked_for_delete");
            if (!androidNotificationList.isEmpty()) {
                DeviceCommandRepository.getInstance().addAnnouncementSyncCommand(androidNotificationList, 1);
                NotificationHandler.getInstance().SendNotification(androidNotificationList, 2);
                if (!markForDelete) {
                    AnnouncementHandler.newInstance().addOrUpdateAnnouncementToResourceRelation(announcementdsList, androidNotificationList);
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in executeInstallCommandForDevice", ex);
        }
    }
}
