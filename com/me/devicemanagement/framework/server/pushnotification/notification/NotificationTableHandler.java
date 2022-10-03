package com.me.devicemanagement.framework.server.pushnotification.notification;

import com.me.devicemanagement.framework.server.pushnotification.common.NotificationStatus;
import java.util.Iterator;
import com.me.devicemanagement.framework.server.pushnotification.device.NotificationDevice;
import java.util.ArrayList;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.pushnotification.common.NotificationPlatform;
import java.util.Arrays;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.util.SyMUtil;

class NotificationTableHandler
{
    void addToTable(final Notification notification) throws Exception {
        final DataObject dataObject = SyMUtil.getPersistence().constructDataObject();
        final Row actionRow = new Row("NotificationList");
        actionRow.set("PRIORITY", (Object)notification.getPriority().id);
        actionRow.set("NOTIFICATION_TIME", (Object)notification.getTime());
        dataObject.addRow(actionRow);
        SyMUtil.getPersistence().add(dataObject);
        final Long notificationID = (Long)actionRow.get("NOTIFICATION_ID");
        this.mapNotificationWithPlatforms(notificationID, notification.getPlatforms());
        notification.info.addInfo(notificationID);
        this.mapNotificationToDevices(notificationID, notification.getDevices());
        if (notification.isForUser) {
            final Long userNotificationID = this.mapNotificationToUser(notificationID, notification.userID);
            if (Arrays.asList(notification.platforms).contains(NotificationPlatform.WEB)) {
                this.addNotificationToWebList(userNotificationID);
            }
        }
    }
    
    private void mapNotificationToDevices(final Long notificationID, final ArrayList<NotificationDevice> devices) throws Exception {
        if (devices != null && devices.size() > 0) {
            final DataObject dataObject = SyMUtil.getPersistence().constructDataObject();
            for (final NotificationDevice device : devices) {
                final Row actionRow = new Row("NotificationToDeviceRel");
                actionRow.set("NOTIFICATION_ID", (Object)notificationID);
                actionRow.set("NOTIFICATION_DEVICE_ID", (Object)device.getDeviceID());
                dataObject.addRow(actionRow);
            }
            SyMUtil.getPersistence().add(dataObject);
        }
    }
    
    private void mapNotificationWithPlatforms(final Long notificationID, final NotificationPlatform... platforms) throws Exception {
        final DataObject dataObject = SyMUtil.getPersistence().constructDataObject();
        for (final NotificationPlatform platform : platforms) {
            final Row actionRow = new Row("NotificationToPlatformRel");
            actionRow.set("NOTIFICATION_ID", (Object)notificationID);
            actionRow.set("PLATFORM", (Object)platform.id);
            dataObject.addRow(actionRow);
        }
        SyMUtil.getPersistence().add(dataObject);
    }
    
    private Long mapNotificationToUser(final Long notificationID, final Long userID) throws Exception {
        final DataObject dataObject = SyMUtil.getPersistence().constructDataObject();
        final Row actionRow = new Row("NotificationToUserRel");
        actionRow.set("NOTIFICATION_ID", (Object)notificationID);
        actionRow.set("USER_ID", (Object)userID);
        dataObject.addRow(actionRow);
        SyMUtil.getPersistence().add(dataObject);
        return (Long)actionRow.get("USER_NOTIFICATION_ID");
    }
    
    private void addNotificationToWebList(final Long userNotificationID) throws Exception {
        final DataObject dataObject = SyMUtil.getPersistence().constructDataObject();
        final Row actionRow = new Row("WebNotificationReadStatus");
        actionRow.set("USER_NOTIFICATION_ID", (Object)userNotificationID);
        actionRow.set("READ_STATUS", (Object)NotificationStatus.UNREAD.id);
        dataObject.addRow(actionRow);
        SyMUtil.getPersistence().add(dataObject);
    }
}
