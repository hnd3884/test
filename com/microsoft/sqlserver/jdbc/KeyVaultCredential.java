package com.microsoft.sqlserver.jdbc;

import java.util.concurrent.Future;
import java.util.concurrent.ExecutorService;
import com.microsoft.aad.adal4j.AuthenticationCallback;
import com.microsoft.aad.adal4j.ClientCredential;
import com.microsoft.aad.adal4j.AuthenticationContext;
import java.util.concurrent.Executors;
import com.microsoft.aad.adal4j.AuthenticationResult;
import com.microsoft.azure.keyvault.authentication.KeyVaultCredentials;

class KeyVaultCredential extends KeyVaultCredentials
{
    SQLServerKeyVaultAuthenticationCallback authenticationCallback;
    String clientId;
    String clientKey;
    String accessToken;
    
    KeyVaultCredential(final String clientId, final String clientKey) {
        this.authenticationCallback = null;
        this.clientId = null;
        this.clientKey = null;
        this.accessToken = null;
        this.clientId = clientId;
        this.clientKey = clientKey;
    }
    
    KeyVaultCredential(final SQLServerKeyVaultAuthenticationCallback authenticationCallback) {
        this.authenticationCallback = null;
        this.clientId = null;
        this.clientKey = null;
        this.accessToken = null;
        this.authenticationCallback = authenticationCallback;
    }
    
    public String doAuthenticate(final String authorization, final String resource, final String scope) {
        String accessToken;
        if (null == this.authenticationCallback) {
            final AuthenticationResult token = getAccessTokenFromClientCredentials(authorization, resource, this.clientId, this.clientKey);
            accessToken = token.getAccessToken();
        }
        else {
            accessToken = this.authenticationCallback.getAccessToken(authorization, resource, scope);
        }
        return accessToken;
    }
    
    private static AuthenticationResult getAccessTokenFromClientCredentials(final String authorization, final String resource, final String clientId, final String clientKey) {
        AuthenticationContext context = null;
        AuthenticationResult result = null;
        ExecutorService service = null;
        try {
            service = Executors.newFixedThreadPool(1);
            context = new AuthenticationContext(authorization, false, service);
            final ClientCredential credentials = new ClientCredential(clientId, clientKey);
            final Future<AuthenticationResult> future = context.acquireToken(resource, credentials, (AuthenticationCallback)null);
            result = future.get();
        }
        catch (final Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            if (null != service) {
                service.shutdown();
            }
        }
        if (null == result) {
            throw new RuntimeException("authentication result was null");
        }
        return result;
    }
}
