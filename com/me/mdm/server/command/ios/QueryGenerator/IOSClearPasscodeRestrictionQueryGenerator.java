package com.me.mdm.server.command.ios.QueryGenerator;

import com.adventnet.sym.server.mdm.ios.payload.IOSCommandPayload;
import com.adventnet.sym.server.mdm.ios.payload.PayloadHandler;
import java.util.logging.Level;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.command.DeviceCommand;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.command.CommandQueryCreator;

public class IOSClearPasscodeRestrictionQueryGenerator implements CommandQueryCreator
{
    private static final Logger LOGGER;
    
    @Override
    public String createCmdQuery(final DeviceCommand deviceCommand, final String strUDID, final Long resourceID, final HashMap requestMap) {
        IOSClearPasscodeRestrictionQueryGenerator.LOGGER.log(Level.FINE, "Inside clear passcode restriction query generator");
        final IOSCommandPayload commandPayload = new PayloadHandler().getRemoveRestrictPasscodeCommand(deviceCommand.commandUUID);
        return commandPayload.toString();
    }
    
    static {
        LOGGER = Logger.getLogger("MDMLogger");
    }
}
