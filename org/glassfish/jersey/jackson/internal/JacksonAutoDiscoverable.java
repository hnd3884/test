package org.glassfish.jersey.jackson.internal;

import org.glassfish.jersey.jackson.JacksonFeature;
import javax.ws.rs.core.FeatureContext;
import javax.annotation.Priority;
import org.glassfish.jersey.internal.spi.AutoDiscoverable;

@Priority(2000)
public class JacksonAutoDiscoverable implements AutoDiscoverable
{
    public void configure(final FeatureContext context) {
        if (!context.getConfiguration().isRegistered((Class)JacksonFeature.class)) {
            context.register((Class)JacksonFeature.class);
        }
    }
}
