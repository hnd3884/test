package com.sun.xml.internal.ws.wsdl.parser;

import com.sun.xml.internal.ws.api.policy.PolicyResolver;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.xml.internal.ws.api.wsdl.parser.WSDLParserExtensionContext;

final class WSDLParserExtensionContextImpl implements WSDLParserExtensionContext
{
    private final boolean isClientSide;
    private final EditableWSDLModel wsdlModel;
    private final Container container;
    private final PolicyResolver policyResolver;
    
    protected WSDLParserExtensionContextImpl(final EditableWSDLModel model, final boolean isClientSide, final Container container, final PolicyResolver policyResolver) {
        this.wsdlModel = model;
        this.isClientSide = isClientSide;
        this.container = container;
        this.policyResolver = policyResolver;
    }
    
    @Override
    public boolean isClientSide() {
        return this.isClientSide;
    }
    
    @Override
    public EditableWSDLModel getWSDLModel() {
        return this.wsdlModel;
    }
    
    @Override
    public Container getContainer() {
        return this.container;
    }
    
    @Override
    public PolicyResolver getPolicyResolver() {
        return this.policyResolver;
    }
}
