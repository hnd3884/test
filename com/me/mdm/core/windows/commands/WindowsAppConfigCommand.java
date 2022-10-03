package com.me.mdm.core.windows.commands;

import java.util.List;
import java.util.logging.Level;
import com.me.mdm.framework.syncml.core.SyncMLRequestCommand;
import com.me.mdm.framework.syncml.xml.XML2SyncMLMessageConverter;
import com.me.mdm.framework.syncml.requestcmds.GetRequestCommand;
import org.json.JSONObject;
import com.me.mdm.framework.syncml.core.SyncMLMessage;
import java.util.logging.Logger;

public class WindowsAppConfigCommand
{
    private Logger logger;
    
    public WindowsAppConfigCommand() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public void processRequest(final SyncMLMessage responseSyncML, final JSONObject cmdParams) {
        try {
            final String payloadContent = String.valueOf(cmdParams.get("payloadContent"));
            final String commandName = String.valueOf(cmdParams.get("commandName"));
            final GetRequestCommand getappStatus = new GetRequestCommand();
            getappStatus.setRequestCmdId(commandName);
            final XML2SyncMLMessageConverter convert = new XML2SyncMLMessageConverter();
            final SyncMLMessage syncMLMsg = convert.transform(payloadContent);
            final List<SyncMLRequestCommand> syncMLreqCmd = syncMLMsg.getSyncBody().getRequestCmds();
            final SyncMLRequestCommand requestCmd = syncMLreqCmd.get(0);
            responseSyncML.getSyncBody().addRequestCmd(requestCmd);
            responseSyncML.getSyncBody().setFinalMessage(Boolean.TRUE);
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception in WindowsAppConfigCommand : {0}", exp);
        }
    }
}
