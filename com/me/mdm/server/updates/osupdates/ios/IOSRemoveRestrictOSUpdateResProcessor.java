package com.me.mdm.server.updates.osupdates.ios;

import com.me.mdm.server.seqcommands.ios.IOSSeqCmdUtil;
import com.me.mdm.server.seqcommands.SeqCmdRepository;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import java.util.List;
import com.me.mdm.server.updates.osupdates.OSUpdatePolicyHandler;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.server.command.CommandResponseProcessor;

public class IOSRemoveRestrictOSUpdateResProcessor implements CommandResponseProcessor.SeqQueuedResponseProcessor, CommandResponseProcessor.QueuedResponseProcessor
{
    private static final Logger LOGGER;
    
    @Override
    public JSONObject processQueuedCommand(final JSONObject params) {
        try {
            final String commandUUID = params.optString("strCommandUuid");
            final String collectionId = MDMUtil.getInstance().getCollectionIdFromCommandUUID(commandUUID);
            final Long resourceId = params.optLong("resourceId");
            final List<Long> resourceList = new ArrayList<Long>();
            resourceList.add(resourceId);
            final List<Long> collectionList = new ArrayList<Long>();
            collectionList.add(Long.parseLong(collectionId));
            MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resourceId, collectionId, 6, "dc.db.mdm.collection.Successfully_removed_the_policy");
            new OSUpdatePolicyHandler().deleteRecentProfileForResourceListCollection(resourceList, collectionList);
            ProfileAssociateHandler.getInstance().updateDeviceProfileSummary();
        }
        catch (final Exception ex) {
            IOSRemoveRestrictOSUpdateResProcessor.LOGGER.log(Level.SEVERE, "Exception while processing Remove restrict queued commands", ex);
        }
        return null;
    }
    
    @Override
    public JSONObject processSeqQueuedCommand(final JSONObject params) {
        final String commandUUID = params.optString("strCommandUuid");
        final Long resourceID = params.optLong("resourceId");
        try {
            final JSONObject response = new JSONObject();
            final JSONObject seqParams = new JSONObject();
            response.put("action", 1);
            response.put("commandUUID", (Object)commandUUID);
            response.put("resourceID", (Object)resourceID);
            response.put("params", (Object)seqParams);
            SeqCmdRepository.getInstance().processSeqCommand(response);
        }
        catch (final Exception ex) {
            IOSRemoveRestrictOSUpdateResProcessor.LOGGER.log(Level.SEVERE, ex, () -> "Exception while processing the restrict seq command. Params:" + jsonObject.toString());
            IOSSeqCmdUtil.getInstance().removeSeqCommandForResource(resourceID, commandUUID);
        }
        return null;
    }
    
    static {
        LOGGER = Logger.getLogger("MDMConfigLogger");
    }
}
