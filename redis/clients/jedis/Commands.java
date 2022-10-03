package redis.clients.jedis;

import java.util.Map;

public interface Commands
{
    void set(final String p0, final String p1);
    
    void set(final String p0, final String p1, final String p2, final String p3, final long p4);
    
    void get(final String p0);
    
    void exists(final String p0);
    
    void del(final String... p0);
    
    void type(final String p0);
    
    void keys(final String p0);
    
    void rename(final String p0, final String p1);
    
    void renamenx(final String p0, final String p1);
    
    void expire(final String p0, final int p1);
    
    void expireAt(final String p0, final long p1);
    
    void ttl(final String p0);
    
    void setbit(final String p0, final long p1, final boolean p2);
    
    void setbit(final String p0, final long p1, final String p2);
    
    void getbit(final String p0, final long p1);
    
    void setrange(final String p0, final long p1, final String p2);
    
    void getrange(final String p0, final long p1, final long p2);
    
    void move(final String p0, final int p1);
    
    void getSet(final String p0, final String p1);
    
    void mget(final String... p0);
    
    void setnx(final String p0, final String p1);
    
    void setex(final String p0, final int p1, final String p2);
    
    void mset(final String... p0);
    
    void msetnx(final String... p0);
    
    void decrBy(final String p0, final long p1);
    
    void decr(final String p0);
    
    void incrBy(final String p0, final long p1);
    
    void incrByFloat(final String p0, final double p1);
    
    void incr(final String p0);
    
    void append(final String p0, final String p1);
    
    void substr(final String p0, final int p1, final int p2);
    
    void hset(final String p0, final String p1, final String p2);
    
    void hget(final String p0, final String p1);
    
    void hsetnx(final String p0, final String p1, final String p2);
    
    void hmset(final String p0, final Map<String, String> p1);
    
    void hmget(final String p0, final String... p1);
    
    void hincrBy(final String p0, final String p1, final long p2);
    
    void hincrByFloat(final String p0, final String p1, final double p2);
    
    void hexists(final String p0, final String p1);
    
    void hdel(final String p0, final String... p1);
    
    void hlen(final String p0);
    
    void hkeys(final String p0);
    
    void hvals(final String p0);
    
    void hgetAll(final String p0);
    
    void rpush(final String p0, final String... p1);
    
    void lpush(final String p0, final String... p1);
    
    void llen(final String p0);
    
    void lrange(final String p0, final long p1, final long p2);
    
    void ltrim(final String p0, final long p1, final long p2);
    
    void lindex(final String p0, final long p1);
    
    void lset(final String p0, final long p1, final String p2);
    
    void lrem(final String p0, final long p1, final String p2);
    
    void lpop(final String p0);
    
    void rpop(final String p0);
    
    void rpoplpush(final String p0, final String p1);
    
    void sadd(final String p0, final String... p1);
    
    void smembers(final String p0);
    
    void srem(final String p0, final String... p1);
    
    void spop(final String p0);
    
    void spop(final String p0, final long p1);
    
    void smove(final String p0, final String p1, final String p2);
    
    void scard(final String p0);
    
    void sismember(final String p0, final String p1);
    
    void sinter(final String... p0);
    
    void sinterstore(final String p0, final String... p1);
    
    void sunion(final String... p0);
    
    void sunionstore(final String p0, final String... p1);
    
    void sdiff(final String... p0);
    
    void sdiffstore(final String p0, final String... p1);
    
    void srandmember(final String p0);
    
    void zadd(final String p0, final double p1, final String p2);
    
    void zadd(final String p0, final Map<String, Double> p1);
    
    void zrange(final String p0, final long p1, final long p2);
    
    void zrem(final String p0, final String... p1);
    
    void zincrby(final String p0, final double p1, final String p2);
    
    void zrank(final String p0, final String p1);
    
    void zrevrank(final String p0, final String p1);
    
    void zrevrange(final String p0, final long p1, final long p2);
    
    void zrangeWithScores(final String p0, final long p1, final long p2);
    
