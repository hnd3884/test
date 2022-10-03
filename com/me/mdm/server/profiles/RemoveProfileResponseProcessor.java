package com.me.mdm.server.profiles;

import org.json.JSONException;
import java.util.logging.Level;
import com.me.mdm.server.seqcommands.SeqCmdRepository;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.server.command.CommandResponseProcessor;

public class RemoveProfileResponseProcessor implements CommandResponseProcessor.ImmediateSeqResponseProcessor, CommandResponseProcessor.SeqQueuedResponseProcessor
{
    private static Logger logger;
    
    @Override
    public JSONObject processImmediateSeqCommand(final JSONObject params) {
        final Long resourceId = params.optLong("resourceId");
        final String commandUUID = params.optString("strCommandUuid");
        final JSONObject seqRespose = new JSONObject();
        try {
            final JSONObject response = new JSONObject();
            final JSONObject seqParams = new JSONObject();
            seqParams.put("isNeedToRemove", true);
            response.put("action", 1);
            response.put("commandUUID", (Object)commandUUID);
            response.put("resourceID", (Object)resourceId);
            response.put("params", (Object)seqParams);
            response.put("isNotify", params.optBoolean("isNotify", (boolean)Boolean.FALSE));
            SeqCmdRepository.getInstance().processSeqCommand(response);
            seqRespose.put("isNeedToAddQueue", this.isNeedToAddQueue(params));
        }
        catch (final Exception e) {
            RemoveProfileResponseProcessor.logger.log(Level.SEVERE, e, () -> "Exception while immediate processing Seq remove profile. For resource:" + String.valueOf(n));
        }
        return seqRespose;
    }
    
    private boolean isNeedToAddQueue(final JSONObject params) throws JSONException {
        final JSONObject noNeedForRemove = new JSONObject();
        noNeedForRemove.put("kioskAppUpdate", false);
        final JSONObject baseSeqParams = params.optJSONObject("baseSeqParams");
        final String caseForSeqCmd = (baseSeqParams != null) ? baseSeqParams.optString("case", "default") : "default";
        return noNeedForRemove.optBoolean(caseForSeqCmd);
    }
    
    @Override
    public JSONObject processSeqQueuedCommand(final JSONObject params) {
        params.put("isNotify", (Object)Boolean.TRUE);
        return this.processImmediateSeqCommand(params);
    }
    
    static {
        RemoveProfileResponseProcessor.logger = Logger.getLogger("MDMLogger");
    }
}
