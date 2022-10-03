package org.glassfish.jersey.server;

import java.util.Iterator;
import org.glassfish.jersey.server.model.Resource;
import java.util.Map;
import org.glassfish.jersey.internal.BootstrapBag;
import org.glassfish.jersey.internal.inject.InjectionManager;
import java.util.logging.Logger;
import org.glassfish.jersey.internal.BootstrapConfigurator;

class ResourceBagConfigurator implements BootstrapConfigurator
{
    private static final Logger LOGGER;
    
    public void init(final InjectionManager injectionManager, final BootstrapBag bootstrapBag) {
        final ServerBootstrapBag serverBag = (ServerBootstrapBag)bootstrapBag;
        final ResourceConfig runtimeConfig = serverBag.getRuntimeConfig();
        final boolean disableValidation = ServerProperties.getValue(runtimeConfig.getProperties(), "jersey.config.server.resource.validation.disable", Boolean.FALSE, Boolean.class);
        final ResourceBag.Builder resourceBagBuilder = new ResourceBag.Builder();
        for (final Resource programmaticResource : runtimeConfig.getResources()) {
            resourceBagBuilder.registerProgrammaticResource(programmaticResource);
        }
        for (final Class<?> c : runtimeConfig.getClasses()) {
            try {
                final Resource resource = Resource.from(c, disableValidation);
                if (resource == null) {
                    continue;
                }
                resourceBagBuilder.registerResource(c, resource);
            }
            catch (final IllegalArgumentException ex) {
                ResourceBagConfigurator.LOGGER.warning(ex.getMessage());
            }
        }
        for (final Object o : runtimeConfig.getSingletons()) {
            try {
                final Resource resource = Resource.from(o.getClass(), disableValidation);
                if (resource == null) {
                    continue;
                }
                resourceBagBuilder.registerResource(o, resource);
            }
            catch (final IllegalArgumentException ex) {
                ResourceBagConfigurator.LOGGER.warning(ex.getMessage());
            }
        }
        serverBag.setResourceBag(resourceBagBuilder.build());
    }
    
    static {
        LOGGER = Logger.getLogger(ResourceBagConfigurator.class.getName());
    }
}
