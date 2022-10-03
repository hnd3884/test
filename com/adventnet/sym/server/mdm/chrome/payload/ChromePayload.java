package com.adventnet.sym.server.mdm.chrome.payload;

import org.json.JSONException;
import com.adventnet.sym.server.mdm.android.payload.AndroidPayload;

public abstract class ChromePayload extends AndroidPayload
{
    public ChromePayload() {
    }
    
    public ChromePayload(final String payloadVersion, final String payloadType, final String payloadIdentifier, final String payloadDisplayName) throws JSONException {
        super(payloadVersion, payloadType, payloadIdentifier, payloadDisplayName);
    }
    
    public String getCertificateID() throws JSONException {
        return "-1";
    }
    
    public void setCertificate(final String certID, final boolean isGUID) throws JSONException {
    }
    
    @Override
    public void setPayloadIdentifier(final String payloadIdentifier) throws JSONException {
        super.setPayloadIdentifier(payloadIdentifier);
        final String payloadType = String.valueOf(this.getPayloadJSON().get("PayloadType"));
        if (this.isONCPayload(payloadType)) {
            this.getPayloadJSON().getJSONObject("PayloadData").put("GUID", (Object)payloadIdentifier);
            this.getPayloadJSON().getJSONObject("PayloadData").put("Name", (Object)(this.getPayloadJSON().get("PayloadDisplayName") + "." + this.getPayloadJSON().get("PayloadType")));
        }
    }
    
    private boolean isONCPayload(final String payloadType) {
        return payloadType != null && (payloadType.equalsIgnoreCase("Wifi") || payloadType.equalsIgnoreCase("Ethernet") || payloadType.equalsIgnoreCase("VPN"));
    }
}
