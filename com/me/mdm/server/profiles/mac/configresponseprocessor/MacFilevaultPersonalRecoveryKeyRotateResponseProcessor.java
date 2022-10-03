package com.me.mdm.server.profiles.mac.configresponseprocessor;

import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.dd.plist.NSDictionary;
import com.me.mdm.server.notification.NotificationHandler;
import java.util.List;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.sym.server.mdm.inv.MDMInvDataPopulator;
import com.adventnet.sym.server.mdm.encryption.ios.filevault.MDMFileVaultRecoveryKeyHander;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.PlistWrapper;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.adventnet.sym.server.mdm.DeviceDetails;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.server.command.CommandStatusHandler;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.me.mdm.server.profiles.mac.MDMFilevaultPersonalRecoveryKeyImport;
import java.util.logging.Level;
import com.me.mdm.server.ios.error.IOSErrorStatusHandler;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.server.command.CommandResponseProcessor;

public class MacFilevaultPersonalRecoveryKeyRotateResponseProcessor implements CommandResponseProcessor.QueuedResponseProcessor
{
    public static Logger logger;
    
    @Override
    public JSONObject processQueuedCommand(final JSONObject params) throws Exception {
        final Long resourceID = params.getLong("resourceId");
        final String commadName = params.getString("strCommandUuid");
        final String commandStatus = params.getString("strStatus");
        final String commandResponse = params.getString("strData");
        final Long customerID = params.getLong("customerId");
        final String deviceUDID = params.getString("strUDID");
        if (commandStatus.equalsIgnoreCase("Acknowledged")) {
            this.processPersonalRecoveryKeyRotateAck(resourceID, commandResponse, commadName);
        }
        else if (commandStatus.equalsIgnoreCase("Error")) {
            this.processPersonalRecoveryKeyRotateError(resourceID, commandResponse, commadName, commandStatus);
        }
        return new JSONObject();
    }
    
    private void processPersonalRecoveryKeyRotateError(final Long resourceID, final String commandResponse, final String commandUUID, final String errorStatus) throws Exception {
        final JSONObject errorJSON = new IOSErrorStatusHandler().getIOSErrors(commandUUID, commandResponse, errorStatus);
        MacFilevaultPersonalRecoveryKeyRotateResponseProcessor.logger.log(Level.SEVERE, "[Filevault] Personal Recovery Key Rotate Command Error{0} , ErrorJSON:{1} ,ErrorResponse:{2}", new Object[] { resourceID, errorJSON, commandResponse });
        MDMFilevaultPersonalRecoveryKeyImport.updateErrorRemarksForFVKeyRotation(resourceID, errorJSON.toString());
        final Long commandId = DeviceCommandRepository.getInstance().getCommandID(commandUUID);
        final JSONObject commandStatusJSON = new CommandStatusHandler().getRecentCommandInfo(resourceID, commandId);
        if (commandStatusJSON.has("ADDED_BY")) {
            commandStatusJSON.put("COMMAND_ID", (Object)commandId);
            commandStatusJSON.put("COMMAND_STATUS", 0);
            commandStatusJSON.put("RESOURCE_ID", (Object)resourceID);
            commandStatusJSON.put("COMMAND_ID", (Object)commandId);
            commandStatusJSON.put("RESOURCE_ID", (Object)resourceID);
            new CommandStatusHandler().populateCommandStatus(commandStatusJSON);
            final String userName = DMUserHandler.getUserNameFromUserID(JSONUtil.optLongForUVH(commandStatusJSON, "ADDED_BY", Long.valueOf(-1L)));
            final DeviceDetails deviceDetail = new DeviceDetails(resourceID);
            MDMEventLogHandler.getInstance().MDMEventLogEntry(2144, resourceID, userName, "mdm.profile.filevault_rotate_failed", deviceDetail.name + "@@@" + deviceDetail.serialNumber, deviceDetail.customerId);
        }
    }
    
