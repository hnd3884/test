package com.me.mdm.core.lockdown.windows.data;

import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "groupType_t")
@XmlEnum
public enum GroupTypeT
{
    @XmlEnumValue("LocalGroup")
    LOCAL_GROUP("LocalGroup"), 
    @XmlEnumValue("ActiveDirectoryGroup")
    ACTIVE_DIRECTORY_GROUP("ActiveDirectoryGroup"), 
    @XmlEnumValue("AzureActiveDirectoryGroup")
    AZURE_ACTIVE_DIRECTORY_GROUP("AzureActiveDirectoryGroup");
    
    private final String value;
    
    private GroupTypeT(final String v) {
        this.value = v;
    }
    
    public String value() {
        return this.value;
    }
    
    public static GroupTypeT fromValue(final String v) {
        for (final GroupTypeT c : values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
