package com.me.mdm.server.announcement.handler;

import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import com.me.mdm.server.notification.pushnotification.DeviceAppNotificationHandler;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import java.util.HashMap;
import com.me.uem.announcement.AnnouncementHandler;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.config.task.CollectionCommandTaskData;
import java.util.logging.Logger;

public class IOSAnnouncementCommandTaskHandler
{
    private final Logger logger;
    
    public IOSAnnouncementCommandTaskHandler() {
        this.logger = Logger.getLogger("MDMAnnouncementLogger");
    }
    
    public void executeInstallCommandForDevice(final CollectionCommandTaskData collectionCommandTaskData) throws Exception {
        try {
            this.logger.log(Level.INFO, "In executeInstallCommandForDevice of IOSAnnouncementCommandTaskHandler");
            final Properties taskprop = collectionCommandTaskData.getTaskProperties();
            final List iosNotificationList = collectionCommandTaskData.getResourceList();
            final JSONObject baseJsonObject = ((Hashtable<K, JSONObject>)taskprop).get("baseJsonObject");
            final boolean isNotify = baseJsonObject.optBoolean("notify_user", true);
            final JSONArray announcementList = (JSONArray)baseJsonObject.get("announcement_list");
            this.logger.log(Level.INFO, "Announcement List in IOSAnnouncementCommandTaskHandler {0}", announcementList.toString());
            this.logger.log(Level.INFO, "Resource List in IOSAnnouncementCommandTaskHandler {0}", iosNotificationList.toString());
            final List announcementdsArrayList = JSONUtil.getInstance().convertLongJSONArrayTOList(announcementList);
            AnnouncementHandler.newInstance().addOrUpdateAnnouncementToResourceRelation(announcementdsArrayList, iosNotificationList);
            for (int j = 0; j < announcementList.length(); ++j) {
                final Long announcementID = Long.parseLong(announcementList.get(j).toString());
                if (!iosNotificationList.isEmpty() && isNotify) {
                    final JSONObject messageJson = AnnouncementHandler.newInstance().getAnnouncementAsPushMessage(announcementID);
                    messageJson.put("CUSTOMER_ID", baseJsonObject.get("customer_id"));
                    final JSONObject customPayload = new JSONObject();
                    customPayload.put("announcement_id", (Object)announcementID);
                    String nBarIcon = messageJson.getString("nbar_icon");
                    final HashMap hm = new HashMap();
                    hm.put("path", nBarIcon);
                    hm.put("IS_SERVER", false);
                    hm.put("IS_AUTHTOKEN", false);
                    nBarIcon = MDMEnrollmentUtil.getInstance().getServerBaseURL() + ApiFactoryProvider.getFileAccessAPI().constructFileURL(hm);
                    customPayload.put("nbar_icon", (Object)nBarIcon);
                    customPayload.put("nbar_icon_id", messageJson.getLong("nbar_icon_id"));
                    final List<Long> tokenMissingList = DeviceAppNotificationHandler.getInstance().addPushNotification(iosNotificationList, 101, messageJson, customPayload);
                    final JSONArray collection_list = (JSONArray)baseJsonObject.get("collection_list");
                    AnnouncementHandler.newInstance().addRemarksForiOSNotApplicableList(tokenMissingList, collection_list);
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in executeInstallCommandForDevice", ex);
        }
    }
}
