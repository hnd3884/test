package com.me.mdm.api.command;

import com.me.mdm.api.APIRequest;
import com.me.mdm.server.inv.actions.ActionConstants;
import com.me.mdm.server.inv.actions.InvActionUtil;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import com.me.mdm.server.customgroup.GroupFacade;
import java.sql.Timestamp;
import com.me.mdm.server.resource.MDMResourceDataProvider;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.me.mdm.api.command.schedule.ScheduledActionsUtils;
import java.util.Set;
import com.me.mdm.core.enrollment.AdminEnrollmentHandler;
import com.me.mdm.core.enrollment.EnrollmentTemplateHandler;
import org.json.JSONException;
import java.util.Iterator;
import com.me.mdm.api.command.container.ContainerCommandWrapper;
import com.me.mdm.server.inv.actions.resource.InventoryAction;
import com.me.mdm.api.command.device.DeviceCommandWrapper;
import com.me.mdm.server.inv.actions.resource.InventoryActionList;
import com.me.mdm.server.metracker.MEMDMTrackParamManager;
import com.me.mdm.api.command.mac.DeviceRestartOptionsAPI;
import com.me.mdm.server.inv.actions.ClearAppDataHandler;
import java.util.Map;
import com.adventnet.sym.server.mdm.security.MacDeviceUserUnlockHandler;
import com.me.mdm.server.enrollment.ios.AppleAccessRightsHandler;
import com.adventnet.sym.server.mdm.security.RemoteWipeHandler;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.onelinelogger.MDMOneLineLogger;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.me.mdm.server.notification.NotificationHandler;
import com.me.mdm.server.inv.actions.RemoteDebugHandler;
import com.me.mdm.server.privacy.PrivacySettingsHandler;
import com.adventnet.sym.server.mdm.security.ResetPasscodeHandler;
import com.adventnet.sym.server.mdm.command.LockScreenMessageUtil;
import com.me.mdm.server.inv.actions.InvActionUtilProvider;
import com.me.mdm.server.device.resource.Device;
import com.google.gson.Gson;
import com.adventnet.sym.server.mdm.DeviceDetails;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import java.util.Date;
import com.me.mdm.server.command.kiosk.KioskPauseResumeManager;
import com.me.mdm.server.settings.location.MDMGeoLocationHandler;
import com.adventnet.sym.server.mdm.command.DeviceInvCommandHandler;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.devicemanagement.framework.server.common.ErrorCodeHandler;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.me.mdm.server.command.CommandStatusHandler;
import com.me.mdm.server.location.lostmode.LostModeDataHandler;
import com.me.mdm.server.remotesession.RemoteSessionManager;
import com.me.mdm.api.APIUtil;
import com.me.mdm.server.device.DeviceFacade;
import java.util.HashSet;
import java.util.Collection;
import java.util.Arrays;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.logging.Logger;

public class CommandFacade
{
    protected static Logger logger;
    protected static Logger assignUserLogger;
    private static Logger actionLogger;
    
