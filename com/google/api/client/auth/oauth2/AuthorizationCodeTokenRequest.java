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

public class AuthorizationCodeTokenRequest extends TokenRequest
{
    @Key
    private String code;
    @Key("redirect_uri")
    private String redirectUri;
    
    public AuthorizationCodeTokenRequest(final HttpTransport transport, final JsonFactory jsonFactory, final GenericUrl tokenServerUrl, final String code) {
        super(transport, jsonFactory, tokenServerUrl, "authorization_code");
        this.setCode(code);
    }
    
    @Override
    public AuthorizationCodeTokenRequest setRequestInitializer(final HttpRequestInitializer requestInitializer) {
        return (AuthorizationCodeTokenRequest)super.setRequestInitializer(requestInitializer);
    }
    
    @Override
    public AuthorizationCodeTokenRequest setTokenServerUrl(final GenericUrl tokenServerUrl) {
        return (AuthorizationCodeTokenRequest)super.setTokenServerUrl(tokenServerUrl);
    }
    
    @Override
    public AuthorizationCodeTokenRequest setScopes(final Collection<String> scopes) {
        return (AuthorizationCodeTokenRequest)super.setScopes(scopes);
    }
    
    @Override
    public AuthorizationCodeTokenRequest setGrantType(final String grantType) {
        return (AuthorizationCodeTokenRequest)super.setGrantType(grantType);
    }
    
    @Override
    public AuthorizationCodeTokenRequest setClientAuthentication(final HttpExecuteInterceptor clientAuthentication) {
        return (AuthorizationCodeTokenRequest)super.setClientAuthentication(clientAuthentication);
    }
    
    @Override
    public AuthorizationCodeTokenRequest setResponseClass(final Class<? extends TokenResponse> responseClass) {
        return (AuthorizationCodeTokenRequest)super.setResponseClass(responseClass);
    }
    
    public final String getCode() {
        return this.code;
    }
    
    public AuthorizationCodeTokenRequest setCode(final String code) {
        this.code = (String)Preconditions.checkNotNull((Object)code);
        return this;
    }
    
    public final String getRedirectUri() {
        return this.redirectUri;
    }
    
    public AuthorizationCodeTokenRequest setRedirectUri(final String redirectUri) {
        this.redirectUri = redirectUri;
        return this;
    }
    
    @Override
    public AuthorizationCodeTokenRequest set(final String fieldName, final Object value) {
        return (AuthorizationCodeTokenRequest)super.set(fieldName, value);
    }
}
