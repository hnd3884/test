package com.me.mdm.server.remotesession;

import com.adventnet.persistence.Row;
import com.adventnet.sym.webclient.mdm.inv.InvDeviceDetailsAction;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import com.adventnet.sym.server.mdm.apps.vpp.VPPAppMgmtHandler;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.sym.server.mdm.inv.InventoryUtil;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.adventnet.sym.server.mdm.iosnativeapp.IosNativeAppHandler;
import com.me.mdm.server.settings.MDMAgentSettingsHandler;
import java.util.Iterator;
import java.util.List;
import com.adventnet.sym.webclient.mdm.inv.InventoryRoleCheckUtil;
import java.util.ArrayList;
import org.json.JSONArray;
import com.me.devicemanagement.framework.server.exception.SyMException;
import org.json.JSONException;
import com.me.mdm.server.onelinelogger.MDMOneLineLogger;
import java.io.IOException;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.i18n.I18N;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.command.DeviceInvCommandHandler;
import com.adventnet.sym.server.mdm.DeviceDetails;
import com.me.mdm.server.command.CommandStatusHandler;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import org.json.JSONObject;
import java.util.logging.Logger;

public class RemoteSessionManager
{
    protected static final int SESSION_STATUS_CREATED_WAITING_FOR_DEVICE = 1;
    private final int SESSION_STATUS_WAITING_FOR_USER_APPROVAL = 2;
    private final int SESSION_STATUS_STARTED_AND_INPROGRESS = 3;
    private final int SESSION_STATUS_STOPPED_USER_REJECTED = 4;
    private final int SESSION_STATUS_STOPPED_AGENT_INCOMPATIBLE = 5;
    private final int SESSION_STATUS_STOPPED_SUCCESS = 6;
    private final int SESSION_STATUS_START_FAILED = 7;
    private static final int SESSION_STATUS_STOPPED_BY_USER = 8;
    private static final int SESSION_STATUS_PAUSED = 9;
    private static final int SESSION_STATUS_INTERRUPTED = 10;
    protected static final int SESSION_FAILED_DEVICE_NOT_RECHABLE = 11;
    protected static final int SESSION_FAILED_VIEWER_INACTIVE = 12;
    private final int SESSION_WAITING_FOR_USER_APPROVAL = 6001;
    private final int SESSION_STARTED_USER_APPROVED = 6003;
    private final int SESSION_STOPPED_USER_REJECTED = 6005;
    private final int SESSION_STOPPED_AGENT_INCOMPATIBLE = 6007;
    private final int SESSION_STOPPED = 6010;
    private final int SESSION_START_FAILED = 6013;
    private static final int SESSION_STOPPED_BY_USER = 6014;
    private static final int SESSION_PAUSED = 6015;
    private static final int SESSION_INTERUPPTED_PARTICIPANT_DOWN = 6020;
    private static final int NO_SESSION_INFO_AVAILABLE = 7001;
    protected static final String REMOTE_SESSION_INFO_NOT_AVAILABLE = "RemoteSessionInfoNotAvailable";
    public static final String STATUS_CODE = "StatusCode";
    private static final Logger LOGGER;
    private static final Long KEY_TIMEOUT;
    
