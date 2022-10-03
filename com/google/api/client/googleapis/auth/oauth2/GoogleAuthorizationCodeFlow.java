package com.google.api.client.googleapis.auth.oauth2;

import com.google.api.client.auth.oauth2.CredentialRefreshListener;
import com.google.api.client.util.Clock;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.util.Preconditions;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.util.Beta;
import com.google.api.client.auth.oauth2.CredentialStore;
import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.util.store.DataStore;
import java.io.IOException;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.http.HttpExecuteInterceptor;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.AuthorizationCodeTokenRequest;
import java.util.Collection;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;

public class GoogleAuthorizationCodeFlow extends AuthorizationCodeFlow
{
    private final String approvalPrompt;
    private final String accessType;
    
    public GoogleAuthorizationCodeFlow(final HttpTransport transport, final JsonFactory jsonFactory, final String clientId, final String clientSecret, final Collection<String> scopes) {
        this(new Builder(transport, jsonFactory, clientId, clientSecret, scopes));
    }
    
    protected GoogleAuthorizationCodeFlow(final Builder builder) {
        super((AuthorizationCodeFlow.Builder)builder);
        this.accessType = builder.accessType;
        this.approvalPrompt = builder.approvalPrompt;
    }
    
    public GoogleAuthorizationCodeTokenRequest newTokenRequest(final String authorizationCode) {
        return new GoogleAuthorizationCodeTokenRequest(this.getTransport(), this.getJsonFactory(), this.getTokenServerEncodedUrl(), "", "", authorizationCode, "").setClientAuthentication(this.getClientAuthentication()).setRequestInitializer(this.getRequestInitializer()).setScopes(this.getScopes());
    }
    
    public GoogleAuthorizationCodeRequestUrl newAuthorizationUrl() {
        return new GoogleAuthorizationCodeRequestUrl(this.getAuthorizationServerEncodedUrl(), this.getClientId(), "", this.getScopes()).setAccessType(this.accessType).setApprovalPrompt(this.approvalPrompt);
    }
    
    public final String getApprovalPrompt() {
        return this.approvalPrompt;
    }
    
    public final String getAccessType() {
        return this.accessType;
    }
    
    public static class Builder extends AuthorizationCodeFlow.Builder
    {
        String approvalPrompt;
        String accessType;
        
        public Builder(final HttpTransport transport, final JsonFactory jsonFactory, final String clientId, final String clientSecret, final Collection<String> scopes) {
            super(BearerToken.authorizationHeaderAccessMethod(), transport, jsonFactory, new GenericUrl("https://oauth2.googleapis.com/token"), (HttpExecuteInterceptor)new ClientParametersAuthentication(clientId, clientSecret), clientId, "https://accounts.google.com/o/oauth2/auth");
            this.setScopes(scopes);
        }
        
        public Builder(final HttpTransport transport, final JsonFactory jsonFactory, final GoogleClientSecrets clientSecrets, final Collection<String> scopes) {
            super(BearerToken.authorizationHeaderAccessMethod(), transport, jsonFactory, new GenericUrl("https://oauth2.googleapis.com/token"), (HttpExecuteInterceptor)new ClientParametersAuthentication(clientSecrets.getDetails().getClientId(), clientSecrets.getDetails().getClientSecret()), clientSecrets.getDetails().getClientId(), "https://accounts.google.com/o/oauth2/auth");
            this.setScopes(scopes);
        }
        
        public GoogleAuthorizationCodeFlow build() {
            return new GoogleAuthorizationCodeFlow(this);
        }
        
        public Builder setDataStoreFactory(final DataStoreFactory dataStore) throws IOException {
            return (Builder)super.setDataStoreFactory(dataStore);
        }
        
        public Builder setCredentialDataStore(final DataStore<StoredCredential> typedDataStore) {
            return (Builder)super.setCredentialDataStore((DataStore)typedDataStore);
        }
        
        public Builder setCredentialCreatedListener(final AuthorizationCodeFlow.CredentialCreatedListener credentialCreatedListener) {
            return (Builder)super.setCredentialCreatedListener(credentialCreatedListener);
        }
        
        @Deprecated
        @Beta
        public Builder setCredentialStore(final CredentialStore credentialStore) {
            return (Builder)super.setCredentialStore(credentialStore);
        }
        
        public Builder setRequestInitializer(final HttpRequestInitializer requestInitializer) {
            return (Builder)super.setRequestInitializer(requestInitializer);
        }
        
        public Builder setScopes(final Collection<String> scopes) {
            Preconditions.checkState(!scopes.isEmpty());
            return (Builder)super.setScopes((Collection)scopes);
        }
        
        public Builder setMethod(final Credential.AccessMethod method) {
            return (Builder)super.setMethod(method);
        }
        
        public Builder setTransport(final HttpTransport transport) {
            return (Builder)super.setTransport(transport);
        }
        
        public Builder setJsonFactory(final JsonFactory jsonFactory) {
            return (Builder)super.setJsonFactory(jsonFactory);
        }
        
        public Builder setTokenServerUrl(final GenericUrl tokenServerUrl) {
            return (Builder)super.setTokenServerUrl(tokenServerUrl);
        }
        
        public Builder setClientAuthentication(final HttpExecuteInterceptor clientAuthentication) {
            return (Builder)super.setClientAuthentication(clientAuthentication);
        }
        
        public Builder setClientId(final String clientId) {
            return (Builder)super.setClientId(clientId);
        }
        
        public Builder setAuthorizationServerEncodedUrl(final String authorizationServerEncodedUrl) {
            return (Builder)super.setAuthorizationServerEncodedUrl(authorizationServerEncodedUrl);
        }
        
        public Builder setClock(final Clock clock) {
            return (Builder)super.setClock(clock);
        }
        
        public Builder addRefreshListener(final CredentialRefreshListener refreshListener) {
            return (Builder)super.addRefreshListener(refreshListener);
        }
        
        public Builder setRefreshListeners(final Collection<CredentialRefreshListener> refreshListeners) {
            return (Builder)super.setRefreshListeners((Collection)refreshListeners);
        }
        
        public Builder setApprovalPrompt(final String approvalPrompt) {
            this.approvalPrompt = approvalPrompt;
            return this;
        }
        
        public final String getApprovalPrompt() {
            return this.approvalPrompt;
        }
        
        public Builder setAccessType(final String accessType) {
            this.accessType = accessType;
            return this;
        }
        
        public final String getAccessType() {
            return this.accessType;
        }
    }
}
