package com.me.mdm.core.windows.xmlbeans;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "RootCertificate")
class RootCertificate
{
    private String certificateName;
    private String name;
    private String certificatePath;
    
    public String getCertificateName() {
        return this.certificateName;
    }
    
    @XmlAttribute(name = "CertificateName")
    public void setCertificateName(final String certificateName) {
        this.certificateName = certificateName;
    }
    
    public String getName() {
        return this.name;
    }
    
    @XmlAttribute(name = "Name")
    public void setName(String name) {
        this.name = name;
    }
    
    public String getCertificatePath() {
        return this.certificatePath;
    }
    
    @XmlElement(name = "CertificatePath")
    public void setCertificatePath(final String certificatePath) {
        this.certificatePath = certificatePath;
    }
}
