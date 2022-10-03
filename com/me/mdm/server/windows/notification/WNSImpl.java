package com.me.mdm.server.windows.notification;

import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import java.util.Map;
import org.json.JSONObject;
import com.me.mdm.server.metracker.METrackParamManager;
import com.me.mdm.server.notification.PushNotificationHandler;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.net.ConnectException;
import java.io.IOException;
import javax.ws.rs.core.MultivaluedMap;
import org.glassfish.jersey.internal.util.collection.MultivaluedStringMap;
import ar.com.fernandospr.wns.model.WnsNotificationResponse;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import java.util.Properties;
import java.util.logging.Level;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import ar.com.fernandospr.wns.WnsProxyProperties;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadManager;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import ar.com.fernandospr.wns.model.builders.WnsRawBuilder;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import java.util.HashMap;
import ar.com.fernandospr.wns.model.WnsRaw;
import ar.com.fernandospr.wns.WnsService;
import java.util.logging.Logger;
import com.me.mdm.server.notification.WakeUpProcessor;

public class WNSImpl extends WakeUpProcessor
{
    public static final int FAILURE_AT_PROXY = 9999;
    public static final String DUMMY_CHANNEL_URL = "https://sin.notify.windows.com";
    public static Logger logger;
    public static Logger accesslogger;
    private static String separator;
    private static WNSImpl wnsImpl;
    private static WnsService wnsService;
    private static WnsRaw raw;
    private static Boolean isDeviceNotification;
    private static int currentNotificationType;
    boolean isInitialized;
    
    private WNSImpl() {
        this.isInitialized = false;
    }
    
    public static WNSImpl getInstance() {
        if (WNSImpl.wnsImpl == null) {
            WNSImpl.wnsImpl = new WNSImpl();
        }
        return WNSImpl.wnsImpl;
    }
    
