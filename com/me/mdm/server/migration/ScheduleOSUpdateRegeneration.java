package com.me.mdm.server.migration;

import com.adventnet.sym.server.mdm.ios.payload.IOSCommandPayload;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.ios.payload.PayloadHandler;
import java.util.regex.Matcher;

public class ScheduleOSUpdateRegeneration extends MigrationFileRegeneration
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
                final PayloadHandler payloadHdlr = PayloadHandler.getInstance();
                ScheduleOSUpdateRegeneration.LOGGER.log(Level.INFO, "IOS Schedule OS Update profile regeneration Collection Id : {0}", new Object[] { collectionId });
                final IOSCommandPayload commandPayload = payloadHdlr.createScheduleOSUpdateCommand(collectionId);
                final String commandUUID = "ScheduleOSUpdate;Collection=" + collectionId.toString();
                commandPayload.setCommandUUID(commandUUID, false);
                payloadData = commandPayload.toString();
            }
        }
        return payloadData;
    }
}
