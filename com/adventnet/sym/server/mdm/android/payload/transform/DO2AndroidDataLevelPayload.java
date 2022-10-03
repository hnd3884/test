package com.adventnet.sym.server.mdm.android.payload.transform;

import java.util.Iterator;
import java.util.logging.Level;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.android.payload.AndroidDataLevelPayload;
import com.adventnet.sym.server.mdm.android.payload.AndroidPayload;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class DO2AndroidDataLevelPayload implements DO2AndroidPayload
{
    public Logger logger;
    
    public DO2AndroidDataLevelPayload() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    @Override
    public AndroidPayload createPayload(final DataObject dataObject) {
        AndroidDataLevelPayload payload = null;
        try {
            if (dataObject != null) {
                final Iterator iterator = dataObject.getRows("DataUsageLevels");
                while (iterator.hasNext()) {
                    payload = new AndroidDataLevelPayload("1.0", "com.mdm.mobiledevice.datalevel", "DataUsage Level");
                    final Row payloadRow = iterator.next();
                    final Long data = (Long)payloadRow.get("MAX_DATA");
                    final Integer unit = (Integer)payloadRow.get("UNIT");
                    final Row ssidRow = dataObject.getRow("DataTrackingSSID", new Criteria(Column.getColumn("DataTrackingSSID", "SSID_TRACKING_ID"), payloadRow.get("SSID_TRACKING_ID"), 0));
                    final String ssid = (String)ssidRow.get("SSID");
                    final int type = (int)ssidRow.get("TYPE");
                    payload.setMaxLevel(data, unit);
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
