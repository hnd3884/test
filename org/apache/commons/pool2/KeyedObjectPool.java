package org.apache.commons.pool2;

import java.util.NoSuchElementException;

public interface KeyedObjectPool<K, V>
{
    V borrowObject(final K p0) throws Exception, NoSuchElementException, IllegalStateException;
    
    void returnObject(final K p0, final V p1) throws Exception;
    
    void invalidateObject(final K p0, final V p1) throws Exception;
    
    void addObject(final K p0) throws Exception, IllegalStateException, UnsupportedOperationException;
    
    int getNumIdle(final K p0);
    
    int getNumActive(final K p0);
    
    int getNumIdle();
    
    int getNumActive();
    
    void clear() throws Exception, UnsupportedOperationException;
    
    void clear(final K p0) throws Exception, UnsupportedOperationException;
    
    void close();
}
