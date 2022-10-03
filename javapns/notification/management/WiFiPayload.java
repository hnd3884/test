package javapns.notification.management;

import org.json.JSONException;
import org.json.JSONObject;

class WiFiPayload extends MobileConfigPayload
{
    public WiFiPayload(final int payloadVersion, final String payloadOrganization, final String payloadIdentifier, final String payloadDisplayName, final String SSID_STR, final boolean hiddenNetwork, final String encryptionType) throws JSONException {
        super(payloadVersion, "com.apple.wifi.managed", payloadOrganization, payloadIdentifier, payloadDisplayName);
        final JSONObject payload = this.getPayload();
        payload.put("SSID_STR", (Object)SSID_STR);
        payload.put("HIDDEN_NETWORK", hiddenNetwork);
        payload.put("EncryptionType", (Object)encryptionType);
    }
    
    public void setPassword(final String value) throws JSONException {
        this.getPayload().put("Password", (Object)value);
    }
    
    public JSONObject addEAPClientConfiguration() throws JSONException {
        final JSONObject object = new JSONObject();
        this.getPayload().put("EAPClientConfiguration", (Object)object);
        return object;
    }
}
