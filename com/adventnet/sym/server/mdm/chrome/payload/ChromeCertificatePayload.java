package com.adventnet.sym.server.mdm.chrome.payload;

import org.json.JSONException;
import org.json.JSONObject;

public class ChromeCertificatePayload extends ChromePayload
{
    public ChromeCertificatePayload() {
    }
    
    public ChromeCertificatePayload(final String payloadVersion, final String payloadIdentifier, final String payloadDisplayName) throws JSONException {
        super(payloadVersion, "Certificate", payloadIdentifier, payloadDisplayName);
        this.getPayloadJSON().put("PayloadData", (Object)new JSONObject());
    }
    
    public JSONObject getPayloadDataJSON() throws JSONException {
        return this.getPayloadJSON().getJSONObject("PayloadData");
    }
    
    public void setGUID(final String guid) throws JSONException {
        this.getPayloadDataJSON().put("GUID", (Object)guid);
    }
    
    public void setType(final String type) throws JSONException {
        this.getPayloadDataJSON().put("Type", (Object)type);
    }
    
    public void setX509(final String x509) throws JSONException {
        this.getPayloadDataJSON().put("X509", (Object)x509);
    }
    
    String getCertGUID() throws JSONException {
        return String.valueOf(this.getPayloadDataJSON().get("GUID"));
    }
}
