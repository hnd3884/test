package com.me.mdm.server.migration;

import com.adventnet.sym.server.mdm.ios.payload.IOSCommandPayload;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.sym.server.mdm.ios.payload.PayloadHandler;
import java.util.logging.Level;
import java.util.regex.Matcher;

public class PasscodeRestrictProfileRegeneration extends MigrationFileRegeneration
{
    @Override
    public String generateFile(final Matcher matcher) throws Exception {
        String payloadData = null;
        final Long customerId = Long.parseLong(matcher.group(1));
        final Long collectionId = Long.parseLong(matcher.group(2));
        final DataObject dataObject = this.getProfileDetailsQuery(collectionId);
        if (dataObject != null) {
            final Row profileRow = dataObject.getFirstRow("Profile");
            final Integer platformType = (Integer)profileRow.get("PLATFORM_TYPE");
            final Integer profileType = (Integer)profileRow.get("PROFILE_TYPE");
            final String payloadIdentifier = (String)profileRow.get("PROFILE_PAYLOAD_IDENTIFIER");
            PasscodeRestrictProfileRegeneration.LOGGER.log(Level.INFO, "Remove passcode disable profile regeneration Collection Id : {0} Platform Type: {1} profile type: {2}", new Object[] { collectionId, platformType, profileType });
            if (platformType == 1 || platformType == 6 || platformType == 7) {
                final String commandUUID = "RestrictPasscode;Collection=" + collectionId.toString();
                final PayloadHandler payloadHdlr = PayloadHandler.getInstance();
                final IOSCommandPayload commandPayload = payloadHdlr.getRestrictPasscodeCommand(commandUUID, true);
                if (commandPayload != null) {
                    payloadData = commandPayload.getPayloadDict().toXMLPropertyList();
                }
            }
        }
        return payloadData;
    }
}
