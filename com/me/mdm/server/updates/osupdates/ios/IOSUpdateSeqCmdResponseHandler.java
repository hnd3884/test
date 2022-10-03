package com.me.mdm.server.updates.osupdates.ios;

import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.sym.server.mdm.util.VersionChecker;
import com.me.mdm.server.updates.osupdates.OSUpdatePolicyHandler;
import com.adventnet.sym.server.mdm.inv.InventoryUtil;
import com.me.mdm.server.seqcommands.SeqCmdUtils;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.mdm.server.notification.PushNotificationHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.seqcommands.SequentialSubCommand;
import com.me.mdm.server.seqcommands.SeqCmdConstants;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.server.seqcommands.ios.IOSBaseSeqCmdResponseHandler;

public class IOSUpdateSeqCmdResponseHandler extends IOSBaseSeqCmdResponseHandler
{
    private static final Logger SEQLOGGER;
    
    @Override
    public Long onFailure(final JSONObject params) throws Exception {
        final String commandUUID = params.optString("commandUUID");
        final Long resourceId = params.optLong("resourceID");
        final JSONObject failureParams = (JSONObject)params.opt("CurCmdParam");
        final boolean forceRestrict = failureParams.optBoolean("forceRestrict", false);
        if (forceRestrict) {
            final JSONObject initialParams = (JSONObject)params.opt("initialParams");
            final Long collectionId = initialParams.optLong("COLLECTION_ID");
            IOSUpdateSeqCmdResponseHandler.SEQLOGGER.log(Level.FINE, "Going to check and update from IOSUpdateSeqCmdResponseHandler,Params:", new Object[] { params });
            new IOSOSUpdateHandler().checkAndAddRestrictOSUpdate(resourceId, collectionId);
        }
        return SeqCmdConstants.ABORT_COMMAND;
    }
    
    @Override
    public boolean subCommandPreProcessor(final Long resourceID, final Long commandID, final SequentialSubCommand sequentialSubCommand) {
        try {
            super.subCommandPreProcessor(resourceID, commandID, sequentialSubCommand);
            final String commandUUID = MDMUtil.getInstance().getCommandUUIDFromCommandID(commandID);
            final String[] spilited = commandUUID.split(";");
            final String commandName = spilited[0];
            this.logger.log(Level.INFO, "OsUpdate Subcommand preprocessor. ResourceID:{0} commandUUID:{1} params{2}", new Object[] { resourceID, commandUUID, sequentialSubCommand.params });
            if (commandName.equalsIgnoreCase("RestrictOSUpdates")) {
                return false;
            }
            if (commandName.equalsIgnoreCase("ClearPasscode")) {
                final JSONObject seqParams = sequentialSubCommand.params;
                final JSONObject currentParams = seqParams.optJSONObject("CurCmdParam");
                final JSONObject commandScopeParams = seqParams.optJSONObject("cmdScopeParams");
                if (commandScopeParams != null && commandScopeParams.has("isAllowedToSkip")) {
                    boolean isAllowedToSkip = commandScopeParams.optBoolean("isAllowedToSkip", true);
                    if (!isAllowedToSkip) {
                        final JSONObject notificationHandler = PushNotificationHandler.getInstance().getNotificationDetails(resourceID, 1);
                        final String unlockToken = notificationHandler.optString("UNLOCK_TOKEN_ENCRYPTED");
                        final boolean isTokenEmpty = MDMStringUtils.isEmpty(unlockToken);
                        if (isTokenEmpty || (currentParams != null && !currentParams.optBoolean("osDownloaded", false))) {
                            this.logger.log(Level.INFO, "Skipping the clear passcode is empty token:{0} or os downloaded:{1}", new Object[] { isTokenEmpty, currentParams.optBoolean("osDownloaded", false) });
                            isAllowedToSkip = true;
                        }
                    }
                    return !isAllowedToSkip;
                }
                return false;
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in sub command prerprocessor", e);
            return false;
        }
        return true;
    }
    
    @Override
    protected void addCommandScopeParams(final SequentialSubCommand sequentialSubCommand, final Long resourceId) {
        super.addCommandScopeParams(sequentialSubCommand, resourceId);
        try {
            final Long collectionId = SeqCmdUtils.getInstance().getCollectionIdFromSeqCmdID(sequentialSubCommand.SequentialCommandID);
            if (collectionId != null) {
                final JSONObject detailJSON = new JSONObject();
                InventoryUtil.getInstance().getOSInfo(resourceId, detailJSON);
                final JSONObject osObject = detailJSON.getJSONObject("os");
                final DataObject policyObject = OSUpdatePolicyHandler.getInstance().getOSUpdatePolicy(collectionId);
                final JSONObject params = sequentialSubCommand.params;
                final JSONObject commandScopeParams = params.getJSONObject("cmdScopeParams");
                if (osObject.has("OS_VERSION") && new VersionChecker().isGreaterOrEqual(osObject.getString("OS_VERSION"), "11.3")) {
                    commandScopeParams.put("COLLECTION_ID", (Object)collectionId);
                    final Row policyRow = policyObject.getFirstRow("OSUpdatePolicy");
                    final Integer policyType = (Integer)policyRow.get("POLICY_TYPE");
                    final Integer deferDays = (Integer)policyRow.get("DEFER_DAYS");
                    commandScopeParams.put("POLICY_TYPE", (Object)policyType);
                    commandScopeParams.put("DEFER_DAYS", (Object)deferDays);
                }
                if (policyObject.containsTable("DeploymentNotifTemplate")) {
                    final Row deploymentTemplateRow = policyObject.getRow("DeploymentNotifTemplate");
                    final boolean isAllowedToSkip = (boolean)deploymentTemplateRow.get("ALLOW_USERS_TO_SKIP");
                    commandScopeParams.put("isAllowedToSkip", isAllowedToSkip);
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in adding commandScopeparams", e);
        }
    }
    
    static {
        SEQLOGGER = Logger.getLogger("MDMSequentialCommandsLogger");
    }
}
