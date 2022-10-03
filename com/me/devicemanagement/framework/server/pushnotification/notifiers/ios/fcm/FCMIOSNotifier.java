package com.me.devicemanagement.framework.server.pushnotification.notifiers.ios.fcm;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadManager;
import com.google.android.gcm.server.Sender;
import com.google.android.gcm.server.Result;
import com.me.devicemanagement.framework.server.pushnotification.device.NotificationDevice;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.pushnotification.common.NotificationPriority;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import java.util.logging.Level;
import java.util.List;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.Properties;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.pushnotification.notification.Notification;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;
import com.me.devicemanagement.framework.server.pushnotification.notifiers.ios.iOSNotifier;

public abstract class FCMIOSNotifier implements iOSNotifier, SchedulerExecutionInterface
{
    private static final String DEVICE_NOT_REGISTERED = "NotRegistered";
    private static final int FCM_RETRIES = 5;
    private static final String ASYNC_TASK_NAME = "FCM_IOS_TASK";
    private static final String NOTIFICATION_TASK_PROP_KEY = "NOTIFICATION_FOR_TASK";
    private Logger logger;
    
    public FCMIOSNotifier() {
        this.logger = Logger.getLogger(FCMIOSNotifier.class.getName());
    }
    
    @Override
    public void notify(final Notification notification) throws Exception {
        this.sendToFCM(notification);
    }
    
    @Override
    public void notifyAsync(final Notification notification) throws Exception {
        final HashMap<String, Object> taskInfoMap = new HashMap<String, Object>();
        taskInfoMap.put("taskName", "FCM_IOS_TASK");
        taskInfoMap.put("schedulerTime", System.currentTimeMillis());
        final Properties taskProps = new Properties();
        ((Hashtable<String, Notification>)taskProps).put("NOTIFICATION_FOR_TASK", notification);
        ApiFactoryProvider.getSchedulerAPI().executeAsynchronously(this.getClass().getName(), taskInfoMap, taskProps);
    }
    
    @Override
    public void executeTask(final Properties props) {
        final Notification notification = ((Hashtable<K, Notification>)props).get("NOTIFICATION_FOR_TASK");
        this.sendToFCM(notification);
    }
    
    private void sendToFCM(final Notification notification) {
        try {
            if (notification.getIOSDeviceTokens() != null && notification.getIOSDeviceTokens().size() > 0) {
                final MulticastResult results = this.getApnsSender().send(this.getMessage(notification), (List)notification.getIOSDeviceTokens(), 5);
                this.processResult(notification, results, notification.getIOSDevices());
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Error occurred while sending message to FCM: ", ex);
        }
    }
    
    private Message getMessage(final Notification notification) {
        final Message.Builder builder = new Message.Builder();
        final com.google.android.gcm.server.Notification.Builder iOSNotificationBuilder = new com.google.android.gcm.server.Notification.Builder("success_icon");
        builder.addData("title", notification.getInfo().getTitle());
        builder.addData("message", notification.getInfo().getMessage());
        if (notification.getPriority() == NotificationPriority.HIGH) {
            builder.priority(Message.Priority.HIGH);
        }
        else {
            builder.priority(Message.Priority.NORMAL);
        }
        if (notification.getInfo().getTitle() != null) {
            iOSNotificationBuilder.title(notification.getInfo().getTitle());
        }
        if (notification.getInfo().getMessage() != null) {
            iOSNotificationBuilder.body(notification.getInfo().getMessage());
        }
        if (notification.getInfo().getSound() != null) {
            builder.addData("sound", notification.getInfo().getSound());
            iOSNotificationBuilder.sound(notification.getInfo().getSound());
        }
        if (notification.getInfo().getCollapseID() != null) {
            builder.collapseKey(notification.getInfo().getCollapseID());
        }
        builder.addData("time", Long.toString(notification.getTime()));
        if (notification.getInfo().getPayload() != null) {
            builder.addData("payload", notification.getInfo().getPayload().toString());
        }
        iOSNotificationBuilder.badge(0);
        builder.notification(iOSNotificationBuilder.build());
        builder.contentAvailable(Boolean.valueOf(true));
        return builder.build();
    }
    
    private void processResult(final Notification notification, final MulticastResult results, final ArrayList<NotificationDevice> devices) {
        final ArrayList<NotificationDevice> success = new ArrayList<NotificationDevice>();
        final ArrayList<NotificationDevice> failure = new ArrayList<NotificationDevice>();
        final ArrayList<NotificationDevice> notRegistered = new ArrayList<NotificationDevice>();
        for (int i = 0; i < results.getResults().size(); ++i) {
            final Result result = results.getResults().get(i);
            if (result.getMessageId() == null) {
                if (result.getErrorCodeName().equals("NotRegistered")) {
                    notRegistered.add(devices.get(i));
                }
                else {
                    failure.add(devices.get(i));
                }
            }
            else {
                success.add(devices.get(i));
            }
        }
        if (success.size() > 0) {
            this.onNotificationSuccess(notification, success);
        }
        if (failure.size() > 0) {
            this.onNotificationFailed(notification, failure);
        }
        if (notRegistered.size() > 0) {
            this.onReRegisterDevice(notification, notRegistered);
        }
    }
    
    private Sender getApnsSender() throws Exception {
        ApnsSender apnsSender = null;
        final Properties proxyDetails = ApiFactoryProvider.getServerSettingsAPI().getProxyConfiguration();
        final DownloadManager downloadMgr = DownloadManager.getInstance();
        final int proxyType = DownloadManager.proxyType;
        if (proxyType == 4) {
            final String url = "https://fcm.googleapis.com/fcm/send";
            final Properties pacProps = ApiFactoryProvider.getServerSettingsAPI().getProxyConfiguration(url, proxyDetails);
            apnsSender = new ApnsSender(this.getFcmKey(), pacProps);
        }
        else {
            apnsSender = new ApnsSender(this.getFcmKey(), proxyDetails);
        }
        return apnsSender;
    }
    
    protected abstract String getFcmKey();
}
