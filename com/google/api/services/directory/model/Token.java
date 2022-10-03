package com.google.api.services.directory.model;

import com.google.api.client.util.GenericData;
import java.util.List;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class Token extends GenericJson
{
    @Key
    private Boolean anonymous;
    @Key
    private String clientId;
    @Key
    private String displayText;
    @Key
    private String etag;
    @Key
    private String kind;
    @Key
    private Boolean nativeApp;
    @Key
    private List<String> scopes;
    @Key
    private String userKey;
    
    public Boolean getAnonymous() {
        return this.anonymous;
    }
    
    public Token setAnonymous(final Boolean anonymous) {
        this.anonymous = anonymous;
        return this;
    }
    
    public String getClientId() {
        return this.clientId;
    }
    
    public Token setClientId(final String clientId) {
        this.clientId = clientId;
        return this;
    }
    
    public String getDisplayText() {
        return this.displayText;
    }
    
    public Token setDisplayText(final String displayText) {
        this.displayText = displayText;
        return this;
    }
    
    public String getEtag() {
        return this.etag;
    }
    
    public Token setEtag(final String etag) {
        this.etag = etag;
        return this;
    }
    
    public String getKind() {
        return this.kind;
    }
    
    public Token setKind(final String kind) {
        this.kind = kind;
        return this;
    }
    
    public Boolean getNativeApp() {
        return this.nativeApp;
    }
    
    public Token setNativeApp(final Boolean nativeApp) {
        this.nativeApp = nativeApp;
        return this;
    }
    
    public List<String> getScopes() {
        return this.scopes;
    }
    
    public Token setScopes(final List<String> scopes) {
        this.scopes = scopes;
        return this;
    }
    
    public String getUserKey() {
        return this.userKey;
    }
    
    public Token setUserKey(final String userKey) {
        this.userKey = userKey;
        return this;
    }
    
    public Token set(final String fieldName, final Object value) {
        return (Token)super.set(fieldName, value);
    }
    
    public Token clone() {
        return (Token)super.clone();
    }
}
