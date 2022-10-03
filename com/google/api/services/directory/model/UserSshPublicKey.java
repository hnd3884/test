package com.google.api.services.directory.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.json.JsonString;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class UserSshPublicKey extends GenericJson
{
    @Key
    @JsonString
    private Long expirationTimeUsec;
    @Key
    private String fingerprint;
    @Key
    private String key;
    
    public Long getExpirationTimeUsec() {
        return this.expirationTimeUsec;
    }
    
    public UserSshPublicKey setExpirationTimeUsec(final Long expirationTimeUsec) {
        this.expirationTimeUsec = expirationTimeUsec;
        return this;
    }
    
    public String getFingerprint() {
        return this.fingerprint;
    }
    
    public UserSshPublicKey setFingerprint(final String fingerprint) {
        this.fingerprint = fingerprint;
        return this;
    }
    
    public String getKey() {
        return this.key;
    }
    
    public UserSshPublicKey setKey(final String key) {
        this.key = key;
        return this;
    }
    
    public UserSshPublicKey set(final String fieldName, final Object value) {
        return (UserSshPublicKey)super.set(fieldName, value);
    }
    
    public UserSshPublicKey clone() {
        return (UserSshPublicKey)super.clone();
    }
}
