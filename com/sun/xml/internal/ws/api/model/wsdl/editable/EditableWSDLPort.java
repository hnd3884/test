package com.sun.xml.internal.ws.api.model.wsdl.editable;

import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundPortType;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLService;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.EndpointAddress;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;

public interface EditableWSDLPort extends WSDLPort
{
    @NotNull
    EditableWSDLBoundPortType getBinding();
    
    @NotNull
    EditableWSDLService getOwner();
    
    void setAddress(final EndpointAddress p0);
    
    void setEPR(@NotNull final WSEndpointReference p0);
    
    void freeze(final EditableWSDLModel p0);
}
