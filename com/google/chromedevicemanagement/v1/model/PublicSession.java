package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class PublicSession extends GenericJson
{
    @Key
    private Boolean publicSessionEnabled;
    @Key
    private UserPolicy userPolicy;
    
    public Boolean getPublicSessionEnabled() {
        return this.publicSessionEnabled;
    }
    
    public PublicSession setPublicSessionEnabled(final Boolean publicSessionEnabled) {
        this.publicSessionEnabled = publicSessionEnabled;
        return this;
    }
    
    public UserPolicy getUserPolicy() {
        return this.userPolicy;
    }
    
    public PublicSession setUserPolicy(final UserPolicy userPolicy) {
        this.userPolicy = userPolicy;
        return this;
    }
    
    public PublicSession set(final String s, final Object o) {
        return (PublicSession)super.set(s, o);
    }
    
    public PublicSession clone() {
        return (PublicSession)super.clone();
    }
}
