package com.me.mdm.core.windows.xmlbeans;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Settings")
class CustomizationSettings
{
    private Customization customization;
    private String xmlNameSpace;
    
    public Customization getCustomization() {
        return this.customization;
    }
    
    @XmlElement(name = "Customizations")
    public void setCustomization(final Customization customization) {
        this.customization = customization;
    }
    
    public String getXmlNameSpace() {
        return this.xmlNameSpace;
    }
    
    @XmlAttribute(name = "xmlns")
    public void setXmlNameSpace(final String xmlNameSpace) {
        this.xmlNameSpace = xmlNameSpace;
    }
}
