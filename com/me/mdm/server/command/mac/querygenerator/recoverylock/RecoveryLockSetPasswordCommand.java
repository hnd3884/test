package com.me.mdm.server.command.mac.querygenerator.recoverylock;

import org.json.JSONObject;
import com.me.mdm.server.seqcommands.SequentialSubCommand;
import com.adventnet.sym.server.mdm.ios.payload.mac.MacPayloadHandler;
import com.me.mdm.server.security.mac.MacFirmwareUtil;
import com.me.mdm.server.profiles.mac.recoverylock.preprocess.RecoveryLockSeqCommandPreProcessor;
import com.me.mdm.server.seqcommands.SeqCmdUtils;
import java.util.logging.Level;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.command.DeviceCommand;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.command.CommandQueryCreator;

public class RecoveryLockSetPasswordCommand implements CommandQueryCreator
{
    private static final Logger LOGGER;
    
    @Override
    public String createCmdQuery(final DeviceCommand deviceCommand, final String strUDID, final Long resourceId, final HashMap requestMap) throws Exception {
        final String deviceCmd = deviceCommand.commandUUID.split(";")[0];
        RecoveryLockSetPasswordCommand.LOGGER.log(Level.INFO, "Sending Set password command for resource: {0}, {1}, {2}", new Object[] { resourceId, strUDID, deviceCmd });
        if (deviceCmd.equals("SetRecoveryLock")) {
            final SequentialSubCommand subCommand = SeqCmdUtils.getInstance().getIfSequentialCommandResponse(resourceId, deviceCommand.commandUUID);
            if (subCommand != null) {
                final boolean isRecoveryLockEnabled = RecoveryLockSeqCommandPreProcessor.isRecoveryLockAlreadyEnabled(resourceId);
                String currentPassword = null;
                if (isRecoveryLockEnabled) {
                    final JSONObject commandScopeParams = subCommand.params.getJSONObject("cmdScopeParams");
                    final Long existingPasswordId = commandScopeParams.getLong("existingPasswordID");
                    currentPassword = MacFirmwareUtil.getMDMManagedPassword(existingPasswordId);
                    RecoveryLockSetPasswordCommand.LOGGER.log(Level.INFO, "Recovery Lock Enabled for resource: {0}: {1}", new Object[] { strUDID, existingPasswordId });
                }
                final JSONObject commandScopeParams = subCommand.params.getJSONObject("cmdScopeParams");
                final Long newPasswordId = commandScopeParams.getLong("newPasswordID");
                final String newPassword = MacFirmwareUtil.getMDMManagedPassword(newPasswordId);
                RecoveryLockSetPasswordCommand.LOGGER.log(Level.INFO, "New password for resource: {0}: {1}", new Object[] { strUDID, newPasswordId });
                return MacPayloadHandler.getInstance().createSetRecoveryLockPasswordCommand(newPassword, currentPassword, deviceCommand.commandUUID).toString();
            }
        }
        return null;
    }
    
    static {
        LOGGER = Logger.getLogger("MDMSequentialCommandsLogger");
    }
}
