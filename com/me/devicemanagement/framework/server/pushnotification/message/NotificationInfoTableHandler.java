package com.me.devicemanagement.framework.server.pushnotification.message;

import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.util.SyMUtil;

public class NotificationInfoTableHandler
{
    void addToTable(final Long notificationID, final NotificationInfo info) throws Exception {
        final DataObject dataObject = SyMUtil.getPersistence().constructDataObject();
        final Row actionRow = new Row("NotificationInfo");
        actionRow.set("NOTIFICATION_ID", (Object)notificationID);
        if (info.title != null) {
            actionRow.set("TITLE", (Object)info.title);
        }
        if (info.message != null) {
            actionRow.set("BODY", (Object)info.message);
        }
        if (info.type != null) {
            actionRow.set("NOTIFICATION_TYPE", (Object)info.type.id);
        }
        if (info.collapseID != null) {
            actionRow.set("COLLAPSE_ID", (Object)info.collapseID);
        }
        if (info.icon != null) {
            actionRow.set("ICON", (Object)info.icon);
        }
        if (info.sound != null) {
            actionRow.set("SOUND", (Object)info.sound);
        }
        if (info.payload != null) {
            actionRow.set("PAYLOAD", (Object)info.payload.toString());
        }
        dataObject.addRow(actionRow);
        SyMUtil.getPersistence().add(dataObject);
    }
}
