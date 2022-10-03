package com.sun.xml.internal.ws.api.model.wsdl;

import javax.xml.namespace.QName;
import com.sun.istack.internal.NotNull;

public interface WSDLFault extends WSDLObject, WSDLExtensible
{
    String getName();
    
    WSDLMessage getMessage();
    
    @NotNull
    WSDLOperation getOperation();
    
    @NotNull
    QName getQName();
    
    String getAction();
    
    boolean isDefaultAction();
}
