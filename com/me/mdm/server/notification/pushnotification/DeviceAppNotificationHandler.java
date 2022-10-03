package com.me.mdm.server.notification.pushnotification;

import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.i18n.I18N;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.queue.DCQueue;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import com.me.devicemanagement.framework.server.queue.DCQueueHandler;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.logging.Level;
import com.me.mdm.server.notification.PushNotificationHandler;
import java.util.Collection;
import java.util.ArrayList;
import org.json.JSONObject;
import java.util.List;
import java.util.logging.Logger;

public class DeviceAppNotificationHandler
{
    private static DeviceAppNotificationHandler handler;
    private Logger logger;
    
    public DeviceAppNotificationHandler() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public static DeviceAppNotificationHandler getInstance() {
        if (DeviceAppNotificationHandler.handler == null) {
            DeviceAppNotificationHandler.handler = new DeviceAppNotificationHandler();
        }
        return DeviceAppNotificationHandler.handler;
    }
    
    public List<Long> addPushNotification(final List resourceList, final int notificationType, final JSONObject messageJSON, final JSONObject customPayload) {
        final List<Long> removedList = new ArrayList<Long>(resourceList);
        final List<Long> notificationTokenList = new PushNotificationHandler().getNotificationTokenDevices(resourceList, notificationType);
        removedList.removeAll(notificationTokenList);
        final Long messageId = this.savePushNotificationRow(notificationTokenList, notificationType, messageJSON, customPayload);
        final Long customerId = Long.parseLong(messageJSON.get("CUSTOMER_ID").toString());
        this.addToQueue(notificationType, messageId, customerId);
        return removedList;
    }
    
    public List<Long> addPushNotification(final List resourceList, final int notificationType, final DeviceAppNotification devicePayload, final Long customerId) {
        final JSONObject messageJSON = devicePayload.toJSON();
        final JSONObject customPayload = devicePayload.getCustomPayload();
        messageJSON.put("CUSTOMER_ID", (Object)customerId);
        return this.addPushNotification(resourceList, notificationType, messageJSON, customPayload);
    }
    
