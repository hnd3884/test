package org.glassfish.jersey.server.spi;

import org.glassfish.jersey.internal.inject.InjectionManager;

public interface ExternalRequestScope<T> extends AutoCloseable
{
    ExternalRequestContext<T> open(final InjectionManager p0);
    
    void suspend(final ExternalRequestContext<T> p0, final InjectionManager p1);
    
    void resume(final ExternalRequestContext<T> p0, final InjectionManager p1);
    
    void close();
}