    public JSONArray getDeviceCommandStatus(final JSONObject message) throws APIHTTPException {
        try {
            final Long deviceId = JSONUtil.optLongForUVH(message.getJSONObject("msg_header").getJSONObject("resource_identifier"), "device_id", (Long)null);
            final String commandName = String.valueOf(message.getJSONObject("msg_header").getJSONObject("resource_identifier").get("command_name"));
            List<Long> resourceIDs = new ArrayList<Long>();
            if (deviceId != 0L) {
                resourceIDs = new ArrayList<Long>(Arrays.asList(deviceId));
            }
            else {
                resourceIDs = new ArrayList<Long>(new HashSet<Long>(JSONUtil.getInstance().convertLongJSONArrayTOList(message.getJSONObject("msg_body").getJSONArray("DEVICE_IDS"))));
            }
            new DeviceFacade().validateIfDevicesExists(resourceIDs, APIUtil.getCustomerID(message));
            if (commandName.equalsIgnoreCase("RemoteSession")) {
                final RemoteSessionManager manager = new RemoteSessionManager();
                final JSONArray array = new JSONArray();
                final JSONObject deviceJSON = new JSONObject();
                deviceJSON.put("id", (Object)deviceId);
                int status;
                try {
                    status = manager.getSessionStatus(deviceId);
                }
                catch (final Exception e) {
                    status = 1;
                }
                deviceJSON.put("status", status);
                array.put((Object)deviceJSON);
                return array;
            }
            if (commandName.equals("EnableLostMode")) {
                int lostModeStatus = new LostModeDataHandler().getLostModeStatus(deviceId);
                final JSONArray array = new JSONArray();
                final JSONObject deviceJSON = new JSONObject();
                deviceJSON.put("id", (Object)deviceId);
                String kbUrl = null;
                switch (lostModeStatus) {
                    case 1: {
                        lostModeStatus = 1;
                        break;
                    }
                    case 2: {
                        lostModeStatus = 2;
                        break;
                    }
                    case 3: {
                        lostModeStatus = 0;
                        final JSONObject commandInfoJSON = new CommandStatusHandler().getRecentCommandInfo(deviceId, DeviceCommandRepository.getInstance().getCommandID(commandName));
                        kbUrl = ErrorCodeHandler.getInstance().getKBURL((long)JSONUtil.optLongForUVH(commandInfoJSON, "ERROR_CODE", Long.valueOf(-1L)));
                        break;
                    }
                }
                deviceJSON.put("status", lostModeStatus);
                deviceJSON.put("kb_url", (Object)kbUrl);
                deviceJSON.put("lost_mode_info", (Object)new LostModeDataHandler().getLostModeDeviceInfo(deviceId, ManagedDeviceHandler.getInstance().getPlatformType(deviceId), true));
                array.put((Object)deviceJSON);
                return array;
            }
            if (commandName.equals("DisableLostMode")) {
                int lostModeStatus = new LostModeDataHandler().getLostModeStatus(deviceId);
                final JSONArray array = new JSONArray();
                final JSONObject deviceJSON = new JSONObject();
                deviceJSON.put("id", (Object)deviceId);
                String kbUrl = null;
                switch (lostModeStatus) {
                    case 4: {
                        lostModeStatus = 1;
                        break;
                    }
                    case 5: {
                        lostModeStatus = 2;
                        break;
                    }
                    case 6: {
                        lostModeStatus = 0;
                        final JSONObject commandInfoJSON = new CommandStatusHandler().getRecentCommandInfo(deviceId, DeviceCommandRepository.getInstance().getCommandID(commandName));
                        kbUrl = ErrorCodeHandler.getInstance().getKBURL((long)JSONUtil.optLongForUVH(commandInfoJSON, "ERROR_CODE", Long.valueOf(-1L)));
                        break;
                    }
                }
                deviceJSON.put("status", lostModeStatus);
                deviceJSON.put("kb_url", (Object)kbUrl);
                array.put((Object)deviceJSON);
                return array;
            }
            JSONArray array2 = DeviceInvCommandHandler.getInstance().getDeviceCommandStatus(resourceIDs, commandName);
            if (array2 != null && array2.length() > 0) {
                final JSONObject statusJSON = array2.getJSONObject(0);
                final int status2 = statusJSON.getInt("status");
                final String s = commandName;
                switch (s) {
                    case "GetLocation": {
                        if (status2 == 0) {
                            final String kbUrl2 = ErrorCodeHandler.getInstance().getKBURL((long)MDMGeoLocationHandler.getInstance().getLocationErrorCode(deviceId));
                            statusJSON.put("kb_url", (Object)kbUrl2);
                            break;
                        }
                        break;
                    }
                    case "PauseKioskCommand": {
                        final JSONObject pauseKioskInfo = new JSONObject();
                        final KioskPauseResumeManager manager2 = new KioskPauseResumeManager();
                        pauseKioskInfo.put("kiosk_password", (Object)manager2.getDeviceSpecificKioskPassword(deviceId));
                        pauseKioskInfo.put("to_time", (Object)new Date(System.currentTimeMillis() / 10000000L * 10000000L + 10000000L));
                        statusJSON.put("pause_kiosk_info", (Object)pauseKioskInfo);
                        break;
                    }
                }
                array2 = new JSONArray().put((Object)statusJSON);
            }
            return array2;
        }
        catch (final Exception e2) {
            if (e2 instanceof APIHTTPException) {
                throw (APIHTTPException)e2;
            }
            CommandFacade.logger.log(Level.SEVERE, "exception in getDeviceCommandStatus", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONArray getDeviceScanStatus(final JSONObject message) throws APIHTTPException {
        try {
            final Long deviceId = JSONUtil.optLongForUVH(message.getJSONObject("msg_header").getJSONObject("resource_identifier"), "device_id", (Long)null);
            List<Long> resourceIDs = new ArrayList<Long>();
            if (deviceId != 0L) {
                resourceIDs = new ArrayList<Long>(Arrays.asList(deviceId));
            }
            else {
                resourceIDs = new ArrayList<Long>(new HashSet<Long>(JSONUtil.getInstance().convertLongJSONArrayTOList(message.getJSONObject("msg_body").getJSONArray("DEVICE_IDS"))));
            }
            new DeviceFacade().validateIfDevicesExists(resourceIDs, APIUtil.getCustomerID(message));
            return DeviceInvCommandHandler.getInstance().getDeviceScanStatus(resourceIDs);
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            CommandFacade.logger.log(Level.SEVERE, "exception in getDeviceScanStatus", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject executeDeviceCommand(final JSONObject message) throws APIHTTPException {
        try {
            final HashMap infoMap = new HashMap();
            final Long deviceId = JSONUtil.optLongForUVH(message.getJSONObject("msg_header").getJSONObject("resource_identifier"), "device_id", (Long)null);
            final Long customerId = APIUtil.getCustomerID(message);
            String commandName = String.valueOf(message.getJSONObject("msg_header").getJSONObject("resource_identifier").get("command_name"));
            final Long userId = APIUtil.getUserID(message);
            infoMap.put("technicianID", userId);
            infoMap.put("isSilentCommand", "false");
            List<Long> resourceIDs = new ArrayList<Long>();
            DeviceDetails deviceDetails = null;
            if (deviceId != 0L) {
                resourceIDs = new ArrayList<Long>(Arrays.asList(deviceId));
                new DeviceFacade().validateIfDeviceExists(deviceId, APIUtil.getCustomerID(message));
                deviceDetails = new DeviceDetails(deviceId);
            }
            else {
                resourceIDs = new ArrayList<Long>(new HashSet<Long>(JSONUtil.getInstance().convertLongJSONArrayTOList(message.getJSONObject("msg_body").getJSONArray("device_ids"))));
                new DeviceFacade().validateIfDevicesExists(resourceIDs, APIUtil.getCustomerID(message));
                deviceDetails = new DeviceDetails(resourceIDs.get(0));
            }
            final Gson gson = new Gson();
            final String jsonString = new DeviceFacade().getDevice(message).toString();
            final Device device = (Device)gson.fromJson(jsonString, (Class)Device.class);
            final InventoryActionList inventoryActionList = InvActionUtilProvider.getInvActionUtil(deviceDetails.platform).getApplicableActions(device, APIUtil.getCustomerID(message));
            final boolean isValid = this.validateCommand(inventoryActionList, commandName, deviceId);
            if (!isValid) {
                throw new APIHTTPException("CMD0001", new Object[0]);
            }
            if (commandName.equalsIgnoreCase("DeviceLock")) {
                String phoneNumber = "";
                String lockMessage = "";
                String unlockPin = "";
                Boolean sendEmailToUser = null;
                if (message.has("msg_body")) {
                    final StringBuilder error = null;
                    final JSONObject msgBody = message.getJSONObject("msg_body");
                    phoneNumber = msgBody.optString("phone_number");
                    lockMessage = msgBody.optString("lock_message");
                    unlockPin = msgBody.optString("unlock_pin");
                    sendEmailToUser = msgBody.optBoolean("email_sent_to_user");
                }
                final JSONObject lockscreenJSON = new JSONObject();
                lockscreenJSON.put("resourceId", (Object)deviceId);
                lockscreenJSON.put("phoneNumber", (Object)phoneNumber);
                lockscreenJSON.put("lockMessage", (Object)lockMessage);
                lockscreenJSON.put("unlockPin", (Object)unlockPin);
                lockscreenJSON.put("sendEmailToUser", (Object)sendEmailToUser);
                LockScreenMessageUtil.getInstance().addorUpdateLockScreenMessage(lockscreenJSON);
                DeviceInvCommandHandler.getInstance().invokeCommand(resourceIDs, commandName, infoMap);
            }
            else if (commandName.equals("EnableLostMode")) {
                message.put("PLATFORM_TYPE", deviceDetails.platform);
                message.put("RESOURCE_ID", (Object)deviceId);
                this.sendEnableLostModeCommand(message, customerId, userId);
            }
            else {
                if (commandName.equalsIgnoreCase("RemoteSession")) {
                    final RemoteSessionManager manager = new RemoteSessionManager();
                    final JSONObject json = manager.startSession(deviceId, userId);
                    return json;
                }
                if (commandName.equals("DisableLostMode")) {
                    message.put("PLATFORM_TYPE", deviceDetails.platform);
                    message.put("RESOURCE_ID", (Object)deviceId);
                    this.sendDisableLostModeCommand(message, customerId, userId);
                }
                else if (commandName.equals("ResetPasscode")) {
                    if (!message.has("msg_body")) {
                        throw new APIHTTPException("COM0005", new Object[0]);
                    }
                    final JSONObject body = message.getJSONObject("msg_body");
                    final String newPasscode = body.optString("passcode");
                    final Boolean sendEmailToUser2 = body.optBoolean("email_sent_to_user", false);
                    final Boolean sendEmailToAdmin = body.optBoolean("email_sent_to_admin", false);
                    final JSONObject devicePascodeObj = new JSONObject();
                    devicePascodeObj.put("RESOURCE_ID", (Object)deviceId);
                    devicePascodeObj.put("PASSCODE", (Object)newPasscode);
                    devicePascodeObj.put("EMAIL_SENT_TO_USER", (Object)sendEmailToUser2);
                    devicePascodeObj.put("EMAIL_SENT_TO_ADMIN", (Object)sendEmailToAdmin);
                    devicePascodeObj.put("UPDATED_TIME", System.currentTimeMillis());
                    devicePascodeObj.put("UPDATED_BY", (Object)userId);
                    new ResetPasscodeHandler().addorUpdateDevicePasscode(devicePascodeObj);
                    DeviceInvCommandHandler.getInstance().invokeCommand(resourceIDs, "ResetPasscode", infoMap);
                }
                else if (commandName.equalsIgnoreCase("RemoteDebug")) {
                    final JSONObject body = message.getJSONObject("msg_body");
                    final JSONArray emailList = body.optJSONArray("email_address_list");
                    final boolean retry = body.optBoolean("retry", false);
                    final JSONObject privacySettings = new PrivacySettingsHandler().getPrivacyDetails(ManagedDeviceHandler.getInstance().getDeviceOwnership(deviceId), APIUtil.getCustomerID(message));
                    if (privacySettings.getInt("disable_bug_report") != 1) {
                        throw new APIHTTPException("COM0015", new Object[0]);
                    }
                    if (!retry && emailList.length() <= 0) {
                        throw new APIHTTPException("COM0005", new Object[0]);
                    }
                    final RemoteDebugHandler remoteDebugHandler = new RemoteDebugHandler();
                    final JSONObject remoteDebugData = new JSONObject();
                    if (retry) {
                        final JSONObject commandInfo = new CommandStatusHandler().getRecentCommandInfo(deviceId, DeviceCommandRepository.getInstance().getCommandID("RemoteDebug"));
                        final JSONObject previousCommandDetails = remoteDebugHandler.getRemoteDebugRequestData(commandInfo.getLong("COMMAND_HISTORY_ID"));
                        final String[] prevEmailList = String.valueOf(previousCommandDetails.get("email_address_list")).split(",");
                        final JSONArray prevEmailListJson = new JSONArray();
                        for (final String email : prevEmailList) {
                            prevEmailListJson.put((Object)email);
                        }
                        remoteDebugData.put("email_address_list", (Object)prevEmailListJson);
                        remoteDebugData.put("ticket_id", (Object)String.valueOf(previousCommandDetails.get("ticket_id")));
                        remoteDebugData.put("description", (Object)String.valueOf(previousCommandDetails.get("description")));
                    }
                    else {
                        remoteDebugData.put("email_address_list", (Object)emailList);
                        remoteDebugData.put("ticket_id", (Object)body.optString("ticket_id", ""));
                        remoteDebugData.put("description", (Object)body.optString("description", ""));
                    }
                    final Long commandId = DeviceCommandRepository.getInstance().addCommand("RemoteDebug");
                    final JSONObject commandHistoryData = new JSONObject();
                    final String remarks = "dc.mdm.inv.remote_debug_initiated";
                    commandHistoryData.put("RESOURCE_ID", (Object)deviceId);
                    commandHistoryData.put("COMMAND_ID", (Object)commandId);
                    commandHistoryData.put("ADDED_BY", (Object)userId);
                    commandHistoryData.put("REMARKS", (Object)remarks);
                    commandHistoryData.put("COMMAND_STATUS", 1);
                    final Long commandHistoryId = new CommandStatusHandler().populateCommandStatus(commandHistoryData);
                    remoteDebugData.put("command_history_id", (Object)commandHistoryId);
                    remoteDebugData.put("resource_id", (Object)deviceId);
                    remoteDebugHandler.addRemoteDebugData(remoteDebugData);
                    DeviceCommandRepository.getInstance().assignCommandToDevices(commandId, resourceIDs);
                    NotificationHandler.getInstance().SendNotification(resourceIDs, 2);
                    MDMEventLogHandler.getInstance().MDMEventLogEntry(2173, deviceId, DMUserHandler.getUserNameFromUserID(userId), "dc.mdm.actionlog.inv.remote_debug_initiated", deviceDetails.name, customerId);
                }
                else if (commandName.equalsIgnoreCase("PauseKioskCommand")) {
                    if (!message.has("msg_body")) {
                        throw new APIHTTPException("COM0005", new Object[0]);
                    }
                    final JSONObject msgBody2 = message.getJSONObject("msg_body");
                    final Long reenterTime = JSONUtil.optLongForUVH(msgBody2, "re_enter_time", Long.valueOf(-1L));
                    final String actionReason = msgBody2.optString("remarks", "");
                    final HashMap kioskCommandInfo = new HashMap();
                    kioskCommandInfo.put("commandType", "PauseKioskCommand");
                    kioskCommandInfo.put("ReEnterTime", reenterTime);
                    kioskCommandInfo.put("actionReason", actionReason);
                    final KioskPauseResumeManager manager2 = new KioskPauseResumeManager();
                    manager2.addKioskCommand(deviceId, userId, kioskCommandInfo);
                    infoMap.put("isSilentCommand", "false");
                    infoMap.put("remarks", actionReason);
                    DeviceInvCommandHandler.getInstance().invokeCommand(resourceIDs, commandName, infoMap);
                    final org.json.simple.JSONObject logJson = new org.json.simple.JSONObject();
                    logJson.put((Object)"RESOURCE_ID", (Object)resourceIDs);
                    logJson.put((Object)"REMARKS", (Object)"command-initiated");
                    MDMOneLineLogger.log(Level.INFO, "PAUSE_KIOSK", logJson);
                }
                else if (commandName.equalsIgnoreCase("ResumeKioskCommand")) {
                    final JSONObject commandHistoryData2 = new JSONObject();
                    commandHistoryData2.put("RESOURCE_ID", (Object)deviceId);
                    final Long commandId2 = DeviceCommandRepository.getInstance().addCommand("ResumeKioskCommand");
                    commandHistoryData2.put("COMMAND_ID", (Object)commandId2);
                    commandHistoryData2.put("ADDED_BY", (Object)userId);
                    new CommandStatusHandler().populateCommandStatus(commandHistoryData2);
                    DeviceInvCommandHandler.getInstance().invokeCommand(resourceIDs, "ResumeKioskCommand", infoMap);
                    final org.json.simple.JSONObject logJson2 = new org.json.simple.JSONObject();
                    logJson2.put((Object)"RESOURCE_ID", (Object)resourceIDs);
                    logJson2.put((Object)"REMARKS", (Object)"command-initiated");
                    MDMOneLineLogger.log(Level.INFO, "RESUME_KIOSK", logJson2);
                }
                else if (commandName.equalsIgnoreCase("EraseDevice")) {
                    final int lostModeStatus = new LostModeDataHandler().getLostModeStatus(device.getResourceId());
                    if (lostModeStatus != 2 && lostModeStatus != 1) {
                        final JSONObject json = new PrivacySettingsHandler().getPrivacySettingsJSON(deviceId);
                        if ((int)json.get("disable_wipe") == 2) {
                            throw new APIHTTPException("CMD0001", new Object[0]);
                        }
                    }
                    final Boolean isMacDevice = MDMUtil.getInstance().isMacDevice(deviceId);
                    if (deviceDetails.platform == 2 || isMacDevice) {
                        if (!message.has("msg_body")) {
                            throw new APIHTTPException("COM0005", new Object[0]);
                        }
                        final JSONObject wipeOptionData = new JSONObject();
                        final JSONObject msgBody3 = message.getJSONObject("msg_body");
                        final String wipeLockPin = msgBody3.optString("wipe_lock_pin", (String)null);
                        final boolean allowWipeSDCard = msgBody3.optBoolean("wipe_sd_card", false);
                        final boolean retainMDM = msgBody3.optBoolean("wipe_but_retain_mdm", false);
                        wipeOptionData.put("RESOURCE_ID", (Object)deviceId);
                        wipeOptionData.put("WIPE_SD_CARD", (Object)allowWipeSDCard);
                        wipeOptionData.put("WIPE_BUT_RETAIN_MDM", (Object)retainMDM);
                        if (!MDMStringUtils.isEmpty(wipeLockPin)) {
                            wipeOptionData.put("WIPE_LOCK_PIN", (Object)wipeLockPin);
                        }
                        final boolean success = new RemoteWipeHandler().addOrUpdateDeviceWipeOptions(wipeOptionData);
                        if (!success) {
                            throw new APIHTTPException("COM0005", new Object[0]);
                        }
                    }
                    else if (deviceDetails.platform == 1) {
                        final Integer accessRights = AppleAccessRightsHandler.getInstance().getAccessRightsForResourceId(deviceId);
                        if (accessRights != null && !AppleAccessRightsHandler.isAccessRightProvided("DEVICE_ERASE", accessRights)) {
                            throw new APIHTTPException("CMD0004", new Object[0]);
                        }
                    }
                    final JSONObject deprovisionJson = new JSONObject();
                    deprovisionJson.put("RESOURCE_ID", (Object)deviceId);
                    deprovisionJson.put("DEPROVISION_TYPE", 2);
                    deprovisionJson.put("COMMENT", (Object)"default");
                    ManagedDeviceHandler.addOrUpdateDeprovisionHistory(deprovisionJson, DMUserHandler.getUserIdForLoginId(DMUserHandler.getLoginId()));
                    DeviceInvCommandHandler.getInstance().invokeCommand(resourceIDs, commandName, infoMap);
                    final org.json.simple.JSONObject secLog = new org.json.simple.JSONObject();
                    secLog.put((Object)"RESOURCE_ID", (Object)resourceIDs.toString());
                    secLog.put((Object)"REMARKS", (Object)"command-initiated");
                    MDMOneLineLogger.log(Level.INFO, "COMPLETE_WIPE", secLog);
                }
                else if (commandName.equalsIgnoreCase("UnlockUserAccount")) {
                    final Boolean isMacDevice2 = MDMUtil.getInstance().isMacDevice(deviceId);
                    if (!isMacDevice2) {
                        throw new APIHTTPException("CMD0001", new Object[0]);
                    }
                    if (!message.has("msg_body")) {
                        throw new APIHTTPException("COM0005", new Object[0]);
                    }
                    final JSONObject accountData = new JSONObject();
                    final JSONObject msgBody4 = message.getJSONObject("msg_body");
                    final String userName = msgBody4.getString("user_name");
                    accountData.put("RESOURCE_ID", (Object)deviceId);
                    accountData.put("USERNAME", (Object)userName);
                    new MacDeviceUserUnlockHandler().addOrUpdateDeviceUnlockOption(accountData);
                    DeviceInvCommandHandler.getInstance().invokeCommand(resourceIDs, commandName, infoMap);
                }
                else if (commandName.equalsIgnoreCase("DeviceRing")) {
                    if (deviceDetails.platform == 1) {
                        commandName = "PlayLostModeSound";
                    }
                    DeviceInvCommandHandler.getInstance().invokeCommand(resourceIDs, commandName, infoMap);
                }
                else if (commandName.equalsIgnoreCase("ClearAppData")) {
                    final List<Long> deviceIds = new ArrayList<Long>();
                    deviceIds.add(deviceId);
                    this.validateAndExecuteClearAppDataCommand(message, customerId, deviceIds, userId, deviceDetails.platform, new HashMap());
                    ClearAppDataHandler.getInstance().updateClearAppDataEventLog(deviceId, customerId, userId);
                }
                else if (commandName.equalsIgnoreCase("GetLocation")) {
                    if (deviceDetails.platform == 1 && new LostModeDataHandler().isLostMode(deviceId) && ManagedDeviceHandler.getInstance().isSupervisedAnd9_3Above(deviceId)) {
                        commandName = "LostModeDeviceLocation";
                    }
                    DeviceInvCommandHandler.getInstance().invokeCommand(resourceIDs, commandName, infoMap);
                }
                else if (commandName.equalsIgnoreCase("ClearPasscode")) {
                    final JSONObject json2 = new PrivacySettingsHandler().getPrivacySettingsJSON(deviceId);
                    if ((int)json2.get("disable_clear_passcode") == 2) {
                        throw new APIHTTPException("CMD0004", new Object[0]);
                    }
                    DeviceInvCommandHandler.getInstance().invokeCommand(resourceIDs, commandName, infoMap);
                }
                else if (commandName.equalsIgnoreCase("RestartDevice")) {
                    final JSONObject messageBody = message.optJSONObject("msg_body");
                    if (messageBody != null) {
                        final boolean isNotifyUser = messageBody.optBoolean("is_notify_user", false);
                        final Map<String, String> optionsMap = new HashMap<String, String>();
                        optionsMap.put("NotifyUser", String.valueOf(isNotifyUser));
                        DeviceRestartOptionsAPI.addOrUpdateRestartOptions(deviceId, optionsMap);
                    }
                    DeviceInvCommandHandler.getInstance().invokeCommand(resourceIDs, commandName, infoMap);
                }
                else if (commandName.equalsIgnoreCase("CorporateWipe")) {
                    DeviceInvCommandHandler.getInstance().invokeCommand(resourceIDs, commandName, infoMap);
                    final JSONObject deprovisionJson2 = new JSONObject();
                    deprovisionJson2.put("RESOURCE_ID", (Object)deviceId);
                    deprovisionJson2.put("DEPROVISION_TYPE", 1);
                    deprovisionJson2.put("COMMENT", (Object)"default");
                    ManagedDeviceHandler.addOrUpdateDeprovisionHistory(deprovisionJson2, DMUserHandler.getUserIdForLoginId(DMUserHandler.getLoginId()));
                    final org.json.simple.JSONObject secLog2 = new org.json.simple.JSONObject();
                    secLog2.put((Object)"RESOURCE_ID", (Object)resourceIDs.toString());
                    secLog2.put((Object)"REMARKS", (Object)"command-initiated");
                    MDMOneLineLogger.log(Level.INFO, "CORPORATE_WIPE", secLog2);
                }
                else {
                    DeviceInvCommandHandler.getInstance().invokeCommand(resourceIDs, commandName, infoMap);
                }
            }
            MEMDMTrackParamManager.getInstance().incrementTrackValue(customerId, "INVENTORY_ACTIONS_MODULE", commandName);
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            CommandFacade.logger.log(Level.SEVERE, "exception in executeDeviceCommand", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return new JSONObject();
    }
    
    private void validateAndExecuteClearAppDataCommand(final JSONObject request, final Long customerId, final List<Long> deviceIds, final Long userId, final int platform, final Map infoMap) throws Exception {
        if (request.has("msg_body")) {
            final JSONObject messageBody = request.getJSONObject("msg_body");
            CommandFacade.logger.log(Level.INFO, "Validating clear app data command {0}", messageBody.toString());
            final Boolean clearDataForAllApps = messageBody.optBoolean("clear_data_for_all_apps");
            final JSONObject clearAppDataInfo = new JSONObject();
            if (!clearDataForAllApps) {
                if (!messageBody.has("inclusion") || !messageBody.has("app_ids")) {
                    throw new APIHTTPException("COM0009", new Object[0]);
                }
                final Boolean includeApps = messageBody.optBoolean("inclusion", true);
                Collection<Long> appGroupIds = JSONUtil.getInstance().convertLongJSONArrayTOList(messageBody.getJSONArray("app_ids"));
                ClearAppDataHandler.getInstance().validateClearDataApps(appGroupIds, customerId, platform);
                appGroupIds = new HashSet<Long>(appGroupIds);
                clearAppDataInfo.put("INCLUSION", (Object)includeApps);
                clearAppDataInfo.put("app_group_ids", (Collection)appGroupIds);
                infoMap.put("app_group_ids", appGroupIds);
            }
            clearAppDataInfo.put("CLEAR_DATA_FOR_ALL_APPS", (Object)clearDataForAllApps);
            clearAppDataInfo.put("devices", (Collection)deviceIds);
            clearAppDataInfo.put("ADDED_BY", (Object)userId);
            clearAppDataInfo.put("PLATFORM_TYPE", platform);
            clearAppDataInfo.put("action_id", messageBody.opt("action_id"));
            clearAppDataInfo.put("command_name", (Object)messageBody.optString("command_name", "ClearAppData"));
            new ClearAppDataHandler().executeClearAppDataCommand(clearAppDataInfo, infoMap);
            return;
        }
        throw new APIHTTPException("COM0005", new Object[0]);
    }
    
    private boolean validateCommand(final InventoryActionList inventoryActionList, final String commandName, final Long deviceId) {
        boolean isValid = false;
        List actionList = inventoryActionList.actions;
        if (actionList != null) {
            final DeviceCommandWrapper deviceCommandWrapper = new DeviceCommandWrapper();
            for (final Object obj : actionList) {
                final InventoryAction inventoryAction = (InventoryAction)obj;
                if (deviceCommandWrapper.getEquivalentCommandName(inventoryAction.name).equalsIgnoreCase(commandName) && inventoryAction.isEnabled) {
                    isValid = true;
                }
            }
        }
        actionList = inventoryActionList.knoxActions;
        if (actionList != null) {
            final ContainerCommandWrapper containerCommandWrapper = new ContainerCommandWrapper();
            for (final Object obj : actionList) {
                final InventoryAction inventoryAction = (InventoryAction)obj;
                if (containerCommandWrapper.getEquivalentCommandName(inventoryAction.name).equalsIgnoreCase(commandName) && inventoryAction.isEnabled) {
                    isValid = true;
                }
            }
        }
        if (!isValid && commandName.equalsIgnoreCase("EnableLostMode") && new LostModeDataHandler().isDeviceInLostMode(deviceId)) {
            isValid = true;
        }
        return isValid;
    }
    
    private boolean validateCommandAPIName(final InventoryActionList inventoryActionList, final String commandName) {
        boolean isValid = false;
        final List actionList = inventoryActionList.actions;
        if (actionList != null) {
            for (final Object obj : actionList) {
                final InventoryAction inventoryAction = (InventoryAction)obj;
                if (inventoryAction.name.equalsIgnoreCase(commandName)) {
                    isValid = true;
                }
            }
        }
        return isValid;
    }
    
    public void suspendDeviceCommand(final JSONObject message) throws APIHTTPException {
        try {
            final Long deviceId = JSONUtil.optLongForUVH(message.getJSONObject("msg_header").getJSONObject("resource_identifier"), "device_id", (Long)null);
            final String commandName = String.valueOf(message.getJSONObject("msg_header").getJSONObject("resource_identifier").get("command_name"));
            List<Long> resourceIDs = new ArrayList<Long>();
            if (deviceId != 0L) {
                resourceIDs = new ArrayList<Long>(Arrays.asList(deviceId));
                new DeviceFacade().validateIfDeviceExists(deviceId, APIUtil.getCustomerID(message));
            }
            else {
                resourceIDs = new ArrayList<Long>(new HashSet<Long>(JSONUtil.getInstance().convertLongJSONArrayTOList(message.getJSONObject("msg_body").getJSONArray("DEVICE_IDS"))));
                new DeviceFacade().validateIfDevicesExists(resourceIDs, APIUtil.getCustomerID(message));
            }
            if (!DeviceInvCommandHandler.getInstance().suspendCommandExceution(resourceIDs, commandName)) {
                throw new APIHTTPException("CMD0002", new Object[] { "Command Does Not Exist or Is Completed" });
            }
        }
        catch (final JSONException e) {
            CommandFacade.logger.log(Level.SEVERE, "exception in suspendDeviceCommand", (Throwable)e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void executeContainerCommand(final JSONObject message) throws APIHTTPException {
        try {
            final Long deviceId = JSONUtil.optLongForUVH(message.getJSONObject("msg_header").getJSONObject("resource_identifier"), "device_id", (Long)null);
            final String commandName = String.valueOf(message.getJSONObject("msg_header").getJSONObject("resource_identifier").get("command_name"));
            final Long userId = APIUtil.getUserID(message);
            final String userName = APIUtil.getUserName(message);
            final Long customerId = APIUtil.getCustomerID(message);
            List<Long> resourceIDs = new ArrayList<Long>();
            if (deviceId != 0L) {
                resourceIDs = new ArrayList<Long>(Arrays.asList(deviceId));
                new DeviceFacade().validateIfDeviceExists(deviceId, APIUtil.getCustomerID(message));
            }
            else {
                resourceIDs = new ArrayList<Long>(new HashSet<Long>(JSONUtil.getInstance().convertLongJSONArrayTOList(message.getJSONObject("msg_body").getJSONArray("DEVICE_IDS"))));
                new DeviceFacade().validateIfDevicesExists(resourceIDs, APIUtil.getCustomerID(message));
            }
            for (final Long resourceID : resourceIDs) {
                final DeviceDetails deviceDetails = new DeviceDetails(resourceID);
                DeviceInvCommandHandler.getInstance().SendCommandToContainer(deviceDetails, commandName, userId);
                final JSONObject requestJSON = new JSONObject();
                requestJSON.put("COMMAND_TYPE", (Object)commandName);
                requestJSON.put("COMMAND_STATUS", 1);
                requestJSON.put("NAME", (Object)deviceDetails.name);
                requestJSON.put("PLATFORM_TYPE", deviceDetails.platform);
                requestJSON.put("RESOURCE_ID", (Object)resourceID);
                final String remarks = DeviceCommandRepository.getInstance().getCommandRemarks(requestJSON);
                MDMEventLogHandler.getInstance().MDMEventLogEntry(2051, resourceID, userName, remarks, null, customerId);
            }
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            CommandFacade.logger.log(Level.SEVERE, "Exception occurred in executeContainerCommand", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void scanDevices(final JSONObject message) throws APIHTTPException {
        try {
            final Long deviceId = JSONUtil.optLongForUVH(message.getJSONObject("msg_header").getJSONObject("resource_identifier"), "device_id", (Long)null);
            List<Long> resourceIDs = new ArrayList<Long>();
            final Long customerId = APIUtil.getCustomerID(message);
            final Long userId = APIUtil.getUserID(message);
            final String userName = DMUserHandler.getUserNameFromUserID(userId);
            if (deviceId != 0L) {
                resourceIDs = new ArrayList<Long>(Arrays.asList(deviceId));
                new DeviceFacade().validateIfDeviceExists(deviceId, APIUtil.getCustomerID(message));
            }
            else {
                resourceIDs = new ArrayList<Long>(new HashSet<Long>(JSONUtil.getInstance().convertLongJSONArrayTOList(message.getJSONObject("msg_body").getJSONArray("DEVICE_IDS"))));
                new DeviceFacade().validateIfDevicesExists(resourceIDs, APIUtil.getCustomerID(message));
            }
            DeviceInvCommandHandler.getInstance().scanDevice(resourceIDs, userId);
            final String sEventLogRemarks = "dc.mdm.actionlog.inv.scan_success";
            final HashMap deviceIdNameMap = ManagedDeviceHandler.getInstance().getDeviceNames(resourceIDs);
            final ArrayList<Object> remarksArgsList = new ArrayList<Object>();
            for (final Long resourceID : resourceIDs) {
                final String remarksArgs = deviceIdNameMap.get(resourceID);
                remarksArgsList.add(remarksArgs + "@@@" + userName);
            }
            MDMEventLogHandler.getInstance().addEvent(2041, resourceIDs, userName, sEventLogRemarks, remarksArgsList, customerId, MDMUtil.getCurrentTimeInMillis());
        }
        catch (final Exception ex) {
            CommandFacade.logger.log(Level.SEVERE, "Exception in scanDevices()", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void assignUser(final JSONObject request) throws APIHTTPException {
        try {
            final JSONObject details = new JSONObject();
            final Long customerId = APIUtil.getCustomerID(request);
            final JSONObject requestBody = request.getJSONObject("msg_body");
            final Long technicianUserId = JSONUtil.optLongForUVH(request.getJSONObject("msg_header").getJSONObject("filters"), "user_id", (Long)null);
            final String primaryKeyLabel = "primary_key";
            final Long deviceID = JSONUtil.optLongForUVH(request.getJSONObject("msg_header").getJSONObject("resource_identifier"), "staged_device_id", (Long)null);
            details.put("DomainName", (Object)requestBody.optString("domain", "MDM"));
            details.put("primary_key", (Object)"1");
            details.put("EmailAddr", requestBody.opt("email_id"));
            details.put("UserName", requestBody.opt("username"));
            details.put("GroupName", requestBody.opt("group_name"));
            details.put("DeviceName", requestBody.opt("device_name"));
            final JSONArray grpArr = requestBody.optJSONArray("group_ids");
            if (grpArr != null && grpArr.length() > 0) {
                details.put("GroupId", (Object)grpArr);
            }
            details.put("PHONE_NUMBER", requestBody.opt("phone_number"));
            details.put("user_id", (Object)JSONUtil.optLongForUVH(requestBody, "user_id", Long.valueOf(-1L)));
            final JSONObject DFEdetails = new EnrollmentTemplateHandler().getDeviceForEnrollmentIDDetails(deviceID, customerId);
            if (DFEdetails == null) {
                throw new APIHTTPException("COM0008", new Object[] { "staged_device_id - " + deviceID });
            }
            details.put("IMEI", DFEdetails.opt("IMEI"));
            details.put("SerialNumber", DFEdetails.opt("SERIAL_NUMBER"));
            details.put("CustomerId", DFEdetails.get("CUSTOMER_ID"));
            details.put("UDID", DFEdetails.opt("UDID"));
            this.performAssignUserTracking((Long)DFEdetails.get("CUSTOMER_ID"), (Integer)DFEdetails.get("TEMPLATE_TYPE"));
            final List<JSONObject> list = new ArrayList<JSONObject>();
            list.add(details);
            final JSONObject result = AdminEnrollmentHandler.assignUser(list, technicianUserId, DFEdetails.getInt("TEMPLATE_TYPE"), primaryKeyLabel, DFEdetails.getInt("PLATFORM_TYPE"));
            final JSONArray failedList = result.getJSONArray("FailedList");
            if (failedList.length() != 0) {
                final JSONObject err = failedList.getJSONObject(0);
                throw new APIHTTPException("SDE0001", new Object[] { String.valueOf(err.get("ErrorMsg")) });
            }
        }
        catch (final Exception e) {
            CommandFacade.logger.log(Level.SEVERE, "Exception occurred in assignUser", e);
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private void performAssignUserTracking(final Long customerID, final Integer templateType) {
        if (templateType == 31) {
            try {
                MEMDMTrackParamManager.getInstance().incrementTrackValue(customerID, "Windows_UEM_Module", "Uem_Device_Assigned");
            }
            catch (final Exception e) {
                CommandFacade.logger.log(Level.WARNING, "UEM tracking not tracked", e);
            }
        }
    }
    
    public void scheduleEmptyGroup(final Map requestMap) throws Exception {
        final Long userId = Long.valueOf(requestMap.get("user_id").toString());
        final String commandName = requestMap.get("Command").toString();
        final Long group_id = requestMap.containsKey("group_id") ? Long.valueOf(requestMap.get("group_id").toString()) : -1L;
        final JSONObject requestJSON = JSONUtil.mapToJSON(requestMap);
        final Long action_id = requestMap.containsKey("action_id") ? Long.valueOf(requestMap.get("action_id").toString()) : -1L;
        final HashMap commandMap = this.getPlatformBasedCommandName(commandName);
        final String deviceCommandType = commandMap.get(1);
        final HashMap emptyScheduledGroupActionInfo = new HashMap();
        emptyScheduledGroupActionInfo.put("is_group_action", requestMap.containsKey("is_group_action") ? requestMap.get("is_group_action") : Boolean.FALSE);
        emptyScheduledGroupActionInfo.put("reason_message", requestMap.get("reason_message"));
        emptyScheduledGroupActionInfo.put("user_id", userId);
        emptyScheduledGroupActionInfo.put("group_id", group_id);
        CommandFacade.actionLogger.log(Level.INFO, "Adding a groupAction for the empty group:{0}", new Object[] { group_id });
        DeviceInvCommandHandler.getInstance().createScheduledAction(new ArrayList<Long>(), deviceCommandType, emptyScheduledGroupActionInfo, action_id, requestJSON);
    }
    
    public void invokeBukActions(final HashMap requestMap) {
        try {
            final String commandName = requestMap.get("Command").toString();
            final Long userId = Long.valueOf(requestMap.get("user_id").toString());
            final Long customerId = Long.valueOf(requestMap.get("customer_id").toString());
            final String user_name = DMUserHandler.getUserNameFromUserID(userId);
            final Long action_id = requestMap.containsKey("action_id") ? Long.valueOf(requestMap.get("action_id").toString()) : -1L;
            final Set resourceSet = requestMap.get("valid_devices");
            final Long group_id = requestMap.containsKey("group_id") ? Long.valueOf(requestMap.get("group_id").toString()) : -1L;
            final JSONObject requestJSON = JSONUtil.mapToJSON(requestMap);
            final List resourceList = new ArrayList();
            resourceList.addAll(resourceSet);
            final HashMap deviceMap = ManagedDeviceHandler.getInstance().getPlatformBasedMemberId(resourceList);
            final HashMap commandMap = this.getPlatformBasedCommandName(commandName);
            final Iterator keySet = commandMap.keySet().iterator();
            List successDeviceList = new ArrayList();
            if (requestMap.containsKey("is_empty")) {
                this.scheduleEmptyGroup(requestMap);
            }
            else {
                while (keySet.hasNext()) {
                    final int platform = keySet.next();
                    final String deviceCommandType = commandMap.get(platform);
                    final HashSet deviceSet = deviceMap.get(platform);
                    final List toSendCommandList = new ArrayList();
                    final HashMap infoMap = new HashMap();
                    infoMap.put("is_group_action", requestMap.containsKey("is_group_action") ? requestMap.get("is_group_action") : Boolean.FALSE);
                    infoMap.put("reason_message", requestMap.get("reason_message"));
                    infoMap.put("user_id", userId);
                    infoMap.put("group_id", group_id);
                    final org.json.simple.JSONArray validDevicesJSONArray = InvActionUtilProvider.getInvActionUtil(platform).getApplicableBulkActionDevices(deviceSet, commandName, customerId);
                    for (int i = 0; i < validDevicesJSONArray.size(); ++i) {
                        final org.json.simple.JSONObject currentDeviceJSON = (org.json.simple.JSONObject)validDevicesJSONArray.get(i);
                        Integer status = null;
                        String remarks = null;
                        final Long deviceId = Long.valueOf(currentDeviceJSON.get((Object)"RESOURCE_ID").toString());
                        final Integer resourceType = ScheduledActionsUtils.getResourceTypeForResourceID(deviceId);
                        final Boolean isComputerFeatureParamEnabled = MDMFeatureParamsHandler.getInstance().isFeatureEnabledInDB("SCHEDULED_ACTION_COMPUTER_DEVICE_BLOCK");
                        if (!resourceType.equals(121) || isComputerFeatureParamEnabled || !requestMap.containsKey("scheduled") || platform == 4) {
                            final JSONObject tempJSON = new JSONObject();
                            toSendCommandList.add(deviceId);
                            successDeviceList.add(deviceId);
                            status = 2;
                            remarks = "Success";
                            tempJSON.put("device_id", (Object)deviceId);
                            tempJSON.put("status", (Object)status);
                            tempJSON.put("remarks", (Object)remarks);
                            resourceSet.remove(deviceId);
                            deviceSet.remove(deviceId);
                        }
                    }
                    if (toSendCommandList.size() > 0) {
                        if (requestMap.containsKey("scheduled")) {
                            DeviceInvCommandHandler.getInstance().invokeScheduledBulkCommand(toSendCommandList, deviceCommandType, platform, infoMap, action_id, requestJSON);
                        }
                        else if (deviceCommandType.equals("EnableLostMode")) {
                            final JSONObject req = new JSONObject();
                            req.put("devices", (Collection)toSendCommandList);
                            req.put("PLATFORM_TYPE", platform);
                            req.put("msg_body", (Object)requestJSON);
                            this.sendEnableLostModeCommand(req, customerId, userId);
                        }
                        else if (deviceCommandType.equals("DisableLostMode")) {
                            final JSONObject req = new JSONObject();
                            req.put("devices", (Collection)toSendCommandList);
                            req.put("PLATFORM_TYPE", platform);
                            req.put("msg_body", (Object)requestJSON);
                            this.sendDisableLostModeCommand(req, customerId, userId);
                        }
                        else if (deviceCommandType.equalsIgnoreCase("ClearAppData")) {
                            requestJSON.put("command_name", (Object)deviceCommandType);
                            requestJSON.put("action_id", (Object)action_id);
                            final JSONObject request = new JSONObject();
                            request.put("msg_body", (Object)requestJSON);
                            this.validateAndExecuteClearAppDataCommand(request, customerId, toSendCommandList, userId, platform, infoMap);
                        }
                        else {
                            DeviceInvCommandHandler.getInstance().invokeBulkCommand(toSendCommandList, deviceCommandType, platform, infoMap, action_id);
                        }
                    }
                    else {
                        if (!requestMap.containsKey("scheduled")) {
                            continue;
                        }
                        this.scheduleEmptyGroup(requestMap);
                    }
                }
            }
            if (group_id != -1L) {
                successDeviceList = new ArrayList();
                successDeviceList.add(group_id);
            }
            String remarksText = "mdm.bulkactions.auditlog.device";
            if (group_id != -1L) {
                remarksText = "mdm.bulkactions.auditlog.group";
            }
            if (commandName.equalsIgnoreCase("clear_app_data")) {
                remarksText = "mdm.bulkactions.auditlog.resetapps.device";
                if (group_id != -1L) {
                    remarksText = "mdm.bulkactions.auditlog.resetapps.group";
                }
            }
            final HashMap resourseVsName = MDMResourceDataProvider.getResourceNames(successDeviceList);
            final List<Object> remarksArgsList = new ArrayList<Object>();
            for (Object remarksArgs : resourseVsName.keySet()) {
                if (commandName.equalsIgnoreCase("clear_app_data")) {
                    remarksArgs = resourseVsName.get(remarksArgs);
                }
                else if (requestMap.containsKey("scheduled")) {
                    if (requestJSON.has("group_action_id")) {
                        remarksText = "mdm.bulkactions.auditlog.scheduled_modify_group";
                        final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                        remarksArgs = resourseVsName.get(remarksArgs) + "@@@" + timestamp.toString() + "@@@" + requestJSON.getString("reason_message");
                    }
                    else {
                        final String reasonMessage = requestJSON.optString("reason_message");
                        final int executionType = requestJSON.optInt("execution_type");
                        if (executionType == 2) {
                            remarksText = "mdm.bulkactions.auditlog.scheduled_repeat";
                            String remarksActionName = "";
                            if (commandName == "restart") {
                                remarksActionName = "Remote Restart";
                            }
                            else {
                                remarksActionName = "Remote Shutdown";
                            }
                            final JSONObject scheduleParams = requestJSON.getJSONObject("schedule_params");
                            remarksArgs = remarksActionName + "@@@" + scheduleParams.getString("schedule_type") + "@@@" + resourseVsName.get(remarksArgs) + "@@@" + requestJSON.getString("reason_message");
                        }
                        else {
                            remarksText = "mdm.bulkactions.auditlog.scheduled_setTime";
                            String remarksActionName = "";
                            if (commandName == "restart") {
                                remarksActionName = "Remote Restart";
                            }
                            else {
                                remarksActionName = "Remote Shutdown";
                            }
                            final Long timeinMilliestime = requestJSON.getLong("schedule_once_time");
                            final String time = new Date(timeinMilliestime).toString();
                            remarksArgs = remarksActionName + "@@@" + resourseVsName.get(remarksArgs) + "@@@" + time + "@@@" + requestJSON.getString("reason_message");
                        }
                    }
                }
                else {
                    remarksArgs = commandName + "@@@" + resourseVsName.get(remarksArgs);
                }
                remarksArgsList.add(remarksArgs);
            }
            MDMEventLogHandler.getInstance().addEvent(2051, user_name, remarksText, remarksArgsList, customerId, new Long(System.currentTimeMillis()));
        }
        catch (final Exception e) {
            CommandFacade.logger.log(Level.SEVERE, "invokeBukActions()    error, ", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void executeScheduledBulkDeviceCommand(final JSONObject requestJSON) {
        try {
            final String commandName = requestJSON.getString("Command");
            final String actionName = requestJSON.getString("scheduled");
            final HashMap queueData = JSONUtil.convertJSONtoMap(requestJSON);
            final Long userID = requestJSON.getLong("user_id");
            final Long customerID = requestJSON.getLong("customer_id");
            queueData.put("customer_id", String.valueOf(customerID));
            queueData.put("user_id", String.valueOf(userID));
            queueData.put("Command", commandName);
            queueData.put("scheduled", actionName);
            final String reasonMsg = requestJSON.optString("reason_message", "--");
            requestJSON.put("reason_message", (Object)reasonMsg);
            final JSONArray devicesJSONArray = requestJSON.optJSONArray("devices");
            final JSONArray groupsJSONArray = requestJSON.optJSONArray("groups");
            if (requestJSON == null && devicesJSONArray == null && groupsJSONArray == null) {
                throw new APIHTTPException("COM0006", new Object[0]);
            }
            final HashSet resourceSet = new HashSet();
            if (devicesJSONArray != null) {
                final List<Long> deviceList = JSONUtil.getInstance().convertLongJSONArrayTOList(devicesJSONArray);
                new DeviceFacade().validateIfDevicesExists(deviceList, customerID);
                resourceSet.addAll(deviceList);
                queueData.put("valid_devices", resourceSet);
                new DeviceInvCommandHandler();
                DeviceInvCommandHandler.getInstance().addToQueue(queueData);
                return;
            }
            if (groupsJSONArray != null) {
                final List<Long> groupList = JSONUtil.getInstance().convertLongJSONArrayTOList(groupsJSONArray);
                for (final Long group_id : groupList) {
                    final HashMap queueDataForGroup = JSONUtil.convertJSONtoMap(requestJSON);
                    queueDataForGroup.put("customer_id", String.valueOf(customerID));
                    queueDataForGroup.put("user_id", String.valueOf(userID));
                    queueDataForGroup.put("Command", commandName);
                    queueDataForGroup.put("reason_message", reasonMsg);
                    final HashSet resourceSetForGroup = new HashSet();
                    final List<Long> currGroupList = new ArrayList<Long>();
                    currGroupList.add(group_id);
                    new GroupFacade().validateGroupsIfExists(currGroupList, customerID);
                    final List<Integer> resourceTypeList = new ArrayList<Integer>();
                    resourceTypeList.add(120);
                    resourceTypeList.add(121);
                    final List<Long> deviceList2 = MDMGroupHandler.getMemberIdListForGroups(currGroupList, resourceTypeList);
                    final Map actionMap = new HashMap();
                    resourceSetForGroup.addAll(deviceList2);
                    actionMap.put("action", InvActionUtil.getEquivalentActionType(commandName));
                    actionMap.put("group_id", group_id);
                    actionMap.put("reason", reasonMsg);
                    actionMap.put("user_id", userID);
                    actionMap.put("scheduled", actionName);
                    long action_id = -1L;
                    if (requestJSON.has("group_action_id")) {
                        action_id = requestJSON.getLong("group_action_id");
                        MDMGroupHandler.getInstance().updateGroupRemarks(action_id, reasonMsg, userID);
                    }
                    else {
                        action_id = MDMGroupHandler.getInstance().populateGroupActionDetails(actionMap);
                    }
                    if (deviceList2.isEmpty()) {
                        queueDataForGroup.put("is_empty", true);
                    }
                    queueDataForGroup.put("group_id", group_id);
                    queueDataForGroup.put("action_id", action_id);
                    queueDataForGroup.put("is_group_action", Boolean.TRUE);
                    queueDataForGroup.put("valid_devices", resourceSetForGroup);
                    new DeviceInvCommandHandler();
                    DeviceInvCommandHandler.getInstance().addToQueue(queueDataForGroup);
                }
            }
        }
        catch (final Exception e) {
            CommandFacade.actionLogger.log(Level.SEVERE, "executeJerseyBulkDeviceCommand()    error, ", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void executeBulkDeviceCommand(final JSONObject requestJSON) {
        try {
            final String commandName = String.valueOf(requestJSON.get("Command"));
            if (!Arrays.asList(ActionConstants.BULK_ACTION_LIST).contains(commandName)) {
                throw new APIHTTPException("CMD0001", new Object[0]);
            }
            final Long userId = APIUtil.getUserID(requestJSON);
            final Long customerId = APIUtil.getCustomerID(requestJSON);
            final JSONObject bodyJSON = requestJSON.optJSONObject("msg_body");
            final HashMap queueData = JSONUtil.convertJSONtoMap(bodyJSON);
            final JSONArray devicesJSONArray = bodyJSON.optJSONArray("devices");
            final JSONArray groupsJSONArray = bodyJSON.optJSONArray("groups");
            final String reasonMsg = bodyJSON.optString("reason_message", "--");
            queueData.put("customer_id", String.valueOf(customerId));
            queueData.put("user_id", String.valueOf(userId));
            queueData.put("Command", commandName);
            queueData.put("reason_message", reasonMsg);
            if (bodyJSON == null && devicesJSONArray == null && groupsJSONArray == null) {
                throw new APIHTTPException("COM0006", new Object[0]);
            }
            final HashSet resourceSet = new HashSet();
            if (devicesJSONArray != null) {
                final List<Long> deviceList = JSONUtil.getInstance().convertLongJSONArrayTOList(devicesJSONArray);
                new DeviceFacade().validateIfDevicesExists(deviceList, customerId);
                resourceSet.addAll(deviceList);
                queueData.put("valid_devices", resourceSet);
                new DeviceInvCommandHandler();
                DeviceInvCommandHandler.getInstance().addToQueue(queueData);
                return;
            }
            if (groupsJSONArray != null) {
                final List<Long> groupList = JSONUtil.getInstance().convertLongJSONArrayTOList(groupsJSONArray);
                for (final Long group_id : groupList) {
                    final HashMap queueDataForGroup = JSONUtil.convertJSONtoMap(bodyJSON);
                    queueDataForGroup.put("customer_id", String.valueOf(customerId));
                    queueDataForGroup.put("user_id", String.valueOf(userId));
                    queueDataForGroup.put("Command", commandName);
                    queueDataForGroup.put("reason_message", reasonMsg);
                    final HashSet resourceSetForGroup = new HashSet();
                    final List<Long> currGroupList = new ArrayList<Long>();
                    currGroupList.add(group_id);
                    new GroupFacade().validateGroupsIfExists(currGroupList, customerId);
                    final List<Integer> resourceTypeList = new ArrayList<Integer>();
                    resourceTypeList.add(120);
                    resourceTypeList.add(121);
                    final List<Long> deviceList2 = MDMGroupHandler.getMemberIdListForGroups(currGroupList, resourceTypeList);
                    if (deviceList2.isEmpty()) {
                        continue;
                    }
                    resourceSetForGroup.addAll(deviceList2);
                    final Map actionMap = new HashMap();
                    actionMap.put("action", InvActionUtil.getEquivalentActionType(commandName));
                    actionMap.put("group_id", group_id);
                    actionMap.put("reason", reasonMsg);
                    actionMap.put("user_id", APIUtil.getUserID(requestJSON));
                    final Long action_id = MDMGroupHandler.getInstance().populateGroupActionDetails(actionMap);
                    queueDataForGroup.put("group_id", group_id);
                    queueDataForGroup.put("action_id", action_id);
                    queueDataForGroup.put("is_group_action", Boolean.TRUE);
                    queueDataForGroup.put("valid_devices", resourceSetForGroup);
                    new DeviceInvCommandHandler();
                    DeviceInvCommandHandler.getInstance().addToQueue(queueDataForGroup);
                }
            }
        }
        catch (final Exception e) {
            CommandFacade.logger.log(Level.SEVERE, "executeBulkDeviceCommand()    error, ", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private void sendEnableLostModeCommand(final JSONObject requestJSON, final Long customerId, final Long userId) {
        String phoneNumber = "";
        String lockMessage = "";
        String ticketId = null;
        String auditMessage = null;
        String passCode = null;
        boolean sendEmailToUser = false;
        boolean sendEmailToAdmin = false;
        final LostModeDataHandler lostModeDataHandler = new LostModeDataHandler();
        if (requestJSON.has("msg_body")) {
            final JSONObject bodyJSON = requestJSON.optJSONObject("msg_body");
            phoneNumber = bodyJSON.optString("phone_number", (String)null);
            lockMessage = bodyJSON.optString("lock_message", (String)null);
            ticketId = bodyJSON.optString("ticket_id", (String)null);
            auditMessage = bodyJSON.optString("audit_message", "--");
            sendEmailToUser = bodyJSON.optBoolean("send_email_to_user", false);
            sendEmailToAdmin = bodyJSON.optBoolean("send_email_to_admin", false);
            passCode = bodyJSON.optString("passcode", (String)null);
        }
        if (lockMessage == null) {
            lockMessage = "This device has been lost. Kindly hand it over to the owner";
        }
        final JSONObject lostmodedata = new JSONObject();
        lostmodedata.put("PASSCODE", (Object)passCode);
        lostmodedata.put("EMAIL_SENT_TO_USER", sendEmailToUser);
        lostmodedata.put("EMAIL_SENT_TO_ADMIN", sendEmailToAdmin);
        lostmodedata.put("CONTACT_NUMBER", (Object)phoneNumber);
        lostmodedata.put("LOCK_SCREEN_MESSAGE", (Object)lockMessage);
        lostmodedata.put("PLATFORM_TYPE", requestJSON.opt("PLATFORM_TYPE"));
        lostmodedata.put("TICKET_ID", (Object)ticketId);
        lostmodedata.put("AUDIT_MESSAGE", (Object)auditMessage);
        lostmodedata.put("ADDED_BY", (Object)userId);
        lostmodedata.put("CUSTOMER_ID", (Object)customerId);
        if (requestJSON.has("devices")) {
            lostmodedata.put("devices", requestJSON.opt("devices"));
            lostModeDataHandler.activateBulkLostMode(lostmodedata);
        }
        else {
            lostmodedata.put("RESOURCE_ID", requestJSON.opt("RESOURCE_ID"));
            lostModeDataHandler.activateLostMode(lostmodedata);
        }
    }
    
    private void sendDisableLostModeCommand(final JSONObject requestJSON, final Long customerId, final Long userId) {
        String auditMessage = "";
        final JSONObject bodyJSON = requestJSON.optJSONObject("msg_body");
        auditMessage = bodyJSON.optString("audit_message", (String)null);
        if (auditMessage == null) {
            auditMessage = bodyJSON.optString("audit_message", "Lost mode disabled");
        }
        final JSONObject lostmodedata = new JSONObject();
        final LostModeDataHandler lostModeDataHandler = new LostModeDataHandler();
        lostmodedata.put("AUDIT_MESSAGE", (Object)auditMessage);
        lostmodedata.put("PLATFORM_TYPE", requestJSON.opt("PLATFORM_TYPE"));
        lostmodedata.put("ADDED_BY", (Object)userId);
        lostmodedata.put("CUSTOMER_ID", (Object)customerId);
        if (requestJSON.has("devices")) {
            lostmodedata.put("devices", requestJSON.opt("devices"));
            lostModeDataHandler.deActivateBulkLostMode(lostmodedata);
        }
        else {
            lostmodedata.put("RESOURCE_ID", requestJSON.opt("RESOURCE_ID"));
            lostModeDataHandler.deActivateLostMode(lostmodedata);
        }
    }
    
    public HashMap getPlatformBasedCommandName(final String commandName) {
        final HashMap responseMap = new HashMap();
        switch (commandName) {
            case "restart":
            case "RestartDevice": {
                responseMap.put(1, "RestartDevice");
                responseMap.put(2, "RestartDevice");
                responseMap.put(3, "RestartDevice");
                break;
            }
            case "shutdown":
            case "ShutDownDevice": {
                responseMap.put(1, "ShutDownDevice");
                break;
            }
            case "unlock_user_account":
            case "rotate_filevault_personal_key": {
                responseMap.put(1, "UnlockUserAccount");
            }
            case "enable_lost_mode": {
                responseMap.put(2, "EnableLostMode");
                responseMap.put(1, "EnableLostMode");
                break;
            }
            case "disable_lost_mode": {
                responseMap.put(2, "DisableLostMode");
                responseMap.put(1, "DisableLostMode");
                break;
            }
            case "clear_app_data": {
                responseMap.put(2, "ClearAppData");
                break;
            }
        }
        return responseMap;
    }
    
    public String getCommandNameFromAPIRequest(final APIRequest apiRequest) {
        final String temp = apiRequest.pathInfo.substring(apiRequest.pathInfo.lastIndexOf("actions/") + "actions/".length());
        return temp.substring(temp.indexOf("/") + 1);
    }
    
    public String getCommandClientName(final String commandType) {
        String commandName = "";
        switch (commandType) {
            case "EnableLostMode": {
                commandName = "enable_lost_mode";
                break;
            }
            case "DisableLostMode": {
                commandName = "disable_lost_mode";
                break;
            }
            case "EraseDevice": {
                commandName = "complete_wipe";
                break;
            }
            case "CorporateWipe": {
                commandName = "corporate_wipe";
                break;
            }
            case "DeviceRing":
            case "PlayLostModeSound": {
                commandName = "remote_alarm";
                break;
            }
            case "DeviceInformation":
            case "AssetScan":
            case "AssetScanContainer":
            case "AndroidInvScan":
            case "AndroidInvScanContainer": {
                commandName = "scan";
                break;
            }
            case "GetLocation":
            case "LostModeDeviceLocation": {
                commandName = "fetch_location";
                break;
            }
            case "CreateContainer": {
                commandName = "create_container";
                break;
            }
            case "RemoveContainer": {
                commandName = "remove_container";
                break;
            }
            case "ContainerLock": {
                commandName = "lock_container";
                break;
            }
            case "ContainerUnlock": {
                commandName = "unlock_container";
                break;
            }
            case "ClearContainerPasscode": {
                commandName = "clear_container_password";
                break;
            }
            case "RemoteSession": {
                commandName = "remote_control";
                break;
            }
            case "DeviceLock": {
                commandName = "lock";
                break;
            }
            case "ClearPasscode": {
                commandName = "clear_passcode";
                break;
            }
            case "ResetPasscode": {
                commandName = "reset_passcode";
                break;
            }
            case "ShutDownDevice": {
                commandName = "shutdown";
                break;
            }
            case "RestartDevice": {
                commandName = "restart";
                break;
            }
            case "PauseKioskCommand": {
                commandName = "pause_kiosk";
                break;
            }
            case "ResumeKioskCommand": {
                commandName = "re_apply_kiosk";
                break;
            }
            case "RemoteDebug": {
                commandName = "remote_debug";
                break;
            }
            case "UnlockUserAccount": {
                commandName = "unlock_user_account";
                break;
            }
            case "MacFileVaultPersonalKeyRotate": {
                commandName = "rotate_filevault_personal_key";
                break;
            }
            case "ClearAppData": {
                commandName = "clear_app_data";
                break;
            }
            default: {
                commandName = commandType;
                break;
            }
        }
        return commandName;
    }
    
    public void saveRemoteWipeOptions(final JSONObject requestJSON) {
        try {
            final Long deviceId = APIUtil.getResourceID(requestJSON, "device_id");
            final Long customerId = APIUtil.getCustomerID(requestJSON);
            new DeviceFacade().validateIfDeviceExists(deviceId, customerId);
            if (!requestJSON.has("msg_body")) {
                throw new APIHTTPException("COM0006", new Object[0]);
            }
            final JSONObject bodyJSON = requestJSON.getJSONObject("msg_body");
            final Boolean allowWipeSDCard = (Boolean)bodyJSON.opt("wipe_sd_card");
            final Boolean wipeButRetainMDM = (Boolean)bodyJSON.opt("wipe_but_retain_mdm");
            final String wipeLockPin = bodyJSON.optString("wipe_lock_pin");
            final JSONObject wipeOptionJSON = new JSONObject();
            wipeOptionJSON.put("WIPE_SD_CARD", (Object)allowWipeSDCard);
            wipeOptionJSON.put("WIPE_BUT_RETAIN_MDM", (Object)wipeButRetainMDM);
            wipeOptionJSON.put("WIPE_LOCK_PIN", (Object)wipeLockPin);
            final boolean success = new RemoteWipeHandler().addOrUpdateDeviceWipeOptions(wipeOptionJSON);
            if (!success) {
                throw new APIHTTPException("COM0004", new Object[0]);
            }
        }
        catch (final Exception e) {
            CommandFacade.logger.log(Level.SEVERE, "Exception in saveRemoteWipeOptions: ", e);
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    static {
        CommandFacade.logger = Logger.getLogger("MDMApiLogger");
        CommandFacade.assignUserLogger = Logger.getLogger("MDMAssignUser");
        CommandFacade.actionLogger = Logger.getLogger("ActionsLogger");
    }
}
