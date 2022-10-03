package io.netty.handler.codec.http2;

public interface Http2FrameSizePolicy
{
    void maxFrameSize(final int p0) throws Http2Exception;
    
    int maxFrameSize();
}
