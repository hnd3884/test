package org.apache.tomcat.dbcp.pool2;

import java.util.NoSuchElementException;
import java.io.Closeable;

public interface ObjectPool<T> extends Closeable
{
    void addObject() throws Exception, IllegalStateException, UnsupportedOperationException;
    
    void addObjects(final int p0) throws Exception;
    
    T borrowObject() throws Exception, NoSuchElementException, IllegalStateException;
    
    void clear() throws Exception, UnsupportedOperationException;
    
    void close();
    
    int getNumActive();
    
    int getNumIdle();
    
    void invalidateObject(final T p0) throws Exception;
    
    void invalidateObject(final T p0, final DestroyMode p1) throws Exception;
    
    void returnObject(final T p0) throws Exception;
}
