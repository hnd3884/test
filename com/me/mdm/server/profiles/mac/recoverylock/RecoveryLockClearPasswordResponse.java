package com.me.mdm.server.profiles.mac.recoverylock;

import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import com.me.mdm.server.security.mac.MacFirmwarePasswordDeviceAssociationHandler;
import com.me.mdm.server.seqcommands.SeqCmdRepository;
import com.adventnet.persistence.DataAccessException;
import java.util.List;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import java.util.logging.Level;
import org.json.JSONObject;

public class RecoveryLockClearPasswordResponse extends RecoveryLockResponse
{
    @Override
    public JSONObject processSeqQueuedCommand(JSONObject params) {
        params = super.processSeqQueuedCommand(params);
        final String udid = params.getString("strUDID");
        final long resourceId = params.getLong("resourceId");
        final String status = params.getString("strStatus");
        RecoveryLockClearPasswordResponse.LOGGER.log(Level.INFO, "Clear password response status: {0}: {1}", new Object[] { udid, status });
        try {
            if (status.equalsIgnoreCase("Acknowledged")) {
                this.handleAcknowledgementForClearPassword(resourceId, udid, params);
            }
            else if (status.equalsIgnoreCase("NotNow")) {
                RecoveryLockClearPasswordResponse.LOGGER.log(Level.INFO, "Device has sent Not Now for Clear Passcode command. Resource: {0}", new Object[] { params.getLong("resourceId") });
                params.put("action", 5);
            }
            else {
                MDMCollectionStatusUpdate.getInstance().updateCollnToResourcesRow(this.getResourceList(params), this.getCollectionID(params), 7, "mdm.mac.recovery.clear_passcode_failed");
                if (status.equalsIgnoreCase("Error")) {
                    this.handleError(params);
                }
                params.put("action", 2);
            }
        }
        catch (final DataAccessException e) {
            RecoveryLockClearPasswordResponse.LOGGER.log(Level.SEVERE, (Throwable)e, () -> "Exception while handling clear password response for resource: " + n);
            params.put("action", 2);
        }
        SeqCmdRepository.getInstance().processSeqCommand(params);
        return params;
    }
    
    private void handleAcknowledgementForClearPassword(final long resourceId, final String udid, final JSONObject params) {
        final long collectionId = this.getCollectionID(params);
        MacFirmwarePasswordDeviceAssociationHandler.addOrUpdateInventoryFirmwareDevice(resourceId, null);
        RecoveryLockClearPasswordResponse.LOGGER.log(Level.INFO, "Passwords removed from corresponding tables for: {0}", new Object[] { udid });
        try {
            MDMCollectionStatusUpdate.getInstance().updateCollnToResourcesRow(this.getResourceList(params), this.getCollectionID(params), 6, "dc.db.mdm.collection.Successfully_removed_the_policy");
            ProfileAssociateHandler.getInstance().deleteRecentProfileForResource(resourceId, collectionId);
        }
        catch (final DataAccessException e) {
            RecoveryLockClearPasswordResponse.LOGGER.log(Level.SEVERE, (Throwable)e, () -> "Exception while deleting recent profile for resource: " + n);
        }
    }
}
