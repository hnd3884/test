package org.glassfish.jersey.servlet.internal;

import org.glassfish.jersey.internal.ServiceFinder;
import org.glassfish.jersey.servlet.internal.spi.ServletContainerProvider;

public final class ServletContainerProviderFactory
{
    private ServletContainerProviderFactory() {
    }
    
    public static ServletContainerProvider[] getAllServletContainerProviders() {
        return (ServletContainerProvider[])ServiceFinder.find((Class)ServletContainerProvider.class).toArray();
    }
}