    public JSONObject startSession(final Long resourceId, final Long aaaUserId) throws JSONException, Exception {
        final JSONObject responseJSON = new JSONObject();
        final org.json.simple.JSONObject secLog = new org.json.simple.JSONObject();
        secLog.put((Object)"RESOURCE_ID", (Object)resourceId);
        String secRemarks = "remote session initiation failed";
        try {
            final Long customerId = CustomerInfoUtil.getInstance().getCustomerIDForResID(resourceId);
            if (MDMApiFactoryProvider.getAssistAuthTokenHandler().isAssistIntegrated(customerId)) {
                try {
                    JSONObject responseData = this.obtainActiveSessionId(resourceId);
                    if (responseData == null) {
                        responseData = MDMApiFactoryProvider.getAssistAuthTokenHandler().generateSession(customerId);
                    }
                    final String status = (String)responseData.get("Status");
                    if (status.equalsIgnoreCase("Success")) {
                        final JSONObject sessionInfo = responseData.getJSONObject("SessionDetails");
                        final RemoteSessionInfoHandler remoteSessionHandler = new RemoteSessionInfoHandler();
                        Long sessionId;
                        if (sessionInfo.has("SESSION_ID")) {
                            sessionId = (Long)sessionInfo.get("SESSION_ID");
                        }
                        else {
                            sessionId = remoteSessionHandler.addRemoteSession(sessionInfo);
                        }
                        secLog.put((Object)"SESSION_ID", (Object)sessionId);
                        final JSONObject commandHistoryData = new JSONObject();
                        commandHistoryData.put("RESOURCE_ID", (Object)resourceId);
                        final Long commandId = DeviceCommandRepository.getInstance().addCommand("RemoteSession");
                        commandHistoryData.put("COMMAND_ID", (Object)commandId);
                        commandHistoryData.put("ADDED_BY", (Object)aaaUserId);
                        final Long commandHistoryId = new CommandStatusHandler().populateCommandStatus(commandHistoryData);
                        final JSONObject sessionToCmdHistData = new JSONObject();
                        sessionToCmdHistData.put("COMMAND_HISTORY_ID", (Object)commandHistoryId);
                        sessionToCmdHistData.put("SESSION_ID", (Object)sessionId);
                        sessionToCmdHistData.put("STATUS", 1);
                        remoteSessionHandler.addOrUpdateResourceToRemoteSession(sessionToCmdHistData);
                        final DeviceDetails device = new DeviceDetails(resourceId);
                        DeviceInvCommandHandler.getInstance().sendCommandToDevice(device, "RemoteSession", aaaUserId);
                        RemoteControlTask.startRemoteCommandTimeout(resourceId);
                        responseJSON.put("Status", (Object)"Success");
                        responseJSON.put("AssistUrl", sessionInfo.get("SESSION_URL"));
                        RemoteSessionManager.LOGGER.log(Level.INFO, "Response for remote command{0}", responseJSON);
                        secRemarks = "remote session initiation success";
                    }
                    else {
                        responseJSON.put("Status", (Object)"Failure");
                        String remarks = responseData.optString("Remarks", "");
                        final String reason = responseData.optString("Reason", "");
                        if (remarks.equals("INVALID_TICKET") || reason.equals("INVALID_TICKET") || remarks.equals("INVALID_TOKEN") || reason.equals("INVALID_TOKEN")) {
                            throw new APIHTTPException("AS0002", new Object[0]);
                        }
                        if (remarks.equals("invalid_grant") || reason.equals("invalid_grant")) {
                            remarks = I18N.getMsg("mdm.api.error.unauthorized", new Object[0]);
                        }
                        responseJSON.put("Remarks", (Object)remarks);
                        responseJSON.put("Reason", (Object)reason);
                        RemoteSessionManager.LOGGER.log(Level.INFO, "Response for remote command failure{0}", responseJSON);
                    }
                }
                catch (final DataAccessException ex) {
                    responseJSON.put("Status", (Object)"failure");
                    responseJSON.put("Remarks", (Object)"Unexpected error. Please contact Support with Logs.");
                    RemoteSessionManager.LOGGER.log(Level.SEVERE, null, (Throwable)ex);
                }
                catch (final IOException ex2) {
                    responseJSON.put("Status", (Object)"failure");
                    responseJSON.put("Remarks", (Object)("Unable to reach " + MDMUtil.getInstance().getMDMApplicationProperties().getProperty("AssistSessionApiUrl") + ", kindly ensure whether this url is not blocked."));
                    RemoteSessionManager.LOGGER.log(Level.SEVERE, null, ex2);
                }
                catch (final Exception ex3) {
                    responseJSON.put("Status", (Object)"failure");
                    responseJSON.put("Remarks", (Object)"Unexpected error. Please contact Support with Logs.");
                    RemoteSessionManager.LOGGER.log(Level.SEVERE, null, ex3);
                }
            }
            else {
                responseJSON.put("Status", (Object)"failure");
            }
        }
        finally {
            secLog.put((Object)"REMARKS", (Object)secRemarks);
            MDMOneLineLogger.log(Level.INFO, "INIT_REMOTE_SESSION", secLog);
        }
        return responseJSON;
    }
    
