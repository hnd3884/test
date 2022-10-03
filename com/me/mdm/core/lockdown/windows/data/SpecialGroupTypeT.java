package com.me.mdm.core.lockdown.windows.data;

import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "specialGroupType_t")
@XmlEnum
public enum SpecialGroupTypeT
{
    @XmlEnumValue("Visitor")
    VISITOR("Visitor");
    
    private final String value;
    
    private SpecialGroupTypeT(final String v) {
        this.value = v;
    }
    
    public String value() {
        return this.value;
    }
    
    public static SpecialGroupTypeT fromValue(final String v) {
        for (final SpecialGroupTypeT c : values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
