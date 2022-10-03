package com.google.api.client.auth.oauth2;

import java.io.IOException;
import com.google.api.client.util.Preconditions;
import com.google.api.client.util.Beta;

@Deprecated
@Beta
public final class CredentialStoreRefreshListener implements CredentialRefreshListener
{
    private final CredentialStore credentialStore;
    private final String userId;
    
    public CredentialStoreRefreshListener(final String userId, final CredentialStore credentialStore) {
        this.userId = (String)Preconditions.checkNotNull((Object)userId);
        this.credentialStore = (CredentialStore)Preconditions.checkNotNull((Object)credentialStore);
    }
    
    @Override
    public void onTokenResponse(final Credential credential, final TokenResponse tokenResponse) throws IOException {
        this.makePersistent(credential);
    }
    
    @Override
    public void onTokenErrorResponse(final Credential credential, final TokenErrorResponse tokenErrorResponse) throws IOException {
        this.makePersistent(credential);
    }
    
    public CredentialStore getCredentialStore() {
        return this.credentialStore;
    }
    
    public void makePersistent(final Credential credential) throws IOException {
        this.credentialStore.store(this.userId, credential);
    }
}
