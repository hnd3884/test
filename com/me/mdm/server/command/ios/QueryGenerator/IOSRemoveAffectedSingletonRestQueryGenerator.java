package com.me.mdm.server.command.ios.QueryGenerator;

import com.dd.plist.NSDictionary;
import com.adventnet.sym.server.mdm.ios.payload.IOSCommandPayload;
import com.me.mdm.server.profiles.ios.IOSSingletonRestrictionHandler;
import com.adventnet.sym.server.mdm.ios.payload.PayloadHandler;
import java.util.logging.Level;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.command.DeviceCommand;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.command.CommandQueryCreator;

public class IOSRemoveAffectedSingletonRestQueryGenerator implements CommandQueryCreator
{
    private static final Logger LOGGER;
    
    @Override
    public String createCmdQuery(final DeviceCommand deviceCommand, final String strUDID, final Long resourceID, final HashMap requestMap) {
        IOSRemoveAffectedSingletonRestQueryGenerator.LOGGER.log(Level.FINE, "IOS remove affected Singleton restriction command query generator.Resource Id:{0} CommandUUID:{0}", new Object[] { resourceID, deviceCommand.commandUUID });
        try {
            final IOSCommandPayload commandPayload = PayloadHandler.getInstance().createCommandPayload("RemoveProfile");
            final NSDictionary commandDict = commandPayload.getCommandDict();
            final String s = "Identifier";
            new IOSSingletonRestrictionHandler();
            commandDict.put(s, (Object)"com.mdm.singleton.restriction");
            commandPayload.setCommandUUID(deviceCommand.commandUUID, Boolean.FALSE);
            IOSRemoveAffectedSingletonRestQueryGenerator.LOGGER.log(Level.FINE, "IOS Remove affected Singleton restriction completes");
            return commandPayload.toString();
        }
        catch (final IndexOutOfBoundsException ex) {
            IOSRemoveAffectedSingletonRestQueryGenerator.LOGGER.log(Level.FINE, "Create remove affected singleton command failed", ex);
            return null;
        }
    }
    
    static {
        LOGGER = Logger.getLogger("MDMLogger");
    }
}
