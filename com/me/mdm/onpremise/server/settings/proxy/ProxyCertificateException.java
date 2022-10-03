package com.me.mdm.onpremise.server.settings.proxy;

import java.security.cert.X509Certificate;
import java.security.cert.CertificateException;

public class ProxyCertificateException extends CertificateException
{
    private final X509Certificate[] certChain;
    private final X509Certificate[] trustedIssuers;
    
    ProxyCertificateException(final CertificateException ex, final X509Certificate[] certChain, final X509Certificate[] trustedIssuers) {
        super(ex);
        this.certChain = certChain;
        this.trustedIssuers = trustedIssuers;
    }
    
    public X509Certificate[] getCertChain() {
        return this.certChain;
    }
    
    public X509Certificate[] getTrustedIssuers() {
        return this.trustedIssuers;
    }
}