    private void initialize() {
        if (!this.isInitialized) {
            try {
                final HashMap<String, String> wakeUpCredentials = MDMApiFactoryProvider.getSecureKeyProviderAPI().getWindowsWakeUpCredentials().get(this.getAppTypeForNotification(WNSImpl.currentNotificationType));
                WNSImpl.raw = new WnsRawBuilder().stream("WakeUp".getBytes()).build();
                final Properties proxyProps = ApiFactoryProvider.getServerSettingsAPI().getProxyConfiguration();
                WnsProxyProperties wnsProxyProps = null;
                if (proxyProps != null) {
                    final DownloadManager downloadMgr = DownloadManager.getInstance();
                    final int proxyType = DownloadManager.proxyType;
                    if (proxyType == 4) {
                        final Properties pacProps = ApiFactoryProvider.getServerSettingsAPI().getProxyConfiguration("https://sin.notify.windows.com", proxyProps);
                        wnsProxyProps = new WnsProxyProperties(pacProps.getProperty("proxyProtocol", "http"), pacProps.getProperty("proxyHost"), Integer.parseInt(pacProps.getProperty("proxyPort", "8080")), pacProps.getProperty("proxyUser"), pacProps.getProperty("proxyPass"));
                    }
                    else {
                        wnsProxyProps = new WnsProxyProperties(proxyProps.getProperty("proxyProtocol", "http"), proxyProps.getProperty("proxyHost"), Integer.parseInt(proxyProps.getProperty("proxyPort", "8080")), proxyProps.getProperty("proxyUser"), proxyProps.getProperty("proxyPass"));
                    }
                }
                WNSImpl.wnsService = new WnsService((String)wakeUpCredentials.get("PACKAGE_SID"), (String)wakeUpCredentials.get("CLIENT_SECRET"), wnsProxyProps, false);
                if (WNSImpl.wnsService != null) {
                    this.isInitialized = true;
                    if (this.isWnsReachable()) {
                        MessageProvider.getInstance().hideMessage("WNS_WAKEUP_FAILED_CONTACT_SUPPORT");
                    }
                    else {
                        MessageProvider.getInstance().unhideMessage("WNS_WAKEUP_FAILED_CONTACT_SUPPORT");
                    }
                    WNSImpl.logger.log(Level.INFO, "WnsService initialized");
                }
                else {
                    WNSImpl.logger.log(Level.WARNING, "WnsService is not inititalized..Possible Proxy Configuration error..");
                }
            }
            catch (final Exception ex) {
                Logger.getLogger(WNSImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void reinitialize() {
        this.isInitialized = false;
        WNSImpl.isDeviceNotification = Boolean.FALSE;
        if (ManagedDeviceHandler.getInstance().getWindowsManagedDeviceCount() > 0) {
            WNSImpl.isDeviceNotification = Boolean.TRUE;
        }
        this.initialize();
    }
    
    private WnsNotificationResponse wakeUpWindowsDevice(final String sChannelUri, final int notificationType) {
        if (notificationType != WNSImpl.currentNotificationType) {
            WNSImpl.currentNotificationType = notificationType;
            this.isInitialized = false;
        }
        if (!this.isInitialized) {
            this.initialize();
        }
        try {
            if (this.isInitialized) {
                return WNSImpl.wnsService.pushRaw(sChannelUri, WNSImpl.raw);
            }
            return new WnsNotificationResponse(sChannelUri, 9999, (MultivaluedMap)new MultivaluedStringMap());
        }
        catch (final Exception ex) {
            WNSImpl.logger.log(Level.SEVERE, "Exception in WNSImpl.wakeUpWindowsDevice", ex);
            if (ex.getCause().getCause() instanceof IOException) {
                final MultivaluedStringMap headers = new MultivaluedStringMap();
                headers.putSingle((Object)"X-WNS-Error-Description", (Object)"Proxy authentication failed");
                return new WnsNotificationResponse(sChannelUri, 9999, (MultivaluedMap)headers);
            }
            if (ex.getCause() instanceof ConnectException) {
                final MultivaluedStringMap headers = new MultivaluedStringMap();
                headers.putSingle((Object)"X-WNS-Error-Description", (Object)"Proxy error, url blocked by proxy");
                return new WnsNotificationResponse(sChannelUri, 9999, (MultivaluedMap)headers);
            }
        }
        catch (final Throwable throwable) {
            WNSImpl.logger.log(Level.SEVERE, throwable.toString());
        }
        return null;
    }
    
    public boolean isWnsReachable() {
        final String dummyChannelUri = "https://sin.notify.windows.com/?token=AgYAAAAb1WJI8Vhmz1RF0VWp%2bgJok2kt%2bP1T4mqE7kBP3Be1xDYjsXGTENpxh1q68JDgbr38%2bBY%2bjT8mv1P8vZUluyYgG2yqsosz6x5jxAYLFxnrOtsyO1%2bcXRxIT%2fG%2bT5Lsu3U%3d";
        final WnsNotificationResponse wnsResponse = this.wakeUpWindowsDevice(dummyChannelUri, WNSImpl.currentNotificationType);
        return this.wnsWakeUpMessageHandler(wnsResponse);
    }
    
    private boolean wnsWakeUpMessageHandler(final WnsNotificationResponse wnsResponse) {
        boolean retVal = Boolean.TRUE;
        if (wnsResponse == null) {
            retVal = Boolean.FALSE;
        }
        else if (wnsResponse.code == 9999 && WNSImpl.isDeviceNotification) {
            if (wnsResponse.errorDescription.contains("Proxy authentication failed")) {
                MessageProvider.getInstance().unhideMessage("WNS_URL_PROXY_AUTH_FAILED");
                MessageProvider.getInstance().hideMessage("WNS_URL_BLOCKED");
            }
            else {
                MessageProvider.getInstance().unhideMessage("WNS_URL_BLOCKED");
                MessageProvider.getInstance().hideMessage("WNS_URL_PROXY_AUTH_FAILED");
            }
        }
        else {
            MessageProvider.getInstance().hideMessage("WNS_URL_BLOCKED");
            MessageProvider.getInstance().hideMessage("WNS_URL_PROXY_AUTH_FAILED");
        }
        return retVal;
    }
    
    @Override
    public HashMap wakeUpDevices(final List resourceList, final int notificationType) {
        this.platform = 3;
        WNSImpl.isDeviceNotification = Boolean.TRUE;
        WNSImpl.logger.log(Level.INFO, "Inside wakeUpWindowsDevices List{0} ", resourceList);
        final HashMap retHash = new HashMap();
        final List notificationSuccessList = new ArrayList();
        List notificationFailureList = new ArrayList();
        final List notificationCompleteList = new ArrayList(resourceList);
        final List reRegisterList = new ArrayList();
        int err = -1;
        try {
            for (int i = 0; i < resourceList.size(); ++i) {
                final Long resourceId = (Long)resourceList.get(i);
                final JSONObject communicationProps = PushNotificationHandler.getInstance().getNotificationDetails(resourceId, notificationType);
                if (communicationProps != null) {
                    final String sChannelUri = (String)communicationProps.get("NOTIFICATION_TOKEN_ENCRYPTED");
                    String sWakeStatus;
                    String sFailureMsg;
                    if (sChannelUri.contains("https://")) {
                        final WnsNotificationResponse notification = this.wakeUpWindowsDevice(sChannelUri, notificationType);
                        if (notification != null) {
                            WNSImpl.logger.log(Level.INFO, "Response Code : {0} , errorDescription : {1} , debugTrace : {2} , deviceConnectionStatus : {3}  ", new Object[] { notification.code, notification.errorDescription, notification.debugTrace, notification.deviceConnectionStatus });
                            WNSImpl.logger.log(Level.FINE, "Channel URI : {0} ", new Object[] { notification.channelUri });
                            if (notification.code == 200 && notification.notificationStatus.contains("received")) {
                                notificationSuccessList.add(resourceId);
                                sWakeStatus = "SUCCEEDED";
                                sFailureMsg = "";
                                MessageProvider.getInstance().hideMessage("WNS_URL_BLOCKED");
                                MessageProvider.getInstance().hideMessage("WNS_WAKEUP_FAILED_CONTACT_SUPPORT");
                                MessageProvider.getInstance().hideMessage("WNS_URL_PROXY_AUTH_FAILED");
                            }
                            else {
                                sWakeStatus = "FAILED";
                                if (notification.code == 9999 && WNSImpl.isDeviceNotification) {
                                    sFailureMsg = notification.errorDescription;
                                    if (sFailureMsg.contains("Proxy authentication failed ")) {
                                        MessageProvider.getInstance().unhideMessage("WNS_URL_PROXY_AUTH_FAILED");
                                        MessageProvider.getInstance().hideMessage("WNS_URL_BLOCKED");
                                    }
                                    else {
                                        MessageProvider.getInstance().unhideMessage("WNS_URL_BLOCKED");
                                        MessageProvider.getInstance().hideMessage("WNS_URL_PROXY_AUTH_FAILED");
                                    }
                                }
                                else if (notification.code == 410 || notification.code == 404 || notification.code == 403) {
                                    reRegisterList.add(resourceId);
                                    sFailureMsg = "Wns Channel URI not recognized or expired. Getting new channel URI in next mgmt session";
                                }
                                else {
                                    sFailureMsg = notification.errorDescription;
                                    if (WNSImpl.isDeviceNotification && (notification.code == 400 || notification.code == 405 || notification.code == 413)) {
                                        MessageProvider.getInstance().unhideMessage("WNS_WAKEUP_FAILED_CONTACT_SUPPORT");
                                    }
                                }
                                WNSImpl.logger.log(Level.INFO, "wakeUpWindowsDevices: Notifcation is failed {0}", resourceId);
                                WNSImpl.logger.log(Level.INFO, "wakeUpWindowsDevices: Notifcation is failed: Error Message-> {0}", sFailureMsg);
                                notificationFailureList.add(resourceId);
                            }
                        }
                        else {
                            notificationFailureList.add(resourceId);
                            sWakeStatus = "FAILED";
                            sFailureMsg = "Notification Response Null";
                        }
                    }
                    else {
                        notificationFailureList.add(resourceId);
                        reRegisterList.add(resourceId);
                        sWakeStatus = "FAILED";
                        sFailureMsg = "Channel URI not in proper format";
                    }
                    final String notificationTypeName = (notificationType == 3) ? "WindowsMDMClientNotificaiton" : "WindowsNativeAppNotification";
                    final String resourceName = ManagedDeviceHandler.getInstance().getDeviceName(resourceId);
                    final String accessMessage = "WAKEUP: " + WNSImpl.separator + resourceId + WNSImpl.separator + resourceName + WNSImpl.separator + sWakeStatus + WNSImpl.separator + notificationTypeName + WNSImpl.separator + sFailureMsg;
                    WNSImpl.accesslogger.log(Level.INFO, accessMessage);
                }
                else {
                    notificationFailureList.add(resourceId);
                    reRegisterList.add(resourceId);
                }
            }
            retHash.put("success", notificationSuccessList);
            retHash.put("failure", notificationFailureList);
        }
        catch (final IOException ioe) {
            WNSImpl.logger.log(Level.SEVERE, "IOException occured in WakeUpWindowsDevice", ioe);
            this.isInitialized = false;
            err = 1;
            resourceList.removeAll(notificationSuccessList);
            notificationFailureList = resourceList;
        }
        catch (final Exception ex) {
            WNSImpl.logger.log(Level.SEVERE, "Exception occured in WakeUpWindowsDevice", ex);
            this.isInitialized = false;
            err = 2;
            resourceList.removeAll(notificationSuccessList);
            notificationFailureList = resourceList;
        }
        finally {
            METrackParamManager.incrementMETrackParams("Win_Success_Notification_Count", notificationSuccessList.size());
            final Map notificationData = new HashMap();
            notificationData.put("NotificationSuccessList", notificationSuccessList);
            notificationData.put("NotificationFailureList", notificationFailureList);
            notificationData.put("NotificationCompleteList", notificationCompleteList);
            notificationData.put("NotificationReRegisterList", reRegisterList);
            notificationData.put("NotificationError", err);
            notificationData.put("NotificationType", notificationType);
            this.processWakeUpResults(notificationData);
        }
        WNSImpl.logger.log(Level.INFO, "Returning from wakeUpWindowsDevices List{0} ", resourceList);
        return retHash;
    }
    
    private int getAppTypeForNotification(final int notificationType) {
        int appType = -1;
        if (notificationType == 3) {
            appType = 1;
        }
        else if (notificationType == 303) {
            appType = 2;
        }
        return appType;
    }
    
    @Override
    protected void reRegisterPushNotificationToken(final List reRegResourceList, final int notificationType) {
        if (notificationType == 3) {
            DeviceCommandRepository.getInstance().addDeviceCommunicationCommand(reRegResourceList);
        }
        else {
            DeviceCommandRepository.getInstance().addNativeAppChannelUriCommand(reRegResourceList);
        }
    }
    
    static {
        WNSImpl.logger = Logger.getLogger("MDMLogger");
        WNSImpl.accesslogger = Logger.getLogger("MDMWakupReqLogger");
        WNSImpl.separator = "\t";
        WNSImpl.wnsImpl = null;
        WNSImpl.wnsService = null;
        WNSImpl.raw = null;
        WNSImpl.isDeviceNotification = Boolean.FALSE;
        WNSImpl.currentNotificationType = 3;
    }
}
