package org.glassfish.jersey.server;

import java.util.function.Function;
import java.util.function.Consumer;
import org.glassfish.jersey.internal.inject.Binding;
import javax.ws.rs.container.ResourceContext;
import org.glassfish.jersey.internal.inject.Bindings;
import org.glassfish.jersey.internal.inject.InstanceBinding;
import org.glassfish.jersey.server.internal.JerseyResourceContext;
import org.glassfish.jersey.internal.inject.Injections;
import org.glassfish.jersey.internal.BootstrapBag;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.internal.BootstrapConfigurator;

class JerseyResourceContextConfigurator implements BootstrapConfigurator
{
    public void init(final InjectionManager injectionManager, final BootstrapBag bootstrapBag) {
        final ServerBootstrapBag serverBag = (ServerBootstrapBag)bootstrapBag;
        final Consumer<Binding> registerBinding = injectionManager::register;
        final Function<Class<?>, ?> getOrCreateInstance = (Function<Class<?>, ?>)(clazz -> Injections.getOrCreate(injectionManager, clazz));
        final Consumer<Object> injectInstance = injectionManager::inject;
        final JerseyResourceContext resourceContext = new JerseyResourceContext(getOrCreateInstance, injectInstance, registerBinding);
        final InstanceBinding<JerseyResourceContext> resourceContextBinding = (InstanceBinding<JerseyResourceContext>)((InstanceBinding)Bindings.service((Object)resourceContext).to((Class)ResourceContext.class)).to((Class)ExtendedResourceContext.class);
        injectionManager.register((Binding)resourceContextBinding);
        serverBag.setResourceContext(resourceContext);
    }
    
    public void postInit(final InjectionManager injectionManager, final BootstrapBag bootstrapBag) {
    }
}
