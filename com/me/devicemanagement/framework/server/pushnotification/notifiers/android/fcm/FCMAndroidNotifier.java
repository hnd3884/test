package com.me.devicemanagement.framework.server.pushnotification.notifiers.android.fcm;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadManager;
import com.google.android.gcm.server.Sender;
import com.google.android.gcm.server.Result;
import com.me.devicemanagement.framework.server.pushnotification.device.NotificationDevice;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.pushnotification.common.NotificationPriority;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import java.util.List;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.Properties;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.pushnotification.notification.Notification;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;
import com.me.devicemanagement.framework.server.pushnotification.notifiers.android.AndroidNotifier;

public abstract class FCMAndroidNotifier implements AndroidNotifier, SchedulerExecutionInterface
{
    private static final String DEVICE_NOT_REGISTERED = "NotRegistered";
    private static final int FCM_RETRIES = 5;
    private static final String ASYNC_TASK_NAME = "FCM_ANDROID_TASK";
    private static final String NOTIFICATION_TASK_PROP_KEY = "NOTIFICATION_FOR_TASK";
    
    @Override
    public void notify(final Notification notification) throws Exception {
        this.sendToFCM(notification);
    }
    
    @Override
    public void notifyAsync(final Notification notification) throws Exception {
        final HashMap<String, Object> taskInfoMap = new HashMap<String, Object>();
        taskInfoMap.put("taskName", "FCM_ANDROID_TASK");
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
            if (notification.getAndroidDeviceTokens() != null && notification.getAndroidDeviceTokens().size() > 0) {
                final MulticastResult results = this.getProxySender().send(this.getMessage(notification), (List)notification.getAndroidDeviceTokens(), 5);
                this.processResult(notification, results, notification.getAndroidDevices());
            }
        }
        catch (final Exception ex) {}
    }
    
    private Message getMessage(final Notification notification) {
        final Message.Builder builder = new Message.Builder();
        builder.addData("title", notification.getInfo().getTitle());
        builder.addData("message", notification.getInfo().getMessage());
        if (notification.getPriority() == NotificationPriority.HIGH) {
            builder.priority(Message.Priority.HIGH);
        }
        else {
            builder.priority(Message.Priority.NORMAL);
        }
        if (notification.getInfo().getSound() != null) {
            builder.addData("sound", notification.getInfo().getSound());
        }
        if (notification.getInfo().getCollapseID() != null) {
            builder.collapseKey(notification.getInfo().getCollapseID());
        }
        builder.addData("time", Long.toString(notification.getTime()));
        if (notification.getInfo().getPayload() != null) {
            builder.addData("payload", notification.getInfo().getPayload().toString());
        }
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
    
    private Sender getProxySender() throws Exception {
        ProxySender proxySender = null;
        final Properties proxyDetails = ApiFactoryProvider.getServerSettingsAPI().getProxyConfiguration();
        final DownloadManager downloadMgr = DownloadManager.getInstance();
        final int proxyType = DownloadManager.proxyType;
        if (proxyType == 4) {
            final String url = "1";
            final Properties pacProps = ApiFactoryProvider.getServerSettingsAPI().getProxyConfiguration(url, proxyDetails);
            proxySender = new ProxySender(this.getFcmKey(), pacProps);
        }
        else {
            proxySender = new ProxySender(this.getFcmKey(), proxyDetails);
        }
        return proxySender;
    }
    
    protected abstract String getFcmKey();
}
