package com.sun.net.ssl;

import javax.net.ssl.X509KeyManager;
import java.security.UnrecoverableKeyException;
import java.security.KeyStoreException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import javax.net.ssl.KeyManagerFactory;

final class KeyManagerFactorySpiWrapper extends KeyManagerFactorySpi
{
    private KeyManagerFactory theKeyManagerFactory;
    
    KeyManagerFactorySpiWrapper(final String s, final Provider provider) throws NoSuchAlgorithmException {
        this.theKeyManagerFactory = KeyManagerFactory.getInstance(s, provider);
    }
    
    @Override
    protected void engineInit(final KeyStore keyStore, final char[] array) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
        this.theKeyManagerFactory.init(keyStore, array);
    }
    
    @Override
    protected KeyManager[] engineGetKeyManagers() {
        final javax.net.ssl.KeyManager[] keyManagers = this.theKeyManagerFactory.getKeyManagers();
        KeyManager[] array = new KeyManager[keyManagers.length];
        int i = 0;
        int n = 0;
        while (i < keyManagers.length) {
            if (!(keyManagers[i] instanceof KeyManager)) {
                if (keyManagers[i] instanceof X509KeyManager) {
                    array[n] = new X509KeyManagerComSunWrapper((X509KeyManager)keyManagers[i]);
                    ++n;
                }
            }
            else {
                array[n] = (KeyManager)keyManagers[i];
                ++n;
            }
            ++i;
        }
        if (n != i) {
            array = (KeyManager[])SSLSecurity.truncateArray(array, new KeyManager[n]);
        }
        return array;
    }
}
