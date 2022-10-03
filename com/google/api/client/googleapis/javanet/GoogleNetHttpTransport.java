package com.google.api.client.googleapis.javanet;

import com.google.api.client.util.Beta;
import java.security.KeyStore;
import com.google.api.client.googleapis.GoogleUtils;
import com.google.api.client.googleapis.mtls.MtlsProvider;
import java.io.IOException;
import java.security.GeneralSecurityException;
import com.google.api.client.googleapis.mtls.MtlsUtils;
import com.google.api.client.http.javanet.NetHttpTransport;

public class GoogleNetHttpTransport
{
    public static NetHttpTransport newTrustedTransport() throws GeneralSecurityException, IOException {
        return newTrustedTransport(MtlsUtils.getDefaultMtlsProvider());
    }
    
    @Beta
    public static NetHttpTransport newTrustedTransport(final MtlsProvider mtlsProvider) throws GeneralSecurityException, IOException {
        KeyStore mtlsKeyStore = null;
        String mtlsKeyStorePassword = null;
        if (mtlsProvider.useMtlsClientCertificate()) {
            mtlsKeyStore = mtlsProvider.getKeyStore();
            mtlsKeyStorePassword = mtlsProvider.getKeyStorePassword();
        }
        if (mtlsKeyStore != null && mtlsKeyStorePassword != null) {
            return new NetHttpTransport.Builder().trustCertificates(GoogleUtils.getCertificateTrustStore(), mtlsKeyStore, mtlsKeyStorePassword).build();
        }
        return new NetHttpTransport.Builder().trustCertificates(GoogleUtils.getCertificateTrustStore()).build();
    }
    
    private GoogleNetHttpTransport() {
    }
}
