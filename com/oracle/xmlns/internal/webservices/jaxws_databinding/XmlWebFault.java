package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.ws.WebFault;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "web-fault")
public class XmlWebFault implements WebFault
{
    @XmlAttribute(name = "name")
    protected String name;
    @XmlAttribute(name = "targetNamespace")
    protected String targetNamespace;
    @XmlAttribute(name = "faultBean")
    protected String faultBean;
    @XmlAttribute(name = "messageName")
    protected String messageName;
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String value) {
        this.name = value;
    }
    
    public String getTargetNamespace() {
        return this.targetNamespace;
    }
    
    public void setTargetNamespace(final String value) {
        this.targetNamespace = value;
    }
    
    public String getFaultBean() {
        return this.faultBean;
    }
    
    public void setFaultBean(final String value) {
        this.faultBean = value;
    }
    
    @Override
    public String name() {
        return Util.nullSafe(this.name);
    }
    
    @Override
    public String targetNamespace() {
        return Util.nullSafe(this.targetNamespace);
    }
    
    @Override
    public String faultBean() {
        return Util.nullSafe(this.faultBean);
    }
    
    @Override
    public String messageName() {
        return Util.nullSafe(this.messageName);
    }
    
    @Override
    public Class<? extends Annotation> annotationType() {
        return WebFault.class;
    }
}
