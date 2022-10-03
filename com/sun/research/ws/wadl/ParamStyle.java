package com.sun.research.ws.wadl;

import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "ParamStyle")
@XmlEnum
public enum ParamStyle
{
    @XmlEnumValue("plain")
    PLAIN("plain"), 
    @XmlEnumValue("query")
    QUERY("query"), 
    @XmlEnumValue("matrix")
    MATRIX("matrix"), 
    @XmlEnumValue("header")
    HEADER("header"), 
    @XmlEnumValue("template")
    TEMPLATE("template");
    
    private final String value;
    
    private ParamStyle(final String v) {
        this.value = v;
    }
    
    public String value() {
        return this.value;
    }
    
    public static ParamStyle fromValue(final String v) {
        for (final ParamStyle c : values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
