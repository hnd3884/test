package com.me.mdm.server.migration;

import com.adventnet.sym.server.mdm.android.payload.AndroidCommandPayload;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.android.payload.AndroidPayloadHandler;
import java.util.regex.Matcher;

public class InstallOSUpdateProfileRegeneration extends MigrationFileRegeneration
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
            if (platformType == 1 && profileType == 3) {
                final AndroidPayloadHandler payloadHdlr = AndroidPayloadHandler.getInstance();
                InstallOSUpdateProfileRegeneration.LOGGER.log(Level.INFO, "Android Install OS Update profile regeneration Collection Id : {0}", new Object[] { collectionId });
                final AndroidCommandPayload commandPayload = payloadHdlr.createScheduleOSUpdateCommand(collectionId);
                final String commandUUID = "OsUpdatePolicy;Collection=" + collectionId.toString();
                commandPayload.setCommandUUID(commandUUID, false);
                payloadData = commandPayload.toString();
            }
        }
        return payloadData;
    }
}
