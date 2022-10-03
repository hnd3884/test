package com.me.mdm.server.profiles.mac.recoverylock;

import com.me.mdm.server.profiles.mac.recoverylock.preprocess.RecoveryLockSeqCommandPreProcessor;
import com.me.mdm.server.seqcommands.SeqCmdRepository;
import java.util.List;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import java.util.logging.Level;
import org.json.JSONObject;

public class RecoveryLockPreSecurityInfoResponse extends RecoveryLockResponse
{
    @Override
    public JSONObject processSeqQueuedCommand(JSONObject params) {
        params = super.processSeqQueuedCommand(params);
        final String udid = params.getString("strUDID");
        final String status = params.getString("strStatus");
        RecoveryLockPreSecurityInfoResponse.LOGGER.log(Level.INFO, "PreSecurity info response status: {0}: {1}", new Object[] { udid, status });
        try {
            if (status.equalsIgnoreCase("Acknowledged")) {
                final long resourceId = params.getLong("resourceId");
                this.updateSecurityInfo(resourceId, params);
                final int seqCommandHandler = this.updateProfileStatus(resourceId, params);
                params.put("action", seqCommandHandler);
            }
            else if (status.equalsIgnoreCase("NotNow")) {
                RecoveryLockPreSecurityInfoResponse.LOGGER.log(Level.INFO, "Device has sent Not Now for Pre security Info command. Resource: {0}", new Object[] { params.getLong("resourceId") });
                params.put("action", 5);
            }
            else {
                MDMCollectionStatusUpdate.getInstance().updateCollnToResourcesRow(this.getResourceList(params), this.getCollectionID(params), 7, "mdm.mac.recovery.unknown_error");
                if (status.equalsIgnoreCase("Error")) {
                    this.handleError(params);
                }
                params.put("action", 2);
            }
        }
        catch (final Exception e) {
            RecoveryLockPreSecurityInfoResponse.LOGGER.log(Level.SEVERE, e, () -> "Exception while handling presecurity info response for resource: " + s);
            params.put("action", 2);
        }
        SeqCmdRepository.getInstance().processSeqCommand(params);
        return params;
    }
    
    private int updateProfileStatus(final long resourceId, final JSONObject params) throws Exception {
        final long collectionId = this.getCollectionID(params);
        final List<Long> resourceIdList = this.getResourceList(params);
        final JSONObject seqCommandParams = params.getJSONObject("PARAMS");
        final JSONObject commandScopeParams = seqCommandParams.getJSONObject("cmdScopeParams");
        final long existingPasswordId = commandScopeParams.optLong("existingPasswordID", -1L);
        final boolean isRecoveryLockEnabled = RecoveryLockSeqCommandPreProcessor.isRecoveryLockAlreadyEnabled(resourceId);
        RecoveryLockPreSecurityInfoResponse.LOGGER.log(Level.INFO, "Pre security profile update: Is Recovery Lock Enabled: {0}, Existing Password id: {1}, Resource: {2}", new Object[] { isRecoveryLockEnabled, existingPasswordId, resourceId });
        if (isRecoveryLockEnabled && existingPasswordId == -1L) {
            MDMCollectionStatusUpdate.getInstance().updateCollnToResourcesRow(resourceIdList, collectionId, 7, "mdm.mac.recovery.recovery_password_unknown");
            return 2;
        }
        final JSONObject initialParams = seqCommandParams.getJSONObject("initialParams");
        final boolean isClearPasswordRequested = initialParams.getBoolean("isClearPassword");
        final String profileRemarks = isClearPasswordRequested ? "mdm.profile.recovery_clear_initiated" : "mdm.profile.recovery_initiated";
        MDMCollectionStatusUpdate.getInstance().updateCollnToResourcesRow(resourceIdList, collectionId, 3, profileRemarks);
        return 1;
    }
}
