package com.me.mdm.server.ios.apns;

import java.util.Hashtable;
import java.util.Map;
import org.json.JSONObject;
import java.io.IOException;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.devicemanagement.framework.server.logger.DMSecurityLogger;
import com.me.mdm.server.notification.PushNotificationHandler;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;
import javapns.notification.PushNotificationPayload;
import java.util.List;
import javapns.Push;
import com.me.mdm.server.enrollment.MDMEnrollmentRequestHandler;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.me.mdm.server.enrollment.notification.EnrollmentNotificationHandler;
import javapns.devices.Device;
import javapns.devices.implementations.basic.BasicDevice;
import javapns.notification.PushedNotification;
import javapns.notification.Payload;
import sun.misc.BASE64Encoder;
import javapns.communication.exceptions.KeystoreException;
import javapns.communication.exceptions.CommunicationException;
import javapns.notification.AppleNotificationServer;
import java.io.InputStream;
import javapns.communication.AppleServer;
import javapns.notification.AppleNotificationServerBasicImpl;
import javapns.notification.PushNotificationManager;
import java.util.HashMap;
import java.io.File;
import java.util.Properties;
import javapns.communication.ProxyManager;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadManager;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.ios.APNSImpl;

@Deprecated
public class APNsLegacyWakeUpProcessor extends APNSImpl
{
    public Logger logger;
    public Logger accesslogger;
    String separator;
    private static APNsLegacyWakeUpProcessor apnsImpl;
    boolean production;
    String proxyHost;
    String proxyPort;
    String proxyUserName;
    String proxyPassword;
    int apns_timeout;
    public static final String CERT_FILE_NAME = "CERT_FILE_NAME";
    public static final String CERT_PASSWORD = "CERT_PASSWORD";
    boolean isInitialized;
    
    private APNsLegacyWakeUpProcessor() {
        this.logger = Logger.getLogger("MDMLogger");
        this.accesslogger = Logger.getLogger("MDMWakupReqLogger");
        this.separator = "\t";
        this.production = true;
        this.proxyHost = null;
        this.proxyPort = null;
        this.proxyUserName = null;
        this.proxyPassword = null;
        this.apns_timeout = 604800;
        this.isInitialized = false;
    }
    
    public static APNsLegacyWakeUpProcessor getInstance() {
        if (APNsLegacyWakeUpProcessor.apnsImpl == null) {
            APNsLegacyWakeUpProcessor.apnsImpl = new APNsLegacyWakeUpProcessor();
        }
        return APNsLegacyWakeUpProcessor.apnsImpl;
    }
    
    private void initialize() {
        try {
            if (!this.isInitialized) {
                final String apns_timeout_str = MDMUtil.getSyMParameter("APNS_TIMEOUT");
                if (apns_timeout_str != null && !apns_timeout_str.equals("")) {
                    this.apns_timeout = Integer.parseInt(apns_timeout_str);
                }
                final Properties proxyDetails = ApiFactoryProvider.getServerSettingsAPI().getProxyConfiguration();
                if (proxyDetails != null && MDMApiFactoryProvider.getMDMUtilAPI().useProxyForApns("APNSv1")) {
                    final DownloadManager downloadMgr = DownloadManager.getInstance();
                    final int proxyType = DownloadManager.proxyType;
                    if (proxyType == 4) {
                        final String url = "http://17.0.0.0:5223";
                        final Properties pacProps = ApiFactoryProvider.getServerSettingsAPI().getProxyConfiguration(url, proxyDetails);
                        this.proxyHost = ((Hashtable<K, String>)pacProps).get("proxyHost");
                        this.proxyPort = ((Hashtable<K, String>)pacProps).get("proxyPort");
                    }
                    else {
                        this.proxyHost = ((Hashtable<K, String>)proxyDetails).get("proxyHost");
                        this.proxyPort = ((Hashtable<K, String>)proxyDetails).get("proxyPort");
                    }
                    this.proxyUserName = ((Hashtable<K, String>)proxyDetails).get("proxyUser");
                    this.proxyPassword = ((Hashtable<K, String>)proxyDetails).get("proxyPass");
                    if (this.proxyHost != null && this.proxyPort != null) {
                        this.logger.log(Level.INFO, "Wakeup using proxy");
                        ProxyManager.setProxy(this.proxyHost, this.proxyPort);
                    }
                    else {
                        ProxyManager.clearProxy();
                    }
                    if (this.proxyUserName != null && this.proxyPassword != null) {
                        ProxyManager.setProxyBasicAuthorization(this.proxyUserName, this.proxyPassword);
                    }
                    else {
                        ProxyManager.clearProxyAuthorization();
                    }
                }
                this.isInitialized = true;
            }
            this.initializeCertFile();
        }
        catch (final Exception ex) {
            this.logger.log(Level.INFO, "Exception occurred while fetching APNS details from DB..", ex);
        }
    }
    
