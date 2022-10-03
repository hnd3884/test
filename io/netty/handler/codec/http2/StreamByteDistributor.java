package io.netty.handler.codec.http2;

public interface StreamByteDistributor
{
    void updateStreamableBytes(final StreamState p0);
    
    void updateDependencyTree(final int p0, final int p1, final short p2, final boolean p3);
    
    boolean distribute(final int p0, final Writer p1) throws Http2Exception;
    
    public interface StreamState
    {
        Http2Stream stream();
        
        long pendingBytes();
        
        boolean hasFrame();
        
        int windowSize();
    }
    
    public interface Writer
    {
        void write(final Http2Stream p0, final int p1);
    }
}
