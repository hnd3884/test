package com.adventnet.sym.server.mdm.android;

import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import java.util.Map;
import com.google.android.gcm.server.MulticastResult;
import java.util.Properties;
import com.adventnet.sym.server.mdm.message.MDMMessageHandler;
import java.io.IOException;
import java.net.ConnectException;
import com.google.android.gcm.server.InvalidRequestException;
import com.google.android.gcm.server.Message;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadManager;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import java.util.Iterator;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.HashMap;
import com.google.android.gcm.server.Result;
import org.json.JSONObject;
import java.sql.SQLException;
import java.util.Collection;
import java.util.logging.Level;
import com.me.mdm.server.notification.PushNotificationHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import com.me.mdm.server.notification.WakeUpProcessor;

public class WakeUpAndroidDevice extends WakeUpProcessor
{
    public static final String COLLAPSE_KEY = "dc_mdm_android";
    public static final int GCM_RETRIES = 5;
    public final String gcmWakeupTimestamp = "WakeupTimestamp";
    private static WakeUpAndroidDevice wakedevice;
    public static Logger logger;
    public Logger accesslogger;
    String separator;
    
    public WakeUpAndroidDevice() {
        this.accesslogger = Logger.getLogger("MDMWakupReqLogger");
        this.separator = "\t";
    }
    
    public static WakeUpAndroidDevice getInstance() {
        if (WakeUpAndroidDevice.wakedevice == null) {
            WakeUpAndroidDevice.wakedevice = new WakeUpAndroidDevice();
        }
        return WakeUpAndroidDevice.wakedevice;
    }
    
    private List<String> getRegistrtionList(final List resourceList, final int notificationType) throws SQLException {
        final List<String> registrationList = new ArrayList<String>();
        final List<Long> resourcesWithoutCommDetails = new ArrayList<Long>();
        try {
            for (int i = 0; i < resourceList.size(); ++i) {
                final Long resourceId = resourceList.get(i);
                final JSONObject communicationProps = PushNotificationHandler.getInstance().getNotificationDetails(resourceId, notificationType);
                if (communicationProps != null) {
                    final String deviceToken = String.valueOf(communicationProps.get("NOTIFICATION_TOKEN_ENCRYPTED"));
                    if (deviceToken == null || (deviceToken != null && deviceToken.equals("--"))) {
                        resourcesWithoutCommDetails.add(resourceId);
                    }
                    else {
                        registrationList.add(deviceToken);
                    }
                }
            }
            WakeUpAndroidDevice.logger.log(Level.SEVERE, "Resources with no communicaiton details {0}", resourcesWithoutCommDetails);
            resourceList.removeAll(resourcesWithoutCommDetails);
        }
        catch (final Exception ex) {
            WakeUpAndroidDevice.logger.log(Level.SEVERE, "Exception while getting registration list", ex);
            throw new SQLException();
        }
        return registrationList;
    }
    
