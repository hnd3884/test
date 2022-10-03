package com.me.mdm.server.updates.osupdates.ios;

import com.adventnet.persistence.Row;
import org.json.JSONObject;
import com.adventnet.ds.query.Join;
import com.me.mdm.server.updates.osupdates.ExtendedOSDetailsDataHandler;

public class IOSUpdatesDetailsHandler extends ExtendedOSDetailsDataHandler
{
    @Override
    protected Join getOSUpdateDetailsExtnJoin() {
        return new Join("OSUpdates", "IOSUpdates", new String[] { "UPDATE_ID" }, new String[] { "UPDATE_ID" }, 2);
    }
    
    @Override
    protected Row getExtnOSDetailsNewRow(final JSONObject updateDetails) throws Exception {
        final JSONObject iosDataJson = updateDetails.getJSONObject("IOSUpdates");
        final Row iosDetailsRow = new Row("IOSUpdates");
        iosDetailsRow.set("PRODUCT_KEY", (Object)String.valueOf(iosDataJson.get("PRODUCT_KEY")));
        iosDetailsRow.set("BUILD", (Object)String.valueOf(iosDataJson.get("BUILD")));
        String humanReadableName = iosDataJson.optString("HUMAN_READABLE_NAME", (String)null);
        if (humanReadableName == null) {
            humanReadableName = "Version ".concat(String.valueOf(updateDetails.get("VERSION"))).concat("Build ").concat(String.valueOf(iosDataJson.get("BUILD")));
        }
        iosDetailsRow.set("HUMAN_READABLE_NAME", (Object)humanReadableName);
        return iosDetailsRow;
    }
}
