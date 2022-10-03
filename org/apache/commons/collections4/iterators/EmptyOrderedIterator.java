package org.apache.commons.collections4.iterators;

import org.apache.commons.collections4.ResettableIterator;
import org.apache.commons.collections4.OrderedIterator;

public class EmptyOrderedIterator<E> extends AbstractEmptyIterator<E> implements OrderedIterator<E>, ResettableIterator<E>
{
    public static final OrderedIterator INSTANCE;
    
    public static <E> OrderedIterator<E> emptyOrderedIterator() {
        return EmptyOrderedIterator.INSTANCE;
    }
    
    protected EmptyOrderedIterator() {
    }
    
    static {
        INSTANCE = new EmptyOrderedIterator();
    }
}
