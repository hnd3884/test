package com.me.mdm.server.notification.pushnotification;

import java.util.Properties;
import com.adventnet.sym.server.mdm.android.ProxySender;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadManager;
import com.google.android.gcm.server.Sender;
import org.json.JSONObject;
import java.util.Iterator;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Result;
import java.util.List;
import java.util.logging.Level;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;
import com.google.android.gcm.server.Message;

public abstract class FCMPushNotificationProcessor extends AppNotificationProcessor
{
    protected Message message;
    
    public abstract String getNotificationServerKey() throws Exception;
    
    @Override
    public HashMap pushMessageNotification(final HashMap notificationMap) {
        try {
            final List<String> deviceToken = new ArrayList<String>(notificationMap.values());
            final List<Long> resourceList = new ArrayList<Long>(notificationMap.keySet());
            this.logger.log(Level.INFO, "Going to send message to resourceId:{0}", new Object[] { resourceList });
            final MulticastResult result = this.getFCMSender().send(this.message, (List)deviceToken, 5);
            return this.processMulticastResult(result.getResults(), resourceList);
        }
        catch (final Exception e) {
            final HashMap failureMap = new HashMap();
            failureMap.put("NotificationFailureList", new ArrayList(notificationMap.keySet()));
            return failureMap;
        }
    }
    
    protected HashMap processMulticastResult(final List<Result> resultList, final List resourceId) throws Exception {
        final HashMap resMap = new HashMap();
        final List sucess = new ArrayList();
        final List failure = new ArrayList();
        final List notRegister = new ArrayList();
        final Iterator resultIter = resultList.iterator();
        int count = 0;
        while (resultIter.hasNext()) {
            final Result result = resultIter.next();
            String res = result.getMessageId();
            if (res == null) {
                res = result.getErrorCodeName();
                if (res.equalsIgnoreCase("NotRegistered") && !ApiFactoryProvider.getDemoUtilAPI().isDemoMode()) {
                    notRegister.add(resourceId.get(count));
                    sucess.add(resourceId.get(count));
                }
                else {
                    failure.add(resourceId.get(count));
                }
            }
            else {
                sucess.add(resourceId.get(count));
            }
            ++count;
        }
        resMap.put("NotificationSuccessList", sucess);
        resMap.put("NotificationFailureList", failure);
        resMap.put("NotificationReRegisterList", notRegister);
        this.logger.log(Level.INFO, "Processed multicast result:{0}", new Object[] { resMap });
        return resMap;
    }
    
    @Override
    public void buildMessage(final JSONObject messageJSON) {
        final Message.Builder builder = new Message.Builder();
        final int messageType = messageJSON.getInt("TYPE");
        if (messageType == 1) {
            builder.addData("title_loc_key", (String)messageJSON.get("TITLE_KEY"));
            builder.addData("body_loc_key", (String)messageJSON.get("MESSAGE_KEY"));
            if (messageJSON.opt("TITLE_ARGS") != null) {
                builder.addData("title_loc_args", (String)messageJSON.get("TITLE_ARGS"));
            }
            if (messageJSON.opt("MESSAGE_ARGS") != null) {
                builder.addData("body_loc_args", (String)messageJSON.get("MESSAGE_ARGS"));
            }
            builder.addData("message", (String)messageJSON.get("MESSAGE"));
        }
        else {
            builder.addData("title", (String)messageJSON.get("TITLE"));
            builder.addData("message", (String)messageJSON.get("MESSAGE"));
        }
        builder.priority(Message.Priority.NORMAL);
        builder.addData("time", Long.toString(System.currentTimeMillis()));
        builder.contentAvailable(Boolean.valueOf(true));
        this.message = builder.build();
    }
    
    @Override
    public void failureNotificationList(final List<Long> resourceList) {
    }
    
    public Sender getFCMSender() throws Exception {
        final Properties proxyDetails = ApiFactoryProvider.getServerSettingsAPI().getProxyConfiguration();
        ProxySender proxySender = null;
        final DownloadManager downloadMgr = DownloadManager.getInstance();
        final int proxyType = DownloadManager.proxyType;
        if (proxyType == 4) {
            final String url = "https://fcm.googleapis.com/fcm/send";
            final Properties pacProps = ApiFactoryProvider.getServerSettingsAPI().getProxyConfiguration(url, proxyDetails);
            proxySender = new ProxySender(this.getNotificationServerKey(), pacProps);
        }
        else {
            proxySender = new ProxySender(this.getNotificationServerKey(), proxyDetails);
        }
        return proxySender;
    }
}
