package redis.clients.jedis;

import redis.clients.util.Slowlog;
import java.util.List;

public interface AdvancedJedisCommands
{
    List<String> configGet(final String p0);
    
    String configSet(final String p0, final String p1);
    
    String slowlogReset();
    
    Long slowlogLen();
    
    List<Slowlog> slowlogGet();
    
    List<Slowlog> slowlogGet(final long p0);
    
    Long objectRefcount(final String p0);
    
    String objectEncoding(final String p0);
    
    Long objectIdletime(final String p0);
}
