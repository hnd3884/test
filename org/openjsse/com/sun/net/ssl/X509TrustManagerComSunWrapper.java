package org.openjsse.com.sun.net.ssl;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

final class X509TrustManagerComSunWrapper implements X509TrustManager
{
    private javax.net.ssl.X509TrustManager theX509TrustManager;
    
    X509TrustManagerComSunWrapper(final javax.net.ssl.X509TrustManager obj) {
        this.theX509TrustManager = obj;
    }
    
    @Override
    public boolean isClientTrusted(final X509Certificate[] chain) {
        try {
            this.theX509TrustManager.checkClientTrusted(chain, "UNKNOWN");
            return true;
        }
        catch (final CertificateException e) {
            return false;
        }
    }
    
    @Override
    public boolean isServerTrusted(final X509Certificate[] chain) {
        try {
            this.theX509TrustManager.checkServerTrusted(chain, "UNKNOWN");
            return true;
        }
        catch (final CertificateException e) {
            return false;
        }
    }
    
    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return this.theX509TrustManager.getAcceptedIssuers();
    }
}
