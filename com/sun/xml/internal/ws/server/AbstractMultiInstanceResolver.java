package com.sun.xml.internal.ws.server;

import com.sun.xml.internal.ws.api.server.InstanceResolver;
import javax.annotation.PreDestroy;
import java.lang.annotation.Annotation;
import javax.annotation.PostConstruct;
import com.sun.xml.internal.ws.api.server.ResourceInjector;
import java.lang.reflect.Method;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.api.server.WSWebServiceContext;
import com.sun.xml.internal.ws.api.server.AbstractInstanceResolver;

public abstract class AbstractMultiInstanceResolver<T> extends AbstractInstanceResolver<T>
{
    protected final Class<T> clazz;
    private WSWebServiceContext webServiceContext;
    protected WSEndpoint owner;
    private final Method postConstructMethod;
    private final Method preDestroyMethod;
    private ResourceInjector resourceInjector;
    
    public AbstractMultiInstanceResolver(final Class<T> clazz) {
        this.clazz = clazz;
        this.postConstructMethod = this.findAnnotatedMethod(clazz, PostConstruct.class);
        this.preDestroyMethod = this.findAnnotatedMethod(clazz, PreDestroy.class);
    }
    
    protected final void prepare(final T t) {
        assert this.webServiceContext != null;
        this.resourceInjector.inject(this.webServiceContext, t);
        AbstractInstanceResolver.invokeMethod(this.postConstructMethod, t, new Object[0]);
    }
    
    protected final T create() {
        final T t = InstanceResolver.createNewInstance(this.clazz);
        this.prepare(t);
        return t;
    }
    
    @Override
    public void start(final WSWebServiceContext wsc, final WSEndpoint endpoint) {
        this.resourceInjector = AbstractInstanceResolver.getResourceInjector(endpoint);
        this.webServiceContext = wsc;
        this.owner = endpoint;
    }
    
    protected final void dispose(final T instance) {
        AbstractInstanceResolver.invokeMethod(this.preDestroyMethod, instance, new Object[0]);
    }
}
