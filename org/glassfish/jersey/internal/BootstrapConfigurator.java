package org.glassfish.jersey.internal;

import org.glassfish.jersey.internal.inject.InjectionManager;

public interface BootstrapConfigurator
{
    void init(final InjectionManager p0, final BootstrapBag p1);
    
    default void postInit(final InjectionManager injectionManager, final BootstrapBag bootstrapBag) {
    }
}
