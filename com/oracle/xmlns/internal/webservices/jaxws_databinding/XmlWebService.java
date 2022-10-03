package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.jws.WebService;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "web-service")
public class XmlWebService implements WebService
{
    @XmlAttribute(name = "endpoint-interface")
    protected String endpointInterface;
    @XmlAttribute(name = "name")
    protected String name;
    @XmlAttribute(name = "port-name")
    protected String portName;
    @XmlAttribute(name = "service-name")
    protected String serviceName;
    @XmlAttribute(name = "target-namespace")
    protected String targetNamespace;
    @XmlAttribute(name = "wsdl-location")
    protected String wsdlLocation;
    
    public String getEndpointInterface() {
        if (this.endpointInterface == null) {
            return "";
        }
        return this.endpointInterface;
    }
    
    public void setEndpointInterface(final String value) {
        this.endpointInterface = value;
    }
    
    public String getName() {
        if (this.name == null) {
            return "";
        }
        return this.name;
    }
    
    public void setName(final String value) {
        this.name = value;
    }
    
    public String getPortName() {
        if (this.portName == null) {
            return "";
        }
        return this.portName;
    }
    
    public void setPortName(final String value) {
        this.portName = value;
    }
    
    public String getServiceName() {
        if (this.serviceName == null) {
            return "";
        }
        return this.serviceName;
    }
    
    public void setServiceName(final String value) {
        this.serviceName = value;
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
    
    public String getWsdlLocation() {
        if (this.wsdlLocation == null) {
            return "";
        }
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
    public String serviceName() {
        return Util.nullSafe(this.serviceName);
    }
    
    @Override
    public String portName() {
        return Util.nullSafe(this.portName);
    }
    
    @Override
    public String wsdlLocation() {
        return Util.nullSafe(this.wsdlLocation);
    }
    
    @Override
    public String endpointInterface() {
        return Util.nullSafe(this.endpointInterface);
    }
    
    @Override
    public Class<? extends Annotation> annotationType() {
        return WebService.class;
    }
}
