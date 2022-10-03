package com.me.devicemanagement.onpremise.server.certificate;

public class ServerSSLCertificateChangeEvent
{
    public int certificateType;
    public CertificateChainObject certificateChainObject;
    
    ServerSSLCertificateChangeEvent(final int certType) {
        this.certificateType = certType;
    }
    
    ServerSSLCertificateChangeEvent(final int certType, final CertificateChainObject certChainObject) {
        this.certificateType = certType;
        this.certificateChainObject = certChainObject;
    }
}
