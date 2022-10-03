package com.me.ems.onpremise.security.certificate.api.model;

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
    
    public PrivateKey getServerKey() {
        return this.serverKey;
    }
    
    public void setServerKey(final PrivateKey serverKey) {
        this.serverKey = serverKey;
    }
    
    public Certificate getServerCertificate() {
        return this.serverCertificate;
    }
    
    public void setServerCertificate(final Certificate serverCertificate) {
        this.serverCertificate = serverCertificate;
    }
    
    public Certificate[] getIntermediateCertificate() {
        return this.intermediateCertificate;
    }
    
    public void setIntermediateCertificate(final Certificate[] intermediateCertificate) {
        this.intermediateCertificate = intermediateCertificate;
    }
    
    public Certificate getRootCACertificate() {
        return this.rootCACertificate;
    }
    
    public void setRootCACertificate(final Certificate rootCACertificate) {
        this.rootCACertificate = rootCACertificate;
    }
    
    public Boolean getIsSelfSignedCA() {
        return this.isSelfSignedCA;
    }
    
    public void setIsSelfSignedCA(final Boolean selfSignedCA) {
        this.isSelfSignedCA = selfSignedCA;
    }
    
    public List<String> getTempFoldersListToDeleteFinally() {
        return this.tempFoldersListToDeleteFinally;
    }
    
    public void setTempFoldersListToDeleteFinally(final List<String> tempFoldersListToDeleteFinally) {
        this.tempFoldersListToDeleteFinally = tempFoldersListToDeleteFinally;
    }
}
