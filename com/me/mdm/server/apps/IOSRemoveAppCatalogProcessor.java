package com.me.mdm.server.apps;

import com.me.mdm.server.seqcommands.ios.IOSSeqCmdUtil;
import com.me.mdm.server.seqcommands.SeqCmdRepository;
import java.util.logging.Level;
import com.me.mdm.server.profiles.IOSInstallProfileResponseProcessor;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.List;
import com.adventnet.sym.server.mdm.util.MDMiOSEntrollmentUtil;
import java.util.ArrayList;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.server.command.CommandResponseProcessor;

public class IOSRemoveAppCatalogProcessor implements CommandResponseProcessor.QueuedResponseProcessor, CommandResponseProcessor.ImmediateSeqResponseProcessor
{
    private static Logger logger;
    
    @Override
    public JSONObject processQueuedCommand(final JSONObject params) {
        final Long resourceID = params.optLong("resourceId");
        final ArrayList resourceList = new ArrayList();
        resourceList.add(resourceID);
        MDMiOSEntrollmentUtil.getInstance().addOrUpdateIOSWebClipAppCatalogStatus(resourceList, false);
        final String commandUUID = params.getString("strCommandUuid");
        try {
            final String collectionId = MDMUtil.getInstance().getCollectionIdFromCommandUUID(commandUUID);
            final Long customerId = params.getLong("customerId");
            final IOSInstallProfileResponseProcessor iosInstallProfileResponseProcessor = new IOSInstallProfileResponseProcessor();
            iosInstallProfileResponseProcessor.processSucceededProfileCommand(Long.valueOf(collectionId), resourceID, customerId);
        }
        catch (final Exception e) {
            IOSRemoveAppCatalogProcessor.logger.log(Level.INFO, "No collection id normal app catalog command");
        }
        return null;
    }
    
    @Override
    public JSONObject processImmediateSeqCommand(final JSONObject params) {
        final Long resourceID = params.optLong("resourceId");
        final String commandUUID = params.optString("strCommandUuid");
        final JSONObject seqResponse = new JSONObject();
        try {
            final JSONObject response = new JSONObject();
            final JSONObject seqParams = new JSONObject();
            response.put("action", 1);
            seqParams.put("isNeedToRemove", true);
            response.put("resourceID", (Object)resourceID);
            response.put("commandUUID", (Object)commandUUID);
            response.put("params", (Object)seqParams);
            SeqCmdRepository.getInstance().processSeqCommand(response);
        }
        catch (final Exception e) {
            IOSRemoveAppCatalogProcessor.logger.log(Level.SEVERE, e, () -> "Exception in processing clear disable passcode immediate seq processing for resource:" + String.valueOf(n));
            IOSSeqCmdUtil.getInstance().removeSeqCommandForResource(resourceID, commandUUID);
        }
        return seqResponse;
    }
    
    static {
        IOSRemoveAppCatalogProcessor.logger = Logger.getLogger("MDMSequentialCommandsLogger");
    }
}
