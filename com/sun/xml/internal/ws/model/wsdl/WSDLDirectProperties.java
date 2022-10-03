package com.sun.xml.internal.ws.model.wsdl;

import com.sun.xml.internal.ws.api.model.SEIModel;
import javax.xml.namespace.QName;

public final class WSDLDirectProperties extends WSDLProperties
{
    private final QName serviceName;
    private final QName portName;
    
    public WSDLDirectProperties(final QName serviceName, final QName portName) {
        this(serviceName, portName, null);
    }
    
    public WSDLDirectProperties(final QName serviceName, final QName portName, final SEIModel seiModel) {
        super(seiModel);
        this.serviceName = serviceName;
        this.portName = portName;
    }
    
    @Override
    public QName getWSDLService() {
        return this.serviceName;
    }
    
    @Override
    public QName getWSDLPort() {
        return this.portName;
    }
    
    @Override
    public QName getWSDLPortType() {
        return null;
    }
}
