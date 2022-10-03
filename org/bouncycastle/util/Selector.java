package org.bouncycastle.util;

public interface Selector<T> extends Cloneable
{
    boolean match(final T p0);
    
    Object clone();
}
