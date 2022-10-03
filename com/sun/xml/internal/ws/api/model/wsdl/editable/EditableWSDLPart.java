package com.sun.xml.internal.ws.api.model.wsdl.editable;

import com.sun.xml.internal.ws.api.model.ParameterBinding;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPart;

public interface EditableWSDLPart extends WSDLPart
{
    void setBinding(final ParameterBinding p0);
    
    void setIndex(final int p0);
}
