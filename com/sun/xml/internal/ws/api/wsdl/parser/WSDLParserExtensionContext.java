package com.sun.xml.internal.ws.api.wsdl.parser;

import com.sun.xml.internal.ws.api.policy.PolicyResolver;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLModel;

public interface WSDLParserExtensionContext
{
    boolean isClientSide();
    
    EditableWSDLModel getWSDLModel();
    
    @NotNull
    Container getContainer();
    
    @NotNull
    PolicyResolver getPolicyResolver();
}
