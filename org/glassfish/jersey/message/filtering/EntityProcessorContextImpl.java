package org.glassfish.jersey.message.filtering;

import org.glassfish.jersey.message.filtering.spi.EntityGraph;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import org.glassfish.jersey.message.filtering.spi.EntityProcessorContext;

final class EntityProcessorContextImpl implements EntityProcessorContext
{
    private final Type type;
    private final Class<?> clazz;
    private final Field field;
    private final Method method;
    private final EntityGraph graph;
    
    public EntityProcessorContextImpl(final Type type, final Class<?> clazz, final EntityGraph graph) {
        this(type, clazz, null, null, graph);
    }
    
    public EntityProcessorContextImpl(final Type type, final Field field, final Method method, final EntityGraph graph) {
        this(type, null, field, method, graph);
    }
    
    public EntityProcessorContextImpl(final Type type, final Method method, final EntityGraph graph) {
        this(type, null, null, method, graph);
    }
    
    public EntityProcessorContextImpl(final Type type, final Class<?> clazz, final Field field, final Method method, final EntityGraph graph) {
        this.type = type;
        this.clazz = clazz;
        this.field = field;
        this.method = method;
        this.graph = graph;
    }
    
    @Override
    public Type getType() {
        return this.type;
    }
    
    @Override
    public Class<?> getEntityClass() {
        return this.clazz;
    }
    
    @Override
    public Field getField() {
        return this.field;
    }
    
    @Override
    public Method getMethod() {
        return this.method;
    }
    
    @Override
    public EntityGraph getEntityGraph() {
        return this.graph;
    }
}
