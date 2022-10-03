package org.glassfish.jersey.message.filtering;

import org.glassfish.jersey.message.filtering.internal.LocalizationMessages;
import java.util.logging.Level;
import java.util.Collections;
import java.util.Iterator;
import org.glassfish.jersey.message.filtering.spi.FilteringHelper;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.lang.annotation.Annotation;
import javax.inject.Inject;
import java.util.Spliterator;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.glassfish.jersey.internal.inject.Providers;
import org.glassfish.jersey.model.internal.RankedComparator;
import org.glassfish.jersey.internal.inject.InjectionManager;
import javax.ws.rs.core.Configuration;
import org.glassfish.jersey.message.filtering.spi.ScopeResolver;
import java.util.List;
import java.util.logging.Logger;
import javax.inject.Singleton;
import org.glassfish.jersey.message.filtering.spi.ScopeProvider;

@Singleton
class CommonScopeProvider implements ScopeProvider
{
    private static final Logger LOGGER;
    private final List<ScopeResolver> resolvers;
    private final Configuration config;
    
    @Inject
    public CommonScopeProvider(final Configuration config, final InjectionManager injectionManager) {
        this.config = config;
        final Spliterator<ScopeResolver> resolverSpliterator = Providers.getAllProviders(injectionManager, (Class)ScopeResolver.class, new RankedComparator()).spliterator();
        this.resolvers = StreamSupport.stream(resolverSpliterator, false).collect((Collector<? super ScopeResolver, ?, List<ScopeResolver>>)Collectors.toList());
    }
    
    @Override
    public Set<String> getFilteringScopes(final Annotation[] entityAnnotations, final boolean defaultIfNotFound) {
        final Set<String> filteringScopes = new HashSet<String>();
        filteringScopes.addAll(this.getFilteringScopes(entityAnnotations));
        if (filteringScopes.isEmpty()) {
            filteringScopes.addAll(this.getFilteringScopes(this.config));
        }
        return this.returnFilteringScopes(filteringScopes, defaultIfNotFound);
    }
    
    protected Set<String> returnFilteringScopes(final Set<String> filteringScopes, final boolean returnDefaultFallback) {
        return (returnDefaultFallback && filteringScopes.isEmpty()) ? FilteringHelper.getDefaultFilteringScope() : filteringScopes;
    }
    
    protected Set<String> getFilteringScopes(final Annotation[] annotations) {
        final Set<String> filteringScopes = new HashSet<String>();
        for (final ScopeResolver provider : this.resolvers) {
            this.mergeFilteringScopes(filteringScopes, provider.resolve(annotations));
        }
        return filteringScopes;
    }
    
    private Set<String> getFilteringScopes(final Configuration config) {
        final Object property = config.getProperty("jersey.config.entityFiltering.scope");
        Set<String> filteringScopes = Collections.emptySet();
        if (property != null) {
            if (property instanceof Annotation) {
                filteringScopes = this.getFilteringScopes(new Annotation[] { (Annotation)property });
            }
            else if (property instanceof Annotation[]) {
                filteringScopes = this.getFilteringScopes((Annotation[])property);
            }
            else {
                CommonScopeProvider.LOGGER.log(Level.CONFIG, LocalizationMessages.ENTITY_FILTERING_SCOPE_NOT_ANNOTATIONS(property));
            }
        }
        return filteringScopes;
    }
    
    protected void mergeFilteringScopes(final Set<String> filteringScopes, final Set<String> resolvedScopes) {
        if (!filteringScopes.isEmpty() && !resolvedScopes.isEmpty()) {
            CommonScopeProvider.LOGGER.log(Level.FINE, LocalizationMessages.MERGING_FILTERING_SCOPES());
        }
        filteringScopes.addAll(resolvedScopes);
    }
    
    static {
        LOGGER = Logger.getLogger(CommonScopeProvider.class.getName());
    }
}
