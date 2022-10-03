package com.me.mdm.server.profiles.ios.response;

import com.me.mdm.server.seqcommands.ios.IOSSeqCmdUtil;
import com.me.mdm.server.seqcommands.SeqCmdRepository;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.server.command.CommandResponseProcessor;

public class IOSRemoveRestrictPasscodeResponseProcessor implements CommandResponseProcessor.ImmediateSeqResponseProcessor
{
    private static final Logger LOGGER;
    
    @Override
    public JSONObject processImmediateSeqCommand(final JSONObject params) {
        final Long resourceID = params.optLong("resourceId");
        final String commandUUID = params.optString("strCommandUuid");
        IOSRemoveRestrictPasscodeResponseProcessor.LOGGER.log(Level.FINE, "Inside remove passcode restrict immediate response processor resourceID:{0}", new Object[] { resourceID });
        final JSONObject seqResponse = new JSONObject();
        try {
            final JSONObject response = new JSONObject();
            final JSONObject seqParams = new JSONObject();
            final String status = params.optString("strStatus");
            if (!status.equalsIgnoreCase("NotNow")) {
                seqParams.put("isNeedToRemove", true);
                response.put("resourceID", (Object)resourceID);
                response.put("commandUUID", (Object)commandUUID);
                response.put("params", (Object)seqParams);
                response.put("action", 1);
                SeqCmdRepository.getInstance().processSeqCommand(response);
            }
            else {
                seqResponse.put("isNeedToAddQueue", true);
            }
        }
        catch (final Exception ex) {
            IOSRemoveRestrictPasscodeResponseProcessor.LOGGER.log(Level.SEVERE, "Exception while processing the IOSDeviceRemove restriction", ex);
            IOSSeqCmdUtil.getInstance().removeSeqCommandForResource(resourceID, commandUUID);
        }
        return seqResponse;
    }
    
    static {
        LOGGER = Logger.getLogger("MDMSequentialCommandsLogger");
    }
}
