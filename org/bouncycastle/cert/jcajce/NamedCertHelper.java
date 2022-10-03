package org.bouncycastle.cert.jcajce;

import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

class NamedCertHelper extends CertHelper
{
    private final String providerName;
    
    NamedCertHelper(final String providerName) {
        this.providerName = providerName;
    }
    
    @Override
    protected CertificateFactory createCertificateFactory(final String s) throws CertificateException, NoSuchProviderException {
        return CertificateFactory.getInstance(s, this.providerName);
    }
}
