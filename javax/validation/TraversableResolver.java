package javax.validation;

import java.lang.annotation.ElementType;

public interface TraversableResolver
{
    boolean isReachable(final Object p0, final Path.Node p1, final Class<?> p2, final Path p3, final ElementType p4);
    
    boolean isCascadable(final Object p0, final Path.Node p1, final Class<?> p2, final Path p3, final ElementType p4);
}
