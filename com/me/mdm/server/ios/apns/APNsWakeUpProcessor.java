package com.me.mdm.server.ios.apns;

import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import java.util.Date;
import com.turo.pushy.apns.util.TokenUtil;
import com.turo.pushy.apns.util.ApnsPayloadBuilder;
import com.me.mdm.server.enrollment.MDMEnrollmentRequestHandler;
import com.me.mdm.server.enrollment.notification.EnrollmentNotificationHandler;
import java.util.Map;
import com.turo.pushy.apns.util.SimpleApnsPushNotification;
import org.json.JSONObject;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.devicemanagement.framework.server.logger.DMSecurityLogger;
import com.me.mdm.server.notification.PushNotificationHandler;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.io.File;
import java.io.IOException;
import javax.net.ssl.SSLException;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.ios.APNSImpl;

public class APNsWakeUpProcessor extends APNSImpl
{
    static final Logger logger;
    static final Logger accesslogger;
    static final String CERT_FILE_NAME = "CERT_FILE_NAME";
    static final String CERT_PASSWORD = "CERT_PASSWORD";
    static final long EXPIRY_DURATION_7_DAYS = 604800000L;
    static final String TAB_SEPARATOR = "\t";
    protected PushyClient pushyClient;
    
    protected APNsWakeUpProcessor() {
        this.pushyClient = null;
        this.platform = 1;
    }
    
    public static APNsWakeUpProcessor getInstance() {
        return new APNsWakeUpProcessor();
    }
    
    private void initialize() throws SSLException, IOException, InterruptedException, Throwable {
        if (this.pushyClient == null || !this.pushyClient.isConnected()) {
            (this.pushyClient = new PushyClient()).setDefaultDMFProxy();
            this.initializeCertificatePath();
            this.pushyClient.build();
        }
    }
    
    private void initializeCertificatePath() throws Exception {
        final HashMap apnsCertificateInfo = APNsCertificateHandler.getInstance().getAPNsCertificateInfo();
        if (apnsCertificateInfo.containsKey("ERROR_CODE")) {
            final int apnsError = apnsCertificateInfo.get("ERROR_CODE");
            if (apnsError == 1001) {
                throw new Exception("certificate_expired");
            }
            if (apnsError == 1002) {
                throw new Exception("certificate_revoked");
            }
        }
        if (apnsCertificateInfo.size() > 0) {
            final String certFileName = apnsCertificateInfo.get("CERTIFICATE_FILE_NAME");
            final String certPassword = apnsCertificateInfo.get("CERTIFICATE_PASSWORD");
            final String apnsCertificateFolder = APNsCertificateHandler.getAPNsCertificateFolderPath();
            final String certPath = apnsCertificateFolder + File.separator + certFileName;
            ApiFactoryProvider.getCacheAccessAPI().putCache("CERT_PASSWORD", (Object)certPassword, 2);
            ApiFactoryProvider.getCacheAccessAPI().putCache("CERT_FILE_NAME", (Object)certPath, 2);
            this.pushyClient.setCertificate(certPath, certPassword);
        }
    }
    
    @Override
    public void reinitialize() throws Throwable {
    }
    
    private void reinitializeAndConnect() throws SSLException, IOException, InterruptedException, Throwable {
        APNsWakeUpProcessor.logger.log(Level.INFO, "APNsWakeUpProcessor: reinitialize() connection..");
        this.disconnectPushyClient();
        this.initialize();
    }
    
