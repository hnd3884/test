package org.apache.commons.collections;

public interface OrderedBidiMap extends BidiMap, OrderedMap
{
    BidiMap inverseBidiMap();
    
    OrderedBidiMap inverseOrderedBidiMap();
}
