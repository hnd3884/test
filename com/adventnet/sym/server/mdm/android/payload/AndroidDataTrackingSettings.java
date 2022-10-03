package com.adventnet.sym.server.mdm.android.payload;

import org.json.JSONException;

public class AndroidDataTrackingSettings extends AndroidPayload
{
    public AndroidDataTrackingSettings(final String payloadVersion, final String payloadIdentifier, final String payloadDisplayName) throws JSONException {
        super(payloadVersion, "DataUsageTracking", payloadIdentifier, payloadDisplayName);
    }
    
    public void setTrackingLevel(final int trackingLevel) throws Exception {
        this.getPayloadJSON().put("TrackingLevel", trackingLevel);
    }
    
    public void setRoaming(final boolean roaming) throws Exception {
        this.getPayloadJSON().put("RoamingEnabled", roaming);
    }
    
    public void setBillingCycle(final int cycle) throws Exception {
        this.getPayloadJSON().put("BillingCycle", cycle);
    }
    
    public void setTrackingSSID(final String ssid, final int type) throws Exception {
        this.getPayloadJSON().put("SSID", (Object)ssid);
        this.getPayloadJSON().put("Type", type);
    }
    
    public void setReportingFrequency(final Long frequency) throws Exception {
        this.getPayloadJSON().put("ReportingFrequency", (Object)frequency);
    }
}
