package com.google.api.client.googleapis.auth.oauth2;

import com.google.api.client.util.GenericData;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.auth.oauth2.TokenRequest;
import java.io.IOException;
import java.util.Collection;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpExecuteInterceptor;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.auth.oauth2.RefreshTokenRequest;

public class GoogleRefreshTokenRequest extends RefreshTokenRequest
{
    public GoogleRefreshTokenRequest(final HttpTransport transport, final JsonFactory jsonFactory, final String refreshToken, final String clientId, final String clientSecret) {
        super(transport, jsonFactory, new GenericUrl("https://oauth2.googleapis.com/token"), refreshToken);
        this.setClientAuthentication((HttpExecuteInterceptor)new ClientParametersAuthentication(clientId, clientSecret));
    }
    
    public GoogleRefreshTokenRequest setRequestInitializer(final HttpRequestInitializer requestInitializer) {
        return (GoogleRefreshTokenRequest)super.setRequestInitializer(requestInitializer);
    }
    
    public GoogleRefreshTokenRequest setTokenServerUrl(final GenericUrl tokenServerUrl) {
        return (GoogleRefreshTokenRequest)super.setTokenServerUrl(tokenServerUrl);
    }
    
    public GoogleRefreshTokenRequest setScopes(final Collection<String> scopes) {
        return (GoogleRefreshTokenRequest)super.setScopes((Collection)scopes);
    }
    
    public GoogleRefreshTokenRequest setGrantType(final String grantType) {
        return (GoogleRefreshTokenRequest)super.setGrantType(grantType);
    }
    
    public GoogleRefreshTokenRequest setClientAuthentication(final HttpExecuteInterceptor clientAuthentication) {
        return (GoogleRefreshTokenRequest)super.setClientAuthentication(clientAuthentication);
    }
    
    public GoogleRefreshTokenRequest setRefreshToken(final String refreshToken) {
        return (GoogleRefreshTokenRequest)super.setRefreshToken(refreshToken);
    }
    
    public GoogleTokenResponse execute() throws IOException {
        return (GoogleTokenResponse)this.executeUnparsed().parseAs((Class)GoogleTokenResponse.class);
    }
    
    public GoogleRefreshTokenRequest set(final String fieldName, final Object value) {
        return (GoogleRefreshTokenRequest)super.set(fieldName, value);
    }
}
