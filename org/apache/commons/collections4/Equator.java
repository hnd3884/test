package org.apache.commons.collections4;

public interface Equator<T>
{
    boolean equate(final T p0, final T p1);
    
    int hash(final T p0);
}
