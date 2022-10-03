package javapns.notification.management;

import org.json.JSONException;

class PasswordPolicyPayload extends MobileConfigPayload
{
    public PasswordPolicyPayload(final int payloadVersion, final String payloadOrganization, final String payloadIdentifier, final String payloadDisplayName) throws JSONException {
        super(payloadVersion, "com.apple.mobiledevice.passwordpolicy", payloadOrganization, payloadIdentifier, payloadDisplayName);
    }
    
    public void setAllowSimple(final boolean value) throws JSONException {
        this.getPayload().put("allowSimple", value);
    }
    
    public void setForcePIN(final boolean value) throws JSONException {
        this.getPayload().put("forcePIN", value);
    }
    
    public void setMaxFailedAttempts(final int value) throws JSONException {
        this.getPayload().put("maxFailedAttempts", value);
    }
    
    public void setMaxInactivity(final int value) throws JSONException {
        this.getPayload().put("maxInactivity", value);
    }
    
    public void setMaxPINAgeInDays(final int value) throws JSONException {
        this.getPayload().put("maxPINAgeInDays", value);
    }
    
    public void setMinComplexChars(final int value) throws JSONException {
        this.getPayload().put("minComplexChars", value);
    }
    
    public void setMinLength(final int value) throws JSONException {
        this.getPayload().put("minLength", value);
    }
    
    public void setRequireAlphanumeric(final boolean value) throws JSONException {
        this.getPayload().put("requireAlphanumeric", value);
    }
    
    public void setPinHistory(final int value) throws JSONException {
        this.getPayload().put("pinHistory", value);
    }
    
    public void setManualFetchingWhenRoaming(final boolean value) throws JSONException {
        this.getPayload().put("manualFetchingWhenRoaming", value);
    }
    
    public void setMaxGracePeriod(final int value) throws JSONException {
        this.getPayload().put("maxGracePeriod", value);
    }
}
