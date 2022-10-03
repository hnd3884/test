package org.apache.tomcat.dbcp.pool2;

import java.util.NoSuchElementException;
import java.util.Collection;
import java.io.Closeable;

public interface KeyedObjectPool<K, V> extends Closeable
{
    void addObject(final K p0) throws Exception, IllegalStateException, UnsupportedOperationException;
    
    void addObjects(final Collection<K> p0, final int p1) throws Exception, IllegalArgumentException;
    
    void addObjects(final K p0, final int p1) throws Exception, IllegalArgumentException;
    
    V borrowObject(final K p0) throws Exception, NoSuchElementException, IllegalStateException;
    
    void clear() throws Exception, UnsupportedOperationException;
    
    void clear(final K p0) throws Exception, UnsupportedOperationException;
    
    void close();
    
    int getNumActive();
    
    int getNumActive(final K p0);
    
    int getNumIdle();
    
    int getNumIdle(final K p0);
    
    void invalidateObject(final K p0, final V p1) throws Exception;
    
    void invalidateObject(final K p0, final V p1, final DestroyMode p2) throws Exception;
    
    void returnObject(final K p0, final V p1) throws Exception;
}
