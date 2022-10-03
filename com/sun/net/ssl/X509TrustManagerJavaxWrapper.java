package com.sun.net.ssl;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.X509TrustManager;

final class X509TrustManagerJavaxWrapper implements X509TrustManager
{
    private com.sun.net.ssl.X509TrustManager theX509TrustManager;
    
    X509TrustManagerJavaxWrapper(final com.sun.net.ssl.X509TrustManager theX509TrustManager) {
        this.theX509TrustManager = theX509TrustManager;
    }
    
    @Override
    public void checkClientTrusted(final X509Certificate[] array, final String s) throws CertificateException {
        if (!this.theX509TrustManager.isClientTrusted(array)) {
            throw new CertificateException("Untrusted Client Certificate Chain");
        }
    }
    
    @Override
    public void checkServerTrusted(final X509Certificate[] array, final String s) throws CertificateException {
        if (!this.theX509TrustManager.isServerTrusted(array)) {
            throw new CertificateException("Untrusted Server Certificate Chain");
        }
    }
    
    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return this.theX509TrustManager.getAcceptedIssuers();
    }
}
