package com.me.mdm.server.command.ios.QueryGenerator;

import com.adventnet.sym.server.mdm.ios.payload.IOSCommandPayload;
import org.json.JSONObject;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.ios.payload.PayloadHandler;
import com.me.mdm.server.profiles.ios.IOSSingletonRestrictionHandler;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.command.DeviceCommand;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.command.CommandQueryCreator;

public class IOSRemoveDeviceNameResQueryGenerator implements CommandQueryCreator
{
    private static final Logger LOGGER;
    
    @Override
    public String createCmdQuery(final DeviceCommand deviceCommand, final String strUDID, final Long resourceID, final HashMap requestMap) {
        try {
            final JSONObject restrictionObject = new IOSSingletonRestrictionHandler().getSingletonRestrictionConfigured(resourceID);
            restrictionObject.put("ALLOW_MODIFI_DEVICE_NAME", true);
            final PayloadHandler payloadHandler = new PayloadHandler();
            final IOSCommandPayload commandPayload = payloadHandler.createSingletonRestrictCommand("IOSRemoveDeviceNameRestriction", restrictionObject);
            return commandPayload.toString();
        }
        catch (final Exception ex) {
            IOSRemoveDeviceNameResQueryGenerator.LOGGER.log(Level.SEVERE, "Exception while creating command in remove device", ex);
            return null;
        }
    }
    
    static {
        LOGGER = Logger.getLogger("MDMLogger");
    }
}
