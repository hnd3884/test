package com.me.mdm.core.lockdown.windows.data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {})
@XmlRootElement(name = "AssignedAccessConfiguration")
public class AssignedAccessConfiguration
{
    @XmlElement(name = "Profiles", required = true)
    protected ProfileListT profiles;
    @XmlElement(name = "Configs", required = true)
    protected ConfigListT configs;
    
    public ProfileListT getProfiles() {
        return this.profiles;
    }
    
    public void setProfiles(final ProfileListT value) {
        this.profiles = value;
    }
    
    public ConfigListT getConfigs() {
        return this.configs;
    }
    
    public void setConfigs(final ConfigListT value) {
        this.configs = value;
    }
}
