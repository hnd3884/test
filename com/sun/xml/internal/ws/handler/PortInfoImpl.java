package com.sun.xml.internal.ws.handler;

import javax.xml.namespace.QName;
import com.sun.xml.internal.ws.api.BindingID;
import javax.xml.ws.handler.PortInfo;

public class PortInfoImpl implements PortInfo
{
    private BindingID bindingId;
    private QName portName;
    private QName serviceName;
    
    public PortInfoImpl(final BindingID bindingId, final QName portName, final QName serviceName) {
        if (bindingId == null) {
            throw new RuntimeException("bindingId cannot be null");
        }
        if (portName == null) {
            throw new RuntimeException("portName cannot be null");
        }
        if (serviceName == null) {
            throw new RuntimeException("serviceName cannot be null");
        }
        this.bindingId = bindingId;
        this.portName = portName;
        this.serviceName = serviceName;
    }
    
    @Override
    public String getBindingID() {
        return this.bindingId.toString();
    }
    
    @Override
    public QName getPortName() {
        return this.portName;
    }
    
    @Override
    public QName getServiceName() {
        return this.serviceName;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof PortInfo) {
            final PortInfo info = (PortInfo)obj;
            if (this.bindingId.toString().equals(info.getBindingID()) && this.portName.equals(info.getPortName()) && this.serviceName.equals(info.getServiceName())) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.bindingId.hashCode();
    }
}
