package com.me.mdm.server.apple.command.querygenerator;

import org.json.JSONObject;
import com.adventnet.sym.server.mdm.ios.payload.PayloadHandler;
import com.me.mdm.server.apple.useraccount.AppleMultiUserUtils;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.command.DeviceCommand;
import com.adventnet.sym.server.mdm.command.CommandQueryCreator;

public class AppleSharedDeviceConfigurationCommandGenerator implements CommandQueryCreator
{
    @Override
    public String createCmdQuery(final DeviceCommand deviceCommand, final String strUDID, final Long resourceID, final HashMap requestMap) throws Exception {
        final JSONObject configJSON = AppleMultiUserUtils.getSharedDeviceConfigurationForDevice(resourceID);
        return new PayloadHandler().createSharedConfigurationCommand(deviceCommand.commandUUID, configJSON);
    }
}
