package org.glassfish.jersey.servlet.internal.spi;

import org.glassfish.jersey.server.spi.RequestScopedInitializer;

public interface RequestScopedInitializerProvider
{
    RequestScopedInitializer get(final RequestContextProvider p0);
}