    private void initializeCertFile() {
        try {
            final HashMap apnsCertificateInfo = APNsCertificateHandler.getInstance().getAPNsCertificateInfo();
            if (apnsCertificateInfo.size() > 0) {
                final String certFileName = apnsCertificateInfo.get("CERTIFICATE_FILE_NAME");
                final String certPassword = apnsCertificateInfo.get("CERTIFICATE_PASSWORD");
                final String apnsCertificateFolder = APNsCertificateHandler.getAPNsCertificateFolderPath();
                final String certPath = apnsCertificateFolder + File.separator + certFileName;
                ApiFactoryProvider.getCacheAccessAPI().putCache("CERT_PASSWORD", (Object)certPassword, 2);
                ApiFactoryProvider.getCacheAccessAPI().putCache("CERT_FILE_NAME", (Object)certPath, 2);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.INFO, "Exception occurred while fetching APNS details from DB..", ex);
        }
    }
    
    @Override
    public void reinitialize() {
        this.isInitialized = false;
        this.initialize();
    }
    
    private PushNotificationManager initializeConnection(final String keystore, final String password, final boolean production) throws CommunicationException, KeystoreException, Exception {
        final PushNotificationManager pushManager = new PushNotificationManager();
        final InputStream inputStream = ApiFactoryProvider.getFileAccessAPI().readFile(keystore);
        final AppleNotificationServer server = (AppleNotificationServer)new AppleNotificationServerBasicImpl((Object)inputStream, password, production);
        this.setBasicAuthorization((AppleServer)server);
        pushManager.initializeConnection(server);
        return pushManager;
    }
    
    private void setBasicAuthorization(final AppleServer server) {
        if (this.proxyUserName != null && !this.proxyUserName.equalsIgnoreCase("") && this.proxyPassword != null && !this.proxyPassword.equalsIgnoreCase("")) {
            final BASE64Encoder encoder = new BASE64Encoder();
            final String pwd = this.proxyUserName + ":" + this.proxyPassword;
            final String encodedUserPwd = encoder.encode(pwd.getBytes());
            final String authorization = "Basic " + encodedUserPwd;
            server.setProxyAuthorization(authorization);
            this.logger.log(Level.INFO, "Authentication added.. {0}", server);
        }
    }
    
    private void stopConnection(final PushNotificationManager pushManager) throws Exception {
        if (pushManager != null) {
            pushManager.stopConnection();
        }
    }
    
    private PushedNotification sendNotification(final PushNotificationManager pushManager, final Payload payload, final String token) throws CommunicationException {
        this.logger.log(Level.INFO, "sendNotification");
        PushedNotification notification = null;
        try {
            final BasicDevice device = new BasicDevice();
            device.setToken(token);
            BasicDevice.validateTokenFormat(token);
            notification = pushManager.sendNotification((Device)device, payload, false);
        }
        catch (final CommunicationException comExp) {
            throw comExp;
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception occured in sendNotification", exp);
        }
        return notification;
    }
    
