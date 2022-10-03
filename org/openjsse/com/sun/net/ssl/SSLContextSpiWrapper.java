package org.openjsse.com.sun.net.ssl;

import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;
import java.security.KeyManagementException;
import java.security.SecureRandom;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import javax.net.ssl.SSLContext;

final class SSLContextSpiWrapper extends SSLContextSpi
{
    private SSLContext theSSLContext;
    
    SSLContextSpiWrapper(final String algName, final Provider prov) throws NoSuchAlgorithmException {
        this.theSSLContext = SSLContext.getInstance(algName, prov);
    }
    
    @Override
    protected void engineInit(final KeyManager[] kma, final TrustManager[] tma, final SecureRandom sr) throws KeyManagementException {
        javax.net.ssl.KeyManager[] kmaw;
        if (kma != null) {
            kmaw = new javax.net.ssl.KeyManager[kma.length];
            int src = 0;
            int dst = 0;
            while (src < kma.length) {
                if (!(kma[src] instanceof javax.net.ssl.KeyManager)) {
                    if (kma[src] instanceof X509KeyManager) {
                        kmaw[dst] = new X509KeyManagerJavaxWrapper((X509KeyManager)kma[src]);
                        ++dst;
                    }
                }
                else {
                    kmaw[dst] = (javax.net.ssl.KeyManager)kma[src];
                    ++dst;
                }
                ++src;
            }
            if (dst != src) {
                kmaw = (javax.net.ssl.KeyManager[])SSLSecurity.truncateArray(kmaw, new javax.net.ssl.KeyManager[dst]);
            }
        }
        else {
            kmaw = null;
        }
        javax.net.ssl.TrustManager[] tmaw;
        if (tma != null) {
            tmaw = new javax.net.ssl.TrustManager[tma.length];
            int src = 0;
            int dst = 0;
            while (src < tma.length) {
                if (!(tma[src] instanceof javax.net.ssl.TrustManager)) {
                    if (tma[src] instanceof X509TrustManager) {
                        tmaw[dst] = new X509TrustManagerJavaxWrapper((X509TrustManager)tma[src]);
                        ++dst;
                    }
                }
                else {
                    tmaw[dst] = (javax.net.ssl.TrustManager)tma[src];
                    ++dst;
                }
                ++src;
            }
            if (dst != src) {
                tmaw = (javax.net.ssl.TrustManager[])SSLSecurity.truncateArray(tmaw, new javax.net.ssl.TrustManager[dst]);
            }
        }
        else {
            tmaw = null;
        }
        this.theSSLContext.init(kmaw, tmaw, sr);
    }
    
    @Override
    protected SSLSocketFactory engineGetSocketFactory() {
        return this.theSSLContext.getSocketFactory();
    }
    
    @Override
    protected SSLServerSocketFactory engineGetServerSocketFactory() {
        return this.theSSLContext.getServerSocketFactory();
    }
}
