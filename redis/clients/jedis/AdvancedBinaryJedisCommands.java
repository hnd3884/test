package redis.clients.jedis;

import java.util.List;

public interface AdvancedBinaryJedisCommands
{
    List<byte[]> configGet(final byte[] p0);
    
    byte[] configSet(final byte[] p0, final byte[] p1);
    
    String slowlogReset();
    
    Long slowlogLen();
    
    List<byte[]> slowlogGetBinary();
    
    List<byte[]> slowlogGetBinary(final long p0);
    
    Long objectRefcount(final byte[] p0);
    
    byte[] objectEncoding(final byte[] p0);
    
    Long objectIdletime(final byte[] p0);
}
