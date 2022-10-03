package javapns.notification.management;

import org.json.JSONException;
import org.json.JSONObject;

class CalendarSubscriptionPayload extends MobileConfigPayload
{
    public CalendarSubscriptionPayload(final int payloadVersion, final String payloadOrganization, final String payloadIdentifier, final String payloadDisplayName, final String subCalAccountHostName, final boolean subCalAccountUseSSL) throws JSONException {
        super(payloadVersion, "com.apple.caldav.account", payloadOrganization, payloadIdentifier, payloadDisplayName);
        final JSONObject payload = this.getPayload();
        payload.put("SubCalAccountHostName", (Object)subCalAccountHostName);
        payload.put("SubCalAccountUseSSL", subCalAccountUseSSL);
    }
    
    public void setSubCalAccountDescription(final String value) throws JSONException {
        this.getPayload().put("SubCalAccountDescription", (Object)value);
    }
    
    public void setSubCalAccountUsername(final String value) throws JSONException {
        this.getPayload().put("SubCalAccountUsername", (Object)value);
    }
    
    public void setSubCalAccountPassword(final String value) throws JSONException {
        this.getPayload().put("SubCalAccountPassword", (Object)value);
    }
    
    public void setSubCalAccountUseSSL(final boolean value) throws JSONException {
        this.getPayload().put("SubCalAccountUseSSL", value);
    }
}
