package org.glassfish.jersey.jaxb.internal;

import javax.ws.rs.RuntimeType;
import javax.ws.rs.core.FeatureContext;
import org.glassfish.jersey.internal.spi.ForcedAutoDiscoverable;

public final class JaxbAutoDiscoverable implements ForcedAutoDiscoverable
{
    public void configure(final FeatureContext context) {
        context.register((Object)new JaxbMessagingBinder());
        if (RuntimeType.SERVER == context.getConfiguration().getRuntimeType()) {
            context.register((Object)new JaxbParamConverterBinder());
        }
    }
}
