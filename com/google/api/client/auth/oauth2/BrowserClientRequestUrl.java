package com.google.api.client.auth.oauth2;

import com.google.api.client.util.GenericData;
import com.google.api.client.http.GenericUrl;
import java.util.Collection;
import java.util.Collections;

public class BrowserClientRequestUrl extends AuthorizationRequestUrl
{
    public BrowserClientRequestUrl(final String encodedAuthorizationServerUrl, final String clientId) {
        super(encodedAuthorizationServerUrl, clientId, Collections.singleton("token"));
    }
    
    @Override
    public BrowserClientRequestUrl setResponseTypes(final Collection<String> responseTypes) {
        return (BrowserClientRequestUrl)super.setResponseTypes(responseTypes);
    }
    
    @Override
    public BrowserClientRequestUrl setRedirectUri(final String redirectUri) {
        return (BrowserClientRequestUrl)super.setRedirectUri(redirectUri);
    }
    
    @Override
    public BrowserClientRequestUrl setScopes(final Collection<String> scopes) {
        return (BrowserClientRequestUrl)super.setScopes(scopes);
    }
    
    @Override
    public BrowserClientRequestUrl setClientId(final String clientId) {
        return (BrowserClientRequestUrl)super.setClientId(clientId);
    }
    
    @Override
    public BrowserClientRequestUrl setState(final String state) {
        return (BrowserClientRequestUrl)super.setState(state);
    }
    
    @Override
    public BrowserClientRequestUrl set(final String fieldName, final Object value) {
        return (BrowserClientRequestUrl)super.set(fieldName, value);
    }
    
    @Override
    public BrowserClientRequestUrl clone() {
        return (BrowserClientRequestUrl)super.clone();
    }
}
