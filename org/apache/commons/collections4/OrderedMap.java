package org.apache.commons.collections4;

public interface OrderedMap<K, V> extends IterableMap<K, V>
{
    OrderedMapIterator<K, V> mapIterator();
    
    K firstKey();
    
    K lastKey();
    
    K nextKey(final K p0);
    
    K previousKey(final K p0);
}
