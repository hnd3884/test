package com.me.mdm.core.windows.xmlbeans;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Certificates")
class CertificatesPPKGPayload
{
    private RootCertificatesContainer rootCertificates;
    
    public RootCertificatesContainer getRootCertificates() {
        return this.rootCertificates;
    }
    
    @XmlElement(name = "RootCertificates")
    public void setRootCertificates(final RootCertificatesContainer rootCertificates) {
        this.rootCertificates = rootCertificates;
    }
}
