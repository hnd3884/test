package com.me.mdm.core.lockdown.windows.data;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "config_list_t", propOrder = { "config" })
public class ConfigListT
{
    @XmlElement(name = "Config", required = true)
    public List<ConfigT> config;
    
    public List<ConfigT> getConfig() {
        if (this.config == null) {
            this.config = new ArrayList<ConfigT>();
        }
        return this.config;
    }
}
