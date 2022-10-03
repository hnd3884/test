package org.jscep.client.verification;

import java.security.cert.X509Certificate;

public final class PreProvisionedCertificateVerifier implements CertificateVerifier
{
    private final X509Certificate cert;
    
    public PreProvisionedCertificateVerifier(final X509Certificate cert) {
        this.cert = cert;
    }
    
    @Override
    public boolean verify(final X509Certificate cert) {
        return this.cert.equals(cert);
    }
}
