package io.netty.handler.codec.http2;

import io.netty.buffer.Unpooled;
import io.netty.buffer.ByteBufUtil;
import java.util.List;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.ObjectUtil;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.HttpServerUpgradeHandler;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.ByteToMessageDecoder;

public final class CleartextHttp2ServerUpgradeHandler extends ByteToMessageDecoder
{
    private static final ByteBuf CONNECTION_PREFACE;
    private final HttpServerCodec httpServerCodec;
    private final HttpServerUpgradeHandler httpServerUpgradeHandler;
    private final ChannelHandler http2ServerHandler;
    
    public CleartextHttp2ServerUpgradeHandler(final HttpServerCodec httpServerCodec, final HttpServerUpgradeHandler httpServerUpgradeHandler, final ChannelHandler http2ServerHandler) {
        this.httpServerCodec = ObjectUtil.checkNotNull(httpServerCodec, "httpServerCodec");
        this.httpServerUpgradeHandler = ObjectUtil.checkNotNull(httpServerUpgradeHandler, "httpServerUpgradeHandler");
        this.http2ServerHandler = ObjectUtil.checkNotNull(http2ServerHandler, "http2ServerHandler");
    }
    
    @Override
    public void handlerAdded(final ChannelHandlerContext ctx) throws Exception {
        ctx.pipeline().addAfter(ctx.name(), null, this.httpServerUpgradeHandler).addAfter(ctx.name(), null, this.httpServerCodec);
    }
    
    @Override
    protected void decode(final ChannelHandlerContext ctx, final ByteBuf in, final List<Object> out) throws Exception {
        final int prefaceLength = CleartextHttp2ServerUpgradeHandler.CONNECTION_PREFACE.readableBytes();
        final int bytesRead = Math.min(in.readableBytes(), prefaceLength);
        if (!ByteBufUtil.equals(CleartextHttp2ServerUpgradeHandler.CONNECTION_PREFACE, CleartextHttp2ServerUpgradeHandler.CONNECTION_PREFACE.readerIndex(), in, in.readerIndex(), bytesRead)) {
            ctx.pipeline().remove(this);
        }
        else if (bytesRead == prefaceLength) {
            ctx.pipeline().remove(this.httpServerCodec).remove(this.httpServerUpgradeHandler);
            ctx.pipeline().addAfter(ctx.name(), null, this.http2ServerHandler);
            ctx.pipeline().remove(this);
            ctx.fireUserEventTriggered((Object)PriorKnowledgeUpgradeEvent.INSTANCE);
        }
    }
    
    static {
        CONNECTION_PREFACE = Unpooled.unreleasableBuffer(Http2CodecUtil.connectionPrefaceBuf());
    }
    
    public static final class PriorKnowledgeUpgradeEvent
    {
        private static final PriorKnowledgeUpgradeEvent INSTANCE;
        
        private PriorKnowledgeUpgradeEvent() {
        }
        
        static {
            INSTANCE = new PriorKnowledgeUpgradeEvent();
        }
    }
}
