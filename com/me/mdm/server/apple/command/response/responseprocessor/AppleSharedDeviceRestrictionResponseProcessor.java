package com.me.mdm.server.apple.command.response.responseprocessor;

import com.me.mdm.server.seqcommands.ios.IOSSeqCmdUtil;
import com.me.mdm.server.seqcommands.SeqCmdRepository;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import com.me.mdm.server.profiles.IOSInstallProfileResponseProcessor;
import com.me.mdm.server.ios.error.IOSErrorStatusHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.server.command.CommandResponseProcessor;

public class AppleSharedDeviceRestrictionResponseProcessor implements CommandResponseProcessor.ImmediateSeqResponseProcessor, CommandResponseProcessor.QueuedResponseProcessor
{
    private static Logger logger;
    
    @Override
    public JSONObject processQueuedCommand(final JSONObject params) throws Exception {
        String remarks = "";
        final Long resourceID = params.optLong("resourceId");
        final String commandUUID = params.optString("strCommandUuid");
        try {
            final JSONObject processedResponse = new JSONObject();
            final String collectionId = MDMUtil.getInstance().getCollectionIdFromCommandUUID(commandUUID);
            final IOSErrorStatusHandler statusHandler = new IOSErrorStatusHandler();
            final JSONObject settingError = statusHandler.getIOSSettingError(params.optString("strData"));
            final String settingStatus = settingError.optString("Status");
            final Long customerId = params.optLong("customerId");
            final IOSInstallProfileResponseProcessor iOSProcessor = new IOSInstallProfileResponseProcessor();
            if (settingStatus.equalsIgnoreCase("Acknowledged")) {
                iOSProcessor.processSucceededProfileCommand(Long.parseLong(collectionId), resourceID, customerId);
            }
            else {
                final String settingItem = settingError.optString("Item");
                if (settingItem.contains("SharedDeviceConfiguration")) {
                    remarks = "Error in enabling autolock setting";
                }
                else if (settingItem.contains("PasscodeLockGracePeriod")) {
                    remarks = "Error in passcode grace period";
                }
                final MDMCollectionStatusUpdate statusUpdate = MDMCollectionStatusUpdate.getInstance();
                statusUpdate.updateMdmConfigStatus(resourceID, collectionId, 7, remarks);
            }
            DeviceCommandRepository.getInstance().deleteResourceCommand(commandUUID, resourceID);
        }
        catch (final Exception ex) {
            AppleSharedDeviceRestrictionResponseProcessor.logger.log(Level.SEVERE, "Exception in processQueuedCommand for AppleSharedDeviceRestrictionResponseProcessor", ex);
        }
        return new JSONObject();
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
            settingResponse = statusHandler.getIOSSettingError(params.optString("strData"));
            final String settingStatus = settingResponse.optString("Status");
            if (settingStatus.equalsIgnoreCase("Acknowledged")) {
                response.put("action", 1);
                queueResponse.put("Status", 6);
            }
            else if (settingStatus.equalsIgnoreCase("Error") || settingStatus.equalsIgnoreCase("CommandFormatError")) {
                response.put("action", 2);
                queueResponse.put("Status", 7);
            }
            seqParams.put("isNeedToRemove", false);
            response.put("commandUUID", (Object)commandUUID);
            response.put("resourceID", (Object)resourceID);
            response.put("params", (Object)seqParams);
            response.put("isNotify", false);
            SeqCmdRepository.getInstance().processSeqCommand(response);
            queueResponse.put("isNeedToAddQueue", false);
        }
        catch (final Exception e) {
            AppleSharedDeviceRestrictionResponseProcessor.logger.log(Level.SEVERE, e, () -> "Exception in processQueuedCommand for AppleSharedDeviceRestrictionResponseProcessor:" + String.valueOf(n));
            IOSSeqCmdUtil.getInstance().removeSeqCommandForResource(resourceID, commandUUID);
        }
        return queueResponse;
    }
    
    static {
        AppleSharedDeviceRestrictionResponseProcessor.logger = Logger.getLogger("MDMLogger");
    }
}
