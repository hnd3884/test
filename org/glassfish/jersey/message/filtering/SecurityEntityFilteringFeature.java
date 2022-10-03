package org.glassfish.jersey.message.filtering;

import javax.ws.rs.core.Configuration;
import javax.ws.rs.RuntimeType;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.core.Feature;

public final class SecurityEntityFilteringFeature implements Feature
{
    public boolean configure(final FeatureContext context) {
        final Configuration config = context.getConfiguration();
        if (!config.isRegistered((Class)SecurityEntityProcessor.class)) {
            if (!config.isRegistered((Class)RolesAllowedDynamicFeature.class)) {
                context.register((Class)RolesAllowedDynamicFeature.class);
            }
            if (!config.isRegistered((Class)EntityFilteringBinder.class)) {
                context.register((Object)new EntityFilteringBinder());
            }
            context.register((Class)SecurityEntityProcessor.class);
            if (!config.isRegistered((Class)DefaultEntityProcessor.class)) {
                context.register((Class)DefaultEntityProcessor.class);
            }
            context.register((Class)SecurityScopeResolver.class);
            if (RuntimeType.SERVER.equals((Object)config.getRuntimeType())) {
                context.register((Class)SecurityServerScopeResolver.class);
            }
            if (RuntimeType.SERVER == config.getRuntimeType()) {
                context.register((Class)SecurityServerScopeProvider.class);
            }
            else {
                context.register((Class)CommonScopeProvider.class);
            }
            return true;
        }
        return false;
    }
}
