package com.zoho.security.cache;

import java.util.Collection;
import com.zoho.jedis.v320.Pipeline;
import com.zoho.jedis.v320.Transaction;
import com.zoho.jedis.v320.params.ClientKillParams;
import com.zoho.jedis.v320.Client;
import com.zoho.jedis.v320.BinaryJedisPubSub;
import com.zoho.jedis.v320.DebugParams;
import com.zoho.jedis.v320.JedisMonitor;
import com.zoho.jedis.v320.StreamPendingEntry;
import com.zoho.jedis.v320.StreamEntry;
import com.zoho.jedis.v320.StreamEntryID;
import com.zoho.jedis.v320.Module;
import com.zoho.jedis.v320.params.GeoRadiusParam;
import com.zoho.jedis.v320.GeoRadiusResponse;
import com.zoho.jedis.v320.GeoUnit;
import com.zoho.jedis.v320.GeoCoordinate;
import com.zoho.jedis.v320.ClusterReset;
import com.zoho.jedis.v320.BitOP;
import com.zoho.jedis.v320.util.Slowlog;
import com.zoho.jedis.v320.BitPosParams;
import com.zoho.jedis.v320.ListPosition;
import com.zoho.jedis.v320.ZParams;
import com.zoho.jedis.v320.Tuple;
import com.zoho.jedis.v320.params.ZIncrByParams;
import com.zoho.jedis.v320.params.ZAddParams;
import java.util.Map;
import com.zoho.jedis.v320.params.MigrateParams;
import com.zoho.jedis.v320.JedisPubSub;
import com.zoho.jedis.v320.commands.ProtocolCommand;
import com.zoho.jedis.v320.JedisPoolAbstract;
import java.util.List;
import com.zoho.jedis.v320.SortingParams;
import com.zoho.jedis.v320.params.SetParams;
import java.util.Set;
import com.zoho.jedis.v320.ScanParams;
import com.zoho.jedis.v320.ScanResult;
import com.zoho.instrument.redis.RedisCall;
import com.zoho.jedis.v320.exceptions.JedisException;
import com.zoho.instrument.Request;
import com.zoho.instrument.InstrumentManager;
import java.net.URI;
import com.zoho.jedis.v320.JedisShardInfo;
import com.zoho.jedis.v320.Jedis;

public class InstrumentedJedis extends Jedis
{
    private Jedis jedis;
    private String ip;
    private Integer connectionId;
    private String poolName;
    
    public InstrumentedJedis(final String host) {
        super(host);
    }
    
    public InstrumentedJedis(final String host, final int port) {
        super(host, port);
    }
    
    public InstrumentedJedis(final String host, final int port, final int timeout) {
        super(host, port, timeout);
    }
    
    public InstrumentedJedis(final JedisShardInfo shardInfo) {
        super(shardInfo);
    }
    
    public InstrumentedJedis(final URI uri) {
        super(uri);
    }
    
    public String getPoolName() {
        return this.poolName;
    }
    
    public void setPoolName(final String poolName) {
        this.poolName = poolName;
    }
    
    public void setWrappedJedis(final Jedis jedis, final String ip) {
        this.jedis = jedis;
        this.ip = ip;
        final Request req = InstrumentManager.getCurrentRequest();
        if (req != null) {
            this.connectionId = req.getConnectionId(10, ip);
        }
    }
    
    public void close() {
        try {
            this.jedis.close();
            final Request req = InstrumentManager.getCurrentRequest();
            if (req != null) {
                req.connectionReturned(this.connectionId);
            }
        }
        catch (final JedisException exc) {
            throw exc;
        }
    }
    
