package com.google.api.client.googleapis.auth.oauth2;

import com.google.api.client.util.GenericData;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.auth.oauth2.TokenRequest;
import java.io.IOException;
import com.google.api.client.util.Preconditions;
import java.util.Collection;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpExecuteInterceptor;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.auth.oauth2.AuthorizationCodeTokenRequest;

public class GoogleAuthorizationCodeTokenRequest extends AuthorizationCodeTokenRequest
{
    public GoogleAuthorizationCodeTokenRequest(final HttpTransport transport, final JsonFactory jsonFactory, final String clientId, final String clientSecret, final String code, final String redirectUri) {
        this(transport, jsonFactory, "https://oauth2.googleapis.com/token", clientId, clientSecret, code, redirectUri);
    }
    
    public GoogleAuthorizationCodeTokenRequest(final HttpTransport transport, final JsonFactory jsonFactory, final String tokenServerEncodedUrl, final String clientId, final String clientSecret, final String code, final String redirectUri) {
        super(transport, jsonFactory, new GenericUrl(tokenServerEncodedUrl), code);
        this.setClientAuthentication((HttpExecuteInterceptor)new ClientParametersAuthentication(clientId, clientSecret));
        this.setRedirectUri(redirectUri);
    }
    
    public GoogleAuthorizationCodeTokenRequest setRequestInitializer(final HttpRequestInitializer requestInitializer) {
        return (GoogleAuthorizationCodeTokenRequest)super.setRequestInitializer(requestInitializer);
    }
    
    public GoogleAuthorizationCodeTokenRequest setTokenServerUrl(final GenericUrl tokenServerUrl) {
        return (GoogleAuthorizationCodeTokenRequest)super.setTokenServerUrl(tokenServerUrl);
    }
    
    public GoogleAuthorizationCodeTokenRequest setScopes(final Collection<String> scopes) {
        return (GoogleAuthorizationCodeTokenRequest)super.setScopes((Collection)scopes);
    }
    
    public GoogleAuthorizationCodeTokenRequest setGrantType(final String grantType) {
        return (GoogleAuthorizationCodeTokenRequest)super.setGrantType(grantType);
    }
    
    public GoogleAuthorizationCodeTokenRequest setClientAuthentication(final HttpExecuteInterceptor clientAuthentication) {
        Preconditions.checkNotNull((Object)clientAuthentication);
        return (GoogleAuthorizationCodeTokenRequest)super.setClientAuthentication(clientAuthentication);
    }
    
    public GoogleAuthorizationCodeTokenRequest setCode(final String code) {
        return (GoogleAuthorizationCodeTokenRequest)super.setCode(code);
    }
    
    public GoogleAuthorizationCodeTokenRequest setRedirectUri(final String redirectUri) {
        Preconditions.checkNotNull((Object)redirectUri);
        return (GoogleAuthorizationCodeTokenRequest)super.setRedirectUri(redirectUri);
    }
    
    public GoogleTokenResponse execute() throws IOException {
        return (GoogleTokenResponse)this.executeUnparsed().parseAs((Class)GoogleTokenResponse.class);
    }
    
    public GoogleAuthorizationCodeTokenRequest set(final String fieldName, final Object value) {
        return (GoogleAuthorizationCodeTokenRequest)super.set(fieldName, value);
    }
}
