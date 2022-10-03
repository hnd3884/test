package redis.clients.jedis;

import redis.clients.util.Sharded;
import java.util.Iterator;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import java.util.regex.Pattern;
import redis.clients.util.Hashing;
import java.util.List;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.util.Pool;

public class ShardedJedisPool extends Pool<ShardedJedis>
{
    public ShardedJedisPool(final GenericObjectPoolConfig poolConfig, final List<JedisShardInfo> shards) {
        this(poolConfig, shards, Hashing.MURMUR_HASH);
    }
    
    public ShardedJedisPool(final GenericObjectPoolConfig poolConfig, final List<JedisShardInfo> shards, final Hashing algo) {
        this(poolConfig, shards, algo, null);
    }
    
    public ShardedJedisPool(final GenericObjectPoolConfig poolConfig, final List<JedisShardInfo> shards, final Pattern keyTagPattern) {
        this(poolConfig, shards, Hashing.MURMUR_HASH, keyTagPattern);
    }
    
    public ShardedJedisPool(final GenericObjectPoolConfig poolConfig, final List<JedisShardInfo> shards, final Hashing algo, final Pattern keyTagPattern) {
        super(poolConfig, (PooledObjectFactory)new ShardedJedisFactory(shards, algo, keyTagPattern));
    }
    
    @Override
    public ShardedJedis getResource() {
        final ShardedJedis jedis = super.getResource();
        jedis.setDataSource(this);
        return jedis;
    }
    
    @Override
    @Deprecated
    public void returnBrokenResource(final ShardedJedis resource) {
        if (resource != null) {
            this.returnBrokenResourceObject(resource);
        }
    }
    
    @Override
    @Deprecated
    public void returnResource(final ShardedJedis resource) {
        if (resource != null) {
            resource.resetState();
            this.returnResourceObject(resource);
        }
    }
    
    private static class ShardedJedisFactory implements PooledObjectFactory<ShardedJedis>
    {
        private List<JedisShardInfo> shards;
        private Hashing algo;
        private Pattern keyTagPattern;
        
        public ShardedJedisFactory(final List<JedisShardInfo> shards, final Hashing algo, final Pattern keyTagPattern) {
            this.shards = shards;
            this.algo = algo;
            this.keyTagPattern = keyTagPattern;
        }
        
        public PooledObject<ShardedJedis> makeObject() throws Exception {
            final ShardedJedis jedis = new ShardedJedis(this.shards, this.algo, this.keyTagPattern);
            return (PooledObject<ShardedJedis>)new DefaultPooledObject((Object)jedis);
        }
        
        public void destroyObject(final PooledObject<ShardedJedis> pooledShardedJedis) throws Exception {
            final ShardedJedis shardedJedis = (ShardedJedis)pooledShardedJedis.getObject();
            for (final Jedis jedis : ((Sharded<Jedis, S>)shardedJedis).getAllShards()) {
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
        
        public boolean validateObject(final PooledObject<ShardedJedis> pooledShardedJedis) {
            try {
                final ShardedJedis jedis = (ShardedJedis)pooledShardedJedis.getObject();
                for (final Jedis shard : ((Sharded<Jedis, S>)jedis).getAllShards()) {
                    if (!shard.ping().equals("PONG")) {
                        return false;
                    }
                }
                return true;
            }
            catch (final Exception ex) {
                return false;
            }
        }
        
        public void activateObject(final PooledObject<ShardedJedis> p) throws Exception {
        }
        
        public void passivateObject(final PooledObject<ShardedJedis> p) throws Exception {
        }
    }
}
