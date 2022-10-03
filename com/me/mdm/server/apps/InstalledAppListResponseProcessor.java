package com.me.mdm.server.apps;

import com.me.mdm.server.seqcommands.ios.IOSSeqCmdUtil;
import java.util.logging.Level;
import com.me.mdm.server.seqcommands.SeqCmdRepository;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.server.command.CommandResponseProcessor;

public class InstalledAppListResponseProcessor implements CommandResponseProcessor.SeqQueuedResponseProcessor
{
    private static Logger logger;
    
    @Override
    public JSONObject processSeqQueuedCommand(final JSONObject params) {
        final String commandUUID = params.optString("strCommandUuid");
        final Long resourceID = params.optLong("resourceId");
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
            InstalledAppListResponseProcessor.logger.log(Level.SEVERE, e, () -> "Exception in processing installed app list seq Cmd for resource" + String.valueOf(n));
            IOSSeqCmdUtil.getInstance().removeSeqCommandForResource(resourceID, commandUUID);
        }
        return null;
    }
    
    static {
        InstalledAppListResponseProcessor.logger = Logger.getLogger("MDMLogger");
    }
}
