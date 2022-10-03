package org.glassfish.jersey.message.filtering;

import java.lang.reflect.Modifier;
import org.glassfish.jersey.message.filtering.spi.FilteringHelper;
import java.security.PrivilegedAction;
import java.security.AccessController;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import java.lang.reflect.Field;
import org.glassfish.jersey.message.filtering.spi.EntityProcessorContext;
import java.util.Iterator;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import org.glassfish.jersey.message.filtering.spi.EntityGraph;
import java.util.HashSet;
import java.util.Spliterator;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.glassfish.jersey.internal.inject.Providers;
import org.glassfish.jersey.model.internal.RankedComparator;
import org.glassfish.jersey.internal.inject.InjectionManager;
import javax.inject.Inject;
import org.glassfish.jersey.message.filtering.spi.EntityGraphProvider;
import org.glassfish.jersey.message.filtering.spi.EntityProcessor;
import java.util.List;
import javax.inject.Singleton;
import org.glassfish.jersey.message.filtering.spi.EntityInspector;

@Singleton
final class EntityInspectorImpl implements EntityInspector
{
    private final List<EntityProcessor> entityProcessors;
    @Inject
    private EntityGraphProvider graphProvider;
    
    @Inject
    public EntityInspectorImpl(final InjectionManager injectionManager) {
        final Spliterator<EntityProcessor> entities = Providers.getAllProviders(injectionManager, (Class)EntityProcessor.class, new RankedComparator()).spliterator();
        this.entityProcessors = StreamSupport.stream(entities, false).collect((Collector<? super EntityProcessor, ?, List<EntityProcessor>>)Collectors.toList());
    }
    
    @Override
    public void inspect(final Class<?> entityClass, final boolean forWriter) {
        if (!this.graphProvider.containsEntityGraph(entityClass, forWriter)) {
            final EntityGraph graph = this.graphProvider.getOrCreateEntityGraph(entityClass, forWriter);
            final Set<Class<?>> inspect = new HashSet<Class<?>>();
            if (!this.inspectEntityClass(entityClass, graph, forWriter)) {
                final Map<String, Method> unmatchedAccessors = this.inspectEntityProperties(entityClass, graph, inspect, forWriter);
                this.inspectStandaloneAccessors(unmatchedAccessors, graph, forWriter);
                for (final Class<?> clazz : inspect) {
                    this.inspect(clazz, forWriter);
                }
            }
        }
    }
    
    private boolean inspectEntityClass(final Class<?> entityClass, final EntityGraph graph, final boolean forWriter) {
        final EntityProcessorContextImpl context = new EntityProcessorContextImpl(forWriter ? EntityProcessorContext.Type.CLASS_WRITER : EntityProcessorContext.Type.CLASS_READER, entityClass, graph);
        for (final EntityProcessor processor : this.entityProcessors) {
            final EntityProcessor.Result result = processor.process(context);
            if (EntityProcessor.Result.ROLLBACK == result) {
                this.graphProvider.getOrCreateEmptyEntityGraph(entityClass, false);
                return true;
            }
        }
        return false;
    }
    
    private Map<String, Method> inspectEntityProperties(final Class<?> entityClass, final EntityGraph graph, final Set<Class<?>> inspect, final boolean forWriter) {
        final Field[] fields = AccessController.doPrivileged((PrivilegedAction<Field[]>)ReflectionHelper.getAllFieldsPA((Class)entityClass));
        final Map<String, Method> methods = FilteringHelper.getPropertyMethods(entityClass, forWriter);
        for (final Field field : fields) {
            if (!Modifier.isStatic(field.getModifiers())) {
                final String name = field.getName();
                final Class<?> clazz = FilteringHelper.getEntityClass(field.getGenericType());
                final Method method = methods.remove(name);
                final EntityProcessorContextImpl context = new EntityProcessorContextImpl(forWriter ? EntityProcessorContext.Type.PROPERTY_WRITER : EntityProcessorContext.Type.PROPERTY_READER, field, method, graph);
                boolean rollback = false;
                for (final EntityProcessor processor : this.entityProcessors) {
                    final EntityProcessor.Result result = processor.process(context);
                    if (EntityProcessor.Result.ROLLBACK == result) {
                        rollback = true;
                        graph.remove(name);
                        break;
                    }
                }
                if (!rollback && FilteringHelper.filterableEntityClass(clazz)) {
                    inspect.add(clazz);
                }
            }
        }
        return methods;
    }
    
    private void inspectStandaloneAccessors(final Map<String, Method> unprocessedAccessors, final EntityGraph graph, final boolean forWriter) {
        for (final Map.Entry<String, Method> entry : unprocessedAccessors.entrySet()) {
            final EntityProcessorContextImpl context = new EntityProcessorContextImpl(forWriter ? EntityProcessorContext.Type.METHOD_WRITER : EntityProcessorContext.Type.METHOD_READER, entry.getValue(), graph);
            for (final EntityProcessor processor : this.entityProcessors) {
                final EntityProcessor.Result result = processor.process(context);
                if (EntityProcessor.Result.ROLLBACK == result) {
                    graph.remove(entry.getKey());
                    break;
                }
            }
        }
    }
}
