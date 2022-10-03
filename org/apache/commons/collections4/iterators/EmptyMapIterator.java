package org.apache.commons.collections4.iterators;

import org.apache.commons.collections4.ResettableIterator;
import org.apache.commons.collections4.MapIterator;

public class EmptyMapIterator<K, V> extends AbstractEmptyMapIterator<K, V> implements MapIterator<K, V>, ResettableIterator<K>
{
    public static final MapIterator INSTANCE;
    
    public static <K, V> MapIterator<K, V> emptyMapIterator() {
        return EmptyMapIterator.INSTANCE;
    }
    
    protected EmptyMapIterator() {
    }
    
    static {
        INSTANCE = new EmptyMapIterator();
    }
}