    public int getSessionStatus(final Long resourceId) throws Exception {
        final JSONObject commandInfo = new CommandStatusHandler().getRecentCommandInfo(resourceId, DeviceCommandRepository.getInstance().getCommandID("RemoteSession"));
        if (commandInfo.length() == 0) {
            throw new SyMException(404, "Command not distributed", (Throwable)null);
        }
        final Long commandHistoryId = commandInfo.getLong("COMMAND_HISTORY_ID");
        return new RemoteSessionInfoHandler().getSessionStatus(commandHistoryId);
    }
    
    public JSONObject getMultipleSessionStatus(final Long customerId, final JSONArray deviceIds) throws Exception {
        List resourceIDList = new ArrayList();
        if (deviceIds != null && deviceIds.length() != 0) {
            for (int i = 0; i < deviceIds.length(); ++i) {
                resourceIDList.add(deviceIds.get(i));
            }
            resourceIDList = InventoryRoleCheckUtil.getInstance().getCustomerBelongDevices(customerId, resourceIDList);
            final JSONArray jsonArray = new JSONArray();
            for (final Object resource_id : resourceIDList) {
                final JSONObject jsonObject = new JSONObject();
                jsonObject.put("RESOURCE_ID", (Object)(resource_id + ""));
                jsonObject.put("STATUS", this.getSessionStatus(Long.valueOf(resource_id.toString())));
                jsonArray.put((Object)jsonObject);
            }
            final JSONObject result = new JSONObject();
            result.put("REMOTE_STATUS", (Object)jsonArray);
            return result;
        }
        return null;
    }
    
    public String getSessionKey(final Long resourceId) throws Exception {
        final Long commandHistoryId = new CommandStatusHandler().getRecentCommandInfo(resourceId, DeviceCommandRepository.getInstance().getCommandID("RemoteSession")).getLong("COMMAND_HISTORY_ID");
        return new RemoteSessionInfoHandler().getSessionKey(commandHistoryId);
    }
    
    public void handleSessionUpdateFromAgent(final Long resourceId, final JSONObject msgFromAgent) {
        try {
            final RemoteSessionReportHandler reportHandler = new RemoteSessionReportHandler();
            final RemoteSessionInfoHandler remoteSessionHandler = new RemoteSessionInfoHandler();
            final Long commandHistoryId = new CommandStatusHandler().getRecentCommandInfo(resourceId, DeviceCommandRepository.getInstance().getCommandID("RemoteSession")).getLong("COMMAND_HISTORY_ID");
            final Long sessionId = remoteSessionHandler.getSessionIdForCmdHisId(commandHistoryId);
            final JSONObject reportJSON = new JSONObject();
            reportJSON.put("SESSION_ID", (Object)sessionId);
            final JSONObject sessionToCmdHistData = new JSONObject();
            sessionToCmdHistData.put("COMMAND_HISTORY_ID", (Object)commandHistoryId);
            final int statusCode = msgFromAgent.getInt("StatusCode");
            int statusToUpdate = 0;
            switch (statusCode) {
                case 6001: {
                    statusToUpdate = 2;
                    break;
                }
                case 6003: {
                    statusToUpdate = 3;
                    reportJSON.put("SESSION_START_TIME", MDMUtil.getCurrentTimeInMillis());
                    MDMOneLineLogger.log(Level.INFO, "ACCEPT_REMOTE_SESSION", "Remote session {0} is accepted by agent with resource ID {1}", new String[] { String.valueOf(sessionId), String.valueOf(resourceId) });
                    break;
                }
                case 6005: {
                    statusToUpdate = 4;
                    break;
                }
                case 6007: {
                    statusToUpdate = 5;
                    break;
                }
                case 6010: {
                    statusToUpdate = 6;
                    reportJSON.put("SESSION_END_TIME", MDMUtil.getCurrentTimeInMillis());
                    break;
                }
                case 6013: {
                    statusToUpdate = 7;
                    break;
                }
                case 6014: {
                    statusToUpdate = 8;
                    reportJSON.put("SESSION_END_TIME", MDMUtil.getCurrentTimeInMillis());
                    break;
                }
                case 6015: {
                    statusToUpdate = 9;
                    break;
                }
                case 6020: {
                    statusToUpdate = 10;
                    break;
                }
                default: {
                    RemoteSessionManager.LOGGER.log(Level.INFO, "REMOTESESSIONMANAGER: Status update from Agent is not in Known formar, So do nothing.");
                    return;
                }
            }
            sessionToCmdHistData.put("STATUS", statusToUpdate);
            remoteSessionHandler.addOrUpdateResourceToRemoteSession(sessionToCmdHistData);
            reportHandler.addOrUpdateRemoteSessionReport(sessionId, reportJSON);
        }
        catch (final Exception ex) {
            RemoteSessionManager.LOGGER.log(Level.SEVERE, null, ex);
        }
    }
    
