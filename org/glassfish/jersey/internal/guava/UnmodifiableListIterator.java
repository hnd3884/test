package org.glassfish.jersey.internal.guava;

import java.util.ListIterator;

public abstract class UnmodifiableListIterator<E> extends UnmodifiableIterator<E> implements ListIterator<E>
{
    UnmodifiableListIterator() {
    }
    
    @Deprecated
    @Override
    public final void add(final E e) {
        throw new UnsupportedOperationException();
    }
    
    @Deprecated
    @Override
    public final void set(final E e) {
        throw new UnsupportedOperationException();
    }
}
