package javapns.notification.management;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.Map;

class APNPayload extends MobileConfigPayload
{
    public APNPayload(final int payloadVersion, final String payloadOrganization, final String payloadIdentifier, final String payloadDisplayName, final Map<String, String> defaultsData, final String defaultsDomainName, final Map<String, String>[] apns, final String apn, final String username) throws JSONException {
        super(payloadVersion, "com.apple.apn.managed", payloadOrganization, payloadIdentifier, payloadDisplayName);
        final JSONObject payload = this.getPayload();
        payload.put("DefaultsData", (Map)defaultsData);
        payload.put("defaultsDomainName", (Object)defaultsDomainName);
        for (final Map<String, String> apnsEntry : apns) {
            payload.put("apns", (Map)apnsEntry);
        }
        payload.put("apn", (Object)apn);
        payload.put("username", (Object)username);
    }
    
    public void setPassword(final APNPayload value) throws JSONException {
        this.getPayload().put("password", (Object)value);
    }
    
    public void setProxy(final String value) throws JSONException {
        this.getPayload().put("proxy", (Object)value);
    }
    
    public void setProxyPort(final int value) throws JSONException {
        this.getPayload().put("proxyPort", value);
    }
}
