package com.me.mdm.server.profiles.mac.recoverylock;

import com.adventnet.sym.server.mdm.PlistWrapper;
import com.adventnet.persistence.DataAccessException;
import com.me.mdm.server.security.mac.MacFirmwarePasswordDeviceAssociationHandler;
import com.me.mdm.server.seqcommands.SeqCmdRepository;
import java.util.List;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import java.util.logging.Level;
import org.json.JSONObject;

public class RecoveryLockVerifyPasswordResponse extends RecoveryLockResponse
{
    @Override
    public JSONObject processSeqQueuedCommand(JSONObject params) {
        params = super.processSeqQueuedCommand(params);
        final String udid = params.getString("strUDID");
        final String status = params.getString("strStatus");
        RecoveryLockVerifyPasswordResponse.LOGGER.log(Level.INFO, "Verify Password response status: {0}: {1}", new Object[] { udid, status });
        try {
            if (status.equalsIgnoreCase("Acknowledged")) {
                this.handleAcknowledgementForVerifyPassword(params);
            }
            else if (status.equalsIgnoreCase("NotNow")) {
                RecoveryLockVerifyPasswordResponse.LOGGER.log(Level.INFO, "Device has sent Not Now for Verify Passcode command: {0}", new Object[] { params.getLong("resourceId") });
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
            params.put("action", 2);
            RecoveryLockVerifyPasswordResponse.LOGGER.log(Level.SEVERE, "Exception while handling verify password response for resource: " + this.getResourceList(params), e);
        }
        SeqCmdRepository.getInstance().processSeqCommand(params);
        return params;
    }
    
    private void handleAcknowledgementForVerifyPassword(final JSONObject params) throws Exception {
        final boolean isPasswordVerified = this.isPasswordVerified(params);
        RecoveryLockVerifyPasswordResponse.LOGGER.log(Level.INFO, "Is Password present in device verified: {0}: {1}", new Object[] { this.getResourceList(params), isPasswordVerified });
        if (isPasswordVerified) {
            this.updateVerifiedPasswordForResource(params);
        }
        else {
            this.handleFailureForPasswordVerification(params);
            params.put("action", 2);
        }
    }
    
    private void updateVerifiedPasswordForResource(final JSONObject params) throws Exception {
        final long resourceId = params.getLong("resourceId");
        final JSONObject commandScopeParams = params.getJSONObject("PARAMS").getJSONObject("cmdScopeParams");
        final Long passwordSetForDevice = commandScopeParams.getLong("existingPasswordID");
        RecoveryLockVerifyPasswordResponse.LOGGER.log(Level.INFO, "Updating verified Password for resource: {0} Password Id: {1}", new Object[] { this.getResourceList(params), passwordSetForDevice });
        MacFirmwarePasswordDeviceAssociationHandler.updateDevicePasswordAfterSucessfullVerification(passwordSetForDevice, resourceId);
    }
    
    private void handleFailureForPasswordVerification(final JSONObject params) throws DataAccessException {
        this.removeExistingPasswordForResource(params);
        MDMCollectionStatusUpdate.getInstance().updateCollnToResourcesRow(this.getResourceList(params), this.getCollectionID(params), 7, "mdm.mac.recovery.password_verification_failed");
    }
    
    private void removeExistingPasswordForResource(final JSONObject params) {
        final long resourceId = params.getLong("resourceId");
        final JSONObject commandScopeParams = params.getJSONObject("PARAMS").getJSONObject("cmdScopeParams");
        final Long existingPasswordId = commandScopeParams.getLong("existingPasswordID");
        RecoveryLockVerifyPasswordResponse.LOGGER.log(Level.INFO, "Removing unverified Password for resource: {0} Password Id: {1}", new Object[] { this.getResourceList(params), existingPasswordId });
        MacFirmwarePasswordDeviceAssociationHandler.removeExistingFirmwarePasswordFromDeviceTables(resourceId, existingPasswordId);
    }
    
    private boolean isPasswordVerified(final JSONObject params) {
        final String strData = params.getString("strData");
        final String isPasswordVerifiedStr = PlistWrapper.getInstance().getValueForKeyString("PasswordVerified", strData);
        return Boolean.parseBoolean(isPasswordVerifiedStr);
    }
}
