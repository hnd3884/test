package org.apache.commons.collections;

public interface PriorityQueue
{
    void clear();
    
    boolean isEmpty();
    
    void insert(final Object p0);
    
    Object peek();
    
    Object pop();
}
