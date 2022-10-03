package com.me.mdm.server.notification.pushnotification;

import com.me.mdm.server.factory.MDMApiFactoryProvider;
import org.json.JSONObject;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import com.me.mdm.server.notification.MDMNotificationLimiter;
import com.me.devicemanagement.framework.server.queue.DCQueueDataProcessor;

public class AppNotificationQueueProcessor extends DCQueueDataProcessor
{
    private static final String APP_NOTIFICATION_KEY = "app_notification";
    private static MDMNotificationLimiter notificationInstance;
    
    public void processData(final DCQueueData qData) {
        final HashMap queueData = (HashMap)qData.queueData;
        final Long messageId = Long.valueOf(queueData.get("MESSAGE_ID"));
        final int notificationType = Integer.valueOf(queueData.get("NOTIFICATION_TYPE"));
        final DeviceAppNotificationHandler pushNotificationHandler = new DeviceAppNotificationHandler();
        final JSONObject messageObject = pushNotificationHandler.getMessageDetails(messageId);
        final AppNotificationProcessor pushNotificationProcessor = AppNotificationProcessor.getPushNotificationProcessor(notificationType);
        if (pushNotificationProcessor != null) {
            pushNotificationProcessor.buildMessage(messageObject);
            this.getNotificationInstance();
            for (HashMap notificationMap = pushNotificationHandler.getResourceForAppNotification(messageId, notificationType, this.getResourceBatchCount()); notificationMap.size() > 0; notificationMap = pushNotificationHandler.getResourceForAppNotification(messageId, notificationType, this.getResourceBatchCount())) {
                AppNotificationQueueProcessor.notificationInstance.checkAndLimitNotification("app_notification", notificationMap.size());
                pushNotificationHandler.updateNotificationStatus(new ArrayList(notificationMap.keySet()), messageId, 2);
                final HashMap resultMap = pushNotificationProcessor.pushMessageNotification(notificationMap);
                pushNotificationProcessor.processNotificationResult(resultMap, messageId);
            }
        }
    }
    
    public int getResourceBatchCount() {
        return AppNotificationQueueProcessor.notificationInstance.getNotificationBatchCount("app_notification");
    }
    
    private MDMNotificationLimiter getNotificationInstance() {
        if (AppNotificationQueueProcessor.notificationInstance == null) {
            AppNotificationQueueProcessor.notificationInstance = MDMApiFactoryProvider.getNotificationLimiter();
        }
        return AppNotificationQueueProcessor.notificationInstance;
    }
    
    static {
        AppNotificationQueueProcessor.notificationInstance = null;
    }
}