    void zrevrangeWithScores(final String p0, final long p1, final long p2);
    
    void zcard(final String p0);
    
    void zscore(final String p0, final String p1);
    
    void watch(final String... p0);
    
    void sort(final String p0);
    
    void sort(final String p0, final SortingParams p1);
    
    void blpop(final String[] p0);
    
    void sort(final String p0, final SortingParams p1, final String p2);
    
    void sort(final String p0, final String p1);
    
    void brpop(final String[] p0);
    
    void brpoplpush(final String p0, final String p1, final int p2);
    
    void zcount(final String p0, final double p1, final double p2);
    
    void zcount(final String p0, final String p1, final String p2);
    
    void zrangeByScore(final String p0, final double p1, final double p2);
    
    void zrangeByScore(final String p0, final String p1, final String p2);
    
    void zrangeByScore(final String p0, final double p1, final double p2, final int p3, final int p4);
    
    void zrangeByScoreWithScores(final String p0, final double p1, final double p2);
    
    void zrangeByScoreWithScores(final String p0, final double p1, final double p2, final int p3, final int p4);
    
    void zrangeByScoreWithScores(final String p0, final String p1, final String p2);
    
    void zrangeByScoreWithScores(final String p0, final String p1, final String p2, final int p3, final int p4);
    
    void zrevrangeByScore(final String p0, final double p1, final double p2);
    
    void zrevrangeByScore(final String p0, final String p1, final String p2);
    
    void zrevrangeByScore(final String p0, final double p1, final double p2, final int p3, final int p4);
    
    void zrevrangeByScoreWithScores(final String p0, final double p1, final double p2);
    
    void zrevrangeByScoreWithScores(final String p0, final double p1, final double p2, final int p3, final int p4);
    
    void zrevrangeByScoreWithScores(final String p0, final String p1, final String p2);
    
    void zrevrangeByScoreWithScores(final String p0, final String p1, final String p2, final int p3, final int p4);
    
    void zremrangeByRank(final String p0, final long p1, final long p2);
    
    void zremrangeByScore(final String p0, final double p1, final double p2);
    
    void zremrangeByScore(final String p0, final String p1, final String p2);
    
    void zunionstore(final String p0, final String... p1);
    
    void zunionstore(final String p0, final ZParams p1, final String... p2);
    
    void zinterstore(final String p0, final String... p1);
    
    void zinterstore(final String p0, final ZParams p1, final String... p2);
    
    void strlen(final String p0);
    
    void lpushx(final String p0, final String... p1);
    
    void persist(final String p0);
    
    void rpushx(final String p0, final String... p1);
    
    void echo(final String p0);
    
    void linsert(final String p0, final BinaryClient.LIST_POSITION p1, final String p2, final String p3);
    
    void bgrewriteaof();
    
    void bgsave();
    
    void lastsave();
    
    void save();
    
    void configSet(final String p0, final String p1);
    
    void configGet(final String p0);
    
    void configResetStat();
    
    void multi();
    
    void exec();
    
    void discard();
    
    void objectRefcount(final String p0);
    
    void objectIdletime(final String p0);
    
    void objectEncoding(final String p0);
    
    void bitcount(final String p0);
    
    void bitcount(final String p0, final long p1, final long p2);
    
    void bitop(final BitOP p0, final String p1, final String... p2);
    
    @Deprecated
    void scan(final int p0, final ScanParams p1);
    
    @Deprecated
    void hscan(final String p0, final int p1, final ScanParams p2);
    
    @Deprecated
    void sscan(final String p0, final int p1, final ScanParams p2);
    
    @Deprecated
    void zscan(final String p0, final int p1, final ScanParams p2);
    
    void scan(final String p0, final ScanParams p1);
    
    void hscan(final String p0, final String p1, final ScanParams p2);
    
    void sscan(final String p0, final String p1, final ScanParams p2);
    
    void zscan(final String p0, final String p1, final ScanParams p2);
    
    void waitReplicas(final int p0, final long p1);
}
