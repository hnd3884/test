package org.apache.commons.collections.iterators;

import org.apache.commons.collections.ResettableIterator;
import org.apache.commons.collections.MapIterator;

public class EmptyMapIterator extends AbstractEmptyIterator implements MapIterator, ResettableIterator
{
    public static final MapIterator INSTANCE;
    
    protected EmptyMapIterator() {
    }
    
    static {
        INSTANCE = new EmptyMapIterator();
    }
}
