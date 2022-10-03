package com.adventnet.sym.server.mdm.ios.payload;

public class PasscodePolicyPayload extends IOSPayload
{
    public PasscodePolicyPayload(final int payloadVersion, final String payloadOrganization, final String payloadIdentifier, final String payloadDisplayName) {
        super(payloadVersion, "com.apple.mobiledevice.passwordpolicy", payloadOrganization, payloadIdentifier, payloadDisplayName);
    }
    
    public void setAllowSimple(final boolean value) {
        this.getPayloadDict().put("allowSimple", (Object)value);
    }
    
    public void setForcePIN(final boolean value) {
        this.getPayloadDict().put("forcePIN", (Object)value);
    }
    
    public void setMaxFailedAttempts(final int value) {
        this.getPayloadDict().put("maxFailedAttempts", (Object)value);
    }
    
    public void setMaxInactivity(final int value) {
        this.getPayloadDict().put("maxInactivity", (Object)value);
    }
    
    public void setMaxPINAgeInDays(final int value) {
        this.getPayloadDict().put("maxPINAgeInDays", (Object)value);
    }
    
    public void setMinComplexChars(final int value) {
        this.getPayloadDict().put("minComplexChars", (Object)value);
    }
    
    public void setMinLength(final int value) {
        this.getPayloadDict().put("minLength", (Object)value);
    }
    
    public void setRequireAlphanumeric(final boolean value) {
        this.getPayloadDict().put("requireAlphanumeric", (Object)value);
    }
    
    public void setPinHistory(final int value) {
        this.getPayloadDict().put("pinHistory", (Object)value);
    }
    
    public void setManualFetchingWhenRoaming(final boolean value) {
        this.getPayloadDict().put("manualFetchingWhenRoaming", (Object)value);
    }
    
    public void setMaxGracePeriod(final int value) {
        this.getPayloadDict().put("maxGracePeriod", (Object)value);
    }
    
    public void setChangeAtNextAuth(final boolean value) {
        this.getPayloadDict().put("changeAtNextAuth", (Object)value);
    }
    
    public void setMinutesUntilFailedLoginReset(final int value) {
        this.getPayloadDict().put("minutesUntilFailedLoginReset", (Object)value);
    }
}
