package org.glassfish.jersey.message.filtering;

import java.util.Set;
import java.lang.annotation.Annotation;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.RuntimeType;
import javax.ws.rs.ConstrainedTo;
import javax.annotation.Priority;
import javax.inject.Singleton;
import org.glassfish.jersey.message.filtering.spi.ScopeResolver;

@Singleton
@Priority(4100)
@ConstrainedTo(RuntimeType.SERVER)
final class SecurityServerScopeResolver implements ScopeResolver
{
    @Context
    private SecurityContext securityContext;
    
    @Override
    public Set<String> resolve(final Annotation[] annotations) {
        return SecurityHelper.getFilteringScopes(this.securityContext, annotations);
    }
}
