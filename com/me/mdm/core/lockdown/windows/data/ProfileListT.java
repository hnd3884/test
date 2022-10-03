package com.me.mdm.core.lockdown.windows.data;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "profile_list_t", propOrder = { "profile" })
public class ProfileListT
{
    @XmlElement(name = "Profile", required = true)
    public List<ProfileT> profile;
    
    public List<ProfileT> getProfile() {
        if (this.profile == null) {
            this.profile = new ArrayList<ProfileT>();
        }
        return this.profile;
    }
}
