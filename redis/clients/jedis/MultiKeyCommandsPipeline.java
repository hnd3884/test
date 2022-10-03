package redis.clients.jedis;

import java.util.Set;
import java.util.List;

public interface MultiKeyCommandsPipeline
{
    Response<Long> del(final String... p0);
    
    Response<List<String>> blpop(final String... p0);
    
    Response<List<String>> brpop(final String... p0);
    
    Response<Set<String>> keys(final String p0);
    
    Response<List<String>> mget(final String... p0);
    
    Response<String> mset(final String... p0);
    
    Response<Long> msetnx(final String... p0);
    
    Response<String> rename(final String p0, final String p1);
    
    Response<Long> renamenx(final String p0, final String p1);
    
    Response<String> rpoplpush(final String p0, final String p1);
    
    Response<Set<String>> sdiff(final String... p0);
    
    Response<Long> sdiffstore(final String p0, final String... p1);
    
    Response<Set<String>> sinter(final String... p0);
    
    Response<Long> sinterstore(final String p0, final String... p1);
    
    Response<Long> smove(final String p0, final String p1, final String p2);
    
    Response<Long> sort(final String p0, final SortingParams p1, final String p2);
    
    Response<Long> sort(final String p0, final String p1);
    
    Response<Set<String>> sunion(final String... p0);
    
    Response<Long> sunionstore(final String p0, final String... p1);
    
    Response<String> watch(final String... p0);
    
    Response<Long> zinterstore(final String p0, final String... p1);
    
    Response<Long> zinterstore(final String p0, final ZParams p1, final String... p2);
    
    Response<Long> zunionstore(final String p0, final String... p1);
    
    Response<Long> zunionstore(final String p0, final ZParams p1, final String... p2);
    
    Response<String> brpoplpush(final String p0, final String p1, final int p2);
    
    Response<Long> publish(final String p0, final String p1);
    
    Response<String> randomKey();
    
    Response<Long> bitop(final BitOP p0, final String p1, final String... p2);
    
    Response<String> pfmerge(final String p0, final String... p1);
    
    Response<Long> pfcount(final String... p0);
}
