package org.apache.commons.collections;

import java.util.Collection;

public interface BoundedCollection extends Collection
{
    boolean isFull();
    
    int maxSize();
}
