package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "existing-annotations-type")
@XmlEnum
public enum ExistingAnnotationsType
{
    @XmlEnumValue("merge")
    MERGE("merge"), 
    @XmlEnumValue("ignore")
    IGNORE("ignore");
    
    private final String value;
    
    private ExistingAnnotationsType(final String v) {
        this.value = v;
    }
    
    public String value() {
        return this.value;
    }
    
    public static ExistingAnnotationsType fromValue(final String v) {
        for (final ExistingAnnotationsType c : values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
