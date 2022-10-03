package com.sun.xml.internal.ws.api.model.wsdl.editable;

import com.sun.xml.internal.ws.api.model.wsdl.WSDLInput;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLOutput;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLFault;
import javax.xml.namespace.QName;
import com.sun.istack.internal.Nullable;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLOperation;

public interface EditableWSDLOperation extends WSDLOperation
{
    @NotNull
    EditableWSDLInput getInput();
    
    void setInput(final EditableWSDLInput p0);
    
    @Nullable
    EditableWSDLOutput getOutput();
    
    void setOutput(final EditableWSDLOutput p0);
    
    Iterable<? extends EditableWSDLFault> getFaults();
    
    void addFault(final EditableWSDLFault p0);
    
    @Nullable
    EditableWSDLFault getFault(final QName p0);
    
    void setParameterOrder(final String p0);
    
    void freeze(final EditableWSDLModel p0);
}
