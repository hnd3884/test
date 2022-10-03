package com.adventnet.sym.server.mdm.android.payload;

import org.json.JSONException;

public class AndroidPasscodePolicyPayload extends AndroidPayload
{
    public AndroidPasscodePolicyPayload() {
    }
    
    public AndroidPasscodePolicyPayload(final String payloadVersion, final String payloadIdentifier, final String payloadDisplayName) throws JSONException {
        super(payloadVersion, "Passcode", payloadIdentifier, payloadDisplayName);
    }
    
    public void setPasscodeType(final int value) throws JSONException {
        this.getPayloadJSON().put("PasscodeType", value);
    }
    
    public void setMinLength(final int value) throws JSONException {
        this.getPayloadJSON().put("MinLength", value);
    }
    
    public void setPasscodeScope(final int value) throws JSONException {
        this.getPayloadJSON().put("PasscodePolicyScope", value);
    }
    
    public void setMaxInactivity(final int value) throws JSONException {
        this.getPayloadJSON().put("MaxInactivity", value);
    }
    
    public void setMaxFailedAttempts(final int value) throws JSONException {
        this.getPayloadJSON().put("MaxFailedAttempts", value);
    }
    
    public void setMaxAge(final int value) throws JSONException {
        this.getPayloadJSON().put("MaxAge", value);
    }
    
    public void setPinHistory(final int value) throws JSONException {
        this.getPayloadJSON().put("PasscodeHistory", value);
    }
    
    public void setMinLowerLength(final int value) throws JSONException {
        this.getPayloadJSON().put("MinLowerLength", value);
    }
    
    public void setMinNonLength(final int value) throws JSONException {
        this.getPayloadJSON().put("MinNonLength", value);
    }
    
    public void setMinNumLength(final int value) throws JSONException {
        this.getPayloadJSON().put("MinNumLength", value);
    }
    
    public void setMinUpperLength(final int value) throws JSONException {
        this.getPayloadJSON().put("MinUpperLength", value);
    }
    
    public void setMinSymbolLength(final int value) throws JSONException {
        this.getPayloadJSON().put("MinSymbolLength", value);
    }
    
    public void setMinLetterLength(final int value) throws JSONException {
        this.getPayloadJSON().put("MinLetterLength", value);
    }
    
    public void setMaximumCharacters(final int value) throws JSONException {
        this.getPayloadJSON().put("maxCharCanOccur", value);
    }
    
    public void setMaximumNumericSequence(final int value) throws JSONException {
        this.getPayloadJSON().put("maxNumSeq", value);
    }
    
    public void setMaximumGracePeriod(final int value) throws JSONException {
        this.getPayloadJSON().put("passcodeChangeTimeout", value);
    }
    
    public void setForbidenString(final String value) throws JSONException {
        this.getPayloadJSON().put("passwordForbiddenList", (Object)value);
    }
    
    public void setMaxCharSequence(final int value) throws JSONException {
        this.getPayloadJSON().put("passwordMaxCharSequence", value);
    }
    
    public void setMinChanges(final int value) throws JSONException {
        this.getPayloadJSON().put("passwordMinChanges", value);
    }
    
    public void setPasswordVisibility(final boolean value) throws JSONException {
        this.getPayloadJSON().put("passwordVisibility", value);
    }
    
    public void setAllowFingerprint(final int value) throws JSONException {
        this.getPayloadJSON().put("AllowFingerPrintAuth", value);
    }
    
    public void setAllowIrisScan(final Boolean irisScan) throws JSONException {
        this.getPayloadJSON().put("AllowIrisScan", (Object)irisScan);
    }
    
    public void setFaceUnlock(final Boolean faceUnlock) throws JSONException {
        this.getPayloadJSON().put("AllowFaceUnlock", (Object)faceUnlock);
    }
    
    public void setOneLock(final Boolean oneLock) throws JSONException {
        this.getPayloadJSON().put("UseOneLock", (Object)oneLock);
    }
    
    public void setAllowTrustAgents(final int allowTrustAgents) throws JSONException {
        this.getPayloadJSON().put("allowTrustAgents", allowTrustAgents);
    }
    
    public void setDefaultPasscode(final boolean setDefaultPasscode) throws JSONException {
        this.getPayloadJSON().put("SetPasscode", (int)(setDefaultPasscode ? 1 : 0));
    }
    
    public void allowChnageDefaultPasscode(final boolean allowChnageDefaultPasscode) throws JSONException {
        this.getPayloadJSON().put("AllowPasscodeChange", (int)(allowChnageDefaultPasscode ? 1 : 0));
    }
    
    public void setNewDefaultPasscode(final String newDefaultPasscode) throws JSONException {
        this.getPayloadJSON().put("NewPasscode", (Object)newDefaultPasscode);
    }
    
    public void setPasscodeComplexity(final Integer passcodeComplexity) throws JSONException {
        this.getPayloadJSON().put("PasscodeComplexity", (Object)passcodeComplexity);
    }
    
    public void setStrongAuthTimeout(final Integer strongAuthTimeout) throws JSONException {
        this.getPayloadJSON().put("MaxTimeForStrongAuth", strongAuthTimeout * 60);
    }
}
