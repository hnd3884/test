package com.me.mdm.server.profiles.ios.response;

import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import com.me.mdm.server.ios.error.IOSErrorStatusHandler;
import com.me.mdm.server.profiles.IOSInstallProfileResponseProcessor;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.seqcommands.ios.IOSSeqCmdUtil;
import com.me.mdm.server.seqcommands.SeqCmdRepository;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.server.command.CommandResponseProcessor;

public class IOSPasscodeDisableResponseProcessor implements CommandResponseProcessor.ImmediateSeqResponseProcessor, CommandResponseProcessor.QueuedResponseProcessor
{
    private static final Logger LOGGER;
    
    @Override
    public JSONObject processImmediateSeqCommand(final JSONObject params) {
        final Long resourceID = params.optLong("resourceId");
        final String commandUUID = params.optString("strCommandUuid");
        IOSPasscodeDisableResponseProcessor.LOGGER.log(Level.FINE, "Inside passcode disable immediate response processor resourceID:{0}", new Object[] { resourceID });
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
            if (!status.equalsIgnoreCase("NotNow")) {
                seqParams.put("isNeedToRemove", true);
                response.put("resourceID", (Object)resourceID);
                response.put("commandUUID", (Object)commandUUID);
                response.put("params", (Object)seqParams);
                response.put("isNotify", false);
                SeqCmdRepository.getInstance().processSeqCommand(response);
            }
            else {
                seqResponse.put("isNeedToAddQueue", true);
            }
        }
        catch (final Exception ex) {
            IOSPasscodeDisableResponseProcessor.LOGGER.log(Level.SEVERE, "Exception while processing the IOSDeviceRemove restriction", ex);
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
                    MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resourceID, collectionId, 7, "");
                }
            }
        }
        catch (final SyMException e) {
            IOSPasscodeDisableResponseProcessor.LOGGER.log(Level.SEVERE, "Exception in passcode disable response processor", (Throwable)e);
        }
        return null;
    }
    
    static {
        LOGGER = Logger.getLogger("MDMSequentialCommandsLogger");
    }
}
