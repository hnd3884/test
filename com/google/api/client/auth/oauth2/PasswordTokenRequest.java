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

public class PasswordTokenRequest extends TokenRequest
{
    @Key
    private String username;
    @Key
    private String password;
    
    public PasswordTokenRequest(final HttpTransport transport, final JsonFactory jsonFactory, final GenericUrl tokenServerUrl, final String username, final String password) {
        super(transport, jsonFactory, tokenServerUrl, "password");
        this.setUsername(username);
        this.setPassword(password);
    }
    
    @Override
    public PasswordTokenRequest setRequestInitializer(final HttpRequestInitializer requestInitializer) {
        return (PasswordTokenRequest)super.setRequestInitializer(requestInitializer);
    }
    
    @Override
    public PasswordTokenRequest setTokenServerUrl(final GenericUrl tokenServerUrl) {
        return (PasswordTokenRequest)super.setTokenServerUrl(tokenServerUrl);
    }
    
    @Override
    public PasswordTokenRequest setScopes(final Collection<String> scopes) {
        return (PasswordTokenRequest)super.setScopes(scopes);
    }
    
    @Override
    public PasswordTokenRequest setGrantType(final String grantType) {
        return (PasswordTokenRequest)super.setGrantType(grantType);
    }
    
    @Override
    public PasswordTokenRequest setClientAuthentication(final HttpExecuteInterceptor clientAuthentication) {
        return (PasswordTokenRequest)super.setClientAuthentication(clientAuthentication);
    }
    
    @Override
    public PasswordTokenRequest setResponseClass(final Class<? extends TokenResponse> responseClass) {
        return (PasswordTokenRequest)super.setResponseClass(responseClass);
    }
    
    public final String getUsername() {
        return this.username;
    }
    
    public PasswordTokenRequest setUsername(final String username) {
        this.username = (String)Preconditions.checkNotNull((Object)username);
        return this;
    }
    
    public final String getPassword() {
        return this.password;
    }
    
    public PasswordTokenRequest setPassword(final String password) {
        this.password = (String)Preconditions.checkNotNull((Object)password);
        return this;
    }
    
    @Override
    public PasswordTokenRequest set(final String fieldName, final Object value) {
        return (PasswordTokenRequest)super.set(fieldName, value);
    }
}
