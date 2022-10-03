package redis.clients.jedis;

public interface BasicCommands
{
    String ping();
    
    String quit();
    
    String flushDB();
    
    Long dbSize();
    
    String select(final int p0);
    
    String flushAll();
    
    String auth(final String p0);
    
    String save();
    
    String bgsave();
    
    String bgrewriteaof();
    
    Long lastsave();
    
    String shutdown();
    
    String info();
    
    String info(final String p0);
    
    String slaveof(final String p0, final int p1);
    
    String slaveofNoOne();
    
    Long getDB();
    
    String debug(final DebugParams p0);
    
    String configResetStat();
    
    Long waitReplicas(final int p0, final long p1);
}
