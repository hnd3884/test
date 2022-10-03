package com.me.mdm.core.lockdown.windows.data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "config_t", propOrder = { "account", "autoLogonAccount", "userGroup", "specialGroup", "defaultProfile" })
public class ConfigT
{
    @XmlElement(name = "Account")
    protected String account;
    @XmlElement(name = "AutoLogonAccount")
    protected AutologonAccountT autoLogonAccount;
    @XmlElement(name = "UserGroup")
    protected GroupT userGroup;
    @XmlElement(name = "SpecialGroup")
    protected SpecialGroupT specialGroup;
    @XmlElement(name = "DefaultProfile", required = true)
    protected ProfileIdT defaultProfile;
    
    public String getAccount() {
        return this.account;
    }
    
    public void setAccount(final String value) {
        this.account = value;
    }
    
    public AutologonAccountT getAutoLogonAccount() {
        return this.autoLogonAccount;
    }
    
    public void setAutoLogonAccount(final AutologonAccountT value) {
        this.autoLogonAccount = value;
    }
    
    public GroupT getUserGroup() {
        return this.userGroup;
    }
    
    public void setUserGroup(final GroupT value) {
        this.userGroup = value;
    }
    
    public SpecialGroupT getSpecialGroup() {
        return this.specialGroup;
    }
    
    public void setSpecialGroup(final SpecialGroupT value) {
        this.specialGroup = value;
    }
    
    public ProfileIdT getDefaultProfile() {
        return this.defaultProfile;
    }
    
    public void setDefaultProfile(final ProfileIdT value) {
        this.defaultProfile = value;
    }
}
