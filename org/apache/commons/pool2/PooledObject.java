package org.apache.commons.pool2;

import java.io.PrintWriter;
import java.util.Deque;

public interface PooledObject<T> extends Comparable<PooledObject<T>>
{
    T getObject();
    
    long getCreateTime();
    
    long getActiveTimeMillis();
    
    long getIdleTimeMillis();
    
    long getLastBorrowTime();
    
    long getLastReturnTime();
    
    long getLastUsedTime();
    
    int compareTo(final PooledObject<T> p0);
    
    boolean equals(final Object p0);
    
    int hashCode();
    
    String toString();
    
    boolean startEvictionTest();
    
    boolean endEvictionTest(final Deque<PooledObject<T>> p0);
    
    boolean allocate();
    
    boolean deallocate();
    
    void invalidate();
    
    void setLogAbandoned(final boolean p0);
    
    void use();
    
    void printStackTrace(final PrintWriter p0);
    
    PooledObjectState getState();
    
    void markAbandoned();
    
    void markReturning();
}