    private HashMap processMulticastResult(final List<Result> resultList, final List resourceId, final Integer notificationType) throws Exception {
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
                final String resourceName = ManagedDeviceHandler.getInstance().getDeviceName(resourceId.get(count));
                final String accessMessage = "WAKEUP: " + this.separator + resourceId.get(count) + this.separator + resourceName + this.separator + "FAILURE" + this.separator + res;
                this.accesslogger.log(Level.INFO, accessMessage);
            }
            else {
                sucess.add(resourceId.get(count));
                res = result.getCanonicalRegistrationId();
                if (res != null) {
                    final JSONObject hAndroidCommMap = new JSONObject();
                    hAndroidCommMap.put("NOTIFICATION_TOKEN_ENCRYPTED", (Object)res);
                    PushNotificationHandler.getInstance().addOrUpdateManagedIdToNotificationRel(resourceId.get(count), notificationType, hAndroidCommMap);
                }
                final String resourceName = ManagedDeviceHandler.getInstance().getDeviceName(resourceId.get(count));
                final String accessMessage = "WAKEUP: " + this.separator + notificationType + this.separator + resourceId.get(count) + this.separator + resourceName + this.separator + "SUCCESS";
                this.accesslogger.log(Level.INFO, accessMessage);
            }
            ++count;
        }
        resMap.put("NotificationSuccessList", sucess);
        resMap.put("NotificationFailureList", failure);
        resMap.put("NotificationReRegisterList", notRegister);
        return resMap;
    }
    
    @Override
    public HashMap wakeUpDevices(final List resourceList, final int notificationType) {
        this.platform = 2;
        HashMap retHash = new HashMap();
        List notificationSuccessList = new ArrayList();
        List notificationFailureList = new ArrayList();
        final List notificationCompleteList = new ArrayList(resourceList);
        List reRegisterList = new ArrayList();
        int er = 2;
        try {
            final String myApiKey = MDMApiFactoryProvider.getSecureKeyProviderAPI().getSecret("GCMAPIKey");
            final List<String> registrationList = this.getRegistrtionList(resourceList, notificationType);
            final Properties proxyDetails = ApiFactoryProvider.getServerSettingsAPI().getProxyConfiguration();
            ProxySender proxySender = null;
            final DownloadManager downloadMgr = DownloadManager.getInstance();
            final int proxyType = DownloadManager.proxyType;
            if (proxyType == 4) {
                final String url = "https://fcm.googleapis.com/fcm/send";
                final Properties pacProps = ApiFactoryProvider.getServerSettingsAPI().getProxyConfiguration(url, proxyDetails);
                proxySender = new ProxySender(myApiKey, pacProps);
            }
            else {
                proxySender = new ProxySender(myApiKey, proxyDetails);
            }
            final Message.Builder msgBuilder = new Message.Builder();
            msgBuilder.priority(Message.Priority.HIGH);
            msgBuilder.addData("WakeupTimestamp", String.valueOf(System.currentTimeMillis()));
            final Message message = msgBuilder.collapseKey("dc_mdm_android").build();
            MulticastResult result = null;
            if (!registrationList.isEmpty()) {
                result = proxySender.send(message, (List)registrationList, 5);
            }
            WakeUpAndroidDevice.logger.log(Level.INFO, "FCM wakeup sent");
            if (result != null) {
                WakeUpAndroidDevice.logger.log(Level.INFO, "Result to wake up android device is : {0}", result.toString());
                WakeUpAndroidDevice.logger.log(Level.INFO, "Sucess Count {0}", result.getSuccess());
                WakeUpAndroidDevice.logger.log(Level.INFO, "Fail Count {0}", result.getFailure());
                WakeUpAndroidDevice.logger.log(Level.INFO, "canonical Count {0}", result.getCanonicalIds());
                retHash = this.processMulticastResult(result.getResults(), resourceList, notificationType);
                notificationSuccessList = retHash.get("NotificationSuccessList");
                notificationFailureList = retHash.get("NotificationFailureList");
                reRegisterList = retHash.get("NotificationReRegisterList");
                this.hideGCMUnreachableMessageBox();
            }
            else {
                WakeUpAndroidDevice.logger.log(Level.WARNING, "Result for wakeup null");
            }
        }
        catch (final IllegalArgumentException illEx) {
            er = 2;
            notificationSuccessList = new ArrayList();
            notificationFailureList = new ArrayList(notificationCompleteList);
            WakeUpAndroidDevice.logger.log(Level.WARNING, "Registration id may null so exception occurred while sending request to client ..", illEx);
        }
        catch (final InvalidRequestException reqEx) {
            er = 2;
            notificationSuccessList = new ArrayList();
            notificationFailureList = new ArrayList(notificationCompleteList);
            WakeUpAndroidDevice.logger.log(Level.WARNING, "GCM didn't returned a 200 or 5xx status so exception occurred while sending request to client ..", (Throwable)reqEx);
        }
        catch (final ConnectException conEx) {
            er = 1;
            notificationSuccessList = new ArrayList();
            notificationFailureList = new ArrayList(notificationCompleteList);
            WakeUpAndroidDevice.logger.log(Level.WARNING, "Connection Exception occurred We can not sent message ..", conEx);
            this.showGCMURLBlockMsgBox();
        }
        catch (final IOException ioEx) {
            er = 1;
            notificationSuccessList = new ArrayList();
            notificationFailureList = new ArrayList(notificationCompleteList);
            WakeUpAndroidDevice.logger.log(Level.WARNING, "Message could not be sent so exception occurred while sending request to client ..", ioEx);
            this.showGCMProxyAuthFailureMsgBox();
        }
        catch (final Exception ex) {
            er = 2;
            notificationSuccessList = new ArrayList();
            notificationFailureList = new ArrayList(notificationCompleteList);
            WakeUpAndroidDevice.logger.log(Level.WARNING, "Exception while sending request to client .. ", ex);
            MDMMessageHandler.getInstance().messageAction("PROXY_NOT_CONFIGURED", null);
        }
        finally {
            final Map notificationData = new HashMap();
            notificationData.put("NotificationSuccessList", notificationSuccessList);
            notificationData.put("NotificationFailureList", notificationFailureList);
            notificationData.put("NotificationCompleteList", notificationCompleteList);
            notificationData.put("NotificationReRegisterList", reRegisterList);
            notificationData.put("NotificationError", er);
            notificationData.put("NotificationType", notificationType);
            this.processWakeUpResults(notificationData);
        }
        return retHash;
    }
    
    private void hideGCMUnreachableMessageBox() {
        MessageProvider.getInstance().hideMessage("GCM_PORT_OR_DOMAIN_BLOCK");
        MessageProvider.getInstance().hideMessage("GCM_URL_PROXY_AUTH_FAILED");
        MessageProvider.getInstance().hideMessage("GCM_WAKEUP_FAILED_CONTACT_SUPPORT");
    }
    
    private void showGCMProxyAuthFailureMsgBox() {
        MessageProvider.getInstance().hideMessage("GCM_PORT_OR_DOMAIN_BLOCK");
        MessageProvider.getInstance().unhideMessage("GCM_URL_PROXY_AUTH_FAILED");
    }
    
    private void showGCMURLBlockMsgBox() {
        MessageProvider.getInstance().unhideMessage("GCM_PORT_OR_DOMAIN_BLOCK");
        MessageProvider.getInstance().hideMessage("GCM_URL_PROXY_AUTH_FAILED");
    }
    
    @Override
    protected void reRegisterPushNotificationToken(final List reRegResourceList, final int notificationType) {
        WakeUpAndroidDevice.logger.log(Level.INFO, "GCM returned NotRegister for the resource ID {0}", reRegResourceList);
        WakeUpAndroidDevice.logger.log(Level.INFO, "GCM Register Command Added for the resource id's");
        if (notificationType == 2) {
            DeviceCommandRepository.getInstance().addGCMReRegisterCommand(reRegResourceList, 1);
        }
        else if (notificationType == 201) {
            DeviceCommandRepository.getInstance().addGCMReRegisterCommand(reRegResourceList, 2);
        }
    }
    
    static {
        WakeUpAndroidDevice.wakedevice = null;
        WakeUpAndroidDevice.logger = Logger.getLogger("MDMLogger");
    }
}
