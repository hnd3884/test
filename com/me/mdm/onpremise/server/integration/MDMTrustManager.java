package com.me.mdm.onpremise.server.integration;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.X509TrustManager;

public class MDMTrustManager implements X509TrustManager
{
    private final X509TrustManager tm;
    private X509Certificate[] chain;
    
    MDMTrustManager(final X509TrustManager tm) {
        this.tm = tm;
    }
    
    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }
    
    public X509Certificate[] getCertificateChain() {
        return this.chain;
    }
    
    @Override
    public void checkServerTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
        this.chain = chain;
        this.tm.checkServerTrusted(chain, authType);
    }
    
    @Override
    public void checkClientTrusted(final X509Certificate[] chain, final String string) throws CertificateException {
        throw new UnsupportedOperationException();
    }
}
