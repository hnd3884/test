package org.glassfish.jersey.server.model.internal;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.internal.inject.ConfiguredValidator;
import java.lang.reflect.InvocationHandler;
import org.glassfish.jersey.server.model.Invocable;
import javax.ws.rs.container.ResourceContext;
import javax.inject.Singleton;
import org.glassfish.jersey.server.spi.internal.ResourceMethodDispatcher;

@Singleton
final class VoidVoidDispatcherProvider implements ResourceMethodDispatcher.Provider
{
    private final ResourceContext resourceContext;
    
    VoidVoidDispatcherProvider(final ResourceContext resourceContext) {
        this.resourceContext = resourceContext;
    }
    
    @Override
    public ResourceMethodDispatcher create(final Invocable resourceMethod, final InvocationHandler handler, final ConfiguredValidator validator) {
        if (resourceMethod.getHandlingMethod().getReturnType() != Void.TYPE || !resourceMethod.getParameters().isEmpty()) {
            return null;
        }
        return (ResourceMethodDispatcher)this.resourceContext.initResource((Object)new VoidToVoidDispatcher(resourceMethod, handler, validator));
    }
    
    private static class VoidToVoidDispatcher extends AbstractJavaResourceMethodDispatcher
    {
        private VoidToVoidDispatcher(final Invocable resourceMethod, final InvocationHandler handler, final ConfiguredValidator validator) {
            super(resourceMethod, handler, validator);
        }
        
        public Response doDispatch(final Object resource, final ContainerRequest containerRequest) throws ProcessingException {
            this.invoke(containerRequest, resource, new Object[0]);
            return Response.noContent().build();
        }
    }
}
