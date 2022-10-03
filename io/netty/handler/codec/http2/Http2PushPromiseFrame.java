package io.netty.handler.codec.http2;

public interface Http2PushPromiseFrame extends Http2StreamFrame
{
    Http2StreamFrame pushStream(final Http2FrameStream p0);
    
    Http2FrameStream pushStream();
    
    Http2Headers http2Headers();
    
    int padding();
    
    int promisedStreamId();
    
    Http2PushPromiseFrame stream(final Http2FrameStream p0);
}
