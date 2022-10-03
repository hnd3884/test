package org.bouncycastle.cert.jcajce;

import java.security.cert.CertificateException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateFactory;

abstract class CertHelper
{
    public CertificateFactory getCertificateFactory(final String s) throws NoSuchProviderException, CertificateException {
        return this.createCertificateFactory(s);
    }
    
    protected abstract CertificateFactory createCertificateFactory(final String p0) throws CertificateException, NoSuchProviderException;
}
