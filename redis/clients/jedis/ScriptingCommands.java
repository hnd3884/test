package redis.clients.jedis;

import java.util.List;

public interface ScriptingCommands
{
    Object eval(final String p0, final int p1, final String... p2);
    
    Object eval(final String p0, final List<String> p1, final List<String> p2);
    
    Object eval(final String p0);
    
    Object evalsha(final String p0);
    
    Object evalsha(final String p0, final List<String> p1, final List<String> p2);
    
    Object evalsha(final String p0, final int p1, final String... p2);
    
    Boolean scriptExists(final String p0);
    
    List<Boolean> scriptExists(final String... p0);
    
    String scriptLoad(final String p0);
}
