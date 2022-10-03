package com.adventnet.sym.server.mdm.android.payload;

import org.json.JSONObject;
import org.json.JSONException;

public class AndroidDataTrackingAction extends AndroidPayload
{
    public AndroidDataTrackingAction(final String payloadVersion, final String payloadIdentifier, final String payloadDisplayName) throws JSONException {
        super(payloadVersion, "DataUsageAction", payloadIdentifier, payloadDisplayName);
    }
    
    public void setUsageBoundries(final Long upperBound, final Long lowerBound) throws Exception {
        final JSONObject usageBounds = new JSONObject();
        if (upperBound != null && (upperBound == null || upperBound >= 0L) && lowerBound != null && (lowerBound == null || lowerBound >= 0L) && lowerBound <= upperBound) {
            usageBounds.put("UpperBound", (Object)upperBound);
            usageBounds.put("LowerBound", (Object)lowerBound);
            this.getPayloadJSON().put("UsageBounds", (Object)usageBounds);
        }
    }
    
    public void setPrecedence(final Integer precedence) throws Exception {
        this.getPayloadJSON().put("Precedence", (Object)precedence);
    }
    
    public void setUsageRestrictions(final JSONObject jsonObject) throws Exception {
        this.getPayloadJSON().put("StopUsage", jsonObject.optBoolean("stop_data_usage"));
        this.getPayloadJSON().put("AllowOnlyManagedApps", jsonObject.optBoolean("restrict_to_managed_apps"));
    }
    
    public void setTrackingSSID(final String ssid, final int type) throws Exception {
        this.getPayloadJSON().put("ssid", (Object)ssid);
        this.getPayloadJSON().put("type", type);
    }
}