    public int handleRemoteSessionStatusUpdate(final Long resID, final int updateStatus) {
        try {
            final RemoteSessionReportHandler reportHandler = new RemoteSessionReportHandler();
            final RemoteSessionInfoHandler remoteSessionHandler = new RemoteSessionInfoHandler();
            final Long commandHistoryId = new CommandStatusHandler().getRecentCommandInfo(resID, DeviceCommandRepository.getInstance().getCommandID("RemoteSession")).getLong("COMMAND_HISTORY_ID");
            final Long sessionId = remoteSessionHandler.getSessionIdForCmdHisId(commandHistoryId);
            final Long addedTime = remoteSessionHandler.getSessionAddedTime(commandHistoryId);
            final Long delayInTask = System.currentTimeMillis() - addedTime;
            final int status = remoteSessionHandler.getSessionStatus(commandHistoryId);
            if (status == 1 || status == 11) {
                final JSONObject reportJSON = new JSONObject();
                reportJSON.put("SESSION_ID", (Object)sessionId);
                final JSONObject sessionToCmdHistData = new JSONObject();
                sessionToCmdHistData.put("COMMAND_HISTORY_ID", (Object)commandHistoryId);
                sessionToCmdHistData.put("STATUS", updateStatus);
                remoteSessionHandler.addOrUpdateResourceToRemoteSession(sessionToCmdHistData);
                reportHandler.addOrUpdateRemoteSessionReport(sessionId, reportJSON);
            }
            return status;
        }
        catch (final Exception ex) {
            RemoteSessionManager.LOGGER.log(Level.SEVERE, null, ex);
            return 0;
        }
    }
    
    public int getNoSessionInfoErrorcode() {
        return 7001;
    }
    
    public int getSessionStartedUserApprovedErrorCode() {
        return 6003;
    }
    
    public int getSessionStoppedCode() {
        return 6010;
    }
    
    protected JSONObject obtainActiveSessionId(final Long resourceId) {
        try {
            final JSONObject commandInfo = this.validLastSessionCommandInfo(resourceId);
            if (commandInfo != null) {
                final Long commandHistoryId = commandInfo.getLong("COMMAND_HISTORY_ID");
                final RemoteSessionInfoHandler remoteSessionInfoHandler = new RemoteSessionInfoHandler();
                final int status = remoteSessionInfoHandler.getSessionStatus(commandHistoryId);
                if (status == 1 || status == 2 || status == 4 || status == 11 || status == 12) {
                    final Long sessionId = remoteSessionInfoHandler.getSessionIdForCmdHisId(commandHistoryId);
                    final JSONObject sessionDetails = remoteSessionInfoHandler.getSessionDetails(sessionId);
                    final JSONObject responseData = new JSONObject();
                    responseData.put("Status", (Object)"Success");
                    responseData.put("SessionDetails", (Object)sessionDetails);
                    return responseData;
                }
            }
        }
        catch (final Exception e) {
            RemoteSessionManager.LOGGER.log(Level.SEVERE, "In obtainActiveSessionId(): ", e);
        }
        return null;
    }
    
