package com.me.devicemanagement.onpremise.server.common;

import com.me.devicemanagement.framework.server.pushnotification.notification.Notification;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.pushnotification.PushNotificationService;
import com.me.devicemanagement.framework.server.pushnotification.common.NotificationPriority;
import com.me.devicemanagement.framework.server.pushnotification.common.NotificationPlatform;
import com.me.devicemanagement.framework.server.pushnotification.notification.NotificationBuilder;
import com.me.devicemanagement.framework.server.pushnotification.message.NotificationInfo;
import java.io.File;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import java.util.Iterator;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.Column;
import org.json.JSONArray;
import org.json.JSONException;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.util.DateTimeUtil;
import java.util.HashMap;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import org.json.JSONObject;
import java.util.logging.Logger;

public class MobileMarketingNotification extends FlashMessage
{
    private static Logger logger;
    
    public static void generateMobileNotification(final String mobileNotificationString, final String msgKey) {
        try {
            final JSONObject mobileNotification = new JSONObject(mobileNotificationString);
            if (mobileNotification.has("Date") && !String.valueOf(mobileNotification.get("Date")).equals("")) {
                final DataObject dataObject = DataAccess.get("MobileNotificationData", (Criteria)null);
                final long timeToNotify = Long.parseLong(String.valueOf(mobileNotification.get("Date")));
                if (timeToNotify > System.currentTimeMillis()) {
                    final Row row = new Row("MobileNotificationData");
                    row.set("MSG_KEY", (Object)msgKey);
                    row.set("NOTIFICATION_TIME", (Object)timeToNotify);
                    row.set("NOTIFICATION_DATA", (Object)mobileNotification.toString());
                    dataObject.addRow(row);
                    DataAccess.update(dataObject);
                    createScheduler(timeToNotify);
                }
            }
            else {
                createNotificationInfo(mobileNotification);
            }
        }
        catch (final Exception e) {
            MobileMarketingNotification.logger.log(Level.INFO, "Exception while adding data to mobile notification data table from a json", e);
        }
    }
    
    private static void createScheduler(final long timeToNotify) {
        final String scheduleName = "FlashMsgMobileNotification_" + System.currentTimeMillis();
        final HashMap schedulerProps = new HashMap();
        schedulerProps.put("workEngineId", scheduleName);
        schedulerProps.put("workflowName", scheduleName);
        schedulerProps.put("schedulerName", scheduleName);
        schedulerProps.put("taskName", scheduleName);
        schedulerProps.put("operationType", String.valueOf(10000));
        schedulerProps.put("className", "com.me.devicemanagement.onpremise.server.common.MobileMarketingNotificationTask");
        schedulerProps.put("description", "To send mobile marketing notification");
        schedulerProps.put("schType", "Once");
        schedulerProps.put("skip_missed_schedule", "false");
        final String date_time = DateTimeUtil.longdateToString(timeToNotify, "MM/dd/yyyy HH:mm:ss");
        final String[] result = date_time.split(" ");
        final String scheduledDate = result[0];
        final String scheduledTime = result[1];
        schedulerProps.put("time", scheduledTime);
        schedulerProps.put("date", scheduledDate);
        ApiFactoryProvider.getSchedulerAPI().createScheduler(schedulerProps);
        MobileMarketingNotification.logger.log(Level.INFO, "Flash message mobile notification scheduled on " + date_time);
    }
    
    public void sendNotification() {
        final JSONArray jsonArray = getNotificationsToSend();
        for (int i = 0; i < jsonArray.length(); ++i) {
            try {
                createNotificationInfo(new JSONObject(String.valueOf(jsonArray.get(i))));
            }
            catch (final JSONException e) {
                MobileMarketingNotification.logger.log(Level.INFO, "JSON exception", (Throwable)e);
            }
        }
    }
    
    private static JSONArray getNotificationsToSend() {
        final JSONArray jsonArray = new JSONArray();
        final Criteria criteria = new Criteria(new Column("MobileNotificationData", "NOTIFICATION_TIME"), (Object)System.currentTimeMillis(), 6);
        try {
            final DataObject dataObject = DataAccess.get("MobileNotificationData", criteria);
            final Iterator rows = dataObject.getRows("MobileNotificationData", criteria);
            while (rows.hasNext()) {
                final Row row = rows.next();
                jsonArray.put(row.get("NOTIFICATION_DATA"));
                dataObject.deleteRow(row);
            }
            DataAccess.update(dataObject);
        }
        catch (final DataAccessException e) {
            MobileMarketingNotification.logger.log(Level.INFO, "Error while accessing MobileNotificationData table");
        }
        return jsonArray;
    }
    
