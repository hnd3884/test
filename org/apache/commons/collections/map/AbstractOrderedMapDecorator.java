package org.apache.commons.collections.map;

import org.apache.commons.collections.OrderedMapIterator;
import org.apache.commons.collections.MapIterator;
import java.util.Map;
import org.apache.commons.collections.OrderedMap;

public abstract class AbstractOrderedMapDecorator extends AbstractMapDecorator implements OrderedMap
{
    protected AbstractOrderedMapDecorator() {
    }
    
    public AbstractOrderedMapDecorator(final OrderedMap map) {
        super(map);
    }
    
    protected OrderedMap getOrderedMap() {
        return (OrderedMap)this.map;
    }
    
    public Object firstKey() {
        return this.getOrderedMap().firstKey();
    }
    
    public Object lastKey() {
        return this.getOrderedMap().lastKey();
    }
    
    public Object nextKey(final Object key) {
        return this.getOrderedMap().nextKey(key);
    }
    
    public Object previousKey(final Object key) {
        return this.getOrderedMap().previousKey(key);
    }
    
    public MapIterator mapIterator() {
        return this.getOrderedMap().mapIterator();
    }
    
    public OrderedMapIterator orderedMapIterator() {
        return this.getOrderedMap().orderedMapIterator();
    }
}
