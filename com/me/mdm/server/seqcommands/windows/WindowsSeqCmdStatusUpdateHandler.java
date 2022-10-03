package com.me.mdm.server.seqcommands.windows;

import org.json.JSONObject;
import com.adventnet.sym.server.mdm.util.MDMCommonConstants;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.adventnet.sym.server.mdm.apps.AppInstallationStatusHandler;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import com.me.mdm.server.seqcommands.SeqCmdUtils;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.seqcommands.BaseSeqCmdStatusUpdateHandler;

public class WindowsSeqCmdStatusUpdateHandler extends BaseSeqCmdStatusUpdateHandler
{
    @Override
    public void makeStatusUpdateforSubCommand(final Long commandID, final Long resourceID, final Long seqID) throws Exception {
        super.makeStatusUpdateforSubCommand(commandID, resourceID, seqID);
        final String commandUUID = MDMUtil.getInstance().getCommandUUIDFromCommandID(commandID);
        final JSONObject baseCmdJSON = SeqCmdUtils.getInstance().getBaseCmdDetailsforResource(resourceID);
        final String baseCmd = String.valueOf(baseCmdJSON.get("COMMAND_UUID"));
        if (commandUUID.contains("EnableSideloadApps") && baseCmd != null && baseCmd.contains("InstallApplication")) {
            final String collectionId = MDMUtil.getInstance().getCollectionIdFromCommandUUID(baseCmd);
            final String remarks = "dc.db.mdm.apps.status.automatic_install";
            MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resourceID, collectionId, 18, remarks);
        }
        if (commandUUID.contains("InstallApplication") || commandUUID.contains("UpdateApplication")) {
            final String collectionId = MDMUtil.getInstance().getCollectionIdFromCommandUUID(commandUUID);
            final String remarks = "dc.db.mdm.apps.status.automatic_install";
            MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resourceID, collectionId, 18, remarks);
            final Long appgroupID = MDMUtil.getInstance().getAppGroupIDFromCollection(Long.parseLong(collectionId));
            final Long appID = MDMUtil.getInstance().getAppIdAssociatedForResource(appgroupID, resourceID);
            if (appgroupID != null && appID != null) {
                final AppInstallationStatusHandler handler = new AppInstallationStatusHandler();
                handler.updateAppInstallationDetailsFromDevice(resourceID, appgroupID, appID, 1, remarks, 0);
                AppsUtil.getInstance().setAppPublishedSource(resourceID, appgroupID, MDMCommonConstants.ASSOCIATED_APP_SOURCE_BY_USER);
            }
        }
        if (baseCmd.contains("InstallProfile") && commandUUID.contains("InstallApplication")) {
            final String baseCollectionId = MDMUtil.getInstance().getCollectionIdFromCommandUUID(baseCmd);
            MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resourceID, baseCollectionId, 3, "mdm.profile.windows.kiosk_install_initiated");
        }
    }
}
