package com.adventnet.sym.server.mdm.command;

import com.me.mdm.server.apps.config.feedback.BaseAppConfigurationFeedbackStatusHandler;
import com.me.uem.announcement.AnnouncementHandler;
import com.me.mdm.agent.handlers.DeviceMessageRequest;
import com.me.mdm.server.datausage.DataUsageTrackingHandler;
import com.me.mdm.server.android.message.UpdateTokenHandler;
import com.me.mdm.server.inv.actions.RemoteDebugHandler;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.mdm.server.command.CommandStatusHandler;
import com.me.mdm.server.devicealerts.DeviceAlertsMessageProcessor;
import com.me.mdm.server.updates.osupdates.android.AndroidOSUpdatesMsgProcessor;
import com.adventnet.sym.server.mdm.security.AgentSecretHandler;
import com.adventnet.sym.server.mdm.security.safetynet.SafetyNetHandler;
import java.util.Map;
import com.me.mdm.server.location.LocationDataHandler;
import com.me.mdm.server.android.agentmigrate.AgentMigrationHandler;
import com.me.mdm.server.apps.android.afw.AFWAccountStatusHandler;
import com.me.mdm.server.apps.android.afw.GoogleAccountChangeHandler;
import java.util.ArrayList;
import com.me.mdm.server.updates.osupdates.ResourceOSUpdateDataHandler;
import com.me.mdm.server.apps.android.afw.usermgmt.GoogleManagedAccountHandler;
import com.adventnet.sym.server.mdm.util.VersionChecker;
import com.me.mdm.server.notification.NotificationHandler;
import java.util.List;
import java.util.Arrays;
import com.adventnet.sym.server.mdm.DeviceDetails;
import com.adventnet.sym.server.mdm.enroll.AgentUpgradeTaskHandler;
import com.me.mdm.server.enrollment.MDMAgentUpdateHandler;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.sym.server.mdm.android.AndroidInventory;
import com.adventnet.sym.server.mdm.samsung.SamsungInventory;
import com.me.mdm.server.events.DeviceEventDataHandler;
import com.me.mdm.server.settings.location.MDMGeoLocationHandler;
import com.adventnet.sym.server.mdm.apps.ManagedAppStatusHandler;
import com.me.mdm.server.android.knox.enroll.KnoxActivationManager;
import com.me.mdm.server.android.knox.container.ContainerManagementHandler;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.sym.server.mdm.inv.MDMInvDataPopulator;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.mdm.agent.handlers.windows.WpServerRequestHandler;
import org.json.JSONException;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.agent.util.AppleMessageQueueUtil;
import com.me.mdm.agent.handlers.macOS.MacOSCommandResponseQueueProcessor;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.PlistWrapper;
import com.adventnet.sym.server.mdm.util.AuthTokenUtil;
import com.me.mdm.agent.handlers.chromeos.ChromeInServerAgentCmdResponseQueueProcessor;
import com.me.mdm.agent.handlers.chromeos.ChromeInServerAgentMsgResponseQueueProcessor;
import com.me.mdm.agent.handlers.windows.WpAppMessageResponseQueueProcessor;
import com.me.mdm.agent.handlers.windows.WpAppCommandResponseQueueProcessor;
import com.me.mdm.agent.handlers.ios.IOSAppCommandResponseQueueProcessor;
import com.me.mdm.agent.handlers.ios.IOSAppMessageRequestQueueProcessor;
import com.me.mdm.agent.handlers.android.admin.AdminAgentMessageResponseQueueProcessor;
import com.me.mdm.agent.handlers.android.admin.AdminAgentCommandResponseQueueProcessor;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.queue.DCQueueDataProcessor;

public class CommandProcessor extends DCQueueDataProcessor
{
    public static final Logger LOGGER;
    Logger accesslogger;
    Logger messagesAccessLogger;
    String separator;
    Logger queueLogger;
    
