package javapns.notification.management;

import org.json.JSONObject;
import org.json.JSONException;
import javapns.notification.Payload;

abstract class MobileConfigPayload extends Payload
{
    private static long serialuuid;
    
    MobileConfigPayload(final int payloadVersion, final String payloadType, final String payloadOrganization, final String payloadIdentifier, final String payloadDisplayName) throws JSONException {
        this(payloadVersion, generateUUID(), payloadType, payloadOrganization, payloadIdentifier, payloadDisplayName);
    }
    
    private MobileConfigPayload(final int payloadVersion, final String payloadUUID, final String payloadType, final String payloadOrganization, final String payloadIdentifier, final String payloadDisplayName) throws JSONException {
        final JSONObject payload = this.getPayload();
        payload.put("PayloadVersion", payloadVersion);
        payload.put("PayloadUUID", (Object)payloadUUID);
        payload.put("PayloadType", (Object)payloadType);
        payload.put("PayloadOrganization", (Object)payloadOrganization);
        payload.put("PayloadIdentifier", (Object)payloadIdentifier);
        payload.put("PayloadDisplayName", (Object)payloadDisplayName);
    }
    
    private static String generateUUID() {
        return System.nanoTime() + "." + ++MobileConfigPayload.serialuuid;
    }
    
    public void setPayloadDescription(final String description) throws JSONException {
        this.getPayload().put("PayloadDescription", (Object)description);
    }
    
    public void setPayloadRemovalDisallowed(final boolean disallowed) throws JSONException {
        this.getPayload().put("PayloadRemovalDisallowed", disallowed);
    }
    
    static {
        MobileConfigPayload.serialuuid = 10000000L;
    }
}
