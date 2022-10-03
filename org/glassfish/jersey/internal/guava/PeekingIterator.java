package org.glassfish.jersey.internal.guava;

import java.util.Iterator;

public interface PeekingIterator<E> extends Iterator<E>
{
    E peek();
    
    E next();
    
    void remove();
}
