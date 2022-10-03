package com.me.mdm.server.security.mac;

import com.me.mdm.server.onelinelogger.MDMOneLineLogger;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.sym.server.mdm.DeviceDetails;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.persistence.Row;
import com.me.mdm.server.seqcommands.SeqCmdDBUtil;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.logging.Level;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import java.util.logging.Logger;

public class MacFirmwareUtil
{
    private static final Logger LOGGER;
    private static final String FIMRWARE_HANDLER = "com.me.mdm.server.security.mac.MacFirmwareSequentialCommandResponseProcessor";
    
    public static void addFirmwarePolicySequentialCommand(final Long collectionID) {
        try {
            final String sequentialCommandID = getFirmwareSeqCommandStr(collectionID);
            final Long commandID = DeviceCommandRepository.getInstance().getCommandID(sequentialCommandID);
            final JSONObject seqCmds = new JSONObject();
            final JSONArray cmdArray = new JSONArray();
            final JSONObject cmdArrayObj = new JSONObject();
            final JSONArray subCmdArray = new JSONArray();
            final ArrayList collectionIdsList = new ArrayList();
            collectionIdsList.add(collectionID);
            final JSONObject preSecurityInfoCommand = new JSONObject();
            preSecurityInfoCommand.put("cmd_id", (Object)DeviceCommandRepository.getInstance().addCommand("MacFirmwarePreSecurityInfo"));
            preSecurityInfoCommand.put("order", 1);
            preSecurityInfoCommand.put("handler", (Object)"com.me.mdm.server.security.mac.MacFirmwareSequentialCommandResponseProcessor");
            subCmdArray.put((Object)preSecurityInfoCommand);
            final JSONObject firmwareVerifyPasswordCommand = new JSONObject();
            firmwareVerifyPasswordCommand.put("cmd_id", (Object)DeviceCommandRepository.getInstance().addCommand("MacFirmwareVerifyPassword"));
            firmwareVerifyPasswordCommand.put("order", 2);
            firmwareVerifyPasswordCommand.put("handler", (Object)"com.me.mdm.server.security.mac.MacFirmwareSequentialCommandResponseProcessor");
            subCmdArray.put((Object)firmwareVerifyPasswordCommand);
            final JSONObject firmwareSetPasswordCommand = new JSONObject();
            firmwareSetPasswordCommand.put("cmd_id", (Object)DeviceCommandRepository.getInstance().addCommand(getFirmwareBaseSeqCommandStr(collectionID)));
            firmwareSetPasswordCommand.put("order", 3);
            firmwareSetPasswordCommand.put("handler", (Object)"com.me.mdm.server.security.mac.MacFirmwareSequentialCommandResponseProcessor");
            subCmdArray.put((Object)firmwareSetPasswordCommand);
            final JSONObject firmwareClearPasswordCommand = new JSONObject();
            firmwareClearPasswordCommand.put("cmd_id", (Object)DeviceCommandRepository.getInstance().addCommand("MacFirmwareClearPasscode"));
            firmwareClearPasswordCommand.put("order", 4);
            firmwareClearPasswordCommand.put("handler", (Object)"com.me.mdm.server.security.mac.MacFirmwareSequentialCommandResponseProcessor");
            subCmdArray.put((Object)firmwareClearPasswordCommand);
            final JSONObject postSecurityInfoCommand = new JSONObject();
            postSecurityInfoCommand.put("cmd_id", (Object)DeviceCommandRepository.getInstance().addCommand("MacFirmwarePostSecurityInfo"));
            postSecurityInfoCommand.put("order", 5);
            postSecurityInfoCommand.put("handler", (Object)"com.me.mdm.server.security.mac.MacFirmwareSequentialCommandResponseProcessor");
            subCmdArray.put((Object)postSecurityInfoCommand);
            final JSONObject commandParams = new JSONObject();
            commandParams.put("CollectionID", (Object)collectionID);
            cmdArrayObj.put("subCommands", (Object)subCmdArray);
            cmdArrayObj.put("basecmdID", (Object)DeviceCommandRepository.getInstance().addCommand(getFirmwareBaseSeqCommandStr(collectionID)));
            cmdArrayObj.put("SequentialCommandId", (Object)DeviceCommandRepository.getInstance().addSequentialCommand(sequentialCommandID));
            cmdArrayObj.put("allowImmediateProcessing", false);
            cmdArrayObj.put("params", (Object)commandParams);
            cmdArrayObj.put("timeout", 60000);
            cmdArray.put((Object)cmdArrayObj);
            seqCmds.put("SequentialCommands", (Object)cmdArray);
            MacFirmwareUtil.LOGGER.log(Level.INFO, "addFirmwarePolicySequentialCommand final json to add {0}", seqCmds.toString());
            final Long seqID = (Long)DBUtil.getValueFromDB("MdCommandToSequentialCommand", "COMMAND_ID", (Object)commandID, "SEQUENTIAL_COMMAND_ID");
            if (seqID == null) {
                SeqCmdDBUtil.getInstance().addSequentialCommands(seqCmds);
            }
        }
        catch (final Exception ex) {
            MacFirmwareUtil.LOGGER.log(Level.SEVERE, "addFirmwarePolicySequentialCommand error ", ex);
        }
    }
    
    public static String getFirmwareBaseSeqCommandStr(final Long collectionID) {
        return "MacFirmwareSetPasscode".concat(";Colln=").concat(collectionID.toString());
    }
    
