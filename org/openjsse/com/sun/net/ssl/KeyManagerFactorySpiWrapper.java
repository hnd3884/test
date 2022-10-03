package org.openjsse.com.sun.net.ssl;

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
    
    KeyManagerFactorySpiWrapper(final String algName, final Provider prov) throws NoSuchAlgorithmException {
        this.theKeyManagerFactory = KeyManagerFactory.getInstance(algName, prov);
    }
    
    @Override
    protected void engineInit(final KeyStore ks, final char[] password) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
        this.theKeyManagerFactory.init(ks, password);
    }
    
    @Override
    protected KeyManager[] engineGetKeyManagers() {
        final javax.net.ssl.KeyManager[] kma = this.theKeyManagerFactory.getKeyManagers();
        KeyManager[] kmaw = new KeyManager[kma.length];
        int src = 0;
        int dst = 0;
        while (src < kma.length) {
            if (!(kma[src] instanceof KeyManager)) {
                if (kma[src] instanceof X509KeyManager) {
                    kmaw[dst] = new X509KeyManagerComSunWrapper((X509KeyManager)kma[src]);
                    ++dst;
                }
            }
            else {
                kmaw[dst] = (KeyManager)kma[src];
                ++dst;
            }
            ++src;
        }
        if (dst != src) {
            kmaw = (KeyManager[])SSLSecurity.truncateArray(kmaw, new KeyManager[dst]);
        }
        return kmaw;
    }
}
