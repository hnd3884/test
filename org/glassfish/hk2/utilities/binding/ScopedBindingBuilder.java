package org.glassfish.hk2.utilities.binding;

import java.lang.annotation.Annotation;
import java.util.List;
import org.glassfish.hk2.api.HK2Loader;
import org.glassfish.hk2.api.TypeLiteral;

public interface ScopedBindingBuilder<T> extends BindingBuilder<T>
{
    ScopedBindingBuilder<T> to(final Class<? super T> p0);
    
    ScopedBindingBuilder<T> to(final TypeLiteral<?> p0);
    
    ScopedBindingBuilder<T> loadedBy(final HK2Loader p0);
    
    ScopedBindingBuilder<T> withMetadata(final String p0, final String p1);
    
    ScopedBindingBuilder<T> withMetadata(final String p0, final List<String> p1);
    
    ScopedBindingBuilder<T> qualifiedBy(final Annotation p0);
    
    ScopedNamedBindingBuilder<T> named(final String p0);
    
    void ranked(final int p0);
    
    ScopedBindingBuilder<T> proxy(final boolean p0);
    
    ScopedBindingBuilder<T> proxyForSameScope(final boolean p0);
    
    ScopedBindingBuilder<T> analyzeWith(final String p0);
}
