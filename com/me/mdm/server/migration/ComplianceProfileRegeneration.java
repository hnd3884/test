package com.me.mdm.server.migration;

import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.me.mdm.server.compliance.ComplianceHandler;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.regex.Matcher;

public class ComplianceProfileRegeneration extends MigrationFileRegeneration
{
    @Override
    public String generateFile(final Matcher complianceProfileMmatcher) throws Exception {
        String payloadData = null;
        final Long customerId = Long.parseLong(complianceProfileMmatcher.group(1));
        final Long collectionId = Long.parseLong(complianceProfileMmatcher.group(2));
        final DataObject dataObject = this.getProfileDetailsQuery(collectionId);
        if (dataObject != null) {
            final Row profileRow = dataObject.getFirstRow("Profile");
            final Long profileId = (Long)profileRow.get("PROFILE_ID");
            final Integer profileType = (Integer)profileRow.get("PROFILE_TYPE");
            if (profileType == 5) {
                final JSONObject requestJSON = new JSONObject();
                requestJSON.put("collection_id", (Object)collectionId);
                requestJSON.put("compliance_id", (Object)profileId);
                requestJSON.put("customer_id", (Object)customerId);
                requestJSON.put("compliance_file_name_path", (Object)complianceProfileMmatcher.group(0));
                ComplianceProfileRegeneration.LOGGER.log(Level.INFO, "Compliance profile regeneration Collection Id : {0}", new Object[] { collectionId });
                final JSONObject complianceJSON = ComplianceHandler.getInstance().generateComplianceProfileJson(requestJSON);
                payloadData = complianceJSON.toString();
            }
        }
        return payloadData;
    }
}
