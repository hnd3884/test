package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.ws.WebServiceClient;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "web-service-client")
public class XmlWebServiceClient implements WebServiceClient
{
    @XmlAttribute(name = "name")
    protected String name;
    @XmlAttribute(name = "targetNamespace")
    protected String targetNamespace;
    @XmlAttribute(name = "wsdlLocation")
    protected String wsdlLocation;
    
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
    
    public String getWsdlLocation() {
        return this.wsdlLocation;
    }
    
    public void setWsdlLocation(final String value) {
        this.wsdlLocation = value;
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
    public String wsdlLocation() {
        return Util.nullSafe(this.wsdlLocation);
    }
    
    @Override
    public Class<? extends Annotation> annotationType() {
        return WebServiceClient.class;
    }
}
