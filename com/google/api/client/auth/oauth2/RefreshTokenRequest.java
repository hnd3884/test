package com.google.api.client.auth.oauth2;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Preconditions;
import com.google.api.client.http.HttpExecuteInterceptor;
import java.util.Collection;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.util.Key;

public class RefreshTokenRequest extends TokenRequest
{
    @Key("refresh_token")
    private String refreshToken;
    
    public RefreshTokenRequest(final HttpTransport transport, final JsonFactory jsonFactory, final GenericUrl tokenServerUrl, final String refreshToken) {
        super(transport, jsonFactory, tokenServerUrl, "refresh_token");
        this.setRefreshToken(refreshToken);
    }
    
    @Override
    public RefreshTokenRequest setRequestInitializer(final HttpRequestInitializer requestInitializer) {
        return (RefreshTokenRequest)super.setRequestInitializer(requestInitializer);
    }
    
    @Override
    public RefreshTokenRequest setTokenServerUrl(final GenericUrl tokenServerUrl) {
        return (RefreshTokenRequest)super.setTokenServerUrl(tokenServerUrl);
    }
    
    @Override
    public RefreshTokenRequest setScopes(final Collection<String> scopes) {
        return (RefreshTokenRequest)super.setScopes(scopes);
    }
    
    @Override
    public RefreshTokenRequest setGrantType(final String grantType) {
        return (RefreshTokenRequest)super.setGrantType(grantType);
    }
    
    @Override
    public RefreshTokenRequest setClientAuthentication(final HttpExecuteInterceptor clientAuthentication) {
        return (RefreshTokenRequest)super.setClientAuthentication(clientAuthentication);
    }
    
    @Override
    public RefreshTokenRequest setResponseClass(final Class<? extends TokenResponse> responseClass) {
        return (RefreshTokenRequest)super.setResponseClass(responseClass);
    }
    
    public final String getRefreshToken() {
        return this.refreshToken;
    }
    
    public RefreshTokenRequest setRefreshToken(final String refreshToken) {
        this.refreshToken = (String)Preconditions.checkNotNull((Object)refreshToken);
        return this;
    }
    
    @Override
    public RefreshTokenRequest set(final String fieldName, final Object value) {
        return (RefreshTokenRequest)super.set(fieldName, value);
    }
}
