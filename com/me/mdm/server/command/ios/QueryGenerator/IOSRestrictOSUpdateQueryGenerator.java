package com.me.mdm.server.command.ios.QueryGenerator;

import com.adventnet.sym.server.mdm.ios.payload.PayloadHandler;
import java.io.File;
import com.me.mdm.server.deploy.MDMMetaDataUtil;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.command.DeviceCommand;
import com.adventnet.sym.server.mdm.command.CommandQueryCreator;

public class IOSRestrictOSUpdateQueryGenerator implements CommandQueryCreator
{
    @Override
    public String createCmdQuery(final DeviceCommand deviceCommand, final String strUDID, final Long resourceID, final HashMap requestMap) {
        final String clientDataParentDir = MDMMetaDataUtil.getInstance().getClientDataParentDir();
        final String profileFullPath = clientDataParentDir + File.separator + deviceCommand.commandFilePath;
        return PayloadHandler.getInstance().readProfileFromFile(profileFullPath);
    }
}
