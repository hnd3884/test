package com.me.mdm.server.security.mac;

import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.server.seqcommands.SequentialSubCommand;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.server.seqcommands.BaseSeqCmdResponseHandler;

public class MacFirmwareSequentialCommandResponseProcessor extends BaseSeqCmdResponseHandler
{
    public Logger logger;
    
    public MacFirmwareSequentialCommandResponseProcessor() {
        this.logger = Logger.getLogger("MDMSequentialCommandsLogger");
    }
    
    @Override
    public Long onSuccess(final JSONObject params) throws Exception {
        try {
            final JSONObject currentParams = params.optJSONObject("CurCmdParam");
            final String value;
            final String commandUUID = value = String.valueOf(params.get("commandUUID"));
            switch (value) {
                case "MacFirmwarePreSecurityInfo": {}
                case "MacFirmwareVerifyPassword": {}
                case "MacFirmwareSetPasscode": {}
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "MacFirmwareSequentialCommandResponseProcessor ", ex);
        }
        return super.onSuccess(params);
    }
    
    @Override
    public Long onFailure(final JSONObject params) throws Exception {
        return super.onFailure(params);
    }
    
    @Override
    public Long retry(final JSONObject params) throws Exception {
        return super.retry(params);
    }
    
    @Override
    public boolean subCommandPreProcessor(final Long resourceID, final Long commandID, final SequentialSubCommand command) {
        boolean sendThisCommand = false;
        final JSONObject commandLevelParams = command.params.optJSONObject("CommandLevelParams");
        final JSONObject initialParams = command.params.optJSONObject("initialParams");
        final JSONObject previousCommandParams = command.params.optJSONObject("PrevCmdParams");
        final JSONObject currentCommandParams = command.params.optJSONObject("CurCmdParam");
        JSONObject commandScopeParams = command.params.optJSONObject("cmdScopeParams");
        try {
            if (previousCommandParams != null) {
                if (commandScopeParams != null) {
                    JSONUtil.putAll(commandScopeParams, previousCommandParams);
                }
                else {
                    commandScopeParams = new JSONObject();
                    JSONUtil.putAll(commandScopeParams, previousCommandParams);
                }
                command.params.put("PrevCmdParams", (Object)new JSONObject());
            }
            if (currentCommandParams != null) {
                if (commandScopeParams != null) {
                    JSONUtil.putAll(commandScopeParams, currentCommandParams);
                }
                else {
                    commandScopeParams = new JSONObject();
                    JSONUtil.putAll(commandScopeParams, currentCommandParams);
                }
                command.params.put("CurCmdParam", (Object)new JSONObject());
            }
            final String s;
            final String cmdUUID = s = MDMUtil.getInstance().getCommandUUIDFromCommandID(commandID).split(";")[0];
            switch (s) {
                case "MacFirmwarePreSecurityInfo": {
                    command.params.put("cmdScopeParams", (Object)MacFirmwarePasswordDeviceAssociationHandler.getFirmwarePasswordCommonParams(resourceID));
                    sendThisCommand = true;
                    break;
                }
                case "MacFirmwareVerifyPassword": {
                    sendThisCommand = commandScopeParams.getBoolean("isFirmwareAlreadySet");
                    if (sendThisCommand) {
                        sendThisCommand = commandScopeParams.has("existingPasswordID");
                        break;
                    }
                    break;
                }
                case "MacFirmwareSetPasscode": {
                    sendThisCommand = !initialParams.getBoolean("isClearPassword");
                    if (sendThisCommand) {
                        final boolean isFirmwareAlreadySet = commandScopeParams.getBoolean("isFirmwareAlreadySet");
                        if (isFirmwareAlreadySet) {
                            if (commandScopeParams.has("isExistingPasswordVerified")) {
                                sendThisCommand = commandScopeParams.getBoolean("isExistingPasswordVerified");
                            }
                            if (sendThisCommand) {
                                sendThisCommand = commandScopeParams.has("existingPasswordID");
                            }
                        }
                        break;
                    }
                    break;
                }
                case "MacFirmwareClearPasscode": {
                    sendThisCommand = initialParams.getBoolean("isClearPassword");
                    if (sendThisCommand) {
                        final boolean isFirmwareAlreadySet = commandScopeParams.getBoolean("isFirmwareAlreadySet");
                        if (isFirmwareAlreadySet) {
                            if (commandScopeParams.has("isExistingPasswordVerified")) {
                                sendThisCommand = commandScopeParams.getBoolean("isExistingPasswordVerified");
                            }
                            if (sendThisCommand) {
                                sendThisCommand = commandScopeParams.has("existingPasswordID");
                            }
                        }
                        break;
                    }
                    break;
                }
                case "MacFirmwarePostSecurityInfo": {
                    sendThisCommand = true;
                    break;
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "MacFirmware:MacFirmwareSequentialCommandResponseProcessor subCommandPreProcessor", ex);
        }
        return sendThisCommand;
    }
    
    @Override
    public Long notNow(final JSONObject params) throws Exception {
        try {
            final Long commandID = super.notNow(params);
            final Long resourceID = params.getLong("resourceID");
            DeviceCommandRepository.getInstance().updateResourceCommandStatus(commandID, resourceID, 1, 12);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "MacFirmwareSequentialCommandResponseProcessor ", ex);
        }
        return super.notNow(params);
    }
}
