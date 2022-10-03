package com.me.ems.onpremise.security.certificate.api.core.events;

import com.me.ems.onpremise.security.certificate.api.model.CertificateChainObject;

public class ImportSSLCertificateChangeEvent
{
    public int certificateType;
    public CertificateChainObject certificateChainObject;
    
    public ImportSSLCertificateChangeEvent(final int certType) {
        this.certificateType = certType;
    }
    
    public ImportSSLCertificateChangeEvent(final int certType, final CertificateChainObject certChainObject) {
        this.certificateType = certType;
        this.certificateChainObject = certChainObject;
    }
}
