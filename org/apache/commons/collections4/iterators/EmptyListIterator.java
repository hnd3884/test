package org.apache.commons.collections4.iterators;

import java.util.ListIterator;
import org.apache.commons.collections4.ResettableListIterator;

public class EmptyListIterator<E> extends AbstractEmptyIterator<E> implements ResettableListIterator<E>
{
    public static final ResettableListIterator RESETTABLE_INSTANCE;
    public static final ListIterator INSTANCE;
    
    public static <E> ResettableListIterator<E> resettableEmptyListIterator() {
        return EmptyListIterator.RESETTABLE_INSTANCE;
    }
    
    public static <E> ListIterator<E> emptyListIterator() {
        return EmptyListIterator.INSTANCE;
    }
    
    protected EmptyListIterator() {
    }
    
    static {
        RESETTABLE_INSTANCE = new EmptyListIterator();
        INSTANCE = EmptyListIterator.RESETTABLE_INSTANCE;
    }
}
