package com.me.mdm.server.command.ios.QueryGenerator;

import com.adventnet.sym.server.mdm.ios.payload.IOSCommandPayload;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.ios.payload.PayloadHandler;
import com.me.mdm.server.profiles.ios.IOSSingletonRestrictionHandler;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.logging.Level;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.command.DeviceCommand;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.command.CommandQueryCreator;

public class IOSSingletonRestQueryGenerator implements CommandQueryCreator
{
    private static final Logger LOGGER;
    
    @Override
    public String createCmdQuery(final DeviceCommand deviceCommand, final String strUDID, final Long resourceID, final HashMap requestMap) {
        IOSSingletonRestQueryGenerator.LOGGER.log(Level.FINE, "IOS Singleton restriction command query generator.Resource Id:{0} CommandUUID:{0}", new Object[] { resourceID, deviceCommand.commandUUID });
        Long collectionId = null;
        try {
            final String tempCollectionId = MDMUtil.getInstance().getCollectionIdFromCommandUUID(deviceCommand.commandUUID);
            collectionId = (MDMStringUtils.isEmpty(tempCollectionId) ? null : Long.valueOf(Long.parseLong(tempCollectionId)));
        }
        catch (final IndexOutOfBoundsException ex) {
            IOSSingletonRestQueryGenerator.LOGGER.log(Level.FINE, "Can't able to get the collectionId");
        }
        final JSONObject restrictionObject = new IOSSingletonRestrictionHandler().getSingletonRestrictionConfigured(resourceID, collectionId, false);
        final IOSCommandPayload commandPayload = PayloadHandler.getInstance().createSingletonRestrictCommand(deviceCommand.commandUUID, restrictionObject);
        IOSSingletonRestQueryGenerator.LOGGER.log(Level.FINE, "IOS Singleton restriction completes");
        return commandPayload.toString();
    }
    
    static {
        LOGGER = Logger.getLogger("MDMLogger");
    }
}
