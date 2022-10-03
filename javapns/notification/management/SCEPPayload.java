package javapns.notification.management;

import java.util.List;
import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;

class SCEPPayload extends MobileConfigPayload
{
    public SCEPPayload(final int payloadVersion, final String payloadOrganization, final String payloadIdentifier, final String payloadDisplayName, final String url) throws JSONException {
        super(payloadVersion, "com.apple.encrypted-profile-service", payloadOrganization, payloadIdentifier, payloadDisplayName);
        final JSONObject payload = this.getPayload();
        payload.put("URL", (Object)url);
    }
    
    public void setName(final String value) throws JSONException {
        this.getPayload().put("Name", (Object)value);
    }
    
    public void setSubject(final String value) throws JSONException {
        final String[] parts = value.split("/");
        final List<String[]> list = new ArrayList<String[]>();
        for (final String part : parts) {
            final String[] subparts = value.split("=");
            list.add(subparts);
        }
        final String[][] subject = list.toArray(new String[0][0]);
        this.setSubject(subject);
    }
    
    private void setSubject(final String[][] value) throws JSONException {
        this.getPayload().put("Subject", (Object)value);
    }
    
    public void setChallenge(final String value) throws JSONException {
        this.getPayload().put("Challenge", (Object)value);
    }
    
    public void setKeysize(final int value) throws JSONException {
        this.getPayload().put("Keysize", value);
    }
    
    public void setKeyType(final String value) throws JSONException {
        this.getPayload().put("Key Type", (Object)value);
    }
    
    public void setKeyUsage(final int value) throws JSONException {
        this.getPayload().put("Key Usage", value);
    }
    
    public JSONObject addSubjectAltName() throws JSONException {
        final JSONObject object = new JSONObject();
        this.getPayload().put("SubjectAltName", (Object)object);
        return object;
    }
    
    public JSONObject addGetCACaps() throws JSONException {
        final JSONObject object = new JSONObject();
        this.getPayload().put("GetCACaps", (Object)object);
        return object;
    }
}
