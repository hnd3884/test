package com.google.api.client.auth.oauth2;

import java.io.Serializable;
import com.google.api.client.util.Preconditions;
import java.io.IOException;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.Beta;

@Beta
public final class DataStoreCredentialRefreshListener implements CredentialRefreshListener
{
    private final DataStore<StoredCredential> credentialDataStore;
    private final String userId;
    
    public DataStoreCredentialRefreshListener(final String userId, final DataStoreFactory dataStoreFactory) throws IOException {
        this(userId, StoredCredential.getDefaultDataStore(dataStoreFactory));
    }
    
    public DataStoreCredentialRefreshListener(final String userId, final DataStore<StoredCredential> credentialDataStore) {
        this.userId = (String)Preconditions.checkNotNull((Object)userId);
        this.credentialDataStore = (DataStore<StoredCredential>)Preconditions.checkNotNull((Object)credentialDataStore);
    }
    
    @Override
    public void onTokenResponse(final Credential credential, final TokenResponse tokenResponse) throws IOException {
        this.makePersistent(credential);
    }
    
    @Override
    public void onTokenErrorResponse(final Credential credential, final TokenErrorResponse tokenErrorResponse) throws IOException {
        this.makePersistent(credential);
    }
    
    public DataStore<StoredCredential> getCredentialDataStore() {
        return this.credentialDataStore;
    }
    
    public void makePersistent(final Credential credential) throws IOException {
        this.credentialDataStore.set(this.userId, (Serializable)new StoredCredential(credential));
    }
}
