package org.glassfish.jersey.message.filtering;

import org.glassfish.jersey.message.filtering.spi.ScopeProvider;
import java.util.HashMap;
import java.util.Map;
import java.util.Collections;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.HashSet;
import org.glassfish.jersey.internal.guava.HashBasedTable;
import org.glassfish.jersey.internal.guava.Table;
import org.glassfish.jersey.internal.guava.HashMultimap;
import java.util.Set;
import org.glassfish.jersey.message.filtering.spi.EntityGraph;

final class EntityGraphImpl implements EntityGraph
{
    private final Class<?> entityClass;
    private final Set<String> globalScopes;
    private final Set<String> localScopes;
    private final HashMultimap<String, String> fields;
    private final Table<String, String, Class<?>> subgraphs;
    
    public EntityGraphImpl(final Class<?> entityClass) {
        this.entityClass = entityClass;
        this.fields = (HashMultimap<String, String>)HashMultimap.create();
        this.subgraphs = (Table<String, String, Class<?>>)HashBasedTable.create();
        this.globalScopes = new HashSet<String>();
        this.localScopes = new HashSet<String>();
    }
    
    @Override
    public EntityGraphImpl addField(final String fieldName) {
        return this.addField(fieldName, this.globalScopes);
    }
    
    @Override
    public EntityGraphImpl addField(final String fieldName, final String... filteringScopes) {
        return this.addField(fieldName, (Set<String>)Arrays.stream(filteringScopes).collect((Collector<? super String, ?, Set<? super String>>)Collectors.toSet()));
    }
    
    @Override
    public EntityGraphImpl addField(final String fieldName, final Set<String> filteringScopes) {
        for (final String filteringScope : filteringScopes) {
            this.createFilteringScope(filteringScope);
            this.fields.get((Object)filteringScope).add(fieldName);
        }
        return this;
    }
    
    @Override
    public EntityGraphImpl addFilteringScopes(final Set<String> filteringScopes) {
        this.globalScopes.addAll(filteringScopes);
        return this;
    }
    
    @Override
    public EntityGraphImpl addSubgraph(final String fieldName, final Class<?> fieldClass) {
        return this.addSubgraph(fieldName, fieldClass, this.globalScopes);
    }
    
    @Override
    public EntityGraphImpl addSubgraph(final String fieldName, final Class<?> fieldClass, final String... filteringScopes) {
        return this.addSubgraph(fieldName, fieldClass, (Set<String>)Arrays.stream(filteringScopes).collect((Collector<? super String, ?, Set<? super String>>)Collectors.toSet()));
    }
    
    @Override
    public EntityGraphImpl addSubgraph(final String fieldName, final Class<?> fieldClass, final Set<String> filteringScopes) {
        for (final String filteringScope : filteringScopes) {
            this.createFilteringScope(filteringScope);
            this.subgraphs.put((Object)filteringScope, (Object)fieldName, (Object)fieldClass);
        }
        return this;
    }
    
    @Override
    public Class<?> getEntityClass() {
        return this.entityClass;
    }
    
    @Override
    public Set<String> getFields(final String filteringScope) {
        return this.fields.containsKey((Object)filteringScope) ? Collections.unmodifiableSet((Set<? extends String>)this.fields.get((Object)filteringScope)) : Collections.emptySet();
    }
    
    @Override
    public Set<String> getFields(final String... filteringScopes) {
        return (filteringScopes.length == 0) ? Collections.emptySet() : ((filteringScopes.length == 1) ? this.getFields(filteringScopes[0]) : this.getFields((Set<String>)Arrays.stream(filteringScopes).collect((Collector<? super String, ?, Set<? super String>>)Collectors.toSet())));
    }
    
    @Override
    public Set<String> getFields(final Set<String> filteringScopes) {
        final Set<String> matched = new HashSet<String>();
        for (final String filteringContext : filteringScopes) {
            matched.addAll(this.fields.get((Object)filteringContext));
        }
        return matched;
    }
    
