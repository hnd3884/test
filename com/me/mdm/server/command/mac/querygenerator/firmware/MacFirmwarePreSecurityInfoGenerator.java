package com.me.mdm.server.command.mac.querygenerator.firmware;

import com.adventnet.sym.server.mdm.ios.payload.mac.MacPayloadHandler;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.command.DeviceCommand;
import com.adventnet.sym.server.mdm.command.CommandQueryCreator;

public class MacFirmwarePreSecurityInfoGenerator implements CommandQueryCreator
{
    @Override
    public String createCmdQuery(final DeviceCommand deviceCommand, final String strUDID, final Long resourceID, final HashMap requestMap) {
        return MacPayloadHandler.getInstance().createFirmwarePreSecurityInfoCommand().toString();
    }
}
