package com.me.mdm.server.inv.ios;

import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.mdm.server.notification.PushNotificationHandler;
import com.me.mdm.server.ios.error.IOSErrorStatusHandler;
import com.me.mdm.server.profiles.IOSInstallProfileResponseProcessor;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.seqcommands.ios.IOSSeqCmdUtil;
import java.util.logging.Level;
import com.me.mdm.server.seqcommands.SeqCmdRepository;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.server.command.CommandResponseProcessor;

public class IOSClearDisablePasscodeResProcessor implements CommandResponseProcessor.ImmediateSeqResponseProcessor, CommandResponseProcessor.QueuedResponseProcessor
{
    private static Logger logger;
    
    @Override
    public JSONObject processImmediateSeqCommand(final JSONObject params) {
        final Long resourceID = params.optLong("resourceId");
        final String commandUUID = params.optString("strCommandUuid");
        final JSONObject seqResponse = new JSONObject();
        try {
            final JSONObject response = new JSONObject();
            final String status = params.optString("strStatus");
            final JSONObject seqParams = new JSONObject();
            if (status.equalsIgnoreCase("Acknowledged")) {
                response.put("action", 1);
            }
            else {
                response.put("action", 2);
                seqResponse.put("isNeedToAddQueue", true);
            }
            seqParams.put("isNeedToRemove", true);
            response.put("resourceID", (Object)resourceID);
            response.put("commandUUID", (Object)commandUUID);
            response.put("params", (Object)seqParams);
            SeqCmdRepository.getInstance().processSeqCommand(response);
        }
        catch (final Exception e) {
            IOSClearDisablePasscodeResProcessor.logger.log(Level.SEVERE, e, () -> "Exception in processing clear disable passcode immediate seq processing for resource:" + String.valueOf(n));
            IOSSeqCmdUtil.getInstance().removeSeqCommandForResource(resourceID, commandUUID);
        }
        return seqResponse;
    }
    
    @Override
    public JSONObject processQueuedCommand(final JSONObject params) {
        try {
            final String commandStatus = params.optString("strStatus");
            final String commandUUID = params.optString("strCommandUuid");
            final String strData = params.optString("strData");
            final Long resourceID = params.optLong("resourceId");
            final Long customerId = params.optLong("customerId");
            final String collectionId = MDMUtil.getInstance().getCollectionIdFromCommandUUID(commandUUID);
            if (collectionId != null) {
                final IOSInstallProfileResponseProcessor processor = new IOSInstallProfileResponseProcessor();
                if (commandStatus.contains("Acknowledged")) {
                    processor.processSucceededProfileCommand(Long.parseLong(collectionId), resourceID, customerId);
                }
                else {
                    processor.processFailureProfileCommand(Long.parseLong(collectionId), resourceID, customerId);
                    final JSONObject errorObject = new IOSErrorStatusHandler().getIOSErrors(commandUUID, strData, commandStatus);
                    final int errorCode = errorObject.getInt("ErrorCode");
                    if (errorCode == 9000) {
                        final JSONObject notificationHandler = PushNotificationHandler.getInstance().getNotificationDetails(resourceID, 1);
                        final String unlockToken = notificationHandler.optString("UNLOCK_TOKEN_ENCRYPTED");
                        if (MDMStringUtils.isEmpty(unlockToken)) {
                            MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resourceID, collectionId, 7, "mdm.profile.passcode.error.clearFailed@@@<l>$(mdmUrl)/kb/mdm-ios-13-update-impacts.html");
                        }
                        else {
                            MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resourceID, collectionId, 7, errorObject.optString("EnglishRemarks"));
                        }
                    }
                    else {
                        MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resourceID, collectionId, 7, errorObject.optString("EnglishRemarks"));
                    }
                }
            }
        }
        catch (final Exception e) {
            IOSClearDisablePasscodeResProcessor.logger.log(Level.SEVERE, "Exception in processing clear disable passcode queued command processing for resource:", e);
        }
        return null;
    }
    
    static {
        IOSClearDisablePasscodeResProcessor.logger = Logger.getLogger("MDMSequentialCommandsLogger");
    }
}
