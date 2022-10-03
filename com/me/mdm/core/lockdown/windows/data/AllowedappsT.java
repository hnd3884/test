package com.me.mdm.core.lockdown.windows.data;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "allowedapps_t", propOrder = { "app" })
public class AllowedappsT
{
    @XmlElement(name = "App", required = true)
    public List<AppT> app;
    
    public List<AppT> getApp() {
        if (this.app == null) {
            this.app = new ArrayList<AppT>();
        }
        return this.app;
    }
}
