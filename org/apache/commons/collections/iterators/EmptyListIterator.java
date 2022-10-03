package org.apache.commons.collections.iterators;

import java.util.ListIterator;
import org.apache.commons.collections.ResettableListIterator;

public class EmptyListIterator extends AbstractEmptyIterator implements ResettableListIterator
{
    public static final ResettableListIterator RESETTABLE_INSTANCE;
    public static final ListIterator INSTANCE;
    
    protected EmptyListIterator() {
    }
    
    static {
        RESETTABLE_INSTANCE = new EmptyListIterator();
        INSTANCE = EmptyListIterator.RESETTABLE_INSTANCE;
    }
}
