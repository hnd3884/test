package com.me.mdm.server.ios.pushNotification;

import java.util.Properties;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadManager;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.google.android.gcm.server.Sender;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import java.util.logging.Level;
import java.util.List;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Notification;
import org.json.JSONObject;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import java.util.logging.Logger;
import com.me.mdm.server.notification.pushnotification.FCMPushNotificationProcessor;

public class IosFCMPushNotificationProcessor extends FCMPushNotificationProcessor
{
    private static Logger logger;
    
    @Override
    public String getNotificationServerKey() throws Exception {
        return MDMApiFactoryProvider.getSecureKeyProviderAPI().getSecret("FCMAPIKey");
    }
    
    @Override
    public void buildMessage(final JSONObject messageJSON) {
        final Notification.Builder iOSNotificationBuilder = new Notification.Builder("success_icon");
        final Message.Builder builder = new Message.Builder();
        final int messageType = messageJSON.getInt("MESSAGE_TYPE");
        if (messageType == 1) {
            builder.addData("title_loc_key", (String)messageJSON.get("I18N_TITLE"));
            builder.addData("body_loc_key", (String)messageJSON.get("I18N_MESSAGE"));
            iOSNotificationBuilder.titleLocKey((String)messageJSON.get("I18N_TITLE"));
            iOSNotificationBuilder.bodyLocKey(messageJSON.get("I18N_MESSAGE").toString());
            if (messageJSON.opt("I18N_TITLE_ARGS") != null) {
                builder.addData("title_loc_args", (String)messageJSON.get("I18N_TITLE_ARGS"));
                final List arg = JSONUtil.convertJSONArrayToList(messageJSON.getJSONArray("I18N_TITLE_ARGS"));
                iOSNotificationBuilder.titleLocArgs(arg);
            }
            if (messageJSON.opt("I18N_MESSAGE_ARGS") != null) {
                builder.addData("body_loc_args", (String)messageJSON.get("I18N_MESSAGE_ARGS"));
                final List arg = JSONUtil.convertJSONArrayToList(messageJSON.getJSONArray("I18N_MESSAGE_ARGS"));
                iOSNotificationBuilder.bodyLocArgs(arg);
            }
        }
        else {
            builder.addData("title", (String)messageJSON.get("TITLE"));
            builder.addData("message", (String)messageJSON.get("BODY"));
            iOSNotificationBuilder.title((String)messageJSON.get("TITLE"));
            iOSNotificationBuilder.body((String)messageJSON.get("BODY"));
        }
        builder.priority(Message.Priority.HIGH);
        builder.addData("time", Long.toString(System.currentTimeMillis()));
        final JSONObject customDataObject = messageJSON.optJSONObject("AppNotificationMessageCustomDetails");
        if (customDataObject != null) {
            builder.addData("payload", customDataObject.toString());
        }
        iOSNotificationBuilder.badge(0);
        builder.notification(iOSNotificationBuilder.build());
        builder.contentAvailable(Boolean.valueOf(true));
        this.message = builder.build();
    }
    
    @Override
    public void reRegisterNotificationList(final List<Long> resourceList) {
        IosFCMPushNotificationProcessor.logger.log(Level.INFO, "Adding Re fetch notification for resource:{0}", new Object[] { resourceList });
        DeviceCommandRepository.getInstance().addGCMReRegisterCommand(resourceList, 2);
    }
    
    @Override
    public Sender getFCMSender() throws Exception {
        MDMApnsSender apnsSender = null;
        final Properties proxyDetails = ApiFactoryProvider.getServerSettingsAPI().getProxyConfiguration();
        final DownloadManager downloadMgr = DownloadManager.getInstance();
        final int proxyType = DownloadManager.proxyType;
        if (proxyType == 4) {
            final String url = "https://fcm.googleapis.com/fcm/send";
            final Properties pacProps = ApiFactoryProvider.getServerSettingsAPI().getProxyConfiguration(url, proxyDetails);
            apnsSender = new MDMApnsSender(this.getNotificationServerKey(), pacProps);
        }
        else {
            apnsSender = new MDMApnsSender(this.getNotificationServerKey(), proxyDetails);
        }
        return apnsSender;
    }
    
    static {
        IosFCMPushNotificationProcessor.logger = Logger.getLogger("MDMAnnouncementLogger");
    }
}
