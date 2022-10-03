package javapns.notification.management;

import org.json.JSONException;

class RestrictionsPayload extends MobileConfigPayload
{
    public RestrictionsPayload(final int payloadVersion, final String payloadOrganization, final String payloadIdentifier, final String payloadDisplayName) throws JSONException {
        super(payloadVersion, "com.apple.applicationaccess", payloadOrganization, payloadIdentifier, payloadDisplayName);
    }
    
    public void setAllowAppInstallation(final boolean value) throws JSONException {
        this.getPayload().put("allowAppInstallation", value);
    }
    
    public void setAllowCamera(final boolean value) throws JSONException {
        this.getPayload().put("allowCamera", value);
    }
    
    public void setAllowExplicitContent(final boolean value) throws JSONException {
        this.getPayload().put("allowExplicitContent", value);
    }
    
    public void setAllowScreenShot(final boolean value) throws JSONException {
        this.getPayload().put("allowScreenShot", value);
    }
    
    public void setAllowYouTube(final boolean value) throws JSONException {
        this.getPayload().put("allowYouTube", value);
    }
    
    public void setAllowiTunes(final boolean value) throws JSONException {
        this.getPayload().put("allowAppInstallation", value);
    }
    
    public void setAllowSafari(final boolean value) throws JSONException {
        this.getPayload().put("allowSafari", value);
    }
}