    private void processPersonalRecoveryKeyRotateAck(final Long resourceID, final String commandResponse, final String commandname) throws Exception {
        final NSDictionary nsDict = PlistWrapper.getInstance().getDictForKey("RotateResult", commandResponse);
        HashMap hsmap = new HashMap();
        hsmap = PlistWrapper.getInstance().getHashFromDict(nsDict);
        final String personalIdentificationKeyStr = hsmap.get("EncryptedNewRecoveryKey");
        final String parsedRecoveryKey = MDMStringUtils.isEmpty(personalIdentificationKeyStr) ? "--" : MDMFileVaultRecoveryKeyHander.getDecodedFileVaultRecoveryKeyForDevice(personalIdentificationKeyStr, resourceID);
        if (MDMStringUtils.isEmpty(parsedRecoveryKey)) {
            MacFilevaultPersonalRecoveryKeyRotateResponseProcessor.logger.log(Level.SEVERE, "[Filevault] Personal Recovery Key through Rotate command is empty after parsing certificate {0}", resourceID);
            return;
        }
        final DataObject fileVaultInfoDO = MDMInvDataPopulator.getInstance().getMacFileVaultDO(resourceID);
        final Row fileVaultInfoRow = fileVaultInfoDO.getFirstRow("MDMDeviceFileVaultInfo");
        MDMInvDataPopulator.getInstance().updatePersonalRecoveryKeyFilevaultRow(fileVaultInfoRow, resourceID, false, parsedRecoveryKey);
        fileVaultInfoDO.updateRow(fileVaultInfoRow);
        MacFilevaultPersonalRecoveryKeyRotateResponseProcessor.logger.log(Level.INFO, "[Filevault] Updating PERSONAL_RECOVERY_KEY through RotateCommand Response {0}", resourceID);
        MDMUtil.getPersistence().update(fileVaultInfoDO);
        final Long commandId = DeviceCommandRepository.getInstance().getCommandID(commandname);
        final JSONObject commandStatusJSON = new CommandStatusHandler().getRecentCommandInfo(resourceID, commandId);
        if (commandStatusJSON.has("ADDED_BY")) {
            commandStatusJSON.put("COMMAND_ID", (Object)commandId);
            commandStatusJSON.put("COMMAND_STATUS", 2);
            commandStatusJSON.put("RESOURCE_ID", (Object)resourceID);
            commandStatusJSON.put("COMMAND_ID", (Object)commandId);
            commandStatusJSON.put("RESOURCE_ID", (Object)resourceID);
            new CommandStatusHandler().populateCommandStatus(commandStatusJSON);
            final String userName = DMUserHandler.getUserNameFromUserID(JSONUtil.optLongForUVH(commandStatusJSON, "ADDED_BY", Long.valueOf(-1L)));
            final DeviceDetails deviceDetail = new DeviceDetails(resourceID);
            MDMEventLogHandler.getInstance().MDMEventLogEntry(2144, resourceID, userName, "mdm.profile.filevault_rotate_success", deviceDetail.name + "@@@" + deviceDetail.serialNumber, deviceDetail.customerId);
        }
        final Long securityInfoID = DeviceCommandRepository.getInstance().getCommandID("SecurityInfo");
        final List<Long> resIDList = new ArrayList<Long>();
        resIDList.add(resourceID);
        DeviceCommandRepository.getInstance().assignCommandToDevices(securityInfoID, resIDList);
        NotificationHandler.getInstance().SendNotification(resIDList, 1);
        MacFilevaultPersonalRecoveryKeyRotateResponseProcessor.logger.log(Level.INFO, "[Filevault] Adding Security Info command for device to fetch FV recovery key {0}", resourceID);
    }
    
    static {
        MacFilevaultPersonalRecoveryKeyRotateResponseProcessor.logger = Logger.getLogger("MDMLogger");
    }
}
