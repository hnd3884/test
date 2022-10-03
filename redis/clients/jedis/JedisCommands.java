package redis.clients.jedis;

import java.util.Set;
import java.util.List;
import java.util.Map;

public interface JedisCommands
{
    String set(final String p0, final String p1);
    
    String set(final String p0, final String p1, final String p2, final String p3, final long p4);
    
    String get(final String p0);
    
    Boolean exists(final String p0);
    
    Long persist(final String p0);
    
    String type(final String p0);
    
    Long expire(final String p0, final int p1);
    
    Long pexpire(final String p0, final long p1);
    
    Long expireAt(final String p0, final long p1);
    
    Long pexpireAt(final String p0, final long p1);
    
    Long ttl(final String p0);
    
    Boolean setbit(final String p0, final long p1, final boolean p2);
    
    Boolean setbit(final String p0, final long p1, final String p2);
    
    Boolean getbit(final String p0, final long p1);
    
    Long setrange(final String p0, final long p1, final String p2);
    
    String getrange(final String p0, final long p1, final long p2);
    
    String getSet(final String p0, final String p1);
    
    Long setnx(final String p0, final String p1);
    
    String setex(final String p0, final int p1, final String p2);
    
    Long decrBy(final String p0, final long p1);
    
    Long decr(final String p0);
    
    Long incrBy(final String p0, final long p1);
    
    Double incrByFloat(final String p0, final double p1);
    
    Long incr(final String p0);
    
    Long append(final String p0, final String p1);
    
    String substr(final String p0, final int p1, final int p2);
    
    Long hset(final String p0, final String p1, final String p2);
    
    String hget(final String p0, final String p1);
    
    Long hsetnx(final String p0, final String p1, final String p2);
    
    String hmset(final String p0, final Map<String, String> p1);
    
    List<String> hmget(final String p0, final String... p1);
    
    Long hincrBy(final String p0, final String p1, final long p2);
    
    Boolean hexists(final String p0, final String p1);
    
    Long hdel(final String p0, final String... p1);
    
    Long hlen(final String p0);
    
    Set<String> hkeys(final String p0);
    
    List<String> hvals(final String p0);
    
    Map<String, String> hgetAll(final String p0);
    
    Long rpush(final String p0, final String... p1);
    
    Long lpush(final String p0, final String... p1);
    
    Long llen(final String p0);
    
    List<String> lrange(final String p0, final long p1, final long p2);
    
    String ltrim(final String p0, final long p1, final long p2);
    
    String lindex(final String p0, final long p1);
    
    String lset(final String p0, final long p1, final String p2);
    
    Long lrem(final String p0, final long p1, final String p2);
    
    String lpop(final String p0);
    
    String rpop(final String p0);
    
    Long sadd(final String p0, final String... p1);
    
    Set<String> smembers(final String p0);
    
    Long srem(final String p0, final String... p1);
    
    String spop(final String p0);
    
    Set<String> spop(final String p0, final long p1);
    
    Long scard(final String p0);
    
    Boolean sismember(final String p0, final String p1);
    
    String srandmember(final String p0);
    
    List<String> srandmember(final String p0, final int p1);
    
    Long strlen(final String p0);
    
    Long zadd(final String p0, final double p1, final String p2);
    
    Long zadd(final String p0, final Map<String, Double> p1);
    
    Set<String> zrange(final String p0, final long p1, final long p2);
    
    Long zrem(final String p0, final String... p1);
    
    Double zincrby(final String p0, final double p1, final String p2);
    
    Long zrank(final String p0, final String p1);
    
    Long zrevrank(final String p0, final String p1);
    
    Set<String> zrevrange(final String p0, final long p1, final long p2);
    
    Set<Tuple> zrangeWithScores(final String p0, final long p1, final long p2);
    
    Set<Tuple> zrevrangeWithScores(final String p0, final long p1, final long p2);
    
    Long zcard(final String p0);
    
    Double zscore(final String p0, final String p1);
    
