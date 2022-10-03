package org.apache.commons.pool2;

import java.util.NoSuchElementException;

public interface ObjectPool<T>
{
    T borrowObject() throws Exception, NoSuchElementException, IllegalStateException;
    
    void returnObject(final T p0) throws Exception;
    
    void invalidateObject(final T p0) throws Exception;
    
    void addObject() throws Exception, IllegalStateException, UnsupportedOperationException;
    
    int getNumIdle();
    
    int getNumActive();
    
    void clear() throws Exception, UnsupportedOperationException;
    
    void close();
}
