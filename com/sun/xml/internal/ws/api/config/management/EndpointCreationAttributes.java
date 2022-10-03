package com.sun.xml.internal.ws.api.config.management;

import org.xml.sax.EntityResolver;
import com.sun.xml.internal.ws.api.server.Invoker;

public class EndpointCreationAttributes
{
    private final boolean processHandlerAnnotation;
    private final Invoker invoker;
    private final EntityResolver entityResolver;
    private final boolean isTransportSynchronous;
    
    public EndpointCreationAttributes(final boolean processHandlerAnnotation, final Invoker invoker, final EntityResolver resolver, final boolean isTransportSynchronous) {
        this.processHandlerAnnotation = processHandlerAnnotation;
        this.invoker = invoker;
        this.entityResolver = resolver;
        this.isTransportSynchronous = isTransportSynchronous;
    }
    
    public boolean isProcessHandlerAnnotation() {
        return this.processHandlerAnnotation;
    }
    
    public Invoker getInvoker() {
        return this.invoker;
    }
    
    public EntityResolver getEntityResolver() {
        return this.entityResolver;
    }
    
    public boolean isTransportSynchronous() {
        return this.isTransportSynchronous;
    }
}