    @Override
    public HashMap wakeUpDevices(final List resourceList, final int notificationType) {
        APNsWakeUpProcessor.logger.log(Level.INFO, "APNsWakeUpProcessor: Inside wakeUpDevices List {0}", resourceList);
        final HashMap retHash = new HashMap();
        String failureReason = null;
        final List notificationCompleteList = new ArrayList(resourceList);
        final List notificationSuccessList = new ArrayList();
        final List notificationFailureList = new ArrayList();
        final List notificationFailureRetryList = new ArrayList();
        final List notificationUnregisteredList = new ArrayList();
        int err = -1;
        try {
            err = this.checkAndReinitialize();
            if (err == -1) {
                for (int i = 0; i < resourceList.size(); ++i) {
                    final Long resourceId = resourceList.get(i);
                    final JSONObject communicationProps = PushNotificationHandler.getInstance().getNotificationDetails(resourceId, notificationType);
                    if (communicationProps != null) {
                        APNsWakeUpProcessor.logger.log(Level.INFO, "APNsWakeUpProcessor: wakeUpIOSDevices: ResID {0}", resourceId);
                        DMSecurityLogger.info(APNsWakeUpProcessor.logger, "APNsWakeUpProcessor", "wakeUpDevices", "CommProps {0}", (Object)communicationProps);
                        final String pushMagic = String.valueOf(communicationProps.get("PUSH_MAGIC_ENCRYPTED"));
                        final String deviceToken = String.valueOf(communicationProps.get("NOTIFICATION_TOKEN_ENCRYPTED"));
                        final String topic = String.valueOf(communicationProps.get("TOPIC"));
                        final SimpleApnsPushNotification pushNotification = this.buildWakeUpPushNotificationPayload(pushMagic, deviceToken, topic);
                        final JSONObject resultJSON = this.pushyClient.sendNotificationWithOneRetry(pushNotification);
                        final String status = resultJSON.optString("Status", "Success");
                        if (status.equals("Success")) {
                            APNsWakeUpProcessor.logger.log(Level.INFO, "APNsWakeUpProcessor: wakeUpIOSDevices: Notification is succeeded. ResID: {0}", resourceId);
                            notificationSuccessList.add(resourceId);
                            failureReason = null;
                        }
                        else {
                            failureReason = resultJSON.optString("Reason", "Unknown");
                            APNsWakeUpProcessor.logger.log(Level.INFO, "APNsWakeUpProcessor: wakeUpIOSDevices: Notification is failed. ResID {0}", resourceId);
                            if (this.isFailureTemporary(failureReason)) {
                                notificationFailureRetryList.add(resourceId);
                                err = 4;
                            }
                            else if (this.isTokenFailure(failureReason)) {
                                notificationUnregisteredList.add(resourceId);
                            }
                            else {
                                if (failureReason.equals("ClientNotConnected")) {
                                    err = 1;
                                    break;
                                }
                                err = 1;
                            }
                            if (!this.isTokenFailure(failureReason)) {
                                ApiFactoryProvider.getCacheAccessAPI().putCache("ApnsConnectionError", (Object)failureReason, 1);
                            }
                        }
                        this.printWakeUpLog(resourceId, status, failureReason);
                    }
                }
            }
            else {
                APNsWakeUpProcessor.logger.log(Level.WARNING, "APNsWakeUpProcessor: wakeUpIOSDevices: WakeUp Failed. Could not connect to APNS");
                notificationFailureRetryList.addAll(resourceList);
                notificationFailureRetryList.removeAll(notificationSuccessList);
            }
        }
        catch (final Throwable t) {
            APNsWakeUpProcessor.logger.log(Level.SEVERE, "APNsWakeUpProcessor: wakeUpIOSDevices: Exception ", t);
            err = Integer.valueOf(String.valueOf(MDMApiFactoryProvider.getAPNSErrorHandler().handleAPNSWakeupFailure(t)));
            if (err == 4) {
                notificationFailureRetryList.addAll(resourceList);
                notificationFailureRetryList.removeAll(notificationSuccessList);
            }
            else if (err == 2 || err == 1) {
                notificationFailureList.addAll(resourceList);
                notificationFailureList.removeAll(notificationSuccessList);
            }
        }
        finally {
            this.disconnectPushyClient();
            final Map notificationData = new HashMap();
            notificationData.put("NotificationSuccessList", notificationSuccessList);
            notificationData.put("NotificationFailureList", notificationFailureList);
            notificationData.put("NotificationCompleteList", notificationCompleteList);
            notificationData.put("NotificationFailureRetriableList", notificationFailureRetryList);
            notificationData.put("NotificationUnregisteredList", notificationUnregisteredList);
            notificationData.put("NotificationError", err);
            notificationData.put("NotificationType", notificationType);
            this.processWakeUpResults(notificationData);
        }
        return retHash;
    }
    
