package redis.clients.jedis;

import java.util.Set;
import java.util.Map;
import java.util.List;

public interface RedisPipeline
{
    Response<Long> append(final String p0, final String p1);
    
    Response<List<String>> blpop(final String p0);
    
    Response<List<String>> brpop(final String p0);
    
    Response<Long> decr(final String p0);
    
    Response<Long> decrBy(final String p0, final long p1);
    
    Response<Long> del(final String p0);
    
    Response<String> echo(final String p0);
    
    Response<Boolean> exists(final String p0);
    
    Response<Long> expire(final String p0, final int p1);
    
    Response<Long> pexpire(final String p0, final long p1);
    
    Response<Long> expireAt(final String p0, final long p1);
    
    Response<Long> pexpireAt(final String p0, final long p1);
    
    Response<String> get(final String p0);
    
    Response<Boolean> getbit(final String p0, final long p1);
    
    Response<String> getrange(final String p0, final long p1, final long p2);
    
    Response<String> getSet(final String p0, final String p1);
    
    Response<Long> hdel(final String p0, final String... p1);
    
    Response<Boolean> hexists(final String p0, final String p1);
    
    Response<String> hget(final String p0, final String p1);
    
    Response<Map<String, String>> hgetAll(final String p0);
    
    Response<Long> hincrBy(final String p0, final String p1, final long p2);
    
    Response<Set<String>> hkeys(final String p0);
    
    Response<Long> hlen(final String p0);
    
    Response<List<String>> hmget(final String p0, final String... p1);
    
    Response<String> hmset(final String p0, final Map<String, String> p1);
    
    Response<Long> hset(final String p0, final String p1, final String p2);
    
    Response<Long> hsetnx(final String p0, final String p1, final String p2);
    
    Response<List<String>> hvals(final String p0);
    
    Response<Long> incr(final String p0);
    
    Response<Long> incrBy(final String p0, final long p1);
    
    Response<String> lindex(final String p0, final long p1);
    
    Response<Long> linsert(final String p0, final BinaryClient.LIST_POSITION p1, final String p2, final String p3);
    
    Response<Long> llen(final String p0);
    
    Response<String> lpop(final String p0);
    
    Response<Long> lpush(final String p0, final String... p1);
    
    Response<Long> lpushx(final String p0, final String... p1);
    
    Response<List<String>> lrange(final String p0, final long p1, final long p2);
    
    Response<Long> lrem(final String p0, final long p1, final String p2);
    
    Response<String> lset(final String p0, final long p1, final String p2);
    
    Response<String> ltrim(final String p0, final long p1, final long p2);
    
    Response<Long> move(final String p0, final int p1);
    
    Response<Long> persist(final String p0);
    
    Response<String> rpop(final String p0);
    
    Response<Long> rpush(final String p0, final String... p1);
    
    Response<Long> rpushx(final String p0, final String... p1);
    
    Response<Long> sadd(final String p0, final String... p1);
    
    Response<Long> scard(final String p0);
    
    Response<Boolean> sismember(final String p0, final String p1);
    
    Response<String> set(final String p0, final String p1);
    
    Response<Boolean> setbit(final String p0, final long p1, final boolean p2);
    
    Response<String> setex(final String p0, final int p1, final String p2);
    
    Response<Long> setnx(final String p0, final String p1);
    
    Response<Long> setrange(final String p0, final long p1, final String p2);
    
    Response<Set<String>> smembers(final String p0);
    
    Response<List<String>> sort(final String p0);
    
    Response<List<String>> sort(final String p0, final SortingParams p1);
    
    Response<String> spop(final String p0);
    
    Response<Set<String>> spop(final String p0, final long p1);
    
    Response<String> srandmember(final String p0);
    
    Response<Long> srem(final String p0, final String... p1);
    
    Response<Long> strlen(final String p0);
    
    Response<String> substr(final String p0, final int p1, final int p2);
    
    Response<Long> ttl(final String p0);
    
    Response<String> type(final String p0);
    
    Response<Long> zadd(final String p0, final double p1, final String p2);
    
    Response<Long> zcard(final String p0);
    
    Response<Long> zcount(final String p0, final double p1, final double p2);
    
    Response<Double> zincrby(final String p0, final double p1, final String p2);
    
    Response<Set<String>> zrange(final String p0, final long p1, final long p2);
    
    Response<Set<String>> zrangeByScore(final String p0, final double p1, final double p2);
    
    Response<Set<String>> zrangeByScore(final String p0, final String p1, final String p2);
    
    Response<Set<String>> zrangeByScore(final String p0, final double p1, final double p2, final int p3, final int p4);
    
    Response<Set<Tuple>> zrangeByScoreWithScores(final String p0, final double p1, final double p2);
    
    Response<Set<Tuple>> zrangeByScoreWithScores(final String p0, final double p1, final double p2, final int p3, final int p4);
    
    Response<Set<String>> zrevrangeByScore(final String p0, final double p1, final double p2);
    
    Response<Set<String>> zrevrangeByScore(final String p0, final String p1, final String p2);
    
    Response<Set<String>> zrevrangeByScore(final String p0, final double p1, final double p2, final int p3, final int p4);
    
    Response<Set<Tuple>> zrevrangeByScoreWithScores(final String p0, final double p1, final double p2);
    
    Response<Set<Tuple>> zrevrangeByScoreWithScores(final String p0, final double p1, final double p2, final int p3, final int p4);
    
    Response<Set<Tuple>> zrangeWithScores(final String p0, final long p1, final long p2);
    
    Response<Long> zrank(final String p0, final String p1);
    
    Response<Long> zrem(final String p0, final String... p1);
    
    Response<Long> zremrangeByRank(final String p0, final long p1, final long p2);
    
    Response<Long> zremrangeByScore(final String p0, final double p1, final double p2);
    
    Response<Set<String>> zrevrange(final String p0, final long p1, final long p2);
    
    Response<Set<Tuple>> zrevrangeWithScores(final String p0, final long p1, final long p2);
    
    Response<Long> zrevrank(final String p0, final String p1);
    
    Response<Double> zscore(final String p0, final String p1);
    
    Response<Long> zlexcount(final String p0, final String p1, final String p2);
    
    Response<Set<String>> zrangeByLex(final String p0, final String p1, final String p2);
    
    Response<Set<String>> zrangeByLex(final String p0, final String p1, final String p2, final int p3, final int p4);
    
    Response<Set<String>> zrevrangeByLex(final String p0, final String p1, final String p2);
    
    Response<Set<String>> zrevrangeByLex(final String p0, final String p1, final String p2, final int p3, final int p4);
    
    Response<Long> zremrangeByLex(final String p0, final String p1, final String p2);
    
    Response<Long> bitcount(final String p0);
    
    Response<Long> bitcount(final String p0, final long p1, final long p2);
    
    Response<Long> pfadd(final String p0, final String... p1);
    
    Response<Long> pfcount(final String p0);
}
