package redis.clients.jedis;

import java.util.List;

public interface BasicRedisPipeline
{
    Response<String> bgrewriteaof();
    
    Response<String> bgsave();
    
    Response<List<String>> configGet(final String p0);
    
    Response<String> configSet(final String p0, final String p1);
    
    Response<String> configResetStat();
    
    Response<String> save();
    
    Response<Long> lastsave();
    
    Response<String> flushDB();
    
    Response<String> flushAll();
    
    Response<String> info();
    
    Response<List<String>> time();
    
    Response<Long> dbSize();
    
    Response<String> shutdown();
    
    Response<String> ping();
    
    Response<String> select(final int p0);
}
