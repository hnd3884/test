package com.adventnet.sym.server.mdm.android.payload.transform;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.android.payload.AndroidAgentMigrationPayload;
import org.json.JSONObject;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.android.payload.AndroidPayload;
import com.adventnet.persistence.DataObject;

public class DO2AndroidAgentMigrationPayload implements DO2AndroidPayload
{
    @Override
    public AndroidPayload createPayload(final DataObject dataObject) {
        AndroidAgentMigrationPayload agentMigrationPayload = null;
        try {
            final Iterator iterator = dataObject.getRows("AgentMigrationDetails");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final Long configdataItem = (Long)row.get("CONFIG_DATA_ITEM_ID");
                final String serverDetails = (String)row.get("MIGRATION_DETAILS");
                final Boolean isDataCenterMigration = Boolean.parseBoolean(String.valueOf(row.get("DATACENTER_MIGRATION")));
                final JSONObject serverJson = new JSONObject(serverDetails);
                final JSONObject migrateJsonObj = new JSONObject();
                migrateJsonObj.put("ServerData", (Object)serverJson);
                migrateJsonObj.put("RemoveProfile", false);
                if (isDataCenterMigration) {
                    migrateJsonObj.put("DatacenterMigration", (Object)isDataCenterMigration);
                }
                agentMigrationPayload = new AndroidAgentMigrationPayload("1.0", "New Agent Migration", "New Agent Migration");
                agentMigrationPayload.setMigrationData(migrateJsonObj);
                agentMigrationPayload.setPayloadUUID(configdataItem);
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(DO2AndroidAgentMigrationPayload.class.getName()).log(Level.SEVERE, "Exception in createPayload", ex);
        }
        return agentMigrationPayload;
    }
}
