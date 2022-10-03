package com.google.api.client.googleapis.mtls;

import java.security.GeneralSecurityException;
import java.io.IOException;
import java.security.KeyStore;
import com.google.api.client.util.Beta;

@Beta
public interface MtlsProvider
{
    boolean useMtlsClientCertificate();
    
    String getKeyStorePassword();
    
    KeyStore getKeyStore() throws IOException, GeneralSecurityException;
}
