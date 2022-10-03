package org.apache.tomcat.dbcp.pool2;

public interface PooledObjectFactory<T>
{
    PooledObject<T> makeObject() throws Exception;
    
    void destroyObject(final PooledObject<T> p0) throws Exception;
    
    void destroyObject(final PooledObject<T> p0, final DestroyMode p1) throws Exception;
    
    boolean validateObject(final PooledObject<T> p0);
    
    void activateObject(final PooledObject<T> p0) throws Exception;
    
    void passivateObject(final PooledObject<T> p0) throws Exception;
}
