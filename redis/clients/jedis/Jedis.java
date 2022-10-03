package redis.clients.jedis;

import java.util.AbstractMap;
import redis.clients.util.Slowlog;
import java.util.ArrayList;
import redis.clients.util.SafeEncoder;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Collections;
import java.util.Map;
import java.util.List;
import java.util.Set;
import java.net.URI;
import redis.clients.util.Pool;

public class Jedis extends BinaryJedis implements JedisCommands, MultiKeyCommands, AdvancedJedisCommands, ScriptingCommands, BasicCommands, ClusterCommands, SentinelCommands
{
    protected Pool<Jedis> dataSource;
    
    public Jedis() {
        this.dataSource = null;
    }
    
    public Jedis(final String host) {
        super(host);
        this.dataSource = null;
    }
    
    public Jedis(final String host, final int port) {
        super(host, port);
        this.dataSource = null;
    }
    
    public Jedis(final String host, final int port, final int timeout) {
        super(host, port, timeout);
        this.dataSource = null;
    }
    
    public Jedis(final String host, final int port, final int connectionTimeout, final int soTimeout) {
        super(host, port, connectionTimeout, soTimeout);
        this.dataSource = null;
    }
    
    public Jedis(final JedisShardInfo shardInfo) {
        super(shardInfo);
        this.dataSource = null;
    }
    
    public Jedis(final URI uri) {
        super(uri);
        this.dataSource = null;
    }
    
    public Jedis(final URI uri, final int timeout) {
        super(uri, timeout);
        this.dataSource = null;
    }
    
    public Jedis(final URI uri, final int connectionTimeout, final int soTimeout) {
        super(uri, connectionTimeout, soTimeout);
        this.dataSource = null;
    }
    
    @Override
    public String set(final String key, final String value) {
        this.checkIsInMultiOrPipeline();
        this.client.set(key, value);
        return this.client.getStatusCodeReply();
    }
    
    @Override
    public String set(final String key, final String value, final String nxxx, final String expx, final long time) {
        this.checkIsInMultiOrPipeline();
        this.client.set(key, value, nxxx, expx, time);
        return this.client.getStatusCodeReply();
    }
    
    @Override
    public String get(final String key) {
        this.checkIsInMultiOrPipeline();
        this.client.sendCommand(Protocol.Command.GET, key);
        return this.client.getBulkReply();
    }
    
    @Override
    public Boolean exists(final String key) {
        this.checkIsInMultiOrPipeline();
        this.client.exists(key);
        return this.client.getIntegerReply() == 1L;
    }
    
