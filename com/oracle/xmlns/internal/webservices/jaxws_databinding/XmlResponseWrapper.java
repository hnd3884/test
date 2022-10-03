package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.ws.ResponseWrapper;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "response-wrapper")
public class XmlResponseWrapper implements ResponseWrapper
{
    @XmlAttribute(name = "local-name")
    protected String localName;
    @XmlAttribute(name = "target-namespace")
    protected String targetNamespace;
    @XmlAttribute(name = "class-name")
    protected String className;
    @XmlAttribute(name = "part-name")
    protected String partName;
    
    public String getLocalName() {
        if (this.localName == null) {
            return "";
        }
        return this.localName;
    }
    
    public void setLocalName(final String value) {
        this.localName = value;
    }
    
    public String getTargetNamespace() {
        if (this.targetNamespace == null) {
            return "";
        }
        return this.targetNamespace;
    }
    
    public void setTargetNamespace(final String value) {
        this.targetNamespace = value;
    }
    
    public String getClassName() {
        if (this.className == null) {
            return "";
        }
        return this.className;
    }
    
    public void setClassName(final String value) {
        this.className = value;
    }
    
    public String getPartName() {
        return this.partName;
    }
    
    public void setPartName(final String partName) {
        this.partName = partName;
    }
    
    @Override
    public String localName() {
        return Util.nullSafe(this.localName);
    }
    
    @Override
    public String targetNamespace() {
        return Util.nullSafe(this.targetNamespace);
    }
    
    @Override
    public String className() {
        return Util.nullSafe(this.className);
    }
    
    @Override
    public String partName() {
        return Util.nullSafe(this.partName);
    }
    
    @Override
    public Class<? extends Annotation> annotationType() {
        return ResponseWrapper.class;
    }
}
