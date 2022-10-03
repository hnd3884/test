package org.glassfish.jersey.logging;

import java.util.Map;
import javax.ws.rs.RuntimeType;
import javax.ws.rs.core.FeatureContext;
import javax.annotation.Priority;
import org.glassfish.jersey.internal.spi.AutoDiscoverable;

@Priority(2000)
public final class LoggingFeatureAutoDiscoverable implements AutoDiscoverable
{
    @Override
    public void configure(final FeatureContext context) {
        if (!context.getConfiguration().isRegistered((Class)LoggingFeature.class)) {
            final Map properties = context.getConfiguration().getProperties();
            if (this.commonPropertyConfigured(properties) || (context.getConfiguration().getRuntimeType() == RuntimeType.CLIENT && this.clientConfigured(properties)) || (context.getConfiguration().getRuntimeType() == RuntimeType.SERVER && this.serverConfigured(properties))) {
                context.register((Class)LoggingFeature.class);
            }
        }
    }
    
    private boolean commonPropertyConfigured(final Map properties) {
        return properties.containsKey("jersey.config.logging.logger.name") || properties.containsKey("jersey.config.logging.logger.level") || properties.containsKey("jersey.config.logging.verbosity") || properties.containsKey("jersey.config.logging.entity.maxSize");
    }
    
    private boolean clientConfigured(final Map properties) {
        return properties.containsKey("jersey.config.client.logging.logger.name") || properties.containsKey("jersey.config.client.logging.logger.level") || properties.containsKey("jersey.config.client.logging.verbosity") || properties.containsKey("jersey.config.client.logging.entity.maxSize");
    }
    
    private boolean serverConfigured(final Map properties) {
        return properties.containsKey("jersey.config.server.logging.logger.name") || properties.containsKey("jersey.config.server.logging.logger.level") || properties.containsKey("jersey.config.server.logging.verbosity") || properties.containsKey("jersey.config.server.logging.entity.maxSize");
    }
}
