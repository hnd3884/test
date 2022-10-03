package com.me.mdm.server.migration;

import com.adventnet.sym.server.mdm.ios.payload.IOSCommandPayload;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.ios.payload.PayloadHandler;
import java.util.regex.Matcher;

public class KioskRestrictionProfileRegeneration extends MigrationFileRegeneration
{
    @Override
    public String generateFile(final Matcher installKioskMatcher) throws Exception {
        String payloadData = null;
        final Long customerId = Long.parseLong(installKioskMatcher.group(1));
        final Long collectionId = Long.parseLong(installKioskMatcher.group(2));
        final DataObject dataObject = this.getProfileDetailsQuery(collectionId);
        if (dataObject != null) {
            final Row profileRow = dataObject.getFirstRow("Profile");
            final Integer platformType = (Integer)profileRow.get("PLATFORM_TYPE");
            final Integer profileType = (Integer)profileRow.get("PROFILE_TYPE");
            if (platformType == 1 && profileType == 1) {
                final PayloadHandler payloadHdlr = PayloadHandler.getInstance();
                KioskRestrictionProfileRegeneration.LOGGER.log(Level.INFO, "IOS KIOSK profile regeneration Collection Id : {0}", new Object[] { collectionId });
                final IOSCommandPayload commandPayload = payloadHdlr.generateCustomKioskPayload(collectionId, customerId);
                if (commandPayload != null) {
                    final String toXMLPropertyList = commandPayload.getPayloadDict().toXMLPropertyList();
                    payloadData = toXMLPropertyList.replaceAll("%profileId%", collectionId.toString());
                }
            }
        }
        return payloadData;
    }
}
