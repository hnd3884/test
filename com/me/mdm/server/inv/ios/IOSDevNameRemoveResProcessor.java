package com.me.mdm.server.inv.ios;

import com.me.mdm.server.seqcommands.ios.IOSSeqCmdUtil;
import com.me.mdm.server.seqcommands.SeqCmdRepository;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.server.command.CommandResponseProcessor;

public class IOSDevNameRemoveResProcessor implements CommandResponseProcessor.ImmediateSeqResponseProcessor
{
    private static final Logger LOGGER;
    
    @Override
    public JSONObject processImmediateSeqCommand(final JSONObject params) {
        IOSDevNameRemoveResProcessor.LOGGER.log(Level.FINE, "Processing immediate sequential command in IOSDevNameRemoveResProcessor.Params:{0}", new Object[] { params });
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
        catch (final Exception ex) {
            IOSDevNameRemoveResProcessor.LOGGER.log(Level.SEVERE, "Exception while processing the IOSDeviceRemove restriction", ex);
            IOSSeqCmdUtil.getInstance().removeSeqCommandForResource(resourceID, commandUUID);
        }
        return seqResponse;
    }
    
    static {
        LOGGER = Logger.getLogger("MDMLogger");
    }
}
