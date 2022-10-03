package com.me.mdm.server.migration;

import com.adventnet.sym.server.mdm.chrome.payload.ChromeCommandPayload;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.chrome.payload.ChromePayloadHandler;
import java.util.regex.Matcher;

public class ChromeRemoveOSUpdateProfileRegeneration extends MigrationFileRegeneration
{
    @Override
    public String generateFile(final Matcher scheduleOsUpdateMatcher) throws Exception {
        String payloadData = null;
        final Long customerId = Long.parseLong(scheduleOsUpdateMatcher.group(1));
        final Long collectionId = Long.parseLong(scheduleOsUpdateMatcher.group(2));
        final DataObject dataObject = this.getProfileDetailsQuery(collectionId);
        if (dataObject != null) {
            final Row profileRow = dataObject.getFirstRow("Profile");
            final Integer platformType = (Integer)profileRow.get("PLATFORM_TYPE");
            final Integer profileType = (Integer)profileRow.get("PROFILE_TYPE");
            if (platformType == 4 && profileType == 3) {
                final ChromePayloadHandler payloadHdlr = ChromePayloadHandler.getInstance();
                ChromeRemoveOSUpdateProfileRegeneration.LOGGER.log(Level.INFO, "Chrome Remove OS Update profile regeneration Collection Id : {0}", new Object[] { collectionId });
                final ChromeCommandPayload commandPayload = payloadHdlr.createRemoveOsUpdateCommand("RemoveOsUpdatePolicy;Collection=" + collectionId.toString(), collectionId);
                final String commandUUID = "RemoveChromeOsUpdatePolicy;Collection=" + collectionId.toString();
                commandPayload.setCommandUUID(commandUUID, false);
                payloadData = commandPayload.toString();
            }
        }
        return payloadData;
    }
}
