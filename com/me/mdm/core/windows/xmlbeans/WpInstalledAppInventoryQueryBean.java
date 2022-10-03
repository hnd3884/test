package com.me.mdm.core.windows.xmlbeans;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Inventory")
public class WpInstalledAppInventoryQueryBean
{
    private String resultDataSet;
    private String appInstallionSourceFilter;
    private String packageTypeFilter;
    private String packageFamilyNameFilter;
    private String publisherNameFilter;
    
    public String getResultDataSet() {
        return this.resultDataSet;
    }
    
    @XmlAttribute(name = "Output")
    public void setResultDataSet(final String resultDataSet) {
        this.resultDataSet = resultDataSet;
    }
    
    public String getAppInstallionSourceFilter() {
        return this.appInstallionSourceFilter;
    }
    
    @XmlAttribute(name = "Source")
    public void setAppInstallionSourceFilter(final String appInstallionSourceFilter) {
        this.appInstallionSourceFilter = appInstallionSourceFilter;
    }
    
    public String getPackageTypeFilter() {
        return this.packageTypeFilter;
    }
    
    @XmlAttribute(name = "PackageTypeFilter")
    public void setPackageTypeFilter(final String packageTypeFilter) {
        this.packageTypeFilter = packageTypeFilter;
    }
    
    public String getPackageFamilyNameFilter() {
        return this.packageFamilyNameFilter;
    }
    
    @XmlAttribute(name = "PackageFamilyName")
    public void setPackageFamilyNameFilter(final String packageFamilyNameFilter) {
        this.packageFamilyNameFilter = packageFamilyNameFilter;
    }
    
    public String getPublisherNameFilter() {
        return this.publisherNameFilter;
    }
    
    @XmlAttribute(name = "Publisher")
    public void setPublisherNameFilter(final String publisherNameFilter) {
        this.publisherNameFilter = publisherNameFilter;
    }
    
    @Override
    public String toString() {
        return "<Inventory Output=\"" + this.resultDataSet + "\"  Source=\"" + this.appInstallionSourceFilter + "\"  PackageTypeFilter=\"" + this.packageTypeFilter + "\"  PackageFamilyName=\"" + this.packageFamilyNameFilter + "\"  Publisher=\"" + this.publisherNameFilter + "\" />";
    }
}
