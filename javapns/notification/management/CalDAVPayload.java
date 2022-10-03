package javapns.notification.management;

import org.json.JSONException;
import org.json.JSONObject;

class CalDAVPayload extends MobileConfigPayload
{
    public CalDAVPayload(final int payloadVersion, final String payloadOrganization, final String payloadIdentifier, final String payloadDisplayName, final String calDAVHostName, final String calDAVUsername, final boolean calDAVUseSSL) throws JSONException {
        super(payloadVersion, "com.apple.caldav.account", payloadOrganization, payloadIdentifier, payloadDisplayName);
        final JSONObject payload = this.getPayload();
        payload.put("CalDAVHostName", (Object)calDAVHostName);
        payload.put("CalDAVUsername", (Object)calDAVUsername);
        payload.put("CalDAVUseSSL", calDAVUseSSL);
    }
    
    public void setCalDAVAccountDescription(final String value) throws JSONException {
        this.getPayload().put("CalDAVAccountDescription", (Object)value);
    }
    
    public void setCalDAVPassword(final String value) throws JSONException {
        this.getPayload().put("CalDAVPassword", (Object)value);
    }
    
    public void setCalDAVPort(final int value) throws JSONException {
        this.getPayload().put("CalDAVPort", value);
    }
    
    public void setCalDAVPrincipalURL(final String value) throws JSONException {
        this.getPayload().put("CalDAVPrincipalURL", (Object)value);
    }
}
