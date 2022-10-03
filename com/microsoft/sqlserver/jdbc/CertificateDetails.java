package com.microsoft.sqlserver.jdbc;

import java.security.Key;
import java.security.cert.X509Certificate;

class CertificateDetails
{
    X509Certificate certificate;
    Key privateKey;
    
    CertificateDetails(final X509Certificate certificate, final Key privateKey) {
        this.certificate = certificate;
        this.privateKey = privateKey;
    }
}
