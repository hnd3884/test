package com.me.mdm.server.seqcommands.ios;

import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.seqcommands.SequentialSubCommand;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import com.me.mdm.server.seqcommands.SeqCmdUtils;
import org.json.JSONObject;

public class IOSAppLockSeqCmdResponseHandler extends IOSBaseSeqCmdResponseHandler
{
    @Override
    public Long onSuccess(final JSONObject params) throws Exception {
        final Long resourceID = params.optLong("resourceID");
        final String CommandUUID = params.optString("commandUUID");
        if (CommandUUID.startsWith("SingleWebAppKioskAppConfiguration")) {
            final Long collectionId = SeqCmdUtils.getInstance().getBaseCollectionIDForResource(resourceID);
            if (collectionId != null) {
                MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resourceID, String.valueOf(collectionId), 3, "mdm.profiles.ios.kiosk.single_web_app_retry");
            }
        }
        return super.onSuccess(params);
    }
    
    @Override
    public Long onFailure(final JSONObject params) throws Exception {
        final Long resourceID = params.optLong("resourceID");
        final Long collectionId = SeqCmdUtils.getInstance().getBaseCollectionIDForResource(resourceID);
        if (collectionId != null) {
            MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resourceID, String.valueOf(collectionId), 7, "mdm.profiles.ios.kiosk.single_web_app_error");
        }
        return super.onFailure(params);
    }
    
    @Override
    public boolean subCommandPreProcessor(final Long resourceID, final Long commandID, final SequentialSubCommand sequentialSubCommand) {
        super.subCommandPreProcessor(resourceID, commandID, sequentialSubCommand);
        final JSONObject params = sequentialSubCommand.params;
        final String commandUUID = MDMUtil.getInstance().getCommandUUIDFromCommandID(commandID);
        final JSONObject commandScopeParams = params.optJSONObject("cmdScopeParams");
        if (commandUUID.contains("SingleWebAppKioskAppConfiguration") && commandScopeParams != null) {
            final int status = commandScopeParams.optInt("AppInstallationStatus");
            if (status != 6) {
                return false;
            }
        }
        return true;
    }
}
