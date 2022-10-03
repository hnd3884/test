package org.glassfish.hk2.utilities.binding;

import java.lang.reflect.Type;
import java.lang.annotation.Annotation;
import java.util.List;
import org.glassfish.hk2.api.HK2Loader;
import org.glassfish.hk2.api.TypeLiteral;

public interface NamedBindingBuilder<T> extends BindingBuilder<T>
{
    NamedBindingBuilder<T> to(final Class<? super T> p0);
    
    NamedBindingBuilder<T> to(final TypeLiteral<?> p0);
    
    NamedBindingBuilder<T> loadedBy(final HK2Loader p0);
    
    NamedBindingBuilder<T> withMetadata(final String p0, final String p1);
    
    NamedBindingBuilder<T> withMetadata(final String p0, final List<String> p1);
    
    NamedBindingBuilder<T> qualifiedBy(final Annotation p0);
    
    ScopedNamedBindingBuilder<T> in(final Class<? extends Annotation> p0);
    
    void ranked(final int p0);
    
    NamedBindingBuilder<T> proxy(final boolean p0);
    
    NamedBindingBuilder<T> asType(final Type p0);
}