    public CommandProcessor() {
        this.accesslogger = Logger.getLogger("MDMCommandsLogger");
        this.messagesAccessLogger = Logger.getLogger("MDMMessagesLogger");
        this.separator = "\t";
        this.queueLogger = Logger.getLogger("MDMQueueBriefLogger");
    }
    
    public void processData(final DCQueueData qData) {
        final Long sysTime = System.currentTimeMillis();
        this.queueLogger.log(Level.INFO, "CommandProcessingStarted{0}{1}{2}TimeSpentInQueueWaiting - {3}", new Object[] { this.separator, qData.fileName, this.separator, String.valueOf(sysTime - qData.postTime) });
        final int queueType = qData.queueDataType;
        qData.customerID = qData.queueExtnTableData.get("CUSTOMER_ID");
        CommandProcessor.LOGGER.log(Level.INFO, "Queue data type is {0}", queueType);
        if (queueType == 100) {
            this.processIOSData(qData);
        }
        else if (queueType == 101 || queueType == 102) {
            this.processAndroidData(qData);
        }
        else if (queueType == 105) {
            new AdminAgentCommandResponseQueueProcessor().processQueueData(qData);
        }
        else if (queueType == 125) {
            new AdminAgentMessageResponseQueueProcessor().processQueueData(qData);
        }
        else if (queueType == 120) {
            new IOSAppMessageRequestQueueProcessor().processQueueData(qData);
        }
        else if (queueType == 121 || queueType == 122) {
            this.processAndroidMessage(qData);
        }
        else if (queueType == 103) {
            this.processWindowsPhoneData(qData);
        }
        else if (queueType == 140) {
            new IOSAppCommandResponseQueueProcessor().processQueueData(qData);
        }
        else if (queueType == 143) {
            new WpAppCommandResponseQueueProcessor().processQueueData(qData);
        }
        else if (queueType == 123) {
            new WpAppMessageResponseQueueProcessor().processQueueData(qData);
        }
        else if (queueType == 124) {
            this.processNativeMessage(qData);
        }
        else if (queueType == 126) {
            new ChromeInServerAgentMsgResponseQueueProcessor().processQueueData(qData);
        }
        else if (queueType == 106) {
            new ChromeInServerAgentCmdResponseQueueProcessor().processQueueData(qData);
        }
        else if (queueType == 107) {
            this.processMacData(qData);
        }
        else if (queueType == 108) {
            this.processAppleMessageQueueData(qData);
        }
        else if (queueType == 900) {
            AuthTokenUtil.processAuthTokenCheckin(qData);
        }
        else {
            CommandProcessor.LOGGER.log(Level.INFO, "Dropping old queue data / other queuedata");
        }
        this.queueLogger.log(Level.INFO, "CommandProcessingEnded{0}{1}{2} TimeTakenForCommandProcessing -  {3}", new Object[] { this.separator, qData.fileName, this.separator, String.valueOf(System.currentTimeMillis() - sysTime) });
    }
    
    private void processIOSData(final DCQueueData qData) {
        try {
            final String commandResponseData = (String)qData.queueData;
            final HashMap hashMap = PlistWrapper.getInstance().getHashFromPlist(commandResponseData);
            final String commandUUID = hashMap.get("CommandUUID");
            if (commandUUID != null) {
                CommandUtil.getInstance().processCommand(commandResponseData, qData.customerID, hashMap, 100, qData);
            }
        }
        catch (final Exception exp) {
            CommandProcessor.LOGGER.log(Level.SEVERE, " Exception while processing IOS data ", exp);
        }
    }
    
    private void processMacData(final DCQueueData qData) {
        try {
            final String commandResponseData = (String)qData.queueData;
            final HashMap hashMap = PlistWrapper.getInstance().getHashFromPlist(commandResponseData);
            final String commandUUID = hashMap.get("CommandUUID");
            if (commandUUID != null) {
                new MacOSCommandResponseQueueProcessor().processCommand(qData);
            }
        }
        catch (final Exception exp) {
            CommandProcessor.LOGGER.log(Level.SEVERE, " Exception while processing IOS data ", exp);
        }
    }
    
