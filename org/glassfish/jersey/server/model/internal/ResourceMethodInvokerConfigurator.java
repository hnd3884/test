package org.glassfish.jersey.server.model.internal;

import java.util.List;
import org.glassfish.jersey.server.internal.inject.ConfiguredValidator;
import java.util.Collection;
import org.glassfish.jersey.server.model.ResourceMethodInvoker;
import java.util.Arrays;
import javax.ws.rs.container.ResourceContext;
import org.glassfish.jersey.server.spi.internal.ResourceMethodDispatcher;
import org.glassfish.jersey.server.ServerBootstrapBag;
import org.glassfish.jersey.internal.BootstrapBag;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.internal.BootstrapConfigurator;

public class ResourceMethodInvokerConfigurator implements BootstrapConfigurator
{
    public void init(final InjectionManager injectionManager, final BootstrapBag bootstrapBag) {
    }
    
    public void postInit(final InjectionManager injectionManager, final BootstrapBag bootstrapBag) {
        final ServerBootstrapBag serverBag = (ServerBootstrapBag)bootstrapBag;
        final List<ResourceMethodDispatcher.Provider> providers = Arrays.asList(new VoidVoidDispatcherProvider((ResourceContext)serverBag.getResourceContext()), new JavaResourceMethodDispatcherProvider(serverBag.getValueParamProviders()));
        final ResourceMethodInvoker.Builder builder = new ResourceMethodInvoker.Builder().injectionManager(injectionManager).resourceMethodDispatcherFactory(new ResourceMethodDispatcherFactory(providers)).resourceMethodInvocationHandlerFactory(new ResourceMethodInvocationHandlerFactory(injectionManager)).configuration(bootstrapBag.getConfiguration()).configurationValidator(() -> injectionManager.getInstance((Class)ConfiguredValidator.class));
        serverBag.setResourceMethodInvokerBuilder(builder);
    }
}
