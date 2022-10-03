package org.jscep.client.verification;

import java.security.cert.X509Certificate;

public interface CertificateVerifier
{
    boolean verify(final X509Certificate p0);
}
