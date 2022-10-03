package org.glassfish.jersey.message.filtering;

import org.glassfish.jersey.message.filtering.spi.ObjectGraph;
import java.util.Set;
import java.util.Collections;
import java.util.Map;
import org.glassfish.jersey.internal.util.collection.DataStructures;
import org.glassfish.jersey.message.filtering.spi.EntityGraph;
import java.util.concurrent.ConcurrentMap;
import org.glassfish.jersey.message.filtering.spi.EntityGraphProvider;

final class EntityGraphProviderImpl implements EntityGraphProvider
{
    private final ConcurrentMap<Class<?>, EntityGraph> writerClassToGraph;
    private final ConcurrentMap<Class<?>, EntityGraph> readerClassToGraph;
    
    EntityGraphProviderImpl() {
        this.writerClassToGraph = DataStructures.createConcurrentMap();
        this.readerClassToGraph = DataStructures.createConcurrentMap();
    }
    
    @Override
    public EntityGraph getOrCreateEntityGraph(final Class<?> entityClass, final boolean forWriter) {
        final ConcurrentMap<Class<?>, EntityGraph> classToGraph = forWriter ? this.writerClassToGraph : this.readerClassToGraph;
        if (!classToGraph.containsKey(entityClass)) {
            classToGraph.putIfAbsent(entityClass, new EntityGraphImpl(entityClass));
        }
        return classToGraph.get(entityClass);
    }
    
    @Override
    public EntityGraph getOrCreateEmptyEntityGraph(final Class<?> entityClass, final boolean forWriter) {
        final ConcurrentMap<Class<?>, EntityGraph> classToGraph = forWriter ? this.writerClassToGraph : this.readerClassToGraph;
        if (!classToGraph.containsKey(entityClass) || !(classToGraph.get(entityClass) instanceof EmptyEntityGraphImpl)) {
            classToGraph.put(entityClass, new EmptyEntityGraphImpl(entityClass));
        }
        return classToGraph.get(entityClass);
    }
    
    public Map<Class<?>, EntityGraph> asMap(final boolean forWriter) {
        return Collections.unmodifiableMap((Map<? extends Class<?>, ? extends EntityGraph>)(forWriter ? this.writerClassToGraph : this.readerClassToGraph));
    }
    
    @Override
    public boolean containsEntityGraph(final Class<?> entityClass, final boolean forWriter) {
        return forWriter ? this.writerClassToGraph.containsKey(entityClass) : this.readerClassToGraph.containsKey(entityClass);
    }
    
    @Override
    public ObjectGraph createObjectGraph(final Class<?> entityClass, final Set<String> filteringScopes, final boolean forWriter) {
        final Map<Class<?>, EntityGraph> classToGraph = forWriter ? this.writerClassToGraph : this.readerClassToGraph;
        final EntityGraph entityGraph = classToGraph.get(entityClass);
        return (entityGraph == null) ? new EmptyObjectGraph(entityClass) : new ObjectGraphImpl(classToGraph, entityGraph, filteringScopes);
    }
}
