package org.apache.commons.collections.iterators;

import org.apache.commons.collections.OrderedMapIterator;

public class AbstractOrderedMapIteratorDecorator implements OrderedMapIterator
{
    protected final OrderedMapIterator iterator;
    
    public AbstractOrderedMapIteratorDecorator(final OrderedMapIterator iterator) {
        if (iterator == null) {
            throw new IllegalArgumentException("OrderedMapIterator must not be null");
        }
        this.iterator = iterator;
    }
    
    protected OrderedMapIterator getOrderedMapIterator() {
        return this.iterator;
    }
    
    public boolean hasNext() {
        return this.iterator.hasNext();
    }
    
    public Object next() {
        return this.iterator.next();
    }
    
    public boolean hasPrevious() {
        return this.iterator.hasPrevious();
    }
    
    public Object previous() {
        return this.iterator.previous();
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
