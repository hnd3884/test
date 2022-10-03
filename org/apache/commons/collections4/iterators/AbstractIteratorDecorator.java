package org.apache.commons.collections4.iterators;

import java.util.Iterator;

public abstract class AbstractIteratorDecorator<E> extends AbstractUntypedIteratorDecorator<E, E>
{
    protected AbstractIteratorDecorator(final Iterator<E> iterator) {
        super(iterator);
    }
    
    @Override
    public E next() {
        return this.getIterator().next();
    }
}
