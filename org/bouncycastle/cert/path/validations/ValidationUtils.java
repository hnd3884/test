package org.bouncycastle.cert.path.validations;

import org.bouncycastle.cert.X509CertificateHolder;

class ValidationUtils
{
    static boolean isSelfIssued(final X509CertificateHolder x509CertificateHolder) {
        return x509CertificateHolder.getSubject().equals((Object)x509CertificateHolder.getIssuer());
    }
}
