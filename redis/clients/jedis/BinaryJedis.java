package redis.clients.jedis;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.AbstractMap;
import java.util.ArrayList;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.util.SafeEncoder;
import java.util.LinkedHashSet;
import java.util.Collections;
import redis.clients.jedis.exceptions.JedisDataException;
import java.util.Iterator;
import redis.clients.util.JedisByteHashMap;
import java.util.Map;
import java.util.List;
import java.util.Set;
import redis.clients.jedis.exceptions.InvalidURIException;
import redis.clients.util.JedisURIHelper;
import java.net.URI;
import java.io.Closeable;

public class BinaryJedis implements BasicCommands, BinaryJedisCommands, MultiKeyBinaryCommands, AdvancedBinaryJedisCommands, BinaryScriptingCommands, Closeable
{
    protected Client client;
    protected Transaction transaction;
    protected Pipeline pipeline;
    
    public BinaryJedis() {
        this.client = null;
        this.transaction = null;
        this.pipeline = null;
        this.client = new Client();
    }
    
    public BinaryJedis(final String host) {
        this.client = null;
        this.transaction = null;
        this.pipeline = null;
        final URI uri = URI.create(host);
        if (uri.getScheme() != null && uri.getScheme().equals("redis")) {
            this.initializeClientFromURI(uri);
        }
        else {
            this.client = new Client(host);
        }
    }
    
    public BinaryJedis(final String host, final int port) {
        this.client = null;
        this.transaction = null;
        this.pipeline = null;
        this.client = new Client(host, port);
    }
    
    public BinaryJedis(final String host, final int port, final int timeout) {
        this.client = null;
        this.transaction = null;
        this.pipeline = null;
        (this.client = new Client(host, port)).setConnectionTimeout(timeout);
        this.client.setSoTimeout(timeout);
    }
    
    public BinaryJedis(final String host, final int port, final int connectionTimeout, final int soTimeout) {
        this.client = null;
        this.transaction = null;
        this.pipeline = null;
        (this.client = new Client(host, port)).setConnectionTimeout(connectionTimeout);
        this.client.setSoTimeout(soTimeout);
    }
    
    public BinaryJedis(final JedisShardInfo shardInfo) {
        this.client = null;
        this.transaction = null;
        this.pipeline = null;
        (this.client = new Client(shardInfo.getHost(), shardInfo.getPort())).setConnectionTimeout(shardInfo.getConnectionTimeout());
        this.client.setSoTimeout(shardInfo.getSoTimeout());
        this.client.setPassword(shardInfo.getPassword());
        this.client.setDb(shardInfo.getDb());
    }
    
    public BinaryJedis(final URI uri) {
        this.client = null;
        this.transaction = null;
        this.pipeline = null;
        this.initializeClientFromURI(uri);
    }
    
    public BinaryJedis(final URI uri, final int timeout) {
        this.client = null;
        this.transaction = null;
        this.pipeline = null;
        this.initializeClientFromURI(uri);
        this.client.setConnectionTimeout(timeout);
        this.client.setSoTimeout(timeout);
    }
    
    public BinaryJedis(final URI uri, final int connectionTimeout, final int soTimeout) {
        this.client = null;
        this.transaction = null;
        this.pipeline = null;
        this.initializeClientFromURI(uri);
        this.client.setConnectionTimeout(connectionTimeout);
        this.client.setSoTimeout(soTimeout);
    }
    
    private void initializeClientFromURI(final URI uri) {
        if (!JedisURIHelper.isValid(uri)) {
            throw new InvalidURIException(String.format("Cannot open Redis connection due invalid URI. %s", uri.toString()));
        }
        this.client = new Client(uri.getHost(), uri.getPort());
        final String password = JedisURIHelper.getPassword(uri);
        if (password != null) {
            this.client.auth(password);
            this.client.getStatusCodeReply();
        }
        final int dbIndex = JedisURIHelper.getDBIndex(uri);
        if (dbIndex > 0) {
            this.client.select(dbIndex);
            this.client.getStatusCodeReply();
            this.client.setDb(dbIndex);
        }
    }
    
    @Override
    public String ping() {
        this.checkIsInMultiOrPipeline();
        this.client.ping();
        return this.client.getStatusCodeReply();
    }
    
    @Override
    public String set(final byte[] key, final byte[] value) {
        this.checkIsInMultiOrPipeline();
        this.client.set(key, value);
        return this.client.getStatusCodeReply();
    }
    
    @Override
    public String set(final byte[] key, final byte[] value, final byte[] nxxx, final byte[] expx, final long time) {
        this.checkIsInMultiOrPipeline();
        this.client.set(key, value, nxxx, expx, time);
        return this.client.getStatusCodeReply();
    }
    
    @Override
    public byte[] get(final byte[] key) {
        this.checkIsInMultiOrPipeline();
        this.client.get(key);
        return this.client.getBinaryBulkReply();
    }
    
    @Override
    public String quit() {
        this.checkIsInMultiOrPipeline();
        this.client.quit();
        return this.client.getStatusCodeReply();
    }
    
    @Override
    public Boolean exists(final byte[] key) {
        this.checkIsInMultiOrPipeline();
        this.client.exists(key);
        return this.client.getIntegerReply() == 1L;
    }
    
