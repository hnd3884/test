package org.glassfish.jersey.message.filtering;

import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.core.Feature;

public final class SelectableEntityFilteringFeature implements Feature
{
    public static final String QUERY_PARAM_NAME = "jersey.config.entityFiltering.selectable.query";
    
    public boolean configure(final FeatureContext context) {
        final Configuration config = context.getConfiguration();
        if (!config.isRegistered((Class)SelectableEntityProcessor.class)) {
            if (!config.isRegistered((Class)EntityFilteringFeature.class)) {
                context.register((Class)EntityFilteringFeature.class);
            }
            context.register((Class)SelectableEntityProcessor.class);
            context.register((Class)SelectableScopeResolver.class);
            return true;
        }
        return true;
    }
}
