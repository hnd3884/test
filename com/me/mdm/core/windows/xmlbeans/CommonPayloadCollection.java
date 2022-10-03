package com.me.mdm.core.windows.xmlbeans;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Common")
class CommonPayloadCollection
{
    private CertificatesPPKGPayload certificatesPayload;
    private WorkplacePPKGPayload workplaceEnrollmentPayload;
    
    public CertificatesPPKGPayload getCertificatesPayload() {
        return this.certificatesPayload;
    }
    
    @XmlElement(name = "Certificates")
    public void setCertificatesPayload(final CertificatesPPKGPayload certificatesPayload) {
        this.certificatesPayload = certificatesPayload;
    }
    
    public WorkplacePPKGPayload getWorkplaceEnrollmentPayload() {
        return this.workplaceEnrollmentPayload;
    }
    
    @XmlElement(name = "Workplace")
    public void setWorkplaceEnrollmentPayload(final WorkplacePPKGPayload workplaceEnrollmentPayload) {
        this.workplaceEnrollmentPayload = workplaceEnrollmentPayload;
    }
}
