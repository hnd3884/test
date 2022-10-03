package com.sun.xml.internal.ws.api.model.wsdl;

import javax.xml.namespace.QName;
import com.sun.istack.internal.NotNull;

public interface WSDLOutput extends WSDLObject, WSDLExtensible
{
    String getName();
    
    WSDLMessage getMessage();
    
    String getAction();
    
    @NotNull
    WSDLOperation getOperation();
    
    @NotNull
    QName getQName();
    
    boolean isDefaultAction();
}
