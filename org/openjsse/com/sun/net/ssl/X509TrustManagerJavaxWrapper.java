package org.openjsse.com.sun.net.ssl;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.X509TrustManager;

final class X509TrustManagerJavaxWrapper implements X509TrustManager
{
    private org.openjsse.com.sun.net.ssl.X509TrustManager theX509TrustManager;
    
    X509TrustManagerJavaxWrapper(final org.openjsse.com.sun.net.ssl.X509TrustManager obj) {
        this.theX509TrustManager = obj;
    }
    
    @Override
    public void checkClientTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
        if (!this.theX509TrustManager.isClientTrusted(chain)) {
            throw new CertificateException("Untrusted Client Certificate Chain");
        }
    }
    
    @Override
    public void checkServerTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
        if (!this.theX509TrustManager.isServerTrusted(chain)) {
            throw new CertificateException("Untrusted Server Certificate Chain");
        }
    }
    
    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return this.theX509TrustManager.getAcceptedIssuers();
    }
}