    public void processAppleMessageQueueData(final DCQueueData dcQueueData) {
        try {
            final String strData = (String)dcQueueData.queueData;
            final HashMap hashPlist = PlistWrapper.getInstance().getHashFromPlist(strData);
            final String messageType = hashPlist.get("MessageType");
            CommandProcessor.LOGGER.log(Level.INFO, "inside processAppleMessageQueueData() for messageType:- {0}", messageType);
            if (messageType != null) {
                AppleMessageQueueUtil.getInstance().processMessageQueue(messageType, hashPlist, dcQueueData);
            }
        }
        catch (final Exception e) {
            CommandProcessor.LOGGER.log(Level.SEVERE, "Exception occurred in processAppleMessageQueueData():- ", e);
        }
    }
    
    private void processAndroidData(final DCQueueData dCQueueData) {
        final String strData = (String)dCQueueData.queueData;
        try {
            final HashMap<String, String> hmap = JSONUtil.getInstance().ConvertJSONObjectToHash(new JSONObject(strData));
            final String responsedData = hmap.get("CommandResponse");
            CommandUtil.getInstance().processCommand(responsedData, dCQueueData.customerID, hmap, dCQueueData.queueDataType, dCQueueData);
        }
        catch (final JSONException ex) {
            CommandProcessor.LOGGER.log(Level.SEVERE, "Exception while processing android command", (Throwable)ex);
        }
    }
    
    private void processWindowsPhoneData(final DCQueueData qData) {
        try {
            final WpServerRequestHandler WpSyncMLProcess = new WpServerRequestHandler();
            WpSyncMLProcess.processWpResponseFromQueue(qData);
        }
        catch (final Exception ex) {
            CommandProcessor.LOGGER.log(Level.SEVERE, " Exception while processing Windows Phone data ", ex);
        }
    }
    
    private void processNativeData(final DCQueueData dCQueueData) {
        final String strData = (String)dCQueueData.queueData;
        try {
            final HashMap<String, String> hmap = JSONUtil.getInstance().ConvertJSONObjectToHash(new JSONObject(strData));
            final String responsedData = hmap.get("CommandResponse");
            CommandUtil.getInstance().processCommand(responsedData, dCQueueData.customerID, hmap, 1, dCQueueData);
        }
        catch (final JSONException ex) {
            CommandProcessor.LOGGER.log(Level.SEVERE, "Exception while processing Native command", (Throwable)ex);
        }
    }
    
    private void processIOSMessage(final DCQueueData qData) {
        final String strData = (String)qData.queueData;
        try {
            final JSONObject iosMessageData = new JSONObject(strData);
            final String msgType = iosMessageData.optString("MessageType", "");
            final String sUDID = iosMessageData.optString("UDID", "");
            iosMessageData.put("CUSTOMER_ID", (Object)(qData.customerID + ""));
            final Long resourceID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(sUDID);
            if (msgType.equalsIgnoreCase("Location")) {
                MDMInvDataPopulator.getInstance().processIosLocationMessage(iosMessageData, sUDID);
            }
            final String accessMessage = "DATA-IN: " + msgType + this.separator + resourceID + this.separator + sUDID + this.separator + iosMessageData.optString("Status", "") + this.separator + qData.postTime + this.separator + (MDMUtil.getCurrentTimeInMillis() - qData.postTime);
            this.messagesAccessLogger.log(Level.INFO, accessMessage);
        }
        catch (final Exception ex) {
            CommandProcessor.LOGGER.log(Level.SEVERE, "Exception while process ios message", ex);
        }
    }
    
