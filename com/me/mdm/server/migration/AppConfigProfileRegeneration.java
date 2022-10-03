package com.me.mdm.server.migration;

import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import java.util.regex.Matcher;

public class AppConfigProfileRegeneration extends MigrationFileRegeneration
{
    @Override
    public String generateFile(final Matcher updateAppMatcher) throws Exception {
        String payloadData = null;
        final Long collectionId = Long.parseLong(updateAppMatcher.group(2));
        final DataObject dataObject = this.getProfileDetailsQuery(collectionId);
        if (dataObject != null) {
            final Row profileRow = dataObject.getFirstRow("Profile");
            final Integer platformType = (Integer)profileRow.get("PLATFORM_TYPE");
            AppConfigProfileRegeneration.LOGGER.log(Level.INFO, "App Configuration profile regeneration Collection Id : {0} Platform Type: {1}", new Object[] { collectionId, platformType });
            if (platformType == 3) {
                payloadData = this.getWindowsAppPayloads(collectionId, "ApplicationConfiguration");
            }
        }
        return payloadData;
    }
}
