package com.me.mdm.server.profiles.mac.recoverylock;

import com.me.mdm.server.security.mac.MacFirmwarePasswordDeviceAssociationHandler;
import com.me.mdm.server.seqcommands.SeqCmdRepository;
import com.adventnet.persistence.DataAccessException;
import java.util.List;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import java.util.logging.Level;
import org.json.JSONObject;

public class RecoveryLockSetPasswordResponse extends RecoveryLockResponse
{
    @Override
    public JSONObject processSeqQueuedCommand(JSONObject params) {
        params = super.processSeqQueuedCommand(params);
        final String udid = params.getString("strUDID");
        final String status = params.getString("strStatus");
        RecoveryLockSetPasswordResponse.LOGGER.log(Level.INFO, "Set Password response status: {0}: {1}", new Object[] { udid, status });
        try {
            if (status.equalsIgnoreCase("Acknowledged")) {
                this.handleAcknowledgementForSetPassword(params);
            }
            else if (status.equalsIgnoreCase("NotNow")) {
                RecoveryLockSetPasswordResponse.LOGGER.log(Level.INFO, "Device has sent Not Now for Set Passcode command. Resource: {0}", new Object[] { params.getLong("resourceId") });
                params.put("action", 5);
            }
            else {
                MDMCollectionStatusUpdate.getInstance().updateCollnToResourcesRow(this.getResourceList(params), this.getCollectionID(params), 7, "mdm.mac.recovery.set_passcode_failed");
                if (status.equalsIgnoreCase("Error")) {
                    this.handleError(params);
                }
                params.put("action", 2);
            }
        }
        catch (final DataAccessException e) {
            params.put("action", 2);
            RecoveryLockSetPasswordResponse.LOGGER.log(Level.SEVERE, "Exception while handling verify password response for resource: " + this.getResourceList(params), (Throwable)e);
        }
        SeqCmdRepository.getInstance().processSeqCommand(params);
        return params;
    }
    
    private void handleAcknowledgementForSetPassword(final JSONObject params) {
        final long collectionId = this.getCollectionID(params);
        final long resourceId = params.getLong("resourceId");
        final List<Long> resourceIdList = this.getResourceList(params);
        try {
            final JSONObject commandScopeParams = params.getJSONObject("PARAMS").getJSONObject("cmdScopeParams");
            final Long newPasswordId = commandScopeParams.getLong("newPasswordID");
            MacFirmwarePasswordDeviceAssociationHandler.updateDevicePasswordAfterSucessfullPasswordSet(newPasswordId, resourceId);
            MacFirmwarePasswordDeviceAssociationHandler.addOrUpdateInventoryFirmwareDevice(resourceId, newPasswordId);
            MDMCollectionStatusUpdate.getInstance().updateCollnToResourcesRow(resourceIdList, collectionId, 6, "dc.db.mdm.collection.Successfully_applied_policy");
            RecoveryLockSetPasswordResponse.LOGGER.log(Level.INFO, "Device Success response for Set Passcode command handled. Resource: {0}", new Object[] { params.getLong("resourceId") });
        }
        catch (final Exception e) {
            RecoveryLockSetPasswordResponse.LOGGER.log(Level.SEVERE, e, () -> "Exception while updating device password after setting in device for resource: " + n + " collection: " + n2);
            params.put("action", 2);
        }
    }
}