    @Override
    public boolean wakeUpDeviceWithERID(final String deviceToken, final String pushMagic, final String topic, final HashMap hsAdditionalParams) {
        Long enrollmentID = null;
        Long resourceID = null;
        String failureReason = "Unknown";
        String status = "Failed";
        int err = -1;
        try {
            if (hsAdditionalParams.containsKey("ENROLLMENT_REQUEST_ID")) {
                enrollmentID = hsAdditionalParams.get("ENROLLMENT_REQUEST_ID");
            }
            if (hsAdditionalParams.containsKey("RESOURCE_ID")) {
                resourceID = hsAdditionalParams.get("RESOURCE_ID");
            }
            if (enrollmentID != null) {
                hsAdditionalParams.put("STATUS", 0);
                EnrollmentNotificationHandler.getInstance().addOrUpdateNotificationDetails(hsAdditionalParams);
            }
            err = this.checkAndReinitialize();
            if (err == -1) {
                APNsWakeUpProcessor.logger.log(Level.INFO, "wakeUpDeviceWithERID: ERID: {0}", enrollmentID);
                APNsWakeUpProcessor.logger.log(Level.FINEST, "wakeUpDeviceWithERID: deviceToken: {0}", deviceToken);
                APNsWakeUpProcessor.logger.log(Level.INFO, "wakeUpDeviceWithERID: pushMagic: {0}", pushMagic);
                APNsWakeUpProcessor.logger.log(Level.INFO, "wakeUpDeviceWithERID: topic: {0}", topic);
                final SimpleApnsPushNotification pushNotification = this.buildWakeUpPushNotificationPayload(pushMagic, deviceToken, topic);
                final JSONObject resultJSON = this.pushyClient.sendNotificationWithOneRetry(pushNotification);
                APNsWakeUpProcessor.logger.log(Level.INFO, "wakeUpDeviceWithERID Push result: {0}", resultJSON.toString());
                status = resultJSON.optString("Status", "Success");
                if (!status.equals("Success")) {
                    failureReason = resultJSON.optString("Reason", "Unknown");
                    if (this.isFailureTemporary(failureReason)) {
                        err = 4;
                    }
                    else if (this.isTokenFailure(failureReason)) {
                        err = 4;
                    }
                    else if (failureReason.equals("ClientNotConnected")) {
                        err = 1;
                    }
                    else {
                        err = 4;
                    }
                    if (!this.isTokenFailure(failureReason)) {
                        ApiFactoryProvider.getCacheAccessAPI().putCache("ApnsConnectionError", (Object)failureReason, 1);
                    }
                }
            }
        }
        catch (final Throwable t) {
            APNsWakeUpProcessor.logger.log(Level.SEVERE, "APNsWakeUpProcessor: wakeUpIOSDevices: Exception ", t);
            err = Integer.valueOf(String.valueOf(MDMApiFactoryProvider.getAPNSErrorHandler().handleAPNSWakeupFailure(t)));
        }
        APNsWakeUpProcessor.accesslogger.log(Level.INFO, "ENROLLMENT WAKEUP: {0}{1}{2}{3}{4}{5}{6}{7}{8}{9}", new Object[] { "\t", enrollmentID, "\t", pushMagic, "\t", status, "\t", failureReason, "\t", resourceID });
        this.disconnectPushyClient();
        if (err != -1) {
            this.handleEnrollmentDeviceError(err, enrollmentID);
        }
        return status.equals("Success");
    }
    
    public void testApnsConnection() throws Throwable {
        final String deviceToken = "sFsDgPKihHrfM02eDNsXxcBd0pJ0KQYn6H60IUYZWFM";
        final String pushMagic = "askjdoijdowqdmqccdijowiedowmjdlq";
        final String topic = APNsCertificateHandler.getAPNSCertificateDetails().get("TOPIC");
        if (topic == null) {
            throw new Exception("push certificate cannot be null");
        }
        this.checkAndReinitialize();
        final SimpleApnsPushNotification pushNotification = this.buildWakeUpPushNotificationPayload(pushMagic, deviceToken, topic);
        final JSONObject resultJSON = this.pushyClient.sendNotificationWithOneRetry(pushNotification);
        APNsWakeUpProcessor.logger.log(Level.INFO, "[Apns] Push result: {0}", resultJSON.toString());
        this.disconnectPushyClient();
    }
    
    protected int checkAndReinitialize() throws Throwable {
        int status = -1;
        try {
            this.reinitializeAndConnect();
            if (!this.pushyClient.isConnected()) {
                status = 1;
            }
        }
        catch (final Throwable t) {
            APNsWakeUpProcessor.logger.log(Level.SEVERE, "APNsWakeUpProcessor: checkAndReinitialize: Exception ", t);
            throw t;
        }
        return status;
    }
    