    private void processAndroidMessage(final DCQueueData qData) {
        final String strData = (String)qData.queueData;
        try {
            final JSONObject requestJson = new JSONObject(strData);
            final HashMap<String, String> hmap = JSONUtil.getInstance().ConvertJSONObjectToHash(requestJson);
            String msgType = null;
            if (hmap.containsKey("MsgRequestType")) {
                msgType = hmap.get("MsgRequestType");
            }
            else if (hmap.containsKey("MessageType")) {
                msgType = hmap.get("MessageType");
            }
            final String sUDID = hmap.get("UDID");
            final Long resourceID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(sUDID);
            hmap.put("PLATFORM_TYPE", "2");
            hmap.put("CUSTOMER_ID", qData.customerID + "");
            if (msgType.equalsIgnoreCase("Enrollment")) {
                MDMEnrollmentUtil.getInstance().updateAndroidResourceDetails(hmap);
            }
            else if (msgType != null && msgType.equalsIgnoreCase("ContainerStatus")) {
                ContainerManagementHandler.getInstance().processMessage(hmap);
            }
            else if (msgType != null && msgType.equalsIgnoreCase("KnoxAvailability")) {
                KnoxActivationManager.getInstance().processIamKnoxMsg(hmap);
            }
            else if (msgType.equalsIgnoreCase("ManagedAppStatus")) {
                new ManagedAppStatusHandler().updateAndroidAppCatalogStatus(strData, qData.customerID);
            }
            else if (msgType.equalsIgnoreCase("Location")) {
                final String geoLocMsg = hmap.get("Message");
                final String strStatus = hmap.get("Status");
                final String strUDID = hmap.get("UDID");
                if (strStatus != null && strStatus.equalsIgnoreCase("Error")) {
                    final int errorCode = Integer.parseInt(hmap.get("ErrorCode"));
                    MDMGeoLocationHandler.getInstance().addorUpdateDeviceLocationErrorCode(resourceID, errorCode);
                }
                else if (strStatus != null) {
                    MDMGeoLocationHandler.getInstance().deleteDeviceLocationErrorCode(resourceID);
                    MDMGeoLocationHandler.getInstance().addOrUpdateDeviceLocationDetails(new JSONObject(geoLocMsg), sUDID);
                }
            }
            else if (msgType.equalsIgnoreCase("DeviceEvents")) {
                final JSONObject eventDataJson = new JSONObject((String)hmap.get("Message"));
                new DeviceEventDataHandler().handleDeviceEventsUpdate(resourceID, eventDataJson);
            }
            else if (msgType.equalsIgnoreCase("PolicyInfo")) {
                final JSONObject poliJson = new JSONObject();
                final String messageRes = hmap.get("Message");
                poliJson.put("PolicyInfo", (Object)new JSONObject(messageRes));
                final int queueType = qData.queueDataType;
                if (queueType == 122) {
                    SamsungInventory.getSamsungInventoryInstance("Device").processSamsungCompliance(resourceID, poliJson);
                }
                else if (queueType == 121) {
                    AndroidInventory.processAndroidComplaince(poliJson, resourceID);
                }
            }
            else if (msgType.equalsIgnoreCase("AgentUpgrade")) {
                final JSONObject resData = new JSONObject();
                final Long beforeAgentVersionCode = (Long)DBUtil.getValueFromDB("ManagedDevice", "UDID", (Object)sUDID, "AGENT_VERSION_CODE");
                resData.put("LAST_VERSION_CODE", (Object)beforeAgentVersionCode);
                resData.put("UDID", (Object)sUDID);
                MDMAgentUpdateHandler.getInstance().updateAgentUpgradeStatus(hmap);
                final AgentUpgradeTaskHandler taskHandler = new AgentUpgradeTaskHandler();
                taskHandler.processAgentUpgrade(resData);
            }
            else if (msgType.equalsIgnoreCase("OSUpgraded")) {
                DeviceCommandRepository.getInstance().addDeviceScanCommand(new DeviceDetails(resourceID), null);
                AndroidInventory.getInstance().initSystemAppCommand(resourceID);
                DeviceCommandRepository.getInstance().addKNOXAvailabilityCommand(Arrays.asList(resourceID), "GetKnoxAvailabilityUpgrade");
                NotificationHandler.getInstance().SendNotification(Arrays.asList(resourceID), 2);
                Long versionCode = (Long)DBUtil.getValueFromDB("ManagedDevice", "RESOURCE_ID", (Object)resourceID, "AGENT_VERSION_CODE");
                versionCode %= 100000L;
                final String updgradedOSVersion = String.valueOf(new JSONObject((String)hmap.get("Message")).get("OSVersion"));
                final String oldOsVersion = (String)DBUtil.getValueFromDB("MdDeviceInfo", "RESOURCE_ID", (Object)resourceID, "OS_VERSION");
                if (!oldOsVersion.matches("[0-9.]+") && new VersionChecker().isGreater("7.0", oldOsVersion)) {
                    new GoogleManagedAccountHandler().checkAndAddAFWAccountForSamsung(resourceID, versionCode, updgradedOSVersion);
                }
                new ResourceOSUpdateDataHandler().deleteAvailableUpdatesForResource(resourceID, new ArrayList<String>());
            }
            else if (msgType.equalsIgnoreCase("GoogleAccountChanged")) {
                final String msg = hmap.get("Message");
                new GoogleAccountChangeHandler().processGoogleAccountChangeInManagedDevice(resourceID, new JSONObject(msg));
            }
            else if (msgType.equalsIgnoreCase("GooglePlayActivationReq")) {
                new GoogleAccountChangeHandler().processGooglePlayActivationMsg(resourceID, new JSONObject((String)hmap.get("Message")));
            }
            else if (msgType.equalsIgnoreCase("AFWAccountStatusUpdate")) {
                final JSONObject statusMsg = new JSONObject((String)hmap.get("Message"));
                new AFWAccountStatusHandler().handleAfWAccountStatusFromDevice(statusMsg, resourceID);
            }
            else if (msgType.equalsIgnoreCase("AgentMigrationStatus")) {
                AgentMigrationHandler.getInstance().processAgentMigrationStatus(hmap);
            }
            else if (msgType.equalsIgnoreCase("LostModeDisabled")) {
                CommandProcessor.LOGGER.log(Level.INFO, "Lost mode disabled by user. ResourceID:{0}, UDID:{1}", new Object[] { resourceID, sUDID });
            }
            else if (msgType.equalsIgnoreCase("LocationUpdate")) {
                LocationDataHandler.getInstance().deviceLocationUpdates(resourceID, hmap);
            }
            else if (msgType.equalsIgnoreCase("SafetyNetResponse")) {
                CommandProcessor.LOGGER.log(Level.INFO, " Going to process safety net response");
                final SafetyNetHandler handler = new SafetyNetHandler();
                final JSONObject responseData = new JSONObject(strData);
                if (handler.isError(responseData)) {
                    if (!handler.isAlreadyAttestedDevice(resourceID)) {
                        handler.storeErrorStates(responseData.optInt("ErrorCode"), responseData.optString("ErrorMsg"), hmap.get("UDID"));
                        new AgentSecretHandler().deleteSecretDetails(String.valueOf(((JSONObject)responseData.get("Message")).get("SafetyNetId")));
                    }
                    else {
                        final String safetyNetId = String.valueOf(((JSONObject)responseData.get("Message")).get("SafetyNetId"));
                        CommandProcessor.LOGGER.log(Level.INFO, " The device seems to be having some temporary issues which is causing safety net to fail, Erasing the corresponding safety net ID");
                        new AgentSecretHandler().deleteSecretDetails(safetyNetId);
                    }
                }
                else {
                    handler.storeSafetyNetResponse(responseData);
                }
            }
            else if (msgType.equalsIgnoreCase("PendingOSUpdates") || msgType.equalsIgnoreCase("OsDownloadFailure") || msgType.equalsIgnoreCase("StorageUsability") || msgType.equalsIgnoreCase("OsDownloadSuccess")) {
                new AndroidOSUpdatesMsgProcessor().processResponse(resourceID, new JSONObject((String)hmap.get("Message")), msgType);
            }
            else if (msgType.equalsIgnoreCase("SecurityPatchLevelUpdated")) {
                new ResourceOSUpdateDataHandler().deleteAvailableUpdatesForResource(resourceID, new ArrayList<String>());
            }
            else if (msgType.equalsIgnoreCase("TokenUpdate")) {
                MDMEnrollmentUtil.getInstance().processGCMReregistration(hmap);
            }
            else if (msgType.equalsIgnoreCase("DeviceAlerts")) {
                DeviceAlertsMessageProcessor.getInstance().processDeviceAlertsMessage(new JSONObject(strData));
            }
            else if (msgType.equalsIgnoreCase("RemoteDebug")) {
                final Long commandId = DeviceCommandRepository.getInstance().getCommandID("RemoteDebug");
                final JSONObject commandStatusJSON = new CommandStatusHandler().getRecentCommandInfo(resourceID, commandId);
                final String sDeviceName = ManagedDeviceHandler.getInstance().getDeviceName(resourceID);
                commandStatusJSON.put("COMMAND_ID", (Object)commandId);
                commandStatusJSON.put("RESOURCE_ID", (Object)resourceID);
                final String strStatus2 = hmap.get("Status");
                if (strStatus2 != null && strStatus2.equalsIgnoreCase("Error")) {
                    final String userName = DMUserHandler.getUserNameFromUserID(JSONUtil.optLongForUVH(commandStatusJSON, "ADDED_BY", Long.valueOf(-1L)));
                    commandStatusJSON.put("COMMAND_STATUS", 0);
                    final int errorCode2 = Integer.parseInt(hmap.get("ErrorCode"));
                    String errorMessage = "";
                    switch (errorCode2) {
                        case 12254:
                        case 12255:
                        case 12257: {
                            errorMessage = "dc.mdm.inv.remote_debug_failed_upload_failed";
                            break;
                        }
                        case 12252:
                        case 12253: {
                            errorMessage = "dc.mdm.inv.remote_debug_failed_prerequsites_failed";
                            break;
                        }
                        case 12256: {
                            errorMessage = "dc.mdm.inv.remote_debug_failed_user_declined";
                            break;
                        }
                    }
                    commandStatusJSON.put("REMARKS", (Object)errorMessage);
                    commandStatusJSON.put("ERROR_CODE", errorCode2);
                    new CommandStatusHandler().populateCommandStatus(commandStatusJSON);
                    MDMEventLogHandler.getInstance().MDMEventLogEntry(2172, null, userName, "dc.mdm.actionlog.inv.remote_debug_failed", sDeviceName + "@@@" + hmap.get("ErrorMsg"), qData.customerID);
                }
                else if (strStatus2.equalsIgnoreCase("Acknowledged")) {
                    final JSONObject requestData = new JSONObject(strData);
                    if (requestData.has("file_id")) {
                        new RemoteDebugHandler().handleRemoteDebugRequest(resourceID, requestData);
                        if (commandStatusJSON.has("ADDED_BY")) {
                            final String userName2 = DMUserHandler.getUserNameFromUserID(JSONUtil.optLongForUVH(commandStatusJSON, "ADDED_BY", Long.valueOf(-1L)));
                            commandStatusJSON.put("COMMAND_STATUS", 2);
                            new CommandStatusHandler().populateCommandStatus(commandStatusJSON);
                            MDMEventLogHandler.getInstance().MDMEventLogEntry(2171, null, userName2, "dc.mdm.actionlog.inv.remote_debug_success", sDeviceName, qData.customerID);
                        }
                    }
                }
            }
            else if (msgType.equalsIgnoreCase("UpdateToken")) {
                new UpdateTokenHandler().handleUpdateToken(resourceID, new JSONObject((String)hmap.get("MsgRequest")));
            }
            else if (msgType.equals("DataUsageMessage") || msgType.equals("DetailedDataUsageMessage")) {
                new DataUsageTrackingHandler(2, resourceID).parseAndPersistResourceReport(new JSONObject((Map)hmap));
            }
            else if (msgType.equalsIgnoreCase("AnnouncementAck")) {
                final DeviceMessageRequest request = new DeviceMessageRequest(requestJson);
                final JSONObject anJSON = request.messageRequest;
                AnnouncementHandler.newInstance().updateAcknowledgment(anJSON, resourceID);
            }
            else if (msgType.equalsIgnoreCase("AnnouncementRead")) {
                final DeviceMessageRequest request = new DeviceMessageRequest(requestJson);
                final JSONObject anJSON = request.messageRequest;
                AnnouncementHandler.newInstance().updateAnnouncementRead(anJSON, resourceID);
            }
            else if (msgType.equalsIgnoreCase("SyncAnnouncementAck")) {
                final DeviceMessageRequest request = new DeviceMessageRequest(requestJson);
                final JSONObject anJSON = request.messageRequest;
                AnnouncementHandler.newInstance().processAnnouncementStatusForResource(resourceID, anJSON);
            }
            else if (msgType.equalsIgnoreCase("ManagedAppFeedback")) {
                BaseAppConfigurationFeedbackStatusHandler.getInstance(2).parseAndStoreAppConfigFeedback(strData, qData.customerID);
            }
            else if (msgType.equalsIgnoreCase("DetectUserGSuiteAccount")) {
                final String msg = hmap.get("Message");
                new GoogleAccountChangeHandler().processGoogleAccountAvailable(resourceID, new JSONObject(msg));
            }
            else if (msgType.equalsIgnoreCase("CapabilitiesInfo")) {
                final DeviceMessageRequest request = new DeviceMessageRequest(requestJson);
                final JSONObject anJSON = request.messageRequest;
                MDMInvDataPopulator.getInstance().addOrUpdateCapabilitiesInfo(resourceID, anJSON.getJSONObject("CapabilitiesInfo"));
            }
            else {
                CommandProcessor.LOGGER.log(Level.WARNING, "Unknown Message Type  : {0}", msgType);
            }
            final String accessMessage = "DATA-IN: " + msgType + this.separator + resourceID + this.separator + sUDID + this.separator + hmap.get("Status") + this.separator + qData.postTime + this.separator + (MDMUtil.getCurrentTimeInMillis() - qData.postTime);
            this.messagesAccessLogger.log(Level.INFO, accessMessage);
        }
        catch (final Exception ex) {
            CommandProcessor.LOGGER.log(Level.SEVERE, "Exception while process android message", ex);
        }
    }
    
