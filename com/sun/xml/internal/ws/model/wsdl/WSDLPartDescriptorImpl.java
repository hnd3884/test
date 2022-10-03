package com.sun.xml.internal.ws.model.wsdl;

import javax.xml.stream.XMLStreamReader;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLDescriptorKind;
import javax.xml.namespace.QName;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPartDescriptor;

public final class WSDLPartDescriptorImpl extends AbstractObjectImpl implements WSDLPartDescriptor
{
    private QName name;
    private WSDLDescriptorKind type;
    
    public WSDLPartDescriptorImpl(final XMLStreamReader xsr, final QName name, final WSDLDescriptorKind kind) {
        super(xsr);
        this.name = name;
        this.type = kind;
    }
    
    @Override
    public QName name() {
        return this.name;
    }
    
    @Override
    public WSDLDescriptorKind type() {
        return this.type;
    }
}
