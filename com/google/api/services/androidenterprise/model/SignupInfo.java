package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class SignupInfo extends GenericJson
{
    @Key
    private String completionToken;
    @Key
    private String kind;
    @Key
    private String url;
    
    public String getCompletionToken() {
        return this.completionToken;
    }
    
    public SignupInfo setCompletionToken(final String completionToken) {
        this.completionToken = completionToken;
        return this;
    }
    
    public String getKind() {
        return this.kind;
    }
    
    public SignupInfo setKind(final String kind) {
        this.kind = kind;
        return this;
    }
    
    public String getUrl() {
        return this.url;
    }
    
    public SignupInfo setUrl(final String url) {
        this.url = url;
        return this;
    }
    
    public SignupInfo set(final String fieldName, final Object value) {
        return (SignupInfo)super.set(fieldName, value);
    }
    
    public SignupInfo clone() {
        return (SignupInfo)super.clone();
    }
}
