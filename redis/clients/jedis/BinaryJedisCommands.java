package redis.clients.jedis;

import java.util.Collection;
import java.util.Set;
import java.util.List;
import java.util.Map;

public interface BinaryJedisCommands
{
    String set(final byte[] p0, final byte[] p1);
    
    String set(final byte[] p0, final byte[] p1, final byte[] p2, final byte[] p3, final long p4);
    
    byte[] get(final byte[] p0);
    
    Boolean exists(final byte[] p0);
    
    Long persist(final byte[] p0);
    
    String type(final byte[] p0);
    
    Long expire(final byte[] p0, final int p1);
    
    @Deprecated
    Long pexpire(final String p0, final long p1);
    
    Long pexpire(final byte[] p0, final long p1);
    
    Long expireAt(final byte[] p0, final long p1);
    
    Long pexpireAt(final byte[] p0, final long p1);
    
    Long ttl(final byte[] p0);
    
    Boolean setbit(final byte[] p0, final long p1, final boolean p2);
    
    Boolean setbit(final byte[] p0, final long p1, final byte[] p2);
    
    Boolean getbit(final byte[] p0, final long p1);
    
    Long setrange(final byte[] p0, final long p1, final byte[] p2);
    
    byte[] getrange(final byte[] p0, final long p1, final long p2);
    
    byte[] getSet(final byte[] p0, final byte[] p1);
    
    Long setnx(final byte[] p0, final byte[] p1);
    
    String setex(final byte[] p0, final int p1, final byte[] p2);
    
    Long decrBy(final byte[] p0, final long p1);
    
    Long decr(final byte[] p0);
    
    Long incrBy(final byte[] p0, final long p1);
    
    Double incrByFloat(final byte[] p0, final double p1);
    
    Long incr(final byte[] p0);
    
    Long append(final byte[] p0, final byte[] p1);
    
    byte[] substr(final byte[] p0, final int p1, final int p2);
    
    Long hset(final byte[] p0, final byte[] p1, final byte[] p2);
    
    byte[] hget(final byte[] p0, final byte[] p1);
    
    Long hsetnx(final byte[] p0, final byte[] p1, final byte[] p2);
    
    String hmset(final byte[] p0, final Map<byte[], byte[]> p1);
    
    List<byte[]> hmget(final byte[] p0, final byte[]... p1);
    
    Long hincrBy(final byte[] p0, final byte[] p1, final long p2);
    
    Double hincrByFloat(final byte[] p0, final byte[] p1, final double p2);
    
    Boolean hexists(final byte[] p0, final byte[] p1);
    
    Long hdel(final byte[] p0, final byte[]... p1);
    
    Long hlen(final byte[] p0);
    
    Set<byte[]> hkeys(final byte[] p0);
    
    Collection<byte[]> hvals(final byte[] p0);
    
    Map<byte[], byte[]> hgetAll(final byte[] p0);
    
    Long rpush(final byte[] p0, final byte[]... p1);
    
    Long lpush(final byte[] p0, final byte[]... p1);
    
    Long llen(final byte[] p0);
    
    List<byte[]> lrange(final byte[] p0, final long p1, final long p2);
    
    String ltrim(final byte[] p0, final long p1, final long p2);
    
    byte[] lindex(final byte[] p0, final long p1);
    
    String lset(final byte[] p0, final long p1, final byte[] p2);
    
    Long lrem(final byte[] p0, final long p1, final byte[] p2);
    
    byte[] lpop(final byte[] p0);
    
    byte[] rpop(final byte[] p0);
    
    Long sadd(final byte[] p0, final byte[]... p1);
    
    Set<byte[]> smembers(final byte[] p0);
    
    Long srem(final byte[] p0, final byte[]... p1);
    
    byte[] spop(final byte[] p0);
    
    Set<byte[]> spop(final byte[] p0, final long p1);
    
    Long scard(final byte[] p0);
    
    Boolean sismember(final byte[] p0, final byte[] p1);
    
    byte[] srandmember(final byte[] p0);
    
    List<byte[]> srandmember(final byte[] p0, final int p1);
    
    Long strlen(final byte[] p0);
    
    Long zadd(final byte[] p0, final double p1, final byte[] p2);
    
    Long zadd(final byte[] p0, final Map<byte[], Double> p1);
    
    Set<byte[]> zrange(final byte[] p0, final long p1, final long p2);
    
    Long zrem(final byte[] p0, final byte[]... p1);
    
    Double zincrby(final byte[] p0, final double p1, final byte[] p2);
    
