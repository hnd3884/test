package com.sun.xml.internal.ws.wsdl.writer;

import com.sun.xml.internal.ws.api.model.CheckedException;
import com.sun.xml.internal.ws.api.model.JavaMethod;
import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.wsdl.writer.WSDLGenExtnContext;
import com.sun.xml.internal.ws.api.wsdl.writer.WSDLGeneratorExtension;

final class WSDLGeneratorExtensionFacade extends WSDLGeneratorExtension
{
    private final WSDLGeneratorExtension[] extensions;
    
    WSDLGeneratorExtensionFacade(final WSDLGeneratorExtension... extensions) {
        assert extensions != null;
        this.extensions = extensions;
    }
    
    @Override
    public void start(final WSDLGenExtnContext ctxt) {
        for (final WSDLGeneratorExtension e : this.extensions) {
            e.start(ctxt);
        }
    }
    
    @Override
    public void end(@NotNull final WSDLGenExtnContext ctxt) {
        for (final WSDLGeneratorExtension e : this.extensions) {
            e.end(ctxt);
        }
    }
    
    @Override
    public void addDefinitionsExtension(final TypedXmlWriter definitions) {
        for (final WSDLGeneratorExtension e : this.extensions) {
            e.addDefinitionsExtension(definitions);
        }
    }
    
    @Override
    public void addServiceExtension(final TypedXmlWriter service) {
        for (final WSDLGeneratorExtension e : this.extensions) {
            e.addServiceExtension(service);
        }
    }
    
    @Override
    public void addPortExtension(final TypedXmlWriter port) {
        for (final WSDLGeneratorExtension e : this.extensions) {
            e.addPortExtension(port);
        }
    }
    
    @Override
    public void addPortTypeExtension(final TypedXmlWriter portType) {
        for (final WSDLGeneratorExtension e : this.extensions) {
            e.addPortTypeExtension(portType);
        }
    }
    
    @Override
    public void addBindingExtension(final TypedXmlWriter binding) {
        for (final WSDLGeneratorExtension e : this.extensions) {
            e.addBindingExtension(binding);
        }
    }
    
    @Override
    public void addOperationExtension(final TypedXmlWriter operation, final JavaMethod method) {
        for (final WSDLGeneratorExtension e : this.extensions) {
            e.addOperationExtension(operation, method);
        }
    }
    
    @Override
    public void addBindingOperationExtension(final TypedXmlWriter operation, final JavaMethod method) {
        for (final WSDLGeneratorExtension e : this.extensions) {
            e.addBindingOperationExtension(operation, method);
        }
    }
    
    @Override
    public void addInputMessageExtension(final TypedXmlWriter message, final JavaMethod method) {
        for (final WSDLGeneratorExtension e : this.extensions) {
            e.addInputMessageExtension(message, method);
        }
    }
    
    @Override
    public void addOutputMessageExtension(final TypedXmlWriter message, final JavaMethod method) {
        for (final WSDLGeneratorExtension e : this.extensions) {
            e.addOutputMessageExtension(message, method);
        }
    }
    
    @Override
    public void addOperationInputExtension(final TypedXmlWriter input, final JavaMethod method) {
        for (final WSDLGeneratorExtension e : this.extensions) {
            e.addOperationInputExtension(input, method);
        }
    }
    
    @Override
    public void addOperationOutputExtension(final TypedXmlWriter output, final JavaMethod method) {
        for (final WSDLGeneratorExtension e : this.extensions) {
            e.addOperationOutputExtension(output, method);
        }
    }
    
    @Override
    public void addBindingOperationInputExtension(final TypedXmlWriter input, final JavaMethod method) {
        for (final WSDLGeneratorExtension e : this.extensions) {
            e.addBindingOperationInputExtension(input, method);
        }
    }
    
    @Override
    public void addBindingOperationOutputExtension(final TypedXmlWriter output, final JavaMethod method) {
        for (final WSDLGeneratorExtension e : this.extensions) {
            e.addBindingOperationOutputExtension(output, method);
        }
    }
    
    @Override
    public void addBindingOperationFaultExtension(final TypedXmlWriter fault, final JavaMethod method, final CheckedException ce) {
        for (final WSDLGeneratorExtension e : this.extensions) {
            e.addBindingOperationFaultExtension(fault, method, ce);
        }
    }
    
    @Override
    public void addFaultMessageExtension(final TypedXmlWriter message, final JavaMethod method, final CheckedException ce) {
        for (final WSDLGeneratorExtension e : this.extensions) {
            e.addFaultMessageExtension(message, method, ce);
        }
    }
    
    @Override
    public void addOperationFaultExtension(final TypedXmlWriter fault, final JavaMethod method, final CheckedException ce) {
        for (final WSDLGeneratorExtension e : this.extensions) {
            e.addOperationFaultExtension(fault, method, ce);
        }
    }
}
