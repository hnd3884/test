package com.me.mdm.server.command.ios.QueryGenerator;

import com.adventnet.sym.server.mdm.ios.payload.IOSCommandPayload;
import com.adventnet.sym.server.mdm.ios.payload.PayloadHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.command.DeviceCommand;
import com.adventnet.sym.server.mdm.command.CommandQueryCreator;

public class IOSRemoveRestOSUpdateQueryGenerator implements CommandQueryCreator
{
    @Override
    public String createCmdQuery(final DeviceCommand deviceCommand, final String strUDID, final Long resourceID, final HashMap requestMap) {
        final String commandUUID = deviceCommand.commandUUID;
        final String collectionId = MDMUtil.getInstance().getCollectionIdFromCommandUUID(commandUUID);
        final IOSCommandPayload commandPayload = new PayloadHandler().createRemoveOSUpdateRestrictionCommand(Long.parseLong(collectionId));
        return commandPayload.toString();
    }
}
