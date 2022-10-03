package org.glassfish.jersey.message.filtering.spi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import org.glassfish.jersey.spi.Contract;

@Contract
public interface ObjectProvider<T>
{
    T getFilteringObject(final Type p0, final boolean p1, final Annotation... p2);
}
