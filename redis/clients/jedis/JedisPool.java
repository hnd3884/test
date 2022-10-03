package redis.clients.jedis;

import redis.clients.jedis.exceptions.JedisException;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import redis.clients.util.JedisURIHelper;
import java.net.URI;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.util.Pool;

public class JedisPool extends Pool<Jedis>
{
    public JedisPool() {
        this("localhost", 6379);
    }
    
    public JedisPool(final GenericObjectPoolConfig poolConfig, final String host) {
        this(poolConfig, host, 6379, 2000, null, 0, null);
    }
    
    public JedisPool(final String host, final int port) {
        this(new GenericObjectPoolConfig(), host, port, 2000, null, 0, null);
    }
    
    public JedisPool(final String host) {
        final URI uri = URI.create(host);
        if (JedisURIHelper.isValid(uri)) {
            final String h = uri.getHost();
            final int port = uri.getPort();
            final String password = JedisURIHelper.getPassword(uri);
            final int database = JedisURIHelper.getDBIndex(uri);
            this.internalPool = (GenericObjectPool<T>)new GenericObjectPool((PooledObjectFactory)new JedisFactory(h, port, 2000, 2000, password, database, null), new GenericObjectPoolConfig());
        }
        else {
            this.internalPool = (GenericObjectPool<T>)new GenericObjectPool((PooledObjectFactory)new JedisFactory(host, 6379, 2000, 2000, null, 0, null), new GenericObjectPoolConfig());
        }
    }
    
    public JedisPool(final URI uri) {
        this(new GenericObjectPoolConfig(), uri, 2000);
    }
    
    public JedisPool(final URI uri, final int timeout) {
        this(new GenericObjectPoolConfig(), uri, timeout);
    }
    
    public JedisPool(final GenericObjectPoolConfig poolConfig, final String host, final int port, final int timeout, final String password) {
        this(poolConfig, host, port, timeout, password, 0, null);
    }
    
    public JedisPool(final GenericObjectPoolConfig poolConfig, final String host, final int port) {
        this(poolConfig, host, port, 2000, null, 0, null);
    }
    
    public JedisPool(final GenericObjectPoolConfig poolConfig, final String host, final int port, final int timeout) {
        this(poolConfig, host, port, timeout, null, 0, null);
    }
    
    public JedisPool(final GenericObjectPoolConfig poolConfig, final String host, final int port, final int timeout, final String password, final int database) {
        this(poolConfig, host, port, timeout, password, database, null);
    }
    
    public JedisPool(final GenericObjectPoolConfig poolConfig, final String host, final int port, final int timeout, final String password, final int database, final String clientName) {
        this(poolConfig, host, port, timeout, timeout, password, database, clientName);
    }
    
    public JedisPool(final GenericObjectPoolConfig poolConfig, final String host, final int port, final int connectionTimeout, final int soTimeout, final String password, final int database, final String clientName) {
        super(poolConfig, (PooledObjectFactory)new JedisFactory(host, port, connectionTimeout, soTimeout, password, database, clientName));
    }
    
    public JedisPool(final GenericObjectPoolConfig poolConfig, final URI uri) {
        this(poolConfig, uri, 2000);
    }
    
    public JedisPool(final GenericObjectPoolConfig poolConfig, final URI uri, final int timeout) {
        this(poolConfig, uri, timeout, timeout);
    }
    
    public JedisPool(final GenericObjectPoolConfig poolConfig, final URI uri, final int connectionTimeout, final int soTimeout) {
        super(poolConfig, (PooledObjectFactory)new JedisFactory(uri, connectionTimeout, soTimeout, null));
    }
    
    @Override
    public Jedis getResource() {
        final Jedis jedis = super.getResource();
        jedis.setDataSource(this);
        return jedis;
    }
    
    @Deprecated
    @Override
    public void returnBrokenResource(final Jedis resource) {
        if (resource != null) {
            this.returnBrokenResourceObject(resource);
        }
    }
    
    @Deprecated
    @Override
    public void returnResource(final Jedis resource) {
        if (resource != null) {
            try {
                resource.resetState();
                this.returnResourceObject(resource);
            }
            catch (final Exception e) {
                this.returnBrokenResource(resource);
                throw new JedisException("Could not return the resource to the pool", e);
            }
        }
    }
}
