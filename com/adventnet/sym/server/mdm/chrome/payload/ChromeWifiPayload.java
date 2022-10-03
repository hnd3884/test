package com.adventnet.sym.server.mdm.chrome.payload;

import org.json.JSONObject;
import org.json.JSONException;

public class ChromeWifiPayload extends ChromeONCPayload
{
    public ChromeWifiPayload() {
    }
    
    public ChromeWifiPayload(final String payloadVersion, final String payloadIdentifier, final String payloadDisplayName) throws JSONException {
        super(payloadVersion, "WiFi", payloadIdentifier, payloadDisplayName);
    }
    
    public void setSSID(final String ssid) throws JSONException {
        this.getONCPayloadObject().put("SSID", (Object)ssid);
    }
    
    public void setAutoJoin(final boolean autoJoin) throws JSONException {
        this.getONCPayloadObject().put("AutoConnect", autoJoin);
    }
    
    public void setHiddenNetwork(final boolean hiddenNetwork) throws JSONException {
        this.getONCPayloadObject().put("HiddenSSID", hiddenNetwork);
    }
    
    public void setSecurityType(final String wifiType) throws JSONException {
        this.getONCPayloadObject().put("Security", (Object)wifiType);
    }
    
    public void setPassword(final String password) throws JSONException {
        this.getONCPayloadObject().put("Passphrase", (Object)password);
    }
    
    public void setAuthProtocol(final String authProtocol) throws JSONException {
        this.getEAPSettings().put("Outer", (Object)authProtocol);
    }
    
    public void setInnerProtocol(final String innerProtocol) throws JSONException {
        this.getEAPSettings().put("Inner", (Object)innerProtocol);
    }
    
    public void setouterIdentity(final String anonumousIdentity) throws JSONException {
        if (!anonumousIdentity.isEmpty()) {
            this.getEAPSettings().put("AnonymousIdentity", (Object)anonumousIdentity);
        }
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
    public String getCertificateID() throws JSONException {
        final String type = String.valueOf(this.getONCPayloadObject().get("Security"));
        if (type.equals("WEP-8021X") || type.equals("WPA-EAP")) {
            return this.getEAPSettings().optString("ServerCARefs", "-1");
        }
        return "-1";
    }
    
    public void setUseSystemCAs(final boolean useSystemCAs) throws JSONException {
        this.getPayloadDataJSON().put("UseSystemCAs", useSystemCAs);
    }
}
