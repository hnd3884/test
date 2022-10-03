package redis.clients.jedis;

import java.util.Set;
import java.util.Map;
import java.util.List;

public interface BinaryRedisPipeline
{
    Response<Long> append(final byte[] p0, final byte[] p1);
    
    Response<List<byte[]>> blpop(final byte[] p0);
    
    Response<List<byte[]>> brpop(final byte[] p0);
    
    Response<Long> decr(final byte[] p0);
    
    Response<Long> decrBy(final byte[] p0, final long p1);
    
    Response<Long> del(final byte[] p0);
    
    Response<byte[]> echo(final byte[] p0);
    
    Response<Boolean> exists(final byte[] p0);
    
    Response<Long> expire(final byte[] p0, final int p1);
    
    Response<Long> pexpire(final byte[] p0, final long p1);
    
    Response<Long> expireAt(final byte[] p0, final long p1);
    
    Response<Long> pexpireAt(final byte[] p0, final long p1);
    
    Response<byte[]> get(final byte[] p0);
    
    Response<Boolean> getbit(final byte[] p0, final long p1);
    
    Response<byte[]> getSet(final byte[] p0, final byte[] p1);
    
    Response<Long> getrange(final byte[] p0, final long p1, final long p2);
    
    Response<Long> hdel(final byte[] p0, final byte[]... p1);
    
    Response<Boolean> hexists(final byte[] p0, final byte[] p1);
    
    Response<byte[]> hget(final byte[] p0, final byte[] p1);
    
    Response<Map<byte[], byte[]>> hgetAll(final byte[] p0);
    
    Response<Long> hincrBy(final byte[] p0, final byte[] p1, final long p2);
    
    Response<Set<byte[]>> hkeys(final byte[] p0);
    
    Response<Long> hlen(final byte[] p0);
    
    Response<List<byte[]>> hmget(final byte[] p0, final byte[]... p1);
    
    Response<String> hmset(final byte[] p0, final Map<byte[], byte[]> p1);
    
    Response<Long> hset(final byte[] p0, final byte[] p1, final byte[] p2);
    
    Response<Long> hsetnx(final byte[] p0, final byte[] p1, final byte[] p2);
    
    Response<List<byte[]>> hvals(final byte[] p0);
    
    Response<Long> incr(final byte[] p0);
    
    Response<Long> incrBy(final byte[] p0, final long p1);
    
    Response<byte[]> lindex(final byte[] p0, final long p1);
    
    Response<Long> linsert(final byte[] p0, final BinaryClient.LIST_POSITION p1, final byte[] p2, final byte[] p3);
    
    Response<Long> llen(final byte[] p0);
    
    Response<byte[]> lpop(final byte[] p0);
    
    Response<Long> lpush(final byte[] p0, final byte[]... p1);
    
    Response<Long> lpushx(final byte[] p0, final byte[]... p1);
    
    Response<List<byte[]>> lrange(final byte[] p0, final long p1, final long p2);
    
    Response<Long> lrem(final byte[] p0, final long p1, final byte[] p2);
    
    Response<String> lset(final byte[] p0, final long p1, final byte[] p2);
    
    Response<String> ltrim(final byte[] p0, final long p1, final long p2);
    
    Response<Long> move(final byte[] p0, final int p1);
    
    Response<Long> persist(final byte[] p0);
    
    Response<byte[]> rpop(final byte[] p0);
    
    Response<Long> rpush(final byte[] p0, final byte[]... p1);
    
    Response<Long> rpushx(final byte[] p0, final byte[]... p1);
    
    Response<Long> sadd(final byte[] p0, final byte[]... p1);
    
    Response<Long> scard(final byte[] p0);
    
    Response<String> set(final byte[] p0, final byte[] p1);
    
    Response<Boolean> setbit(final byte[] p0, final long p1, final byte[] p2);
    
    Response<Long> setrange(final byte[] p0, final long p1, final byte[] p2);
    
    Response<String> setex(final byte[] p0, final int p1, final byte[] p2);
    
    Response<Long> setnx(final byte[] p0, final byte[] p1);
    
    Response<Long> setrange(final String p0, final long p1, final String p2);
    
    Response<Set<byte[]>> smembers(final byte[] p0);
    
    Response<Boolean> sismember(final byte[] p0, final byte[] p1);
    
    Response<List<byte[]>> sort(final byte[] p0);
    
    Response<List<byte[]>> sort(final byte[] p0, final SortingParams p1);
    
    Response<byte[]> spop(final byte[] p0);
    
    Response<Set<byte[]>> spop(final byte[] p0, final long p1);
    
    Response<byte[]> srandmember(final byte[] p0);
    
    Response<Long> srem(final byte[] p0, final byte[]... p1);
    
