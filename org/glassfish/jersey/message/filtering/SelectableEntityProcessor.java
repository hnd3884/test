package org.glassfish.jersey.message.filtering;

import java.util.Set;
import java.util.HashSet;
import org.glassfish.jersey.message.filtering.spi.EntityProcessor;
import org.glassfish.jersey.message.filtering.spi.EntityGraph;
import java.lang.annotation.Annotation;
import javax.annotation.Priority;
import javax.inject.Singleton;
import org.glassfish.jersey.message.filtering.spi.AbstractEntityProcessor;

@Singleton
@Priority(2147478647)
public class SelectableEntityProcessor extends AbstractEntityProcessor
{
    @Override
    protected EntityProcessor.Result process(final String fieldName, final Class<?> fieldClass, final Annotation[] fieldAnnotations, final Annotation[] annotations, final EntityGraph graph) {
        if (fieldName != null) {
            final Set<String> scopes = new HashSet<String>();
            scopes.add(SelectableScopeResolver.DEFAULT_SCOPE);
            scopes.add(SelectableScopeResolver.PREFIX + fieldName);
            this.addFilteringScopes(fieldName, fieldClass, scopes, graph);
        }
        return EntityProcessor.Result.APPLY;
    }
}
