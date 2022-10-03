package org.glassfish.jersey.message.filtering;

import java.util.regex.Matcher;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.HashSet;
import java.util.function.Function;
import org.glassfish.jersey.internal.util.collection.Views;
import java.util.Collections;
import org.glassfish.jersey.message.filtering.spi.ScopeProvider;
import org.glassfish.jersey.message.filtering.spi.EntityGraph;
import java.util.Map;
import java.util.Set;
import org.glassfish.jersey.message.filtering.spi.ObjectGraph;

final class ObjectGraphImpl implements ObjectGraph
{
    private final Set<String> filteringScopes;
    private final Map<Class<?>, EntityGraph> classToGraph;
    private final EntityGraph graph;
    private Set<String> fields;
    private Map<String, ObjectGraph> subgraphs;
    
    ObjectGraphImpl(final Map<Class<?>, EntityGraph> classToGraph, final EntityGraph graph, final Set<String> filteringScopes) {
        this.filteringScopes = filteringScopes;
        this.classToGraph = classToGraph;
        this.graph = graph;
    }
    
    @Override
    public Class<?> getEntityClass() {
        return this.graph.getEntityClass();
    }
    
    @Override
    public Set<String> getFields() {
        return this.getFields(null);
    }
    
    @Override
    public Set<String> getFields(final String parent) {
        final Set<String> childFilteringScopes = this.getFilteringScopes(parent);
        if (this.fields == null) {
            this.fields = this.graph.getFields(Views.setUnionView((Set)childFilteringScopes, (Set)Collections.singleton(ScopeProvider.DEFAULT_SCOPE)));
        }
        return this.fields;
    }
    
    @Override
    public Map<String, ObjectGraph> getSubgraphs() {
        return this.getSubgraphs(null);
    }
    
    @Override
    public Map<String, ObjectGraph> getSubgraphs(final String parent) {
        final Set<String> childFilteringScopes = this.getFilteringScopes(parent);
        if (this.subgraphs == null) {
            final Map<String, Class<?>> contextSubgraphs = this.graph.getSubgraphs(childFilteringScopes);
            contextSubgraphs.putAll(this.graph.getSubgraphs(ScopeProvider.DEFAULT_SCOPE));
            this.subgraphs = Views.mapView((Map)contextSubgraphs, (Function)new Function<Class<?>, ObjectGraph>() {
                @Override
                public ObjectGraph apply(final Class<?> clazz) {
                    final EntityGraph entityGraph = ObjectGraphImpl.this.classToGraph.get(clazz);
                    return (entityGraph == null) ? new EmptyObjectGraph(clazz) : new ObjectGraphImpl(ObjectGraphImpl.this.classToGraph, entityGraph, ObjectGraphImpl.this.filteringScopes);
                }
            });
        }
        return this.subgraphs;
    }
    
    private Set<String> getFilteringScopes(final String parent) {
        Set<String> childFilteringScopes = new HashSet<String>();
        if (this.filteringScopes.contains(SelectableScopeResolver.DEFAULT_SCOPE) || parent == null) {
            childFilteringScopes = this.filteringScopes;
        }
        else {
            for (final String filteringScope : this.filteringScopes) {
                final Pattern p = Pattern.compile(SelectableScopeResolver.PREFIX + parent + "\\.(\\w+)(\\.\\w+)*$");
                final Matcher m = p.matcher(filteringScope);
                if (m.matches()) {
                    childFilteringScopes.add(SelectableScopeResolver.PREFIX + m.group(1));
                }
                else {
                    childFilteringScopes.add(filteringScope);
                }
            }
        }
        return childFilteringScopes;
    }
}
