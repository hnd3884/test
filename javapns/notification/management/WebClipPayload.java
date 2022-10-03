package javapns.notification.management;

import org.json.JSONException;
import org.json.JSONObject;

class WebClipPayload extends MobileConfigPayload
{
    public WebClipPayload(final int payloadVersion, final String payloadOrganization, final String payloadIdentifier, final String payloadDisplayName, final String url, final String label) throws JSONException {
        super(payloadVersion, "com.apple.webClip.managed", payloadOrganization, payloadIdentifier, payloadDisplayName);
        final JSONObject payload = this.getPayload();
        payload.put("URL", (Object)url);
        payload.put("Label", (Object)label);
    }
    
    public void setIcon(final Object data) throws JSONException {
        this.getPayload().put("Icon", data);
    }
    
    public void setIsRemovable(final boolean value) throws JSONException {
        this.getPayload().put("IsRemovable", value);
    }
}
