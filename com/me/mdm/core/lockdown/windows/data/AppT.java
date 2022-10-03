package com.me.mdm.core.lockdown.windows.data;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "app_t")
public class AppT
{
    @XmlAttribute(name = "AppUserModelId")
    protected String appUserModelId;
    @XmlAttribute(name = "DesktopAppPath")
    protected String desktopAppPath;
    
    public String getAppUserModelId() {
        return this.appUserModelId;
    }
    
    public void setAppUserModelId(final String value) {
        this.appUserModelId = value;
    }
    
    public String getDesktopAppPath() {
        return this.desktopAppPath;
    }
    
    public void setDesktopAppPath(final String value) {
        this.desktopAppPath = value;
    }
}
