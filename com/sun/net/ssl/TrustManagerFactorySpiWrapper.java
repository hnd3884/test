package com.sun.net.ssl;

import javax.net.ssl.X509TrustManager;
import java.security.KeyStoreException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import javax.net.ssl.TrustManagerFactory;

final class TrustManagerFactorySpiWrapper extends TrustManagerFactorySpi
{
    private TrustManagerFactory theTrustManagerFactory;
    
    TrustManagerFactorySpiWrapper(final String s, final Provider provider) throws NoSuchAlgorithmException {
        this.theTrustManagerFactory = TrustManagerFactory.getInstance(s, provider);
    }
    
    @Override
    protected void engineInit(final KeyStore keyStore) throws KeyStoreException {
        this.theTrustManagerFactory.init(keyStore);
    }
    
    @Override
    protected TrustManager[] engineGetTrustManagers() {
        final javax.net.ssl.TrustManager[] trustManagers = this.theTrustManagerFactory.getTrustManagers();
        TrustManager[] array = new TrustManager[trustManagers.length];
        int i = 0;
        int n = 0;
        while (i < trustManagers.length) {
            if (!(trustManagers[i] instanceof TrustManager)) {
                if (trustManagers[i] instanceof X509TrustManager) {
                    array[n] = new X509TrustManagerComSunWrapper((X509TrustManager)trustManagers[i]);
                    ++n;
                }
            }
            else {
                array[n] = (TrustManager)trustManagers[i];
                ++n;
            }
            ++i;
        }
        if (n != i) {
            array = (TrustManager[])SSLSecurity.truncateArray(array, new TrustManager[n]);
        }
        return array;
    }
}