    List<String> sort(final String p0);
    
    List<String> sort(final String p0, final SortingParams p1);
    
    Long zcount(final String p0, final double p1, final double p2);
    
    Long zcount(final String p0, final String p1, final String p2);
    
    Set<String> zrangeByScore(final String p0, final double p1, final double p2);
    
    Set<String> zrangeByScore(final String p0, final String p1, final String p2);
    
    Set<String> zrevrangeByScore(final String p0, final double p1, final double p2);
    
    Set<String> zrangeByScore(final String p0, final double p1, final double p2, final int p3, final int p4);
    
    Set<String> zrevrangeByScore(final String p0, final String p1, final String p2);
    
    Set<String> zrangeByScore(final String p0, final String p1, final String p2, final int p3, final int p4);
    
    Set<String> zrevrangeByScore(final String p0, final double p1, final double p2, final int p3, final int p4);
    
    Set<Tuple> zrangeByScoreWithScores(final String p0, final double p1, final double p2);
    
    Set<Tuple> zrevrangeByScoreWithScores(final String p0, final double p1, final double p2);
    
    Set<Tuple> zrangeByScoreWithScores(final String p0, final double p1, final double p2, final int p3, final int p4);
    
    Set<String> zrevrangeByScore(final String p0, final String p1, final String p2, final int p3, final int p4);
    
    Set<Tuple> zrangeByScoreWithScores(final String p0, final String p1, final String p2);
    
    Set<Tuple> zrevrangeByScoreWithScores(final String p0, final String p1, final String p2);
    
    Set<Tuple> zrangeByScoreWithScores(final String p0, final String p1, final String p2, final int p3, final int p4);
    
    Set<Tuple> zrevrangeByScoreWithScores(final String p0, final double p1, final double p2, final int p3, final int p4);
    
    Set<Tuple> zrevrangeByScoreWithScores(final String p0, final String p1, final String p2, final int p3, final int p4);
    
    Long zremrangeByRank(final String p0, final long p1, final long p2);
    
    Long zremrangeByScore(final String p0, final double p1, final double p2);
    
    Long zremrangeByScore(final String p0, final String p1, final String p2);
    
    Long zlexcount(final String p0, final String p1, final String p2);
    
    Set<String> zrangeByLex(final String p0, final String p1, final String p2);
    
    Set<String> zrangeByLex(final String p0, final String p1, final String p2, final int p3, final int p4);
    
    Set<String> zrevrangeByLex(final String p0, final String p1, final String p2);
    
    Set<String> zrevrangeByLex(final String p0, final String p1, final String p2, final int p3, final int p4);
    
    Long zremrangeByLex(final String p0, final String p1, final String p2);
    
    Long linsert(final String p0, final BinaryClient.LIST_POSITION p1, final String p2, final String p3);
    
    Long lpushx(final String p0, final String... p1);
    
    Long rpushx(final String p0, final String... p1);
    
    @Deprecated
    List<String> blpop(final String p0);
    
    List<String> blpop(final int p0, final String p1);
    
    @Deprecated
    List<String> brpop(final String p0);
    
    List<String> brpop(final int p0, final String p1);
    
    Long del(final String p0);
    
    String echo(final String p0);
    
    Long move(final String p0, final int p1);
    
    Long bitcount(final String p0);
    
    Long bitcount(final String p0, final long p1, final long p2);
    
    @Deprecated
    ScanResult<Map.Entry<String, String>> hscan(final String p0, final int p1);
    
    @Deprecated
    ScanResult<String> sscan(final String p0, final int p1);
    
    @Deprecated
    ScanResult<Tuple> zscan(final String p0, final int p1);
    
    ScanResult<Map.Entry<String, String>> hscan(final String p0, final String p1);
    
    ScanResult<String> sscan(final String p0, final String p1);
    
    ScanResult<Tuple> zscan(final String p0, final String p1);
    
    Long pfadd(final String p0, final String... p1);
    
    long pfcount(final String p0);
}
