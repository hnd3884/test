package com.me.devicemanagement.framework.server.pushnotification;

import com.me.devicemanagement.framework.server.pushnotification.message.CustomPayload;
import com.me.devicemanagement.framework.server.pushnotification.message.CustomNotificationPayload;
import org.apache.commons.lang3.StringUtils;
import com.me.devicemanagement.framework.server.pushnotification.notification.Notification;
import java.util.Iterator;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.pushnotification.message.NotificationInfo;
import com.me.devicemanagement.framework.server.pushnotification.common.NotificationPriority;
import com.me.devicemanagement.framework.server.pushnotification.common.NotificationPlatform;
import com.me.devicemanagement.framework.server.pushnotification.notification.NotificationBuilder;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.List;
import java.util.logging.Logger;

public class PushNotificationUtil
{
    private Logger logger;
    public static final int SCHEDULE_DB_BK_PUSH_NOTIFICATION = 11;
    private static PushNotificationUtil pushNotificationUtil;
    
    private PushNotificationUtil() {
        this.logger = Logger.getLogger(PushNotificationUtil.class.getName());
    }
    
    public static PushNotificationUtil getInstance() {
        if (PushNotificationUtil.pushNotificationUtil == null) {
            PushNotificationUtil.pushNotificationUtil = new PushNotificationUtil();
        }
        return PushNotificationUtil.pushNotificationUtil;
    }
    
    @Deprecated
    public void sendPushNotification(final List<Long> pushNotifyUsers, final JSONObject notificationMessage, final JSONObject customPayLoad) {
        if (!this.isEmptyMessage(pushNotifyUsers, notificationMessage)) {
            try {
                this.logger.log(Level.INFO, "Constructed Push Notification: " + pushNotifyUsers + " " + notificationMessage + " " + customPayLoad);
                for (final Long userId : pushNotifyUsers) {
                    final Notification notification = NotificationBuilder.forUser.withID(userId).withPlatforms(NotificationPlatform.ANDROID, NotificationPlatform.IOS).withPriority(NotificationPriority.MEDIUM).withInfo(new NotificationInfo.Builder().withTitle(notificationMessage.getString("title")).withMessage(notificationMessage.getString("message")).withIcon("success_icon").withSound("default").withCollapseID("PATCH_NOTIFICATION").withCustomPayload(customPayLoad).build()).build();
                    final PushNotificationService pushNotificationService = (PushNotificationService)ApiFactoryProvider.getImplClassInstance("PUSH_NOTIFICATION_SERVICE");
                    pushNotificationService.pushNotify(notification);
                }
            }
            catch (final Exception e) {
                this.logger.log(Level.WARNING, "Exception in sending Push Notification ", e);
            }
        }
        else {
            this.logger.log(Level.INFO, "Push Notification not sent, received empty userList/message: " + pushNotifyUsers + " " + notificationMessage);
        }
    }
    
    private boolean isEmptyMessage(final List<Long> users, final JSONObject msg) {
        try {
            return users.isEmpty() || msg.isNull("title") || msg.isNull("message") || StringUtils.isEmpty((CharSequence)msg.getString("title")) || StringUtils.isEmpty((CharSequence)msg.getString("message"));
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception in isEmptyMessage", e);
            return false;
        }
    }
    
    public void sendPushNotificationWithCustomPayload(final List<Long> pushNotifyUsers, final CustomNotificationPayload notificationPayload) {
        if (!this.isEmptyCustomMessage(pushNotifyUsers, notificationPayload)) {
            try {
                final JSONObject jsonPayload = new JSONObject();
                if (notificationPayload.getPayload() != null) {
                    final CustomPayload payload = notificationPayload.getPayload();
                    jsonPayload.put("notification_type", (Object)payload.getNotificationType());
                    jsonPayload.put("moduleId", payload.getModuleID());
                    if (payload.getNType() != null) {
                        jsonPayload.put("n_type", (Object)payload.getNType());
                    }
                    if (payload.getLink() != null) {
                        jsonPayload.put("link", (Object)payload.getLink());
                    }
                    if (payload.getSummary() != null) {
                        jsonPayload.put("summary", (Object)payload.getSummary());
                    }
                }
                else {
                    jsonPayload.put("notification_type", (Object)CustomPayload.NotificationType.INFORMATION);
                }
                this.logger.log(Level.INFO, "Constructed Push Notification: " + pushNotifyUsers + " Title: " + notificationPayload.getTitle() + " Message: " + notificationPayload.getMessage() + " CustomPayload: " + jsonPayload);
                for (final Long userId : pushNotifyUsers) {
                    final Notification notification = NotificationBuilder.forUser.withID(userId).withPlatforms(NotificationPlatform.ANDROID, NotificationPlatform.IOS).withPriority(NotificationPriority.MEDIUM).withInfo(new NotificationInfo.Builder().withTitle(notificationPayload.getTitle()).withMessage(notificationPayload.getMessage()).withIcon("success_icon").withSound("default").withCollapseID("PATCH_NOTIFICATION").withCustomPayload(jsonPayload).build()).build();
                    final PushNotificationService pushNotificationService = (PushNotificationService)ApiFactoryProvider.getImplClassInstance("PUSH_NOTIFICATION_SERVICE");
                    pushNotificationService.pushNotify(notification);
                }
            }
            catch (final Exception e) {
                this.logger.log(Level.WARNING, "Exception in sending Push Notification ", e);
            }
        }
        else {
            this.logger.log(Level.INFO, "Push Notification not sent, received empty userList/message: " + pushNotifyUsers + " " + notificationPayload);
        }
    }
    
    private boolean isEmptyCustomMessage(final List<Long> users, final CustomNotificationPayload notificationPayload) {
        try {
            return users.isEmpty() || notificationPayload.getTitle() == null || notificationPayload.getMessage() == null || StringUtils.isEmpty((CharSequence)notificationPayload.getTitle()) || StringUtils.isEmpty((CharSequence)notificationPayload.getMessage());
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception in isEmptyCustomMessage", e);
            return false;
        }
    }
    
    static {
        PushNotificationUtil.pushNotificationUtil = null;
    }
}
