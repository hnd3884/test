package javapns.notification.management;

import org.json.JSONException;

class RemovalPasswordPayload extends MobileConfigPayload
{
    public RemovalPasswordPayload(final int payloadVersion, final String payloadOrganization, final String payloadIdentifier, final String payloadDisplayName) throws JSONException {
        super(payloadVersion, "com.apple.profileRemovalPassword", payloadOrganization, payloadIdentifier, payloadDisplayName);
    }
    
    public void setRemovalPasword(final String value) throws JSONException {
        this.getPayload().put("RemovalPassword", (Object)value);
    }
}
