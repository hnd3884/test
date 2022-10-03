package com.me.mdm.server.seqcommands.ios;

import org.json.JSONObject;
import org.json.JSONException;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.seqcommands.SequentialSubCommand;
import java.util.logging.Logger;

public class IOSPasscodeSeqCmdResponseHandler extends IOSBaseSeqCmdResponseHandler
{
    private static final Logger LOGGER;
    
    @Override
    public boolean subCommandPreProcessor(final Long resourceID, final Long commandID, final SequentialSubCommand sequentialSubCommand) {
        super.subCommandPreProcessor(resourceID, commandID, sequentialSubCommand);
        try {
            final JSONObject params = sequentialSubCommand.params;
            final String commandUUID = MDMUtil.getInstance().getCommandUUIDFromCommandID(commandID);
            final JSONObject commandScopeParams = params.getJSONObject("cmdScopeParams");
            if (commandScopeParams != null && commandScopeParams.length() > 0) {
                if (commandUUID.contains("ClearPasscodeForPasscodeRestriction")) {
                    final boolean isSupervised = commandScopeParams.optBoolean("isSupervised");
                    if (!isSupervised) {
                        return false;
                    }
                }
            }
            else {
                IOSPasscodeSeqCmdResponseHandler.LOGGER.log(Level.INFO, "No command scope params for seqhandler");
            }
            return true;
        }
        catch (final JSONException e) {
            IOSPasscodeSeqCmdResponseHandler.LOGGER.log(Level.SEVERE, "Exception in subcommand preprocessor", (Throwable)e);
            return false;
        }
    }
    
    static {
        LOGGER = Logger.getLogger("MDMSequentialCommandsLogger");
    }
}
