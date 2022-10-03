package com.me.mdm.server.profiles.mac.recoverylock.preprocess;

import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.sym.server.mdm.inv.MDMInvDataPopulator;
import com.me.mdm.server.security.mac.MacFirmwarePasswordDeviceAssociationHandler;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import java.util.List;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import java.util.ArrayList;
import com.me.mdm.server.security.mac.recoverylock.RecoveryLock;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.seqcommands.SequentialSubCommand;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import org.json.JSONObject;
import com.me.mdm.server.seqcommands.BaseSeqCmdResponseHandler;

public class RecoveryLockSeqCommandPreProcessor extends BaseSeqCmdResponseHandler
{
    @Override
    public Long onSuccess(final JSONObject params) throws Exception {
        return super.onSuccess(params);
    }
    
    @Override
    public Long onFailure(final JSONObject params) throws Exception {
        return super.onFailure(params);
    }
    
    @Override
    public Long retry(final JSONObject params) throws Exception {
        return super.retry(params);
    }
    
    @Override
    public Long notNow(final JSONObject params) throws Exception {
        try {
            final Long commandID = super.notNow(params);
            final Long resourceID = params.getLong("resourceID");
            DeviceCommandRepository.getInstance().updateResourceCommandStatus(commandID, resourceID, 1, 12);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, e, () -> "Exception while handling not now for params: " + jsonObject);
        }
        return super.notNow(params);
    }
    
    @Override
    public boolean subCommandPreProcessor(final Long resourceId, final Long commandId, final SequentialSubCommand nextSubCommand) {
        boolean allowCurrentCommand = false;
        final String commandUuid = MDMUtil.getInstance().getCommandUUIDFromCommandID(commandId).split(";")[0];
        if (commandUuid.equalsIgnoreCase(RecoveryLock.PRE_SECURITY.command)) {
            this.addPasswordDetailsToSeqCommandScope(resourceId, nextSubCommand);
            allowCurrentCommand = true;
        }
        else if (commandUuid.equalsIgnoreCase(RecoveryLock.VERIFY_PASSWORD.command)) {
            allowCurrentCommand = this.isVerifyPasswordCommandAllowed(resourceId, nextSubCommand);
        }
        else if (commandUuid.equalsIgnoreCase("SetRecoveryLock")) {
            allowCurrentCommand = this.isSetPasswordCommandAllowed(resourceId, nextSubCommand);
        }
        else if (commandUuid.equalsIgnoreCase(RecoveryLock.CLEAR_PASSWORD.command)) {
            allowCurrentCommand = this.isAllowClearPasswordCommand(resourceId, nextSubCommand);
        }
        else if (commandUuid.equalsIgnoreCase(RecoveryLock.POST_SECURITY.command)) {
            allowCurrentCommand = true;
        }
        return allowCurrentCommand;
    }
    
    private boolean isVerifyPasswordCommandAllowed(final long resourceId, final SequentialSubCommand nextSubCommand) {
        boolean isAllowVerifyPasswordCommand = false;
        try {
            final boolean isRecoveryLockAlreadyEnabled = isRecoveryLockAlreadyEnabled(resourceId);
            final boolean isExistingPasswordAvailable = this.isExistingPasswordAvailableInCommandScope(nextSubCommand);
            this.logger.log(Level.INFO, "Verify Passcode command details for resource: {0} -> Recovery: {1}, Existing Password available: {2}", new Object[] { resourceId, isRecoveryLockAlreadyEnabled, isExistingPasswordAvailable });
            if (isRecoveryLockAlreadyEnabled && isExistingPasswordAvailable) {
                isAllowVerifyPasswordCommand = true;
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, e, () -> "Exception while checking whether recovery lock is already enabled in the device: " + n);
        }
        return isAllowVerifyPasswordCommand;
    }
    
    private boolean isSetPasswordCommandAllowed(final long resourceId, final SequentialSubCommand nextSubCommand) {
        if (this.isClearPasscodeRequested(nextSubCommand)) {
            this.logger.log(Level.INFO, "Clear Passcode requested for resource: {0}", new Object[] { resourceId });
            return false;
        }
        boolean isAllowSetPasswordCommand = false;
        try {
            final boolean isRecoveryLockAlreadyEnabled = isRecoveryLockAlreadyEnabled(resourceId);
            final boolean isExistingPasswordAvailable = this.isExistingPasswordAvailableInCommandScope(nextSubCommand);
            final boolean isNewPasswordAvailable = this.isNewPasswordAvailableInCommandScope(nextSubCommand);
            this.logger.log(Level.INFO, "Set Passcode command details for resource: {0} -> Recovery: {1}, Existing Password available: {2}, New Password: {3}", new Object[] { resourceId, isRecoveryLockAlreadyEnabled, isExistingPasswordAvailable, isNewPasswordAvailable });
            if (isNewPasswordAvailable) {
                isAllowSetPasswordCommand = true;
            }
            if (isNewPasswordAvailable && isRecoveryLockAlreadyEnabled && !isExistingPasswordAvailable) {
                isAllowSetPasswordCommand = false;
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, e, () -> "Exception while getting password to be set on device for resource: " + n);
        }
        return isAllowSetPasswordCommand;
    }
    
    private boolean isAllowClearPasswordCommand(final long resourceId, final SequentialSubCommand nextSubCommand) {
        if (!this.isClearPasscodeRequested(nextSubCommand)) {
            this.logger.log(Level.INFO, "Clear Passcode not requested for resource: {0}", new Object[] { resourceId });
            return false;
        }
        boolean isAllowClearPasswordCommand = false;
        try {
            final boolean isRecoveryLockAlreadyEnabled = isRecoveryLockAlreadyEnabled(resourceId);
            final boolean isExistingPasswordAvailable = this.isExistingPasswordAvailableInCommandScope(nextSubCommand);
            this.logger.log(Level.INFO, "Clear Passcode command details for resource: {0} -> Recovery: {1}, Existing Password available: {2}", new Object[] { resourceId, isRecoveryLockAlreadyEnabled, isExistingPasswordAvailable });
            if (isRecoveryLockAlreadyEnabled && isExistingPasswordAvailable) {
                isAllowClearPasswordCommand = true;
            }
            if (!isRecoveryLockAlreadyEnabled) {
                this.deleteRecoveryProfileForResource(resourceId, nextSubCommand);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, e, () -> "Exception while checking whether recovery lock is already enabled in the device: " + n);
        }
        return isAllowClearPasswordCommand;
    }
    
    private void deleteRecoveryProfileForResource(final long resourceId, final SequentialSubCommand nextSubCommand) {
        try {
            this.logger.log(Level.INFO, "No recovery lock present in device: {0}. So deleting the profile", new Object[] { resourceId });
            final JSONObject commandLevelParams = nextSubCommand.params.getJSONObject("CommandLevelParams");
            final Long collectionId = commandLevelParams.getLong("CollectionID");
            final List<Long> resourceList = new ArrayList<Long>();
            resourceList.add(resourceId);
            MDMCollectionStatusUpdate.getInstance().updateCollnToResourcesRow(resourceList, collectionId, 6, "dc.db.mdm.collection.Successfully_removed_the_policy");
            ProfileAssociateHandler.getInstance().deleteRecentProfileForResource(resourceId, collectionId);
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, (Throwable)e, () -> "Exception while deleting recent profile for resource during pre process: " + n);
        }
    }
    
    private void addPasswordDetailsToSeqCommandScope(final Long resourceId, final SequentialSubCommand nextSubCommand) {
        try {
            final JSONObject commandScopeParams = MacFirmwarePasswordDeviceAssociationHandler.getFirmwarePasswordCommonParams(resourceId);
            this.logger.log(Level.INFO, "Command Scope Params for Recovery Lock Sequential command: {0}, Resource: {1}, Params: {2}", new Object[] { nextSubCommand.SequentialCommandID, resourceId, commandScopeParams });
            nextSubCommand.params.put("cmdScopeParams", (Object)commandScopeParams);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, e, () -> "Exception while getting password for resource: " + n);
        }
    }
    
    private boolean isExistingPasswordAvailableInCommandScope(final SequentialSubCommand nextSubCommand) {
        final JSONObject commandScopeParams = nextSubCommand.params.getJSONObject("cmdScopeParams");
        final long existingPasswordId = commandScopeParams.optLong("existingPasswordID", -1L);
        return existingPasswordId != -1L;
    }
    
    private boolean isNewPasswordAvailableInCommandScope(final SequentialSubCommand nextSubCommand) {
        final JSONObject commandScopeParams = nextSubCommand.params.getJSONObject("cmdScopeParams");
        final long newPasswordId = commandScopeParams.optLong("newPasswordID", -1L);
        return newPasswordId != -1L;
    }
    
    private boolean isClearPasscodeRequested(final SequentialSubCommand nextSubCommand) {
        final JSONObject initialParams = nextSubCommand.params.getJSONObject("initialParams");
        return initialParams.getBoolean("isClearPassword");
    }
    
    public static boolean isRecoveryLockAlreadyEnabled(final long resourceId) throws Exception {
        final DataObject dataObject = new MDMInvDataPopulator().getMacFirmwareDO(resourceId);
        if (!dataObject.isEmpty()) {
            final Row row = dataObject.getFirstRow("MDMDeviceFirmwareInfo");
            return (boolean)row.get("IS_FIRMWARE_PASSWORD_EXISTS");
        }
        throw new Exception("No row found in MDMDeviceFirmwareInfo for the given resource: " + resourceId);
    }
}
