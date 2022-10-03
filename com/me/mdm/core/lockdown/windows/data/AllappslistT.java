package com.me.mdm.core.lockdown.windows.data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "allappslist_t", propOrder = { "allowedApps" })
public class AllappslistT
{
    @XmlElement(name = "AllowedApps", required = true)
    protected AllowedappsT allowedApps;
    
    public AllowedappsT getAllowedApps() {
        return this.allowedApps;
    }
    
    public void setAllowedApps(final AllowedappsT value) {
        this.allowedApps = value;
    }
}
