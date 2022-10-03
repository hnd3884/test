package org.glassfish.jersey.server;

import javax.ws.rs.core.Configuration;
import java.lang.reflect.Type;
import org.glassfish.jersey.internal.spi.AutoDiscoverable;
import org.glassfish.jersey.internal.inject.Bindings;
import org.glassfish.jersey.internal.BootstrapBag;
import org.glassfish.jersey.internal.inject.InjectionManager;
import javax.ws.rs.RuntimeType;
import org.glassfish.jersey.server.spi.ContainerProvider;
import org.glassfish.jersey.internal.AbstractServiceFinderConfigurator;

class ContainerProviderConfigurator extends AbstractServiceFinderConfigurator<ContainerProvider>
{
    ContainerProviderConfigurator(final RuntimeType runtimeType) {
        super((Class)ContainerProvider.class, runtimeType);
    }
    
    public void init(final InjectionManager injectionManager, final BootstrapBag bootstrapBag) {
        final Configuration configuration = bootstrapBag.getConfiguration();
        this.loadImplementations(configuration.getProperties()).forEach(implClass -> injectionManager.register(Bindings.service(implClass).to((Type)AutoDiscoverable.class)));
    }
}
