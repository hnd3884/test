package com.me.devicemanagement.onpremise.webclient.admin.certificate;

import java.security.cert.X509Certificate;

public final class CertificateAlgorithmIdentifier
{
    private CertificateAlgorithmIdentifier() {
    }
    
    public static CertificateAlgorithm getAlgorithmFromCertificate(final X509Certificate certificateObj) {
        return new RSAAlgorithm();
    }
}
