package com.me.mdm.server.notification.pushnotification;

import java.util.logging.Level;
import java.util.List;
import org.json.JSONObject;
import java.util.HashMap;
import com.me.mdm.server.ios.pushNotification.IosFCMPushNotificationProcessor;
import java.util.logging.Logger;

public abstract class AppNotificationProcessor
{
    protected Logger logger;
    protected static final String NOTIFICATION_SUCCESS_LIST = "NotificationSuccessList";
    protected static final String NOTIFICATION_FAILURE_LIST = "NotificationFailureList";
    protected static final String NOTIFICATION_RE_REGISTER_LIST = "NotificationReRegisterList";
    
    public AppNotificationProcessor() {
        this.logger = Logger.getLogger("MDMWakupReqLogger");
    }
    
    public static AppNotificationProcessor getPushNotificationProcessor(final int notificationType) {
        if (notificationType == 101) {
            return new IosFCMPushNotificationProcessor();
        }
        return null;
    }
    
    public abstract HashMap pushMessageNotification(final HashMap p0);
    
    public abstract void buildMessage(final JSONObject p0);
    
    public void processNotificationResult(final HashMap notificationResult, final Long messageId) {
        final List<Long> successList = notificationResult.get("NotificationSuccessList");
        final List<Long> failureList = notificationResult.get("NotificationFailureList");
        final List<Long> reRegisterList = notificationResult.get("NotificationReRegisterList");
        if (successList != null && successList.size() > 0) {
            this.logger.log(Level.INFO, "Successfully sent notification message id : {0} for resource:{1}", new Object[] { messageId, successList });
            DeviceAppNotificationHandler.getInstance().updateNotificationStatus(successList, messageId, 3);
        }
        if (failureList != null && failureList.size() > 0) {
            this.logger.log(Level.INFO, "Failure notification message id : {0} for resource:{1}", new Object[] { messageId, successList });
            DeviceAppNotificationHandler.getInstance().updateNotificationStatus(failureList, messageId, 4);
            this.failureNotificationList(failureList);
        }
        if (reRegisterList != null && reRegisterList.size() > 0) {
            this.logger.log(Level.INFO, "Re-register resource:{1}", new Object[] { messageId, successList });
            DeviceAppNotificationHandler.getInstance().updateNotificationStatus(failureList, messageId, 4);
            this.reRegisterNotificationList(reRegisterList);
        }
    }
    
    public abstract void reRegisterNotificationList(final List<Long> p0);
    
    public abstract void failureNotificationList(final List<Long> p0);
}
