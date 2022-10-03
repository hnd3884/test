package io.netty.handler.codec.http.websocketx.extensions.compression;

import java.util.List;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionFilter;

class PerMessageDeflateDecoder extends DeflateDecoder
{
    private boolean compressing;
    
    PerMessageDeflateDecoder(final boolean noContext) {
        super(noContext, WebSocketExtensionFilter.NEVER_SKIP);
    }
    
    PerMessageDeflateDecoder(final boolean noContext, final WebSocketExtensionFilter extensionDecoderFilter) {
        super(noContext, extensionDecoderFilter);
    }
    
    @Override
    public boolean acceptInboundMessage(final Object msg) throws Exception {
        if (!super.acceptInboundMessage(msg)) {
            return false;
        }
        final WebSocketFrame wsFrame = (WebSocketFrame)msg;
        if (!this.extensionDecoderFilter().mustSkip(wsFrame)) {
            return ((wsFrame instanceof TextWebSocketFrame || wsFrame instanceof BinaryWebSocketFrame) && (wsFrame.rsv() & 0x4) > 0) || (wsFrame instanceof ContinuationWebSocketFrame && this.compressing);
        }
        if (this.compressing) {
            throw new IllegalStateException("Cannot skip per message deflate decoder, compression in progress");
        }
        return false;
    }
    
    @Override
    protected int newRsv(final WebSocketFrame msg) {
        return ((msg.rsv() & 0x4) > 0) ? (msg.rsv() ^ 0x4) : msg.rsv();
    }
    
    @Override
    protected boolean appendFrameTail(final WebSocketFrame msg) {
        return msg.isFinalFragment();
    }
    
    @Override
    protected void decode(final ChannelHandlerContext ctx, final WebSocketFrame msg, final List<Object> out) throws Exception {
        super.decode(ctx, msg, out);
        if (msg.isFinalFragment()) {
            this.compressing = false;
        }
        else if (msg instanceof TextWebSocketFrame || msg instanceof BinaryWebSocketFrame) {
            this.compressing = true;
        }
    }
}
