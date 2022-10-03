package com.me.mdm.core.windows.xmlbeans;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "PackageConfig")
class ProvisioningPackageConfig
{
    private String packageGUID;
    private String packageName;
    private String packageVersion;
    private String packageOwnerType;
    private String packageRank;
    private String xmlNameSpace;
    
    public String getPackageGUID() {
        return this.packageGUID;
    }
    
    @XmlElement(name = "ID")
    public void setPackageGUID(final String packageGUID) {
        this.packageGUID = packageGUID;
    }
    
    public String getPackageName() {
        return this.packageName;
    }
    
    @XmlElement(name = "Name")
    public void setPackageName(final String packageName) {
        this.packageName = packageName;
    }
    
    public String getPackageVersion() {
        return this.packageVersion;
    }
    
    @XmlElement(name = "Version")
    public void setPackageVersion(final String packageVersion) {
        this.packageVersion = packageVersion;
    }
    
    public String getPackageRank() {
        return this.packageRank;
    }
    
    @XmlElement(name = "Rank")
    public void setPackageRank(final String packageRank) {
        this.packageRank = packageRank;
    }
    
    public String getPackageOwnerType() {
        return this.packageOwnerType;
    }
    
    @XmlElement(name = "OwnerType")
    public void setPackageOwnerType(final String packageOwnerType) {
        this.packageOwnerType = packageOwnerType;
    }
    
    public String getXmlNameSpace() {
        return this.xmlNameSpace;
    }
    
    @XmlAttribute(name = "xmlns")
    public void setXmlNameSpace(final String xmlNameSpace) {
        this.xmlNameSpace = xmlNameSpace;
    }
}
