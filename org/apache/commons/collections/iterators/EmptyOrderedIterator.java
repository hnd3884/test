package org.apache.commons.collections.iterators;

import org.apache.commons.collections.ResettableIterator;
import org.apache.commons.collections.OrderedIterator;

public class EmptyOrderedIterator extends AbstractEmptyIterator implements OrderedIterator, ResettableIterator
{
    public static final OrderedIterator INSTANCE;
    
    protected EmptyOrderedIterator() {
    }
    
    static {
        INSTANCE = new EmptyOrderedIterator();
    }
}
