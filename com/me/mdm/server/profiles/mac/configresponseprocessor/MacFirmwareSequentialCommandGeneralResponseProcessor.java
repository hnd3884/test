package com.me.mdm.server.profiles.mac.configresponseprocessor;

import com.me.mdm.server.seqcommands.ios.IOSSeqCmdUtil;
import java.util.logging.Level;
import java.util.Map;
import java.util.List;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import java.util.ArrayList;
import com.me.mdm.server.ios.error.IOSErrorStatusHandler;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.server.command.CommandResponseProcessor;

public class MacFirmwareSequentialCommandGeneralResponseProcessor implements CommandResponseProcessor.SeqQueuedResponseProcessor
{
    public Logger logger;
    
    public MacFirmwareSequentialCommandGeneralResponseProcessor() {
        this.logger = Logger.getLogger("MDMSequentialCommandsLogger");
    }
    
    @Override
    public JSONObject processSeqQueuedCommand(final JSONObject requestJSON) {
        final JSONObject sequentialParams = new JSONObject();
        final Long resourceID = JSONUtil.optLongForUVH(requestJSON, "resourceId", Long.valueOf(-1L));
        final String commandUDID = requestJSON.optString("strCommandUuid");
        try {
            final String status = String.valueOf(requestJSON.get("strStatus"));
            final String strData = String.valueOf(requestJSON.get("strData"));
            final JSONObject sequentialCommandParams = requestJSON.getJSONObject("PARAMS");
            final JSONObject commandLevelParams = sequentialCommandParams.optJSONObject("CommandLevelParams");
            final Long collectionID = commandLevelParams.optLong("CollectionID", -1L);
            if (status.equalsIgnoreCase("Acknowledged")) {
                sequentialParams.put("action", 1);
            }
            else if (status.equalsIgnoreCase("NotNow")) {
                sequentialParams.put("action", 5);
            }
            else {
                sequentialParams.put("action", 2);
                if (status.equalsIgnoreCase("Error")) {
                    final JSONObject errorJSON = new IOSErrorStatusHandler().getIOSErrors(strData, strData, status);
                    sequentialParams.put("Remarks", errorJSON.get("EnglishRemarks"));
                    sequentialParams.put("ErrorCode", errorJSON.get("ErrorCode"));
                    if (collectionID != null) {
                        final ArrayList resurceIDList = new ArrayList();
                        resurceIDList.add(resourceID);
                        MDMCollectionStatusUpdate.getInstance().updateCollnToResourcesRow(resurceIDList, collectionID, 7, "mdm.mac.firmware.previous_command_requires_restart");
                    }
                }
            }
            sequentialParams.put("resourceID", (Object)resourceID);
            sequentialParams.put("commandUUID", (Object)commandUDID);
            sequentialParams.put("params", (Map)new org.json.simple.JSONObject());
            sequentialParams.put("isNeedToAddQueue", true);
            JSONUtil.putAll(requestJSON, sequentialParams);
            this.logger.log(Level.INFO, "----------  Base MacFirmwareSequentialCommandGeneralResponseProcessor -----------");
            this.logger.log(Level.INFO, "Base  MacFirmwareSequentialCommandGeneralResponseProcessor : {0}", requestJSON);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception processImmediateSeqCommand", ex);
            IOSSeqCmdUtil.getInstance().removeSeqCommandForResource(resourceID, commandUDID);
        }
        return requestJSON;
    }
}
