package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;

public interface Http2UnknownFrame extends Http2StreamFrame, ByteBufHolder
{
    Http2FrameStream stream();
    
    Http2UnknownFrame stream(final Http2FrameStream p0);
    
    byte frameType();
    
    Http2Flags flags();
    
    Http2UnknownFrame copy();
    
    Http2UnknownFrame duplicate();
    
    Http2UnknownFrame retainedDuplicate();
    
    Http2UnknownFrame replace(final ByteBuf p0);
    
    Http2UnknownFrame retain();
    
    Http2UnknownFrame retain(final int p0);
    
    Http2UnknownFrame touch();
    
    Http2UnknownFrame touch(final Object p0);
}
