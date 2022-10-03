package com.me.mdm.server.command.ios;

import com.me.mdm.server.seqcommands.ios.IOSSeqCmdUtil;
import com.me.mdm.server.seqcommands.SeqCmdRepository;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import com.adventnet.sym.server.mdm.util.VersionChecker;
import com.adventnet.sym.server.mdm.inv.InventoryUtil;
import com.me.mdm.server.profiles.IOSInstallProfileResponseProcessor;
import com.me.mdm.server.ios.error.IOSErrorStatusHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.logging.Logger;
import com.me.mdm.server.command.CommandResponseProcessor;

public class MGSettingCmdResponseProcessor implements CommandResponseProcessor.ImmediateSeqResponseProcessor
{
    static MGSettingCmdResponseProcessor installResponse;
    private static Logger logger;
    HashMap<String, String> osBasedCommandSetting;
    
    public MGSettingCmdResponseProcessor() {
        this.osBasedCommandSetting = new HashMap() {
            {
                this.put("TimeZone", "14.0");
                this.put("Bluetooth", "11.3");
                this.put("AccessibilitySettings", "16.0");
            }
        };
    }
    
    public void processCommand(final Long resourceID, final String responseData, final String commandUUID, final Long customerId) {
        String remarks = null;
        Integer errorCode = null;
        final Integer status = null;
        try {
            final JSONObject processedResponse = new JSONObject();
            final String collectionId = MDMUtil.getInstance().getCollectionIdFromCommandUUID(commandUUID);
            final IOSErrorStatusHandler statusHandler = new IOSErrorStatusHandler();
            final JSONObject settingError = statusHandler.getIOSSettingError(responseData);
            final String settingStatus = settingError.optString("Status");
            final IOSInstallProfileResponseProcessor iOSProcessor = new IOSInstallProfileResponseProcessor();
            if (settingStatus.equalsIgnoreCase("Acknowledged")) {
                iOSProcessor.processSucceededProfileCommand(Long.parseLong(collectionId), resourceID, customerId);
            }
            else {
                final JSONObject deviceDetails = InventoryUtil.getInstance().getDeviceDetails(resourceID);
                final boolean isSuperVised = deviceDetails.getBoolean("IS_SUPERVISED");
                final String deviceOSVersion = deviceDetails.getString("OS_VERSION");
                final String settingItem = settingError.optString("Item");
                boolean isSuccess = false;
                if (!isSuperVised && settingStatus.contains("CommandFormatError") && settingItem.equalsIgnoreCase("Wallpaper")) {
                    remarks = "dc.mdm.device_mgmt.profile_supervised_eror_msg";
                    errorCode = 29000;
                }
                else if (!isSuperVised && settingStatus.contains("CommandFormatError")) {
                    isSuccess = true;
                }
                else if (settingItem.contains("PersonalHotspot")) {
                    final Integer cellularTechnology = (Integer)deviceDetails.get("CELLULAR_TECHNOLOGY");
                    if (cellularTechnology == 0) {
                        isSuccess = true;
                    }
                }
                else if (settingItem.contains("AccessibilitySettings")) {
                    final int modelType = MDMUtil.getInstance().getModelTypeFromDB(resourceID);
                    if (modelType != 1 || modelType != 2 || modelType != 0) {
                        isSuccess = true;
                    }
                }
                else if (this.osBasedCommandSetting.containsKey(settingItem) && !new VersionChecker().isGreaterOrEqual(deviceOSVersion, this.osBasedCommandSetting.get(settingItem))) {
                    isSuccess = true;
                }
                if (isSuccess) {
                    iOSProcessor.processSucceededProfileCommand(Long.parseLong(collectionId), resourceID, customerId);
                }
                else {
                    if (remarks == null) {
                        remarks = settingError.optString("EnglishRemarks");
                    }
                    final MDMCollectionStatusUpdate statusUpdate = MDMCollectionStatusUpdate.getInstance();
                    statusUpdate.updateMdmConfigStatus(resourceID, collectionId, 7, remarks);
                    if (errorCode != null) {
                        statusUpdate.updateCollnToResErrorCode(resourceID, Long.parseLong(collectionId), errorCode);
                    }
                }
            }
            DeviceCommandRepository.getInstance().deleteResourceCommand(commandUUID, resourceID);
        }
        catch (final Exception ex) {
            MGSettingCmdResponseProcessor.logger.log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public JSONObject processImmediateSeqCommand(final JSONObject params) {
        JSONObject settingResponse = null;
        final JSONObject queueResponse = new JSONObject();
        final Long resourceID = params.optLong("resourceId");
        final String commandUUID = params.optString("strCommandUuid");
        try {
            final IOSErrorStatusHandler statusHandler = new IOSErrorStatusHandler();
            final JSONObject response = new JSONObject();
            final JSONObject seqParams = new JSONObject();
            final JSONObject sequentialParams = params.optJSONObject("seqParams");
            settingResponse = statusHandler.getIOSSettingError(params.optString("strData"));
            final String settingStatus = settingResponse.optString("Status");
            if (settingStatus.equalsIgnoreCase("Acknowledged")) {
                response.put("action", 1);
                queueResponse.put("Status", 6);
                seqParams.put("isNeedToRemove", true);
            }
            else if (settingStatus.equalsIgnoreCase("CommandFormatError")) {
                final JSONObject commandScopeParams = sequentialParams.optJSONObject("cmdScopeParams");
                boolean success = false;
                if (commandScopeParams != null) {
                    final Integer cellularTechnology = commandScopeParams.optInt("cellularTechnology", -1);
                    final boolean isSupervised = commandScopeParams.optBoolean("isSupervised");
                    boolean cellular = false;
                    if (cellularTechnology == 0) {
                        cellular = true;
                    }
                    boolean osVersionCheck = false;
                    final String deviceOSVersion = commandScopeParams.optString("OS_VERSION");
                    final String settingItem = settingResponse.optString("Item");
                    if (!MDMStringUtils.isEmpty(deviceOSVersion) && this.osBasedCommandSetting.containsKey(settingItem) && !new VersionChecker().isGreaterOrEqual(deviceOSVersion, this.osBasedCommandSetting.get(settingItem))) {
                        osVersionCheck = true;
                    }
                    success = (!isSupervised || cellular || osVersionCheck);
                }
                if (success) {
                    MGSettingCmdResponseProcessor.logger.log(Level.INFO, "Giving success to profile due to commandformat error");
                    response.put("action", 1);
                    queueResponse.put("Status", 6);
                    seqParams.put("isNeedToRemove", true);
                }
                else {
                    response.put("action", 2);
                    queueResponse.put("Status", 7);
                    queueResponse.put("isNeedToAddQueue", true);
                }
            }
            else if (settingStatus.equalsIgnoreCase("Error")) {
                boolean success2 = false;
                final String settingItem2 = settingResponse.optString("Item");
                if (settingItem2.contains("AccessibilitySettings")) {
                    final int modelType = MDMUtil.getInstance().getModelTypeFromDB(resourceID);
                    if (modelType != 1 || modelType != 2 || modelType != 0) {
                        success2 = true;
                    }
                }
                if (success2) {
                    MGSettingCmdResponseProcessor.logger.log(Level.INFO, "Giving success to profile due to device not applicable for error status");
                    response.put("action", 1);
                    queueResponse.put("Status", 6);
                    seqParams.put("isNeedToRemove", true);
                }
                else {
                    response.put("action", 2);
                    queueResponse.put("Status", 7);
                    queueResponse.put("isNeedToAddQueue", true);
                }
            }
            response.put("commandUUID", (Object)commandUUID);
            response.put("resourceID", (Object)resourceID);
            response.put("params", (Object)seqParams);
            response.put("isNotify", false);
            SeqCmdRepository.getInstance().processSeqCommand(response);
        }
        catch (final Exception e) {
            MGSettingCmdResponseProcessor.logger.log(Level.SEVERE, e, () -> "Exception in immediate processing the Setting response for resource:" + String.valueOf(n));
            IOSSeqCmdUtil.getInstance().removeSeqCommandForResource(resourceID, commandUUID);
        }
        return queueResponse;
    }
    
    static {
        MGSettingCmdResponseProcessor.installResponse = null;
        MGSettingCmdResponseProcessor.logger = Logger.getLogger("MDMLogger");
    }
}
