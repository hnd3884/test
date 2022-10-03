package com.me.mdm.server.command.mac.querygenerator.recoverylock;

import org.json.JSONObject;
import com.me.mdm.server.seqcommands.SequentialSubCommand;
import com.adventnet.sym.server.mdm.ios.payload.mac.MacPayloadHandler;
import com.me.mdm.server.security.mac.MacFirmwareUtil;
import com.me.mdm.server.seqcommands.SeqCmdUtils;
import com.me.mdm.server.security.mac.recoverylock.RecoveryLock;
import java.util.logging.Level;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.command.DeviceCommand;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.command.CommandQueryCreator;

public class RecoveryLockClearPasswordCommand implements CommandQueryCreator
{
    private static final Logger LOGGER;
    
    @Override
    public String createCmdQuery(final DeviceCommand deviceCommand, final String strUDID, final Long resourceId, final HashMap requestMap) throws Exception {
        RecoveryLockClearPasswordCommand.LOGGER.log(Level.INFO, "Sending Clear password command for resource: {0}, {1}, {2}", new Object[] { resourceId, strUDID, deviceCommand.commandUUID });
        if (deviceCommand.commandUUID.equals(RecoveryLock.CLEAR_PASSWORD.command)) {
            final SequentialSubCommand subCommand = SeqCmdUtils.getInstance().getIfSequentialCommandResponse(resourceId, deviceCommand.commandUUID);
            if (subCommand != null) {
                final JSONObject commandScopeParams = subCommand.params.getJSONObject("cmdScopeParams");
                final Long existingPasswordId = commandScopeParams.getLong("existingPasswordID");
                final String existingPassword = MacFirmwareUtil.getMDMManagedPassword(existingPasswordId);
                RecoveryLockClearPasswordCommand.LOGGER.log(Level.INFO, "Existing password while clearing for resource: {0}, {1}", new Object[] { strUDID, existingPasswordId });
                return MacPayloadHandler.getInstance().createClearRecoveryLockPasswordCommand(existingPassword).toString();
            }
        }
        return null;
    }
    
    static {
        LOGGER = Logger.getLogger("MDMSequentialCommandsLogger");
    }
}
