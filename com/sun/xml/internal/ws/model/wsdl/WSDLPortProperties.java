package com.sun.xml.internal.ws.model.wsdl;

import javax.xml.namespace.QName;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;

public final class WSDLPortProperties extends WSDLProperties
{
    @NotNull
    private final WSDLPort port;
    
    public WSDLPortProperties(@NotNull final WSDLPort port) {
        this(port, null);
    }
    
    public WSDLPortProperties(@NotNull final WSDLPort port, @Nullable final SEIModel seiModel) {
        super(seiModel);
        this.port = port;
    }
    
    @Override
    public QName getWSDLService() {
        return this.port.getOwner().getName();
    }
    
    @Override
    public QName getWSDLPort() {
        return this.port.getName();
    }
    
    @Override
    public QName getWSDLPortType() {
        return this.port.getBinding().getPortTypeName();
    }
}
