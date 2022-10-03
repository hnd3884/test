package redis.clients.jedis;

import redis.clients.jedis.exceptions.InvalidURIException;
import redis.clients.util.JedisURIHelper;
import java.net.URI;
import redis.clients.util.ShardInfo;

public class JedisShardInfo extends ShardInfo<Jedis>
{
    private int connectionTimeout;
    private int soTimeout;
    private String host;
    private int port;
    private String password;
    private String name;
    private int db;
    
    @Override
    public String toString() {
        return this.host + ":" + this.port + "*" + this.getWeight();
    }
    
    public String getHost() {
        return this.host;
    }
    
    public int getPort() {
        return this.port;
    }
    
    public JedisShardInfo(final String host) {
        super(1);
        this.password = null;
        this.name = null;
        this.db = 0;
        final URI uri = URI.create(host);
        if (JedisURIHelper.isValid(uri)) {
            this.host = uri.getHost();
            this.port = uri.getPort();
            this.password = JedisURIHelper.getPassword(uri);
            this.db = JedisURIHelper.getDBIndex(uri);
        }
        else {
            this.host = host;
            this.port = 6379;
        }
    }
    
    public JedisShardInfo(final String host, final String name) {
        this(host, 6379, name);
    }
    
    public JedisShardInfo(final String host, final int port) {
        this(host, port, 2000);
    }
    
    public JedisShardInfo(final String host, final int port, final String name) {
        this(host, port, 2000, name);
    }
    
    public JedisShardInfo(final String host, final int port, final int timeout) {
        this(host, port, timeout, timeout, 1);
    }
    
    public JedisShardInfo(final String host, final int port, final int timeout, final String name) {
        this(host, port, timeout, timeout, 1);
        this.name = name;
    }
    
    public JedisShardInfo(final String host, final int port, final int connectionTimeout, final int soTimeout, final int weight) {
        super(weight);
        this.password = null;
        this.name = null;
        this.db = 0;
        this.host = host;
        this.port = port;
        this.connectionTimeout = connectionTimeout;
        this.soTimeout = soTimeout;
    }
    
    public JedisShardInfo(final String host, final String name, final int port, final int timeout, final int weight) {
        super(weight);
        this.password = null;
        this.name = null;
        this.db = 0;
        this.host = host;
        this.name = name;
        this.port = port;
        this.connectionTimeout = timeout;
        this.soTimeout = timeout;
    }
    
    public JedisShardInfo(final URI uri) {
        super(1);
        this.password = null;
        this.name = null;
        this.db = 0;
        if (!JedisURIHelper.isValid(uri)) {
            throw new InvalidURIException(String.format("Cannot open Redis connection due invalid URI. %s", uri.toString()));
        }
        this.host = uri.getHost();
        this.port = uri.getPort();
        this.password = JedisURIHelper.getPassword(uri);
        this.db = JedisURIHelper.getDBIndex(uri);
    }
    
    public String getPassword() {
        return this.password;
    }
    
    public void setPassword(final String auth) {
        this.password = auth;
    }
    
    public int getConnectionTimeout() {
        return this.connectionTimeout;
    }
    
    public void setConnectionTimeout(final int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }
    
    public int getSoTimeout() {
        return this.soTimeout;
    }
    
    public void setSoTimeout(final int soTimeout) {
        this.soTimeout = soTimeout;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    public int getDb() {
        return this.db;
    }
    
    public Jedis createResource() {
        return new Jedis(this);
    }
}
