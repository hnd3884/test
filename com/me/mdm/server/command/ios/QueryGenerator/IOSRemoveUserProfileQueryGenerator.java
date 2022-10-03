package com.me.mdm.server.command.ios.QueryGenerator;

import com.adventnet.sym.server.mdm.ios.payload.IOSCommandPayload;
import com.me.mdm.server.profiles.ios.ConfigProfileRemoveHandler;
import com.adventnet.sym.server.mdm.ios.payload.PayloadHandler;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.command.DeviceCommand;
import com.adventnet.sym.server.mdm.command.CommandQueryCreator;

public class IOSRemoveUserProfileQueryGenerator implements CommandQueryCreator
{
    @Override
    public String createCmdQuery(final DeviceCommand deviceCommand, final String strUDID, final Long resourceID, final HashMap requestMap) {
        final IOSCommandPayload commandPayload = PayloadHandler.getInstance().createRemoveUserInstalledProfileCommand(deviceCommand.commandUUID, new ConfigProfileRemoveHandler().getPayloadIdentifierFromCommandUUID(deviceCommand.commandUUID));
        return commandPayload.toString();
    }
}
