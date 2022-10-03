package com.adventnet.sym.server.mdm.android.payload;

import org.json.JSONArray;
import org.json.JSONException;

public class AndroidConfigurationPayload extends AndroidBaseConfigPayload
{
    public AndroidConfigurationPayload(final int payloadVersion, final String payloadIdentifier, final String payloadDisplayName) throws JSONException {
        super(payloadVersion, "Configuration", payloadIdentifier, payloadDisplayName);
    }
    
    public void setPayloadDescription(final String description) throws JSONException {
        this.getPayloadJSON().put("PayloadDescription", (Object)description);
    }
    
    public void setPayloadContent(final JSONArray arrayJson) throws JSONException {
        this.getPayloadJSON().put("PayloadContent", (Object)arrayJson);
    }
}
