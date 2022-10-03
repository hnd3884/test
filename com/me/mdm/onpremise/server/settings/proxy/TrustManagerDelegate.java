package com.me.mdm.onpremise.server.settings.proxy;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.X509TrustManager;

public class TrustManagerDelegate implements X509TrustManager
{
    private final X509TrustManager trustManager;
    
    TrustManagerDelegate(final X509TrustManager trustManager) {
        this.trustManager = trustManager;
    }
    
    @Override
    public void checkClientTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
        this.trustManager.checkClientTrusted(chain, authType);
    }
    
    @Override
    public void checkServerTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
        try {
            this.trustManager.checkServerTrusted(chain, authType);
        }
        catch (final CertificateException var4) {
            throw new ProxyCertificateException(var4, chain, this.getAcceptedIssuers());
        }
    }
    
    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return this.trustManager.getAcceptedIssuers();
    }
}
