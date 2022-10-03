package com.me.mdm.core.windows.xmlbeans;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "RootCertificates")
class RootCertificatesContainer
{
    private ArrayList<RootCertificate> rootCertificateList;
    
    public ArrayList<RootCertificate> getRootCertificateList() {
        return this.rootCertificateList;
    }
    
    @XmlElement(name = "RootCertificate")
    public void setRootCertificateList(final ArrayList<RootCertificate> rootCertificateList) {
        this.rootCertificateList = rootCertificateList;
    }
}
