package com.sun.xml.internal.ws.api.model.wsdl;

import javax.xml.namespace.QName;

public interface WSDLPortType extends WSDLObject, WSDLExtensible
{
    QName getName();
    
    WSDLOperation get(final String p0);
    
    Iterable<? extends WSDLOperation> getOperations();
}
