package com.me.mdm.core.windows.xmlbeans;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Customizations")
class Customization
{
    private CommonPayloadCollection commonSettings;
    
    public CommonPayloadCollection getCommonSettings() {
        return this.commonSettings;
    }
    
    @XmlElement(name = "Common")
    public void setCommonSettings(final CommonPayloadCollection commonSettings) {
        this.commonSettings = commonSettings;
    }
}
