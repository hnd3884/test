package com.me.mdm.core.lockdown.windows.data;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "autologon_account_t")
public class AutologonAccountT
{
    @XmlAttribute(name = "HiddenId")
    protected String hiddenId;
    
    public String getHiddenId() {
        if (this.hiddenId == null) {
            return "{74331115-F68A-4DF9-8D2C-52BA2CE2ADB1}";
        }
        return this.hiddenId;
    }
    
    public void setHiddenId(final String value) {
        this.hiddenId = value;
    }
}