    public Long publish(final String arg0, final String arg1) {
        final RedisCall call = RedisCall.getInstance("publish");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.publish(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long move(final String arg0, final int arg1) {
        final RedisCall call = RedisCall.getInstance("move");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.move(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long unlink(final String arg0) {
        final RedisCall call = RedisCall.getInstance("unlink");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.unlink(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long unlink(final String... arg0) {
        final RedisCall call = RedisCall.getInstance("unlink");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.unlink(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public ScanResult<String> scan(final String arg0) {
        final RedisCall call = RedisCall.getInstance("scan");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final ScanResult<String> return_value = (ScanResult<String>)this.jedis.scan(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public ScanResult<String> scan(final String arg0, final ScanParams arg1) {
        final RedisCall call = RedisCall.getInstance("scan");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final ScanResult<String> return_value = (ScanResult<String>)this.jedis.scan(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String echo(final String arg0) {
        final RedisCall call = RedisCall.getInstance("echo");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.echo(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String getSet(final String arg0, final String arg1) {
        final RedisCall call = RedisCall.getInstance("getSet");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.getSet(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String get(final String arg0) {
        final RedisCall call = RedisCall.getInstance("get");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.get(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String type(final String arg0) {
        final RedisCall call = RedisCall.getInstance("type");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.type(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long append(final String arg0, final String arg1) {
        final RedisCall call = RedisCall.getInstance("append");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.append(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Set<String> keys(final String arg0) {
        final RedisCall call = RedisCall.getInstance("keys");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Set<String> return_value = this.jedis.keys(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String set(final String arg0, final String arg1, final SetParams arg2) {
        final RedisCall call = RedisCall.getInstance("set");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.set(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String set(final String arg0, final String arg1) {
        final RedisCall call = RedisCall.getInstance("set");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.set(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Boolean exists(final String arg0) {
        final RedisCall call = RedisCall.getInstance("exists");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Boolean return_value = this.jedis.exists(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long exists(final String... arg0) {
        final RedisCall call = RedisCall.getInstance("exists");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.exists(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String rename(final String arg0, final String arg1) {
        final RedisCall call = RedisCall.getInstance("rename");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.rename(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long sort(final String arg0, final String arg1) {
        final RedisCall call = RedisCall.getInstance("sort");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.sort(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long sort(final String arg0, final SortingParams arg1, final String arg2) {
        final RedisCall call = RedisCall.getInstance("sort");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.sort(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<String> sort(final String arg0) {
        final RedisCall call = RedisCall.getInstance("sort");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<String> return_value = this.jedis.sort(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<String> sort(final String arg0, final SortingParams arg1) {
        final RedisCall call = RedisCall.getInstance("sort");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<String> return_value = this.jedis.sort(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long expire(final String arg0, final int arg1) {
        final RedisCall call = RedisCall.getInstance("expire");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.expire(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long ttl(final String arg0) {
        final RedisCall call = RedisCall.getInstance("ttl");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.ttl(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String clusterInfo() {
        final RedisCall call = RedisCall.getInstance("clusterInfo");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.clusterInfo();
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String readonly() {
        final RedisCall call = RedisCall.getInstance("readonly");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.readonly();
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String ping(final String arg0) {
        final RedisCall call = RedisCall.getInstance("ping");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.ping(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public void setDataSource(final JedisPoolAbstract arg0) {
        final RedisCall call = RedisCall.getInstance("setDataSource");
        call.setClusterIp(this.ip);
        call.start();
        try {
            this.jedis.setDataSource(arg0);
            call.complete();
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String restore(final String arg0, final int arg1, final byte[] arg2) {
        final RedisCall call = RedisCall.getInstance("restore");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.restore(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public byte[] dump(final String arg0) {
        final RedisCall call = RedisCall.getInstance("dump");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final byte[] return_value = this.jedis.dump(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long incr(final String arg0) {
        final RedisCall call = RedisCall.getInstance("incr");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.incr(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Object sendCommand(final ProtocolCommand arg0, final String... arg1) {
        final RedisCall call = RedisCall.getInstance("sendCommand");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Object return_value = this.jedis.sendCommand(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public void subscribe(final JedisPubSub arg0, final String... arg1) {
        final RedisCall call = RedisCall.getInstance("subscribe");
        call.setClusterIp(this.ip);
        call.start();
        try {
            this.jedis.subscribe(arg0, arg1);
            call.complete();
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String migrate(final String arg0, final int arg1, final String arg2, final int arg3, final int arg4) {
        final RedisCall call = RedisCall.getInstance("migrate");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.migrate(arg0, arg1, arg2, arg3, arg4);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String migrate(final String arg0, final int arg1, final int arg2, final int arg3, final MigrateParams arg4, final String... arg5) {
        final RedisCall call = RedisCall.getInstance("migrate");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.migrate(arg0, arg1, arg2, arg3, arg4, arg5);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<String> configGet(final String arg0) {
        final RedisCall call = RedisCall.getInstance("configGet");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<String> return_value = this.jedis.configGet(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long incrBy(final String arg0, final long arg1) {
        final RedisCall call = RedisCall.getInstance("incrBy");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.incrBy(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String randomKey() {
        final RedisCall call = RedisCall.getInstance("randomKey");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.randomKey();
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long renamenx(final String arg0, final String arg1) {
        final RedisCall call = RedisCall.getInstance("renamenx");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.renamenx(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long expireAt(final String arg0, final long arg1) {
        final RedisCall call = RedisCall.getInstance("expireAt");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.expireAt(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long touch(final String... arg0) {
        final RedisCall call = RedisCall.getInstance("touch");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.touch(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long touch(final String arg0) {
        final RedisCall call = RedisCall.getInstance("touch");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.touch(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<String> mget(final String... arg0) {
        final RedisCall call = RedisCall.getInstance("mget");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<String> return_value = this.jedis.mget(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String setex(final String arg0, final int arg1, final String arg2) {
        final RedisCall call = RedisCall.getInstance("setex");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.setex(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String mset(final String... arg0) {
        final RedisCall call = RedisCall.getInstance("mset");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.mset(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long msetnx(final String... arg0) {
        final RedisCall call = RedisCall.getInstance("msetnx");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.msetnx(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long decrBy(final String arg0, final long arg1) {
        final RedisCall call = RedisCall.getInstance("decrBy");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.decrBy(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long decr(final String arg0) {
        final RedisCall call = RedisCall.getInstance("decr");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.decr(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Double incrByFloat(final String arg0, final double arg1) {
        final RedisCall call = RedisCall.getInstance("incrByFloat");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Double return_value = this.jedis.incrByFloat(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String substr(final String arg0, final int arg1, final int arg2) {
        final RedisCall call = RedisCall.getInstance("substr");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.substr(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long hset(final String arg0, final String arg1, final String arg2) {
        final RedisCall call = RedisCall.getInstance("hset");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.hset(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long hset(final String arg0, final Map<String, String> arg1) {
        final RedisCall call = RedisCall.getInstance("hset");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.hset(arg0, (Map)arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String hget(final String arg0, final String arg1) {
        final RedisCall call = RedisCall.getInstance("hget");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.hget(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long hsetnx(final String arg0, final String arg1, final String arg2) {
        final RedisCall call = RedisCall.getInstance("hsetnx");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.hsetnx(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String hmset(final String arg0, final Map<String, String> arg1) {
        final RedisCall call = RedisCall.getInstance("hmset");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.hmset(arg0, (Map)arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<String> hmget(final String arg0, final String... arg1) {
        final RedisCall call = RedisCall.getInstance("hmget");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<String> return_value = this.jedis.hmget(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long hincrBy(final String arg0, final String arg1, final long arg2) {
        final RedisCall call = RedisCall.getInstance("hincrBy");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.hincrBy(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Double hincrByFloat(final String arg0, final String arg1, final double arg2) {
        final RedisCall call = RedisCall.getInstance("hincrByFloat");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Double return_value = this.jedis.hincrByFloat(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Boolean hexists(final String arg0, final String arg1) {
        final RedisCall call = RedisCall.getInstance("hexists");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Boolean return_value = this.jedis.hexists(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long hdel(final String arg0, final String... arg1) {
        final RedisCall call = RedisCall.getInstance("hdel");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.hdel(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long hlen(final String arg0) {
        final RedisCall call = RedisCall.getInstance("hlen");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.hlen(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Set<String> hkeys(final String arg0) {
        final RedisCall call = RedisCall.getInstance("hkeys");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Set<String> return_value = this.jedis.hkeys(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<String> hvals(final String arg0) {
        final RedisCall call = RedisCall.getInstance("hvals");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<String> return_value = this.jedis.hvals(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Map<String, String> hgetAll(final String arg0) {
        final RedisCall call = RedisCall.getInstance("hgetAll");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Map<String, String> return_value = this.jedis.hgetAll(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long rpush(final String arg0, final String... arg1) {
        final RedisCall call = RedisCall.getInstance("rpush");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.rpush(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long lpush(final String arg0, final String... arg1) {
        final RedisCall call = RedisCall.getInstance("lpush");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.lpush(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long llen(final String arg0) {
        final RedisCall call = RedisCall.getInstance("llen");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.llen(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<String> lrange(final String arg0, final long arg1, final long arg2) {
        final RedisCall call = RedisCall.getInstance("lrange");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<String> return_value = this.jedis.lrange(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String ltrim(final String arg0, final long arg1, final long arg2) {
        final RedisCall call = RedisCall.getInstance("ltrim");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.ltrim(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String lindex(final String arg0, final long arg1) {
        final RedisCall call = RedisCall.getInstance("lindex");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.lindex(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String lset(final String arg0, final long arg1, final String arg2) {
        final RedisCall call = RedisCall.getInstance("lset");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.lset(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long lrem(final String arg0, final long arg1, final String arg2) {
        final RedisCall call = RedisCall.getInstance("lrem");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.lrem(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String lpop(final String arg0) {
        final RedisCall call = RedisCall.getInstance("lpop");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.lpop(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String rpop(final String arg0) {
        final RedisCall call = RedisCall.getInstance("rpop");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.rpop(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String rpoplpush(final String arg0, final String arg1) {
        final RedisCall call = RedisCall.getInstance("rpoplpush");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.rpoplpush(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long sadd(final String arg0, final String... arg1) {
        final RedisCall call = RedisCall.getInstance("sadd");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.sadd(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Set<String> smembers(final String arg0) {
        final RedisCall call = RedisCall.getInstance("smembers");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Set<String> return_value = this.jedis.smembers(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long srem(final String arg0, final String... arg1) {
        final RedisCall call = RedisCall.getInstance("srem");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.srem(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String spop(final String arg0) {
        final RedisCall call = RedisCall.getInstance("spop");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.spop(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Set<String> spop(final String arg0, final long arg1) {
        final RedisCall call = RedisCall.getInstance("spop");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Set<String> return_value = this.jedis.spop(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long smove(final String arg0, final String arg1, final String arg2) {
        final RedisCall call = RedisCall.getInstance("smove");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.smove(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long scard(final String arg0) {
        final RedisCall call = RedisCall.getInstance("scard");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.scard(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Boolean sismember(final String arg0, final String arg1) {
        final RedisCall call = RedisCall.getInstance("sismember");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Boolean return_value = this.jedis.sismember(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Set<String> sinter(final String... arg0) {
        final RedisCall call = RedisCall.getInstance("sinter");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Set<String> return_value = this.jedis.sinter(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long sinterstore(final String arg0, final String... arg1) {
        final RedisCall call = RedisCall.getInstance("sinterstore");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.sinterstore(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Set<String> sunion(final String... arg0) {
        final RedisCall call = RedisCall.getInstance("sunion");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Set<String> return_value = this.jedis.sunion(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long sunionstore(final String arg0, final String... arg1) {
        final RedisCall call = RedisCall.getInstance("sunionstore");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.sunionstore(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Set<String> sdiff(final String... arg0) {
        final RedisCall call = RedisCall.getInstance("sdiff");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Set<String> return_value = this.jedis.sdiff(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long sdiffstore(final String arg0, final String... arg1) {
        final RedisCall call = RedisCall.getInstance("sdiffstore");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.sdiffstore(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<String> srandmember(final String arg0, final int arg1) {
        final RedisCall call = RedisCall.getInstance("srandmember");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<String> return_value = this.jedis.srandmember(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String srandmember(final String arg0) {
        final RedisCall call = RedisCall.getInstance("srandmember");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.srandmember(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long zadd(final String arg0, final Map<String, Double> arg1) {
        final RedisCall call = RedisCall.getInstance("zadd");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.zadd(arg0, (Map)arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long zadd(final String arg0, final double arg1, final String arg2, final ZAddParams arg3) {
        final RedisCall call = RedisCall.getInstance("zadd");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.zadd(arg0, arg1, arg2, arg3);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long zadd(final String arg0, final double arg1, final String arg2) {
        final RedisCall call = RedisCall.getInstance("zadd");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.zadd(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long zadd(final String arg0, final Map<String, Double> arg1, final ZAddParams arg2) {
        final RedisCall call = RedisCall.getInstance("zadd");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.zadd(arg0, (Map)arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Set<String> zrange(final String arg0, final long arg1, final long arg2) {
        final RedisCall call = RedisCall.getInstance("zrange");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Set<String> return_value = this.jedis.zrange(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long zrem(final String arg0, final String... arg1) {
        final RedisCall call = RedisCall.getInstance("zrem");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.zrem(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Double zincrby(final String arg0, final double arg1, final String arg2) {
        final RedisCall call = RedisCall.getInstance("zincrby");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Double return_value = this.jedis.zincrby(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Double zincrby(final String arg0, final double arg1, final String arg2, final ZIncrByParams arg3) {
        final RedisCall call = RedisCall.getInstance("zincrby");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Double return_value = this.jedis.zincrby(arg0, arg1, arg2, arg3);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long zrank(final String arg0, final String arg1) {
        final RedisCall call = RedisCall.getInstance("zrank");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.zrank(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long zrevrank(final String arg0, final String arg1) {
        final RedisCall call = RedisCall.getInstance("zrevrank");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.zrevrank(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Set<String> zrevrange(final String arg0, final long arg1, final long arg2) {
        final RedisCall call = RedisCall.getInstance("zrevrange");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Set<String> return_value = this.jedis.zrevrange(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Set<Tuple> zrangeWithScores(final String arg0, final long arg1, final long arg2) {
        final RedisCall call = RedisCall.getInstance("zrangeWithScores");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Set<Tuple> return_value = this.jedis.zrangeWithScores(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Set<Tuple> zrevrangeWithScores(final String arg0, final long arg1, final long arg2) {
        final RedisCall call = RedisCall.getInstance("zrevrangeWithScores");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Set<Tuple> return_value = this.jedis.zrevrangeWithScores(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long zcard(final String arg0) {
        final RedisCall call = RedisCall.getInstance("zcard");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.zcard(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Double zscore(final String arg0, final String arg1) {
        final RedisCall call = RedisCall.getInstance("zscore");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Double return_value = this.jedis.zscore(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Set<Tuple> zpopmax(final String arg0, final int arg1) {
        final RedisCall call = RedisCall.getInstance("zpopmax");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Set<Tuple> return_value = this.jedis.zpopmax(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Tuple zpopmax(final String arg0) {
        final RedisCall call = RedisCall.getInstance("zpopmax");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Tuple return_value = this.jedis.zpopmax(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Tuple zpopmin(final String arg0) {
        final RedisCall call = RedisCall.getInstance("zpopmin");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Tuple return_value = this.jedis.zpopmin(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Set<Tuple> zpopmin(final String arg0, final int arg1) {
        final RedisCall call = RedisCall.getInstance("zpopmin");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Set<Tuple> return_value = this.jedis.zpopmin(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String watch(final String... arg0) {
        final RedisCall call = RedisCall.getInstance("watch");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.watch(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<String> blpop(final int arg0, final String arg1) {
        final RedisCall call = RedisCall.getInstance("blpop");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<String> return_value = this.jedis.blpop(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<String> blpop(final String... arg0) {
        final RedisCall call = RedisCall.getInstance("blpop");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<String> return_value = this.jedis.blpop(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<String> blpop(final int arg0, final String... arg1) {
        final RedisCall call = RedisCall.getInstance("blpop");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<String> return_value = this.jedis.blpop(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<String> brpop(final int arg0, final String arg1) {
        final RedisCall call = RedisCall.getInstance("brpop");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<String> return_value = this.jedis.brpop(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<String> brpop(final int arg0, final String... arg1) {
        final RedisCall call = RedisCall.getInstance("brpop");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<String> return_value = this.jedis.brpop(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<String> brpop(final String... arg0) {
        final RedisCall call = RedisCall.getInstance("brpop");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<String> return_value = this.jedis.brpop(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long zcount(final String arg0, final String arg1, final String arg2) {
        final RedisCall call = RedisCall.getInstance("zcount");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.zcount(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long zcount(final String arg0, final double arg1, final double arg2) {
        final RedisCall call = RedisCall.getInstance("zcount");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.zcount(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Set<String> zrangeByScore(final String arg0, final String arg1, final String arg2, final int arg3, final int arg4) {
        final RedisCall call = RedisCall.getInstance("zrangeByScore");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Set<String> return_value = this.jedis.zrangeByScore(arg0, arg1, arg2, arg3, arg4);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Set<String> zrangeByScore(final String arg0, final double arg1, final double arg2, final int arg3, final int arg4) {
        final RedisCall call = RedisCall.getInstance("zrangeByScore");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Set<String> return_value = this.jedis.zrangeByScore(arg0, arg1, arg2, arg3, arg4);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Set<String> zrangeByScore(final String arg0, final double arg1, final double arg2) {
        final RedisCall call = RedisCall.getInstance("zrangeByScore");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Set<String> return_value = this.jedis.zrangeByScore(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Set<String> zrangeByScore(final String arg0, final String arg1, final String arg2) {
        final RedisCall call = RedisCall.getInstance("zrangeByScore");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Set<String> return_value = this.jedis.zrangeByScore(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Set<Tuple> zrangeByScoreWithScores(final String arg0, final String arg1, final String arg2, final int arg3, final int arg4) {
        final RedisCall call = RedisCall.getInstance("zrangeByScoreWithScores");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Set<Tuple> return_value = this.jedis.zrangeByScoreWithScores(arg0, arg1, arg2, arg3, arg4);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Set<Tuple> zrangeByScoreWithScores(final String arg0, final String arg1, final String arg2) {
        final RedisCall call = RedisCall.getInstance("zrangeByScoreWithScores");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Set<Tuple> return_value = this.jedis.zrangeByScoreWithScores(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Set<Tuple> zrangeByScoreWithScores(final String arg0, final double arg1, final double arg2, final int arg3, final int arg4) {
        final RedisCall call = RedisCall.getInstance("zrangeByScoreWithScores");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Set<Tuple> return_value = this.jedis.zrangeByScoreWithScores(arg0, arg1, arg2, arg3, arg4);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Set<Tuple> zrangeByScoreWithScores(final String arg0, final double arg1, final double arg2) {
        final RedisCall call = RedisCall.getInstance("zrangeByScoreWithScores");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Set<Tuple> return_value = this.jedis.zrangeByScoreWithScores(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Set<String> zrevrangeByScore(final String arg0, final double arg1, final double arg2) {
        final RedisCall call = RedisCall.getInstance("zrevrangeByScore");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Set<String> return_value = this.jedis.zrevrangeByScore(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Set<String> zrevrangeByScore(final String arg0, final String arg1, final String arg2) {
        final RedisCall call = RedisCall.getInstance("zrevrangeByScore");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Set<String> return_value = this.jedis.zrevrangeByScore(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Set<String> zrevrangeByScore(final String arg0, final double arg1, final double arg2, final int arg3, final int arg4) {
        final RedisCall call = RedisCall.getInstance("zrevrangeByScore");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Set<String> return_value = this.jedis.zrevrangeByScore(arg0, arg1, arg2, arg3, arg4);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Set<String> zrevrangeByScore(final String arg0, final String arg1, final String arg2, final int arg3, final int arg4) {
        final RedisCall call = RedisCall.getInstance("zrevrangeByScore");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Set<String> return_value = this.jedis.zrevrangeByScore(arg0, arg1, arg2, arg3, arg4);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Set<Tuple> zrevrangeByScoreWithScores(final String arg0, final String arg1, final String arg2, final int arg3, final int arg4) {
        final RedisCall call = RedisCall.getInstance("zrevrangeByScoreWithScores");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Set<Tuple> return_value = this.jedis.zrevrangeByScoreWithScores(arg0, arg1, arg2, arg3, arg4);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Set<Tuple> zrevrangeByScoreWithScores(final String arg0, final double arg1, final double arg2, final int arg3, final int arg4) {
        final RedisCall call = RedisCall.getInstance("zrevrangeByScoreWithScores");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Set<Tuple> return_value = this.jedis.zrevrangeByScoreWithScores(arg0, arg1, arg2, arg3, arg4);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Set<Tuple> zrevrangeByScoreWithScores(final String arg0, final double arg1, final double arg2) {
        final RedisCall call = RedisCall.getInstance("zrevrangeByScoreWithScores");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Set<Tuple> return_value = this.jedis.zrevrangeByScoreWithScores(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Set<Tuple> zrevrangeByScoreWithScores(final String arg0, final String arg1, final String arg2) {
        final RedisCall call = RedisCall.getInstance("zrevrangeByScoreWithScores");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Set<Tuple> return_value = this.jedis.zrevrangeByScoreWithScores(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long zremrangeByRank(final String arg0, final long arg1, final long arg2) {
        final RedisCall call = RedisCall.getInstance("zremrangeByRank");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.zremrangeByRank(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long zremrangeByScore(final String arg0, final double arg1, final double arg2) {
        final RedisCall call = RedisCall.getInstance("zremrangeByScore");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.zremrangeByScore(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long zremrangeByScore(final String arg0, final String arg1, final String arg2) {
        final RedisCall call = RedisCall.getInstance("zremrangeByScore");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.zremrangeByScore(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long zunionstore(final String arg0, final ZParams arg1, final String... arg2) {
        final RedisCall call = RedisCall.getInstance("zunionstore");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.zunionstore(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long zunionstore(final String arg0, final String... arg1) {
        final RedisCall call = RedisCall.getInstance("zunionstore");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.zunionstore(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long zinterstore(final String arg0, final String... arg1) {
        final RedisCall call = RedisCall.getInstance("zinterstore");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.zinterstore(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long zinterstore(final String arg0, final ZParams arg1, final String... arg2) {
        final RedisCall call = RedisCall.getInstance("zinterstore");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.zinterstore(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long zlexcount(final String arg0, final String arg1, final String arg2) {
        final RedisCall call = RedisCall.getInstance("zlexcount");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.zlexcount(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Set<String> zrangeByLex(final String arg0, final String arg1, final String arg2, final int arg3, final int arg4) {
        final RedisCall call = RedisCall.getInstance("zrangeByLex");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Set<String> return_value = this.jedis.zrangeByLex(arg0, arg1, arg2, arg3, arg4);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Set<String> zrangeByLex(final String arg0, final String arg1, final String arg2) {
        final RedisCall call = RedisCall.getInstance("zrangeByLex");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Set<String> return_value = this.jedis.zrangeByLex(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Set<String> zrevrangeByLex(final String arg0, final String arg1, final String arg2) {
        final RedisCall call = RedisCall.getInstance("zrevrangeByLex");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Set<String> return_value = this.jedis.zrevrangeByLex(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Set<String> zrevrangeByLex(final String arg0, final String arg1, final String arg2, final int arg3, final int arg4) {
        final RedisCall call = RedisCall.getInstance("zrevrangeByLex");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Set<String> return_value = this.jedis.zrevrangeByLex(arg0, arg1, arg2, arg3, arg4);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long zremrangeByLex(final String arg0, final String arg1, final String arg2) {
        final RedisCall call = RedisCall.getInstance("zremrangeByLex");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.zremrangeByLex(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long strlen(final String arg0) {
        final RedisCall call = RedisCall.getInstance("strlen");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.strlen(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long lpushx(final String arg0, final String... arg1) {
        final RedisCall call = RedisCall.getInstance("lpushx");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.lpushx(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long persist(final String arg0) {
        final RedisCall call = RedisCall.getInstance("persist");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.persist(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long rpushx(final String arg0, final String... arg1) {
        final RedisCall call = RedisCall.getInstance("rpushx");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.rpushx(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long linsert(final String arg0, final ListPosition arg1, final String arg2, final String arg3) {
        final RedisCall call = RedisCall.getInstance("linsert");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.linsert(arg0, arg1, arg2, arg3);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String brpoplpush(final String arg0, final String arg1, final int arg2) {
        final RedisCall call = RedisCall.getInstance("brpoplpush");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.brpoplpush(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Boolean setbit(final String arg0, final long arg1, final String arg2) {
        final RedisCall call = RedisCall.getInstance("setbit");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Boolean return_value = this.jedis.setbit(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Boolean setbit(final String arg0, final long arg1, final boolean arg2) {
        final RedisCall call = RedisCall.getInstance("setbit");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Boolean return_value = this.jedis.setbit(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Boolean getbit(final String arg0, final long arg1) {
        final RedisCall call = RedisCall.getInstance("getbit");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Boolean return_value = this.jedis.getbit(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long setrange(final String arg0, final long arg1, final String arg2) {
        final RedisCall call = RedisCall.getInstance("setrange");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.setrange(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String getrange(final String arg0, final long arg1, final long arg2) {
        final RedisCall call = RedisCall.getInstance("getrange");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.getrange(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long bitpos(final String arg0, final boolean arg1, final BitPosParams arg2) {
        final RedisCall call = RedisCall.getInstance("bitpos");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.bitpos(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long bitpos(final String arg0, final boolean arg1) {
        final RedisCall call = RedisCall.getInstance("bitpos");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.bitpos(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String configSet(final String arg0, final String arg1) {
        final RedisCall call = RedisCall.getInstance("configSet");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.configSet(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public void psubscribe(final JedisPubSub arg0, final String... arg1) {
        final RedisCall call = RedisCall.getInstance("psubscribe");
        call.setClusterIp(this.ip);
        call.start();
        try {
            this.jedis.psubscribe(arg0, arg1);
            call.complete();
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Object eval(final String arg0, final int arg1, final String... arg2) {
        final RedisCall call = RedisCall.getInstance("eval");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Object return_value = this.jedis.eval(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Object eval(final String arg0, final List<String> arg1, final List<String> arg2) {
        final RedisCall call = RedisCall.getInstance("eval");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Object return_value = this.jedis.eval(arg0, (List)arg1, (List)arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Object eval(final String arg0) {
        final RedisCall call = RedisCall.getInstance("eval");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Object return_value = this.jedis.eval(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Object evalsha(final String arg0) {
        final RedisCall call = RedisCall.getInstance("evalsha");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Object return_value = this.jedis.evalsha(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Object evalsha(final String arg0, final List<String> arg1, final List<String> arg2) {
        final RedisCall call = RedisCall.getInstance("evalsha");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Object return_value = this.jedis.evalsha(arg0, (List)arg1, (List)arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Object evalsha(final String arg0, final int arg1, final String... arg2) {
        final RedisCall call = RedisCall.getInstance("evalsha");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Object return_value = this.jedis.evalsha(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<Boolean> scriptExists(final String... arg0) {
        final RedisCall call = RedisCall.getInstance("scriptExists");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<Boolean> return_value = this.jedis.scriptExists(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Boolean scriptExists(final String arg0) {
        final RedisCall call = RedisCall.getInstance("scriptExists");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Boolean return_value = this.jedis.scriptExists(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String scriptLoad(final String arg0) {
        final RedisCall call = RedisCall.getInstance("scriptLoad");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.scriptLoad(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<Slowlog> slowlogGet() {
        final RedisCall call = RedisCall.getInstance("slowlogGet");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<Slowlog> return_value = this.jedis.slowlogGet();
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<Slowlog> slowlogGet(final long arg0) {
        final RedisCall call = RedisCall.getInstance("slowlogGet");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<Slowlog> return_value = this.jedis.slowlogGet(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long objectRefcount(final String arg0) {
        final RedisCall call = RedisCall.getInstance("objectRefcount");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.objectRefcount(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String objectEncoding(final String arg0) {
        final RedisCall call = RedisCall.getInstance("objectEncoding");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.objectEncoding(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long objectIdletime(final String arg0) {
        final RedisCall call = RedisCall.getInstance("objectIdletime");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.objectIdletime(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long bitcount(final String arg0) {
        final RedisCall call = RedisCall.getInstance("bitcount");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.bitcount(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long bitcount(final String arg0, final long arg1, final long arg2) {
        final RedisCall call = RedisCall.getInstance("bitcount");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.bitcount(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long bitop(final BitOP arg0, final String arg1, final String... arg2) {
        final RedisCall call = RedisCall.getInstance("bitop");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.bitop(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<Map<String, String>> sentinelMasters() {
        final RedisCall call = RedisCall.getInstance("sentinelMasters");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<Map<String, String>> return_value = this.jedis.sentinelMasters();
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<String> sentinelGetMasterAddrByName(final String arg0) {
        final RedisCall call = RedisCall.getInstance("sentinelGetMasterAddrByName");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<String> return_value = this.jedis.sentinelGetMasterAddrByName(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long sentinelReset(final String arg0) {
        final RedisCall call = RedisCall.getInstance("sentinelReset");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.sentinelReset(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<Map<String, String>> sentinelSlaves(final String arg0) {
        final RedisCall call = RedisCall.getInstance("sentinelSlaves");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<Map<String, String>> return_value = this.jedis.sentinelSlaves(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String sentinelFailover(final String arg0) {
        final RedisCall call = RedisCall.getInstance("sentinelFailover");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.sentinelFailover(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String sentinelMonitor(final String arg0, final String arg1, final int arg2, final int arg3) {
        final RedisCall call = RedisCall.getInstance("sentinelMonitor");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.sentinelMonitor(arg0, arg1, arg2, arg3);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String sentinelRemove(final String arg0) {
        final RedisCall call = RedisCall.getInstance("sentinelRemove");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.sentinelRemove(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String sentinelSet(final String arg0, final Map<String, String> arg1) {
        final RedisCall call = RedisCall.getInstance("sentinelSet");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.sentinelSet(arg0, (Map)arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String restoreReplace(final String arg0, final int arg1, final byte[] arg2) {
        final RedisCall call = RedisCall.getInstance("restoreReplace");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.restoreReplace(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long pexpire(final String arg0, final long arg1) {
        final RedisCall call = RedisCall.getInstance("pexpire");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.pexpire(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long pexpireAt(final String arg0, final long arg1) {
        final RedisCall call = RedisCall.getInstance("pexpireAt");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.pexpireAt(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long pttl(final String arg0) {
        final RedisCall call = RedisCall.getInstance("pttl");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.pttl(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String psetex(final String arg0, final long arg1, final String arg2) {
        final RedisCall call = RedisCall.getInstance("psetex");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.psetex(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String clientKill(final String arg0) {
        final RedisCall call = RedisCall.getInstance("clientKill");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.clientKill(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String clientGetname() {
        final RedisCall call = RedisCall.getInstance("clientGetname");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.clientGetname();
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String clientList() {
        final RedisCall call = RedisCall.getInstance("clientList");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.clientList();
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String clientSetname(final String arg0) {
        final RedisCall call = RedisCall.getInstance("clientSetname");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.clientSetname(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public ScanResult<Map.Entry<String, String>> hscan(final String arg0, final String arg1) {
        final RedisCall call = RedisCall.getInstance("hscan");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final ScanResult<Map.Entry<String, String>> return_value = (ScanResult<Map.Entry<String, String>>)this.jedis.hscan(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public ScanResult<Map.Entry<String, String>> hscan(final String arg0, final String arg1, final ScanParams arg2) {
        final RedisCall call = RedisCall.getInstance("hscan");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final ScanResult<Map.Entry<String, String>> return_value = (ScanResult<Map.Entry<String, String>>)this.jedis.hscan(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public ScanResult<String> sscan(final String arg0, final String arg1, final ScanParams arg2) {
        final RedisCall call = RedisCall.getInstance("sscan");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final ScanResult<String> return_value = (ScanResult<String>)this.jedis.sscan(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public ScanResult<String> sscan(final String arg0, final String arg1) {
        final RedisCall call = RedisCall.getInstance("sscan");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final ScanResult<String> return_value = (ScanResult<String>)this.jedis.sscan(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public ScanResult<Tuple> zscan(final String arg0, final String arg1) {
        final RedisCall call = RedisCall.getInstance("zscan");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final ScanResult<Tuple> return_value = (ScanResult<Tuple>)this.jedis.zscan(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public ScanResult<Tuple> zscan(final String arg0, final String arg1, final ScanParams arg2) {
        final RedisCall call = RedisCall.getInstance("zscan");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final ScanResult<Tuple> return_value = (ScanResult<Tuple>)this.jedis.zscan(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String clusterNodes() {
        final RedisCall call = RedisCall.getInstance("clusterNodes");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.clusterNodes();
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String clusterMeet(final String arg0, final int arg1) {
        final RedisCall call = RedisCall.getInstance("clusterMeet");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.clusterMeet(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String clusterReset(final ClusterReset arg0) {
        final RedisCall call = RedisCall.getInstance("clusterReset");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.clusterReset(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String clusterAddSlots(final int... arg0) {
        final RedisCall call = RedisCall.getInstance("clusterAddSlots");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.clusterAddSlots(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String clusterDelSlots(final int... arg0) {
        final RedisCall call = RedisCall.getInstance("clusterDelSlots");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.clusterDelSlots(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<String> clusterGetKeysInSlot(final int arg0, final int arg1) {
        final RedisCall call = RedisCall.getInstance("clusterGetKeysInSlot");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<String> return_value = this.jedis.clusterGetKeysInSlot(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String clusterSetSlotNode(final int arg0, final String arg1) {
        final RedisCall call = RedisCall.getInstance("clusterSetSlotNode");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.clusterSetSlotNode(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String clusterSetSlotMigrating(final int arg0, final String arg1) {
        final RedisCall call = RedisCall.getInstance("clusterSetSlotMigrating");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.clusterSetSlotMigrating(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String clusterSetSlotImporting(final int arg0, final String arg1) {
        final RedisCall call = RedisCall.getInstance("clusterSetSlotImporting");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.clusterSetSlotImporting(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String clusterSetSlotStable(final int arg0) {
        final RedisCall call = RedisCall.getInstance("clusterSetSlotStable");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.clusterSetSlotStable(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String clusterForget(final String arg0) {
        final RedisCall call = RedisCall.getInstance("clusterForget");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.clusterForget(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long clusterKeySlot(final String arg0) {
        final RedisCall call = RedisCall.getInstance("clusterKeySlot");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.clusterKeySlot(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long setnx(final String arg0, final String arg1) {
        final RedisCall call = RedisCall.getInstance("setnx");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.setnx(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long del(final String... arg0) {
        final RedisCall call = RedisCall.getInstance("del");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.del(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long del(final String arg0) {
        final RedisCall call = RedisCall.getInstance("del");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.del(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String clusterFlushSlots() {
        final RedisCall call = RedisCall.getInstance("clusterFlushSlots");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.clusterFlushSlots();
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long clusterCountKeysInSlot(final int arg0) {
        final RedisCall call = RedisCall.getInstance("clusterCountKeysInSlot");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.clusterCountKeysInSlot(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String clusterSaveConfig() {
        final RedisCall call = RedisCall.getInstance("clusterSaveConfig");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.clusterSaveConfig();
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String clusterReplicate(final String arg0) {
        final RedisCall call = RedisCall.getInstance("clusterReplicate");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.clusterReplicate(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<String> clusterSlaves(final String arg0) {
        final RedisCall call = RedisCall.getInstance("clusterSlaves");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<String> return_value = this.jedis.clusterSlaves(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String clusterFailover() {
        final RedisCall call = RedisCall.getInstance("clusterFailover");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.clusterFailover();
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<Object> clusterSlots() {
        final RedisCall call = RedisCall.getInstance("clusterSlots");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<Object> return_value = this.jedis.clusterSlots();
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String asking() {
        final RedisCall call = RedisCall.getInstance("asking");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.asking();
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<String> pubsubChannels(final String arg0) {
        final RedisCall call = RedisCall.getInstance("pubsubChannels");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<String> return_value = this.jedis.pubsubChannels(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long pubsubNumPat() {
        final RedisCall call = RedisCall.getInstance("pubsubNumPat");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.pubsubNumPat();
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Map<String, String> pubsubNumSub(final String... arg0) {
        final RedisCall call = RedisCall.getInstance("pubsubNumSub");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Map<String, String> return_value = this.jedis.pubsubNumSub(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long pfadd(final String arg0, final String... arg1) {
        final RedisCall call = RedisCall.getInstance("pfadd");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.pfadd(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public long pfcount(final String arg0) {
        final RedisCall call = RedisCall.getInstance("pfcount");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final long return_value = this.jedis.pfcount(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public long pfcount(final String... arg0) {
        final RedisCall call = RedisCall.getInstance("pfcount");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final long return_value = this.jedis.pfcount(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String pfmerge(final String arg0, final String... arg1) {
        final RedisCall call = RedisCall.getInstance("pfmerge");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.pfmerge(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long geoadd(final String arg0, final Map<String, GeoCoordinate> arg1) {
        final RedisCall call = RedisCall.getInstance("geoadd");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.geoadd(arg0, (Map)arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long geoadd(final String arg0, final double arg1, final double arg2, final String arg3) {
        final RedisCall call = RedisCall.getInstance("geoadd");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.geoadd(arg0, arg1, arg2, arg3);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Double geodist(final String arg0, final String arg1, final String arg2, final GeoUnit arg3) {
        final RedisCall call = RedisCall.getInstance("geodist");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Double return_value = this.jedis.geodist(arg0, arg1, arg2, arg3);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Double geodist(final String arg0, final String arg1, final String arg2) {
        final RedisCall call = RedisCall.getInstance("geodist");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Double return_value = this.jedis.geodist(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<String> geohash(final String arg0, final String... arg1) {
        final RedisCall call = RedisCall.getInstance("geohash");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<String> return_value = this.jedis.geohash(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<GeoCoordinate> geopos(final String arg0, final String... arg1) {
        final RedisCall call = RedisCall.getInstance("geopos");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<GeoCoordinate> return_value = this.jedis.geopos(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<GeoRadiusResponse> georadius(final String arg0, final double arg1, final double arg2, final double arg3, final GeoUnit arg4) {
        final RedisCall call = RedisCall.getInstance("georadius");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<GeoRadiusResponse> return_value = this.jedis.georadius(arg0, arg1, arg2, arg3, arg4);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<GeoRadiusResponse> georadius(final String arg0, final double arg1, final double arg2, final double arg3, final GeoUnit arg4, final GeoRadiusParam arg5) {
        final RedisCall call = RedisCall.getInstance("georadius");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<GeoRadiusResponse> return_value = this.jedis.georadius(arg0, arg1, arg2, arg3, arg4, arg5);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<GeoRadiusResponse> georadiusReadonly(final String arg0, final double arg1, final double arg2, final double arg3, final GeoUnit arg4) {
        final RedisCall call = RedisCall.getInstance("georadiusReadonly");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<GeoRadiusResponse> return_value = this.jedis.georadiusReadonly(arg0, arg1, arg2, arg3, arg4);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<GeoRadiusResponse> georadiusReadonly(final String arg0, final double arg1, final double arg2, final double arg3, final GeoUnit arg4, final GeoRadiusParam arg5) {
        final RedisCall call = RedisCall.getInstance("georadiusReadonly");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<GeoRadiusResponse> return_value = this.jedis.georadiusReadonly(arg0, arg1, arg2, arg3, arg4, arg5);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<GeoRadiusResponse> georadiusByMember(final String arg0, final String arg1, final double arg2, final GeoUnit arg3, final GeoRadiusParam arg4) {
        final RedisCall call = RedisCall.getInstance("georadiusByMember");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<GeoRadiusResponse> return_value = this.jedis.georadiusByMember(arg0, arg1, arg2, arg3, arg4);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<GeoRadiusResponse> georadiusByMember(final String arg0, final String arg1, final double arg2, final GeoUnit arg3) {
        final RedisCall call = RedisCall.getInstance("georadiusByMember");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<GeoRadiusResponse> return_value = this.jedis.georadiusByMember(arg0, arg1, arg2, arg3);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<GeoRadiusResponse> georadiusByMemberReadonly(final String arg0, final String arg1, final double arg2, final GeoUnit arg3, final GeoRadiusParam arg4) {
        final RedisCall call = RedisCall.getInstance("georadiusByMemberReadonly");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<GeoRadiusResponse> return_value = this.jedis.georadiusByMemberReadonly(arg0, arg1, arg2, arg3, arg4);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<GeoRadiusResponse> georadiusByMemberReadonly(final String arg0, final String arg1, final double arg2, final GeoUnit arg3) {
        final RedisCall call = RedisCall.getInstance("georadiusByMemberReadonly");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<GeoRadiusResponse> return_value = this.jedis.georadiusByMemberReadonly(arg0, arg1, arg2, arg3);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String moduleLoad(final String arg0) {
        final RedisCall call = RedisCall.getInstance("moduleLoad");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.moduleLoad(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String moduleUnload(final String arg0) {
        final RedisCall call = RedisCall.getInstance("moduleUnload");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.moduleUnload(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<Module> moduleList() {
        final RedisCall call = RedisCall.getInstance("moduleList");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<Module> return_value = this.jedis.moduleList();
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<Long> bitfield(final String arg0, final String... arg1) {
        final RedisCall call = RedisCall.getInstance("bitfield");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<Long> return_value = this.jedis.bitfield(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long hstrlen(final String arg0, final String arg1) {
        final RedisCall call = RedisCall.getInstance("hstrlen");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.hstrlen(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String memoryDoctor() {
        final RedisCall call = RedisCall.getInstance("memoryDoctor");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.memoryDoctor();
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public StreamEntryID xadd(final String arg0, final StreamEntryID arg1, final Map<String, String> arg2, final long arg3, final boolean arg4) {
        final RedisCall call = RedisCall.getInstance("xadd");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final StreamEntryID return_value = this.jedis.xadd(arg0, arg1, (Map)arg2, arg3, arg4);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public StreamEntryID xadd(final String arg0, final StreamEntryID arg1, final Map<String, String> arg2) {
        final RedisCall call = RedisCall.getInstance("xadd");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final StreamEntryID return_value = this.jedis.xadd(arg0, arg1, (Map)arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long xlen(final String arg0) {
        final RedisCall call = RedisCall.getInstance("xlen");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.xlen(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<StreamEntry> xrange(final String arg0, final StreamEntryID arg1, final StreamEntryID arg2, final int arg3) {
        final RedisCall call = RedisCall.getInstance("xrange");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<StreamEntry> return_value = this.jedis.xrange(arg0, arg1, arg2, arg3);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<StreamEntry> xrevrange(final String arg0, final StreamEntryID arg1, final StreamEntryID arg2, final int arg3) {
        final RedisCall call = RedisCall.getInstance("xrevrange");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<StreamEntry> return_value = this.jedis.xrevrange(arg0, arg1, arg2, arg3);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<Map.Entry<String, List<StreamEntry>>> xread(final int arg0, final long arg1, final Map.Entry<String, StreamEntryID>... arg2) {
        final RedisCall call = RedisCall.getInstance("xread");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<Map.Entry<String, List<StreamEntry>>> return_value = this.jedis.xread(arg0, arg1, (Map.Entry[])arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public long xack(final String arg0, final String arg1, final StreamEntryID... arg2) {
        final RedisCall call = RedisCall.getInstance("xack");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final long return_value = this.jedis.xack(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String xgroupCreate(final String arg0, final String arg1, final StreamEntryID arg2, final boolean arg3) {
        final RedisCall call = RedisCall.getInstance("xgroupCreate");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.xgroupCreate(arg0, arg1, arg2, arg3);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String xgroupSetID(final String arg0, final String arg1, final StreamEntryID arg2) {
        final RedisCall call = RedisCall.getInstance("xgroupSetID");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.xgroupSetID(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public long xgroupDestroy(final String arg0, final String arg1) {
        final RedisCall call = RedisCall.getInstance("xgroupDestroy");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final long return_value = this.jedis.xgroupDestroy(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String xgroupDelConsumer(final String arg0, final String arg1, final String arg2) {
        final RedisCall call = RedisCall.getInstance("xgroupDelConsumer");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.xgroupDelConsumer(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public long xdel(final String arg0, final StreamEntryID... arg1) {
        final RedisCall call = RedisCall.getInstance("xdel");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final long return_value = this.jedis.xdel(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public long xtrim(final String arg0, final long arg1, final boolean arg2) {
        final RedisCall call = RedisCall.getInstance("xtrim");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final long return_value = this.jedis.xtrim(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<Map.Entry<String, List<StreamEntry>>> xreadGroup(final String arg0, final String arg1, final int arg2, final long arg3, final boolean arg4, final Map.Entry<String, StreamEntryID>... arg5) {
        final RedisCall call = RedisCall.getInstance("xreadGroup");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<Map.Entry<String, List<StreamEntry>>> return_value = this.jedis.xreadGroup(arg0, arg1, arg2, arg3, arg4, (Map.Entry[])arg5);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<StreamPendingEntry> xpending(final String arg0, final String arg1, final StreamEntryID arg2, final StreamEntryID arg3, final int arg4, final String arg5) {
        final RedisCall call = RedisCall.getInstance("xpending");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<StreamPendingEntry> return_value = this.jedis.xpending(arg0, arg1, arg2, arg3, arg4, arg5);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<StreamEntry> xclaim(final String arg0, final String arg1, final String arg2, final long arg3, final long arg4, final int arg5, final boolean arg6, final StreamEntryID... arg7) {
        final RedisCall call = RedisCall.getInstance("xclaim");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<StreamEntry> return_value = this.jedis.xclaim(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long publish(final byte[] arg0, final byte[] arg1) {
        final RedisCall call = RedisCall.getInstance("publish");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.publish(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public void monitor(final JedisMonitor arg0) {
        final RedisCall call = RedisCall.getInstance("monitor");
        call.setClusterIp(this.ip);
        call.start();
        try {
            this.jedis.monitor(arg0);
            call.complete();
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long move(final byte[] arg0, final int arg1) {
        final RedisCall call = RedisCall.getInstance("move");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.move(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long unlink(final byte[]... arg0) {
        final RedisCall call = RedisCall.getInstance("unlink");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.unlink(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long unlink(final byte[] arg0) {
        final RedisCall call = RedisCall.getInstance("unlink");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.unlink(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public ScanResult<byte[]> scan(final byte[] arg0) {
        final RedisCall call = RedisCall.getInstance("scan");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final ScanResult<byte[]> return_value = (ScanResult<byte[]>)this.jedis.scan(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public ScanResult<byte[]> scan(final byte[] arg0, final ScanParams arg1) {
        final RedisCall call = RedisCall.getInstance("scan");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final ScanResult<byte[]> return_value = (ScanResult<byte[]>)this.jedis.scan(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public byte[] echo(final byte[] arg0) {
        final RedisCall call = RedisCall.getInstance("echo");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final byte[] return_value = this.jedis.echo(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public byte[] getSet(final byte[] arg0, final byte[] arg1) {
        final RedisCall call = RedisCall.getInstance("getSet");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final byte[] return_value = this.jedis.getSet(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String shutdown() {
        final RedisCall call = RedisCall.getInstance("shutdown");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.shutdown();
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public byte[] get(final byte[] arg0) {
        final RedisCall call = RedisCall.getInstance("get");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final byte[] return_value = this.jedis.get(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String type(final byte[] arg0) {
        final RedisCall call = RedisCall.getInstance("type");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.type(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long append(final byte[] arg0, final byte[] arg1) {
        final RedisCall call = RedisCall.getInstance("append");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.append(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String debug(final DebugParams arg0) {
        final RedisCall call = RedisCall.getInstance("debug");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.debug(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String save() {
        final RedisCall call = RedisCall.getInstance("save");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.save();
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Set<byte[]> keys(final byte[] arg0) {
        final RedisCall call = RedisCall.getInstance("keys");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Set<byte[]> return_value = this.jedis.keys(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String set(final byte[] arg0, final byte[] arg1, final SetParams arg2) {
        final RedisCall call = RedisCall.getInstance("set");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.set(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String set(final byte[] arg0, final byte[] arg1) {
        final RedisCall call = RedisCall.getInstance("set");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.set(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Boolean exists(final byte[] arg0) {
        final RedisCall call = RedisCall.getInstance("exists");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Boolean return_value = this.jedis.exists(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long exists(final byte[]... arg0) {
        final RedisCall call = RedisCall.getInstance("exists");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.exists(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String rename(final byte[] arg0, final byte[] arg1) {
        final RedisCall call = RedisCall.getInstance("rename");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.rename(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long sort(final byte[] arg0, final byte[] arg1) {
        final RedisCall call = RedisCall.getInstance("sort");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.sort(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<byte[]> sort(final byte[] arg0, final SortingParams arg1) {
        final RedisCall call = RedisCall.getInstance("sort");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<byte[]> return_value = this.jedis.sort(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long sort(final byte[] arg0, final SortingParams arg1, final byte[] arg2) {
        final RedisCall call = RedisCall.getInstance("sort");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.sort(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<byte[]> sort(final byte[] arg0) {
        final RedisCall call = RedisCall.getInstance("sort");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<byte[]> return_value = this.jedis.sort(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public void sync() {
        final RedisCall call = RedisCall.getInstance("sync");
        call.setClusterIp(this.ip);
        call.start();
        try {
            this.jedis.sync();
            call.complete();
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String info() {
        final RedisCall call = RedisCall.getInstance("info");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.info();
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String info(final String arg0) {
        final RedisCall call = RedisCall.getInstance("info");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.info(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<String> time() {
        final RedisCall call = RedisCall.getInstance("time");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<String> return_value = this.jedis.time();
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String auth(final String arg0) {
        final RedisCall call = RedisCall.getInstance("auth");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.auth(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String select(final int arg0) {
        final RedisCall call = RedisCall.getInstance("select");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.select(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long expire(final byte[] arg0, final int arg1) {
        final RedisCall call = RedisCall.getInstance("expire");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.expire(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long ttl(final byte[] arg0) {
        final RedisCall call = RedisCall.getInstance("ttl");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.ttl(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String unwatch() {
        final RedisCall call = RedisCall.getInstance("unwatch");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.unwatch();
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String ping() {
        final RedisCall call = RedisCall.getInstance("ping");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.ping();
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public byte[] ping(final byte[] arg0) {
        final RedisCall call = RedisCall.getInstance("ping");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final byte[] return_value = this.jedis.ping(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String restore(final byte[] arg0, final int arg1, final byte[] arg2) {
        final RedisCall call = RedisCall.getInstance("restore");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.restore(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public byte[] dump(final byte[] arg0) {
        final RedisCall call = RedisCall.getInstance("dump");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final byte[] return_value = this.jedis.dump(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long incr(final byte[] arg0) {
        final RedisCall call = RedisCall.getInstance("incr");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.incr(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Object sendCommand(final ProtocolCommand arg0, final byte[]... arg1) {
        final RedisCall call = RedisCall.getInstance("sendCommand");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Object return_value = this.jedis.sendCommand(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Object sendCommand(final ProtocolCommand arg0) {
        final RedisCall call = RedisCall.getInstance("sendCommand");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Object return_value = this.jedis.sendCommand(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String quit() {
        final RedisCall call = RedisCall.getInstance("quit");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.quit();
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public void subscribe(final BinaryJedisPubSub arg0, final byte[]... arg1) {
        final RedisCall call = RedisCall.getInstance("subscribe");
        call.setClusterIp(this.ip);
        call.start();
        try {
            this.jedis.subscribe(arg0, arg1);
            call.complete();
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String migrate(final String arg0, final int arg1, final int arg2, final int arg3, final MigrateParams arg4, final byte[]... arg5) {
        final RedisCall call = RedisCall.getInstance("migrate");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.migrate(arg0, arg1, arg2, arg3, arg4, arg5);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String migrate(final String arg0, final int arg1, final byte[] arg2, final int arg3, final int arg4) {
        final RedisCall call = RedisCall.getInstance("migrate");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.migrate(arg0, arg1, arg2, arg3, arg4);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<byte[]> configGet(final byte[] arg0) {
        final RedisCall call = RedisCall.getInstance("configGet");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<byte[]> return_value = this.jedis.configGet(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Client getClient() {
        final RedisCall call = RedisCall.getInstance("getClient");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Client return_value = this.jedis.getClient();
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String flushAll() {
        final RedisCall call = RedisCall.getInstance("flushAll");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.flushAll();
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long incrBy(final byte[] arg0, final long arg1) {
        final RedisCall call = RedisCall.getInstance("incrBy");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.incrBy(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String flushDB() {
        final RedisCall call = RedisCall.getInstance("flushDB");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.flushDB();
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long renamenx(final byte[] arg0, final byte[] arg1) {
        final RedisCall call = RedisCall.getInstance("renamenx");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.renamenx(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long expireAt(final byte[] arg0, final long arg1) {
        final RedisCall call = RedisCall.getInstance("expireAt");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.expireAt(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long touch(final byte[] arg0) {
        final RedisCall call = RedisCall.getInstance("touch");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.touch(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long touch(final byte[]... arg0) {
        final RedisCall call = RedisCall.getInstance("touch");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.touch(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<byte[]> mget(final byte[]... arg0) {
        final RedisCall call = RedisCall.getInstance("mget");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<byte[]> return_value = this.jedis.mget(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String setex(final byte[] arg0, final int arg1, final byte[] arg2) {
        final RedisCall call = RedisCall.getInstance("setex");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.setex(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String mset(final byte[]... arg0) {
        final RedisCall call = RedisCall.getInstance("mset");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.mset(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long msetnx(final byte[]... arg0) {
        final RedisCall call = RedisCall.getInstance("msetnx");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.msetnx(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long decrBy(final byte[] arg0, final long arg1) {
        final RedisCall call = RedisCall.getInstance("decrBy");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.decrBy(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long decr(final byte[] arg0) {
        final RedisCall call = RedisCall.getInstance("decr");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.decr(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Double incrByFloat(final byte[] arg0, final double arg1) {
        final RedisCall call = RedisCall.getInstance("incrByFloat");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Double return_value = this.jedis.incrByFloat(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public byte[] substr(final byte[] arg0, final int arg1, final int arg2) {
        final RedisCall call = RedisCall.getInstance("substr");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final byte[] return_value = this.jedis.substr(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long hset(final byte[] arg0, final Map<byte[], byte[]> arg1) {
        final RedisCall call = RedisCall.getInstance("hset");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.hset(arg0, (Map)arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long hset(final byte[] arg0, final byte[] arg1, final byte[] arg2) {
        final RedisCall call = RedisCall.getInstance("hset");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.hset(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public byte[] hget(final byte[] arg0, final byte[] arg1) {
        final RedisCall call = RedisCall.getInstance("hget");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final byte[] return_value = this.jedis.hget(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long hsetnx(final byte[] arg0, final byte[] arg1, final byte[] arg2) {
        final RedisCall call = RedisCall.getInstance("hsetnx");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.hsetnx(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String hmset(final byte[] arg0, final Map<byte[], byte[]> arg1) {
        final RedisCall call = RedisCall.getInstance("hmset");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.hmset(arg0, (Map)arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<byte[]> hmget(final byte[] arg0, final byte[]... arg1) {
        final RedisCall call = RedisCall.getInstance("hmget");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<byte[]> return_value = this.jedis.hmget(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long hincrBy(final byte[] arg0, final byte[] arg1, final long arg2) {
        final RedisCall call = RedisCall.getInstance("hincrBy");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.hincrBy(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Double hincrByFloat(final byte[] arg0, final byte[] arg1, final double arg2) {
        final RedisCall call = RedisCall.getInstance("hincrByFloat");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Double return_value = this.jedis.hincrByFloat(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Boolean hexists(final byte[] arg0, final byte[] arg1) {
        final RedisCall call = RedisCall.getInstance("hexists");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Boolean return_value = this.jedis.hexists(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long hdel(final byte[] arg0, final byte[]... arg1) {
        final RedisCall call = RedisCall.getInstance("hdel");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.hdel(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long hlen(final byte[] arg0) {
        final RedisCall call = RedisCall.getInstance("hlen");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.hlen(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Set<byte[]> hkeys(final byte[] arg0) {
        final RedisCall call = RedisCall.getInstance("hkeys");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Set<byte[]> return_value = this.jedis.hkeys(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<byte[]> hvals(final byte[] arg0) {
        final RedisCall call = RedisCall.getInstance("hvals");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<byte[]> return_value = this.jedis.hvals(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Map<byte[], byte[]> hgetAll(final byte[] arg0) {
        final RedisCall call = RedisCall.getInstance("hgetAll");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Map<byte[], byte[]> return_value = this.jedis.hgetAll(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long rpush(final byte[] arg0, final byte[]... arg1) {
        final RedisCall call = RedisCall.getInstance("rpush");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.rpush(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long lpush(final byte[] arg0, final byte[]... arg1) {
        final RedisCall call = RedisCall.getInstance("lpush");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.lpush(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long llen(final byte[] arg0) {
        final RedisCall call = RedisCall.getInstance("llen");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.llen(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<byte[]> lrange(final byte[] arg0, final long arg1, final long arg2) {
        final RedisCall call = RedisCall.getInstance("lrange");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<byte[]> return_value = this.jedis.lrange(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String ltrim(final byte[] arg0, final long arg1, final long arg2) {
        final RedisCall call = RedisCall.getInstance("ltrim");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.ltrim(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public byte[] lindex(final byte[] arg0, final long arg1) {
        final RedisCall call = RedisCall.getInstance("lindex");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final byte[] return_value = this.jedis.lindex(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String lset(final byte[] arg0, final long arg1, final byte[] arg2) {
        final RedisCall call = RedisCall.getInstance("lset");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.lset(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long lrem(final byte[] arg0, final long arg1, final byte[] arg2) {
        final RedisCall call = RedisCall.getInstance("lrem");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.lrem(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public byte[] lpop(final byte[] arg0) {
        final RedisCall call = RedisCall.getInstance("lpop");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final byte[] return_value = this.jedis.lpop(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public byte[] rpop(final byte[] arg0) {
        final RedisCall call = RedisCall.getInstance("rpop");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final byte[] return_value = this.jedis.rpop(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public byte[] rpoplpush(final byte[] arg0, final byte[] arg1) {
        final RedisCall call = RedisCall.getInstance("rpoplpush");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final byte[] return_value = this.jedis.rpoplpush(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long sadd(final byte[] arg0, final byte[]... arg1) {
        final RedisCall call = RedisCall.getInstance("sadd");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.sadd(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Set<byte[]> smembers(final byte[] arg0) {
        final RedisCall call = RedisCall.getInstance("smembers");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Set<byte[]> return_value = this.jedis.smembers(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long srem(final byte[] arg0, final byte[]... arg1) {
        final RedisCall call = RedisCall.getInstance("srem");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.srem(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Set<byte[]> spop(final byte[] arg0, final long arg1) {
        final RedisCall call = RedisCall.getInstance("spop");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Set<byte[]> return_value = this.jedis.spop(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public byte[] spop(final byte[] arg0) {
        final RedisCall call = RedisCall.getInstance("spop");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final byte[] return_value = this.jedis.spop(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long smove(final byte[] arg0, final byte[] arg1, final byte[] arg2) {
        final RedisCall call = RedisCall.getInstance("smove");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.smove(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long scard(final byte[] arg0) {
        final RedisCall call = RedisCall.getInstance("scard");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.scard(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Boolean sismember(final byte[] arg0, final byte[] arg1) {
        final RedisCall call = RedisCall.getInstance("sismember");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Boolean return_value = this.jedis.sismember(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Set<byte[]> sinter(final byte[]... arg0) {
        final RedisCall call = RedisCall.getInstance("sinter");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Set<byte[]> return_value = this.jedis.sinter(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long sinterstore(final byte[] arg0, final byte[]... arg1) {
        final RedisCall call = RedisCall.getInstance("sinterstore");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.sinterstore(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Set<byte[]> sunion(final byte[]... arg0) {
        final RedisCall call = RedisCall.getInstance("sunion");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Set<byte[]> return_value = this.jedis.sunion(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long sunionstore(final byte[] arg0, final byte[]... arg1) {
        final RedisCall call = RedisCall.getInstance("sunionstore");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.sunionstore(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Set<byte[]> sdiff(final byte[]... arg0) {
        final RedisCall call = RedisCall.getInstance("sdiff");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Set<byte[]> return_value = this.jedis.sdiff(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long sdiffstore(final byte[] arg0, final byte[]... arg1) {
        final RedisCall call = RedisCall.getInstance("sdiffstore");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.sdiffstore(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<byte[]> srandmember(final byte[] arg0, final int arg1) {
        final RedisCall call = RedisCall.getInstance("srandmember");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<byte[]> return_value = this.jedis.srandmember(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public byte[] srandmember(final byte[] arg0) {
        final RedisCall call = RedisCall.getInstance("srandmember");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final byte[] return_value = this.jedis.srandmember(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long zadd(final byte[] arg0, final Map<byte[], Double> arg1) {
        final RedisCall call = RedisCall.getInstance("zadd");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.zadd(arg0, (Map)arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long zadd(final byte[] arg0, final Map<byte[], Double> arg1, final ZAddParams arg2) {
        final RedisCall call = RedisCall.getInstance("zadd");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.zadd(arg0, (Map)arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long zadd(final byte[] arg0, final double arg1, final byte[] arg2, final ZAddParams arg3) {
        final RedisCall call = RedisCall.getInstance("zadd");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.zadd(arg0, arg1, arg2, arg3);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long zadd(final byte[] arg0, final double arg1, final byte[] arg2) {
        final RedisCall call = RedisCall.getInstance("zadd");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.zadd(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Set<byte[]> zrange(final byte[] arg0, final long arg1, final long arg2) {
        final RedisCall call = RedisCall.getInstance("zrange");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Set<byte[]> return_value = this.jedis.zrange(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long zrem(final byte[] arg0, final byte[]... arg1) {
        final RedisCall call = RedisCall.getInstance("zrem");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.zrem(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Double zincrby(final byte[] arg0, final double arg1, final byte[] arg2) {
        final RedisCall call = RedisCall.getInstance("zincrby");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Double return_value = this.jedis.zincrby(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Double zincrby(final byte[] arg0, final double arg1, final byte[] arg2, final ZIncrByParams arg3) {
        final RedisCall call = RedisCall.getInstance("zincrby");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Double return_value = this.jedis.zincrby(arg0, arg1, arg2, arg3);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long zrank(final byte[] arg0, final byte[] arg1) {
        final RedisCall call = RedisCall.getInstance("zrank");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.zrank(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long zrevrank(final byte[] arg0, final byte[] arg1) {
        final RedisCall call = RedisCall.getInstance("zrevrank");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.zrevrank(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Set<byte[]> zrevrange(final byte[] arg0, final long arg1, final long arg2) {
        final RedisCall call = RedisCall.getInstance("zrevrange");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Set<byte[]> return_value = this.jedis.zrevrange(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Set<Tuple> zrangeWithScores(final byte[] arg0, final long arg1, final long arg2) {
        final RedisCall call = RedisCall.getInstance("zrangeWithScores");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Set<Tuple> return_value = this.jedis.zrangeWithScores(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Set<Tuple> zrevrangeWithScores(final byte[] arg0, final long arg1, final long arg2) {
        final RedisCall call = RedisCall.getInstance("zrevrangeWithScores");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Set<Tuple> return_value = this.jedis.zrevrangeWithScores(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long zcard(final byte[] arg0) {
        final RedisCall call = RedisCall.getInstance("zcard");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.zcard(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Double zscore(final byte[] arg0, final byte[] arg1) {
        final RedisCall call = RedisCall.getInstance("zscore");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Double return_value = this.jedis.zscore(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Tuple zpopmax(final byte[] arg0) {
        final RedisCall call = RedisCall.getInstance("zpopmax");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Tuple return_value = this.jedis.zpopmax(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Set<Tuple> zpopmax(final byte[] arg0, final int arg1) {
        final RedisCall call = RedisCall.getInstance("zpopmax");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Set<Tuple> return_value = this.jedis.zpopmax(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Tuple zpopmin(final byte[] arg0) {
        final RedisCall call = RedisCall.getInstance("zpopmin");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Tuple return_value = this.jedis.zpopmin(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Set<Tuple> zpopmin(final byte[] arg0, final int arg1) {
        final RedisCall call = RedisCall.getInstance("zpopmin");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Set<Tuple> return_value = this.jedis.zpopmin(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String watch(final byte[]... arg0) {
        final RedisCall call = RedisCall.getInstance("watch");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.watch(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<byte[]> blpop(final int arg0, final byte[]... arg1) {
        final RedisCall call = RedisCall.getInstance("blpop");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<byte[]> return_value = this.jedis.blpop(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<byte[]> blpop(final byte[]... arg0) {
        final RedisCall call = RedisCall.getInstance("blpop");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<byte[]> return_value = this.jedis.blpop(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<byte[]> brpop(final int arg0, final byte[]... arg1) {
        final RedisCall call = RedisCall.getInstance("brpop");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<byte[]> return_value = this.jedis.brpop(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<byte[]> brpop(final byte[]... arg0) {
        final RedisCall call = RedisCall.getInstance("brpop");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<byte[]> return_value = this.jedis.brpop(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long zcount(final byte[] arg0, final byte[] arg1, final byte[] arg2) {
        final RedisCall call = RedisCall.getInstance("zcount");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.zcount(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long zcount(final byte[] arg0, final double arg1, final double arg2) {
        final RedisCall call = RedisCall.getInstance("zcount");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.zcount(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Set<byte[]> zrangeByScore(final byte[] arg0, final byte[] arg1, final byte[] arg2, final int arg3, final int arg4) {
        final RedisCall call = RedisCall.getInstance("zrangeByScore");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Set<byte[]> return_value = this.jedis.zrangeByScore(arg0, arg1, arg2, arg3, arg4);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Set<byte[]> zrangeByScore(final byte[] arg0, final double arg1, final double arg2, final int arg3, final int arg4) {
        final RedisCall call = RedisCall.getInstance("zrangeByScore");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Set<byte[]> return_value = this.jedis.zrangeByScore(arg0, arg1, arg2, arg3, arg4);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Set<byte[]> zrangeByScore(final byte[] arg0, final byte[] arg1, final byte[] arg2) {
        final RedisCall call = RedisCall.getInstance("zrangeByScore");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Set<byte[]> return_value = this.jedis.zrangeByScore(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Set<byte[]> zrangeByScore(final byte[] arg0, final double arg1, final double arg2) {
        final RedisCall call = RedisCall.getInstance("zrangeByScore");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Set<byte[]> return_value = this.jedis.zrangeByScore(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Set<Tuple> zrangeByScoreWithScores(final byte[] arg0, final byte[] arg1, final byte[] arg2) {
        final RedisCall call = RedisCall.getInstance("zrangeByScoreWithScores");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Set<Tuple> return_value = this.jedis.zrangeByScoreWithScores(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Set<Tuple> zrangeByScoreWithScores(final byte[] arg0, final byte[] arg1, final byte[] arg2, final int arg3, final int arg4) {
        final RedisCall call = RedisCall.getInstance("zrangeByScoreWithScores");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Set<Tuple> return_value = this.jedis.zrangeByScoreWithScores(arg0, arg1, arg2, arg3, arg4);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Set<Tuple> zrangeByScoreWithScores(final byte[] arg0, final double arg1, final double arg2, final int arg3, final int arg4) {
        final RedisCall call = RedisCall.getInstance("zrangeByScoreWithScores");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Set<Tuple> return_value = this.jedis.zrangeByScoreWithScores(arg0, arg1, arg2, arg3, arg4);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Set<Tuple> zrangeByScoreWithScores(final byte[] arg0, final double arg1, final double arg2) {
        final RedisCall call = RedisCall.getInstance("zrangeByScoreWithScores");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Set<Tuple> return_value = this.jedis.zrangeByScoreWithScores(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Set<byte[]> zrevrangeByScore(final byte[] arg0, final byte[] arg1, final byte[] arg2, final int arg3, final int arg4) {
        final RedisCall call = RedisCall.getInstance("zrevrangeByScore");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Set<byte[]> return_value = this.jedis.zrevrangeByScore(arg0, arg1, arg2, arg3, arg4);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Set<byte[]> zrevrangeByScore(final byte[] arg0, final double arg1, final double arg2, final int arg3, final int arg4) {
        final RedisCall call = RedisCall.getInstance("zrevrangeByScore");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Set<byte[]> return_value = this.jedis.zrevrangeByScore(arg0, arg1, arg2, arg3, arg4);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Set<byte[]> zrevrangeByScore(final byte[] arg0, final byte[] arg1, final byte[] arg2) {
        final RedisCall call = RedisCall.getInstance("zrevrangeByScore");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Set<byte[]> return_value = this.jedis.zrevrangeByScore(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Set<byte[]> zrevrangeByScore(final byte[] arg0, final double arg1, final double arg2) {
        final RedisCall call = RedisCall.getInstance("zrevrangeByScore");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Set<byte[]> return_value = this.jedis.zrevrangeByScore(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Set<Tuple> zrevrangeByScoreWithScores(final byte[] arg0, final byte[] arg1, final byte[] arg2) {
        final RedisCall call = RedisCall.getInstance("zrevrangeByScoreWithScores");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Set<Tuple> return_value = this.jedis.zrevrangeByScoreWithScores(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Set<Tuple> zrevrangeByScoreWithScores(final byte[] arg0, final byte[] arg1, final byte[] arg2, final int arg3, final int arg4) {
        final RedisCall call = RedisCall.getInstance("zrevrangeByScoreWithScores");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Set<Tuple> return_value = this.jedis.zrevrangeByScoreWithScores(arg0, arg1, arg2, arg3, arg4);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Set<Tuple> zrevrangeByScoreWithScores(final byte[] arg0, final double arg1, final double arg2, final int arg3, final int arg4) {
        final RedisCall call = RedisCall.getInstance("zrevrangeByScoreWithScores");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Set<Tuple> return_value = this.jedis.zrevrangeByScoreWithScores(arg0, arg1, arg2, arg3, arg4);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Set<Tuple> zrevrangeByScoreWithScores(final byte[] arg0, final double arg1, final double arg2) {
        final RedisCall call = RedisCall.getInstance("zrevrangeByScoreWithScores");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Set<Tuple> return_value = this.jedis.zrevrangeByScoreWithScores(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long zremrangeByRank(final byte[] arg0, final long arg1, final long arg2) {
        final RedisCall call = RedisCall.getInstance("zremrangeByRank");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.zremrangeByRank(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long zremrangeByScore(final byte[] arg0, final byte[] arg1, final byte[] arg2) {
        final RedisCall call = RedisCall.getInstance("zremrangeByScore");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.zremrangeByScore(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long zremrangeByScore(final byte[] arg0, final double arg1, final double arg2) {
        final RedisCall call = RedisCall.getInstance("zremrangeByScore");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.zremrangeByScore(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long zunionstore(final byte[] arg0, final ZParams arg1, final byte[]... arg2) {
        final RedisCall call = RedisCall.getInstance("zunionstore");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.zunionstore(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long zunionstore(final byte[] arg0, final byte[]... arg1) {
        final RedisCall call = RedisCall.getInstance("zunionstore");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.zunionstore(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long zinterstore(final byte[] arg0, final ZParams arg1, final byte[]... arg2) {
        final RedisCall call = RedisCall.getInstance("zinterstore");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.zinterstore(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long zinterstore(final byte[] arg0, final byte[]... arg1) {
        final RedisCall call = RedisCall.getInstance("zinterstore");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.zinterstore(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long zlexcount(final byte[] arg0, final byte[] arg1, final byte[] arg2) {
        final RedisCall call = RedisCall.getInstance("zlexcount");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.zlexcount(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Set<byte[]> zrangeByLex(final byte[] arg0, final byte[] arg1, final byte[] arg2, final int arg3, final int arg4) {
        final RedisCall call = RedisCall.getInstance("zrangeByLex");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Set<byte[]> return_value = this.jedis.zrangeByLex(arg0, arg1, arg2, arg3, arg4);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Set<byte[]> zrangeByLex(final byte[] arg0, final byte[] arg1, final byte[] arg2) {
        final RedisCall call = RedisCall.getInstance("zrangeByLex");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Set<byte[]> return_value = this.jedis.zrangeByLex(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Set<byte[]> zrevrangeByLex(final byte[] arg0, final byte[] arg1, final byte[] arg2, final int arg3, final int arg4) {
        final RedisCall call = RedisCall.getInstance("zrevrangeByLex");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Set<byte[]> return_value = this.jedis.zrevrangeByLex(arg0, arg1, arg2, arg3, arg4);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Set<byte[]> zrevrangeByLex(final byte[] arg0, final byte[] arg1, final byte[] arg2) {
        final RedisCall call = RedisCall.getInstance("zrevrangeByLex");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Set<byte[]> return_value = this.jedis.zrevrangeByLex(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long zremrangeByLex(final byte[] arg0, final byte[] arg1, final byte[] arg2) {
        final RedisCall call = RedisCall.getInstance("zremrangeByLex");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.zremrangeByLex(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long strlen(final byte[] arg0) {
        final RedisCall call = RedisCall.getInstance("strlen");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.strlen(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long lpushx(final byte[] arg0, final byte[]... arg1) {
        final RedisCall call = RedisCall.getInstance("lpushx");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.lpushx(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long persist(final byte[] arg0) {
        final RedisCall call = RedisCall.getInstance("persist");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.persist(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long rpushx(final byte[] arg0, final byte[]... arg1) {
        final RedisCall call = RedisCall.getInstance("rpushx");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.rpushx(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long linsert(final byte[] arg0, final ListPosition arg1, final byte[] arg2, final byte[] arg3) {
        final RedisCall call = RedisCall.getInstance("linsert");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.linsert(arg0, arg1, arg2, arg3);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public byte[] brpoplpush(final byte[] arg0, final byte[] arg1, final int arg2) {
        final RedisCall call = RedisCall.getInstance("brpoplpush");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final byte[] return_value = this.jedis.brpoplpush(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Boolean setbit(final byte[] arg0, final long arg1, final byte[] arg2) {
        final RedisCall call = RedisCall.getInstance("setbit");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Boolean return_value = this.jedis.setbit(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Boolean setbit(final byte[] arg0, final long arg1, final boolean arg2) {
        final RedisCall call = RedisCall.getInstance("setbit");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Boolean return_value = this.jedis.setbit(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Boolean getbit(final byte[] arg0, final long arg1) {
        final RedisCall call = RedisCall.getInstance("getbit");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Boolean return_value = this.jedis.getbit(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long setrange(final byte[] arg0, final long arg1, final byte[] arg2) {
        final RedisCall call = RedisCall.getInstance("setrange");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.setrange(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public byte[] getrange(final byte[] arg0, final long arg1, final long arg2) {
        final RedisCall call = RedisCall.getInstance("getrange");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final byte[] return_value = this.jedis.getrange(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long bitpos(final byte[] arg0, final boolean arg1) {
        final RedisCall call = RedisCall.getInstance("bitpos");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.bitpos(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long bitpos(final byte[] arg0, final boolean arg1, final BitPosParams arg2) {
        final RedisCall call = RedisCall.getInstance("bitpos");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.bitpos(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public byte[] configSet(final byte[] arg0, final byte[] arg1) {
        final RedisCall call = RedisCall.getInstance("configSet");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final byte[] return_value = this.jedis.configSet(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public void psubscribe(final BinaryJedisPubSub arg0, final byte[]... arg1) {
        final RedisCall call = RedisCall.getInstance("psubscribe");
        call.setClusterIp(this.ip);
        call.start();
        try {
            this.jedis.psubscribe(arg0, arg1);
            call.complete();
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Object eval(final byte[] arg0) {
        final RedisCall call = RedisCall.getInstance("eval");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Object return_value = this.jedis.eval(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Object eval(final byte[] arg0, final List<byte[]> arg1, final List<byte[]> arg2) {
        final RedisCall call = RedisCall.getInstance("eval");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Object return_value = this.jedis.eval(arg0, (List)arg1, (List)arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Object eval(final byte[] arg0, final int arg1, final byte[]... arg2) {
        final RedisCall call = RedisCall.getInstance("eval");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Object return_value = this.jedis.eval(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Object eval(final byte[] arg0, final byte[] arg1, final byte[]... arg2) {
        final RedisCall call = RedisCall.getInstance("eval");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Object return_value = this.jedis.eval(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Object evalsha(final byte[] arg0, final List<byte[]> arg1, final List<byte[]> arg2) {
        final RedisCall call = RedisCall.getInstance("evalsha");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Object return_value = this.jedis.evalsha(arg0, (List)arg1, (List)arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Object evalsha(final byte[] arg0) {
        final RedisCall call = RedisCall.getInstance("evalsha");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Object return_value = this.jedis.evalsha(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Object evalsha(final byte[] arg0, final int arg1, final byte[]... arg2) {
        final RedisCall call = RedisCall.getInstance("evalsha");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Object return_value = this.jedis.evalsha(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<Long> scriptExists(final byte[]... arg0) {
        final RedisCall call = RedisCall.getInstance("scriptExists");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<Long> return_value = this.jedis.scriptExists(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long scriptExists(final byte[] arg0) {
        final RedisCall call = RedisCall.getInstance("scriptExists");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.scriptExists(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public byte[] scriptLoad(final byte[] arg0) {
        final RedisCall call = RedisCall.getInstance("scriptLoad");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final byte[] return_value = this.jedis.scriptLoad(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long objectRefcount(final byte[] arg0) {
        final RedisCall call = RedisCall.getInstance("objectRefcount");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.objectRefcount(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public byte[] objectEncoding(final byte[] arg0) {
        final RedisCall call = RedisCall.getInstance("objectEncoding");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final byte[] return_value = this.jedis.objectEncoding(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long objectIdletime(final byte[] arg0) {
        final RedisCall call = RedisCall.getInstance("objectIdletime");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.objectIdletime(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long bitcount(final byte[] arg0) {
        final RedisCall call = RedisCall.getInstance("bitcount");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.bitcount(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long bitcount(final byte[] arg0, final long arg1, final long arg2) {
        final RedisCall call = RedisCall.getInstance("bitcount");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.bitcount(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long bitop(final BitOP arg0, final byte[] arg1, final byte[]... arg2) {
        final RedisCall call = RedisCall.getInstance("bitop");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.bitop(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String restoreReplace(final byte[] arg0, final int arg1, final byte[] arg2) {
        final RedisCall call = RedisCall.getInstance("restoreReplace");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.restoreReplace(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long pexpire(final byte[] arg0, final long arg1) {
        final RedisCall call = RedisCall.getInstance("pexpire");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.pexpire(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long pexpireAt(final byte[] arg0, final long arg1) {
        final RedisCall call = RedisCall.getInstance("pexpireAt");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.pexpireAt(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long pttl(final byte[] arg0) {
        final RedisCall call = RedisCall.getInstance("pttl");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.pttl(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String psetex(final byte[] arg0, final long arg1, final byte[] arg2) {
        final RedisCall call = RedisCall.getInstance("psetex");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.psetex(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String clientKill(final String arg0, final int arg1) {
        final RedisCall call = RedisCall.getInstance("clientKill");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.clientKill(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long clientKill(final ClientKillParams arg0) {
        final RedisCall call = RedisCall.getInstance("clientKill");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.clientKill(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String clientKill(final byte[] arg0) {
        final RedisCall call = RedisCall.getInstance("clientKill");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.clientKill(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String clientSetname(final byte[] arg0) {
        final RedisCall call = RedisCall.getInstance("clientSetname");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.clientSetname(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public ScanResult<Map.Entry<byte[], byte[]>> hscan(final byte[] arg0, final byte[] arg1, final ScanParams arg2) {
        final RedisCall call = RedisCall.getInstance("hscan");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final ScanResult<Map.Entry<byte[], byte[]>> return_value = (ScanResult<Map.Entry<byte[], byte[]>>)this.jedis.hscan(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public ScanResult<Map.Entry<byte[], byte[]>> hscan(final byte[] arg0, final byte[] arg1) {
        final RedisCall call = RedisCall.getInstance("hscan");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final ScanResult<Map.Entry<byte[], byte[]>> return_value = (ScanResult<Map.Entry<byte[], byte[]>>)this.jedis.hscan(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public ScanResult<byte[]> sscan(final byte[] arg0, final byte[] arg1, final ScanParams arg2) {
        final RedisCall call = RedisCall.getInstance("sscan");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final ScanResult<byte[]> return_value = (ScanResult<byte[]>)this.jedis.sscan(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public ScanResult<byte[]> sscan(final byte[] arg0, final byte[] arg1) {
        final RedisCall call = RedisCall.getInstance("sscan");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final ScanResult<byte[]> return_value = (ScanResult<byte[]>)this.jedis.sscan(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public ScanResult<Tuple> zscan(final byte[] arg0, final byte[] arg1) {
        final RedisCall call = RedisCall.getInstance("zscan");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final ScanResult<Tuple> return_value = (ScanResult<Tuple>)this.jedis.zscan(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public ScanResult<Tuple> zscan(final byte[] arg0, final byte[] arg1, final ScanParams arg2) {
        final RedisCall call = RedisCall.getInstance("zscan");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final ScanResult<Tuple> return_value = (ScanResult<Tuple>)this.jedis.zscan(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long setnx(final byte[] arg0, final byte[] arg1) {
        final RedisCall call = RedisCall.getInstance("setnx");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.setnx(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long del(final byte[] arg0) {
        final RedisCall call = RedisCall.getInstance("del");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.del(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long del(final byte[]... arg0) {
        final RedisCall call = RedisCall.getInstance("del");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.del(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long pfadd(final byte[] arg0, final byte[]... arg1) {
        final RedisCall call = RedisCall.getInstance("pfadd");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.pfadd(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long pfcount(final byte[]... arg0) {
        final RedisCall call = RedisCall.getInstance("pfcount");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.pfcount(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public long pfcount(final byte[] arg0) {
        final RedisCall call = RedisCall.getInstance("pfcount");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final long return_value = this.jedis.pfcount(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String pfmerge(final byte[] arg0, final byte[]... arg1) {
        final RedisCall call = RedisCall.getInstance("pfmerge");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.pfmerge(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long geoadd(final byte[] arg0, final Map<byte[], GeoCoordinate> arg1) {
        final RedisCall call = RedisCall.getInstance("geoadd");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.geoadd(arg0, (Map)arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long geoadd(final byte[] arg0, final double arg1, final double arg2, final byte[] arg3) {
        final RedisCall call = RedisCall.getInstance("geoadd");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.geoadd(arg0, arg1, arg2, arg3);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Double geodist(final byte[] arg0, final byte[] arg1, final byte[] arg2) {
        final RedisCall call = RedisCall.getInstance("geodist");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Double return_value = this.jedis.geodist(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Double geodist(final byte[] arg0, final byte[] arg1, final byte[] arg2, final GeoUnit arg3) {
        final RedisCall call = RedisCall.getInstance("geodist");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Double return_value = this.jedis.geodist(arg0, arg1, arg2, arg3);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<byte[]> geohash(final byte[] arg0, final byte[]... arg1) {
        final RedisCall call = RedisCall.getInstance("geohash");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<byte[]> return_value = this.jedis.geohash(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<GeoCoordinate> geopos(final byte[] arg0, final byte[]... arg1) {
        final RedisCall call = RedisCall.getInstance("geopos");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<GeoCoordinate> return_value = this.jedis.geopos(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<GeoRadiusResponse> georadius(final byte[] arg0, final double arg1, final double arg2, final double arg3, final GeoUnit arg4, final GeoRadiusParam arg5) {
        final RedisCall call = RedisCall.getInstance("georadius");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<GeoRadiusResponse> return_value = this.jedis.georadius(arg0, arg1, arg2, arg3, arg4, arg5);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<GeoRadiusResponse> georadius(final byte[] arg0, final double arg1, final double arg2, final double arg3, final GeoUnit arg4) {
        final RedisCall call = RedisCall.getInstance("georadius");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<GeoRadiusResponse> return_value = this.jedis.georadius(arg0, arg1, arg2, arg3, arg4);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<GeoRadiusResponse> georadiusReadonly(final byte[] arg0, final double arg1, final double arg2, final double arg3, final GeoUnit arg4) {
        final RedisCall call = RedisCall.getInstance("georadiusReadonly");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<GeoRadiusResponse> return_value = this.jedis.georadiusReadonly(arg0, arg1, arg2, arg3, arg4);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<GeoRadiusResponse> georadiusReadonly(final byte[] arg0, final double arg1, final double arg2, final double arg3, final GeoUnit arg4, final GeoRadiusParam arg5) {
        final RedisCall call = RedisCall.getInstance("georadiusReadonly");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<GeoRadiusResponse> return_value = this.jedis.georadiusReadonly(arg0, arg1, arg2, arg3, arg4, arg5);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<GeoRadiusResponse> georadiusByMember(final byte[] arg0, final byte[] arg1, final double arg2, final GeoUnit arg3, final GeoRadiusParam arg4) {
        final RedisCall call = RedisCall.getInstance("georadiusByMember");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<GeoRadiusResponse> return_value = this.jedis.georadiusByMember(arg0, arg1, arg2, arg3, arg4);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<GeoRadiusResponse> georadiusByMember(final byte[] arg0, final byte[] arg1, final double arg2, final GeoUnit arg3) {
        final RedisCall call = RedisCall.getInstance("georadiusByMember");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<GeoRadiusResponse> return_value = this.jedis.georadiusByMember(arg0, arg1, arg2, arg3);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<GeoRadiusResponse> georadiusByMemberReadonly(final byte[] arg0, final byte[] arg1, final double arg2, final GeoUnit arg3) {
        final RedisCall call = RedisCall.getInstance("georadiusByMemberReadonly");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<GeoRadiusResponse> return_value = this.jedis.georadiusByMemberReadonly(arg0, arg1, arg2, arg3);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<GeoRadiusResponse> georadiusByMemberReadonly(final byte[] arg0, final byte[] arg1, final double arg2, final GeoUnit arg3, final GeoRadiusParam arg4) {
        final RedisCall call = RedisCall.getInstance("georadiusByMemberReadonly");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<GeoRadiusResponse> return_value = this.jedis.georadiusByMemberReadonly(arg0, arg1, arg2, arg3, arg4);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<Long> bitfield(final byte[] arg0, final byte[]... arg1) {
        final RedisCall call = RedisCall.getInstance("bitfield");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<Long> return_value = this.jedis.bitfield(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long hstrlen(final byte[] arg0, final byte[] arg1) {
        final RedisCall call = RedisCall.getInstance("hstrlen");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.hstrlen(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public byte[] xadd(final byte[] arg0, final byte[] arg1, final Map<byte[], byte[]> arg2, final long arg3, final boolean arg4) {
        final RedisCall call = RedisCall.getInstance("xadd");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final byte[] return_value = this.jedis.xadd(arg0, arg1, (Map)arg2, arg3, arg4);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long xlen(final byte[] arg0) {
        final RedisCall call = RedisCall.getInstance("xlen");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.xlen(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<byte[]> xrange(final byte[] arg0, final byte[] arg1, final byte[] arg2, final long arg3) {
        final RedisCall call = RedisCall.getInstance("xrange");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<byte[]> return_value = this.jedis.xrange(arg0, arg1, arg2, arg3);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<byte[]> xrevrange(final byte[] arg0, final byte[] arg1, final byte[] arg2, final int arg3) {
        final RedisCall call = RedisCall.getInstance("xrevrange");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<byte[]> return_value = this.jedis.xrevrange(arg0, arg1, arg2, arg3);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<byte[]> xread(final int arg0, final long arg1, final Map<byte[], byte[]> arg2) {
        final RedisCall call = RedisCall.getInstance("xread");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<byte[]> return_value = this.jedis.xread(arg0, arg1, (Map)arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long xack(final byte[] arg0, final byte[] arg1, final byte[]... arg2) {
        final RedisCall call = RedisCall.getInstance("xack");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.xack(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String xgroupCreate(final byte[] arg0, final byte[] arg1, final byte[] arg2, final boolean arg3) {
        final RedisCall call = RedisCall.getInstance("xgroupCreate");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.xgroupCreate(arg0, arg1, arg2, arg3);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String xgroupSetID(final byte[] arg0, final byte[] arg1, final byte[] arg2) {
        final RedisCall call = RedisCall.getInstance("xgroupSetID");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.xgroupSetID(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long xgroupDestroy(final byte[] arg0, final byte[] arg1) {
        final RedisCall call = RedisCall.getInstance("xgroupDestroy");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.xgroupDestroy(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String xgroupDelConsumer(final byte[] arg0, final byte[] arg1, final byte[] arg2) {
        final RedisCall call = RedisCall.getInstance("xgroupDelConsumer");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.xgroupDelConsumer(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long xdel(final byte[] arg0, final byte[]... arg1) {
        final RedisCall call = RedisCall.getInstance("xdel");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.xdel(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long xtrim(final byte[] arg0, final long arg1, final boolean arg2) {
        final RedisCall call = RedisCall.getInstance("xtrim");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.xtrim(arg0, arg1, arg2);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<byte[]> xreadGroup(final byte[] arg0, final byte[] arg1, final int arg2, final long arg3, final boolean arg4, final Map<byte[], byte[]> arg5) {
        final RedisCall call = RedisCall.getInstance("xreadGroup");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<byte[]> return_value = this.jedis.xreadGroup(arg0, arg1, arg2, arg3, arg4, (Map)arg5);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<byte[]> xpending(final byte[] arg0, final byte[] arg1, final byte[] arg2, final byte[] arg3, final int arg4, final byte[] arg5) {
        final RedisCall call = RedisCall.getInstance("xpending");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<byte[]> return_value = this.jedis.xpending(arg0, arg1, arg2, arg3, arg4, arg5);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<byte[]> xclaim(final byte[] arg0, final byte[] arg1, final byte[] arg2, final long arg3, final long arg4, final int arg5, final boolean arg6, final byte[][] arg7) {
        final RedisCall call = RedisCall.getInstance("xclaim");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<byte[]> return_value = this.jedis.xclaim(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String slowlogReset() {
        final RedisCall call = RedisCall.getInstance("slowlogReset");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.slowlogReset();
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long slowlogLen() {
        final RedisCall call = RedisCall.getInstance("slowlogLen");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.slowlogLen();
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public byte[] randomBinaryKey() {
        final RedisCall call = RedisCall.getInstance("randomBinaryKey");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final byte[] return_value = this.jedis.randomBinaryKey();
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String swapDB(final int arg0, final int arg1) {
        final RedisCall call = RedisCall.getInstance("swapDB");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.swapDB(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long dbSize() {
        final RedisCall call = RedisCall.getInstance("dbSize");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.dbSize();
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String bgsave() {
        final RedisCall call = RedisCall.getInstance("bgsave");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.bgsave();
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String bgrewriteaof() {
        final RedisCall call = RedisCall.getInstance("bgrewriteaof");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.bgrewriteaof();
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long lastsave() {
        final RedisCall call = RedisCall.getInstance("lastsave");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.lastsave();
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String slaveof(final String arg0, final int arg1) {
        final RedisCall call = RedisCall.getInstance("slaveof");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.slaveof(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String slaveofNoOne() {
        final RedisCall call = RedisCall.getInstance("slaveofNoOne");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.slaveofNoOne();
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public int getDB() {
        final RedisCall call = RedisCall.getInstance("getDB");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final int return_value = this.jedis.getDB();
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String configResetStat() {
        final RedisCall call = RedisCall.getInstance("configResetStat");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.configResetStat();
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String configRewrite() {
        final RedisCall call = RedisCall.getInstance("configRewrite");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.configRewrite();
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Long waitReplicas(final int arg0, final long arg1) {
        final RedisCall call = RedisCall.getInstance("waitReplicas");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Long return_value = this.jedis.waitReplicas(arg0, arg1);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Transaction multi() {
        final RedisCall call = RedisCall.getInstance("multi");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Transaction return_value = this.jedis.multi();
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public Pipeline pipelined() {
        final RedisCall call = RedisCall.getInstance("pipelined");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final Pipeline return_value = this.jedis.pipelined();
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String scriptFlush() {
        final RedisCall call = RedisCall.getInstance("scriptFlush");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.scriptFlush();
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String scriptKill() {
        final RedisCall call = RedisCall.getInstance("scriptKill");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.scriptKill();
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<byte[]> slowlogGetBinary() {
        final RedisCall call = RedisCall.getInstance("slowlogGetBinary");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<byte[]> return_value = this.jedis.slowlogGetBinary();
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public List<byte[]> slowlogGetBinary(final long arg0) {
        final RedisCall call = RedisCall.getInstance("slowlogGetBinary");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final List<byte[]> return_value = this.jedis.slowlogGetBinary(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public byte[] memoryDoctorBinary() {
        final RedisCall call = RedisCall.getInstance("memoryDoctorBinary");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final byte[] return_value = this.jedis.memoryDoctorBinary();
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public byte[] clientGetnameBinary() {
        final RedisCall call = RedisCall.getInstance("clientGetnameBinary");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final byte[] return_value = this.jedis.clientGetnameBinary();
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public byte[] clientListBinary() {
        final RedisCall call = RedisCall.getInstance("clientListBinary");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final byte[] return_value = this.jedis.clientListBinary();
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
    
    public String clientPause(final long arg0) {
        final RedisCall call = RedisCall.getInstance("clientPause");
        call.setClusterIp(this.ip);
        call.start();
        try {
            final String return_value = this.jedis.clientPause(arg0);
            call.complete();
            return return_value;
        }
        catch (final JedisException exc) {
            call.complete((Throwable)exc);
            throw exc;
        }
    }
}
