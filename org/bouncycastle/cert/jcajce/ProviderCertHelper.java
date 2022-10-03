package org.bouncycastle.cert.jcajce;

import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.Provider;

class ProviderCertHelper extends CertHelper
{
    private final Provider provider;
    
    ProviderCertHelper(final Provider provider) {
        this.provider = provider;
    }
    
    @Override
    protected CertificateFactory createCertificateFactory(final String s) throws CertificateException {
        return CertificateFactory.getInstance(s, this.provider);
    }
}
