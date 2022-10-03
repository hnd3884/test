package com.me.mdm.server.migration;

import com.adventnet.sym.server.mdm.android.payload.AndroidCommandPayload;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.sym.server.mdm.android.payload.AndroidPayloadHandler;
import java.util.logging.Level;
import java.util.regex.Matcher;

public class InstallDataUsageProfileRegeneration extends MigrationFileRegeneration
{
    @Override
    public String generateFile(final Matcher installDataUsageMatcher) throws Exception {
        String payloadData = null;
        final Long collectionId = Long.parseLong(installDataUsageMatcher.group(2));
        final DataObject dataObject = this.getProfileDetailsQuery(collectionId);
        if (dataObject != null) {
            final Row profileRow = dataObject.getFirstRow("Profile");
            final Integer profileType = (Integer)profileRow.get("PROFILE_TYPE");
            InstallDataUsageProfileRegeneration.LOGGER.log(Level.INFO, "Install Data usage profile regeneration Collection Id : {0} profile type: {1}", new Object[] { collectionId, profileType });
            if (profileType == 8) {
                final AndroidPayloadHandler payloadHdlr = AndroidPayloadHandler.getInstance();
                final AndroidCommandPayload commandPayload = payloadHdlr.generateProfilePayload(collectionId, "InstallDataProfile");
                payloadData = commandPayload.getPayloadJSON().toString();
            }
        }
        return payloadData;
    }
}
