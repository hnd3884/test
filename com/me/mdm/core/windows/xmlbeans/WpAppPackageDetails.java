package com.me.mdm.core.windows.xmlbeans;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Package")
public class WpAppPackageDetails
{
    private String packageFamilyName;
    private String packageFullName;
    private String name;
    private String version;
    private String publisher;
    private String architecture;
    private String users;
    private String installLocation;
    private String installDate;
    private String resourceID;
    private String isBundle;
    private String isFramework;
    private String isProvisioned;
    private String packageStatus;
    
    public String getName() {
        return this.name;
    }
    
    @XmlAttribute(name = "Name")
    public void setName(String name) {
        this.name = name;
    }
    
    public String getPackageFullName() {
        return this.packageFullName;
    }
    
    @XmlAttribute(name = "PackageFullName")
    public void setPackageFullName(final String packageFullName) {
        this.packageFullName = packageFullName;
    }
    
    public String getVersion() {
        return this.version;
    }
    
    @XmlAttribute(name = "Version")
    public void setVersion(final String version) {
        this.version = version;
    }
    
    public String getPublisher() {
        return this.publisher;
    }
    
    @XmlAttribute(name = "Publisher")
    public void setPublisher(final String publisher) {
        this.publisher = publisher;
    }
    
    public String getArchitecture() {
        return this.architecture;
    }
    
    @XmlAttribute(name = "Architecture")
    public void setArchitecture(final String architecture) {
        this.architecture = architecture;
    }
    
    public String getUsers() {
        return this.users;
    }
    
    @XmlAttribute(name = "Users")
    public void setUsers(final String users) {
        this.users = users;
    }
    
    public String getInstallLocation() {
        return this.installLocation;
    }
    
    @XmlAttribute(name = "InstallLocation")
    public void setInstallLocation(final String installLocation) {
        this.installLocation = installLocation;
    }
    
    public String getInstallDate() {
        return this.installDate;
    }
    
    @XmlAttribute(name = "InstallDate")
    public void setInstallDate(final String installDate) {
        this.installDate = installDate;
    }
    
    public String getResourceID() {
        return this.resourceID;
    }
    
    @XmlAttribute(name = "ResourceID")
    public void setResourceID(final String resourceID) {
        this.resourceID = resourceID;
    }
    
    public String getIsBundle() {
        return this.isBundle;
    }
    
    @XmlAttribute(name = "IsBundle")
    public void setIsBundle(final String isBundle) {
        this.isBundle = isBundle;
    }
    
    public String getIsFramework() {
        return this.isFramework;
    }
    
    @XmlAttribute(name = "IsFramework")
    public void setIsFramework(final String isFramework) {
        this.isFramework = isFramework;
    }
    
    public String getIsProvisioned() {
        return this.isProvisioned;
    }
    
    @XmlAttribute(name = "IsProvisioned")
    public void setIsProvisioned(final String isProvisioned) {
        this.isProvisioned = isProvisioned;
    }
    
    public String getPackageStatus() {
        return this.packageStatus;
    }
    
    @XmlAttribute(name = "PackageStatus")
    public void setPackageStatus(final String packageStatus) {
        this.packageStatus = packageStatus;
    }
    
    public String getPackageFamilyName() {
        return this.packageFamilyName;
    }
    
    @XmlAttribute(name = "PackageFamilyName")
    public void setPackageFamilyName(final String packageFamilyName) {
        this.packageFamilyName = packageFamilyName;
    }
    
    @Override
    public String toString() {
        return this.packageFamilyName + "," + this.packageFullName + "," + this.name + "," + this.version + "," + this.publisher + "," + this.architecture + "," + this.users + "," + this.installLocation + "," + this.installDate + "," + this.resourceID + "," + this.isBundle + "," + this.isFramework + "," + this.isProvisioned + "," + this.packageStatus;
    }
}
