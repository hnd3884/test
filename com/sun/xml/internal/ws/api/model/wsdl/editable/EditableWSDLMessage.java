package com.sun.xml.internal.ws.api.model.wsdl.editable;

import com.sun.xml.internal.ws.api.model.wsdl.WSDLMessage;

public interface EditableWSDLMessage extends WSDLMessage
{
    Iterable<? extends EditableWSDLPart> parts();
    
    void add(final EditableWSDLPart p0);
}
