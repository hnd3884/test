package redis.clients.jedis;

import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.PooledObject;
import redis.clients.jedis.exceptions.InvalidURIException;
import redis.clients.util.JedisURIHelper;
import java.net.URI;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.pool2.PooledObjectFactory;

class JedisFactory implements PooledObjectFactory<Jedis>
{
    private final AtomicReference<HostAndPort> hostAndPort;
    private final int connectionTimeout;
    private final int soTimeout;
    private final String password;
    private final int database;
    private final String clientName;
    
    public JedisFactory(final String host, final int port, final int connectionTimeout, final int soTimeout, final String password, final int database, final String clientName) {
        (this.hostAndPort = new AtomicReference<HostAndPort>()).set(new HostAndPort(host, port));
        this.connectionTimeout = connectionTimeout;
        this.soTimeout = soTimeout;
        this.password = password;
        this.database = database;
        this.clientName = clientName;
    }
    
    public JedisFactory(final URI uri, final int connectionTimeout, final int soTimeout, final String clientName) {
        this.hostAndPort = new AtomicReference<HostAndPort>();
        if (!JedisURIHelper.isValid(uri)) {
            throw new InvalidURIException(String.format("Cannot open Redis connection due invalid URI. %s", uri.toString()));
        }
        this.hostAndPort.set(new HostAndPort(uri.getHost(), uri.getPort()));
        this.connectionTimeout = connectionTimeout;
        this.soTimeout = soTimeout;
        this.password = JedisURIHelper.getPassword(uri);
        this.database = JedisURIHelper.getDBIndex(uri);
        this.clientName = clientName;
    }
    
    public void setHostAndPort(final HostAndPort hostAndPort) {
        this.hostAndPort.set(hostAndPort);
    }
    
    public void activateObject(final PooledObject<Jedis> pooledJedis) throws Exception {
        final BinaryJedis jedis = (BinaryJedis)pooledJedis.getObject();
        if (jedis.getDB() != this.database) {
            jedis.select(this.database);
        }
    }
    
    public void destroyObject(final PooledObject<Jedis> pooledJedis) throws Exception {
        final BinaryJedis jedis = (BinaryJedis)pooledJedis.getObject();
        if (jedis.isConnected()) {
            try {
                try {
                    jedis.quit();
                }
                catch (final Exception ex) {}
                jedis.disconnect();
            }
            catch (final Exception ex2) {}
        }
    }
    
    public PooledObject<Jedis> makeObject() throws Exception {
        final HostAndPort hostAndPort = this.hostAndPort.get();
        final Jedis jedis = new Jedis(hostAndPort.getHost(), hostAndPort.getPort(), this.connectionTimeout, this.soTimeout);
        jedis.connect();
        if (null != this.password) {
            jedis.auth(this.password);
        }
        if (this.database != 0) {
            jedis.select(this.database);
        }
        if (this.clientName != null) {
            jedis.clientSetname(this.clientName);
        }
        return (PooledObject<Jedis>)new DefaultPooledObject((Object)jedis);
    }
    
    public void passivateObject(final PooledObject<Jedis> pooledJedis) throws Exception {
    }
    
    public boolean validateObject(final PooledObject<Jedis> pooledJedis) {
        final BinaryJedis jedis = (BinaryJedis)pooledJedis.getObject();
        try {
            final HostAndPort hostAndPort = this.hostAndPort.get();
            final String connectionHost = jedis.getClient().getHost();
            final int connectionPort = jedis.getClient().getPort();
            return hostAndPort.getHost().equals(connectionHost) && hostAndPort.getPort() == connectionPort && jedis.isConnected() && jedis.ping().equals("PONG");
        }
        catch (final Exception e) {
            return false;
        }
    }
}
