package org.glassfish.jersey.message.filtering;

import java.util.Set;
import java.util.Collection;
import java.util.HashSet;
import org.glassfish.jersey.message.filtering.spi.EntityGraph;
import java.lang.annotation.Annotation;
import org.glassfish.jersey.message.filtering.spi.EntityProcessor;
import org.glassfish.jersey.message.filtering.spi.EntityProcessorContext;
import javax.annotation.Priority;
import javax.inject.Singleton;
import org.glassfish.jersey.message.filtering.spi.AbstractEntityProcessor;

@Singleton
@Priority(2147481647)
final class EntityFilteringProcessor extends AbstractEntityProcessor
{
    @Override
    public EntityProcessor.Result process(final EntityProcessorContext context) {
        switch (context.getType()) {
            case CLASS_READER:
            case CLASS_WRITER: {
                this.addGlobalScopes(EntityFilteringHelper.getFilteringScopes(context.getEntityClass().getDeclaredAnnotations()), context.getEntityGraph());
                break;
            }
        }
        return super.process(context);
    }
    
    @Override
    protected EntityProcessor.Result process(final String field, final Class<?> fieldClass, final Annotation[] fieldAnnotations, final Annotation[] annotations, final EntityGraph graph) {
        final Set<String> filteringScopes = new HashSet<String>();
        if (fieldAnnotations.length > 0) {
            filteringScopes.addAll(EntityFilteringHelper.getFilteringScopes(fieldAnnotations));
        }
        if (annotations.length > 0) {
            filteringScopes.addAll(EntityFilteringHelper.getFilteringScopes(annotations));
        }
        if (!filteringScopes.isEmpty()) {
            if (field != null) {
                this.addFilteringScopes(field, fieldClass, filteringScopes, graph);
            }
            else {
                this.addGlobalScopes(filteringScopes, graph);
            }
            return EntityProcessor.Result.APPLY;
        }
        return EntityProcessor.Result.SKIP;
    }
}
