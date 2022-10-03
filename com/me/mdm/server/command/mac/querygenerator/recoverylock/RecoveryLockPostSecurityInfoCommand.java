package com.me.mdm.server.command.mac.querygenerator.recoverylock;

import com.adventnet.sym.server.mdm.ios.payload.mac.MacPayloadHandler;
import java.util.logging.Level;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.command.DeviceCommand;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.command.CommandQueryCreator;

public class RecoveryLockPostSecurityInfoCommand implements CommandQueryCreator
{
    private static final Logger LOGGER;
    
    @Override
    public String createCmdQuery(final DeviceCommand deviceCommand, final String strUDID, final Long resourceID, final HashMap requestMap) throws Exception {
        RecoveryLockPostSecurityInfoCommand.LOGGER.log(Level.INFO, "Sending PostSecurityInfo Command for resource: {0}, {1}", new Object[] { resourceID, strUDID });
        return MacPayloadHandler.getInstance().createRecoveryLockPostSecurityInfoCommand().toString();
    }
    
    static {
        LOGGER = Logger.getLogger("MDMSequentialCommandsLogger");
    }
}