    protected JSONObject validLastSessionCommandInfo(final Long resourceId) {
        try {
            final JSONObject commandInfo = new CommandStatusHandler().getRecentCommandInfo(resourceId, DeviceCommandRepository.getInstance().getCommandID("RemoteSession"));
            Long addedTime = null;
            addedTime = commandInfo.optLong("ADDED_TIME", -1L);
            if (addedTime != -1L) {
                final Long currentTime = System.currentTimeMillis();
                if (currentTime - addedTime <= RemoteSessionManager.KEY_TIMEOUT) {
                    return commandInfo;
                }
                return null;
            }
        }
        catch (final JSONException e) {
            RemoteSessionManager.LOGGER.log(Level.SEVERE, "In validLastSessionCommand(): ", (Throwable)e);
        }
        return null;
    }
    
    public void PreRemoteSessionCommand(final String action, final Long customerId, final Long resID) {
        this.processTheAction(action, customerId, resID);
    }
    
    private void processTheAction(final String action, final Long customerID, final Long resId) {
        try {
            final List resourceList = new ArrayList();
            resourceList.add(resId);
            if (action.equalsIgnoreCase("enableiOSAgentSettings")) {
                MDMAgentSettingsHandler.getInstance().enableioSMEMDMAppSettings(customerID);
                RemoteSessionManager.LOGGER.log(Level.INFO, "Going to enable ME MDM Settings for all devices when starting remote view");
            }
            else if (action.equalsIgnoreCase("distributeiOSAgent")) {
                RemoteSessionManager.LOGGER.log(Level.INFO, "Intiating action : distributeiOSAgent for ME MDM App in res id: {0} when starting remote view", resId);
                IosNativeAppHandler.getInstance().distributeAndInstallIOSNativeAgent(resourceList, customerID);
            }
            else if (action.equalsIgnoreCase("installiOSAgent")) {
                RemoteSessionManager.LOGGER.log(Level.INFO, "Intiating action : installiOSAgent for ME MDM App in res id: {0} when starting remote view", resId);
                IosNativeAppHandler.getInstance().distributeIOSAgentToDevices(resourceList, customerID, Boolean.FALSE);
            }
            else if (action.equalsIgnoreCase("updateiOSAgent")) {
                RemoteSessionManager.LOGGER.log(Level.INFO, "Intiating action : updateiOSAgent for ME MDM App in res id: {0} when starting remote view", resId);
                IosNativeAppHandler.getInstance().distributeIOSAgentToDevices(resourceList, customerID, Boolean.TRUE);
            }
        }
        catch (final Exception exception) {
            RemoteSessionManager.LOGGER.log(Level.WARNING, "Exception occurred in processTheAction method of invokeCommandAction", exception);
        }
    }
    
