package org.glassfish.jersey.message.filtering.spi;

import java.util.Set;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.AccessibleObject;
import org.glassfish.jersey.internal.util.ReflectionHelper;

public abstract class AbstractEntityProcessor implements EntityProcessor
{
    @Override
    public Result process(final EntityProcessorContext context) {
        switch (context.getType()) {
            case CLASS_READER:
            case CLASS_WRITER: {
                return this.process(null, null, FilteringHelper.EMPTY_ANNOTATIONS, context.getEntityClass().getDeclaredAnnotations(), context.getEntityGraph());
            }
            case PROPERTY_READER:
            case PROPERTY_WRITER:
            case METHOD_READER:
            case METHOD_WRITER: {
                final Field field = context.getField();
                final Method method = context.getMethod();
                final boolean isProperty = field != null;
                String fieldName;
                Type fieldType;
                if (isProperty) {
                    fieldName = field.getName();
                    fieldType = field.getGenericType();
                }
                else {
                    fieldName = ReflectionHelper.getPropertyName(method);
                    fieldType = (ReflectionHelper.isGetter(method) ? method.getGenericReturnType() : method.getGenericParameterTypes()[0]);
                }
                return this.process(fieldName, FilteringHelper.getEntityClass(fieldType), this.getAnnotations(field), this.getAnnotations(method), context.getEntityGraph());
            }
            default: {
                return Result.SKIP;
            }
        }
    }
    
    private Annotation[] getAnnotations(final AccessibleObject accessibleObject) {
        return (accessibleObject == null) ? FilteringHelper.EMPTY_ANNOTATIONS : accessibleObject.getDeclaredAnnotations();
    }
    
    protected Result process(final String fieldName, final Class<?> fieldClass, final Annotation[] fieldAnnotations, final Annotation[] annotations, final EntityGraph graph) {
        return Result.SKIP;
    }
    
    protected final void addFilteringScopes(final String field, final Class<?> fieldClass, final Set<String> filteringScopes, final EntityGraph graph) {
        if (!filteringScopes.isEmpty()) {
            if (FilteringHelper.filterableEntityClass(fieldClass)) {
                graph.addSubgraph(field, fieldClass, filteringScopes);
            }
            else {
                graph.addField(field, filteringScopes);
            }
        }
    }
    
    protected final void addGlobalScopes(final Set<String> filteringScopes, final EntityGraph graph) {
        graph.addFilteringScopes(filteringScopes);
    }
}
