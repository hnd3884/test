package com.google.api.client.googleapis.auth.oauth2;

import com.google.api.client.util.GenericData;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.auth.oauth2.AuthorizationRequestUrl;
import com.google.api.client.util.Preconditions;
import java.util.Collection;
import com.google.api.client.util.Key;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;

public class GoogleAuthorizationCodeRequestUrl extends AuthorizationCodeRequestUrl
{
    @Key("approval_prompt")
    private String approvalPrompt;
    @Key("access_type")
    private String accessType;
    
    public GoogleAuthorizationCodeRequestUrl(final String clientId, final String redirectUri, final Collection<String> scopes) {
        this("https://accounts.google.com/o/oauth2/auth", clientId, redirectUri, scopes);
    }
    
    public GoogleAuthorizationCodeRequestUrl(final String authorizationServerEncodedUrl, final String clientId, final String redirectUri, final Collection<String> scopes) {
        super(authorizationServerEncodedUrl, clientId);
        this.setRedirectUri(redirectUri);
        this.setScopes(scopes);
    }
    
    public GoogleAuthorizationCodeRequestUrl(final GoogleClientSecrets clientSecrets, final String redirectUri, final Collection<String> scopes) {
        this(clientSecrets.getDetails().getClientId(), redirectUri, scopes);
    }
    
    public final String getApprovalPrompt() {
        return this.approvalPrompt;
    }
    
    public GoogleAuthorizationCodeRequestUrl setApprovalPrompt(final String approvalPrompt) {
        this.approvalPrompt = approvalPrompt;
        return this;
    }
    
    public final String getAccessType() {
        return this.accessType;
    }
    
    public GoogleAuthorizationCodeRequestUrl setAccessType(final String accessType) {
        this.accessType = accessType;
        return this;
    }
    
    public GoogleAuthorizationCodeRequestUrl setResponseTypes(final Collection<String> responseTypes) {
        return (GoogleAuthorizationCodeRequestUrl)super.setResponseTypes((Collection)responseTypes);
    }
    
    public GoogleAuthorizationCodeRequestUrl setRedirectUri(final String redirectUri) {
        Preconditions.checkNotNull((Object)redirectUri);
        return (GoogleAuthorizationCodeRequestUrl)super.setRedirectUri(redirectUri);
    }
    
    public GoogleAuthorizationCodeRequestUrl setScopes(final Collection<String> scopes) {
        Preconditions.checkArgument(scopes.iterator().hasNext());
        return (GoogleAuthorizationCodeRequestUrl)super.setScopes((Collection)scopes);
    }
    
    public GoogleAuthorizationCodeRequestUrl setClientId(final String clientId) {
        return (GoogleAuthorizationCodeRequestUrl)super.setClientId(clientId);
    }
    
    public GoogleAuthorizationCodeRequestUrl setState(final String state) {
        return (GoogleAuthorizationCodeRequestUrl)super.setState(state);
    }
    
    public GoogleAuthorizationCodeRequestUrl set(final String fieldName, final Object value) {
        return (GoogleAuthorizationCodeRequestUrl)super.set(fieldName, value);
    }
    
    public GoogleAuthorizationCodeRequestUrl clone() {
        return (GoogleAuthorizationCodeRequestUrl)super.clone();
    }
}
