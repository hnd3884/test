package com.google.api.client.auth.oauth2;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Preconditions;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public class TokenResponse extends GenericJson
{
    @Key("access_token")
    private String accessToken;
    @Key("token_type")
    private String tokenType;
    @Key("expires_in")
    private Long expiresInSeconds;
    @Key("refresh_token")
    private String refreshToken;
    @Key
    private String scope;
    
    public String getAccessToken() {
        return this.accessToken;
    }
    
    public TokenResponse setAccessToken(final String accessToken) {
        this.accessToken = (String)Preconditions.checkNotNull((Object)accessToken);
        return this;
    }
    
    public String getTokenType() {
        return this.tokenType;
    }
    
    public TokenResponse setTokenType(final String tokenType) {
        this.tokenType = (String)Preconditions.checkNotNull((Object)tokenType);
        return this;
    }
    
    public Long getExpiresInSeconds() {
        return this.expiresInSeconds;
    }
    
    public TokenResponse setExpiresInSeconds(final Long expiresInSeconds) {
        this.expiresInSeconds = expiresInSeconds;
        return this;
    }
    
    public String getRefreshToken() {
        return this.refreshToken;
    }
    
    public TokenResponse setRefreshToken(final String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }
    
    public String getScope() {
        return this.scope;
    }
    
    public TokenResponse setScope(final String scope) {
        this.scope = scope;
        return this;
    }
    
    public TokenResponse set(final String fieldName, final Object value) {
        return (TokenResponse)super.set(fieldName, value);
    }
    
    public TokenResponse clone() {
        return (TokenResponse)super.clone();
    }
}
