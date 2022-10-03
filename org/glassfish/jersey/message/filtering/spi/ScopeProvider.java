package org.glassfish.jersey.message.filtering.spi;

import org.glassfish.jersey.message.filtering.EntityFiltering;
import java.util.Set;
import java.lang.annotation.Annotation;
import org.glassfish.jersey.spi.Contract;

@Contract
public interface ScopeProvider
{
    public static final String DEFAULT_SCOPE = EntityFiltering.class.getName();
    
    Set<String> getFilteringScopes(final Annotation[] p0, final boolean p1);
}
