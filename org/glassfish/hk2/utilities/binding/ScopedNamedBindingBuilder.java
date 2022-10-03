package org.glassfish.hk2.utilities.binding;

import java.lang.annotation.Annotation;
import java.util.List;
import org.glassfish.hk2.api.HK2Loader;
import org.glassfish.hk2.api.TypeLiteral;

public interface ScopedNamedBindingBuilder<T> extends BindingBuilder<T>
{
    ScopedNamedBindingBuilder<T> to(final Class<? super T> p0);
    
    ScopedNamedBindingBuilder<T> to(final TypeLiteral<?> p0);
    
    ScopedNamedBindingBuilder<T> loadedBy(final HK2Loader p0);
    
    ScopedNamedBindingBuilder<T> withMetadata(final String p0, final String p1);
    
    ScopedNamedBindingBuilder<T> withMetadata(final String p0, final List<String> p1);
    
    ScopedNamedBindingBuilder<T> qualifiedBy(final Annotation p0);
    
    void ranked(final int p0);
    
    ScopedNamedBindingBuilder<T> proxy(final boolean p0);
    
    ScopedNamedBindingBuilder<T> analyzeWith(final String p0);
}