    @Override
    public Set<String> getFilteringScopes() {
        final HashSet<String> strings = new HashSet<String>(this.globalScopes);
        strings.addAll((Collection<?>)this.localScopes);
        return Collections.unmodifiableSet((Set<? extends String>)strings);
    }
    
    @Override
    public Set<String> getClassFilteringScopes() {
        return Collections.unmodifiableSet((Set<? extends String>)this.globalScopes);
    }
    
    @Override
    public Map<String, Class<?>> getSubgraphs(final String filteringScope) {
        return this.subgraphs.containsRow((Object)filteringScope) ? Collections.unmodifiableMap((Map<? extends String, ? extends Class<?>>)this.subgraphs.row((Object)filteringScope)) : Collections.emptyMap();
    }
    
    @Override
    public Map<String, Class<?>> getSubgraphs(final String... filteringScopes) {
        return (filteringScopes.length == 0) ? Collections.emptyMap() : ((filteringScopes.length == 1) ? this.getSubgraphs(filteringScopes[0]) : this.getSubgraphs((Set<String>)Arrays.stream(filteringScopes).collect((Collector<? super String, ?, Set<? super String>>)Collectors.toSet())));
    }
    
    @Override
    public Map<String, Class<?>> getSubgraphs(final Set<String> filteringScopes) {
        final Map<String, Class<?>> matched = new HashMap<String, Class<?>>();
        for (final String filteringContext : filteringScopes) {
            matched.putAll(this.subgraphs.row((Object)filteringContext));
        }
        return matched;
    }
    
    @Override
    public boolean presentInScopes(final String name) {
        return this.fields.containsValue((Object)name) || this.subgraphs.containsColumn((Object)name);
    }
    
    @Override
    public boolean presentInScope(final String field, final String filteringScope) {
        return this.fields.containsEntry((Object)filteringScope, (Object)field) || this.subgraphs.contains((Object)filteringScope, (Object)field);
    }
    
    @Override
    public EntityGraphImpl remove(final String fieldName) {
        for (final String scope : this.getFilteringScopes()) {
            if (this.fields.containsEntry((Object)scope, (Object)fieldName)) {
                this.fields.remove((Object)scope, (Object)fieldName);
            }
            if (this.subgraphs.containsColumn((Object)fieldName)) {
                this.subgraphs.remove((Object)scope, (Object)fieldName);
            }
        }
        return this;
    }
    
    private void createFilteringScope(final String filteringScope) {
        if (!this.getFilteringScopes().contains(filteringScope)) {
            if (this.localScopes.contains(ScopeProvider.DEFAULT_SCOPE)) {
                this.fields.putAll((Object)filteringScope, (Iterable)this.fields.get((Object)ScopeProvider.DEFAULT_SCOPE));
                final Map<String, Class<?>> row = this.subgraphs.row((Object)ScopeProvider.DEFAULT_SCOPE);
                for (final Map.Entry<String, Class<?>> entry : row.entrySet()) {
                    this.subgraphs.put((Object)filteringScope, (Object)entry.getKey(), (Object)entry.getValue());
                }
            }
            this.localScopes.add(filteringScope);
        }
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final EntityGraphImpl that = (EntityGraphImpl)o;
        return this.entityClass.equals(that.entityClass) && this.fields.equals((Object)that.fields) && this.globalScopes.equals(that.globalScopes) && this.localScopes.equals(that.localScopes) && this.subgraphs.equals((Object)that.subgraphs);
    }
    
    @Override
    public int hashCode() {
        int result = this.entityClass.hashCode();
        result = 53 * result + this.globalScopes.hashCode();
        result = 53 * result + this.localScopes.hashCode();
        result = 53 * result + this.fields.hashCode();
        result = 53 * result + this.subgraphs.hashCode();
        return result;
    }
}
