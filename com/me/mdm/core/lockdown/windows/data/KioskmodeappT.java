package com.me.mdm.core.lockdown.windows.data;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "kioskmodeapp_t")
public class KioskmodeappT
{
    @XmlAttribute(name = "AppUserModelId")
    protected String appUserModelId;
    
    public String getAppUserModelId() {
        return this.appUserModelId;
    }
    
    public void setAppUserModelId(final String value) {
        this.appUserModelId = value;
    }
}