    @Override
    public Long del(final byte[]... keys) {
        this.checkIsInMultiOrPipeline();
        this.client.del(keys);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Long del(final byte[] key) {
        this.checkIsInMultiOrPipeline();
        this.client.del(new byte[][] { key });
        return this.client.getIntegerReply();
    }
    
    @Override
    public String type(final byte[] key) {
        this.checkIsInMultiOrPipeline();
        this.client.type(key);
        return this.client.getStatusCodeReply();
    }
    
    @Override
    public String flushDB() {
        this.checkIsInMultiOrPipeline();
        this.client.flushDB();
        return this.client.getStatusCodeReply();
    }
    
    @Override
    public Set<byte[]> keys(final byte[] pattern) {
        this.checkIsInMultiOrPipeline();
        this.client.keys(pattern);
        return SetFromList.of(this.client.getBinaryMultiBulkReply());
    }
    
    @Override
    public byte[] randomBinaryKey() {
        this.checkIsInMultiOrPipeline();
        this.client.randomKey();
        return this.client.getBinaryBulkReply();
    }
    
    @Override
    public String rename(final byte[] oldkey, final byte[] newkey) {
        this.checkIsInMultiOrPipeline();
        this.client.rename(oldkey, newkey);
        return this.client.getStatusCodeReply();
    }
    
    @Override
    public Long renamenx(final byte[] oldkey, final byte[] newkey) {
        this.checkIsInMultiOrPipeline();
        this.client.renamenx(oldkey, newkey);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Long dbSize() {
        this.checkIsInMultiOrPipeline();
        this.client.dbSize();
        return this.client.getIntegerReply();
    }
    
    @Override
    public Long expire(final byte[] key, final int seconds) {
        this.checkIsInMultiOrPipeline();
        this.client.expire(key, seconds);
        return this.client.getIntegerReply();
    }
    
    @Deprecated
    @Override
    public Long pexpire(final String key, final long milliseconds) {
        this.checkIsInMultiOrPipeline();
        this.client.pexpire(key, milliseconds);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Long expireAt(final byte[] key, final long unixTime) {
        this.checkIsInMultiOrPipeline();
        this.client.expireAt(key, unixTime);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Long ttl(final byte[] key) {
        this.checkIsInMultiOrPipeline();
        this.client.ttl(key);
        return this.client.getIntegerReply();
    }
    
    @Override
    public String select(final int index) {
        this.checkIsInMultiOrPipeline();
        this.client.select(index);
        final String statusCodeReply = this.client.getStatusCodeReply();
        this.client.setDb(index);
        return statusCodeReply;
    }
    
    @Override
    public Long move(final byte[] key, final int dbIndex) {
        this.checkIsInMultiOrPipeline();
        this.client.move(key, dbIndex);
        return this.client.getIntegerReply();
    }
    
    @Override
    public String flushAll() {
        this.checkIsInMultiOrPipeline();
        this.client.flushAll();
        return this.client.getStatusCodeReply();
    }
    
    @Override
    public byte[] getSet(final byte[] key, final byte[] value) {
        this.checkIsInMultiOrPipeline();
        this.client.getSet(key, value);
        return this.client.getBinaryBulkReply();
    }
    
    @Override
    public List<byte[]> mget(final byte[]... keys) {
        this.checkIsInMultiOrPipeline();
        this.client.mget(keys);
        return this.client.getBinaryMultiBulkReply();
    }
    
    @Override
    public Long setnx(final byte[] key, final byte[] value) {
        this.checkIsInMultiOrPipeline();
        this.client.setnx(key, value);
        return this.client.getIntegerReply();
    }
    
    @Override
    public String setex(final byte[] key, final int seconds, final byte[] value) {
        this.checkIsInMultiOrPipeline();
        this.client.setex(key, seconds, value);
        return this.client.getStatusCodeReply();
    }
    
    @Override
    public String mset(final byte[]... keysvalues) {
        this.checkIsInMultiOrPipeline();
        this.client.mset(keysvalues);
        return this.client.getStatusCodeReply();
    }
    
    @Override
    public Long msetnx(final byte[]... keysvalues) {
        this.checkIsInMultiOrPipeline();
        this.client.msetnx(keysvalues);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Long decrBy(final byte[] key, final long integer) {
        this.checkIsInMultiOrPipeline();
        this.client.decrBy(key, integer);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Long decr(final byte[] key) {
        this.checkIsInMultiOrPipeline();
        this.client.decr(key);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Long incrBy(final byte[] key, final long integer) {
        this.checkIsInMultiOrPipeline();
        this.client.incrBy(key, integer);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Double incrByFloat(final byte[] key, final double integer) {
        this.checkIsInMultiOrPipeline();
        this.client.incrByFloat(key, integer);
        final String dval = this.client.getBulkReply();
        return (dval != null) ? new Double(dval) : null;
    }
    
    @Override
    public Long incr(final byte[] key) {
        this.checkIsInMultiOrPipeline();
        this.client.incr(key);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Long append(final byte[] key, final byte[] value) {
        this.checkIsInMultiOrPipeline();
        this.client.append(key, value);
        return this.client.getIntegerReply();
    }
    
    @Override
    public byte[] substr(final byte[] key, final int start, final int end) {
        this.checkIsInMultiOrPipeline();
        this.client.substr(key, start, end);
        return this.client.getBinaryBulkReply();
    }
    
    @Override
    public Long hset(final byte[] key, final byte[] field, final byte[] value) {
        this.checkIsInMultiOrPipeline();
        this.client.hset(key, field, value);
        return this.client.getIntegerReply();
    }
    
    @Override
    public byte[] hget(final byte[] key, final byte[] field) {
        this.checkIsInMultiOrPipeline();
        this.client.hget(key, field);
        return this.client.getBinaryBulkReply();
    }
    
    @Override
    public Long hsetnx(final byte[] key, final byte[] field, final byte[] value) {
        this.checkIsInMultiOrPipeline();
        this.client.hsetnx(key, field, value);
        return this.client.getIntegerReply();
    }
    
    @Override
    public String hmset(final byte[] key, final Map<byte[], byte[]> hash) {
        this.checkIsInMultiOrPipeline();
        this.client.hmset(key, hash);
        return this.client.getStatusCodeReply();
    }
    
    @Override
    public List<byte[]> hmget(final byte[] key, final byte[]... fields) {
        this.checkIsInMultiOrPipeline();
        this.client.hmget(key, fields);
        return this.client.getBinaryMultiBulkReply();
    }
    
    @Override
    public Long hincrBy(final byte[] key, final byte[] field, final long value) {
        this.checkIsInMultiOrPipeline();
        this.client.hincrBy(key, field, value);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Double hincrByFloat(final byte[] key, final byte[] field, final double value) {
        this.checkIsInMultiOrPipeline();
        this.client.hincrByFloat(key, field, value);
        final String dval = this.client.getBulkReply();
        return (dval != null) ? new Double(dval) : null;
    }
    
    @Override
    public Boolean hexists(final byte[] key, final byte[] field) {
        this.checkIsInMultiOrPipeline();
        this.client.hexists(key, field);
        return this.client.getIntegerReply() == 1L;
    }
    
    @Override
    public Long hdel(final byte[] key, final byte[]... fields) {
        this.checkIsInMultiOrPipeline();
        this.client.hdel(key, fields);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Long hlen(final byte[] key) {
        this.checkIsInMultiOrPipeline();
        this.client.hlen(key);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Set<byte[]> hkeys(final byte[] key) {
        this.checkIsInMultiOrPipeline();
        this.client.hkeys(key);
        return SetFromList.of(this.client.getBinaryMultiBulkReply());
    }
    
    @Override
    public List<byte[]> hvals(final byte[] key) {
        this.checkIsInMultiOrPipeline();
        this.client.hvals(key);
        return this.client.getBinaryMultiBulkReply();
    }
    
    @Override
    public Map<byte[], byte[]> hgetAll(final byte[] key) {
        this.checkIsInMultiOrPipeline();
        this.client.hgetAll(key);
        final List<byte[]> flatHash = this.client.getBinaryMultiBulkReply();
        final Map<byte[], byte[]> hash = new JedisByteHashMap();
        final Iterator<byte[]> iterator = flatHash.iterator();
        while (iterator.hasNext()) {
            hash.put(iterator.next(), iterator.next());
        }
        return hash;
    }
    
    @Override
    public Long rpush(final byte[] key, final byte[]... strings) {
        this.checkIsInMultiOrPipeline();
        this.client.rpush(key, strings);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Long lpush(final byte[] key, final byte[]... strings) {
        this.checkIsInMultiOrPipeline();
        this.client.lpush(key, strings);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Long llen(final byte[] key) {
        this.checkIsInMultiOrPipeline();
        this.client.llen(key);
        return this.client.getIntegerReply();
    }
    
    @Override
    public List<byte[]> lrange(final byte[] key, final long start, final long end) {
        this.checkIsInMultiOrPipeline();
        this.client.lrange(key, start, end);
        return this.client.getBinaryMultiBulkReply();
    }
    
    @Override
    public String ltrim(final byte[] key, final long start, final long end) {
        this.checkIsInMultiOrPipeline();
        this.client.ltrim(key, start, end);
        return this.client.getStatusCodeReply();
    }
    
    @Override
    public byte[] lindex(final byte[] key, final long index) {
        this.checkIsInMultiOrPipeline();
        this.client.lindex(key, index);
        return this.client.getBinaryBulkReply();
    }
    
    @Override
    public String lset(final byte[] key, final long index, final byte[] value) {
        this.checkIsInMultiOrPipeline();
        this.client.lset(key, index, value);
        return this.client.getStatusCodeReply();
    }
    
    @Override
    public Long lrem(final byte[] key, final long count, final byte[] value) {
        this.checkIsInMultiOrPipeline();
        this.client.lrem(key, count, value);
        return this.client.getIntegerReply();
    }
    
    @Override
    public byte[] lpop(final byte[] key) {
        this.checkIsInMultiOrPipeline();
        this.client.lpop(key);
        return this.client.getBinaryBulkReply();
    }
    
    @Override
    public byte[] rpop(final byte[] key) {
        this.checkIsInMultiOrPipeline();
        this.client.rpop(key);
        return this.client.getBinaryBulkReply();
    }
    
    @Override
    public byte[] rpoplpush(final byte[] srckey, final byte[] dstkey) {
        this.checkIsInMultiOrPipeline();
        this.client.rpoplpush(srckey, dstkey);
        return this.client.getBinaryBulkReply();
    }
    
    @Override
    public Long sadd(final byte[] key, final byte[]... members) {
        this.checkIsInMultiOrPipeline();
        this.client.sadd(key, members);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Set<byte[]> smembers(final byte[] key) {
        this.checkIsInMultiOrPipeline();
        this.client.smembers(key);
        return SetFromList.of(this.client.getBinaryMultiBulkReply());
    }
    
    @Override
    public Long srem(final byte[] key, final byte[]... member) {
        this.checkIsInMultiOrPipeline();
        this.client.srem(key, member);
        return this.client.getIntegerReply();
    }
    
    @Override
    public byte[] spop(final byte[] key) {
        this.checkIsInMultiOrPipeline();
        this.client.spop(key);
        return this.client.getBinaryBulkReply();
    }
    
    @Override
    public Set<byte[]> spop(final byte[] key, final long count) {
        this.checkIsInMultiOrPipeline();
        this.client.spop(key, count);
        return SetFromList.of(this.client.getBinaryMultiBulkReply());
    }
    
    @Override
    public Long smove(final byte[] srckey, final byte[] dstkey, final byte[] member) {
        this.checkIsInMultiOrPipeline();
        this.client.smove(srckey, dstkey, member);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Long scard(final byte[] key) {
        this.checkIsInMultiOrPipeline();
        this.client.scard(key);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Boolean sismember(final byte[] key, final byte[] member) {
        this.checkIsInMultiOrPipeline();
        this.client.sismember(key, member);
        return this.client.getIntegerReply() == 1L;
    }
    
    @Override
    public Set<byte[]> sinter(final byte[]... keys) {
        this.checkIsInMultiOrPipeline();
        this.client.sinter(keys);
        return SetFromList.of(this.client.getBinaryMultiBulkReply());
    }
    
    @Override
    public Long sinterstore(final byte[] dstkey, final byte[]... keys) {
        this.checkIsInMultiOrPipeline();
        this.client.sinterstore(dstkey, keys);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Set<byte[]> sunion(final byte[]... keys) {
        this.checkIsInMultiOrPipeline();
        this.client.sunion(keys);
        return SetFromList.of(this.client.getBinaryMultiBulkReply());
    }
    
    @Override
    public Long sunionstore(final byte[] dstkey, final byte[]... keys) {
        this.checkIsInMultiOrPipeline();
        this.client.sunionstore(dstkey, keys);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Set<byte[]> sdiff(final byte[]... keys) {
        this.checkIsInMultiOrPipeline();
        this.client.sdiff(keys);
        return SetFromList.of(this.client.getBinaryMultiBulkReply());
    }
    
    @Override
    public Long sdiffstore(final byte[] dstkey, final byte[]... keys) {
        this.checkIsInMultiOrPipeline();
        this.client.sdiffstore(dstkey, keys);
        return this.client.getIntegerReply();
    }
    
    @Override
    public byte[] srandmember(final byte[] key) {
        this.checkIsInMultiOrPipeline();
        this.client.srandmember(key);
        return this.client.getBinaryBulkReply();
    }
    
    @Override
    public List<byte[]> srandmember(final byte[] key, final int count) {
        this.checkIsInMultiOrPipeline();
        this.client.srandmember(key, count);
        return this.client.getBinaryMultiBulkReply();
    }
    
    @Override
    public Long zadd(final byte[] key, final double score, final byte[] member) {
        this.checkIsInMultiOrPipeline();
        this.client.zadd(key, score, member);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Long zadd(final byte[] key, final Map<byte[], Double> scoreMembers) {
        this.checkIsInMultiOrPipeline();
        this.client.zaddBinary(key, scoreMembers);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Set<byte[]> zrange(final byte[] key, final long start, final long end) {
        this.checkIsInMultiOrPipeline();
        this.client.zrange(key, start, end);
        return SetFromList.of(this.client.getBinaryMultiBulkReply());
    }
    
    @Override
    public Long zrem(final byte[] key, final byte[]... members) {
        this.checkIsInMultiOrPipeline();
        this.client.zrem(key, members);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Double zincrby(final byte[] key, final double score, final byte[] member) {
        this.checkIsInMultiOrPipeline();
        this.client.zincrby(key, score, member);
        final String newscore = this.client.getBulkReply();
        return Double.valueOf(newscore);
    }
    
    @Override
    public Long zrank(final byte[] key, final byte[] member) {
        this.checkIsInMultiOrPipeline();
        this.client.zrank(key, member);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Long zrevrank(final byte[] key, final byte[] member) {
        this.checkIsInMultiOrPipeline();
        this.client.zrevrank(key, member);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Set<byte[]> zrevrange(final byte[] key, final long start, final long end) {
        this.checkIsInMultiOrPipeline();
        this.client.zrevrange(key, start, end);
        return SetFromList.of(this.client.getBinaryMultiBulkReply());
    }
    
    @Override
    public Set<Tuple> zrangeWithScores(final byte[] key, final long start, final long end) {
        this.checkIsInMultiOrPipeline();
        this.client.zrangeWithScores(key, start, end);
        return this.getBinaryTupledSet();
    }
    
    @Override
    public Set<Tuple> zrevrangeWithScores(final byte[] key, final long start, final long end) {
        this.checkIsInMultiOrPipeline();
        this.client.zrevrangeWithScores(key, start, end);
        return this.getBinaryTupledSet();
    }
    
    @Override
    public Long zcard(final byte[] key) {
        this.checkIsInMultiOrPipeline();
        this.client.zcard(key);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Double zscore(final byte[] key, final byte[] member) {
        this.checkIsInMultiOrPipeline();
        this.client.zscore(key, member);
        final String score = this.client.getBulkReply();
        return (score != null) ? new Double(score) : null;
    }
    
    public Transaction multi() {
        this.client.multi();
        return this.transaction = new Transaction(this.client);
    }
    
    @Deprecated
    public List<Object> multi(final TransactionBlock jedisTransaction) {
        jedisTransaction.setClient(this.client);
        this.client.multi();
        jedisTransaction.execute();
        return jedisTransaction.exec();
    }
    
    protected void checkIsInMultiOrPipeline() {
        if (this.client.isInMulti()) {
            throw new JedisDataException("Cannot use Jedis when in Multi. Please use Transation or reset jedis state.");
        }
        if (this.pipeline != null && this.pipeline.hasPipelinedResponse()) {
            throw new JedisDataException("Cannot use Jedis when in Pipeline. Please use Pipeline or reset jedis state .");
        }
    }
    
    public void connect() {
        this.client.connect();
    }
    
    public void disconnect() {
        this.client.disconnect();
    }
    
    public void resetState() {
        if (this.client.isConnected()) {
            if (this.transaction != null) {
                this.transaction.clear();
            }
            if (this.pipeline != null) {
                this.pipeline.clear();
            }
            if (this.client.isInWatch()) {
                this.unwatch();
            }
            this.client.resetState();
        }
        this.transaction = null;
        this.pipeline = null;
    }
    
    @Override
    public String watch(final byte[]... keys) {
        this.client.watch(keys);
        return this.client.getStatusCodeReply();
    }
    
    @Override
    public String unwatch() {
        this.client.unwatch();
        return this.client.getStatusCodeReply();
    }
    
    @Override
    public void close() {
        this.client.close();
    }
    
    @Override
    public List<byte[]> sort(final byte[] key) {
        this.checkIsInMultiOrPipeline();
        this.client.sort(key);
        return this.client.getBinaryMultiBulkReply();
    }
    
    @Override
    public List<byte[]> sort(final byte[] key, final SortingParams sortingParameters) {
        this.checkIsInMultiOrPipeline();
        this.client.sort(key, sortingParameters);
        return this.client.getBinaryMultiBulkReply();
    }
    
    @Override
    public List<byte[]> blpop(final int timeout, final byte[]... keys) {
        return this.blpop(this.getArgsAddTimeout(timeout, keys));
    }
    
    private byte[][] getArgsAddTimeout(final int timeout, final byte[][] keys) {
        final int size = keys.length;
        final byte[][] args = new byte[size + 1][];
        for (int at = 0; at != size; ++at) {
            args[at] = keys[at];
        }
        args[size] = Protocol.toByteArray(timeout);
        return args;
    }
    
    @Override
    public Long sort(final byte[] key, final SortingParams sortingParameters, final byte[] dstkey) {
        this.checkIsInMultiOrPipeline();
        this.client.sort(key, sortingParameters, dstkey);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Long sort(final byte[] key, final byte[] dstkey) {
        this.checkIsInMultiOrPipeline();
        this.client.sort(key, dstkey);
        return this.client.getIntegerReply();
    }
    
    @Override
    public List<byte[]> brpop(final int timeout, final byte[]... keys) {
        return this.brpop(this.getArgsAddTimeout(timeout, keys));
    }
    
    @Deprecated
    @Override
    public List<byte[]> blpop(final byte[] arg) {
        return this.blpop(new byte[][] { arg });
    }
    
    @Deprecated
    @Override
    public List<byte[]> brpop(final byte[] arg) {
        return this.brpop(new byte[][] { arg });
    }
    
    @Override
    public List<byte[]> blpop(final byte[]... args) {
        this.checkIsInMultiOrPipeline();
        this.client.blpop(args);
        this.client.setTimeoutInfinite();
        try {
            return this.client.getBinaryMultiBulkReply();
        }
        finally {
            this.client.rollbackTimeout();
        }
    }
    
    @Override
    public List<byte[]> brpop(final byte[]... args) {
        this.checkIsInMultiOrPipeline();
        this.client.brpop(args);
        this.client.setTimeoutInfinite();
        try {
            return this.client.getBinaryMultiBulkReply();
        }
        finally {
            this.client.rollbackTimeout();
        }
    }
    
    @Override
    public String auth(final String password) {
        this.checkIsInMultiOrPipeline();
        this.client.auth(password);
        return this.client.getStatusCodeReply();
    }
    
    @Deprecated
    public List<Object> pipelined(final PipelineBlock jedisPipeline) {
        jedisPipeline.setClient(this.client);
        jedisPipeline.execute();
        return jedisPipeline.syncAndReturnAll();
    }
    
    public Pipeline pipelined() {
        (this.pipeline = new Pipeline()).setClient(this.client);
        return this.pipeline;
    }
    
    @Override
    public Long zcount(final byte[] key, final double min, final double max) {
        return this.zcount(key, Protocol.toByteArray(min), Protocol.toByteArray(max));
    }
    
    @Override
    public Long zcount(final byte[] key, final byte[] min, final byte[] max) {
        this.checkIsInMultiOrPipeline();
        this.client.zcount(key, min, max);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Set<byte[]> zrangeByScore(final byte[] key, final double min, final double max) {
        return this.zrangeByScore(key, Protocol.toByteArray(min), Protocol.toByteArray(max));
    }
    
    @Override
    public Set<byte[]> zrangeByScore(final byte[] key, final byte[] min, final byte[] max) {
        this.checkIsInMultiOrPipeline();
        this.client.zrangeByScore(key, min, max);
        return SetFromList.of(this.client.getBinaryMultiBulkReply());
    }
    
    @Override
    public Set<byte[]> zrangeByScore(final byte[] key, final double min, final double max, final int offset, final int count) {
        return this.zrangeByScore(key, Protocol.toByteArray(min), Protocol.toByteArray(max), offset, count);
    }
    
    @Override
    public Set<byte[]> zrangeByScore(final byte[] key, final byte[] min, final byte[] max, final int offset, final int count) {
        this.checkIsInMultiOrPipeline();
        this.client.zrangeByScore(key, min, max, offset, count);
        return SetFromList.of(this.client.getBinaryMultiBulkReply());
    }
    
    @Override
    public Set<Tuple> zrangeByScoreWithScores(final byte[] key, final double min, final double max) {
        return this.zrangeByScoreWithScores(key, Protocol.toByteArray(min), Protocol.toByteArray(max));
    }
    
    @Override
    public Set<Tuple> zrangeByScoreWithScores(final byte[] key, final byte[] min, final byte[] max) {
        this.checkIsInMultiOrPipeline();
        this.client.zrangeByScoreWithScores(key, min, max);
        return this.getBinaryTupledSet();
    }
    
    @Override
    public Set<Tuple> zrangeByScoreWithScores(final byte[] key, final double min, final double max, final int offset, final int count) {
        return this.zrangeByScoreWithScores(key, Protocol.toByteArray(min), Protocol.toByteArray(max), offset, count);
    }
    
    @Override
    public Set<Tuple> zrangeByScoreWithScores(final byte[] key, final byte[] min, final byte[] max, final int offset, final int count) {
        this.checkIsInMultiOrPipeline();
        this.client.zrangeByScoreWithScores(key, min, max, offset, count);
        return this.getBinaryTupledSet();
    }
    
    private Set<Tuple> getBinaryTupledSet() {
        this.checkIsInMultiOrPipeline();
        final List<byte[]> membersWithScores = this.client.getBinaryMultiBulkReply();
        if (membersWithScores.size() == 0) {
            return Collections.emptySet();
        }
        final Set<Tuple> set = new LinkedHashSet<Tuple>(membersWithScores.size() / 2, 1.0f);
        final Iterator<byte[]> iterator = membersWithScores.iterator();
        while (iterator.hasNext()) {
            set.add(new Tuple(iterator.next(), Double.valueOf(SafeEncoder.encode(iterator.next()))));
        }
        return set;
    }
    
    @Override
    public Set<byte[]> zrevrangeByScore(final byte[] key, final double max, final double min) {
        return this.zrevrangeByScore(key, Protocol.toByteArray(max), Protocol.toByteArray(min));
    }
    
    @Override
    public Set<byte[]> zrevrangeByScore(final byte[] key, final byte[] max, final byte[] min) {
        this.checkIsInMultiOrPipeline();
        this.client.zrevrangeByScore(key, max, min);
        return SetFromList.of(this.client.getBinaryMultiBulkReply());
    }
    
    @Override
    public Set<byte[]> zrevrangeByScore(final byte[] key, final double max, final double min, final int offset, final int count) {
        return this.zrevrangeByScore(key, Protocol.toByteArray(max), Protocol.toByteArray(min), offset, count);
    }
    
    @Override
    public Set<byte[]> zrevrangeByScore(final byte[] key, final byte[] max, final byte[] min, final int offset, final int count) {
        this.checkIsInMultiOrPipeline();
        this.client.zrevrangeByScore(key, max, min, offset, count);
        return SetFromList.of(this.client.getBinaryMultiBulkReply());
    }
    
    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(final byte[] key, final double max, final double min) {
        return this.zrevrangeByScoreWithScores(key, Protocol.toByteArray(max), Protocol.toByteArray(min));
    }
    
    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(final byte[] key, final double max, final double min, final int offset, final int count) {
        return this.zrevrangeByScoreWithScores(key, Protocol.toByteArray(max), Protocol.toByteArray(min), offset, count);
    }
    
    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(final byte[] key, final byte[] max, final byte[] min) {
        this.checkIsInMultiOrPipeline();
        this.client.zrevrangeByScoreWithScores(key, max, min);
        return this.getBinaryTupledSet();
    }
    
    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(final byte[] key, final byte[] max, final byte[] min, final int offset, final int count) {
        this.checkIsInMultiOrPipeline();
        this.client.zrevrangeByScoreWithScores(key, max, min, offset, count);
        return this.getBinaryTupledSet();
    }
    
    @Override
    public Long zremrangeByRank(final byte[] key, final long start, final long end) {
        this.checkIsInMultiOrPipeline();
        this.client.zremrangeByRank(key, start, end);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Long zremrangeByScore(final byte[] key, final double start, final double end) {
        return this.zremrangeByScore(key, Protocol.toByteArray(start), Protocol.toByteArray(end));
    }
    
    @Override
    public Long zremrangeByScore(final byte[] key, final byte[] start, final byte[] end) {
        this.checkIsInMultiOrPipeline();
        this.client.zremrangeByScore(key, start, end);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Long zunionstore(final byte[] dstkey, final byte[]... sets) {
        this.checkIsInMultiOrPipeline();
        this.client.zunionstore(dstkey, sets);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Long zunionstore(final byte[] dstkey, final ZParams params, final byte[]... sets) {
        this.checkIsInMultiOrPipeline();
        this.client.zunionstore(dstkey, params, sets);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Long zinterstore(final byte[] dstkey, final byte[]... sets) {
        this.checkIsInMultiOrPipeline();
        this.client.zinterstore(dstkey, sets);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Long zinterstore(final byte[] dstkey, final ZParams params, final byte[]... sets) {
        this.checkIsInMultiOrPipeline();
        this.client.zinterstore(dstkey, params, sets);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Long zlexcount(final byte[] key, final byte[] min, final byte[] max) {
        this.checkIsInMultiOrPipeline();
        this.client.zlexcount(key, min, max);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Set<byte[]> zrangeByLex(final byte[] key, final byte[] min, final byte[] max) {
        this.checkIsInMultiOrPipeline();
        this.client.zrangeByLex(key, min, max);
        return SetFromList.of(this.client.getBinaryMultiBulkReply());
    }
    
    @Override
    public Set<byte[]> zrangeByLex(final byte[] key, final byte[] min, final byte[] max, final int offset, final int count) {
        this.checkIsInMultiOrPipeline();
        this.client.zrangeByLex(key, min, max, offset, count);
        return SetFromList.of(this.client.getBinaryMultiBulkReply());
    }
    
    @Override
    public Set<byte[]> zrevrangeByLex(final byte[] key, final byte[] max, final byte[] min) {
        this.checkIsInMultiOrPipeline();
        this.client.zrevrangeByLex(key, max, min);
        return SetFromList.of(this.client.getBinaryMultiBulkReply());
    }
    
    @Override
    public Set<byte[]> zrevrangeByLex(final byte[] key, final byte[] max, final byte[] min, final int offset, final int count) {
        this.checkIsInMultiOrPipeline();
        this.client.zrevrangeByLex(key, max, min, offset, count);
        return SetFromList.of(this.client.getBinaryMultiBulkReply());
    }
    
    @Override
    public Long zremrangeByLex(final byte[] key, final byte[] min, final byte[] max) {
        this.checkIsInMultiOrPipeline();
        this.client.zremrangeByLex(key, min, max);
        return this.client.getIntegerReply();
    }
    
    @Override
    public String save() {
        this.client.save();
        return this.client.getStatusCodeReply();
    }
    
    @Override
    public String bgsave() {
        this.client.bgsave();
        return this.client.getStatusCodeReply();
    }
    
    @Override
    public String bgrewriteaof() {
        this.client.bgrewriteaof();
        return this.client.getStatusCodeReply();
    }
    
    @Override
    public Long lastsave() {
        this.client.lastsave();
        return this.client.getIntegerReply();
    }
    
    @Override
    public String shutdown() {
        this.client.shutdown();
        String status;
        try {
            status = this.client.getStatusCodeReply();
        }
        catch (final JedisException ex) {
            status = null;
        }
        return status;
    }
    
    @Override
    public String info() {
        this.client.info();
        return this.client.getBulkReply();
    }
    
    @Override
    public String info(final String section) {
        this.client.info(section);
        return this.client.getBulkReply();
    }
    
    public void monitor(final JedisMonitor jedisMonitor) {
        this.client.monitor();
        this.client.getStatusCodeReply();
        jedisMonitor.proceed(this.client);
    }
    
    @Override
    public String slaveof(final String host, final int port) {
        this.client.slaveof(host, port);
        return this.client.getStatusCodeReply();
    }
    
    @Override
    public String slaveofNoOne() {
        this.client.slaveofNoOne();
        return this.client.getStatusCodeReply();
    }
    
    @Override
    public List<byte[]> configGet(final byte[] pattern) {
        this.client.configGet(pattern);
        return this.client.getBinaryMultiBulkReply();
    }
    
    @Override
    public String configResetStat() {
        this.client.configResetStat();
        return this.client.getStatusCodeReply();
    }
    
    @Override
    public byte[] configSet(final byte[] parameter, final byte[] value) {
        this.client.configSet(parameter, value);
        return this.client.getBinaryBulkReply();
    }
    
    public boolean isConnected() {
        return this.client.isConnected();
    }
    
    @Override
    public Long strlen(final byte[] key) {
        this.client.strlen(key);
        return this.client.getIntegerReply();
    }
    
    public void sync() {
        this.client.sync();
    }
    
    @Override
    public Long lpushx(final byte[] key, final byte[]... string) {
        this.client.lpushx(key, string);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Long persist(final byte[] key) {
        this.client.persist(key);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Long rpushx(final byte[] key, final byte[]... string) {
        this.client.rpushx(key, string);
        return this.client.getIntegerReply();
    }
    
    @Override
    public byte[] echo(final byte[] string) {
        this.client.echo(string);
        return this.client.getBinaryBulkReply();
    }
    
    @Override
    public Long linsert(final byte[] key, final BinaryClient.LIST_POSITION where, final byte[] pivot, final byte[] value) {
        this.client.linsert(key, where, pivot, value);
        return this.client.getIntegerReply();
    }
    
    @Override
    public String debug(final DebugParams params) {
        this.client.debug(params);
        return this.client.getStatusCodeReply();
    }
    
    public Client getClient() {
        return this.client;
    }
    
    @Override
    public byte[] brpoplpush(final byte[] source, final byte[] destination, final int timeout) {
        this.client.brpoplpush(source, destination, timeout);
        this.client.setTimeoutInfinite();
        try {
            return this.client.getBinaryBulkReply();
        }
        finally {
            this.client.rollbackTimeout();
        }
    }
    
    @Override
    public Boolean setbit(final byte[] key, final long offset, final boolean value) {
        this.client.setbit(key, offset, value);
        return this.client.getIntegerReply() == 1L;
    }
    
    @Override
    public Boolean setbit(final byte[] key, final long offset, final byte[] value) {
        this.client.setbit(key, offset, value);
        return this.client.getIntegerReply() == 1L;
    }
    
    @Override
    public Boolean getbit(final byte[] key, final long offset) {
        this.client.getbit(key, offset);
        return this.client.getIntegerReply() == 1L;
    }
    
    public Long bitpos(final byte[] key, final boolean value) {
        return this.bitpos(key, value, new BitPosParams());
    }
    
    public Long bitpos(final byte[] key, final boolean value, final BitPosParams params) {
        this.client.bitpos(key, value, params);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Long setrange(final byte[] key, final long offset, final byte[] value) {
        this.client.setrange(key, offset, value);
        return this.client.getIntegerReply();
    }
    
    @Override
    public byte[] getrange(final byte[] key, final long startOffset, final long endOffset) {
        this.client.getrange(key, startOffset, endOffset);
        return this.client.getBinaryBulkReply();
    }
    
    @Override
    public Long publish(final byte[] channel, final byte[] message) {
        this.client.publish(channel, message);
        return this.client.getIntegerReply();
    }
    
    @Override
    public void subscribe(final BinaryJedisPubSub jedisPubSub, final byte[]... channels) {
        this.client.setTimeoutInfinite();
        try {
            jedisPubSub.proceed(this.client, channels);
        }
        finally {
            this.client.rollbackTimeout();
        }
    }
    
    @Override
    public void psubscribe(final BinaryJedisPubSub jedisPubSub, final byte[]... patterns) {
        this.client.setTimeoutInfinite();
        try {
            jedisPubSub.proceedWithPatterns(this.client, patterns);
        }
        finally {
            this.client.rollbackTimeout();
        }
    }
    
    @Override
    public Long getDB() {
        return this.client.getDB();
    }
    
    @Override
    public Object eval(final byte[] script, final List<byte[]> keys, final List<byte[]> args) {
        return this.eval(script, Protocol.toByteArray(keys.size()), this.getParams(keys, args));
    }
    
    private byte[][] getParams(final List<byte[]> keys, final List<byte[]> args) {
        final int keyCount = keys.size();
        final int argCount = args.size();
        final byte[][] params = new byte[keyCount + argCount][];
        for (int i = 0; i < keyCount; ++i) {
            params[i] = keys.get(i);
        }
        for (int i = 0; i < argCount; ++i) {
            params[keyCount + i] = args.get(i);
        }
        return params;
    }
    
    @Override
    public Object eval(final byte[] script, final byte[] keyCount, final byte[]... params) {
        this.client.setTimeoutInfinite();
        try {
            this.client.eval(script, keyCount, params);
            return this.client.getOne();
        }
        finally {
            this.client.rollbackTimeout();
        }
    }
    
    @Override
    public Object eval(final byte[] script, final int keyCount, final byte[]... params) {
        return this.eval(script, Protocol.toByteArray(keyCount), params);
    }
    
    @Override
    public Object eval(final byte[] script) {
        return this.eval(script, 0);
    }
    
    @Override
    public Object evalsha(final byte[] sha1) {
        return this.evalsha(sha1, 1);
    }
    
    @Override
    public Object evalsha(final byte[] sha1, final List<byte[]> keys, final List<byte[]> args) {
        return this.evalsha(sha1, keys.size(), this.getParams(keys, args));
    }
    
    @Override
    public Object evalsha(final byte[] sha1, final int keyCount, final byte[]... params) {
        this.client.setTimeoutInfinite();
        try {
            this.client.evalsha(sha1, keyCount, params);
            return this.client.getOne();
        }
        finally {
            this.client.rollbackTimeout();
        }
    }
    
    @Override
    public String scriptFlush() {
        this.client.scriptFlush();
        return this.client.getStatusCodeReply();
    }
    
    @Override
    public List<Long> scriptExists(final byte[]... sha1) {
        this.client.scriptExists(sha1);
        return this.client.getIntegerMultiBulkReply();
    }
    
    @Override
    public byte[] scriptLoad(final byte[] script) {
        this.client.scriptLoad(script);
        return this.client.getBinaryBulkReply();
    }
    
    @Override
    public String scriptKill() {
        this.client.scriptKill();
        return this.client.getStatusCodeReply();
    }
    
    @Override
    public String slowlogReset() {
        this.client.slowlogReset();
        return this.client.getBulkReply();
    }
    
    @Override
    public Long slowlogLen() {
        this.client.slowlogLen();
        return this.client.getIntegerReply();
    }
    
    @Override
    public List<byte[]> slowlogGetBinary() {
        this.client.slowlogGet();
        return this.client.getBinaryMultiBulkReply();
    }
    
    @Override
    public List<byte[]> slowlogGetBinary(final long entries) {
        this.client.slowlogGet(entries);
        return this.client.getBinaryMultiBulkReply();
    }
    
    @Override
    public Long objectRefcount(final byte[] key) {
        this.client.objectRefcount(key);
        return this.client.getIntegerReply();
    }
    
    @Override
    public byte[] objectEncoding(final byte[] key) {
        this.client.objectEncoding(key);
        return this.client.getBinaryBulkReply();
    }
    
    @Override
    public Long objectIdletime(final byte[] key) {
        this.client.objectIdletime(key);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Long bitcount(final byte[] key) {
        this.client.bitcount(key);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Long bitcount(final byte[] key, final long start, final long end) {
        this.client.bitcount(key, start, end);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Long bitop(final BitOP op, final byte[] destKey, final byte[]... srcKeys) {
        this.client.bitop(op, destKey, srcKeys);
        return this.client.getIntegerReply();
    }
    
    public byte[] dump(final byte[] key) {
        this.checkIsInMultiOrPipeline();
        this.client.dump(key);
        return this.client.getBinaryBulkReply();
    }
    
    public String restore(final byte[] key, final int ttl, final byte[] serializedValue) {
        this.checkIsInMultiOrPipeline();
        this.client.restore(key, ttl, serializedValue);
        return this.client.getStatusCodeReply();
    }
    
    @Deprecated
    public Long pexpire(final byte[] key, final int milliseconds) {
        return this.pexpire(key, (long)milliseconds);
    }
    
    @Override
    public Long pexpire(final byte[] key, final long milliseconds) {
        this.checkIsInMultiOrPipeline();
        this.client.pexpire(key, milliseconds);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Long pexpireAt(final byte[] key, final long millisecondsTimestamp) {
        this.checkIsInMultiOrPipeline();
        this.client.pexpireAt(key, millisecondsTimestamp);
        return this.client.getIntegerReply();
    }
    
    public Long pttl(final byte[] key) {
        this.checkIsInMultiOrPipeline();
        this.client.pttl(key);
        return this.client.getIntegerReply();
    }
    
    @Deprecated
    public String psetex(final byte[] key, final int milliseconds, final byte[] value) {
        return this.psetex(key, (long)milliseconds, value);
    }
    
    public String psetex(final byte[] key, final long milliseconds, final byte[] value) {
        this.checkIsInMultiOrPipeline();
        this.client.psetex(key, milliseconds, value);
        return this.client.getStatusCodeReply();
    }
    
    public String set(final byte[] key, final byte[] value, final byte[] nxxx) {
        this.checkIsInMultiOrPipeline();
        this.client.set(key, value, nxxx);
        return this.client.getStatusCodeReply();
    }
    
    public String set(final byte[] key, final byte[] value, final byte[] nxxx, final byte[] expx, final int time) {
        this.checkIsInMultiOrPipeline();
        this.client.set(key, value, nxxx, expx, time);
        return this.client.getStatusCodeReply();
    }
    
    public String clientKill(final byte[] client) {
        this.checkIsInMultiOrPipeline();
        this.client.clientKill(client);
        return this.client.getStatusCodeReply();
    }
    
    public String clientGetname() {
        this.checkIsInMultiOrPipeline();
        this.client.clientGetname();
        return this.client.getBulkReply();
    }
    
    public String clientList() {
        this.checkIsInMultiOrPipeline();
        this.client.clientList();
        return this.client.getBulkReply();
    }
    
    public String clientSetname(final byte[] name) {
        this.checkIsInMultiOrPipeline();
        this.client.clientSetname(name);
        return this.client.getBulkReply();
    }
    
    public List<String> time() {
        this.checkIsInMultiOrPipeline();
        this.client.time();
        return this.client.getMultiBulkReply();
    }
    
    public String migrate(final byte[] host, final int port, final byte[] key, final int destinationDb, final int timeout) {
        this.checkIsInMultiOrPipeline();
        this.client.migrate(host, port, key, destinationDb, timeout);
        return this.client.getStatusCodeReply();
    }
    
    @Override
    public Long waitReplicas(final int replicas, final long timeout) {
        this.checkIsInMultiOrPipeline();
        this.client.waitReplicas(replicas, timeout);
        return this.client.getIntegerReply();
    }
    
    @Override
    public Long pfadd(final byte[] key, final byte[]... elements) {
        this.checkIsInMultiOrPipeline();
        this.client.pfadd(key, elements);
        return this.client.getIntegerReply();
    }
    
    @Override
    public long pfcount(final byte[] key) {
        this.checkIsInMultiOrPipeline();
        this.client.pfcount(key);
        return this.client.getIntegerReply();
    }
    
    @Override
    public String pfmerge(final byte[] destkey, final byte[]... sourcekeys) {
        this.checkIsInMultiOrPipeline();
        this.client.pfmerge(destkey, sourcekeys);
        return this.client.getStatusCodeReply();
    }
    
    @Override
    public Long pfcount(final byte[]... keys) {
        this.checkIsInMultiOrPipeline();
        this.client.pfcount(keys);
        return this.client.getIntegerReply();
    }
    
    public ScanResult<byte[]> scan(final byte[] cursor) {
        return this.scan(cursor, new ScanParams());
    }
    
    public ScanResult<byte[]> scan(final byte[] cursor, final ScanParams params) {
        this.checkIsInMultiOrPipeline();
        this.client.scan(cursor, params);
        final List<Object> result = this.client.getObjectMultiBulkReply();
        final byte[] newcursor = result.get(0);
        final List<byte[]> rawResults = result.get(1);
        return new ScanResult<byte[]>(newcursor, rawResults);
    }
    
    public ScanResult<Map.Entry<byte[], byte[]>> hscan(final byte[] key, final byte[] cursor) {
        return this.hscan(key, cursor, new ScanParams());
    }
    
    public ScanResult<Map.Entry<byte[], byte[]>> hscan(final byte[] key, final byte[] cursor, final ScanParams params) {
        this.checkIsInMultiOrPipeline();
        this.client.hscan(key, cursor, params);
        final List<Object> result = this.client.getObjectMultiBulkReply();
        final byte[] newcursor = result.get(0);
        final List<Map.Entry<byte[], byte[]>> results = new ArrayList<Map.Entry<byte[], byte[]>>();
        final List<byte[]> rawResults = result.get(1);
        final Iterator<byte[]> iterator = rawResults.iterator();
        while (iterator.hasNext()) {
            results.add(new AbstractMap.SimpleEntry<byte[], byte[]>(iterator.next(), iterator.next()));
        }
        return new ScanResult<Map.Entry<byte[], byte[]>>(newcursor, results);
    }
    
    public ScanResult<byte[]> sscan(final byte[] key, final byte[] cursor) {
        return this.sscan(key, cursor, new ScanParams());
    }
    
    public ScanResult<byte[]> sscan(final byte[] key, final byte[] cursor, final ScanParams params) {
        this.checkIsInMultiOrPipeline();
        this.client.sscan(key, cursor, params);
        final List<Object> result = this.client.getObjectMultiBulkReply();
        final byte[] newcursor = result.get(0);
        final List<byte[]> rawResults = result.get(1);
        return new ScanResult<byte[]>(newcursor, rawResults);
    }
    
    public ScanResult<Tuple> zscan(final byte[] key, final byte[] cursor) {
        return this.zscan(key, cursor, new ScanParams());
    }
    
    public ScanResult<Tuple> zscan(final byte[] key, final byte[] cursor, final ScanParams params) {
        this.checkIsInMultiOrPipeline();
        this.client.zscan(key, cursor, params);
        final List<Object> result = this.client.getObjectMultiBulkReply();
        final byte[] newcursor = result.get(0);
        final List<Tuple> results = new ArrayList<Tuple>();
        final List<byte[]> rawResults = result.get(1);
        final Iterator<byte[]> iterator = rawResults.iterator();
        while (iterator.hasNext()) {
            results.add(new Tuple(iterator.next(), Double.valueOf(SafeEncoder.encode(iterator.next()))));
        }
        return new ScanResult<Tuple>(newcursor, results);
    }
    
    protected static class SetFromList<E> extends AbstractSet<E>
    {
        private final List<E> list;
        
        private SetFromList(final List<E> list) {
            if (list == null) {
                throw new NullPointerException("list");
            }
            this.list = list;
        }
        
        @Override
        public void clear() {
            this.list.clear();
        }
        
        @Override
        public int size() {
            return this.list.size();
        }
        
        @Override
        public boolean isEmpty() {
            return this.list.isEmpty();
        }
        
        @Override
        public boolean contains(final Object o) {
            return this.list.contains(o);
        }
        
        @Override
        public boolean remove(final Object o) {
            return this.list.remove(o);
        }
        
        @Override
        public boolean add(final E e) {
            return !this.contains(e) && this.list.add(e);
        }
        
        @Override
        public Iterator<E> iterator() {
            return this.list.iterator();
        }
        
        @Override
        public Object[] toArray() {
            return this.list.toArray();
        }
        
        @Override
        public <T> T[] toArray(final T[] a) {
            return this.list.toArray(a);
        }
        
        @Override
        public String toString() {
            return this.list.toString();
        }
        
        @Override
        public int hashCode() {
            return this.list.hashCode();
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof Set)) {
                return false;
            }
            final Collection<?> c = (Collection<?>)o;
            return c.size() == this.size() && this.containsAll(c);
        }
        
        @Override
        public boolean containsAll(final Collection<?> c) {
            return this.list.containsAll(c);
        }
        
        @Override
        public boolean removeAll(final Collection<?> c) {
            return this.list.removeAll(c);
        }
        
        @Override
        public boolean retainAll(final Collection<?> c) {
            return this.list.retainAll(c);
        }
        
        protected static <E> SetFromList<E> of(final List<E> list) {
            return new SetFromList<E>(list);
        }
    }
}
