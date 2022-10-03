package org.glassfish.jersey.message.filtering;

import java.util.Map;
import java.util.Collections;
import java.util.Set;
import org.glassfish.jersey.message.filtering.spi.EntityGraph;

final class EmptyEntityGraphImpl implements EntityGraph
{
    private final Class<?> clazz;
    
    EmptyEntityGraphImpl(final Class<?> clazz) {
        this.clazz = clazz;
    }
    
    @Override
    public EntityGraph addField(final String fieldName) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public EntityGraph addField(final String fieldName, final String... filteringScopes) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public EntityGraph addField(final String fieldName, final Set<String> filteringScopes) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public EntityGraph addSubgraph(final String fieldName, final Class<?> fieldClass) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public EntityGraph addSubgraph(final String fieldName, final Class<?> fieldClass, final String... filteringScopes) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public EntityGraph addSubgraph(final String fieldName, final Class<?> fieldClass, final Set<String> filteringScopes) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Class<?> getEntityClass() {
        return this.clazz;
    }
    
    @Override
    public Set<String> getFields(final String filteringScope) {
        return Collections.emptySet();
    }
    
    @Override
    public Set<String> getFields(final String... filteringScopes) {
        return Collections.emptySet();
    }
    
    @Override
    public Set<String> getFields(final Set<String> filteringScopes) {
        return Collections.emptySet();
    }
    
    @Override
    public Map<String, Class<?>> getSubgraphs(final String filteringScope) {
        return Collections.emptyMap();
    }
    
    @Override
    public Map<String, Class<?>> getSubgraphs(final String... filteringScopes) {
        return Collections.emptyMap();
    }
    
    @Override
    public Map<String, Class<?>> getSubgraphs(final Set<String> filteringScopes) {
        return Collections.emptyMap();
    }
    
    @Override
    public boolean presentInScopes(final String field) {
        return false;
    }
    
    @Override
    public boolean presentInScope(final String field, final String filteringScope) {
        return false;
    }
    
    @Override
    public EntityGraph remove(final String name) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Set<String> getFilteringScopes() {
        return Collections.emptySet();
    }
    
    @Override
    public Set<String> getClassFilteringScopes() {
        return Collections.emptySet();
    }
    
    @Override
    public EntityGraph addFilteringScopes(final Set<String> filteringScopes) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final EmptyEntityGraphImpl that = (EmptyEntityGraphImpl)o;
        return this.clazz.equals(that.clazz);
    }
    
    @Override
    public int hashCode() {
        return this.clazz.hashCode();
    }
}
