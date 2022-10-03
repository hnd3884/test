package com.me.mdm.server.updates.osupdates.ios;

import com.me.mdm.server.seqcommands.ios.IOSSeqCmdUtil;
import com.me.mdm.server.seqcommands.SeqCmdRepository;
import java.util.List;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.server.command.CommandResponseProcessor;

public class IOSRestrictOSUpdateResponseProcessor implements CommandResponseProcessor.QueuedResponseProcessor, CommandResponseProcessor.SeqQueuedResponseProcessor
{
    private static final Logger LOGGER;
    
    @Override
    public JSONObject processQueuedCommand(final JSONObject params) {
        try {
            IOSRestrictOSUpdateResponseProcessor.LOGGER.log(Level.FINE, "Processing Queued command for IOSRestrictOSUpdateResponseProcessor, Params:{0}", new Object[] { params });
            final String strStatus = params.optString("strStatus");
            final String commandUUID = params.optString("strCommandUuid");
            final Long resourceId = params.optLong("resourceId");
            if (strStatus.equalsIgnoreCase("Acknowledged")) {
                final String collectionId = MDMUtil.getInstance().getCollectionIdFromCommandUUID(commandUUID);
                final Integer status = ProfileAssociateHandler.getInstance().getCollectionStatusForResource(resourceId, Long.parseLong(collectionId));
                IOSRestrictOSUpdateResponseProcessor.LOGGER.log(Level.FINE, "Status of the OS update collection command.{0}", new Object[(int)status]);
                if (status == 18 || status == 6) {
                    final List<Long> resourceList = new ArrayList<Long>();
                    resourceList.add(resourceId);
                    IOSRestrictOSUpdateResponseProcessor.LOGGER.log(Level.FINE, "Going to update the collection status for Restrict OS Update.");
                    MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(resourceList, Long.parseLong(collectionId), 6, "mdm.osupdate.remarks.policyScheduled");
                }
            }
            IOSRestrictOSUpdateResponseProcessor.LOGGER.log(Level.FINE, "Completed Processing of IOSRestrictOSUpdateResponseProcessor Queued command");
        }
        catch (final Exception ex) {
            IOSRestrictOSUpdateResponseProcessor.LOGGER.log(Level.SEVERE, "Exception While Processing queued command for IOS restrict update processor", ex);
        }
        return null;
    }
    
    @Override
    public JSONObject processSeqQueuedCommand(final JSONObject params) {
        IOSRestrictOSUpdateResponseProcessor.LOGGER.log(Level.FINE, "Processing Queued command for IOSRestrictOSUpdateResponseProcessor, Params:{0}", new Object[] { params });
        final String commandUUID = params.optString("strCommandUuid");
        final Long resourceID = params.optLong("resourceId");
        try {
            final JSONObject response = new JSONObject();
            final String strStatus = params.optString("strStatus");
            if (strStatus.equalsIgnoreCase("Acknowledged")) {
                response.put("action", 1);
            }
            else {
                response.put("action", 2);
            }
            final JSONObject seqParams = new JSONObject();
            response.put("UPDATEFAILED", true);
            response.put("commandUUID", (Object)commandUUID);
            response.put("resourceID", (Object)resourceID);
            response.put("params", (Object)seqParams);
            SeqCmdRepository.getInstance().processSeqCommand(response);
            IOSRestrictOSUpdateResponseProcessor.LOGGER.log(Level.FINE, "Completed Processing of seq IOSRestrictOSUpdateResponseProcessor Queued command");
        }
        catch (final Exception ex) {
            IOSRestrictOSUpdateResponseProcessor.LOGGER.log(Level.SEVERE, "Exception While Processing queued command for IOS restrict update processor", ex);
            IOSSeqCmdUtil.getInstance().removeSeqCommandForResource(resourceID, commandUUID);
        }
        return null;
    }
    
    static {
        LOGGER = Logger.getLogger("MDMLogger");
    }
}
