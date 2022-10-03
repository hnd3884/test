package org.apache.commons.collections;

import java.util.Map;

public interface BoundedMap extends Map
{
    boolean isFull();
    
    int maxSize();
}
