package com.me.mdm.server.profiles.ios;

import com.me.mdm.server.profiles.IOSInstallProfileResponseProcessor;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.seqcommands.ios.IOSSeqCmdUtil;
import java.util.logging.Level;
import com.me.mdm.server.seqcommands.SeqCmdRepository;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.server.command.CommandResponseProcessor;

public class IOSSingletonRestrictResProcessor implements CommandResponseProcessor.ImmediateSeqResponseProcessor, CommandResponseProcessor.QueuedResponseProcessor
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
        catch (final Exception e) {
            IOSSingletonRestrictResProcessor.logger.log(Level.SEVERE, e, () -> "Exception in processing Singleton restriction immediate seq processing for resource:" + String.valueOf(n));
            IOSSeqCmdUtil.getInstance().removeSeqCommandForResource(resourceID, commandUUID);
        }
        return seqResponse;
    }
    
    @Override
    public JSONObject processQueuedCommand(final JSONObject params) {
        final String commandUUID = params.optString("strCommandUuid");
        final String collectionId = MDMUtil.getInstance().getCollectionIdFromCommandUUID(commandUUID);
        if (collectionId != null) {
            return new IOSInstallProfileResponseProcessor().processQueuedCommand(params);
        }
        return null;
    }
    
    static {
        IOSSingletonRestrictResProcessor.logger = Logger.getLogger("MDMLogger");
    }
}
