package org.apache.tomcat.dbcp.pool2;

public interface KeyedPooledObjectFactory<K, V>
{
    PooledObject<V> makeObject(final K p0) throws Exception;
    
    void destroyObject(final K p0, final PooledObject<V> p1) throws Exception;
    
    void destroyObject(final K p0, final PooledObject<V> p1, final DestroyMode p2) throws Exception;
    
    boolean validateObject(final K p0, final PooledObject<V> p1);
    
    void activateObject(final K p0, final PooledObject<V> p1) throws Exception;
    
    void passivateObject(final K p0, final PooledObject<V> p1) throws Exception;
}
