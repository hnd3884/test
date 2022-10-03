package org.glassfish.jersey.message.filtering;

import java.util.Iterator;
import java.util.HashSet;
import java.util.Set;
import java.lang.annotation.Annotation;
import javax.inject.Inject;
import org.glassfish.jersey.internal.inject.InjectionManager;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.RuntimeType;
import javax.ws.rs.ConstrainedTo;
import javax.inject.Singleton;

@Singleton
@ConstrainedTo(RuntimeType.SERVER)
final class SecurityServerScopeProvider extends ServerScopeProvider
{
    @Context
    private SecurityContext securityContext;
    
    @Inject
    public SecurityServerScopeProvider(final Configuration config, final InjectionManager injectionManager) {
        super(config, injectionManager);
    }
    
    @Override
    public Set<String> getFilteringScopes(final Annotation[] entityAnnotations, final boolean defaultIfNotFound) {
        Set<String> filteringScope = super.getFilteringScopes(entityAnnotations, false);
        if (filteringScope.isEmpty()) {
            filteringScope = new HashSet<String>();
            for (final String role : SecurityHelper.getProcessedRoles()) {
                if (this.securityContext.isUserInRole(role)) {
                    filteringScope.add(SecurityHelper.getRolesAllowedScope(role));
                }
            }
        }
        return this.returnFilteringScopes(filteringScope, defaultIfNotFound);
    }
}
