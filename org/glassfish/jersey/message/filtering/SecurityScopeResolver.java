package org.glassfish.jersey.message.filtering;

import java.util.Set;
import java.lang.annotation.Annotation;
import javax.inject.Singleton;
import org.glassfish.jersey.message.filtering.spi.ScopeResolver;

@Singleton
final class SecurityScopeResolver implements ScopeResolver
{
    @Override
    public Set<String> resolve(final Annotation[] annotations) {
        return SecurityHelper.getFilteringScopes(annotations);
    }
}
