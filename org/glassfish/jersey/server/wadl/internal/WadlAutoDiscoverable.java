package org.glassfish.jersey.server.wadl.internal;

import org.glassfish.jersey.server.wadl.WadlFeature;
import javax.ws.rs.core.FeatureContext;
import javax.annotation.Priority;
import javax.ws.rs.RuntimeType;
import javax.ws.rs.ConstrainedTo;
import org.glassfish.jersey.internal.spi.ForcedAutoDiscoverable;

@ConstrainedTo(RuntimeType.SERVER)
@Priority(2000)
public final class WadlAutoDiscoverable implements ForcedAutoDiscoverable
{
    public void configure(final FeatureContext context) {
        if (!context.getConfiguration().isRegistered((Class)WadlFeature.class)) {
            context.register((Class)WadlFeature.class);
        }
    }
}
