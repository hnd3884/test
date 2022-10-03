package com.sun.xml.internal.ws.server;

import javax.annotation.PreDestroy;
import java.lang.annotation.Annotation;
import javax.annotation.PostConstruct;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.api.server.WSWebServiceContext;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.server.AbstractInstanceResolver;

public final class SingletonResolver<T> extends AbstractInstanceResolver<T>
{
    @NotNull
    private final T singleton;
    
    public SingletonResolver(@NotNull final T singleton) {
        this.singleton = singleton;
    }
    
    @NotNull
    @Override
    public T resolve(final Packet request) {
        return this.singleton;
    }
    
    @Override
    public void start(final WSWebServiceContext wsc, final WSEndpoint endpoint) {
        AbstractInstanceResolver.getResourceInjector(endpoint).inject(wsc, this.singleton);
        AbstractInstanceResolver.invokeMethod(this.findAnnotatedMethod(this.singleton.getClass(), PostConstruct.class), this.singleton, new Object[0]);
    }
    
    @Override
    public void dispose() {
        AbstractInstanceResolver.invokeMethod(this.findAnnotatedMethod(this.singleton.getClass(), PreDestroy.class), this.singleton, new Object[0]);
    }
}
