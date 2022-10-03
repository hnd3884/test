package org.jscep.client.verification;

import java.security.cert.X509Certificate;

public final class OptimisticCertificateVerifier implements CertificateVerifier
{
    @Override
    public boolean verify(final X509Certificate cert) {
        return true;
    }
}