    private void processNativeMessage(final DCQueueData qData) {
        final String strData = (String)qData.queueData;
        try {
            final HashMap<String, String> hmap = JSONUtil.getInstance().ConvertJSONObjectToHash(new JSONObject(strData));
            final String msgType = hmap.get("MessageType");
            final String sUDID = hmap.get("UDID");
            final String strStatus = hmap.get("Status");
            hmap.put("CUSTOMER_ID", qData.customerID + "");
            if (msgType.equalsIgnoreCase("RegistrationStatusUpdate")) {
                if (strStatus != null && strStatus.equalsIgnoreCase("Error")) {
                    MDMAgentUpdateHandler.getInstance().updateAgentUpgradeStatus(hmap);
                }
                else {
                    MDMAgentUpdateHandler.getInstance().updateAgentUpgradeStatus(hmap);
                }
            }
            else {
                CommandProcessor.LOGGER.log(Level.WARNING, "Unknown Message Type  : {0}", msgType);
            }
            final Long resourceID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(sUDID);
            final String accessMessage = "DATA-IN: " + msgType + this.separator + resourceID + this.separator + sUDID + this.separator + hmap.get("Status") + this.separator + qData.postTime + this.separator + (MDMUtil.getCurrentTimeInMillis() - qData.postTime);
            this.messagesAccessLogger.log(Level.INFO, accessMessage);
        }
        catch (final Exception ex) {
            CommandProcessor.LOGGER.log(Level.SEVERE, "Exception while process Native app message", ex);
        }
    }
    
    static {
        LOGGER = Logger.getLogger("MDMLogger");
    }
}
