package com.adventnet.sym.server.mdm.android.payload;

import org.json.JSONException;

public class AndroidDataLevelPayload extends AndroidPayload
{
    public AndroidDataLevelPayload(final String payloadVersion, final String payloadIdentifier, final String payloadDisplayName) throws JSONException {
        super(payloadVersion, "DataUsageLevel", payloadIdentifier, payloadDisplayName);
    }
    
    public void setMaxLevel(final Long data, final Integer unit) throws Exception {
        this.getPayloadJSON().put("max_level", (Object)data);
        this.getPayloadJSON().put("unit", (Object)unit);
    }
    
    public void setTrackingSSID(final String ssid, final int type) throws Exception {
        this.getPayloadJSON().put("ssid", (Object)ssid);
        this.getPayloadJSON().put("type", type);
    }
}
