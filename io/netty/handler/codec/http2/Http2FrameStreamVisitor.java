package io.netty.handler.codec.http2;

public interface Http2FrameStreamVisitor
{
    boolean visit(final Http2FrameStream p0);
}
