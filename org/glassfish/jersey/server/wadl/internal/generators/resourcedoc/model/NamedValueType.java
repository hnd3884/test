package org.glassfish.jersey.server.wadl.internal.generators.resourcedoc.model;

import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "namedValue", propOrder = {})
public class NamedValueType
{
    private String name;
    private String value;
    
    public NamedValueType() {
    }
    
    public NamedValueType(final String name, final String value) {
        this.name = name;
        this.value = value;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public void setValue(final String value) {
        this.value = value;
    }
}