    protected void disconnectPushyClient() {
        try {
            if (this.pushyClient != null) {
                APNsWakeUpProcessor.logger.log(Level.INFO, "APNsWakeUpProcessor: disconnectPushyClient() called");
                this.pushyClient.disconnect();
                APNsWakeUpProcessor.logger.log(Level.INFO, "APNsWakeUpProcessor: disconnected");
            }
        }
        catch (final Exception e) {
            APNsWakeUpProcessor.logger.log(Level.SEVERE, "APNsWakeUpProcessor: disconnectPushyClient, error during disconnect: {0}", e.toString());
        }
    }
    
    private void handleEnrollmentDeviceError(final int err, final Long enrollmentID) {
        if (enrollmentID != null) {
            int errorCode = Integer.parseInt(String.valueOf(12138L));
            String remarks = "dc.db.mdm.scan.remarks.unable_to_reach_apns_retry";
            if (err == 12140L) {
                errorCode = Integer.parseInt(String.valueOf(12140L));
                remarks = "mdm.apns_expired_device_scan_failed";
            }
            else if (err == 12139L) {
                errorCode = Integer.parseInt(String.valueOf(12139L));
                remarks = "mdm.apns_revoke_device_scan_failed";
            }
            MDMEnrollmentRequestHandler.getInstance().updateEnrollmentStatusAndErrorCode(enrollmentID, 0, remarks, errorCode);
        }
    }
    
    private SimpleApnsPushNotification buildWakeUpPushNotificationPayload(final String pushMagic, final String deviceToken, final String topic) {
        final String payload = ApnsPayloadBuilder.buildMdmPayload(pushMagic);
        final String token = TokenUtil.sanitizeTokenString(deviceToken);
        final SimpleApnsPushNotification pushNotification = new SimpleApnsPushNotification(token, topic, payload, new Date(System.currentTimeMillis() + 604800000L));
        return pushNotification;
    }
    
    @Override
    public boolean IsAPNsReachacble() {
        boolean status = false;
        try {
            this.reinitializeAndConnect();
            APNsWakeUpProcessor.logger.log(Level.INFO, "APNsWakeUpProcessor IsAPNsReachable? {0}", this.pushyClient.isConnected());
            status = this.pushyClient.isConnected();
            this.disconnectPushyClient();
        }
        catch (final Throwable e) {
            final int err = Integer.valueOf(String.valueOf(MDMApiFactoryProvider.getAPNSErrorHandler().handleAPNSWakeupFailure(e)));
            if (e instanceof ClassNotFoundException || e instanceof NoClassDefFoundError || e instanceof ExceptionInInitializerError || err == 2) {
                status = false;
                APNsWakeUpProcessor.logger.log(Level.SEVERE, "isAPNsReachable Error and enable legacy", e);
            }
            else {
                status = true;
                APNsWakeUpProcessor.logger.log(Level.SEVERE, "isAPNsReachable Error and disable legacy", e);
            }
        }
        return status;
    }
    
    private boolean isFailureTemporary(final String rejectionReason) {
        return rejectionReason.equals("IdleTimeout") || rejectionReason.equals("InternalServerError") || rejectionReason.equals("InterruptedException") || rejectionReason.equals("ServiceUnavailable") || rejectionReason.equals("Shutdown") || rejectionReason.equals("TooManyRequests");
    }
    
    private boolean isTokenFailure(final String rejectionReason) {
        return rejectionReason.equals("BadDeviceToken") || rejectionReason.equals("DeviceTokenNotForTopic") || rejectionReason.equals("Unregistered");
    }
    
    private void printWakeUpLog(final Long resourceId, final String status, final String failureReason) {
        String resourceName = "unknown";
        try {
            resourceName = ManagedDeviceHandler.getInstance().getDeviceName(resourceId);
        }
        catch (final Exception e) {
            APNsWakeUpProcessor.logger.log(Level.SEVERE, "APNsWakeUpProcessor: printWakeUpLog() Could not retrieve device name for resource {0}", resourceId);
        }
        final String accessMessage = "WAKEUP: \t" + resourceId + "\t" + resourceName + "\t" + status + "\t" + failureReason;
        APNsWakeUpProcessor.accesslogger.log(Level.INFO, accessMessage);
    }
    
    static {
        logger = Logger.getLogger("MDMLogger");
        accesslogger = Logger.getLogger("MDMWakupReqLogger");
    }
}
