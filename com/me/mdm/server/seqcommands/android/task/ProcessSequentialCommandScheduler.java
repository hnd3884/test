package com.me.mdm.server.seqcommands.android.task;

import java.util.Hashtable;
import org.json.JSONException;
import com.me.mdm.server.seqcommands.SeqCmdRepository;
import com.me.mdm.server.seqcommands.android.AndroidSeqCmdUtil;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class ProcessSequentialCommandScheduler implements SchedulerExecutionInterface
{
    public Logger logger;
    
    public ProcessSequentialCommandScheduler() {
        this.logger = Logger.getLogger("MDMSequentialCommandsLogger");
    }
    
    public void executeTask(final Properties properties) {
        try {
            final JSONObject params = new JSONObject((String)((Hashtable<K, String>)properties).get("params"));
            this.logger.log(Level.INFO, "Executing Scheduled response Processing {0} ", params);
            final int retryCount = params.getJSONObject("CurCmdParam").optInt("retryCount", 0);
            final String commandUUID = String.valueOf(params.get("commandUUID"));
            final Long commandID = DeviceCommandRepository.getInstance().getCommandID(commandUUID);
            final Long collectionID = DeviceCommandRepository.getInstance().getCollectionId(commandID);
            final Long resourceID = params.getLong("resourceID");
            this.logger.log(Level.INFO, "Processing Scheduled Sequential Command ({0}) for resource :{1}", new Object[] { commandUUID, resourceID });
            if (commandUUID.startsWith("InstallApplication")) {
                final JSONObject responseJSON = new JSONObject();
                if (AndroidSeqCmdUtil.getInstance().isAppInstallSuccessfulforResource(resourceID, collectionID)) {
                    responseJSON.put("action", 1);
                }
                else if (retryCount <= 1) {
                    responseJSON.put("action", 4);
                }
                else {
                    responseJSON.put("action", 1);
                }
                responseJSON.put("resourceID", (Object)resourceID);
                responseJSON.put("commandUUID", (Object)commandUUID);
                final JSONObject paramsJSON = new JSONObject();
                paramsJSON.put("retryCount", retryCount + 1);
                responseJSON.put("params", (Object)paramsJSON);
                SeqCmdRepository.getInstance().processSeqCommand(responseJSON);
            }
        }
        catch (final JSONException e) {
            this.logger.log(Level.WARNING, "Error in Json structure ", (Throwable)e);
        }
        catch (final Exception e2) {
            this.logger.log(Level.WARNING, "Error in ProcessSequential Later task ", e2);
        }
    }
}
