package redis.clients.jedis;

import java.util.Map;
import java.util.List;

public interface SentinelCommands
{
    List<Map<String, String>> sentinelMasters();
    
    List<String> sentinelGetMasterAddrByName(final String p0);
    
    Long sentinelReset(final String p0);
    
    List<Map<String, String>> sentinelSlaves(final String p0);
    
    String sentinelFailover(final String p0);
    
    String sentinelMonitor(final String p0, final String p1, final int p2, final int p3);
    
    String sentinelRemove(final String p0);
    
    String sentinelSet(final String p0, final Map<String, String> p1);
}