    public JSONObject checkiOSRemoteControlCompatibility(final Long resId, final Long custId) throws Exception {
        String osVersion = "";
        final JSONObject preConditionsToValidate = new JSONObject();
        Boolean isIOSScreenCaptureRestricted = false;
        String profileNamesOfScreenCaptureRestricted = "";
        Boolean isAgentRemoteCompatible = false;
        Boolean isAgentInstalled = false;
        Boolean canDeviceSilentlyInstallAgent = false;
        Boolean isAppDeviceAssignable = false;
        Boolean isIOS11AndAbove = false;
        Boolean isAgentDistributedToDevice = true;
        Boolean isAgentAddedToPackage = true;
        final int notificationService = MDMAgentSettingsHandler.getInstance().getNotificaitonServiceType(1, custId);
        final DeviceDetails device = new DeviceDetails(resId);
        long appVersioncodeFromInstalledAppList = -1L;
        final long agentVersionProducedFromApp = device.agentVersionCode;
        final JSONObject appdetailsjson = AppsUtil.getInstance().getInstalledAppDetailsJson(resId, "com.manageengine.mdm.iosagent");
        if (appdetailsjson != null) {
            isAgentInstalled = true;
            final String appVersion = appdetailsjson.optString("APP_NAME_SHORT_VERSION");
            try {
                appVersioncodeFromInstalledAppList = Long.parseLong(appVersion);
            }
            catch (final NumberFormatException numEx) {
                RemoteSessionManager.LOGGER.log(Level.WARNING, "Exception in parsing the ios ME MDM agent version", numEx);
            }
        }
        if (appVersioncodeFromInstalledAppList >= 1430L || agentVersionProducedFromApp >= 1430L) {
            isAgentRemoteCompatible = true;
        }
        final Long appGroupId = AppsUtil.getInstance().getAppGroupIDFromIdentifier("com.manageengine.mdm.iosagent", 1, custId);
        if (!isAgentInstalled) {
            final Long publishedAppId = AppsUtil.getInstance().getPublishedAppId(resId, appGroupId);
            if (publishedAppId == null) {
                isAgentDistributedToDevice = false;
            }
        }
        if (!isAgentDistributedToDevice) {
            isAgentAddedToPackage = AppsUtil.getInstance().isAppExistsInPackage("com.manageengine.mdm.iosagent", 1, custId);
        }
        final Boolean isSupervised = InventoryUtil.getInstance().isSupervisedDevice(resId);
        final Row deviceRow = DBUtil.getRowFromDB("MdDeviceInfo", "RESOURCE_ID", (Object)resId);
        if (deviceRow != null) {
            osVersion = (String)deviceRow.get("OS_VERSION");
        }
        if (ManagedDeviceHandler.getInstance().isOsVersionGreaterThan(osVersion, 11.0f)) {
            isIOS11AndAbove = true;
        }
        final Integer typeOfAssignment = VPPAppMgmtHandler.getInstance().getVppAppAssignmentType(appGroupId);
        if (typeOfAssignment == 2) {
            isAppDeviceAssignable = true;
        }
        if (isSupervised && isIOS11AndAbove && isAppDeviceAssignable) {
            canDeviceSilentlyInstallAgent = true;
        }
        final List profileNamesWithScreenShotRestricted = ProfileUtil.getInstance().getProfileNameWithRestriction(resId, "RestrictionsPolicy", "ALLOW_SCREEN_CAPTURE", "false");
        if (profileNamesWithScreenShotRestricted.size() > 0) {
            isIOSScreenCaptureRestricted = true;
            final Iterator iter = profileNamesWithScreenShotRestricted.iterator();
            while (iter.hasNext()) {
                if (profileNamesOfScreenCaptureRestricted.equalsIgnoreCase("")) {
                    profileNamesOfScreenCaptureRestricted = iter.next();
                }
                else {
                    profileNamesOfScreenCaptureRestricted = profileNamesOfScreenCaptureRestricted + "," + iter.next();
                }
            }
            preConditionsToValidate.put("profileNamesOfScreenCaptureRestricted", (Object)profileNamesOfScreenCaptureRestricted);
        }
        preConditionsToValidate.put("isIOSScreenCaptureRestricted", (Object)isIOSScreenCaptureRestricted);
        preConditionsToValidate.put("canDeviceSilentlyInstallAgent", (Object)canDeviceSilentlyInstallAgent);
        preConditionsToValidate.put("isIOS11AndAbove", (Object)isIOS11AndAbove);
        preConditionsToValidate.put("isAgentRemoteCompatible", (Object)isAgentRemoteCompatible);
        preConditionsToValidate.put("isAgentInstalled", (Object)isAgentInstalled);
        preConditionsToValidate.put("isAgentDistributedToDevice", (Object)isAgentDistributedToDevice);
        preConditionsToValidate.put("isAgentAddedToPackage", (Object)isAgentAddedToPackage);
        preConditionsToValidate.put("notifyServiceType", notificationService);
        new InvDeviceDetailsAction().trackRemoteSessionData(preConditionsToValidate, custId);
        return preConditionsToValidate;
    }
    
    static {
        LOGGER = Logger.getLogger("MDMRemoteControlLogger");
        KEY_TIMEOUT = 1800000L;
    }
}