    @Override
    public Long del(final String... keys) {
        this.checkIsInMultiOrPipeline();
        this.client.del(keys);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Long del(final String key) {
        this.client.del(key);
        return this.client.getIntegerReply();
    }
    
    @Override
    public String type(final String key) {
        this.checkIsInMultiOrPipeline();
        this.client.type(key);
        return this.client.getStatusCodeReply();
    }
    
    @Override
    public Set<String> keys(final String pattern) {
        this.checkIsInMultiOrPipeline();
        this.client.keys(pattern);
        return BuilderFactory.STRING_SET.build(this.client.getBinaryMultiBulkReply());
    }
    
    @Override
    public String randomKey() {
        this.checkIsInMultiOrPipeline();
        this.client.randomKey();
        return this.client.getBulkReply();
    }
    
    @Override
    public String rename(final String oldkey, final String newkey) {
        this.checkIsInMultiOrPipeline();
        this.client.rename(oldkey, newkey);
        return this.client.getStatusCodeReply();
    }
    
    @Override
    public Long renamenx(final String oldkey, final String newkey) {
        this.checkIsInMultiOrPipeline();
        this.client.renamenx(oldkey, newkey);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Long expire(final String key, final int seconds) {
        this.checkIsInMultiOrPipeline();
        this.client.expire(key, seconds);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Long expireAt(final String key, final long unixTime) {
        this.checkIsInMultiOrPipeline();
        this.client.expireAt(key, unixTime);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Long ttl(final String key) {
        this.checkIsInMultiOrPipeline();
        this.client.ttl(key);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Long move(final String key, final int dbIndex) {
        this.checkIsInMultiOrPipeline();
        this.client.move(key, dbIndex);
        return this.client.getIntegerReply();
    }
    
    @Override
    public String getSet(final String key, final String value) {
        this.checkIsInMultiOrPipeline();
        this.client.getSet(key, value);
        return this.client.getBulkReply();
    }
    
    @Override
    public List<String> mget(final String... keys) {
        this.checkIsInMultiOrPipeline();
        this.client.mget(keys);
        return this.client.getMultiBulkReply();
    }
    
    @Override
    public Long setnx(final String key, final String value) {
        this.checkIsInMultiOrPipeline();
        this.client.setnx(key, value);
        return this.client.getIntegerReply();
    }
    
    @Override
    public String setex(final String key, final int seconds, final String value) {
        this.checkIsInMultiOrPipeline();
        this.client.setex(key, seconds, value);
        return this.client.getStatusCodeReply();
    }
    
    @Override
    public String mset(final String... keysvalues) {
        this.checkIsInMultiOrPipeline();
        this.client.mset(keysvalues);
        return this.client.getStatusCodeReply();
    }
    
    @Override
    public Long msetnx(final String... keysvalues) {
        this.checkIsInMultiOrPipeline();
        this.client.msetnx(keysvalues);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Long decrBy(final String key, final long integer) {
        this.checkIsInMultiOrPipeline();
        this.client.decrBy(key, integer);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Long decr(final String key) {
        this.checkIsInMultiOrPipeline();
        this.client.decr(key);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Long incrBy(final String key, final long integer) {
        this.checkIsInMultiOrPipeline();
        this.client.incrBy(key, integer);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Double incrByFloat(final String key, final double value) {
        this.checkIsInMultiOrPipeline();
        this.client.incrByFloat(key, value);
        final String dval = this.client.getBulkReply();
        return (dval != null) ? new Double(dval) : null;
    }
    
    @Override
    public Long incr(final String key) {
        this.checkIsInMultiOrPipeline();
        this.client.incr(key);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Long append(final String key, final String value) {
        this.checkIsInMultiOrPipeline();
        this.client.append(key, value);
        return this.client.getIntegerReply();
    }
    
    @Override
    public String substr(final String key, final int start, final int end) {
        this.checkIsInMultiOrPipeline();
        this.client.substr(key, start, end);
        return this.client.getBulkReply();
    }
    
    @Override
    public Long hset(final String key, final String field, final String value) {
        this.checkIsInMultiOrPipeline();
        this.client.hset(key, field, value);
        return this.client.getIntegerReply();
    }
    
    @Override
    public String hget(final String key, final String field) {
        this.checkIsInMultiOrPipeline();
        this.client.hget(key, field);
        return this.client.getBulkReply();
    }
    
    @Override
    public Long hsetnx(final String key, final String field, final String value) {
        this.checkIsInMultiOrPipeline();
        this.client.hsetnx(key, field, value);
        return this.client.getIntegerReply();
    }
    
    @Override
    public String hmset(final String key, final Map<String, String> hash) {
        this.checkIsInMultiOrPipeline();
        this.client.hmset(key, hash);
        return this.client.getStatusCodeReply();
    }
    
    @Override
    public List<String> hmget(final String key, final String... fields) {
        this.checkIsInMultiOrPipeline();
        this.client.hmget(key, fields);
        return this.client.getMultiBulkReply();
    }
    
    @Override
    public Long hincrBy(final String key, final String field, final long value) {
        this.checkIsInMultiOrPipeline();
        this.client.hincrBy(key, field, value);
        return this.client.getIntegerReply();
    }
    
    public Double hincrByFloat(final String key, final String field, final double value) {
        this.checkIsInMultiOrPipeline();
        this.client.hincrByFloat(key, field, value);
        final String dval = this.client.getBulkReply();
        return (dval != null) ? new Double(dval) : null;
    }
    
    @Override
    public Boolean hexists(final String key, final String field) {
        this.checkIsInMultiOrPipeline();
        this.client.hexists(key, field);
        return this.client.getIntegerReply() == 1L;
    }
    
    @Override
    public Long hdel(final String key, final String... fields) {
        this.checkIsInMultiOrPipeline();
        this.client.hdel(key, fields);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Long hlen(final String key) {
        this.checkIsInMultiOrPipeline();
        this.client.hlen(key);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Set<String> hkeys(final String key) {
        this.checkIsInMultiOrPipeline();
        this.client.hkeys(key);
        return BuilderFactory.STRING_SET.build(this.client.getBinaryMultiBulkReply());
    }
    
    @Override
    public List<String> hvals(final String key) {
        this.checkIsInMultiOrPipeline();
        this.client.hvals(key);
        final List<String> lresult = this.client.getMultiBulkReply();
        return lresult;
    }
    
    @Override
    public Map<String, String> hgetAll(final String key) {
        this.checkIsInMultiOrPipeline();
        this.client.hgetAll(key);
        return BuilderFactory.STRING_MAP.build(this.client.getBinaryMultiBulkReply());
    }
    
    @Override
    public Long rpush(final String key, final String... strings) {
        this.checkIsInMultiOrPipeline();
        this.client.rpush(key, strings);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Long lpush(final String key, final String... strings) {
        this.checkIsInMultiOrPipeline();
        this.client.lpush(key, strings);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Long llen(final String key) {
        this.checkIsInMultiOrPipeline();
        this.client.llen(key);
        return this.client.getIntegerReply();
    }
    
    @Override
    public List<String> lrange(final String key, final long start, final long end) {
        this.checkIsInMultiOrPipeline();
        this.client.lrange(key, start, end);
        return this.client.getMultiBulkReply();
    }
    
    @Override
    public String ltrim(final String key, final long start, final long end) {
        this.checkIsInMultiOrPipeline();
        this.client.ltrim(key, start, end);
        return this.client.getStatusCodeReply();
    }
    
    @Override
    public String lindex(final String key, final long index) {
        this.checkIsInMultiOrPipeline();
        this.client.lindex(key, index);
        return this.client.getBulkReply();
    }
    
    @Override
    public String lset(final String key, final long index, final String value) {
        this.checkIsInMultiOrPipeline();
        this.client.lset(key, index, value);
        return this.client.getStatusCodeReply();
    }
    
    @Override
    public Long lrem(final String key, final long count, final String value) {
        this.checkIsInMultiOrPipeline();
        this.client.lrem(key, count, value);
        return this.client.getIntegerReply();
    }
    
    @Override
    public String lpop(final String key) {
        this.checkIsInMultiOrPipeline();
        this.client.lpop(key);
        return this.client.getBulkReply();
    }
    
    @Override
    public String rpop(final String key) {
        this.checkIsInMultiOrPipeline();
        this.client.rpop(key);
        return this.client.getBulkReply();
    }
    
    @Override
    public String rpoplpush(final String srckey, final String dstkey) {
        this.checkIsInMultiOrPipeline();
        this.client.rpoplpush(srckey, dstkey);
        return this.client.getBulkReply();
    }
    
    @Override
    public Long sadd(final String key, final String... members) {
        this.checkIsInMultiOrPipeline();
        this.client.sadd(key, members);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Set<String> smembers(final String key) {
        this.checkIsInMultiOrPipeline();
        this.client.smembers(key);
        final List<String> members = this.client.getMultiBulkReply();
        if (members == null) {
            return null;
        }
        return SetFromList.of(members);
    }
    
    @Override
    public Long srem(final String key, final String... members) {
        this.checkIsInMultiOrPipeline();
        this.client.srem(key, members);
        return this.client.getIntegerReply();
    }
    
    @Override
    public String spop(final String key) {
        this.checkIsInMultiOrPipeline();
        this.client.spop(key);
        return this.client.getBulkReply();
    }
    
    @Override
    public Set<String> spop(final String key, final long count) {
        this.checkIsInMultiOrPipeline();
        this.client.spop(key, count);
        final List<String> members = this.client.getMultiBulkReply();
        if (members == null) {
            return null;
        }
        return SetFromList.of(members);
    }
    
    @Override
    public Long smove(final String srckey, final String dstkey, final String member) {
        this.checkIsInMultiOrPipeline();
        this.client.smove(srckey, dstkey, member);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Long scard(final String key) {
        this.checkIsInMultiOrPipeline();
        this.client.scard(key);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Boolean sismember(final String key, final String member) {
        this.checkIsInMultiOrPipeline();
        this.client.sismember(key, member);
        return this.client.getIntegerReply() == 1L;
    }
    
    @Override
    public Set<String> sinter(final String... keys) {
        this.checkIsInMultiOrPipeline();
        this.client.sinter(keys);
        final List<String> members = this.client.getMultiBulkReply();
        if (members == null) {
            return null;
        }
        return SetFromList.of(members);
    }
    
    @Override
    public Long sinterstore(final String dstkey, final String... keys) {
        this.checkIsInMultiOrPipeline();
        this.client.sinterstore(dstkey, keys);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Set<String> sunion(final String... keys) {
        this.checkIsInMultiOrPipeline();
        this.client.sunion(keys);
        final List<String> members = this.client.getMultiBulkReply();
        if (members == null) {
            return null;
        }
        return SetFromList.of(members);
    }
    
    @Override
    public Long sunionstore(final String dstkey, final String... keys) {
        this.checkIsInMultiOrPipeline();
        this.client.sunionstore(dstkey, keys);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Set<String> sdiff(final String... keys) {
        this.checkIsInMultiOrPipeline();
        this.client.sdiff(keys);
        return BuilderFactory.STRING_SET.build(this.client.getBinaryMultiBulkReply());
    }
    
    @Override
    public Long sdiffstore(final String dstkey, final String... keys) {
        this.checkIsInMultiOrPipeline();
        this.client.sdiffstore(dstkey, keys);
        return this.client.getIntegerReply();
    }
    
    @Override
    public String srandmember(final String key) {
        this.checkIsInMultiOrPipeline();
        this.client.srandmember(key);
        return this.client.getBulkReply();
    }
    
    @Override
    public List<String> srandmember(final String key, final int count) {
        this.checkIsInMultiOrPipeline();
        this.client.srandmember(key, count);
        return this.client.getMultiBulkReply();
    }
    
    @Override
    public Long zadd(final String key, final double score, final String member) {
        this.checkIsInMultiOrPipeline();
        this.client.zadd(key, score, member);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Long zadd(final String key, final Map<String, Double> scoreMembers) {
        this.checkIsInMultiOrPipeline();
        this.client.zadd(key, scoreMembers);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Set<String> zrange(final String key, final long start, final long end) {
        this.checkIsInMultiOrPipeline();
        this.client.zrange(key, start, end);
        final List<String> members = this.client.getMultiBulkReply();
        if (members == null) {
            return null;
        }
        return SetFromList.of(members);
    }
    
    @Override
    public Long zrem(final String key, final String... members) {
        this.checkIsInMultiOrPipeline();
        this.client.zrem(key, members);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Double zincrby(final String key, final double score, final String member) {
        this.checkIsInMultiOrPipeline();
        this.client.zincrby(key, score, member);
        final String newscore = this.client.getBulkReply();
        return Double.valueOf(newscore);
    }
    
    @Override
    public Long zrank(final String key, final String member) {
        this.checkIsInMultiOrPipeline();
        this.client.zrank(key, member);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Long zrevrank(final String key, final String member) {
        this.checkIsInMultiOrPipeline();
        this.client.zrevrank(key, member);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Set<String> zrevrange(final String key, final long start, final long end) {
        this.checkIsInMultiOrPipeline();
        this.client.zrevrange(key, start, end);
        final List<String> members = this.client.getMultiBulkReply();
        if (members == null) {
            return null;
        }
        return SetFromList.of(members);
    }
    
    @Override
    public Set<Tuple> zrangeWithScores(final String key, final long start, final long end) {
        this.checkIsInMultiOrPipeline();
        this.client.zrangeWithScores(key, start, end);
        return this.getTupledSet();
    }
    
    @Override
    public Set<Tuple> zrevrangeWithScores(final String key, final long start, final long end) {
        this.checkIsInMultiOrPipeline();
        this.client.zrevrangeWithScores(key, start, end);
        return this.getTupledSet();
    }
    
    @Override
    public Long zcard(final String key) {
        this.checkIsInMultiOrPipeline();
        this.client.zcard(key);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Double zscore(final String key, final String member) {
        this.checkIsInMultiOrPipeline();
        this.client.zscore(key, member);
        final String score = this.client.getBulkReply();
        return (score != null) ? new Double(score) : null;
    }
    
    @Override
    public String watch(final String... keys) {
        this.client.watch(keys);
        return this.client.getStatusCodeReply();
    }
    
    @Override
    public List<String> sort(final String key) {
        this.checkIsInMultiOrPipeline();
        this.client.sort(key);
        return this.client.getMultiBulkReply();
    }
    
    @Override
    public List<String> sort(final String key, final SortingParams sortingParameters) {
        this.checkIsInMultiOrPipeline();
        this.client.sort(key, sortingParameters);
        return this.client.getMultiBulkReply();
    }
    
    @Override
    public List<String> blpop(final int timeout, final String... keys) {
        return this.blpop(this.getArgsAddTimeout(timeout, keys));
    }
    
    private String[] getArgsAddTimeout(final int timeout, final String[] keys) {
        final int keyCount = keys.length;
        final String[] args = new String[keyCount + 1];
        for (int at = 0; at != keyCount; ++at) {
            args[at] = keys[at];
        }
        args[keyCount] = String.valueOf(timeout);
        return args;
    }
    
    @Override
    public List<String> blpop(final String... args) {
        this.checkIsInMultiOrPipeline();
        this.client.blpop(args);
        this.client.setTimeoutInfinite();
        try {
            return this.client.getMultiBulkReply();
        }
        finally {
            this.client.rollbackTimeout();
        }
    }
    
    @Override
    public List<String> brpop(final String... args) {
        this.checkIsInMultiOrPipeline();
        this.client.brpop(args);
        this.client.setTimeoutInfinite();
        try {
            return this.client.getMultiBulkReply();
        }
        finally {
            this.client.rollbackTimeout();
        }
    }
    
    @Deprecated
    @Override
    public List<String> blpop(final String arg) {
        return this.blpop(new String[] { arg });
    }
    
    @Deprecated
    @Override
    public List<String> brpop(final String arg) {
        return this.brpop(new String[] { arg });
    }
    
    @Override
    public Long sort(final String key, final SortingParams sortingParameters, final String dstkey) {
        this.checkIsInMultiOrPipeline();
        this.client.sort(key, sortingParameters, dstkey);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Long sort(final String key, final String dstkey) {
        this.checkIsInMultiOrPipeline();
        this.client.sort(key, dstkey);
        return this.client.getIntegerReply();
    }
    
    @Override
    public List<String> brpop(final int timeout, final String... keys) {
        return this.brpop(this.getArgsAddTimeout(timeout, keys));
    }
    
    @Override
    public Long zcount(final String key, final double min, final double max) {
        this.checkIsInMultiOrPipeline();
        this.client.zcount(key, min, max);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Long zcount(final String key, final String min, final String max) {
        this.checkIsInMultiOrPipeline();
        this.client.zcount(key, min, max);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Set<String> zrangeByScore(final String key, final double min, final double max) {
        this.checkIsInMultiOrPipeline();
        this.client.zrangeByScore(key, min, max);
        final List<String> members = this.client.getMultiBulkReply();
        if (members == null) {
            return null;
        }
        return SetFromList.of(members);
    }
    
    @Override
    public Set<String> zrangeByScore(final String key, final String min, final String max) {
        this.checkIsInMultiOrPipeline();
        this.client.zrangeByScore(key, min, max);
        final List<String> members = this.client.getMultiBulkReply();
        if (members == null) {
            return null;
        }
        return SetFromList.of(members);
    }
    
    @Override
    public Set<String> zrangeByScore(final String key, final double min, final double max, final int offset, final int count) {
        this.checkIsInMultiOrPipeline();
        this.client.zrangeByScore(key, min, max, offset, count);
        final List<String> members = this.client.getMultiBulkReply();
        if (members == null) {
            return null;
        }
        return SetFromList.of(members);
    }
    
    @Override
    public Set<String> zrangeByScore(final String key, final String min, final String max, final int offset, final int count) {
        this.checkIsInMultiOrPipeline();
        this.client.zrangeByScore(key, min, max, offset, count);
        final List<String> members = this.client.getMultiBulkReply();
        if (members == null) {
            return null;
        }
        return SetFromList.of(members);
    }
    
    @Override
    public Set<Tuple> zrangeByScoreWithScores(final String key, final double min, final double max) {
        this.checkIsInMultiOrPipeline();
        this.client.zrangeByScoreWithScores(key, min, max);
        return this.getTupledSet();
    }
    
    @Override
    public Set<Tuple> zrangeByScoreWithScores(final String key, final String min, final String max) {
        this.checkIsInMultiOrPipeline();
        this.client.zrangeByScoreWithScores(key, min, max);
        return this.getTupledSet();
    }
    
    @Override
    public Set<Tuple> zrangeByScoreWithScores(final String key, final double min, final double max, final int offset, final int count) {
        this.checkIsInMultiOrPipeline();
        this.client.zrangeByScoreWithScores(key, min, max, offset, count);
        return this.getTupledSet();
    }
    
    @Override
    public Set<Tuple> zrangeByScoreWithScores(final String key, final String min, final String max, final int offset, final int count) {
        this.checkIsInMultiOrPipeline();
        this.client.zrangeByScoreWithScores(key, min, max, offset, count);
        return this.getTupledSet();
    }
    
    private Set<Tuple> getTupledSet() {
        this.checkIsInMultiOrPipeline();
        final List<String> membersWithScores = this.client.getMultiBulkReply();
        if (membersWithScores == null) {
            return null;
        }
        if (membersWithScores.size() == 0) {
            return Collections.emptySet();
        }
        final Set<Tuple> set = new LinkedHashSet<Tuple>(membersWithScores.size() / 2, 1.0f);
        final Iterator<String> iterator = membersWithScores.iterator();
        while (iterator.hasNext()) {
            set.add(new Tuple(iterator.next(), Double.valueOf(iterator.next())));
        }
        return set;
    }
    
    @Override
    public Set<String> zrevrangeByScore(final String key, final double max, final double min) {
        this.checkIsInMultiOrPipeline();
        this.client.zrevrangeByScore(key, max, min);
        final List<String> members = this.client.getMultiBulkReply();
        if (members == null) {
            return null;
        }
        return SetFromList.of(members);
    }
    
    @Override
    public Set<String> zrevrangeByScore(final String key, final String max, final String min) {
        this.checkIsInMultiOrPipeline();
        this.client.zrevrangeByScore(key, max, min);
        final List<String> members = this.client.getMultiBulkReply();
        if (members == null) {
            return null;
        }
        return SetFromList.of(members);
    }
    
    @Override
    public Set<String> zrevrangeByScore(final String key, final double max, final double min, final int offset, final int count) {
        this.checkIsInMultiOrPipeline();
        this.client.zrevrangeByScore(key, max, min, offset, count);
        final List<String> members = this.client.getMultiBulkReply();
        if (members == null) {
            return null;
        }
        return SetFromList.of(members);
    }
    
    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(final String key, final double max, final double min) {
        this.checkIsInMultiOrPipeline();
        this.client.zrevrangeByScoreWithScores(key, max, min);
        return this.getTupledSet();
    }
    
    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(final String key, final double max, final double min, final int offset, final int count) {
        this.checkIsInMultiOrPipeline();
        this.client.zrevrangeByScoreWithScores(key, max, min, offset, count);
        return this.getTupledSet();
    }
    
    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(final String key, final String max, final String min, final int offset, final int count) {
        this.checkIsInMultiOrPipeline();
        this.client.zrevrangeByScoreWithScores(key, max, min, offset, count);
        return this.getTupledSet();
    }
    
    @Override
    public Set<String> zrevrangeByScore(final String key, final String max, final String min, final int offset, final int count) {
        this.checkIsInMultiOrPipeline();
        this.client.zrevrangeByScore(key, max, min, offset, count);
        final List<String> members = this.client.getMultiBulkReply();
        if (members == null) {
            return null;
        }
        return SetFromList.of(members);
    }
    
    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(final String key, final String max, final String min) {
        this.checkIsInMultiOrPipeline();
        this.client.zrevrangeByScoreWithScores(key, max, min);
        return this.getTupledSet();
    }
    
    @Override
    public Long zremrangeByRank(final String key, final long start, final long end) {
        this.checkIsInMultiOrPipeline();
        this.client.zremrangeByRank(key, start, end);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Long zremrangeByScore(final String key, final double start, final double end) {
        this.checkIsInMultiOrPipeline();
        this.client.zremrangeByScore(key, start, end);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Long zremrangeByScore(final String key, final String start, final String end) {
        this.checkIsInMultiOrPipeline();
        this.client.zremrangeByScore(key, start, end);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Long zunionstore(final String dstkey, final String... sets) {
        this.checkIsInMultiOrPipeline();
        this.client.zunionstore(dstkey, sets);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Long zunionstore(final String dstkey, final ZParams params, final String... sets) {
        this.checkIsInMultiOrPipeline();
        this.client.zunionstore(dstkey, params, sets);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Long zinterstore(final String dstkey, final String... sets) {
        this.checkIsInMultiOrPipeline();
        this.client.zinterstore(dstkey, sets);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Long zinterstore(final String dstkey, final ZParams params, final String... sets) {
        this.checkIsInMultiOrPipeline();
        this.client.zinterstore(dstkey, params, sets);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Long zlexcount(final String key, final String min, final String max) {
        this.checkIsInMultiOrPipeline();
        this.client.zlexcount(key, min, max);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Set<String> zrangeByLex(final String key, final String min, final String max) {
        this.checkIsInMultiOrPipeline();
        this.client.zrangeByLex(key, min, max);
        final List<String> members = this.client.getMultiBulkReply();
        if (members == null) {
            return null;
        }
        return SetFromList.of(members);
    }
    
    @Override
    public Set<String> zrangeByLex(final String key, final String min, final String max, final int offset, final int count) {
        this.checkIsInMultiOrPipeline();
        this.client.zrangeByLex(key, min, max, offset, count);
        final List<String> members = this.client.getMultiBulkReply();
        if (members == null) {
            return null;
        }
        return SetFromList.of(members);
    }
    
    @Override
    public Set<String> zrevrangeByLex(final String key, final String max, final String min) {
        this.checkIsInMultiOrPipeline();
        this.client.zrevrangeByLex(key, max, min);
        final List<String> members = this.client.getMultiBulkReply();
        if (members == null) {
            return null;
        }
        return SetFromList.of(members);
    }
    
    @Override
    public Set<String> zrevrangeByLex(final String key, final String max, final String min, final int offset, final int count) {
        this.checkIsInMultiOrPipeline();
        this.client.zrevrangeByLex(key, max, min, offset, count);
        final List<String> members = this.client.getMultiBulkReply();
        if (members == null) {
            return null;
        }
        return SetFromList.of(members);
    }
    
    @Override
    public Long zremrangeByLex(final String key, final String min, final String max) {
        this.checkIsInMultiOrPipeline();
        this.client.zremrangeByLex(key, min, max);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Long strlen(final String key) {
        this.client.strlen(key);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Long lpushx(final String key, final String... string) {
        this.client.lpushx(key, string);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Long persist(final String key) {
        this.client.persist(key);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Long rpushx(final String key, final String... string) {
        this.client.rpushx(key, string);
        return this.client.getIntegerReply();
    }
    
    @Override
    public String echo(final String string) {
        this.client.echo(string);
        return this.client.getBulkReply();
    }
    
    @Override
    public Long linsert(final String key, final BinaryClient.LIST_POSITION where, final String pivot, final String value) {
        this.client.linsert(key, where, pivot, value);
        return this.client.getIntegerReply();
    }
    
    @Override
    public String brpoplpush(final String source, final String destination, final int timeout) {
        this.client.brpoplpush(source, destination, timeout);
        this.client.setTimeoutInfinite();
        try {
            return this.client.getBulkReply();
        }
        finally {
            this.client.rollbackTimeout();
        }
    }
    
    @Override
    public Boolean setbit(final String key, final long offset, final boolean value) {
        this.client.setbit(key, offset, value);
        return this.client.getIntegerReply() == 1L;
    }
    
    @Override
    public Boolean setbit(final String key, final long offset, final String value) {
        this.client.setbit(key, offset, value);
        return this.client.getIntegerReply() == 1L;
    }
    
    @Override
    public Boolean getbit(final String key, final long offset) {
        this.client.getbit(key, offset);
        return this.client.getIntegerReply() == 1L;
    }
    
    @Override
    public Long setrange(final String key, final long offset, final String value) {
        this.client.setrange(key, offset, value);
        return this.client.getIntegerReply();
    }
    
    @Override
    public String getrange(final String key, final long startOffset, final long endOffset) {
        this.client.getrange(key, startOffset, endOffset);
        return this.client.getBulkReply();
    }
    
    public Long bitpos(final String key, final boolean value) {
        return this.bitpos(key, value, new BitPosParams());
    }
    
    public Long bitpos(final String key, final boolean value, final BitPosParams params) {
        this.client.bitpos(key, value, params);
        return this.client.getIntegerReply();
    }
    
    @Override
    public List<String> configGet(final String pattern) {
        this.client.configGet(pattern);
        return this.client.getMultiBulkReply();
    }
    
    @Override
    public String configSet(final String parameter, final String value) {
        this.client.configSet(parameter, value);
        return this.client.getStatusCodeReply();
    }
    
    @Override
    public Object eval(final String script, final int keyCount, final String... params) {
        this.client.setTimeoutInfinite();
        try {
            this.client.eval(script, keyCount, params);
            return this.getEvalResult();
        }
        finally {
            this.client.rollbackTimeout();
        }
    }
    
    @Override
    public void subscribe(final JedisPubSub jedisPubSub, final String... channels) {
        this.client.setTimeoutInfinite();
        try {
            jedisPubSub.proceed(this.client, channels);
        }
        finally {
            this.client.rollbackTimeout();
        }
    }
    
    @Override
    public Long publish(final String channel, final String message) {
        this.checkIsInMultiOrPipeline();
        this.connect();
        this.client.publish(channel, message);
        return this.client.getIntegerReply();
    }
    
    @Override
    public void psubscribe(final JedisPubSub jedisPubSub, final String... patterns) {
        this.checkIsInMultiOrPipeline();
        this.client.setTimeoutInfinite();
        try {
            jedisPubSub.proceedWithPatterns(this.client, patterns);
        }
        finally {
            this.client.rollbackTimeout();
        }
    }
    
    protected static String[] getParams(final List<String> keys, final List<String> args) {
        final int keyCount = keys.size();
        final int argCount = args.size();
        final String[] params = new String[keyCount + args.size()];
        for (int i = 0; i < keyCount; ++i) {
            params[i] = keys.get(i);
        }
        for (int i = 0; i < argCount; ++i) {
            params[keyCount + i] = args.get(i);
        }
        return params;
    }
    
    @Override
    public Object eval(final String script, final List<String> keys, final List<String> args) {
        return this.eval(script, keys.size(), getParams(keys, args));
    }
    
    @Override
    public Object eval(final String script) {
        return this.eval(script, 0, new String[0]);
    }
    
    @Override
    public Object evalsha(final String script) {
        return this.evalsha(script, 0, new String[0]);
    }
    
    private Object getEvalResult() {
        return this.evalResult(this.client.getOne());
    }
    
    private Object evalResult(final Object result) {
        if (result instanceof byte[]) {
            return SafeEncoder.encode((byte[])result);
        }
        if (result instanceof List) {
            final List<?> list = (List<?>)result;
            final List<Object> listResult = new ArrayList<Object>(list.size());
            for (final Object bin : list) {
                listResult.add(this.evalResult(bin));
            }
            return listResult;
        }
        return result;
    }
    
    @Override
    public Object evalsha(final String sha1, final List<String> keys, final List<String> args) {
        return this.evalsha(sha1, keys.size(), getParams(keys, args));
    }
    
    @Override
    public Object evalsha(final String sha1, final int keyCount, final String... params) {
        this.checkIsInMultiOrPipeline();
        this.client.evalsha(sha1, keyCount, params);
        return this.getEvalResult();
    }
    
    @Override
    public Boolean scriptExists(final String sha1) {
        final String[] a = { sha1 };
        return this.scriptExists(a).get(0);
    }
    
    @Override
    public List<Boolean> scriptExists(final String... sha1) {
        this.client.scriptExists(sha1);
        final List<Long> result = this.client.getIntegerMultiBulkReply();
        final List<Boolean> exists = new ArrayList<Boolean>();
        for (final Long value : result) {
            exists.add(value == 1L);
        }
        return exists;
    }
    
    @Override
    public String scriptLoad(final String script) {
        this.client.scriptLoad(script);
        return this.client.getBulkReply();
    }
    
    @Override
    public List<Slowlog> slowlogGet() {
        this.client.slowlogGet();
        return Slowlog.from(this.client.getObjectMultiBulkReply());
    }
    
    @Override
    public List<Slowlog> slowlogGet(final long entries) {
        this.client.slowlogGet(entries);
        return Slowlog.from(this.client.getObjectMultiBulkReply());
    }
    
    @Override
    public Long objectRefcount(final String string) {
        this.client.objectRefcount(string);
        return this.client.getIntegerReply();
    }
    
    @Override
    public String objectEncoding(final String string) {
        this.client.objectEncoding(string);
        return this.client.getBulkReply();
    }
    
    @Override
    public Long objectIdletime(final String string) {
        this.client.objectIdletime(string);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Long bitcount(final String key) {
        this.client.bitcount(key);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Long bitcount(final String key, final long start, final long end) {
        this.client.bitcount(key, start, end);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Long bitop(final BitOP op, final String destKey, final String... srcKeys) {
        this.client.bitop(op, destKey, srcKeys);
        return this.client.getIntegerReply();
    }
    
    @Override
    public List<Map<String, String>> sentinelMasters() {
        this.client.sentinel("masters");
        final List<Object> reply = this.client.getObjectMultiBulkReply();
        final List<Map<String, String>> masters = new ArrayList<Map<String, String>>();
        for (final Object obj : reply) {
            masters.add(BuilderFactory.STRING_MAP.build(obj));
        }
        return masters;
    }
    
    @Override
    public List<String> sentinelGetMasterAddrByName(final String masterName) {
        this.client.sentinel("get-master-addr-by-name", masterName);
        final List<Object> reply = this.client.getObjectMultiBulkReply();
        return BuilderFactory.STRING_LIST.build(reply);
    }
    
    @Override
    public Long sentinelReset(final String pattern) {
        this.client.sentinel("reset", pattern);
        return this.client.getIntegerReply();
    }
    
    @Override
    public List<Map<String, String>> sentinelSlaves(final String masterName) {
        this.client.sentinel("slaves", masterName);
        final List<Object> reply = this.client.getObjectMultiBulkReply();
        final List<Map<String, String>> slaves = new ArrayList<Map<String, String>>();
        for (final Object obj : reply) {
            slaves.add(BuilderFactory.STRING_MAP.build(obj));
        }
        return slaves;
    }
    
    @Override
    public String sentinelFailover(final String masterName) {
        this.client.sentinel("failover", masterName);
        return this.client.getStatusCodeReply();
    }
    
    @Override
    public String sentinelMonitor(final String masterName, final String ip, final int port, final int quorum) {
        this.client.sentinel("monitor", masterName, ip, String.valueOf(port), String.valueOf(quorum));
        return this.client.getStatusCodeReply();
    }
    
    @Override
    public String sentinelRemove(final String masterName) {
        this.client.sentinel("remove", masterName);
        return this.client.getStatusCodeReply();
    }
    
    @Override
    public String sentinelSet(final String masterName, final Map<String, String> parameterMap) {
        int index = 0;
        final int paramsLength = parameterMap.size() * 2 + 2;
        final String[] params = new String[paramsLength];
        params[index++] = "set";
        params[index++] = masterName;
        for (final Map.Entry<String, String> entry : parameterMap.entrySet()) {
            params[index++] = entry.getKey();
            params[index++] = entry.getValue();
        }
        this.client.sentinel(params);
        return this.client.getStatusCodeReply();
    }
    
    public byte[] dump(final String key) {
        this.checkIsInMultiOrPipeline();
        this.client.dump(key);
        return this.client.getBinaryBulkReply();
    }
    
    public String restore(final String key, final int ttl, final byte[] serializedValue) {
        this.checkIsInMultiOrPipeline();
        this.client.restore(key, ttl, serializedValue);
        return this.client.getStatusCodeReply();
    }
    
    @Deprecated
    public Long pexpire(final String key, final int milliseconds) {
        return this.pexpire(key, (long)milliseconds);
    }
    
    @Override
    public Long pexpire(final String key, final long milliseconds) {
        this.checkIsInMultiOrPipeline();
        this.client.pexpire(key, milliseconds);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Long pexpireAt(final String key, final long millisecondsTimestamp) {
        this.checkIsInMultiOrPipeline();
        this.client.pexpireAt(key, millisecondsTimestamp);
        return this.client.getIntegerReply();
    }
    
    public Long pttl(final String key) {
        this.checkIsInMultiOrPipeline();
        this.client.pttl(key);
        return this.client.getIntegerReply();
    }
    
    @Deprecated
    public String psetex(final String key, final int milliseconds, final String value) {
        return this.psetex(key, (long)milliseconds, value);
    }
    
    public String psetex(final String key, final long milliseconds, final String value) {
        this.checkIsInMultiOrPipeline();
        this.client.psetex(key, milliseconds, value);
        return this.client.getStatusCodeReply();
    }
    
    public String set(final String key, final String value, final String nxxx) {
        this.checkIsInMultiOrPipeline();
        this.client.set(key, value, nxxx);
        return this.client.getStatusCodeReply();
    }
    
    public String set(final String key, final String value, final String nxxx, final String expx, final int time) {
        this.checkIsInMultiOrPipeline();
        this.client.set(key, value, nxxx, expx, time);
        return this.client.getStatusCodeReply();
    }
    
    public String clientKill(final String client) {
        this.checkIsInMultiOrPipeline();
        this.client.clientKill(client);
        return this.client.getStatusCodeReply();
    }
    
    public String clientSetname(final String name) {
        this.checkIsInMultiOrPipeline();
        this.client.clientSetname(name);
        return this.client.getStatusCodeReply();
    }
    
    public String migrate(final String host, final int port, final String key, final int destinationDb, final int timeout) {
        this.checkIsInMultiOrPipeline();
        this.client.migrate(host, port, key, destinationDb, timeout);
        return this.client.getStatusCodeReply();
    }
    
    @Deprecated
    @Override
    public ScanResult<String> scan(final int cursor) {
        return this.scan(cursor, new ScanParams());
    }
    
    @Deprecated
    public ScanResult<String> scan(final int cursor, final ScanParams params) {
        this.checkIsInMultiOrPipeline();
        this.client.scan(cursor, params);
        final List<Object> result = this.client.getObjectMultiBulkReply();
        final int newcursor = Integer.parseInt(new String(result.get(0)));
        final List<String> results = new ArrayList<String>();
        final List<byte[]> rawResults = result.get(1);
        for (final byte[] bs : rawResults) {
            results.add(SafeEncoder.encode(bs));
        }
        return new ScanResult<String>(newcursor, results);
    }
    
    @Deprecated
    @Override
    public ScanResult<Map.Entry<String, String>> hscan(final String key, final int cursor) {
        return this.hscan(key, cursor, new ScanParams());
    }
    
    @Deprecated
    public ScanResult<Map.Entry<String, String>> hscan(final String key, final int cursor, final ScanParams params) {
        this.checkIsInMultiOrPipeline();
        this.client.hscan(key, cursor, params);
        final List<Object> result = this.client.getObjectMultiBulkReply();
        final int newcursor = Integer.parseInt(new String(result.get(0)));
        final List<Map.Entry<String, String>> results = new ArrayList<Map.Entry<String, String>>();
        final List<byte[]> rawResults = result.get(1);
        final Iterator<byte[]> iterator = rawResults.iterator();
        while (iterator.hasNext()) {
            results.add(new AbstractMap.SimpleEntry<String, String>(SafeEncoder.encode(iterator.next()), SafeEncoder.encode(iterator.next())));
        }
        return new ScanResult<Map.Entry<String, String>>(newcursor, results);
    }
    
    @Deprecated
    @Override
    public ScanResult<String> sscan(final String key, final int cursor) {
        return this.sscan(key, cursor, new ScanParams());
    }
    
    @Deprecated
    public ScanResult<String> sscan(final String key, final int cursor, final ScanParams params) {
        this.checkIsInMultiOrPipeline();
        this.client.sscan(key, cursor, params);
        final List<Object> result = this.client.getObjectMultiBulkReply();
        final int newcursor = Integer.parseInt(new String(result.get(0)));
        final List<String> results = new ArrayList<String>();
        final List<byte[]> rawResults = result.get(1);
        for (final byte[] bs : rawResults) {
            results.add(SafeEncoder.encode(bs));
        }
        return new ScanResult<String>(newcursor, results);
    }
    
    @Deprecated
    @Override
    public ScanResult<Tuple> zscan(final String key, final int cursor) {
        return this.zscan(key, cursor, new ScanParams());
    }
    
    @Deprecated
    public ScanResult<Tuple> zscan(final String key, final int cursor, final ScanParams params) {
        this.checkIsInMultiOrPipeline();
        this.client.zscan(key, cursor, params);
        final List<Object> result = this.client.getObjectMultiBulkReply();
        final int newcursor = Integer.parseInt(new String(result.get(0)));
        final List<Tuple> results = new ArrayList<Tuple>();
        final List<byte[]> rawResults = result.get(1);
        final Iterator<byte[]> iterator = rawResults.iterator();
        while (iterator.hasNext()) {
            results.add(new Tuple(SafeEncoder.encode(iterator.next()), Double.valueOf(SafeEncoder.encode(iterator.next()))));
        }
        return new ScanResult<Tuple>(newcursor, results);
    }
    
    @Override
    public ScanResult<String> scan(final String cursor) {
        return this.scan(cursor, new ScanParams());
    }
    
    public ScanResult<String> scan(final String cursor, final ScanParams params) {
        this.checkIsInMultiOrPipeline();
        this.client.scan(cursor, params);
        final List<Object> result = this.client.getObjectMultiBulkReply();
        final String newcursor = new String(result.get(0));
        final List<String> results = new ArrayList<String>();
        final List<byte[]> rawResults = result.get(1);
        for (final byte[] bs : rawResults) {
            results.add(SafeEncoder.encode(bs));
        }
        return new ScanResult<String>(newcursor, results);
    }
    
    @Override
    public ScanResult<Map.Entry<String, String>> hscan(final String key, final String cursor) {
        return this.hscan(key, cursor, new ScanParams());
    }
    
    public ScanResult<Map.Entry<String, String>> hscan(final String key, final String cursor, final ScanParams params) {
        this.checkIsInMultiOrPipeline();
        this.client.hscan(key, cursor, params);
        final List<Object> result = this.client.getObjectMultiBulkReply();
        final String newcursor = new String(result.get(0));
        final List<Map.Entry<String, String>> results = new ArrayList<Map.Entry<String, String>>();
        final List<byte[]> rawResults = result.get(1);
        final Iterator<byte[]> iterator = rawResults.iterator();
        while (iterator.hasNext()) {
            results.add(new AbstractMap.SimpleEntry<String, String>(SafeEncoder.encode(iterator.next()), SafeEncoder.encode(iterator.next())));
        }
        return new ScanResult<Map.Entry<String, String>>(newcursor, results);
    }
    
    @Override
    public ScanResult<String> sscan(final String key, final String cursor) {
        return this.sscan(key, cursor, new ScanParams());
    }
    
    public ScanResult<String> sscan(final String key, final String cursor, final ScanParams params) {
        this.checkIsInMultiOrPipeline();
        this.client.sscan(key, cursor, params);
        final List<Object> result = this.client.getObjectMultiBulkReply();
        final String newcursor = new String(result.get(0));
        final List<String> results = new ArrayList<String>();
        final List<byte[]> rawResults = result.get(1);
        for (final byte[] bs : rawResults) {
            results.add(SafeEncoder.encode(bs));
        }
        return new ScanResult<String>(newcursor, results);
    }
    
    @Override
    public ScanResult<Tuple> zscan(final String key, final String cursor) {
        return this.zscan(key, cursor, new ScanParams());
    }
    
    public ScanResult<Tuple> zscan(final String key, final String cursor, final ScanParams params) {
        this.checkIsInMultiOrPipeline();
        this.client.zscan(key, cursor, params);
        final List<Object> result = this.client.getObjectMultiBulkReply();
        final String newcursor = new String(result.get(0));
        final List<Tuple> results = new ArrayList<Tuple>();
        final List<byte[]> rawResults = result.get(1);
        final Iterator<byte[]> iterator = rawResults.iterator();
        while (iterator.hasNext()) {
            results.add(new Tuple(SafeEncoder.encode(iterator.next()), Double.valueOf(SafeEncoder.encode(iterator.next()))));
        }
        return new ScanResult<Tuple>(newcursor, results);
    }
    
    @Override
    public String clusterNodes() {
        this.checkIsInMultiOrPipeline();
        this.client.clusterNodes();
        return this.client.getBulkReply();
    }
    
    @Override
    public String clusterMeet(final String ip, final int port) {
        this.checkIsInMultiOrPipeline();
        this.client.clusterMeet(ip, port);
        return this.client.getStatusCodeReply();
    }
    
    @Override
    public String clusterReset(final JedisCluster.Reset resetType) {
        this.checkIsInMultiOrPipeline();
        this.client.clusterReset(resetType);
        return this.client.getStatusCodeReply();
    }
    
    @Override
    public String clusterAddSlots(final int... slots) {
        this.checkIsInMultiOrPipeline();
        this.client.clusterAddSlots(slots);
        return this.client.getStatusCodeReply();
    }
    
    @Override
    public String clusterDelSlots(final int... slots) {
        this.checkIsInMultiOrPipeline();
        this.client.clusterDelSlots(slots);
        return this.client.getStatusCodeReply();
    }
    
    @Override
    public String clusterInfo() {
        this.checkIsInMultiOrPipeline();
        this.client.clusterInfo();
        return this.client.getStatusCodeReply();
    }
    
    @Override
    public List<String> clusterGetKeysInSlot(final int slot, final int count) {
        this.checkIsInMultiOrPipeline();
        this.client.clusterGetKeysInSlot(slot, count);
        return this.client.getMultiBulkReply();
    }
    
    @Override
    public String clusterSetSlotNode(final int slot, final String nodeId) {
        this.checkIsInMultiOrPipeline();
        this.client.clusterSetSlotNode(slot, nodeId);
        return this.client.getStatusCodeReply();
    }
    
    @Override
    public String clusterSetSlotMigrating(final int slot, final String nodeId) {
        this.checkIsInMultiOrPipeline();
        this.client.clusterSetSlotMigrating(slot, nodeId);
        return this.client.getStatusCodeReply();
    }
    
    @Override
    public String clusterSetSlotImporting(final int slot, final String nodeId) {
        this.checkIsInMultiOrPipeline();
        this.client.clusterSetSlotImporting(slot, nodeId);
        return this.client.getStatusCodeReply();
    }
    
    @Override
    public String clusterSetSlotStable(final int slot) {
        this.checkIsInMultiOrPipeline();
        this.client.clusterSetSlotStable(slot);
        return this.client.getStatusCodeReply();
    }
    
    @Override
    public String clusterForget(final String nodeId) {
        this.checkIsInMultiOrPipeline();
        this.client.clusterForget(nodeId);
        return this.client.getStatusCodeReply();
    }
    
    @Override
    public String clusterFlushSlots() {
        this.checkIsInMultiOrPipeline();
        this.client.clusterFlushSlots();
        return this.client.getStatusCodeReply();
    }
    
    @Override
    public Long clusterKeySlot(final String key) {
        this.checkIsInMultiOrPipeline();
        this.client.clusterKeySlot(key);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Long clusterCountKeysInSlot(final int slot) {
        this.checkIsInMultiOrPipeline();
        this.client.clusterCountKeysInSlot(slot);
        return this.client.getIntegerReply();
    }
    
    @Override
    public String clusterSaveConfig() {
        this.checkIsInMultiOrPipeline();
        this.client.clusterSaveConfig();
        return this.client.getStatusCodeReply();
    }
    
    @Override
    public String clusterReplicate(final String nodeId) {
        this.checkIsInMultiOrPipeline();
        this.client.clusterReplicate(nodeId);
        return this.client.getStatusCodeReply();
    }
    
    @Override
    public List<String> clusterSlaves(final String nodeId) {
        this.checkIsInMultiOrPipeline();
        this.client.clusterSlaves(nodeId);
        return this.client.getMultiBulkReply();
    }
    
    @Override
    public String clusterFailover() {
        this.checkIsInMultiOrPipeline();
        this.client.clusterFailover();
        return this.client.getStatusCodeReply();
    }
    
    @Override
    public List<Object> clusterSlots() {
        this.checkIsInMultiOrPipeline();
        this.client.clusterSlots();
        return this.client.getObjectMultiBulkReply();
    }
    
    public String asking() {
        this.checkIsInMultiOrPipeline();
        this.client.asking();
        return this.client.getStatusCodeReply();
    }
    
    public List<String> pubsubChannels(final String pattern) {
        this.checkIsInMultiOrPipeline();
        this.client.pubsubChannels(pattern);
        return this.client.getMultiBulkReply();
    }
    
    public Long pubsubNumPat() {
        this.checkIsInMultiOrPipeline();
        this.client.pubsubNumPat();
        return this.client.getIntegerReply();
    }
    
    public Map<String, String> pubsubNumSub(final String... channels) {
        this.checkIsInMultiOrPipeline();
        this.client.pubsubNumSub(channels);
        return BuilderFactory.PUBSUB_NUMSUB_MAP.build(this.client.getBinaryMultiBulkReply());
    }
    
    @Override
    public void close() {
        if (this.dataSource != null) {
            if (this.client.isBroken()) {
                this.dataSource.returnBrokenResource(this);
            }
            else {
                this.dataSource.returnResource(this);
            }
        }
        else {
            this.client.close();
        }
    }
    
    public void setDataSource(final Pool<Jedis> jedisPool) {
        this.dataSource = jedisPool;
    }
    
    @Override
    public Long pfadd(final String key, final String... elements) {
        this.checkIsInMultiOrPipeline();
        this.client.pfadd(key, elements);
        return this.client.getIntegerReply();
    }
    
    @Override
    public long pfcount(final String key) {
        this.checkIsInMultiOrPipeline();
        this.client.pfcount(key);
        return this.client.getIntegerReply();
    }
    
    @Override
    public long pfcount(final String... keys) {
        this.checkIsInMultiOrPipeline();
        this.client.pfcount(keys);
        return this.client.getIntegerReply();
    }
    
    @Override
    public String pfmerge(final String destkey, final String... sourcekeys) {
        this.checkIsInMultiOrPipeline();
        this.client.pfmerge(destkey, sourcekeys);
        return this.client.getStatusCodeReply();
    }
    
    @Override
    public List<String> blpop(final int timeout, final String key) {
        return this.blpop(key, String.valueOf(timeout));
    }
    
    @Override
    public List<String> brpop(final int timeout, final String key) {
        return this.brpop(key, String.valueOf(timeout));
    }
}
