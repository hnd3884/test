package io.netty.handler.codec.http2;

import io.netty.channel.ChannelHandler;
import io.netty.util.internal.StringUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelDuplexHandler;

public abstract class Http2ChannelDuplexHandler extends ChannelDuplexHandler
{
    private volatile Http2FrameCodec frameCodec;
    
    @Override
    public final void handlerAdded(final ChannelHandlerContext ctx) throws Exception {
        this.frameCodec = requireHttp2FrameCodec(ctx);
        this.handlerAdded0(ctx);
    }
    
    protected void handlerAdded0(final ChannelHandlerContext ctx) throws Exception {
    }
    
    @Override
    public final void handlerRemoved(final ChannelHandlerContext ctx) throws Exception {
        try {
            this.handlerRemoved0(ctx);
        }
        finally {
            this.frameCodec = null;
        }
    }
    
    protected void handlerRemoved0(final ChannelHandlerContext ctx) throws Exception {
    }
    
    public final Http2FrameStream newStream() {
        final Http2FrameCodec codec = this.frameCodec;
        if (codec == null) {
            throw new IllegalStateException(StringUtil.simpleClassName(Http2FrameCodec.class) + " not found. Has the handler been added to a pipeline?");
        }
        return codec.newStream();
    }
    
    protected final void forEachActiveStream(final Http2FrameStreamVisitor streamVisitor) throws Http2Exception {
        this.frameCodec.forEachActiveStream(streamVisitor);
    }
    
    private static Http2FrameCodec requireHttp2FrameCodec(final ChannelHandlerContext ctx) {
        final ChannelHandlerContext frameCodecCtx = ctx.pipeline().context(Http2FrameCodec.class);
        if (frameCodecCtx == null) {
            throw new IllegalArgumentException(Http2FrameCodec.class.getSimpleName() + " was not found in the channel pipeline.");
        }
        return (Http2FrameCodec)frameCodecCtx.handler();
    }
}
