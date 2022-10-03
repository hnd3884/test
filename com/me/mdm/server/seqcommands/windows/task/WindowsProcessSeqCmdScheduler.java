package com.me.mdm.server.seqcommands.windows.task;

import java.util.Hashtable;
import org.json.JSONException;
import com.me.mdm.server.seqcommands.SeqCmdRepository;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class WindowsProcessSeqCmdScheduler implements SchedulerExecutionInterface
{
    public Logger logger;
    
    public WindowsProcessSeqCmdScheduler() {
        this.logger = Logger.getLogger("MDMSequentialCommandsLogger");
    }
    
    public void executeTask(final Properties properties) {
        try {
            final JSONObject params = new JSONObject((String)((Hashtable<K, String>)properties).get("params"));
            final int retryCount = params.getJSONObject("cmdScopeParams").optInt("retryCount", 0);
            this.logger.log(Level.INFO, "Executing Scheduled response Processing {0} ", params);
            final String commandUUID = String.valueOf(params.get("commandUUID"));
            final Long commandID = DeviceCommandRepository.getInstance().getCommandID(commandUUID);
            final Long collectionID = DeviceCommandRepository.getInstance().getCollectionId(commandID);
            final Long resourceID = params.getLong("resourceID");
            final JSONObject Seqresponse = new JSONObject();
            if (retryCount <= 5) {
                Seqresponse.put("action", (Object)new Integer(3));
            }
            else {
                Seqresponse.put("action", (Object)new Integer(1));
            }
            Seqresponse.put("resourceID", (Object)resourceID);
            Seqresponse.put("commandUUID", (Object)commandUUID);
            final JSONObject seqparams = new JSONObject();
            Seqresponse.put("params", (Object)seqparams);
            SeqCmdRepository.getInstance().processSeqCommand(Seqresponse);
        }
        catch (final JSONException e) {
            this.logger.log(Level.WARNING, "Error in Json structure ", (Throwable)e);
        }
        catch (final Exception e2) {
            this.logger.log(Level.WARNING, "Error in ProcessSequential Later task ", e2);
        }
    }
}
