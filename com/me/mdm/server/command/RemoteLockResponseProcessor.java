package com.me.mdm.server.command;

import java.util.logging.Level;
import com.me.mdm.server.seqcommands.SeqCmdRepository;
import org.json.JSONObject;
import java.util.logging.Logger;

public class RemoteLockResponseProcessor implements CommandResponseProcessor.SeqQueuedResponseProcessor
{
    private static Logger logger;
    
    @Override
    public JSONObject processSeqQueuedCommand(final JSONObject params) {
        final Long resourceID = params.optLong("resourceId");
        final String commandUUID = params.optString("strCommandUuid");
        try {
            final JSONObject response = new JSONObject();
            final JSONObject seqParams = new JSONObject();
            response.put("action", 1);
            response.put("resourceID", (Object)resourceID);
            response.put("commandUUID", (Object)commandUUID);
            response.put("params", (Object)seqParams);
            SeqCmdRepository.getInstance().processSeqCommand(response);
        }
        catch (final Exception e) {
            RemoteLockResponseProcessor.logger.log(Level.SEVERE, e, () -> "Exception while processing immediate processing for resource:" + String.valueOf(n));
        }
        return null;
    }
    
    static {
        RemoteLockResponseProcessor.logger = Logger.getLogger("MDMLogger");
    }
}
