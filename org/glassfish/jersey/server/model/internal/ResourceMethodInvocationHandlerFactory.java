package org.glassfish.jersey.server.model.internal;

import java.lang.reflect.Method;
import org.glassfish.jersey.internal.inject.Providers;
import java.util.Iterator;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import java.util.logging.Level;
import org.glassfish.jersey.server.model.Invocable;
import org.glassfish.jersey.internal.util.collection.Values;
import org.glassfish.jersey.internal.inject.InjectionManager;
import java.util.Set;
import org.glassfish.jersey.internal.util.collection.LazyValue;
import java.util.logging.Logger;
import java.lang.reflect.InvocationHandler;
import javax.inject.Singleton;
import org.glassfish.jersey.server.spi.internal.ResourceMethodInvocationHandlerProvider;

@Singleton
public final class ResourceMethodInvocationHandlerFactory implements ResourceMethodInvocationHandlerProvider
{
    private static final InvocationHandler DEFAULT_HANDLER;
    private static final Logger LOGGER;
    private final LazyValue<Set<ResourceMethodInvocationHandlerProvider>> providers;
    
    ResourceMethodInvocationHandlerFactory(final InjectionManager injectionManager) {
        this.providers = (LazyValue<Set<ResourceMethodInvocationHandlerProvider>>)Values.lazy(() -> Providers.getProviders(injectionManager, (Class)ResourceMethodInvocationHandlerProvider.class));
    }
    
    @Override
    public InvocationHandler create(final Invocable resourceMethod) {
        for (final ResourceMethodInvocationHandlerProvider provider : (Set)this.providers.get()) {
            try {
                final InvocationHandler handler = provider.create(resourceMethod);
                if (handler != null) {
                    return handler;
                }
                continue;
            }
            catch (final Exception e) {
                ResourceMethodInvocationHandlerFactory.LOGGER.log(Level.SEVERE, LocalizationMessages.ERROR_PROCESSING_METHOD(resourceMethod, provider.getClass().getName()), e);
            }
        }
        return ResourceMethodInvocationHandlerFactory.DEFAULT_HANDLER;
    }
    
    static {
        DEFAULT_HANDLER = ((target, method, args) -> method.invoke(target, args));
        LOGGER = Logger.getLogger(ResourceMethodInvocationHandlerFactory.class.getName());
    }
}
