package org.glassfish.jersey.server.internal.process;

import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.SecurityContext;
import org.glassfish.jersey.internal.inject.ClassBinding;
import javax.ws.rs.container.ResourceInfo;
import org.glassfish.jersey.server.ExtendedUriInfo;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.container.ContainerRequestContext;
import java.lang.reflect.Type;
import org.glassfish.jersey.internal.inject.SupplierClassBinding;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.server.AsyncContext;
import org.glassfish.jersey.server.CloseableService;
import org.glassfish.jersey.server.internal.routing.UriRoutingContext;
import javax.inject.Inject;
import org.glassfish.jersey.server.ContainerRequest;
import java.util.function.Supplier;
import org.glassfish.jersey.internal.inject.Binder;
import org.glassfish.jersey.internal.BootstrapBag;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.internal.BootstrapConfigurator;

public class RequestProcessingConfigurator implements BootstrapConfigurator
{
    public void init(final InjectionManager injectionManager, final BootstrapBag bootstrapBag) {
        injectionManager.register((Binder)new ServerProcessingBinder());
    }
    
    private static class ContainerRequestFactory implements Supplier<ContainerRequest>
    {
        private final RequestProcessingContextReference reference;
        
        @Inject
        private ContainerRequestFactory(final RequestProcessingContextReference reference) {
            this.reference = reference;
        }
        
        @Override
        public ContainerRequest get() {
            return this.reference.get().request();
        }
    }
    
    private static class UriRoutingContextFactory implements Supplier<UriRoutingContext>
    {
        private final RequestProcessingContextReference reference;
        
        @Inject
        private UriRoutingContextFactory(final RequestProcessingContextReference reference) {
            this.reference = reference;
        }
        
        @Override
        public UriRoutingContext get() {
            return this.reference.get().uriRoutingContext();
        }
    }
    
    private static class CloseableServiceFactory implements Supplier<CloseableService>
    {
        private final RequestProcessingContextReference reference;
        
        @Inject
        private CloseableServiceFactory(final RequestProcessingContextReference reference) {
            this.reference = reference;
        }
        
        @Override
        public CloseableService get() {
            return this.reference.get().closeableService();
        }
    }
    
    private static class AsyncContextFactory implements Supplier<AsyncContext>
    {
        private final RequestProcessingContextReference reference;
        
        @Inject
        private AsyncContextFactory(final RequestProcessingContextReference reference) {
            this.reference = reference;
        }
        
        @Override
        public AsyncContext get() {
            return this.reference.get().asyncContext();
        }
    }
    
    private class ServerProcessingBinder extends AbstractBinder
    {
        protected void configure() {
            this.bindAsContract((Class)RequestProcessingContextReference.class).in((Class)RequestScoped.class);
            ((SupplierClassBinding)((SupplierClassBinding)((SupplierClassBinding)this.bindFactory((Class)ContainerRequestFactory.class).to((Type)ContainerRequest.class)).to((Type)ContainerRequestContext.class)).proxy(false)).in((Class)RequestScoped.class);
            ((SupplierClassBinding)((SupplierClassBinding)((SupplierClassBinding)((SupplierClassBinding)this.bindFactory((Class)ContainerRequestFactory.class).to((Type)HttpHeaders.class)).to((Type)Request.class)).proxy(true)).proxyForSameScope(false)).in((Class)RequestScoped.class);
            ((SupplierClassBinding)((SupplierClassBinding)((SupplierClassBinding)((SupplierClassBinding)((SupplierClassBinding)this.bindFactory((Class)UriRoutingContextFactory.class).to((Type)UriInfo.class)).to((Type)ExtendedUriInfo.class)).to((Type)ResourceInfo.class)).proxy(true)).proxyForSameScope(false)).in((Class)RequestScoped.class);
            ((ClassBinding)((ClassBinding)((ClassBinding)this.bind((Class)SecurityContextInjectee.class).to((Class)SecurityContext.class)).proxy(true)).proxyForSameScope(false)).in((Class)RequestScoped.class);
            ((SupplierClassBinding)((SupplierClassBinding)((SupplierClassBinding)this.bindFactory((Class)CloseableServiceFactory.class).to((Type)CloseableService.class)).proxy(true)).proxyForSameScope(false)).in((Class)RequestScoped.class);
            ((SupplierClassBinding)((SupplierClassBinding)this.bindFactory((Class)AsyncContextFactory.class).to((Type)AsyncContext.class)).to((Type)AsyncResponse.class)).in((Class)RequestScoped.class);
        }
    }
}
