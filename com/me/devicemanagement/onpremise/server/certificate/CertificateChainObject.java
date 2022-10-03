package com.me.devicemanagement.onpremise.server.certificate;

import java.util.List;
import java.security.cert.Certificate;
import java.security.PrivateKey;

public class CertificateChainObject
{
    PrivateKey serverKey;
    Certificate serverCertificate;
    Certificate[] intermediateCertificate;
    Certificate rootCACertificate;
    Boolean isSelfSignedCA;
    List<String> tempFoldersListToDeleteFinally;
    
    public Certificate getRootCACertificate() {
        return this.rootCACertificate;
    }
}
