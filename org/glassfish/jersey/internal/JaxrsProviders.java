package org.glassfish.jersey.internal;

import org.glassfish.jersey.internal.inject.Binding;
import org.glassfish.jersey.internal.inject.PerLookup;
import org.glassfish.jersey.internal.inject.Bindings;
import org.glassfish.jersey.internal.inject.ClassBinding;
import org.glassfish.jersey.internal.inject.InjectionManager;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import org.glassfish.jersey.spi.ExceptionMappers;
import org.glassfish.jersey.spi.ContextResolvers;
import javax.inject.Inject;
import org.glassfish.jersey.message.MessageBodyWorkers;
import javax.inject.Provider;
import javax.ws.rs.ext.Providers;

public class JaxrsProviders implements Providers
{
    @Inject
    private Provider<MessageBodyWorkers> workers;
    @Inject
    private Provider<ContextResolvers> resolvers;
    @Inject
    private Provider<ExceptionMappers> mappers;
    
    public <T> MessageBodyReader<T> getMessageBodyReader(final Class<T> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return ((MessageBodyWorkers)this.workers.get()).getMessageBodyReader(type, genericType, annotations, mediaType);
    }
    
    public <T> MessageBodyWriter<T> getMessageBodyWriter(final Class<T> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return ((MessageBodyWorkers)this.workers.get()).getMessageBodyWriter(type, genericType, annotations, mediaType);
    }
    
    public <T extends Throwable> ExceptionMapper<T> getExceptionMapper(final Class<T> type) {
        final ExceptionMappers actualMappers = (ExceptionMappers)this.mappers.get();
        return (actualMappers != null) ? actualMappers.find(type) : null;
    }
    
    public <T> ContextResolver<T> getContextResolver(final Class<T> contextType, final MediaType mediaType) {
        return ((ContextResolvers)this.resolvers.get()).resolve(contextType, mediaType);
    }
    
    public static class ProvidersConfigurator implements BootstrapConfigurator
    {
        @Override
        public void init(final InjectionManager injectionManager, final BootstrapBag bootstrapBag) {
            injectionManager.register(Bindings.service(JaxrsProviders.class).to((Class<? super Object>)Providers.class).in(PerLookup.class));
        }
    }
}
