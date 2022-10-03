package com.me.mdm.server.profiles.ios.response;

import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.seqcommands.ios.IOSSeqCmdUtil;
import java.util.logging.Level;
import com.me.mdm.server.seqcommands.SeqCmdRepository;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.server.command.CommandResponseProcessor;

public class IOSSingleWebAppConfigurationResponseProcessor implements CommandResponseProcessor.ImmediateSeqResponseProcessor, CommandResponseProcessor.QueuedResponseProcessor
{
    private static Logger logger;
    
    @Override
    public JSONObject processImmediateSeqCommand(final JSONObject params) {
        final JSONObject queueResponse = new JSONObject();
        final Long resourceID = params.optLong("resourceId");
        final String commandUUID = params.optString("strCommandUuid");
        final JSONObject response = new JSONObject();
        final JSONObject seqParams = new JSONObject();
        try {
            final String status = params.optString("strStatus");
            if (status.equalsIgnoreCase("Acknowledged")) {
                response.put("action", 1);
                queueResponse.put("Status", 6);
                seqParams.put("isNeedToRemove", true);
            }
            else if (status.equalsIgnoreCase("Error") || status.equalsIgnoreCase("CommandFormatError")) {
                response.put("action", 2);
                queueResponse.put("Status", 7);
                queueResponse.put("isNeedToAddQueue", true);
            }
            response.put("commandUUID", (Object)commandUUID);
            response.put("resourceID", (Object)resourceID);
            response.put("params", (Object)seqParams);
            response.put("isNotify", false);
            SeqCmdRepository.getInstance().processSeqCommand(response);
        }
        catch (final Exception e) {
            IOSSingleWebAppConfigurationResponseProcessor.logger.log(Level.SEVERE, e, () -> "Exception in processing the single webapp response for resource:" + String.valueOf(n));
            IOSSeqCmdUtil.getInstance().removeSeqCommandForResource(resourceID, commandUUID);
        }
        return queueResponse;
    }
    
    @Override
    public JSONObject processQueuedCommand(final JSONObject params) {
        final JSONObject response = new JSONObject();
        final Long resourceID = params.optLong("resourceId");
        final String commandUUID = params.optString("strCommandUuid");
        try {
            final String collectionID = MDMUtil.getInstance().getCollectionIdFromCommandUUID(commandUUID);
            final String remarks = "mdm.profiles.ios.kiosk.single_web_app_error";
            MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resourceID, collectionID, 7, remarks);
        }
        catch (final SyMException e) {
            IOSSingleWebAppConfigurationResponseProcessor.logger.log(Level.SEVERE, "Error in IOSSingleWebAppResponseProcessor", (Throwable)e);
        }
        return response;
    }
    
    static {
        IOSSingleWebAppConfigurationResponseProcessor.logger = Logger.getLogger("MDMLogger");
    }
}
