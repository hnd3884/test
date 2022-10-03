package org.glassfish.jersey.message.filtering;

import java.util.Set;
import org.glassfish.jersey.message.filtering.spi.EntityProcessor;
import org.glassfish.jersey.message.filtering.spi.EntityGraph;
import java.lang.annotation.Annotation;
import javax.annotation.Priority;
import javax.inject.Singleton;
import org.glassfish.jersey.message.filtering.spi.AbstractEntityProcessor;

@Singleton
@Priority(2147480647)
final class SecurityEntityProcessor extends AbstractEntityProcessor
{
    @Override
    protected EntityProcessor.Result process(final String fieldName, final Class<?> fieldClass, final Annotation[] fieldAnnotations, final Annotation[] annotations, final EntityGraph graph) {
        if (annotations.length > 0) {
            final Set<String> filteringScopes = SecurityHelper.getFilteringScopes(annotations);
            if (filteringScopes == null) {
                return EntityProcessor.Result.ROLLBACK;
            }
            if (!filteringScopes.isEmpty()) {
                if (fieldName != null) {
                    this.addFilteringScopes(fieldName, fieldClass, filteringScopes, graph);
                }
                else {
                    this.addGlobalScopes(filteringScopes, graph);
                }
            }
        }
        return EntityProcessor.Result.APPLY;
    }
}
