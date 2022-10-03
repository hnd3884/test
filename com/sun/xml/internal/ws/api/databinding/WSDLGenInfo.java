package com.sun.xml.internal.ws.api.databinding;

import com.sun.xml.internal.ws.api.wsdl.writer.WSDLGeneratorExtension;
import com.sun.xml.internal.ws.api.server.Container;
import com.oracle.webservices.internal.api.databinding.WSDLResolver;

public class WSDLGenInfo
{
    WSDLResolver wsdlResolver;
    Container container;
    boolean inlineSchemas;
    boolean secureXmlProcessingDisabled;
    WSDLGeneratorExtension[] extensions;
    
    public WSDLResolver getWsdlResolver() {
        return this.wsdlResolver;
    }
    
    public void setWsdlResolver(final WSDLResolver wsdlResolver) {
        this.wsdlResolver = wsdlResolver;
    }
    
    public Container getContainer() {
        return this.container;
    }
    
    public void setContainer(final Container container) {
        this.container = container;
    }
    
    public boolean isInlineSchemas() {
        return this.inlineSchemas;
    }
    
    public void setInlineSchemas(final boolean inlineSchemas) {
        this.inlineSchemas = inlineSchemas;
    }
    
    public WSDLGeneratorExtension[] getExtensions() {
        if (this.extensions == null) {
            return new WSDLGeneratorExtension[0];
        }
        return this.extensions;
    }
    
    public void setExtensions(final WSDLGeneratorExtension[] extensions) {
        this.extensions = extensions;
    }
    
    public void setSecureXmlProcessingDisabled(final boolean secureXmlProcessingDisabled) {
        this.secureXmlProcessingDisabled = secureXmlProcessingDisabled;
    }
    
    public boolean isSecureXmlProcessingDisabled() {
        return this.secureXmlProcessingDisabled;
    }
}
