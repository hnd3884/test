package org.antlr.v4.runtime.misc;

public interface EqualityComparator<T>
{
    int hashCode(final T p0);
    
    boolean equals(final T p0, final T p1);
}
