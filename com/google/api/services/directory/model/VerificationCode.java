package com.google.api.services.directory.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class VerificationCode extends GenericJson
{
    @Key
    private String etag;
    @Key
    private String kind;
    @Key
    private String userId;
    @Key
    private String verificationCode;
    
    public String getEtag() {
        return this.etag;
    }
    
    public VerificationCode setEtag(final String etag) {
        this.etag = etag;
        return this;
    }
    
    public String getKind() {
        return this.kind;
    }
    
    public VerificationCode setKind(final String kind) {
        this.kind = kind;
        return this;
    }
    
    public String getUserId() {
        return this.userId;
    }
    
    public VerificationCode setUserId(final String userId) {
        this.userId = userId;
        return this;
    }
    
    public String getVerificationCode() {
        return this.verificationCode;
    }
    
    public VerificationCode setVerificationCode(final String verificationCode) {
        this.verificationCode = verificationCode;
        return this;
    }
    
    public VerificationCode set(final String fieldName, final Object value) {
        return (VerificationCode)super.set(fieldName, value);
    }
    
    public VerificationCode clone() {
        return (VerificationCode)super.clone();
    }
}
