package org.openjsse.com.sun.net.ssl;

import javax.net.ssl.X509TrustManager;
import java.security.KeyStoreException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import javax.net.ssl.TrustManagerFactory;

final class TrustManagerFactorySpiWrapper extends TrustManagerFactorySpi
{
    private TrustManagerFactory theTrustManagerFactory;
    
    TrustManagerFactorySpiWrapper(final String algName, final Provider prov) throws NoSuchAlgorithmException {
        this.theTrustManagerFactory = TrustManagerFactory.getInstance(algName, prov);
    }
    
    @Override
    protected void engineInit(final KeyStore ks) throws KeyStoreException {
        this.theTrustManagerFactory.init(ks);
    }
    
    @Override
    protected TrustManager[] engineGetTrustManagers() {
        final javax.net.ssl.TrustManager[] tma = this.theTrustManagerFactory.getTrustManagers();
        TrustManager[] tmaw = new TrustManager[tma.length];
        int src = 0;
        int dst = 0;
        while (src < tma.length) {
            if (!(tma[src] instanceof TrustManager)) {
                if (tma[src] instanceof X509TrustManager) {
                    tmaw[dst] = new X509TrustManagerComSunWrapper((X509TrustManager)tma[src]);
                    ++dst;
                }
            }
            else {
                tmaw[dst] = (TrustManager)tma[src];
                ++dst;
            }
            ++src;
        }
        if (dst != src) {
            tmaw = (TrustManager[])SSLSecurity.truncateArray(tmaw, new TrustManager[dst]);
        }
        return tmaw;
    }
}
