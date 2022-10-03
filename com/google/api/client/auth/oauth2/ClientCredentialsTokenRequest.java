package com.google.api.client.auth.oauth2;

import com.google.api.client.util.GenericData;
import com.google.api.client.http.HttpExecuteInterceptor;
import java.util.Collection;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.http.HttpTransport;

public class ClientCredentialsTokenRequest extends TokenRequest
{
    public ClientCredentialsTokenRequest(final HttpTransport transport, final JsonFactory jsonFactory, final GenericUrl tokenServerUrl) {
        super(transport, jsonFactory, tokenServerUrl, "client_credentials");
    }
    
    @Override
    public ClientCredentialsTokenRequest setRequestInitializer(final HttpRequestInitializer requestInitializer) {
        return (ClientCredentialsTokenRequest)super.setRequestInitializer(requestInitializer);
    }
    
    @Override
    public ClientCredentialsTokenRequest setTokenServerUrl(final GenericUrl tokenServerUrl) {
        return (ClientCredentialsTokenRequest)super.setTokenServerUrl(tokenServerUrl);
    }
    
    @Override
    public ClientCredentialsTokenRequest setScopes(final Collection<String> scopes) {
        return (ClientCredentialsTokenRequest)super.setScopes(scopes);
    }
    
    @Override
    public ClientCredentialsTokenRequest setGrantType(final String grantType) {
        return (ClientCredentialsTokenRequest)super.setGrantType(grantType);
    }
    
    @Override
    public ClientCredentialsTokenRequest setClientAuthentication(final HttpExecuteInterceptor clientAuthentication) {
        return (ClientCredentialsTokenRequest)super.setClientAuthentication(clientAuthentication);
    }
    
    @Override
    public ClientCredentialsTokenRequest set(final String fieldName, final Object value) {
        return (ClientCredentialsTokenRequest)super.set(fieldName, value);
    }
    
    @Override
    public ClientCredentialsTokenRequest setResponseClass(final Class<? extends TokenResponse> responseClass) {
        return (ClientCredentialsTokenRequest)super.setResponseClass(responseClass);
    }
}
