package org.apache.commons.collections.iterators;

import org.apache.commons.collections.MapIterator;

public class AbstractMapIteratorDecorator implements MapIterator
{
    protected final MapIterator iterator;
    
    public AbstractMapIteratorDecorator(final MapIterator iterator) {
        if (iterator == null) {
            throw new IllegalArgumentException("MapIterator must not be null");
        }
        this.iterator = iterator;
    }
    
    protected MapIterator getMapIterator() {
        return this.iterator;
    }
    
    public boolean hasNext() {
        return this.iterator.hasNext();
    }
    
    public Object next() {
        return this.iterator.next();
    }
    
    public void remove() {
        this.iterator.remove();
    }
    
    public Object getKey() {
        return this.iterator.getKey();
    }
    
    public Object getValue() {
        return this.iterator.getValue();
    }
    
    public Object setValue(final Object obj) {
        return this.iterator.setValue(obj);
    }
}
