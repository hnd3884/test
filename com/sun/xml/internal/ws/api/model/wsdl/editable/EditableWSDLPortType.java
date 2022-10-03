package com.sun.xml.internal.ws.api.model.wsdl.editable;

import com.sun.xml.internal.ws.api.model.wsdl.WSDLOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPortType;

public interface EditableWSDLPortType extends WSDLPortType
{
    EditableWSDLOperation get(final String p0);
    
    Iterable<? extends EditableWSDLOperation> getOperations();
    
    void put(final String p0, final EditableWSDLOperation p1);
    
    void freeze();
}
