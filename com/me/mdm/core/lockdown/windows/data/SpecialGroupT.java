package com.me.mdm.core.lockdown.windows.data;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "specialGroup_t")
public class SpecialGroupT
{
    @XmlAttribute(name = "Name", required = true)
    protected SpecialGroupTypeT name;
    
    public SpecialGroupTypeT getName() {
        return this.name;
    }
    
    public void setName(final SpecialGroupTypeT value) {
        this.name = value;
    }
}
