package com.me.mdm.server.migration;

import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import java.util.regex.Matcher;

public class UpdateProfileRegeneration extends MigrationFileRegeneration
{
    @Override
    public String generateFile(final Matcher updateAppMatcher) throws Exception {
        String payloadData = null;
        final Long collectionId = Long.parseLong(updateAppMatcher.group(2));
        final DataObject dataObject = this.getProfileDetailsQuery(collectionId);
        if (dataObject != null) {
            final Row profileRow = dataObject.getFirstRow("Profile");
            final Integer platformType = (Integer)profileRow.get("PLATFORM_TYPE");
            final Integer profileType = (Integer)profileRow.get("PROFILE_TYPE");
            UpdateProfileRegeneration.LOGGER.log(Level.INFO, "Update_App profile regeneration Collection Id : {0} Platform Type: {1} profile type: {2}", new Object[] { collectionId, platformType });
            if (platformType == 3 && profileType == 2) {
                payloadData = this.getWindowsAppPayloads(collectionId, "UpdateApplication");
            }
        }
        return payloadData;
    }
}
