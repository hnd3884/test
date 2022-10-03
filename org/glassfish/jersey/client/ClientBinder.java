package org.glassfish.jersey.client;

import java.util.function.Supplier;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.ext.MessageBodyReader;
import org.glassfish.jersey.internal.inject.ClassBinding;
import org.glassfish.jersey.internal.PropertiesDelegate;
import javax.inject.Singleton;
import java.lang.reflect.Type;
import org.glassfish.jersey.internal.inject.SupplierClassBinding;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.glassfish.jersey.internal.util.collection.Ref;
import javax.ws.rs.core.GenericType;
import org.glassfish.jersey.internal.inject.ReferencingFactory;
import org.glassfish.jersey.internal.inject.SupplierInstanceBinding;
import org.glassfish.jersey.message.internal.MessagingBinders;
import javax.ws.rs.RuntimeType;
import java.util.Map;
import org.glassfish.jersey.internal.inject.AbstractBinder;

class ClientBinder extends AbstractBinder
{
    private final Map<String, Object> clientRuntimeProperties;
    
    ClientBinder(final Map<String, Object> clientRuntimeProperties) {
        this.clientRuntimeProperties = clientRuntimeProperties;
    }
    
    protected void configure() {
        this.install(new AbstractBinder[] { (AbstractBinder)new MessagingBinders.MessageBodyProviders((Map)this.clientRuntimeProperties, RuntimeType.CLIENT), (AbstractBinder)new MessagingBinders.HeaderDelegateProviders() });
        ((SupplierInstanceBinding)this.bindFactory(ReferencingFactory.referenceFactory()).to((GenericType)new GenericType<Ref<ClientConfig>>() {})).in((Class)RequestScoped.class);
        ((SupplierClassBinding)this.bindFactory((Class)RequestContextInjectionFactory.class).to((Type)ClientRequest.class)).in((Class)RequestScoped.class);
        ((SupplierInstanceBinding)this.bindFactory(ReferencingFactory.referenceFactory()).to((GenericType)new GenericType<Ref<ClientRequest>>() {})).in((Class)RequestScoped.class);
        ((SupplierClassBinding)this.bindFactory((Class)PropertiesDelegateFactory.class, (Class)Singleton.class).to((Type)PropertiesDelegate.class)).in((Class)RequestScoped.class);
        ((ClassBinding)this.bind((Class)ChunkedInputReader.class).to((Class)MessageBodyReader.class)).in((Class)Singleton.class);
    }
    
    private static class RequestContextInjectionFactory extends ReferencingFactory<ClientRequest>
    {
        @Inject
        public RequestContextInjectionFactory(final Provider<Ref<ClientRequest>> referenceFactory) {
            super((Provider)referenceFactory);
        }
    }
    
    private static class PropertiesDelegateFactory implements Supplier<PropertiesDelegate>
    {
        private final Provider<ClientRequest> requestProvider;
        
        @Inject
        private PropertiesDelegateFactory(final Provider<ClientRequest> requestProvider) {
            this.requestProvider = requestProvider;
        }
        
        @Override
        public PropertiesDelegate get() {
            return ((ClientRequest)this.requestProvider.get()).getPropertiesDelegate();
        }
    }
}
