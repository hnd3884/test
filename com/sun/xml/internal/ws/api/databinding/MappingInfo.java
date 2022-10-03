package com.sun.xml.internal.ws.api.databinding;

import javax.xml.namespace.QName;
import com.sun.xml.internal.ws.api.BindingID;

public class MappingInfo
{
    protected String targetNamespace;
    protected String databindingMode;
    protected SoapBodyStyle soapBodyStyle;
    protected BindingID bindingID;
    protected QName serviceName;
    protected QName portName;
    protected String defaultSchemaNamespaceSuffix;
    
    public String getTargetNamespace() {
        return this.targetNamespace;
    }
    
    public void setTargetNamespace(final String targetNamespace) {
        this.targetNamespace = targetNamespace;
    }
    
    public String getDatabindingMode() {
        return this.databindingMode;
    }
    
    public void setDatabindingMode(final String databindingMode) {
        this.databindingMode = databindingMode;
    }
    
    public SoapBodyStyle getSoapBodyStyle() {
        return this.soapBodyStyle;
    }
    
    public void setSoapBodyStyle(final SoapBodyStyle soapBodyStyle) {
        this.soapBodyStyle = soapBodyStyle;
    }
    
    public BindingID getBindingID() {
        return this.bindingID;
    }
    
    public void setBindingID(final BindingID bindingID) {
        this.bindingID = bindingID;
    }
    
    public QName getServiceName() {
        return this.serviceName;
    }
    
    public void setServiceName(final QName serviceName) {
        this.serviceName = serviceName;
    }
    
    public QName getPortName() {
        return this.portName;
    }
    
    public void setPortName(final QName portName) {
        this.portName = portName;
    }
    
    public String getDefaultSchemaNamespaceSuffix() {
        return this.defaultSchemaNamespaceSuffix;
    }
    
    public void setDefaultSchemaNamespaceSuffix(final String defaultSchemaNamespaceSuffix) {
        this.defaultSchemaNamespaceSuffix = defaultSchemaNamespaceSuffix;
    }
}
