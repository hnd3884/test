package com.adventnet.sym.server.mdm.chrome.payload;

import org.json.JSONObject;
import org.json.JSONException;

public class ChromeEthernetPayload extends ChromeONCPayload
{
    public ChromeEthernetPayload() {
    }
    
    public ChromeEthernetPayload(final String payloadVersion, final String payloadIdentifier, final String payloadDisplayName) throws JSONException {
        super(payloadVersion, "Ethernet", payloadIdentifier, payloadDisplayName);
    }
    
    public void setType(final int type) throws JSONException {
        this.getONCPayloadObject().put("Authentication", type);
    }
    
    public void setAuthProtocol(final String authProtocol) throws JSONException {
        this.getEAPSettings().put("Outer", (Object)authProtocol);
    }
    
    public void setInnerProtocol(final String innerProtocol) throws JSONException {
        this.getEAPSettings().put("Inner", (Object)innerProtocol);
    }
    
    public void setouterIdentity(final String anonumousIdentity) throws JSONException {
        this.getEAPSettings().put("AnonymousIdentity", (Object)anonumousIdentity);
    }
    
    public void setIdentity(final String identity) throws JSONException {
        this.getEAPSettings().put("Identity", (Object)identity);
    }
    
    public void setEnterprisePassword(final String password) throws JSONException {
        this.getEAPSettings().put("Password", (Object)password);
    }
    
    public void initEAPSettings() throws JSONException {
        this.getONCPayloadObject().put("EAP", (Object)new JSONObject());
    }
    
    public JSONObject getEAPSettings() throws JSONException {
        return this.getONCPayloadObject().getJSONObject("EAP");
    }
    
    @Override
    public void setCertificate(String certficateID, final boolean isGUID) throws JSONException {
        if (isGUID) {
            certficateID = "[" + certficateID + "]";
        }
        if (!"-1".equals(certficateID)) {
            this.getEAPSettings().put("ServerCARefs", (Object)certficateID);
        }
    }
    
    @Override
    public String getCertificateID() {
        return this.getEAPSettings().optString("ServerCARefs", "-1");
    }
}
