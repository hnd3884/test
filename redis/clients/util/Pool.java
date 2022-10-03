package redis.clients.util;

import redis.clients.jedis.exceptions.JedisException;
import redis.clients.jedis.exceptions.JedisConnectionException;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.commons.pool2.impl.GenericObjectPool;
import java.io.Closeable;

public abstract class Pool<T> implements Closeable
{
    protected GenericObjectPool<T> internalPool;
    
    public Pool() {
    }
    
    @Override
    public void close() {
        this.destroy();
    }
    
    public boolean isClosed() {
        return this.internalPool.isClosed();
    }
    
    public Pool(final GenericObjectPoolConfig poolConfig, final PooledObjectFactory<T> factory) {
        this.initPool(poolConfig, factory);
    }
    
    public void initPool(final GenericObjectPoolConfig poolConfig, final PooledObjectFactory<T> factory) {
        if (this.internalPool != null) {
            try {
                this.closeInternalPool();
            }
            catch (final Exception ex) {}
        }
        this.internalPool = (GenericObjectPool<T>)new GenericObjectPool((PooledObjectFactory)factory, poolConfig);
    }
    
    public T getResource() {
        try {
            return (T)this.internalPool.borrowObject();
        }
        catch (final Exception e) {
            throw new JedisConnectionException("Could not get a resource from the pool", e);
        }
    }
    
    @Deprecated
    public void returnResourceObject(final T resource) {
        if (resource == null) {
            return;
        }
        try {
            this.internalPool.returnObject((Object)resource);
        }
        catch (final Exception e) {
            throw new JedisException("Could not return the resource to the pool", e);
        }
    }
    
    public void returnBrokenResource(final T resource) {
        if (resource != null) {
            this.returnBrokenResourceObject(resource);
        }
    }
    
    public void returnResource(final T resource) {
        if (resource != null) {
            this.returnResourceObject(resource);
        }
    }
    
    public void destroy() {
        this.closeInternalPool();
    }
    
    protected void returnBrokenResourceObject(final T resource) {
        try {
            this.internalPool.invalidateObject((Object)resource);
        }
        catch (final Exception e) {
            throw new JedisException("Could not return the resource to the pool", e);
        }
    }
    
    protected void closeInternalPool() {
        try {
            this.internalPool.close();
        }
        catch (final Exception e) {
            throw new JedisException("Could not destroy the pool", e);
        }
    }
    
    public int getNumActive() {
        if (this.poolInactive()) {
            return -1;
        }
        return this.internalPool.getNumActive();
    }
    
    public int getNumIdle() {
        if (this.poolInactive()) {
            return -1;
        }
        return this.internalPool.getNumIdle();
    }
    
    public int getNumWaiters() {
        if (this.poolInactive()) {
            return -1;
        }
        return this.internalPool.getNumWaiters();
    }
    
    public long getMeanBorrowWaitTimeMillis() {
        if (this.poolInactive()) {
            return -1L;
        }
        return this.internalPool.getMeanBorrowWaitTimeMillis();
    }
    
    public long getMaxBorrowWaitTimeMillis() {
        if (this.poolInactive()) {
            return -1L;
        }
        return this.internalPool.getMaxBorrowWaitTimeMillis();
    }
    
    private boolean poolInactive() {
        return this.internalPool == null || this.internalPool.isClosed();
    }
    
    public void addObjects(final int count) {
        try {
            for (int i = 0; i < count; ++i) {
                this.internalPool.addObject();
            }
        }
        catch (final Exception e) {
            throw new JedisException("Error trying to add idle objects", e);
        }
    }
}
