package com.me.mdm.server.apple.command.querygenerator;

import com.adventnet.sym.server.mdm.ios.payload.IOSCommandPayload;
import com.adventnet.sym.server.mdm.ios.payload.PayloadHandler;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.command.DeviceCommand;
import com.adventnet.sym.server.mdm.command.CommandQueryCreator;

public class AppleLogoutUserCommandGenerator implements CommandQueryCreator
{
    @Override
    public String createCmdQuery(final DeviceCommand deviceCommand, final String strUDID, final Long resourceID, final HashMap requestMap) throws Exception {
        final IOSCommandPayload commandPayload = new PayloadHandler().createAppleLogoutUserCommand(deviceCommand.commandUUID);
        return commandPayload.toString();
    }
}
