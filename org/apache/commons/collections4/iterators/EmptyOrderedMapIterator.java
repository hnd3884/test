package org.apache.commons.collections4.iterators;

import org.apache.commons.collections4.ResettableIterator;
import org.apache.commons.collections4.OrderedMapIterator;

public class EmptyOrderedMapIterator<K, V> extends AbstractEmptyMapIterator<K, V> implements OrderedMapIterator<K, V>, ResettableIterator<K>
{
    public static final OrderedMapIterator INSTANCE;
    
    public static <K, V> OrderedMapIterator<K, V> emptyOrderedMapIterator() {
        return EmptyOrderedMapIterator.INSTANCE;
    }
    
    protected EmptyOrderedMapIterator() {
    }
    
    static {
        INSTANCE = new EmptyOrderedMapIterator();
    }
}
