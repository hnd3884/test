package org.glassfish.jersey.server.model.internal;

import java.lang.reflect.Type;
import javax.ws.rs.ProcessingException;
import java.io.IOException;
import java.io.Flushable;
import org.glassfish.jersey.server.ContainerRequest;
import java.util.Iterator;
import org.glassfish.jersey.server.spi.internal.ParamValueFactoryWithSource;
import java.util.List;
import javax.ws.rs.sse.SseEventSink;
import org.glassfish.jersey.server.model.Parameter;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.server.model.Parameterized;
import org.glassfish.jersey.server.spi.internal.ParameterValueHelper;
import org.glassfish.jersey.server.internal.inject.ConfiguredValidator;
import java.lang.reflect.InvocationHandler;
import org.glassfish.jersey.server.model.Invocable;
import org.glassfish.jersey.server.spi.internal.ValueParamProvider;
import java.util.Collection;
import org.glassfish.jersey.server.spi.internal.ResourceMethodDispatcher;

class JavaResourceMethodDispatcherProvider implements ResourceMethodDispatcher.Provider
{
    private final Collection<ValueParamProvider> allValueProviders;
    
    JavaResourceMethodDispatcherProvider(final Collection<ValueParamProvider> allValueProviders) {
        this.allValueProviders = allValueProviders;
    }
    
    @Override
    public ResourceMethodDispatcher create(final Invocable resourceMethod, final InvocationHandler invocationHandler, final ConfiguredValidator validator) {
        final List<ParamValueFactoryWithSource<?>> valueProviders = ParameterValueHelper.createValueProviders(this.allValueProviders, resourceMethod);
        final Class<?> returnType = resourceMethod.getHandlingMethod().getReturnType();
        ResourceMethodDispatcher resourceMethodDispatcher = null;
        if (Response.class.isAssignableFrom(returnType)) {
            resourceMethodDispatcher = new ResponseOutInvoker(resourceMethod, invocationHandler, valueProviders, validator);
        }
        else if (returnType != Void.TYPE) {
            if (returnType == Object.class || GenericEntity.class.isAssignableFrom(returnType)) {
                resourceMethodDispatcher = new ObjectOutInvoker(resourceMethod, invocationHandler, valueProviders, validator);
            }
            else {
                resourceMethodDispatcher = new TypeOutInvoker(resourceMethod, invocationHandler, valueProviders, validator);
            }
        }
        else {
            int i = 0;
            for (final Parameter parameter : resourceMethod.getParameters()) {
                if (SseEventSink.class.equals(parameter.getRawType())) {
                    resourceMethodDispatcher = new SseEventSinkInvoker(resourceMethod, invocationHandler, valueProviders, validator, i);
                    break;
                }
                ++i;
            }
            if (resourceMethodDispatcher == null) {
                resourceMethodDispatcher = new VoidOutInvoker(resourceMethod, invocationHandler, valueProviders, validator);
            }
        }
        return resourceMethodDispatcher;
    }
    
    private abstract static class AbstractMethodParamInvoker extends AbstractJavaResourceMethodDispatcher
    {
        private final List<ParamValueFactoryWithSource<?>> valueProviders;
        
        AbstractMethodParamInvoker(final Invocable resourceMethod, final InvocationHandler handler, final List<ParamValueFactoryWithSource<?>> valueProviders, final ConfiguredValidator validator) {
            super(resourceMethod, handler, validator);
            this.valueProviders = valueProviders;
        }
        
        final Object[] getParamValues(final ContainerRequest request) {
            return ParameterValueHelper.getParameterValues(this.valueProviders, request);
        }
    }
    
    private static final class SseEventSinkInvoker extends AbstractMethodParamInvoker
    {
        private final int parameterIndex;
        
        SseEventSinkInvoker(final Invocable resourceMethod, final InvocationHandler handler, final List<ParamValueFactoryWithSource<?>> valueProviders, final ConfiguredValidator validator, final int parameterIndex) {
            super(resourceMethod, handler, valueProviders, validator);
            this.parameterIndex = parameterIndex;
        }
        
        @Override
        protected Response doDispatch(final Object resource, final ContainerRequest request) throws ProcessingException {
            final Object[] paramValues = this.getParamValues(request);
            this.invoke(request, resource, paramValues);
            final SseEventSink eventSink = (SseEventSink)paramValues[this.parameterIndex];
            if (eventSink == null) {
                throw new IllegalArgumentException("SseEventSink parameter detected, but not found.");
            }
            if (eventSink instanceof Flushable) {
                try {
                    ((Flushable)eventSink).flush();
                }
                catch (final IOException ex) {}
            }
            return Response.ok().entity((Object)eventSink).build();
        }
    }
    
    private static final class VoidOutInvoker extends AbstractMethodParamInvoker
    {
        VoidOutInvoker(final Invocable resourceMethod, final InvocationHandler handler, final List<ParamValueFactoryWithSource<?>> valueProviders, final ConfiguredValidator validator) {
            super(resourceMethod, handler, valueProviders, validator);
        }
        
        @Override
        protected Response doDispatch(final Object resource, final ContainerRequest containerRequest) throws ProcessingException {
            this.invoke(containerRequest, resource, this.getParamValues(containerRequest));
            return Response.noContent().build();
        }
    }
    
    private static final class ResponseOutInvoker extends AbstractMethodParamInvoker
    {
        ResponseOutInvoker(final Invocable resourceMethod, final InvocationHandler handler, final List<ParamValueFactoryWithSource<?>> valueProviders, final ConfiguredValidator validator) {
            super(resourceMethod, handler, valueProviders, validator);
        }
        
        @Override
        protected Response doDispatch(final Object resource, final ContainerRequest containerRequest) throws ProcessingException {
            return Response.class.cast(this.invoke(containerRequest, resource, this.getParamValues(containerRequest)));
        }
    }
    
    private static final class ObjectOutInvoker extends AbstractMethodParamInvoker
    {
        ObjectOutInvoker(final Invocable resourceMethod, final InvocationHandler handler, final List<ParamValueFactoryWithSource<?>> valueProviders, final ConfiguredValidator validator) {
            super(resourceMethod, handler, valueProviders, validator);
        }
        
        @Override
        protected Response doDispatch(final Object resource, final ContainerRequest containerRequest) throws ProcessingException {
            final Object o = this.invoke(containerRequest, resource, this.getParamValues(containerRequest));
            if (o instanceof Response) {
                return Response.class.cast(o);
            }
            if (o != null) {
                return Response.ok().entity(o).build();
            }
            return Response.noContent().build();
        }
    }
    
    private static final class TypeOutInvoker extends AbstractMethodParamInvoker
    {
        private final Type t;
        
        TypeOutInvoker(final Invocable resourceMethod, final InvocationHandler handler, final List<ParamValueFactoryWithSource<?>> valueProviders, final ConfiguredValidator validator) {
            super(resourceMethod, handler, valueProviders, validator);
            this.t = resourceMethod.getHandlingMethod().getGenericReturnType();
        }
        
        @Override
        protected Response doDispatch(final Object resource, final ContainerRequest containerRequest) throws ProcessingException {
            final Object o = this.invoke(containerRequest, resource, this.getParamValues(containerRequest));
            if (o == null) {
                return Response.noContent().build();
            }
            if (o instanceof Response) {
                return Response.class.cast(o);
            }
            return Response.ok().entity(o).build();
        }
    }
}