    Response<Long> strlen(final byte[] p0);
    
    Response<String> substr(final byte[] p0, final int p1, final int p2);
    
    Response<Long> ttl(final byte[] p0);
    
    Response<String> type(final byte[] p0);
    
    Response<Long> zadd(final byte[] p0, final double p1, final byte[] p2);
    
    Response<Long> zcard(final byte[] p0);
    
    Response<Long> zcount(final byte[] p0, final double p1, final double p2);
    
    Response<Double> zincrby(final byte[] p0, final double p1, final byte[] p2);
    
    Response<Set<byte[]>> zrange(final byte[] p0, final long p1, final long p2);
    
    Response<Set<byte[]>> zrangeByScore(final byte[] p0, final double p1, final double p2);
    
    Response<Set<byte[]>> zrangeByScore(final byte[] p0, final byte[] p1, final byte[] p2);
    
    Response<Set<byte[]>> zrangeByScore(final byte[] p0, final double p1, final double p2, final int p3, final int p4);
    
    Response<Set<byte[]>> zrangeByScore(final byte[] p0, final byte[] p1, final byte[] p2, final int p3, final int p4);
    
    Response<Set<Tuple>> zrangeByScoreWithScores(final byte[] p0, final double p1, final double p2);
    
    Response<Set<Tuple>> zrangeByScoreWithScores(final byte[] p0, final byte[] p1, final byte[] p2);
    
    Response<Set<Tuple>> zrangeByScoreWithScores(final byte[] p0, final double p1, final double p2, final int p3, final int p4);
    
    Response<Set<Tuple>> zrangeByScoreWithScores(final byte[] p0, final byte[] p1, final byte[] p2, final int p3, final int p4);
    
    Response<Set<byte[]>> zrevrangeByScore(final byte[] p0, final double p1, final double p2);
    
    Response<Set<byte[]>> zrevrangeByScore(final byte[] p0, final byte[] p1, final byte[] p2);
    
    Response<Set<byte[]>> zrevrangeByScore(final byte[] p0, final double p1, final double p2, final int p3, final int p4);
    
    Response<Set<byte[]>> zrevrangeByScore(final byte[] p0, final byte[] p1, final byte[] p2, final int p3, final int p4);
    
    Response<Set<Tuple>> zrevrangeByScoreWithScores(final byte[] p0, final double p1, final double p2);
    
    Response<Set<Tuple>> zrevrangeByScoreWithScores(final byte[] p0, final byte[] p1, final byte[] p2);
    
    Response<Set<Tuple>> zrevrangeByScoreWithScores(final byte[] p0, final double p1, final double p2, final int p3, final int p4);
    
    Response<Set<Tuple>> zrevrangeByScoreWithScores(final byte[] p0, final byte[] p1, final byte[] p2, final int p3, final int p4);
    
    Response<Set<Tuple>> zrangeWithScores(final byte[] p0, final long p1, final long p2);
    
    Response<Long> zrank(final byte[] p0, final byte[] p1);
    
    Response<Long> zrem(final byte[] p0, final byte[]... p1);
    
    Response<Long> zremrangeByRank(final byte[] p0, final long p1, final long p2);
    
    Response<Long> zremrangeByScore(final byte[] p0, final double p1, final double p2);
    
    Response<Long> zremrangeByScore(final byte[] p0, final byte[] p1, final byte[] p2);
    
    Response<Set<byte[]>> zrevrange(final byte[] p0, final long p1, final long p2);
    
    Response<Set<Tuple>> zrevrangeWithScores(final byte[] p0, final long p1, final long p2);
    
    Response<Long> zrevrank(final byte[] p0, final byte[] p1);
    
    Response<Double> zscore(final byte[] p0, final byte[] p1);
    
    Response<Long> zlexcount(final byte[] p0, final byte[] p1, final byte[] p2);
    
    Response<Set<byte[]>> zrangeByLex(final byte[] p0, final byte[] p1, final byte[] p2);
    
    Response<Set<byte[]>> zrangeByLex(final byte[] p0, final byte[] p1, final byte[] p2, final int p3, final int p4);
    
    Response<Set<byte[]>> zrevrangeByLex(final byte[] p0, final byte[] p1, final byte[] p2);
    
    Response<Set<byte[]>> zrevrangeByLex(final byte[] p0, final byte[] p1, final byte[] p2, final int p3, final int p4);
    
    Response<Long> zremrangeByLex(final byte[] p0, final byte[] p1, final byte[] p2);
    
    Response<Long> bitcount(final byte[] p0);
    
    Response<Long> bitcount(final byte[] p0, final long p1, final long p2);
    
    Response<Long> pfadd(final byte[] p0, final byte[]... p1);
    
    Response<Long> pfcount(final byte[] p0);
}
