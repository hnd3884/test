package org.jscep.client.verification;

import java.security.cert.X509Certificate;

public final class PessimisticCertificateVerifier implements CertificateVerifier
{
    @Override
    public boolean verify(final X509Certificate cert) {
        return false;
    }
}
