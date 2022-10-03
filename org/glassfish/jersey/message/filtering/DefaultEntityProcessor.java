package org.glassfish.jersey.message.filtering;

import java.lang.reflect.Type;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import org.glassfish.jersey.message.filtering.spi.EntityGraph;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import org.glassfish.jersey.message.filtering.spi.FilteringHelper;
import org.glassfish.jersey.message.filtering.spi.EntityProcessor;
import org.glassfish.jersey.message.filtering.spi.EntityProcessorContext;
import javax.annotation.Priority;
import javax.inject.Singleton;
import org.glassfish.jersey.message.filtering.spi.AbstractEntityProcessor;

@Singleton
@Priority(2147482647)
final class DefaultEntityProcessor extends AbstractEntityProcessor
{
    @Override
    public EntityProcessor.Result process(final EntityProcessorContext context) {
        switch (context.getType()) {
            case CLASS_READER:
            case CLASS_WRITER: {
                final EntityGraph graph = context.getEntityGraph();
                if (graph.getFilteringScopes().isEmpty()) {
                    graph.addFilteringScopes(FilteringHelper.getDefaultFilteringScope());
                }
                return EntityProcessor.Result.APPLY;
            }
            case PROPERTY_READER:
            case PROPERTY_WRITER: {
                final Field field = context.getField();
                this.process(context.getEntityGraph(), field.getName(), field.getGenericType());
                return EntityProcessor.Result.APPLY;
            }
            case METHOD_READER:
            case METHOD_WRITER: {
                final Method method = context.getMethod();
                this.process(context.getEntityGraph(), ReflectionHelper.getPropertyName(method), method.getGenericReturnType());
                return EntityProcessor.Result.APPLY;
            }
            default: {
                return EntityProcessor.Result.SKIP;
            }
        }
    }
    
    private void process(final EntityGraph graph, final String fieldName, final Type fieldType) {
        if (!graph.presentInScopes(fieldName)) {
            this.addFilteringScopes(fieldName, FilteringHelper.getEntityClass(fieldType), graph.getClassFilteringScopes(), graph);
        }
    }
}
