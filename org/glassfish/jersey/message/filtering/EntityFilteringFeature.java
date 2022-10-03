package org.glassfish.jersey.message.filtering;

import javax.ws.rs.core.Configuration;
import javax.ws.rs.RuntimeType;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.core.Feature;

public final class EntityFilteringFeature implements Feature
{
    public static final String ENTITY_FILTERING_SCOPE = "jersey.config.entityFiltering.scope";
    
    public boolean configure(final FeatureContext context) {
        final Configuration config = context.getConfiguration();
        if (!config.isRegistered((Class)EntityFilteringProcessor.class)) {
            if (!config.isRegistered((Class)EntityFilteringBinder.class)) {
                context.register((Object)new EntityFilteringBinder());
            }
            context.register((Class)EntityFilteringProcessor.class);
            if (!config.isRegistered((Class)DefaultEntityProcessor.class)) {
                context.register((Class)DefaultEntityProcessor.class);
            }
            context.register((Class)EntityFilteringScopeResolver.class);
            if (RuntimeType.SERVER == config.getRuntimeType()) {
                context.register((Class)ServerScopeProvider.class);
            }
            else {
                context.register((Class)CommonScopeProvider.class);
            }
            return true;
        }
        return false;
    }
    
    public static boolean enabled(final Configuration config) {
        return config.isRegistered((Class)EntityFilteringFeature.class) || config.isRegistered((Class)SecurityEntityFilteringFeature.class) || config.isRegistered((Class)SelectableEntityFilteringFeature.class);
    }
}
