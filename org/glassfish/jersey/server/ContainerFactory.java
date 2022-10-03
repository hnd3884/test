package org.glassfish.jersey.server;

import java.util.Iterator;
import org.glassfish.jersey.internal.ServiceFinder;
import org.glassfish.jersey.server.spi.ContainerProvider;
import javax.ws.rs.core.Application;

public final class ContainerFactory
{
    private ContainerFactory() {
    }
    
    public static <T> T createContainer(final Class<T> type, final Application application) {
        for (final ContainerProvider containerProvider : ServiceFinder.find((Class)ContainerProvider.class)) {
            final T container = containerProvider.createContainer(type, application);
            if (container != null) {
                return container;
            }
        }
        throw new IllegalArgumentException("No container provider supports the type " + type);
    }
}
