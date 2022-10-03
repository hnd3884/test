package org.glassfish.jersey.server.filter.internal;

import javax.ws.rs.core.Configuration;
import org.glassfish.jersey.server.filter.UriConnegFilter;
import javax.ws.rs.core.FeatureContext;
import javax.annotation.Priority;
import javax.ws.rs.RuntimeType;
import javax.ws.rs.ConstrainedTo;
import org.glassfish.jersey.internal.spi.AutoDiscoverable;

@ConstrainedTo(RuntimeType.SERVER)
@Priority(2000)
public final class ServerFiltersAutoDiscoverable implements AutoDiscoverable
{
    public void configure(final FeatureContext context) {
        final Configuration config = context.getConfiguration();
        final Object languageMappings = config.getProperty("jersey.config.server.languageMappings");
        final Object mediaTypesMappings = config.getProperty("jersey.config.server.mediaTypeMappings");
        if (!config.isRegistered((Class)UriConnegFilter.class) && (languageMappings != null || mediaTypesMappings != null)) {
            context.register((Class)UriConnegFilter.class);
        }
    }
}
