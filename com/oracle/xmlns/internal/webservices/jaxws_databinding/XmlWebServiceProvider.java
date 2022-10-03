package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.ws.WebServiceProvider;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "web-service-provider")
public class XmlWebServiceProvider implements WebServiceProvider
{
    @XmlAttribute(name = "targetNamespace")
    protected String targetNamespace;
    @XmlAttribute(name = "serviceName")
    protected String serviceName;
    @XmlAttribute(name = "portName")
    protected String portName;
    @XmlAttribute(name = "wsdlLocation")
    protected String wsdlLocation;
    
    public String getTargetNamespace() {
        return this.targetNamespace;
    }
    
    public void setTargetNamespace(final String value) {
        this.targetNamespace = value;
    }
    
    public String getServiceName() {
        return this.serviceName;
    }
    
    public void setServiceName(final String value) {
        this.serviceName = value;
    }
    
    public String getPortName() {
        return this.portName;
    }
    
    public void setPortName(final String value) {
        this.portName = value;
    }
    
    public String getWsdlLocation() {
        return this.wsdlLocation;
    }
    
    public void setWsdlLocation(final String value) {
        this.wsdlLocation = value;
    }
    
    @Override
    public String wsdlLocation() {
        return Util.nullSafe(this.wsdlLocation);
    }
    
    @Override
    public String serviceName() {
        return Util.nullSafe(this.serviceName);
    }
    
    @Override
    public String targetNamespace() {
        return Util.nullSafe(this.targetNamespace);
    }
    
    @Override
    public String portName() {
        return Util.nullSafe(this.portName);
    }
    
    @Override
    public Class<? extends Annotation> annotationType() {
        return WebServiceProvider.class;
    }
}
