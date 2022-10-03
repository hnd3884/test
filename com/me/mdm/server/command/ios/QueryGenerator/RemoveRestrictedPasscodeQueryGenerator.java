package com.me.mdm.server.command.ios.QueryGenerator;

import com.adventnet.sym.server.mdm.ios.payload.IOSCommandPayload;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.ios.payload.PayloadHandler;
import com.me.mdm.server.profiles.ios.IOSPasscodeSingletonRestrictionHandler;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.command.DeviceCommand;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.command.CommandQueryCreator;

public class RemoveRestrictedPasscodeQueryGenerator implements CommandQueryCreator
{
    private static final Logger LOGGER;
    
    @Override
    public String createCmdQuery(final DeviceCommand deviceCommand, final String strUDID, final Long resourceID, final HashMap requestMap) {
        Long collectionId = null;
        try {
            final String tempCollectionId = MDMUtil.getInstance().getCollectionIdFromCommandUUID(deviceCommand.commandUUID);
            collectionId = (MDMStringUtils.isEmpty(tempCollectionId) ? null : Long.valueOf(Long.parseLong(tempCollectionId)));
        }
        catch (final IndexOutOfBoundsException ex) {
            RemoveRestrictedPasscodeQueryGenerator.LOGGER.log(Level.FINE, "Can't able to get the collectionId");
        }
        final JSONObject restrictionObject = new IOSPasscodeSingletonRestrictionHandler().getSingletonRestrictionConfigured(resourceID, collectionId, true);
        boolean passcodeRestricted = false;
        if (restrictionObject != null && restrictionObject.length() > 0) {
            passcodeRestricted = restrictionObject.optBoolean("RESTRICT_PASSCODE");
        }
        final IOSCommandPayload commandPayload = PayloadHandler.getInstance().getRestrictPasscodeCommand(deviceCommand.commandUUID, passcodeRestricted);
        RemoveRestrictedPasscodeQueryGenerator.LOGGER.log(Level.FINE, "IOS remove passcode restriction completes");
        return commandPayload.toString();
    }
    
    static {
        LOGGER = Logger.getLogger("MDMLogger");
    }
}
