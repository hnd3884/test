package org.glassfish.jersey.server.spi;

import org.glassfish.jersey.internal.inject.InjectionManager;

public interface RequestScopedInitializer
{
    void initialize(final InjectionManager p0);
}
