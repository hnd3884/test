package sun.misc;

import java.nio.Buffer;
import java.nio.ByteBuffer;

public interface JavaNioAccess
{
    BufferPool getDirectBufferPool();
    
    ByteBuffer newDirectByteBuffer(final long p0, final int p1, final Object p2);
    
    void truncate(final Buffer p0);
    
    public interface BufferPool
    {
        String getName();
        
        long getCount();
        
        long getTotalCapacity();
        
        long getMemoryUsed();
    }
}
