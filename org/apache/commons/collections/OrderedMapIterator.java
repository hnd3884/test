package org.apache.commons.collections;

public interface OrderedMapIterator extends MapIterator, OrderedIterator
{
    boolean hasPrevious();
    
    Object previous();
}
