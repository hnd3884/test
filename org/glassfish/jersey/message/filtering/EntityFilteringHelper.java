package org.glassfish.jersey.message.filtering;

import java.util.List;
import java.util.ArrayList;
import org.glassfish.jersey.message.filtering.spi.FilteringHelper;
import java.util.HashSet;
import java.util.Collections;
import java.util.Set;
import java.lang.annotation.Annotation;

final class EntityFilteringHelper
{
    public static Set<String> getFilteringScopes(final Annotation[] annotations) {
        return getFilteringScopes(annotations, true);
    }
    
    public static Set<String> getFilteringScopes(Annotation[] annotations, final boolean filter) {
        if (annotations.length == 0) {
            return Collections.emptySet();
        }
        final Set<String> contexts = new HashSet<String>(annotations.length);
        final Annotation[] array;
        annotations = (array = (filter ? getFilteringAnnotations(annotations) : annotations));
        for (final Annotation annotation : array) {
            contexts.add(annotation.annotationType().getName());
        }
        return contexts;
    }
    
    public static Annotation[] getFilteringAnnotations(final Annotation[] annotations) {
        if (annotations == null || annotations.length == 0) {
            return FilteringHelper.EMPTY_ANNOTATIONS;
        }
        final List<Annotation> filteringAnnotations = new ArrayList<Annotation>(annotations.length);
        for (final Annotation annotation : annotations) {
            final Class<? extends Annotation> annotationType = annotation.annotationType();
            for (final Annotation metaAnnotation : annotationType.getDeclaredAnnotations()) {
                if (metaAnnotation instanceof EntityFiltering) {
                    filteringAnnotations.add(annotation);
                }
            }
        }
        return filteringAnnotations.toArray(new Annotation[filteringAnnotations.size()]);
    }
    
    public static <T extends Annotation> T getAnnotation(final Annotation[] annotations, final Class<T> clazz) {
        for (final Annotation annotation : annotations) {
            if (annotation.annotationType().getClass().isAssignableFrom(clazz)) {
                return (T)annotation;
            }
        }
        return null;
    }
    
    private EntityFilteringHelper() {
    }
}
