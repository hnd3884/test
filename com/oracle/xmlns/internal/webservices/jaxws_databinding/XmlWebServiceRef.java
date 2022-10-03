package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import java.lang.annotation.Annotation;
import javax.xml.ws.Service;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.ws.WebServiceRef;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "web-service-ref")
public class XmlWebServiceRef implements WebServiceRef
{
    @XmlAttribute(name = "name")
    protected String name;
    @XmlAttribute(name = "type")
    protected String type;
    @XmlAttribute(name = "mappedName")
    protected String mappedName;
    @XmlAttribute(name = "value")
    protected String value;
    @XmlAttribute(name = "wsdlLocation")
    protected String wsdlLocation;
    @XmlAttribute(name = "lookup")
    protected String lookup;
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String value) {
        this.name = value;
    }
    
    public String getType() {
        return this.type;
    }
    
    public void setType(final String value) {
        this.type = value;
    }
    
    public String getMappedName() {
        return this.mappedName;
    }
    
    public void setMappedName(final String value) {
        this.mappedName = value;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public void setValue(final String value) {
        this.value = value;
    }
    
    public String getWsdlLocation() {
        return this.wsdlLocation;
    }
    
    public void setWsdlLocation(final String value) {
        this.wsdlLocation = value;
    }
    
    public String getLookup() {
        return this.lookup;
    }
    
    public void setLookup(final String lookup) {
        this.lookup = lookup;
    }
    
    @Override
    public String name() {
        return Util.nullSafe(this.name);
    }
    
    @Override
    public Class<?> type() {
        if (this.type == null) {
            return Object.class;
        }
        return Util.findClass(this.type);
    }
    
    @Override
    public String mappedName() {
        return Util.nullSafe(this.mappedName);
    }
    
    @Override
    public Class<? extends Service> value() {
        if (this.value == null) {
            return Service.class;
        }
        return (Class<? extends Service>)Util.findClass(this.value);
    }
    
    @Override
    public String wsdlLocation() {
        return Util.nullSafe(this.wsdlLocation);
    }
    
    @Override
    public String lookup() {
        return Util.nullSafe(this.lookup);
    }
    
    @Override
    public Class<? extends Annotation> annotationType() {
        return WebServiceRef.class;
    }
}
