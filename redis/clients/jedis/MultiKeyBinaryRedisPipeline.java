package redis.clients.jedis;

import java.util.Set;
import java.util.List;

public interface MultiKeyBinaryRedisPipeline
{
    Response<Long> del(final byte[]... p0);
    
    Response<List<byte[]>> blpop(final byte[]... p0);
    
    Response<List<byte[]>> brpop(final byte[]... p0);
    
    Response<Set<byte[]>> keys(final byte[] p0);
    
    Response<List<byte[]>> mget(final byte[]... p0);
    
    Response<String> mset(final byte[]... p0);
    
    Response<Long> msetnx(final byte[]... p0);
    
    Response<String> rename(final byte[] p0, final byte[] p1);
    
    Response<Long> renamenx(final byte[] p0, final byte[] p1);
    
    Response<byte[]> rpoplpush(final byte[] p0, final byte[] p1);
    
    Response<Set<byte[]>> sdiff(final byte[]... p0);
    
    Response<Long> sdiffstore(final byte[] p0, final byte[]... p1);
    
    Response<Set<byte[]>> sinter(final byte[]... p0);
    
    Response<Long> sinterstore(final byte[] p0, final byte[]... p1);
    
    Response<Long> smove(final byte[] p0, final byte[] p1, final byte[] p2);
    
    Response<Long> sort(final byte[] p0, final SortingParams p1, final byte[] p2);
    
    Response<Long> sort(final byte[] p0, final byte[] p1);
    
    Response<Set<byte[]>> sunion(final byte[]... p0);
    
    Response<Long> sunionstore(final byte[] p0, final byte[]... p1);
    
    Response<String> watch(final byte[]... p0);
    
    Response<Long> zinterstore(final byte[] p0, final byte[]... p1);
    
    Response<Long> zinterstore(final byte[] p0, final ZParams p1, final byte[]... p2);
    
    Response<Long> zunionstore(final byte[] p0, final byte[]... p1);
    
    Response<Long> zunionstore(final byte[] p0, final ZParams p1, final byte[]... p2);
    
    Response<byte[]> brpoplpush(final byte[] p0, final byte[] p1, final int p2);
    
    Response<Long> publish(final byte[] p0, final byte[] p1);
    
    Response<byte[]> randomKeyBinary();
    
    Response<Long> bitop(final BitOP p0, final byte[] p1, final byte[]... p2);
    
    Response<String> pfmerge(final byte[] p0, final byte[]... p1);
    
    Response<Long> pfcount(final byte[]... p0);
}