    public static String getFirmwareSeqCommandStr(final Long collectionID) {
        return "Sequential;".concat("MacFirmwareSetPasscode").concat(";Colln=").concat(collectionID.toString());
    }
    
    public static int getFirmwareModeFromResponse(final String firmwareMode) {
        if (firmwareMode == null) {
            return 0;
        }
        switch (firmwareMode) {
            case "command": {
                return 1;
            }
            case "full": {
                return 2;
            }
            default: {
                return 0;
            }
        }
    }
    
    public static JSONObject getFirmwareDeviceDetails(final Long resourceID) {
        try {
            final JSONObject responseJSON = new JSONObject();
            final Row row = DBUtil.getRowFromDB("MDMDeviceFirmwareInfo", "RESOURCE_ID", (Object)resourceID);
            if (row != null) {
                responseJSON.put("IS_FIRMWARE_PASSWORD_EXISTS", row.get("IS_FIRMWARE_PASSWORD_EXISTS"));
                responseJSON.put("FIRMWARE_MODE", row.get("FIRMWARE_MODE"));
                responseJSON.put("IS_FIRMWARE_CHANGE_PENDING", row.get("IS_FIRMWARE_CHANGE_PENDING"));
                responseJSON.put("IS_ROMS_ALLOWED", row.get("IS_ROMS_ALLOWED"));
                responseJSON.put("MANAGED_PASSWORD_ID", (row.get("MANAGED_PASSWORD_ID") == null) ? Integer.valueOf(-1) : row.get("MANAGED_PASSWORD_ID"));
                return responseJSON;
            }
        }
        catch (final Exception ex) {
            MacFirmwareUtil.LOGGER.log(Level.SEVERE, "getFirmwareDeviceDetails error ", ex);
        }
        return null;
    }
    
    public static JSONObject getFirmwarePasswordForDevice(final Long resourceID, final Long userID) {
        try {
            final JSONObject responseJSON = new JSONObject();
            final Long managedPassword = (Long)DBUtil.getValueFromDB("MDMDeviceFirmwareInfo", "RESOURCE_ID", (Object)resourceID, "MANAGED_PASSWORD_ID");
            if (managedPassword != null) {
                final String password = getMDMManagedPassword(managedPassword);
                if (!MDMStringUtils.isEmpty(password)) {
                    responseJSON.put("resource_id", (Object)resourceID);
                    responseJSON.put("firmware_password", (Object)password);
                    final DeviceDetails deviceDetail = new DeviceDetails(resourceID);
                    final boolean isSiliconMac = MDMUtil.isSiliconMac(resourceID);
                    final String remarks = isSiliconMac ? "mdm.profile.audit_recovery_passcode_viewed" : "mdm.profile.firmware_password_audit";
                    MDMEventLogHandler.getInstance().MDMEventLogEntry(2143, resourceID, DMUserHandler.getUserNameFromUserID(userID), remarks, deviceDetail.name + "@@@" + deviceDetail.udid + "@@@" + deviceDetail.serialNumber, deviceDetail.customerId);
                    final org.json.simple.JSONObject logJSON = new org.json.simple.JSONObject();
                    logJSON.put((Object)"REMARKS", (Object)"get-success");
                    logJSON.put((Object)"RESOURCE_ID", (Object)resourceID);
                    logJSON.put((Object)"NAME", (Object)deviceDetail.name);
                    MDMOneLineLogger.log(Level.INFO, "VIEW_FIRMWARE_PASSWORD", logJSON);
                    return responseJSON;
                }
            }
        }
        catch (final Exception ex) {
            MacFirmwareUtil.LOGGER.log(Level.SEVERE, "getFirmwareDeviceDetails error ", ex);
        }
        return null;
    }
    
    public static String getMDMManagedPassword(final Long passwordID) {
        try {
            return (String)DBUtil.getValueFromDB("MDMManagedPassword", "MANAGED_PASSWORD_ID", (Object)passwordID, "PASSWORD");
        }
        catch (final Exception ex) {
            MacFirmwareUtil.LOGGER.log(Level.SEVERE, ex, () -> "MacFirmwareSetFirmwareCommandGenerator: No password found for password ID:" + n);
            return null;
        }
    }
    
    static {
        LOGGER = Logger.getLogger("MDMSequentialCommandsLogger");
    }
    
    public static final class FirmwareSequentialConstants
    {
        public static final class SubCommandCommonParams
        {
            public static final String IS_CLEAR_PASSWORD = "isClearPassword";
            public static final String IS_EXISTING_PASSWORD_VERIFIED = "isExistingPasswordVerified";
            public static final String IS_FIRMWARE_ALREADY_SET = "isFirmwareAlreadySet";
            public static final String IS_PREVIOUS_PASSWORD_WAITING_FOR_RESTART = "isPreviousPasswordWaitingForRestart";
            public static final String IS_PASSWORD_CLEARED = "isPasswordCleared";
            public static final String COLLECTION_ID = "CollectionID";
            public static final String NEW_PASSWORD_ID = "newPasswordID";
            public static final String EXISTING_PASSWORD_ID = "existingPasswordID";
        }
    }
    
    public static final class FirmwareConstants
    {
        public static final int FIRMWARE_MODE_COMMAND = 1;
        public static final int FIRMWARE_MODE_FULL = 2;
    }
}
