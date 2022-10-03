package com.sun.xml.internal.ws.api.model.wsdl.editable;

import com.sun.xml.internal.ws.api.model.wsdl.WSDLModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.istack.internal.Nullable;
import javax.xml.namespace.QName;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLService;

public interface EditableWSDLService extends WSDLService
{
    @NotNull
    EditableWSDLModel getParent();
    
    EditableWSDLPort get(final QName p0);
    
    EditableWSDLPort getFirstPort();
    
    @Nullable
    EditableWSDLPort getMatchingPort(final QName p0);
    
    Iterable<? extends EditableWSDLPort> getPorts();
    
    void put(final QName p0, final EditableWSDLPort p1);
    
    void freeze(final EditableWSDLModel p0);
}
