package redis.clients.jedis;

import java.util.Set;
import java.util.List;

public interface MultiKeyBinaryCommands
{
    Long del(final byte[]... p0);
    
    List<byte[]> blpop(final int p0, final byte[]... p1);
    
    List<byte[]> brpop(final int p0, final byte[]... p1);
    
    List<byte[]> blpop(final byte[]... p0);
    
    List<byte[]> brpop(final byte[]... p0);
    
    Set<byte[]> keys(final byte[] p0);
    
    List<byte[]> mget(final byte[]... p0);
    
    String mset(final byte[]... p0);
    
    Long msetnx(final byte[]... p0);
    
    String rename(final byte[] p0, final byte[] p1);
    
    Long renamenx(final byte[] p0, final byte[] p1);
    
    byte[] rpoplpush(final byte[] p0, final byte[] p1);
    
    Set<byte[]> sdiff(final byte[]... p0);
    
    Long sdiffstore(final byte[] p0, final byte[]... p1);
    
    Set<byte[]> sinter(final byte[]... p0);
    
    Long sinterstore(final byte[] p0, final byte[]... p1);
    
    Long smove(final byte[] p0, final byte[] p1, final byte[] p2);
    
    Long sort(final byte[] p0, final SortingParams p1, final byte[] p2);
    
    Long sort(final byte[] p0, final byte[] p1);
    
    Set<byte[]> sunion(final byte[]... p0);
    
    Long sunionstore(final byte[] p0, final byte[]... p1);
    
    String watch(final byte[]... p0);
    
    String unwatch();
    
    Long zinterstore(final byte[] p0, final byte[]... p1);
    
    Long zinterstore(final byte[] p0, final ZParams p1, final byte[]... p2);
    
    Long zunionstore(final byte[] p0, final byte[]... p1);
    
    Long zunionstore(final byte[] p0, final ZParams p1, final byte[]... p2);
    
    byte[] brpoplpush(final byte[] p0, final byte[] p1, final int p2);
    
    Long publish(final byte[] p0, final byte[] p1);
    
    void subscribe(final BinaryJedisPubSub p0, final byte[]... p1);
    
    void psubscribe(final BinaryJedisPubSub p0, final byte[]... p1);
    
    byte[] randomBinaryKey();
    
    Long bitop(final BitOP p0, final byte[] p1, final byte[]... p2);
    
    String pfmerge(final byte[] p0, final byte[]... p1);
    
    Long pfcount(final byte[]... p0);
}
