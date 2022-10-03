package com.me.devicemanagement.framework.server.pushnotification.web;

import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import com.adventnet.ds.query.SelectQuery;
import org.json.JSONArray;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.me.devicemanagement.framework.server.pushnotification.common.NotificationStatus;
import com.adventnet.ds.query.Criteria;
import com.me.devicemanagement.framework.server.pushnotification.common.NotificationPlatform;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.JSONObject;

public class WebNotification
{
    public JSONObject getNotificationsForUser(final Long userID, final boolean unRead) throws Exception {
        final JSONObject object = new JSONObject();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("NotificationList"));
        selectQuery.addJoin(new Join("NotificationList", "NotificationInfo", new String[] { "NOTIFICATION_ID" }, new String[] { "NOTIFICATION_ID" }, 1));
        selectQuery.addJoin(new Join("NotificationList", "NotificationToPlatformRel", new String[] { "NOTIFICATION_ID" }, new String[] { "NOTIFICATION_ID" }, 1));
        selectQuery.addJoin(new Join("NotificationList", "NotificationToUserRel", new String[] { "NOTIFICATION_ID" }, new String[] { "NOTIFICATION_ID" }, 1));
        selectQuery.addJoin(new Join("NotificationToUserRel", "WebNotificationReadStatus", new String[] { "USER_NOTIFICATION_ID" }, new String[] { "USER_NOTIFICATION_ID" }, 1));
        selectQuery.addSelectColumn(new Column((String)null, "*"));
        final Criteria platformCriteria = new Criteria(Column.getColumn("NotificationToPlatformRel", "PLATFORM"), (Object)NotificationPlatform.WEB.id, 0);
        final Criteria userCriteria = new Criteria(Column.getColumn("NotificationToUserRel", "USER_ID"), (Object)userID, 0);
        Criteria criteria = platformCriteria.and(userCriteria);
        if (unRead) {
            final Criteria unreadCriteria = new Criteria(Column.getColumn("WebNotificationReadStatus", "READ_STATUS"), (Object)NotificationStatus.UNREAD.id, 0);
            criteria = criteria.and(unreadCriteria);
        }
        selectQuery.setCriteria(criteria);
        try (final Connection conn = RelationalAPI.getInstance().getConnection()) {
            DataSet s = null;
            try {
                s = RelationalAPI.getInstance().executeQuery((Query)selectQuery, conn);
                if (s != null) {
                    final JSONArray notifications = new JSONArray();
                    while (s.next()) {
                        final Long notificationID = (Long)s.getValue("NOTIFICATION_ID");
                        final Integer priority = (Integer)s.getValue("PRIORITY");
                        final Long time = (Long)s.getValue("NOTIFICATION_TIME");
                        final Integer platform = (Integer)s.getValue("PLATFORM");
                        final Long tableUserID = (Long)s.getValue("USER_ID");
                        final Long notificationUserID = (Long)s.getValue("USER_NOTIFICATION_ID");
                        final Integer readStatus = (Integer)s.getValue("READ_STATUS");
                        final Long notificationInfoID = (Long)s.getValue("NOTIFICATION_INFO_ID");
                        final String title = (String)s.getValue("TITLE");
                        final String message = (String)s.getValue("BODY");
                        final String icon = (String)s.getValue("ICON");
                        final String sound = (String)s.getValue("SOUND");
                        final JSONObject payload = new JSONObject((String)s.getValue("payload"));
                        final String collapseId = (String)s.getValue("COLLAPSE_ID");
                        final Integer notificationType = (Integer)s.getValue("NOTIFICATION_TYPE");
                        final JSONObject notification = new JSONObject();
                        notification.put("notification_id", (notificationID == null) ? JSONObject.NULL : notificationID);
                        notification.put("priority", (priority == null) ? JSONObject.NULL : priority);
                        notification.put("time", (time == null) ? JSONObject.NULL : time);
                        notification.put("platform", (platform == null) ? JSONObject.NULL : platform);
                        notification.put("user_id", (tableUserID == null) ? JSONObject.NULL : tableUserID);
                        notification.put("notification_user_id", (notificationUserID == null) ? JSONObject.NULL : notificationUserID);
                        notification.put("read_status", (readStatus == null) ? JSONObject.NULL : readStatus);
                        notification.put("notification_info_id", (notificationInfoID == null) ? JSONObject.NULL : notificationInfoID);
                        notification.put("title", (title == null) ? JSONObject.NULL : title);
                        notification.put("message", (message == null) ? JSONObject.NULL : message);
                        notification.put("icon", (icon == null) ? JSONObject.NULL : icon);
                        notification.put("sound", (sound == null) ? JSONObject.NULL : sound);
                        notification.put("payload", (Object)payload);
                        notification.put("collapse_id", (collapseId == null) ? JSONObject.NULL : collapseId);
                        notification.put("notification_type", (notificationType == null) ? JSONObject.NULL : notificationType);
                        notifications.put((Object)notification);
                    }
                    object.put("notifications", (Object)notifications);
                }
            }
            catch (final Exception ex) {}
            finally {
                try {
                    if (s != null) {
                        s.close();
                    }
                }
                catch (final Exception ex2) {}
            }
        }
        catch (final Exception ex3) {}
        return object;
    }
    
    public void markNotificationsReadByUser(final Long userID) throws Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("NotificationList"));
        selectQuery.addJoin(new Join("NotificationList", "NotificationInfo", new String[] { "NOTIFICATION_ID" }, new String[] { "NOTIFICATION_ID" }, 1));
        selectQuery.addJoin(new Join("NotificationList", "NotificationToPlatformRel", new String[] { "NOTIFICATION_ID" }, new String[] { "NOTIFICATION_ID" }, 1));
        selectQuery.addJoin(new Join("NotificationList", "NotificationToUserRel", new String[] { "NOTIFICATION_ID" }, new String[] { "NOTIFICATION_ID" }, 1));
        selectQuery.addJoin(new Join("NotificationToUserRel", "WebNotificationReadStatus", new String[] { "USER_NOTIFICATION_ID" }, new String[] { "USER_NOTIFICATION_ID" }, 1));
        selectQuery.addSelectColumn(new Column((String)null, "*"));
        final Criteria platformCriteria = new Criteria(Column.getColumn("NotificationToPlatformRel", "PLATFORM"), (Object)NotificationPlatform.WEB.id, 0);
        final Criteria userCriteria = new Criteria(Column.getColumn("NotificationToUserRel", "USER_ID"), (Object)userID, 0);
        final Criteria unreadCriteria = new Criteria(Column.getColumn("WebNotificationReadStatus", "READ_STATUS"), (Object)NotificationStatus.UNREAD.id, 0);
        final Criteria criteria = platformCriteria.and(userCriteria).and(unreadCriteria);
        selectQuery.setCriteria(criteria);
        final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
        final Iterator<Row> rows = dataObject.getRows("WebNotificationReadStatus");
        while (rows.hasNext()) {
            final Row currentRow = rows.next();
            currentRow.set("READ_STATUS", (Object)NotificationStatus.READ.id);
            dataObject.updateRow(currentRow);
        }
        SyMUtil.getPersistence().update(dataObject);
    }
}
