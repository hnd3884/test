package com.adventnet.sym.server.mdm.android.payload.transform;

import java.util.Iterator;
import java.util.logging.Level;
import org.json.JSONObject;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.android.payload.AndroidDataTrackingAction;
import com.adventnet.sym.server.mdm.android.payload.AndroidPayload;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class DO2AndroidDataTrackingAction implements DO2AndroidPayload
{
    public Logger logger;
    
    public DO2AndroidDataTrackingAction() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    @Override
    public AndroidPayload createPayload(final DataObject dataObject) {
        AndroidDataTrackingAction payload = null;
        try {
            if (dataObject != null) {
                final Iterator iterator = dataObject.getRows("DataUsageActions");
                while (iterator.hasNext()) {
                    payload = new AndroidDataTrackingAction("1.0", "com.mdm.mobiledevice.dataaction", "DataUsage Action");
                    final Row payloadRow = iterator.next();
                    final Long usageUpperBound = (Long)payloadRow.get("USAGE_UPPER_BOUND");
                    final Long usageLowerBound = (Long)payloadRow.get("USAGE_LOWER_BOUND");
                    final Integer precedence = (Integer)payloadRow.get("PRECEDENCE");
                    final Boolean stopUsage = (Boolean)payloadRow.get("STOP_DATA_USAGE");
                    final Boolean onlyManged = (Boolean)payloadRow.get("RESTRICT_TO_MANAGED_APPS");
                    final Row ssidRow = dataObject.getRow("DataTrackingSSID", new Criteria(Column.getColumn("DataTrackingSSID", "SSID_TRACKING_ID"), payloadRow.get("SSID_TRACKING_ID"), 0));
                    final String ssid = (String)ssidRow.get("SSID");
                    final int type = (int)ssidRow.get("TYPE");
                    payload.setUsageBoundries(usageUpperBound, usageLowerBound);
                    payload.setPrecedence(precedence);
                    final JSONObject jsonObject = new JSONObject();
                    jsonObject.put("stop_data_usage", (Object)stopUsage);
                    jsonObject.put("restrict_to_managed_apps", (Object)onlyManged);
                    payload.setUsageRestrictions(jsonObject);
                    payload.setTrackingSSID(ssid, type);
                }
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "failed to save data usage actions payload", exp);
        }
        return payload;
    }
}