    @Override
    public boolean wakeUpDeviceWithERID(final String deviceToken, final String pushMagic, final String topic, final HashMap hsAdditionalParams) {
        Long enrollmentID = null;
        Long resourceID = null;
        if (hsAdditionalParams.containsKey("ENROLLMENT_REQUEST_ID")) {
            enrollmentID = hsAdditionalParams.get("ENROLLMENT_REQUEST_ID");
        }
        if (hsAdditionalParams.containsKey("RESOURCE_ID")) {
            resourceID = hsAdditionalParams.get("RESOURCE_ID");
        }
        this.logger.log(Level.FINE, "WakeUpIOSDevice()");
        boolean bRet = false;
        try {
            this.initialize();
            final String certPath = (String)ApiFactoryProvider.getCacheAccessAPI().getCache("CERT_FILE_NAME", 2);
            final String certPassword = (String)ApiFactoryProvider.getCacheAccessAPI().getCache("CERT_PASSWORD", 2);
            this.logger.log(Level.FINEST, "WakeUpIOSDevice: deviceToken: {0}", deviceToken);
            this.logger.log(Level.INFO, "WakeUpIOSDevice: pushMagic: {0}", pushMagic);
            this.logger.log(Level.INFO, "WakeUpIOSDevice: erid: {0}", enrollmentID);
            this.logger.log(Level.INFO, "WakeUpIOSDevice: topic: {0}", topic);
            if (enrollmentID != null) {
                hsAdditionalParams.put("STATUS", 0);
                EnrollmentNotificationHandler.getInstance().addOrUpdateNotificationDetails(hsAdditionalParams);
            }
            final Payload mdmPayload = this.createMDMPayload(pushMagic);
            bRet = this.sendAPNSNotification(mdmPayload, certPath, certPassword, this.production, deviceToken);
            if (bRet) {
                MessageProvider.getInstance().hideMessage("IOS_WAKEUP_FAILED_CONTACT_SUPPORT");
            }
            else {
                this.handleWakeUpFailure(enrollmentID, null);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception occured in WakeUpIOSDevice", ex);
            this.handleWakeUpFailure(enrollmentID, ex);
            return bRet;
        }
        this.accesslogger.log(Level.INFO, "ENROLLMENT WAKEUP: {0}{1}{2}{3}{4}{5}{6}{7}", new Object[] { this.separator, enrollmentID, this.separator, pushMagic, this.separator, bRet ? "Success" : "Failed", this.separator, resourceID });
        try {
            if (enrollmentID != null) {}
        }
        catch (final Exception ex) {
            this.logger.severe("Exception occurred while calling EnrollmentWakeUpVerifyTask : " + ex);
        }
        return bRet;
    }
    
    private void handleWakeUpFailure(final Long enrollmentID, final Exception ex) {
        final long localErrorCode = MDMApiFactoryProvider.getAPNSErrorHandler().handleAPNSWakeupFailure(ex);
        if (enrollmentID != null) {
            String errorMsg = "dc.db.mdm.scan.remarks.unable_to_reach_apns";
            if (localErrorCode == 12140L) {
                errorMsg = "mdm.enroll.remarks_wakeupfailed_apns_expired";
            }
            else if (localErrorCode == 12139L) {
                errorMsg = "mdm.enroll.remarks_wakeupfailed_apns_revoked";
            }
            final int errorCode = Integer.parseInt(String.valueOf(localErrorCode));
            MDMEnrollmentRequestHandler.getInstance().updateEnrollmentStatusAndErrorCode(enrollmentID, 0, errorMsg, errorCode);
        }
    }
    
    private Boolean sendAPNSNotification(final Payload payload, final String keystore, final String password, final boolean production, final String token) throws Exception {
        Boolean bRet = false;
        final InputStream inputStream = ApiFactoryProvider.getFileAccessAPI().readFile(keystore);
        final List<PushedNotification> notifications = (List<PushedNotification>)Push.payload(payload, (Object)inputStream, password, production, (Object)token);
        bRet = this.printPushedNotifications(notifications);
        return bRet;
    }
    
    private Payload createMDMPayload(final String pushMagicValue) {
        final PushNotificationPayload complexPayload = PushNotificationPayload.complex();
        try {
            complexPayload.addCustomDictionary("mdm", pushMagicValue);
            complexPayload.setExpiry(this.apns_timeout);
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception occured in createMDMPayload(){0}", e);
        }
        return (Payload)complexPayload;
    }
    
    private Boolean printPushedNotifications(final List<PushedNotification> notifications) throws Exception {
        Boolean bRet = false;
        final List<PushedNotification> failedNotifications = PushedNotification.findFailedNotifications((List)notifications);
        final List<PushedNotification> succesfulNotifications = PushedNotification.findSuccessfulNotifications((List)notifications);
        if (failedNotifications.isEmpty()) {
            this.printPushedNotifications("All notifications pushed successfully (" + succesfulNotifications.size() + "):", succesfulNotifications);
            if (succesfulNotifications.size() > 0) {
                bRet = true;
            }
        }
        else {
            this.printPushedNotifications("Some notifications failed (" + failedNotifications.size() + "):", failedNotifications);
            this.printPushedNotifications("Others succeeded (" + succesfulNotifications.size() + "):", succesfulNotifications);
        }
        if (!bRet) {
            for (final PushedNotification notfiation : notifications) {
                final Exception ex = notfiation.getException();
                if (ex != null) {
                    throw ex;
                }
            }
        }
        return bRet;
    }
    
    private void printPushedNotifications(final String description, final List<PushedNotification> notifications) {
        this.logger.log(Level.INFO, description);
        for (final PushedNotification notification : notifications) {
            try {
                this.logger.log(Level.INFO, "  {0}", notification.toString());
            }
            catch (final Exception e) {
                this.logger.log(Level.WARNING, "Exception occured in printPushedNotifications(){0}", e);
            }
        }
    }
    
    @Override
    public boolean IsAPNsReachacble() {
        boolean bRet = Boolean.TRUE;
        final String deviceToken = "08c9aa01eae6682a93216ec36cadf9d0370b1897839a8005e4a1e28d5d9400ec";
        final String pushMagic = "998F2FA6-4B57-48BE-AB65-97B58978C660";
        bRet = this.wakeUpDeviceWithERID(deviceToken, pushMagic, "", new HashMap());
        return bRet;
    }
    
    @Override
    public HashMap wakeUpDevices(final List resourceList, final int notificationType) {
        this.platform = 1;
        this.logger.log(Level.INFO, "Inside wakeUpIOSDevices List {0}", resourceList);
        final HashMap retHash = new HashMap();
        String sWakeStatus = null;
        String failureMsg = null;
        PushNotificationManager pushManager = null;
        final List notificationSuccessList = new ArrayList();
        List notificationFailureList = new ArrayList();
        final List notificationCompleteList = new ArrayList(resourceList);
        int err = -1;
        try {
            this.initialize();
            final String certPath = (String)ApiFactoryProvider.getCacheAccessAPI().getCache("CERT_FILE_NAME", 2);
            final String certPassword = (String)ApiFactoryProvider.getCacheAccessAPI().getCache("CERT_PASSWORD", 2);
            pushManager = this.initializeConnection(certPath, certPassword, this.production);
            PushedNotification notification = null;
            for (int i = 0; i < resourceList.size(); ++i) {
                final Long resourceId = (Long)resourceList.get(i);
                final JSONObject communicationProps = PushNotificationHandler.getInstance().getNotificationDetails(resourceId, notificationType);
                if (communicationProps != null) {
                    DMSecurityLogger.info(this.logger, "APNsLegacyWakeUpProcessor", "wakeUpIOSDevices", "CommProps {0}", (Object)communicationProps);
                    final String pushMagic = String.valueOf(communicationProps.get("PUSH_MAGIC_ENCRYPTED"));
                    final String deviceToken = String.valueOf(communicationProps.get("NOTIFICATION_TOKEN_ENCRYPTED"));
                    final Payload mdmPayload = this.createMDMPayload(pushMagic);
                    notification = this.sendNotification(pushManager, mdmPayload, deviceToken);
                    this.logger.log(Level.INFO, "wakeUpIOSDevices: sResourceID {0}", resourceId);
                    this.logger.log(Level.INFO, "wakeUpIOSDevices: ManagedDeviceUserId {0}", resourceId);
                    this.logger.log(Level.INFO, "wakeUpIOSDevices: pushMagic {0}", pushMagic);
                    this.logger.log(Level.FINEST, "wakeUpIOSDevices: deviceToken {0}", deviceToken);
                    if (notification != null) {
                        if (notification.isSuccessful()) {
                            this.logger.log(Level.INFO, "wakeUpIOSDevices: Notifcation is succeeded {0}", resourceId);
                            notificationSuccessList.add(resourceId);
                            sWakeStatus = "SUCCEEDED";
                            failureMsg = "";
                        }
                        else {
                            sWakeStatus = "FAILED";
                            failureMsg = ((notification.getResponse() == null) ? "Not Available" : notification.getResponse().getMessage());
                            if (failureMsg == null) {
                                failureMsg = "Not Available";
                            }
                            this.logger.log(Level.INFO, "wakeUpIOSDevices: Notifcation is failed {0}", resourceId);
                            this.logger.log(Level.INFO, "wakeUpIOSDevices: Notifcation is failed: Error Message-> {0}", failureMsg);
                            notificationFailureList.add(resourceId);
                        }
                        final String resourceName = ManagedDeviceHandler.getInstance().getDeviceName(resourceId);
                        final String accessMessage = "WAKEUP: " + this.separator + resourceId + this.separator + resourceName + this.separator + sWakeStatus + this.separator + failureMsg;
                        this.accesslogger.log(Level.INFO, accessMessage);
                    }
                    else {
                        notificationFailureList.add(resourceId);
                    }
                }
            }
            if (notificationFailureList.size() > 0) {
                err = 1;
            }
            MessageProvider.getInstance().hideMessage("IOS_WAKEUP_FAILED_CONTACT_SUPPORT");
        }
        catch (final IOException ioe) {
            this.logger.log(Level.SEVERE, "IOException occured in WakeUpIOSDevice", ioe);
            MDMApiFactoryProvider.getAPNSErrorHandler().handleAPNSWakeupFailure(ioe);
            resourceList.removeAll(notificationSuccessList);
            err = 1;
            notificationFailureList = resourceList;
        }
        catch (final CommunicationException e) {
            this.logger.log(Level.SEVERE, "CommunicationException occured in WakeUpIOSDevice", (Throwable)e);
            MDMApiFactoryProvider.getAPNSErrorHandler().handleAPNSWakeupFailure((Throwable)e);
            resourceList.removeAll(notificationSuccessList);
            err = 1;
            notificationFailureList = resourceList;
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception occured in WakeUpIOSDevice", exp);
            MDMApiFactoryProvider.getAPNSErrorHandler().handleAPNSWakeupFailure(exp);
            resourceList.removeAll(notificationSuccessList);
            err = 2;
            notificationFailureList = resourceList;
        }
        finally {
            try {
                this.stopConnection(pushManager);
            }
            catch (final Exception exp2) {
                this.logger.log(Level.SEVERE, "Exception occured in WakeUpIOSDevice", exp2);
            }
            final Map notificationData = new HashMap();
            notificationData.put("NotificationSuccessList", notificationSuccessList);
            notificationData.put("NotificationFailureList", notificationFailureList);
            notificationData.put("NotificationCompleteList", notificationCompleteList);
            notificationData.put("NotificationError", err);
            notificationData.put("NotificationType", notificationType);
            this.processWakeUpResults(notificationData);
        }
        return retHash;
    }
    
    static {
        APNsLegacyWakeUpProcessor.apnsImpl = null;
    }
}
