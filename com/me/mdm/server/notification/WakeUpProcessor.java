package com.me.mdm.server.notification;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.mdm.core.enrollment.DEPAdminEnrollmentHandler;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import java.util.Iterator;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.inv.MDMInvDataPopulator;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.me.mdm.server.windows.notification.WNSImpl;
import com.adventnet.sym.server.mdm.ios.APNSImpl;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.adventnet.sym.server.mdm.android.WakeUpAndroidDevice;
import com.me.mdm.server.settings.MDMAgentSettingsHandler;
import java.util.logging.Logger;

public abstract class WakeUpProcessor
{
    public static final int WAKEUP_FAILED = 1;
    public static final int UNKNOWN_ERROR = 2;
    public static final int COMM_DETAILS_MISSING = 3;
    public static final int TEMPORARY_ERROR_RETRY = 4;
    protected int platform;
    private Logger logger;
    protected static final String NOTIFICATION_SUCCESS_LIST = "NotificationSuccessList";
    protected static final String NOTIFICATION_FAILURE_LIST = "NotificationFailureList";
    protected static final String NOTIFICATION_FAILURE_RETRIABLE_LIST = "NotificationFailureRetriableList";
    protected static final String NOTIFICATION_RE_REGISTER_LIST = "NotificationReRegisterList";
    protected static final String NOTIFICATION_COMPLETE_LIST = "NotificationCompleteList";
    protected static final String NOTIFICATION_UNREGISTERED_LIST = "NotificationUnregisteredList";
    protected static final String NOTIFICATION_ERROR = "NotificationError";
    protected static final String NOTIFICATION_TYPE = "NotificationType";
    
