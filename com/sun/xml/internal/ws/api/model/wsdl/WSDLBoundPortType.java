package com.sun.xml.internal.ws.api.model.wsdl;

import com.sun.xml.internal.ws.api.model.ParameterBinding;
import javax.jws.WebParam;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.BindingID;
import javax.jws.soap.SOAPBinding;
import com.sun.istack.internal.NotNull;
import javax.xml.namespace.QName;

public interface WSDLBoundPortType extends WSDLFeaturedObject, WSDLExtensible
{
    QName getName();
    
    @NotNull
    WSDLModel getOwner();
    
    WSDLBoundOperation get(final QName p0);
    
    QName getPortTypeName();
    
    WSDLPortType getPortType();
    
    Iterable<? extends WSDLBoundOperation> getBindingOperations();
    
    @NotNull
    SOAPBinding.Style getStyle();
    
    BindingID getBindingId();
    
    @Nullable
    WSDLBoundOperation getOperation(final String p0, final String p1);
    
    ParameterBinding getBinding(final QName p0, final String p1, final WebParam.Mode p2);
}
