package com.me.mdm.server.command;

import java.util.logging.Level;
import com.adventnet.sym.server.mdm.security.MacDeviceUserUnlockHandler;
import org.json.JSONObject;
import java.util.logging.Logger;

public class DeviceUserUnlockResponseProcessor implements CommandResponseProcessor.QueuedResponseProcessor
{
    private static Logger logger;
    
    @Override
    public JSONObject processQueuedCommand(final JSONObject params) {
        final Long resourceID = params.optLong("resourceId");
        try {
            new MacDeviceUserUnlockHandler().deleteUserNameForResource(resourceID);
        }
        catch (final Exception e) {
            DeviceUserUnlockResponseProcessor.logger.log(Level.SEVERE, e, () -> "Exception while processing immediate processing for resource:" + String.valueOf(n));
        }
        return null;
    }
    
    static {
        DeviceUserUnlockResponseProcessor.logger = Logger.getLogger("MDMLogger");
    }
}