    public WakeUpProcessor() {
        this.platform = -1;
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public static WakeUpProcessor getWakeUpProcessor(final int notificationType) {
        return getWakeUpProcessor(notificationType, null);
    }
    
    public static WakeUpProcessor getWakeUpProcessor(final int notificationType, final Long customerId) {
        switch (notificationType) {
            case 2:
            case 201: {
                return (MDMAgentSettingsHandler.getInstance().getNotificaitonServiceType(2, customerId) == 1) ? WakeUpAndroidDevice.getInstance() : ((MDMAgentSettingsHandler.getInstance().getNotificaitonServiceType(2, customerId) == 3) ? MDMApiFactoryProvider.getNSwakeupAPI() : PollingImpl.getInstance());
            }
            case 1: {
                return APNSImpl.getInstance();
            }
            case 3: {
                return (MDMAgentSettingsHandler.getInstance().getNotificaitonServiceType(3, customerId) == 1) ? WNSImpl.getInstance() : PollingImpl.getInstance();
            }
            case 303: {
                return WNSImpl.getInstance();
            }
            case 4:
            case 401: {
                return ChromeInServerNotificationImpl.getInstance();
            }
            default: {
                return null;
            }
        }
    }
    
    protected final synchronized void processWakeUpResults(final Map notificationData) {
        final List notificationCompleteList = (notificationData.get("NotificationCompleteList") == null) ? new ArrayList() : notificationData.get("NotificationCompleteList");
        final List notificationSuccessList = (notificationData.get("NotificationSuccessList") == null) ? new ArrayList() : notificationData.get("NotificationSuccessList");
        final List notificationFailureList = (notificationData.get("NotificationFailureList") == null) ? new ArrayList() : notificationData.get("NotificationFailureList");
        final List notificationReRegisterList = (notificationData.get("NotificationReRegisterList") == null) ? new ArrayList() : notificationData.get("NotificationReRegisterList");
        final List notificationUnregisteredList = (notificationData.get("NotificationUnregisteredList") == null) ? new ArrayList() : notificationData.get("NotificationUnregisteredList");
        final List notificationFailureRetryList = (notificationData.get("NotificationFailureRetriableList") == null) ? new ArrayList() : notificationData.get("NotificationFailureRetriableList");
        final List notificationUnprocessedList = new ArrayList(notificationCompleteList);
        notificationUnprocessedList.removeAll(notificationSuccessList);
        notificationUnprocessedList.removeAll(notificationFailureList);
        notificationUnprocessedList.removeAll(notificationFailureRetryList);
        notificationUnprocessedList.removeAll(notificationUnregisteredList);
        final Integer notificationType = notificationData.get("NotificationType");
        final Integer error = notificationData.get("NotificationError");
        final Criteria inProgressCriteria = new Criteria(new Column("DeviceNotification", "STATUS"), (Object)3, 0);
        if (notificationSuccessList.size() > 0) {
            this.logger.log(Level.INFO, "Resource list of success notification {0}", notificationSuccessList);
            NotificationHandler.getInstance().updateNotificationStatus(notificationSuccessList, 1, notificationType, inProgressCriteria);
        }
        if (notificationFailureList.size() > 0) {
            this.logger.log(Level.INFO, "Resource list of failure notification {0}", notificationFailureList);
            NotificationHandler.getInstance().updateNotificationStatus(notificationFailureList, 2, notificationType, inProgressCriteria);
            this.handleFailureNotification(notificationFailureList, error);
        }
        if (notificationFailureRetryList.size() > 0) {
            this.logger.log(Level.INFO, "Resource list of failure retry notification {0}", notificationFailureRetryList);
            NotificationHandler.getInstance().updateNotificationStatus(notificationFailureRetryList, 2, notificationType, inProgressCriteria);
            this.handleFailureNotification(notificationFailureRetryList, error);
        }
        if (notificationReRegisterList != null && notificationReRegisterList.size() > 0) {
            this.reRegisterPushNotificationToken(notificationReRegisterList, notificationType);
        }
        if (notificationUnregisteredList.size() > 0) {
            this.logger.log(Level.INFO, "Resource list of unregistered devices {0}", notificationUnregisteredList);
            NotificationHandler.getInstance().updateNotificationStatus(notificationUnregisteredList, 1, notificationType, inProgressCriteria);
            this.updateUnmanagedResourceStatuses(notificationUnregisteredList);
        }
        if (notificationUnprocessedList.size() > 0) {
            this.logger.log(Level.INFO, "Resource list of unprocessed notifications (comm details missing) {0}", notificationUnprocessedList);
            NotificationHandler.getInstance().updateNotificationStatus(notificationUnprocessedList, 2, notificationType, inProgressCriteria);
            this.handleFailureNotification(notificationUnprocessedList, 3);
        }
    }
    
    public static final void wakeUpAsynchronously(final int platform, final int notificationType, final List<Long> resourceList, final int timeDelay) {
        try {
            final Properties taskProps = new Properties();
            ((Hashtable<String, List<Long>>)taskProps).put("RESOURCE_LIST", resourceList);
            ((Hashtable<String, Integer>)taskProps).put("platform", platform);
            ((Hashtable<String, Integer>)taskProps).put("NotificationType", notificationType);
            final HashMap taskInfoMap = new HashMap();
            taskInfoMap.put("taskName", "DeviceWakeUpTask");
            taskInfoMap.put("schedulerTime", System.currentTimeMillis() + timeDelay);
            taskInfoMap.put("poolName", "mdmPool");
            ApiFactoryProvider.getSchedulerAPI().executeAsynchronousWithDelay("com.me.mdm.server.notification.task.DeviceWakeUpTask", taskInfoMap, taskProps);
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void handleFailureNotification(final List notificationFailureList, final int error) {
        this.scanCommandNotifyFailureHandling(notificationFailureList, error);
        this.securityCommandNotifyFailureHandling(notificationFailureList, error);
    }
    
    private void scanCommandNotifyFailureHandling(final List notificationFailureList, final int error) {
        try {
            final JSONObject errData = this.getScanErrData(error);
            final String scanRemarks = errData.optString("ErrorMsg");
            final int scanErrorCode = errData.optInt("ErrorCode", -1);
            MDMInvDataPopulator.getInstance().updateScanNotificaitonFailed(notificationFailureList, scanRemarks);
            if (scanErrorCode != -1L) {
                this.updateMultipleDevicesScanToErrorCode(notificationFailureList, scanErrorCode);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while scanCommandNotifyFailureHandling", ex);
        }
    }
    
    private JSONObject getScanErrData(final int errCode) throws Exception {
        final JSONObject errData = new JSONObject();
        switch (this.platform) {
            case 2: {
                errData.put("ErrorMsg", (Object)((errCode == 3 || errCode == 2) ? "dc.db.mdm.scan.remarks.fetch_data_failed" : "dc.db.mdm.scan.remarks.unable_to_reach_gcm"));
                errData.put("ErrorCode", (errCode == 3 || errCode == 2) ? -1L : 12132L);
                break;
            }
            case 1: {
                String remarks = "dc.db.mdm.scan.remarks.fetch_data_failed";
                Long dbCode = -1L;
                if (errCode == 1) {
                    dbCode = 12133L;
                    remarks = "dc.db.mdm.scan.remarks.unable_to_reach_apns";
                }
                else if (errCode == 4) {
                    dbCode = 12138L;
                    remarks = "dc.db.mdm.scan.remarks.unable_to_reach_apns_retry";
                }
                errData.put("ErrorMsg", (Object)remarks);
                errData.put("ErrorCode", (Object)dbCode);
                break;
            }
            case 3: {
                errData.put("ErrorMsg", (Object)((errCode == 3) ? "dc.db.mdm.scan.remarks.fetch_data_failed" : "dc.db.mdm.scan.remarks.unable_to_reach_wns"));
                errData.put("ErrorCode", (errCode == 3) ? -1L : 12134L);
                break;
            }
        }
        return errData;
    }
    
    private void updateMultipleDevicesScanToErrorCode(final List resList, final int errCode) {
        for (final Long resID : resList) {
            MDMInvDataPopulator.getInstance().updateDeviceScanToErrorCode(resID, errCode);
        }
    }
    
    private void securityCommandNotifyFailureHandling(final List notificationFailureList, final int error) {
        final String errRemarks = this.getSecurityCommandErrData(error);
        MDMInvDataPopulator.getInstance().updateSecurityCommandStatus(notificationFailureList, 0, errRemarks);
    }
    
    private String getSecurityCommandErrData(final int errCode) {
        switch (this.platform) {
            case 2: {
                return (errCode == 3 || errCode == 2) ? "dc.mdm.comamnd.failure_cotact_support_with_server_agent_logs" : "dc.db.mdm.scan.remarks.unable_to_reach_gcm";
            }
            case 1: {
                String remarks = "dc.mdm.comamnd.failure_cotact_support_with_server_agent_logs";
                if (errCode == 1) {
                    remarks = "dc.db.mdm.scan.remarks.unable_to_reach_apns";
                }
                else if (errCode == 4) {
                    remarks = "dc.db.mdm.scan.remarks.unable_to_reach_apns_retry";
                }
                return remarks;
            }
            case 3: {
                return (errCode == 3 || errCode == 2) ? "dc.mdm.comamnd.failure_cotact_support_with_server_agent_logs" : "dc.db.mdm.scan.remarks.unable_to_reach_wns";
            }
            default: {
                return null;
            }
        }
    }
    
    public static final void wakeUpAsynchronously(final Long erid, final Long time) {
        final Properties props = new Properties();
        ((Hashtable<String, String>)props).put("ENROLLMENT_REQUEST_ID", erid.toString());
        final HashMap taskInfoMap = new HashMap();
        taskInfoMap.put("taskName", "EnrollmentWakeUpTask");
        taskInfoMap.put("poolName", "mdmPool");
        if (time == null) {
            taskInfoMap.put("schedulerTime", System.currentTimeMillis());
        }
        else {
            taskInfoMap.put("schedulerTime", System.currentTimeMillis() + time);
        }
        ApiFactoryProvider.getSchedulerAPI().executeAsynchronousWithDelay("com.me.mdm.server.enrollment.task.EnrollmentWakeUpTask", taskInfoMap, props);
    }
    
    protected void reRegisterPushNotificationToken(final List reRegResourceList, final int notificationType) {
    }
    
    private void updateUnmanagedResourceStatuses(final List unmanagedResList) {
        if (!MDMFeatureParamsHandler.getInstance().isFeatureEnabled("DoNotAllowApnsUnregister")) {
            final Properties props = new Properties();
            ((Hashtable<String, Long>)props).put("UNREGISTERED_TIME", System.currentTimeMillis());
            ((Hashtable<String, Integer>)props).put("PLATFORM_TYPE", this.platform);
            ((Hashtable<String, Integer>)props).put("MANAGED_STATUS", 4);
            ((Hashtable<String, String>)props).put("REMARKS", "dc.mdm.profile.ios.remarks.removed_from_device");
            final ManagedDeviceHandler handler = ManagedDeviceHandler.getInstance();
            final List depRescIdList = DEPAdminEnrollmentHandler.getDepDevicesInResList(unmanagedResList);
            this.logger.log(Level.INFO, "Removing DEP devices from unregistered list: {0}", depRescIdList);
            unmanagedResList.removeAll(depRescIdList);
            for (final Object res : unmanagedResList) {
                int managedstatus = 2;
                try {
                    managedstatus = (int)DBUtil.getValueFromDB("ManagedDevice", "RESOURCE_ID", res, "MANAGED_STATUS");
                }
                catch (final Exception exp) {
                    this.logger.log(Level.SEVERE, "WakeUpProcessor: updateUnmanagedResourceStatuses: Exception while getting managedstatus", exp);
                }
                if (managedstatus == 2) {
                    ((Hashtable<String, Object>)props).put("RESOURCE_ID", res);
                    try {
                        handler.updateManagedDeviceDetails(props);
                    }
                    catch (final Exception e) {
                        this.logger.log(Level.SEVERE, "WakeUpProcessor: updateUnmanagedResourceStatuses: Exception ", e);
                    }
                }
            }
        }
    }
    
    public abstract HashMap wakeUpDevices(final List p0, final int p1);
}
