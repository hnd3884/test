package com.adventnet.sym.server.mdm.android.payload.transform;

import java.util.Iterator;
import java.util.logging.Level;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.android.payload.AndroidDataTrackingSettings;
import com.adventnet.sym.server.mdm.android.payload.AndroidPayload;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class DO2AndroidTrackingPolicy implements DO2AndroidPayload
{
    public Logger logger;
    
    public DO2AndroidTrackingPolicy() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    @Override
    public AndroidPayload createPayload(final DataObject dataObject) {
        Iterator iterator = null;
        AndroidDataTrackingSettings payload = null;
        try {
            if (dataObject != null) {
                iterator = dataObject.getRows("DataTrackingPolicy");
                while (iterator.hasNext()) {
                    payload = new AndroidDataTrackingSettings("1.0", "com.mdm.mobiledevice.datatracking", "DataUsage Action");
                    final Row payloadRow = iterator.next();
                    final int trackingLevel = (int)payloadRow.get("TRACKING_LEVEL");
                    final Long frequency = (Long)payloadRow.get("REPORTING_FREQUENCY");
                    final Boolean roamingEnabled = (Boolean)payloadRow.get("ROAMING_ENABLED");
                    final int billingCycle = (int)payloadRow.get("BILLING_CYCLE");
                    final Row ssidRow = dataObject.getRow("DataTrackingSSID", new Criteria(Column.getColumn("DataTrackingSSID", "SSID_TRACKING_ID"), payloadRow.get("SSID_TRACKING_ID"), 0));
                    final String ssid = (String)ssidRow.get("SSID");
                    final int type = (int)ssidRow.get("TYPE");
                    payload.setBillingCycle(billingCycle);
                    payload.setRoaming(roamingEnabled);
                    payload.setTrackingLevel(trackingLevel);
                    payload.setTrackingSSID(ssid, type);
                    payload.setReportingFrequency(frequency);
                }
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "failed to save data usage actions payload", exp);
        }
        return payload;
    }
}