    private static void createNotificationInfo(final JSONObject mobileNotification) {
        if (mobileNotification != null) {
            try {
                final String notificationTitle = String.valueOf(mobileNotification.get("Title"));
                final String notificationMessage = String.valueOf(mobileNotification.get("Message"));
                final String notificationPageUrl = String.valueOf(mobileNotification.get("Url"));
                String imageUrl = "";
                if (mobileNotification.has("Image")) {
                    imageUrl = String.valueOf(mobileNotification.get("Image"));
                    if (imageUrl != null && !imageUrl.equals("")) {
                        imageUrl = ProductUrlLoader.getInstance().getValue("updates_check_url").replace("dcupdates.json", "flashmsg") + File.separator + imageUrl;
                    }
                }
                long notificationTime;
                if (mobileNotification.has("Date") && mobileNotification.get("Date") != null && !String.valueOf(mobileNotification.get("Date")).equals("")) {
                    notificationTime = Long.parseLong(String.valueOf(mobileNotification.get("Date")));
                }
                else {
                    notificationTime = System.currentTimeMillis();
                }
                final JSONObject payLoad = new JSONObject();
                payLoad.put("n_type", (Object)"mktg");
                payLoad.put("title", (Object)notificationTitle);
                payLoad.put("msg", (Object)notificationMessage);
                payLoad.put("url", (Object)notificationPageUrl);
                if (imageUrl != null && !imageUrl.equals("")) {
                    payLoad.put("img_url", (Object)imageUrl);
                }
                deleteOldNotification(payLoad.toString());
                final NotificationInfo notificationInfo = new NotificationInfo.Builder().withTitle(notificationTitle).withMessage(notificationMessage).withSound("default").withCustomPayload(payLoad).build();
                notify(notificationInfo, notificationTime);
            }
            catch (final JSONException e) {
                MobileMarketingNotification.logger.log(Level.INFO, "JSON Exception ", (Throwable)e);
            }
        }
    }
    
    private static void notify(final NotificationInfo notificationInfo, final Long notificationTime) {
        final ArrayList<Long> usersId = getUserList();
        for (final Long singleUserId : usersId) {
            try {
                final Notification notification = NotificationBuilder.forUser.withID(Long.valueOf(Long.parseLong(singleUserId.toString()))).withPlatforms(new NotificationPlatform[] { NotificationPlatform.ANDROID, NotificationPlatform.IOS }).withPriority(NotificationPriority.MEDIUM).withInfo(notificationInfo).onTime((long)notificationTime).build();
                final PushNotificationService pushNotificationService = (PushNotificationService)com.me.devicemanagement.onpremise.server.factory.ApiFactoryProvider.getImplClassInstance("PUSH_NOTIFICATION_SERVICE");
                assert pushNotificationService != null;
                pushNotificationService.pushNotify(notification);
            }
            catch (final Exception e) {
                MobileMarketingNotification.logger.log(Level.INFO, "Error while creating notification", e);
            }
        }
    }
    
    private static void deleteOldNotification(final String payLoad) {
        try {
            Criteria criteria = new Criteria(new Column("NotificationInfo", "PAYLOAD"), (Object)payLoad, 0);
            DataObject dataObject = DataAccess.get("NotificationInfo", criteria);
            final Iterator rows = dataObject.getRows("NotificationInfo");
            dataObject = DataAccess.get("NotificationList", (Criteria)null);
            criteria = new Criteria(new Column("NotificationList", "NOTIFICATION_ID"), (Object)0, 0);
            while (rows.hasNext()) {
                final Row row = rows.next();
                criteria = criteria.or(new Criteria(new Column("NotificationList", "NOTIFICATION_ID"), row.get("NOTIFICATION_ID"), 0));
            }
            dataObject.deleteRows("NotificationList", criteria);
            DataAccess.update(dataObject);
        }
        catch (final Exception e) {
            MobileMarketingNotification.logger.log(Level.INFO, "Error while accessing NotificationInfo Table", e);
        }
    }
    
    private static ArrayList<Long> getUserList() {
        final ArrayList<Long> usersId = new ArrayList<Long>();
        try {
            final DataObject dataObject = DataAccess.get("AaaLogin", (Criteria)null);
            final Iterator rows = dataObject.getRows("AaaLogin");
            while (rows.hasNext()) {
                final Row userDetails = rows.next();
                usersId.add((Long)userDetails.get("USER_ID"));
            }
        }
        catch (final DataAccessException e) {
            MobileMarketingNotification.logger.log(Level.INFO, "Exception while accessing AAALOGIN table", (Throwable)e);
        }
        return usersId;
    }
    
    static {
        MobileMarketingNotification.logger = Logger.getLogger(MobileMarketingNotification.class.getName());
    }
}
