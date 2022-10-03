package com.me.mdm.core.windows.xmlbeans;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Download")
class Download
{
    private List<String> contentURLList;
    
    public List<String> getContentURLList() {
        return this.contentURLList;
    }
    
    @XmlElementWrapper(name = "ContentURLList")
    @XmlElement(name = "ContentURL")
    public void setContentURLList(final List<String> contentURLList) {
        this.contentURLList = contentURLList;
    }
}
