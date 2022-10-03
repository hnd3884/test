package org.apache.commons.collections.iterators;

import java.util.Iterator;
import org.apache.commons.collections.ResettableIterator;

public class EmptyIterator extends AbstractEmptyIterator implements ResettableIterator
{
    public static final ResettableIterator RESETTABLE_INSTANCE;
    public static final Iterator INSTANCE;
    
    protected EmptyIterator() {
    }
    
    static {
        RESETTABLE_INSTANCE = new EmptyIterator();
        INSTANCE = EmptyIterator.RESETTABLE_INSTANCE;
    }
}
