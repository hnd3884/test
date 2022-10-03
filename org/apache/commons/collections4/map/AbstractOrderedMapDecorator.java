package org.apache.commons.collections4.map;

import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.OrderedMapIterator;
import java.util.Map;
import org.apache.commons.collections4.OrderedMap;

public abstract class AbstractOrderedMapDecorator<K, V> extends AbstractMapDecorator<K, V> implements OrderedMap<K, V>
{
    protected AbstractOrderedMapDecorator() {
    }
    
    public AbstractOrderedMapDecorator(final OrderedMap<K, V> map) {
        super(map);
    }
    
    @Override
    protected OrderedMap<K, V> decorated() {
        return (OrderedMap)super.decorated();
    }
    
    @Override
    public K firstKey() {
        return this.decorated().firstKey();
    }
    
    @Override
    public K lastKey() {
        return this.decorated().lastKey();
    }
    
    @Override
    public K nextKey(final K key) {
        return this.decorated().nextKey(key);
    }
    
    @Override
    public K previousKey(final K key) {
        return this.decorated().previousKey(key);
    }
    
    @Override
    public OrderedMapIterator<K, V> mapIterator() {
        return this.decorated().mapIterator();
    }
}
