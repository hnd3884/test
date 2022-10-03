package com.sun.xml.internal.ws.api.model.wsdl.editable;

import com.sun.xml.internal.ws.api.model.wsdl.WSDLMessage;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLOperation;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLInput;

public interface EditableWSDLInput extends WSDLInput
{
    EditableWSDLMessage getMessage();
    
    @NotNull
    EditableWSDLOperation getOperation();
    
    void setAction(final String p0);
    
    void setDefaultAction(final boolean p0);
    
    void freeze(final EditableWSDLModel p0);
}
