package com.adventnet.sym.server.mdm.enroll;

import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import java.util.HashMap;
import java.util.logging.Logger;

public class BaseRemoveDeviceHandler
{
    Logger logger;
    
    public BaseRemoveDeviceHandler() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public static BaseRemoveDeviceHandler getInstance(final int platformType) {
        BaseRemoveDeviceHandler handler = null;
        switch (platformType) {
            case 4: {
                handler = new ChromeRemoveDeviceHandler();
                break;
            }
            default: {
                handler = new BaseRemoveDeviceHandler();
                break;
            }
        }
        return handler;
    }
    
    public void handleRemoveDevice(final HashMap hashMap, final Long customerId) throws Exception {
        final String strUDID = hashMap.get("UDID");
        final String strCommandUuid = hashMap.get("CommandUUID");
        DeviceCommandRepository.getInstance().deleteResourceCommand(strCommandUuid, strUDID);
        if (!DeviceCommandRepository.getInstance().hasDeviceCommandInCacheOrRepo(strUDID)) {
            ManagedDeviceHandler.getInstance().removeDeviceInTrash(ManagedDeviceHandler.getInstance().getResourceIDFromUDID(strUDID));
        }
    }
}
