package com.sun.xml.internal.ws.api.wsdl.writer;

import com.sun.xml.internal.ws.api.model.CheckedException;
import com.sun.xml.internal.ws.api.model.JavaMethod;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.txw2.TypedXmlWriter;

public abstract class WSDLGeneratorExtension
{
    @Deprecated
    public void start(@NotNull final TypedXmlWriter root, @NotNull final SEIModel model, @NotNull final WSBinding binding, @NotNull final Container container) {
    }
    
    public void end(@NotNull final WSDLGenExtnContext ctxt) {
    }
    
    public void start(final WSDLGenExtnContext ctxt) {
    }
    
    public void addDefinitionsExtension(final TypedXmlWriter definitions) {
    }
    
    public void addServiceExtension(final TypedXmlWriter service) {
    }
    
    public void addPortExtension(final TypedXmlWriter port) {
    }
    
    public void addPortTypeExtension(final TypedXmlWriter portType) {
    }
    
    public void addBindingExtension(final TypedXmlWriter binding) {
    }
    
    public void addOperationExtension(final TypedXmlWriter operation, final JavaMethod method) {
    }
    
    public void addBindingOperationExtension(final TypedXmlWriter operation, final JavaMethod method) {
    }
    
    public void addInputMessageExtension(final TypedXmlWriter message, final JavaMethod method) {
    }
    
    public void addOutputMessageExtension(final TypedXmlWriter message, final JavaMethod method) {
    }
    
    public void addOperationInputExtension(final TypedXmlWriter input, final JavaMethod method) {
    }
    
    public void addOperationOutputExtension(final TypedXmlWriter output, final JavaMethod method) {
    }
    
    public void addBindingOperationInputExtension(final TypedXmlWriter input, final JavaMethod method) {
    }
    
    public void addBindingOperationOutputExtension(final TypedXmlWriter output, final JavaMethod method) {
    }
    
    public void addBindingOperationFaultExtension(final TypedXmlWriter fault, final JavaMethod method, final CheckedException ce) {
    }
    
    public void addFaultMessageExtension(final TypedXmlWriter message, final JavaMethod method, final CheckedException ce) {
    }
    
    public void addOperationFaultExtension(final TypedXmlWriter fault, final JavaMethod method, final CheckedException ce) {
    }
}
