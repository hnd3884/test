package com.adventnet.sym.server.mdm.android.payload;

import org.json.JSONObject;
import org.json.JSONException;

public class AndroidWifiPayload extends AndroidPayload
{
    public AndroidWifiPayload(final String payloadVersion, final String payloadIdentifier, final String payloadDisplayName) throws JSONException {
        super(payloadVersion, "Wifi", payloadIdentifier, payloadDisplayName);
    }
    
    public void setSSID(final String ssid) throws JSONException {
        this.getPayloadJSON().put("SSID", (Object)ssid);
    }
    
    public void setAutoJoin(final boolean autoJoin) throws JSONException {
        this.getPayloadJSON().put("AutoJoin", autoJoin);
    }
    
    public void setSecurityType(final String securityType) throws JSONException {
        this.getPayloadJSON().put("SecurityType", (Object)securityType);
    }
    
    public void setDefaultWepIndex(final int defaultWepIndex) throws JSONException {
        this.getPayloadJSON().put("DefaultWEPIndex", defaultWepIndex);
    }
    
    public void setWep(final String wep) throws JSONException {
        this.getPayloadJSON().put("WEP1", (Object)wep);
    }
    
    public void setPreSharedKey(final String psk) throws JSONException {
        this.getPayloadJSON().put("PreSharedKey", (Object)psk);
    }
    
    public void setEAPMethod(final String eapMethod) throws JSONException {
        this.getPayloadJSON().put("EAPMethod", (Object)eapMethod);
    }
    
    public void setPhase2(final String phase2Val) throws JSONException {
        this.getPayloadJSON().put("Phase2", (Object)phase2Val);
    }
    
    public void setIdentity(final String identity) throws JSONException {
        this.getPayloadJSON().put("Identity", (Object)identity);
    }
    
    public void setAnonymousIdentity(final String anonyMousidentity) throws JSONException {
        this.getPayloadJSON().put("AnonymousIdentity", (Object)anonyMousidentity);
    }
    
    public void setPassword(final String password) throws JSONException {
        this.getPayloadJSON().put("Password", (Object)password);
    }
    
    public void setCACertName(final String certName) throws JSONException {
        this.getPayloadJSON().put("CaCertName", (Object)certName);
    }
    
    public void setCACertPassword(final String certPassword) throws JSONException {
        this.getPayloadJSON().put("CaCertPassword", (Object)certPassword);
    }
    
    public void setCACertType(final String certType) throws JSONException {
        this.getPayloadJSON().put("CaCertType", (Object)certType);
    }
    
    public void setCACertContent(final String certContent) throws JSONException {
        this.getPayloadJSON().put("CaCertContent", (Object)certContent);
    }
    
    public void setCAKeyStoreType(final String keyStoreType) throws JSONException {
        this.getPayloadJSON().put("CaKeystoreType", (Object)keyStoreType);
    }
    
    public void setClientCertName(final String certName) throws JSONException {
        this.getPayloadJSON().put("ClientCertName", (Object)certName);
    }
    
    public void setClientCertType(final String certType) throws JSONException {
        this.getPayloadJSON().put("ClientCertType", (Object)certType);
    }
    
    public void setClientCertPassword(final String password) throws JSONException {
        this.getPayloadJSON().put("ClientCertPassword", (Object)password);
    }
    
    public void setClientCertContent(final String certContent) throws JSONException {
        this.getPayloadJSON().put("ClientCertContent", (Object)certContent);
    }
    
    public void setClientCertKeyStoreType(final String keyStoreType) throws JSONException {
        this.getPayloadJSON().put("ClientKeystoreType", (Object)keyStoreType);
    }
    
    public void setProxySetting(final String setting) throws JSONException {
        this.getPayloadJSON().put("ProxySettings", (Object)setting);
    }
    
    public void setProxyData(final JSONObject ipData) throws JSONException {
        this.getPayloadJSON().put("ProxySettingsData", (Object)ipData);
    }
    
    public void setIPSetting(final String setting) throws JSONException {
        this.getPayloadJSON().put("IpSettings", (Object)setting);
    }
    
    public void setIPData(final JSONObject ipData) throws JSONException {
        this.getPayloadJSON().put("IpSettingsData", (Object)ipData);
    }
    
    public void setEnrollType(final String type) throws JSONException {
        this.getPayloadJSON().put("ClientCertEnrollType", (Object)type);
    }
}