    Long zrank(final byte[] p0, final byte[] p1);
    
    Long zrevrank(final byte[] p0, final byte[] p1);
    
    Set<byte[]> zrevrange(final byte[] p0, final long p1, final long p2);
    
    Set<Tuple> zrangeWithScores(final byte[] p0, final long p1, final long p2);
    
    Set<Tuple> zrevrangeWithScores(final byte[] p0, final long p1, final long p2);
    
    Long zcard(final byte[] p0);
    
    Double zscore(final byte[] p0, final byte[] p1);
    
    List<byte[]> sort(final byte[] p0);
    
    List<byte[]> sort(final byte[] p0, final SortingParams p1);
    
    Long zcount(final byte[] p0, final double p1, final double p2);
    
    Long zcount(final byte[] p0, final byte[] p1, final byte[] p2);
    
    Set<byte[]> zrangeByScore(final byte[] p0, final double p1, final double p2);
    
    Set<byte[]> zrangeByScore(final byte[] p0, final byte[] p1, final byte[] p2);
    
    Set<byte[]> zrevrangeByScore(final byte[] p0, final double p1, final double p2);
    
    Set<byte[]> zrangeByScore(final byte[] p0, final double p1, final double p2, final int p3, final int p4);
    
    Set<byte[]> zrevrangeByScore(final byte[] p0, final byte[] p1, final byte[] p2);
    
    Set<byte[]> zrangeByScore(final byte[] p0, final byte[] p1, final byte[] p2, final int p3, final int p4);
    
    Set<byte[]> zrevrangeByScore(final byte[] p0, final double p1, final double p2, final int p3, final int p4);
    
    Set<Tuple> zrangeByScoreWithScores(final byte[] p0, final double p1, final double p2);
    
    Set<Tuple> zrevrangeByScoreWithScores(final byte[] p0, final double p1, final double p2);
    
    Set<Tuple> zrangeByScoreWithScores(final byte[] p0, final double p1, final double p2, final int p3, final int p4);
    
    Set<byte[]> zrevrangeByScore(final byte[] p0, final byte[] p1, final byte[] p2, final int p3, final int p4);
    
    Set<Tuple> zrangeByScoreWithScores(final byte[] p0, final byte[] p1, final byte[] p2);
    
    Set<Tuple> zrevrangeByScoreWithScores(final byte[] p0, final byte[] p1, final byte[] p2);
    
    Set<Tuple> zrangeByScoreWithScores(final byte[] p0, final byte[] p1, final byte[] p2, final int p3, final int p4);
    
    Set<Tuple> zrevrangeByScoreWithScores(final byte[] p0, final double p1, final double p2, final int p3, final int p4);
    
    Set<Tuple> zrevrangeByScoreWithScores(final byte[] p0, final byte[] p1, final byte[] p2, final int p3, final int p4);
    
    Long zremrangeByRank(final byte[] p0, final long p1, final long p2);
    
    Long zremrangeByScore(final byte[] p0, final double p1, final double p2);
    
    Long zremrangeByScore(final byte[] p0, final byte[] p1, final byte[] p2);
    
    Long zlexcount(final byte[] p0, final byte[] p1, final byte[] p2);
    
    Set<byte[]> zrangeByLex(final byte[] p0, final byte[] p1, final byte[] p2);
    
    Set<byte[]> zrangeByLex(final byte[] p0, final byte[] p1, final byte[] p2, final int p3, final int p4);
    
    Set<byte[]> zrevrangeByLex(final byte[] p0, final byte[] p1, final byte[] p2);
    
    Set<byte[]> zrevrangeByLex(final byte[] p0, final byte[] p1, final byte[] p2, final int p3, final int p4);
    
    Long zremrangeByLex(final byte[] p0, final byte[] p1, final byte[] p2);
    
    Long linsert(final byte[] p0, final BinaryClient.LIST_POSITION p1, final byte[] p2, final byte[] p3);
    
    Long lpushx(final byte[] p0, final byte[]... p1);
    
    Long rpushx(final byte[] p0, final byte[]... p1);
    
    @Deprecated
    List<byte[]> blpop(final byte[] p0);
    
    @Deprecated
    List<byte[]> brpop(final byte[] p0);
    
    Long del(final byte[] p0);
    
    byte[] echo(final byte[] p0);
    
    Long move(final byte[] p0, final int p1);
    
    Long bitcount(final byte[] p0);
    
    Long bitcount(final byte[] p0, final long p1, final long p2);
    
    Long pfadd(final byte[] p0, final byte[]... p1);
    
    long pfcount(final byte[] p0);
}
