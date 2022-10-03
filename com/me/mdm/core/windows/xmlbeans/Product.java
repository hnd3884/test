package com.me.mdm.core.windows.xmlbeans;

import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Product")
class Product
{
    private String version;
    private Download download;
    private List<String> fileHashList;
    private Enforcement enforcement;
    
    public Download getDownload() {
        return this.download;
    }
    
    @XmlElement(name = "Download")
    public void setDownload(final Download download) {
        this.download = download;
    }
    
    public String getVersion() {
        return this.version;
    }
    
    @XmlAttribute(name = "Version")
    public void setVersion(final String version) {
        this.version = version;
    }
    
    public List<String> getFileHashList() {
        return this.fileHashList;
    }
    
    @XmlElementWrapper(name = "Validation")
    @XmlElement(name = "FileHash")
    public void setFileHashList(final List<String> fileHashList) {
        this.fileHashList = fileHashList;
    }
    
    public Enforcement getEnforcement() {
        return this.enforcement;
    }
    
    @XmlElement(name = "Enforcement")
    public void setEnforcement(final Enforcement enforcement) {
        this.enforcement = enforcement;
    }
}
