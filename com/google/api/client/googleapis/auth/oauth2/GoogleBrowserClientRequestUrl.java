package com.google.api.client.googleapis.auth.oauth2;

import com.google.api.client.util.GenericData;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.auth.oauth2.AuthorizationRequestUrl;
import com.google.api.client.util.Preconditions;
import java.util.Collection;
import com.google.api.client.util.Key;
import com.google.api.client.auth.oauth2.BrowserClientRequestUrl;

public class GoogleBrowserClientRequestUrl extends BrowserClientRequestUrl
{
    @Key("approval_prompt")
    private String approvalPrompt;
    
    public GoogleBrowserClientRequestUrl(final String clientId, final String redirectUri, final Collection<String> scopes) {
        super("https://accounts.google.com/o/oauth2/auth", clientId);
        this.setRedirectUri(redirectUri);
        this.setScopes(scopes);
    }
    
    public GoogleBrowserClientRequestUrl(final GoogleClientSecrets clientSecrets, final String redirectUri, final Collection<String> scopes) {
        this(clientSecrets.getDetails().getClientId(), redirectUri, scopes);
    }
    
    public final String getApprovalPrompt() {
        return this.approvalPrompt;
    }
    
    public GoogleBrowserClientRequestUrl setApprovalPrompt(final String approvalPrompt) {
        this.approvalPrompt = approvalPrompt;
        return this;
    }
    
    public GoogleBrowserClientRequestUrl setResponseTypes(final Collection<String> responseTypes) {
        return (GoogleBrowserClientRequestUrl)super.setResponseTypes((Collection)responseTypes);
    }
    
    public GoogleBrowserClientRequestUrl setRedirectUri(final String redirectUri) {
        return (GoogleBrowserClientRequestUrl)super.setRedirectUri(redirectUri);
    }
    
    public GoogleBrowserClientRequestUrl setScopes(final Collection<String> scopes) {
        Preconditions.checkArgument(scopes.iterator().hasNext());
        return (GoogleBrowserClientRequestUrl)super.setScopes((Collection)scopes);
    }
    
    public GoogleBrowserClientRequestUrl setClientId(final String clientId) {
        return (GoogleBrowserClientRequestUrl)super.setClientId(clientId);
    }
    
    public GoogleBrowserClientRequestUrl setState(final String state) {
        return (GoogleBrowserClientRequestUrl)super.setState(state);
    }
    
    public GoogleBrowserClientRequestUrl set(final String fieldName, final Object value) {
        return (GoogleBrowserClientRequestUrl)super.set(fieldName, value);
    }
    
    public GoogleBrowserClientRequestUrl clone() {
        return (GoogleBrowserClientRequestUrl)super.clone();
    }
}
