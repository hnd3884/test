package com.me.mdm.server.migration;

import com.adventnet.sym.server.mdm.ios.payload.IOSCommandPayload;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.ios.payload.PayloadHandler;
import java.util.regex.Matcher;

public class RemoveKioskRestrictionProfileRegeneration extends MigrationFileRegeneration
{
    @Override
    public String generateFile(final Matcher removeKioskMatcher) throws Exception {
        String payloadData = null;
        final Long customerId = Long.parseLong(removeKioskMatcher.group(1));
        final Long collectionId = Long.parseLong(removeKioskMatcher.group(2));
        final DataObject dataObject = this.getProfileDetailsQuery(collectionId);
        if (dataObject != null) {
            final Row profileRow = dataObject.getFirstRow("Profile");
            final Integer platformType = (Integer)profileRow.get("PLATFORM_TYPE");
            final Integer profileType = (Integer)profileRow.get("PROFILE_TYPE");
            if (platformType == 1 && profileType == 1) {
                final PayloadHandler payloadHdlr = PayloadHandler.getInstance();
                final IOSCommandPayload commandPayload = payloadHdlr.generateCustomKioskRemoveProfile(collectionId);
                RemoveKioskRestrictionProfileRegeneration.LOGGER.log(Level.INFO, "IOS Remove KIOSK profile regeneration Collection Id : {0} Platform Type: {1} profile type: {2}", new Object[] { collectionId });
                if (commandPayload != null) {
                    final String toXMLPropertyList = commandPayload.getPayloadDict().toXMLPropertyList();
                    payloadData = toXMLPropertyList.replaceAll("%profileId%", collectionId.toString());
                }
            }
        }
        return payloadData;
    }
}