    private Long savePushNotificationRow(final List resourceList, final int notificationType, final JSONObject messageJSON, final JSONObject customPayload) {
        Long messageId = -1L;
        try {
            this.logger.log(Level.INFO, "Going to save notification row for resource:{0}", new Object[] { resourceList });
            final DataObject DO = MDMUtil.getPersistence().constructDataObject();
            final int messageType = messageJSON.getInt("type");
            final Row appNotifRow = new Row("AppNotificationMessageInfo");
            appNotifRow.set("MESSAGE_TYPE", (Object)messageType);
            appNotifRow.set("CATEGORY", messageJSON.get("category"));
            DO.addRow(appNotifRow);
            if (messageType == 1) {
                appNotifRow.set("I18N_TITLE", messageJSON.get("title_key"));
                appNotifRow.set("I18N_MESSAGE", messageJSON.get("message_key"));
                if (messageJSON.has("I18N_TITLE_ARGS")) {
                    appNotifRow.set("I18N_TITLE_ARGS", (Object)messageJSON.get("title_args").toString());
                }
                if (messageJSON.has("I18N_MESSAGE_ARGS")) {
                    appNotifRow.set("I18N_MESSAGE_ARGS", (Object)messageJSON.get("message_args").toString());
                }
            }
            else {
                appNotifRow.set("TITLE", messageJSON.get("title"));
                appNotifRow.set("BODY", messageJSON.get("message"));
            }
            final Iterator iterator = customPayload.keys();
            while (iterator.hasNext()) {
                final String key = iterator.next().toString();
                final Row notifCustomRow = new Row("AppNotificationMessageCustomDetails");
                notifCustomRow.set("MESSAGE_ID", appNotifRow.get("MESSAGE_ID"));
                notifCustomRow.set("KEY", (Object)key);
                notifCustomRow.set("VALUE", customPayload.get(key));
                DO.addRow(notifCustomRow);
            }
            final Iterator item = resourceList.iterator();
            while (item.hasNext()) {
                final Row pushRow = new Row("DeviceAppNotification");
                pushRow.set("MESSAGE_ID", appNotifRow.get("MESSAGE_ID"));
                pushRow.set("RESOURCE_ID", (Object)item.next());
                pushRow.set("STATUS", (Object)1);
                pushRow.set("ADDED_TIME", (Object)System.currentTimeMillis());
                DO.addRow(pushRow);
            }
            MDMUtil.getPersistence().add(DO);
            messageId = Long.parseLong(appNotifRow.get("MESSAGE_ID").toString());
            this.logger.log(Level.INFO, "Added message to appnotificationmessageinfo. Message Id:{0}", new Object[] { messageId });
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, "Exception while saving push notification", (Throwable)e);
        }
        return messageId;
    }
    
    private void addToQueue(final int notificationType, final Long messageId, final Long customerId) {
        try {
            final DCQueue queue = DCQueueHandler.getQueue("app-notification-processor");
            final DCQueueData queueData = new DCQueueData();
            queueData.postTime = System.currentTimeMillis();
            final HashMap dataToQueue = new HashMap();
            dataToQueue.put("MESSAGE_ID", Long.toString(messageId));
            dataToQueue.put("NOTIFICATION_TYPE", Integer.toString(notificationType));
            final long postTime = MDMUtil.getCurrentTimeInMillis();
            final String qFileName = customerId + "-" + messageId + "-" + postTime + ".txt";
            queueData.queueData = dataToQueue;
            queueData.fileName = qFileName;
            queue.addToQueue(queueData);
            this.logger.log(Level.INFO, "Added the app notification to queue notification type:{0} message id:{1}", new Object[] { notificationType, messageId });
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in adding to queue", e);
        }
    }
    
    public void updateNotificationStatus(final List resourceIdList, final Long messageId, final int status) {
        try {
            if (status == 4) {
                final SelectQuery subQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AppNotificationMessageCustomDetails"));
                Criteria criteria = new Criteria(Column.getColumn("AppNotificationMessageCustomDetails", "MESSAGE_ID"), (Object)messageId, 0);
                Criteria criteria2 = new Criteria(new Column("AppNotificationMessageCustomDetails", "KEY"), (Object)"announcement_id", 0);
                subQuery.setCriteria(criteria.and(criteria2));
                subQuery.addSelectColumn(new Column("AppNotificationMessageCustomDetails", "*"));
                DataObject dataObject = MDMUtil.getPersistence().get(subQuery);
                Long announcement_id = null;
                if (!dataObject.isEmpty()) {
                    announcement_id = Long.parseLong(dataObject.getFirstRow("AppNotificationMessageCustomDetails").get("VALUE").toString());
                    final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("AnnouncementConfigData"));
                    selectQuery.setCriteria(new Criteria(new Column("AnnouncementConfigData", "ANNOUNCEMENT_ID"), (Object)announcement_id, 0));
                    selectQuery.addJoin(new Join("AnnouncementConfigData", "ConfigDataItem", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ID" }, 1));
                    selectQuery.addJoin(new Join("ConfigDataItem", "CfgDataToCollection", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 1));
                    selectQuery.addSelectColumn(new Column("CfgDataToCollection", "*"));
                    dataObject = MDMUtil.getPersistence().get(selectQuery);
                    if (!dataObject.isEmpty()) {
                        final Row row = dataObject.getFirstRow("CfgDataToCollection");
                        final Long collectionId = Long.parseLong(row.get("COLLECTION_ID").toString());
                        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("CollnToResources");
                        criteria = new Criteria(new Column("CollnToResources", "RESOURCE_ID"), (Object)resourceIdList.toArray(), 8);
                        criteria2 = new Criteria(new Column("CollnToResources", "COLLECTION_ID"), (Object)collectionId, 0);
                        updateQuery.setCriteria(criteria.and(criteria2));
                        final String remarks = I18N.getMsg("mdm.fcm.ios.notification_failed_remarks", new Object[0]);
                        updateQuery.setUpdateColumn("STATUS", (Object)7);
                        updateQuery.setUpdateColumn("REMARKS", (Object)remarks);
                        MDMUtil.getPersistence().update(updateQuery);
                    }
                }
            }
            final UpdateQuery uQuery = (UpdateQuery)new UpdateQueryImpl("DeviceAppNotification");
            uQuery.setCriteria(new Criteria(new Column("DeviceAppNotification", "RESOURCE_ID"), (Object)resourceIdList.toArray(), 8).and(new Criteria(new Column("DeviceAppNotification", "MESSAGE_ID"), (Object)messageId, 0)));
            uQuery.setUpdateColumn("STATUS", (Object)status);
            uQuery.setUpdateColumn("STATUS_CHANGE_TIME", (Object)System.currentTimeMillis());
            MDMUtil.getPersistence().update(uQuery);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    public JSONObject getMessageDetails(final Long messageId) {
        final JSONObject jsonObject = new JSONObject();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("AppNotificationMessageInfo"));
            selectQuery.addJoin(new Join("AppNotificationMessageInfo", "AppNotificationMessageCustomDetails", new String[] { "MESSAGE_ID" }, new String[] { "MESSAGE_ID" }, 2));
            selectQuery.addSelectColumn(new Column((String)null, "*"));
            final Criteria messageCriteria = new Criteria(new Column("AppNotificationMessageInfo", "MESSAGE_ID"), (Object)messageId, 0);
            selectQuery.setCriteria(messageCriteria);
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Row messageRow = dataObject.getRow("AppNotificationMessageInfo");
                for (final Object column : messageRow.getColumns()) {
                    final String columnName = (String)column;
                    jsonObject.put(columnName, messageRow.get(columnName));
                }
                final Iterator iterator = dataObject.getRows("AppNotificationMessageCustomDetails", messageRow);
                final JSONObject customDetailJSON = new JSONObject();
                while (iterator.hasNext()) {
                    final Row customRow = iterator.next();
                    final String key = (String)customRow.get("KEY");
                    final Object value = customRow.get("VALUE");
                    customDetailJSON.put(key, value);
                }
                jsonObject.put("AppNotificationMessageCustomDetails", (Object)customDetailJSON);
            }
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, "Exception in getting message", (Throwable)e);
        }
        return jsonObject;
    }
    
    public HashMap getResourceForAppNotification(final Long messageId, final int notificationType, final int range) {
        final HashMap notificationMap = new HashMap();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("DeviceAppNotification"));
            selectQuery.addJoin(new Join("DeviceAppNotification", "ManagedDeviceNotification", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
            selectQuery.addJoin(new Join("ManagedDeviceNotification", "NotificationDetails", new String[] { "NOTIFICATION_DETAILS_ID" }, new String[] { "NOTIFICATION_DETAILS_ID" }, 2));
            selectQuery.addSelectColumn(new Column("ManagedDeviceNotification", "MANAGED_DEVICE_ID"));
            selectQuery.addSelectColumn(new Column("ManagedDeviceNotification", "NOTIFICATION_DETAILS_ID"));
            selectQuery.addSelectColumn(new Column("NotificationDetails", "NOTIFICATION_DETAILS_ID", "NOTIFICATION_DETAILS_ID_ALIAS"));
            selectQuery.addSelectColumn(new Column("NotificationDetails", "NOTIFICATION_TOKEN_ENCRYPTED"));
            final Criteria messageCriteria = new Criteria(new Column("DeviceAppNotification", "MESSAGE_ID"), (Object)messageId, 0);
            final Criteria notificationCriteria = new Criteria(new Column("ManagedDeviceNotification", "NOTIFICATION_TYPE"), (Object)notificationType, 0);
            final Criteria statusCriteria = new Criteria(new Column("DeviceAppNotification", "STATUS"), (Object)1, 0);
            selectQuery.setCriteria(messageCriteria.and(notificationCriteria).and(statusCriteria));
            final SortColumn sortColumn = new SortColumn("ManagedDeviceNotification", "MANAGED_DEVICE_ID", false);
            final ArrayList list = new ArrayList();
            list.add(sortColumn);
            selectQuery.addSortColumns((List)list);
            selectQuery.setRange(new Range(0, range));
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows("ManagedDeviceNotification");
                while (iterator.hasNext()) {
                    final Row managedDeviceRow = iterator.next();
                    final Long resourceId = (Long)managedDeviceRow.get("MANAGED_DEVICE_ID");
                    final Row notificationRow = dataObject.getRow("NotificationDetails", managedDeviceRow);
                    final String notificationToken = (String)notificationRow.get("NOTIFICATION_TOKEN_ENCRYPTED");
                    notificationMap.put(resourceId, notificationToken);
                }
            }
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, "Exception in getting getresource for app notification", (Throwable)e);
        }
        return notificationMap;
    }
}
