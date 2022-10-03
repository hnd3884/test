package org.glassfish.jersey.server.spi;

import java.util.Set;
import org.glassfish.jersey.internal.inject.InjectionManager;

public interface ComponentProvider
{
    void initialize(final InjectionManager p0);
    
    boolean bind(final Class<?> p0, final Set<Class<?>> p1);
    
    void done();
}
