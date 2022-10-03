package redis.clients.jedis;

import java.util.Set;
import java.util.List;

public interface MultiKeyCommands
{
    Long del(final String... p0);
    
    List<String> blpop(final int p0, final String... p1);
    
    List<String> brpop(final int p0, final String... p1);
    
    List<String> blpop(final String... p0);
    
    List<String> brpop(final String... p0);
    
    Set<String> keys(final String p0);
    
    List<String> mget(final String... p0);
    
    String mset(final String... p0);
    
    Long msetnx(final String... p0);
    
    String rename(final String p0, final String p1);
    
    Long renamenx(final String p0, final String p1);
    
    String rpoplpush(final String p0, final String p1);
    
    Set<String> sdiff(final String... p0);
    
    Long sdiffstore(final String p0, final String... p1);
    
    Set<String> sinter(final String... p0);
    
    Long sinterstore(final String p0, final String... p1);
    
    Long smove(final String p0, final String p1, final String p2);
    
    Long sort(final String p0, final SortingParams p1, final String p2);
    
    Long sort(final String p0, final String p1);
    
    Set<String> sunion(final String... p0);
    
    Long sunionstore(final String p0, final String... p1);
    
    String watch(final String... p0);
    
    String unwatch();
    
    Long zinterstore(final String p0, final String... p1);
    
    Long zinterstore(final String p0, final ZParams p1, final String... p2);
    
    Long zunionstore(final String p0, final String... p1);
    
    Long zunionstore(final String p0, final ZParams p1, final String... p2);
    
    String brpoplpush(final String p0, final String p1, final int p2);
    
    Long publish(final String p0, final String p1);
    
    void subscribe(final JedisPubSub p0, final String... p1);
    
    void psubscribe(final JedisPubSub p0, final String... p1);
    
    String randomKey();
    
    Long bitop(final BitOP p0, final String p1, final String... p2);
    
    @Deprecated
    ScanResult<String> scan(final int p0);
    
    ScanResult<String> scan(final String p0);
    
    String pfmerge(final String p0, final String... p1);
    
    long pfcount(final String... p0);
}
