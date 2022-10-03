package redis.clients.jedis;

import java.util.Iterator;
import redis.clients.jedis.exceptions.JedisConnectionException;
import java.util.Map;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import java.util.Set;

public abstract class JedisClusterConnectionHandler
{
    protected final JedisClusterInfoCache cache;
    
    abstract Jedis getConnection();
    
    abstract Jedis getConnectionFromSlot(final int p0);
    
    public Jedis getConnectionFromNode(final HostAndPort node) {
        this.cache.setNodeIfNotExist(node);
        return this.cache.getNode(JedisClusterInfoCache.getNodeKey(node)).getResource();
    }
    
    public JedisClusterConnectionHandler(final Set<HostAndPort> nodes, final GenericObjectPoolConfig poolConfig, final int connectionTimeout, final int soTimeout) {
        this.cache = new JedisClusterInfoCache(poolConfig, connectionTimeout, soTimeout);
        this.initializeSlotsCache(nodes, poolConfig);
    }
    
    public Map<String, JedisPool> getNodes() {
        return this.cache.getNodes();
    }
    
    private void initializeSlotsCache(final Set<HostAndPort> startNodes, final GenericObjectPoolConfig poolConfig) {
        for (final HostAndPort hostAndPort : startNodes) {
            final Jedis jedis = new Jedis(hostAndPort.getHost(), hostAndPort.getPort());
            try {
                this.cache.discoverClusterNodesAndSlots(jedis);
                break;
            }
            catch (final JedisConnectionException e) {}
            finally {
                if (jedis != null) {
                    jedis.close();
                }
            }
        }
        for (final HostAndPort node : startNodes) {
            this.cache.setNodeIfNotExist(node);
        }
    }
    
    public void renewSlotCache() {
        for (final JedisPool jp : this.cache.getNodes().values()) {
            Jedis jedis = null;
            try {
                jedis = jp.getResource();
                this.cache.discoverClusterSlots(jedis);
                break;
            }
            catch (final JedisConnectionException e) {}
            finally {
                if (jedis != null) {
                    jedis.close();
                }
            }
        }
    }
}
