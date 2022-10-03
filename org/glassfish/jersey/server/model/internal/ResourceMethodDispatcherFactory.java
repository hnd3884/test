package org.glassfish.jersey.server.model.internal;

import java.util.Iterator;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import java.util.logging.Level;
import org.glassfish.jersey.server.internal.inject.ConfiguredValidator;
import java.lang.reflect.InvocationHandler;
import org.glassfish.jersey.server.model.Invocable;
import java.util.Collection;
import java.util.logging.Logger;
import javax.inject.Singleton;
import org.glassfish.jersey.server.spi.internal.ResourceMethodDispatcher;

@Singleton
public final class ResourceMethodDispatcherFactory implements ResourceMethodDispatcher.Provider
{
    private static final Logger LOGGER;
    private final Collection<ResourceMethodDispatcher.Provider> providers;
    
    ResourceMethodDispatcherFactory(final Collection<ResourceMethodDispatcher.Provider> providers) {
        this.providers = providers;
    }
    
    @Override
    public ResourceMethodDispatcher create(final Invocable resourceMethod, final InvocationHandler handler, final ConfiguredValidator validator) {
        for (final ResourceMethodDispatcher.Provider provider : this.providers) {
            try {
                final ResourceMethodDispatcher dispatcher = provider.create(resourceMethod, handler, validator);
                if (dispatcher != null) {
                    return dispatcher;
                }
                continue;
            }
            catch (final Exception e) {
                ResourceMethodDispatcherFactory.LOGGER.log(Level.SEVERE, LocalizationMessages.ERROR_PROCESSING_METHOD(resourceMethod, provider.getClass().getName()), e);
            }
        }
        return null;
    }
    
    static {
        LOGGER = Logger.getLogger(ResourceMethodDispatcherFactory.class.getName());
    }
}
