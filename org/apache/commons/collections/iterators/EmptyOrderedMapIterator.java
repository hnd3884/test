package org.apache.commons.collections.iterators;

import org.apache.commons.collections.ResettableIterator;
import org.apache.commons.collections.OrderedMapIterator;

public class EmptyOrderedMapIterator extends AbstractEmptyIterator implements OrderedMapIterator, ResettableIterator
{
    public static final OrderedMapIterator INSTANCE;
    
    protected EmptyOrderedMapIterator() {
    }
    
    static {
        INSTANCE = new EmptyOrderedMapIterator();
    }
}
