package com.me.mdm.server.command;

import org.json.JSONArray;
import java.util.List;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.command.CommandUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.logging.Level;
import com.me.mdm.server.seqcommands.SequentialSubCommand;
import java.util.logging.Logger;

public class SeqCmdImmediateServerResponseProcessor
{
    protected static Logger logger;
    private static Logger accesslogger;
    String separator;
    
    public SeqCmdImmediateServerResponseProcessor() {
        this.separator = "\t";
    }
    
    public boolean processSeqCmdResponse(final String strStatus, final String commandUUID, final Long resourceID, final String responseData, final Long customerId, final String strUUID, final SequentialSubCommand sequentialSubCommand) throws Exception {
        SeqCmdImmediateServerResponseProcessor.logger.log(Level.FINE, "Entered SeqImmediate Response Handler");
        final JSONObject sequentialCommandParams = sequentialSubCommand.params;
        final JSONObject baseParams = sequentialCommandParams.optJSONObject("initialParams");
        final JSONObject commandScopeParams = sequentialCommandParams.optJSONObject("cmdScopeParams");
        final JSONObject currentParams = sequentialCommandParams.optJSONObject("CurCmdParam");
        final List commandList = MDMUtil.getInstance().getStringList(commandUUID, ";");
        final String deviceCommand = commandList.get(0);
        final CommandResponseProcessor.ImmediateSeqResponseProcessor processors = CommandUtil.getInstance().getInstanceForImmeSeqResponse(deviceCommand);
        final JSONObject params = new JSONObject();
        params.put("strStatus", (Object)strStatus);
        params.put("resourceId", (Object)resourceID);
        params.put("strCommandUuid", (Object)commandUUID);
        params.put("strData", (Object)responseData);
        params.put("customerId", (Object)customerId);
        params.put("seqParams", (Object)sequentialCommandParams);
        params.put("baseSeqParams", (Object)baseParams);
        params.put("currentSeqParams", (Object)currentParams);
        final JSONObject response = processors.processImmediateSeqCommand(params);
        boolean isNeedForQueue = response.optBoolean("isNeedToAddQueue");
        SeqCmdImmediateServerResponseProcessor.logger.log(Level.FINE, "Response from the immediate processor.{0}", new Object[] { response });
        if (!isNeedForQueue) {
            final JSONObject queuedCommandsJSON = (sequentialCommandParams != null) ? sequentialCommandParams.optJSONObject("QueuedCommands") : null;
            final JSONArray queuedCommandUUIDs = (commandScopeParams != null) ? commandScopeParams.optJSONArray("QueueCommandUUIDS") : null;
            final JSONArray queuedCommands = (queuedCommandsJSON != null) ? queuedCommandsJSON.optJSONArray("QueuedCommands") : null;
            if (queuedCommands != null && queuedCommands.length() == 2) {
                SeqCmdImmediateServerResponseProcessor.logger.log(Level.INFO, "Command:{0} sent for iOS Queue processing.", new Object[] { commandUUID });
                isNeedForQueue = true;
            }
            else if (queuedCommandUUIDs != null && JSONUtil.findInJSONArray(queuedCommandUUIDs, commandUUID)) {
                isNeedForQueue = true;
            }
            else {
                final String accessMessage = "DATA-IN: " + commandUUID + this.separator + resourceID + this.separator + strUUID + this.separator + strStatus + this.separator + MDMUtil.getCurrentTimeInMillis();
                SeqCmdImmediateServerResponseProcessor.accesslogger.log(Level.INFO, accessMessage);
            }
        }
        return isNeedForQueue;
    }
    
    static {
        SeqCmdImmediateServerResponseProcessor.logger = Logger.getLogger("MDMSequentialCommandsLogger");
        SeqCmdImmediateServerResponseProcessor.accesslogger = Logger.getLogger("MDMCommandsLogger");
    }
}
