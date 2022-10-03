package org.glassfish.hk2.utilities.binding;

import java.lang.annotation.Annotation;
import java.util.List;
import org.glassfish.hk2.api.HK2Loader;
import java.lang.reflect.Type;
import org.glassfish.hk2.api.TypeLiteral;

public interface ServiceBindingBuilder<T> extends BindingBuilder<T>
{
    ServiceBindingBuilder<T> to(final Class<? super T> p0);
    
    ServiceBindingBuilder<T> to(final TypeLiteral<?> p0);
    
    ServiceBindingBuilder<T> to(final Type p0);
    
    ServiceBindingBuilder<T> loadedBy(final HK2Loader p0);
    
    ServiceBindingBuilder<T> withMetadata(final String p0, final String p1);
    
    ServiceBindingBuilder<T> withMetadata(final String p0, final List<String> p1);
    
    ServiceBindingBuilder<T> qualifiedBy(final Annotation p0);
    
    ScopedBindingBuilder<T> in(final Annotation p0);
    
    ScopedBindingBuilder<T> in(final Class<? extends Annotation> p0);
    
    NamedBindingBuilder<T> named(final String p0);
    
    void ranked(final int p0);
    
    ServiceBindingBuilder<T> proxy(final boolean p0);
    
    ServiceBindingBuilder<T> proxyForSameScope(final boolean p0);
    
    ServiceBindingBuilder<T> analyzeWith(final String p0);
    
    ServiceBindingBuilder<T> asType(final Type p0);
}
