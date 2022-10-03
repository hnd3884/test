package io.netty.handler.codec.http2;

public interface Http2StreamFrame extends Http2Frame
{
    Http2StreamFrame stream(final Http2FrameStream p0);
    
    Http2FrameStream stream();
}
