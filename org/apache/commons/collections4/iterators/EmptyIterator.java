package org.apache.commons.collections4.iterators;

import java.util.Iterator;
import org.apache.commons.collections4.ResettableIterator;

public class EmptyIterator<E> extends AbstractEmptyIterator<E> implements ResettableIterator<E>
{
    public static final ResettableIterator RESETTABLE_INSTANCE;
    public static final Iterator INSTANCE;
    
    public static <E> ResettableIterator<E> resettableEmptyIterator() {
        return EmptyIterator.RESETTABLE_INSTANCE;
    }
    
    public static <E> Iterator<E> emptyIterator() {
        return EmptyIterator.INSTANCE;
    }
    
    protected EmptyIterator() {
    }
    
    static {
        RESETTABLE_INSTANCE = new EmptyIterator();
        INSTANCE = EmptyIterator.RESETTABLE_INSTANCE;
    }
}
