package redis.clients.jedis;

import java.util.List;

public interface BinaryScriptingCommands
{
    Object eval(final byte[] p0, final byte[] p1, final byte[]... p2);
    
    Object eval(final byte[] p0, final int p1, final byte[]... p2);
    
    Object eval(final byte[] p0, final List<byte[]> p1, final List<byte[]> p2);
    
    Object eval(final byte[] p0);
    
    Object evalsha(final byte[] p0);
    
    Object evalsha(final byte[] p0, final List<byte[]> p1, final List<byte[]> p2);
    
    Object evalsha(final byte[] p0, final int p1, final byte[]... p2);
    
    List<Long> scriptExists(final byte[]... p0);
    
    byte[] scriptLoad(final byte[] p0);
    
    String scriptFlush();
    
    String scriptKill();
}
