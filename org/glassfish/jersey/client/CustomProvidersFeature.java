package org.glassfish.jersey.client;

import java.util.Iterator;
import javax.ws.rs.core.FeatureContext;
import java.util.Collection;
import javax.ws.rs.core.Feature;

public class CustomProvidersFeature implements Feature
{
    private final Collection<Class<?>> providers;
    
    public CustomProvidersFeature(final Collection<Class<?>> providers) {
        this.providers = providers;
    }
    
    public boolean configure(final FeatureContext context) {
        for (final Class<?> provider : this.providers) {
            context.register((Class)provider);
        }
        return true;
    }
}
