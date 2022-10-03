package io.netty.handler.codec.http.websocketx.extensions.compression;

import java.util.List;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionFilter;

class PerMessageDeflateEncoder extends DeflateEncoder
{
    private boolean compressing;
    
    PerMessageDeflateEncoder(final int compressionLevel, final int windowSize, final boolean noContext) {
        super(compressionLevel, windowSize, noContext, WebSocketExtensionFilter.NEVER_SKIP);
    }
    
    PerMessageDeflateEncoder(final int compressionLevel, final int windowSize, final boolean noContext, final WebSocketExtensionFilter extensionEncoderFilter) {
        super(compressionLevel, windowSize, noContext, extensionEncoderFilter);
    }
    
    @Override
    public boolean acceptOutboundMessage(final Object msg) throws Exception {
        if (!super.acceptOutboundMessage(msg)) {
            return false;
        }
        final WebSocketFrame wsFrame = (WebSocketFrame)msg;
        if (!this.extensionEncoderFilter().mustSkip(wsFrame)) {
            return ((wsFrame instanceof TextWebSocketFrame || wsFrame instanceof BinaryWebSocketFrame) && (wsFrame.rsv() & 0x4) == 0x0) || (wsFrame instanceof ContinuationWebSocketFrame && this.compressing);
        }
        if (this.compressing) {
            throw new IllegalStateException("Cannot skip per message deflate encoder, compression in progress");
        }
        return false;
    }
    
    @Override
    protected int rsv(final WebSocketFrame msg) {
        return (msg instanceof TextWebSocketFrame || msg instanceof BinaryWebSocketFrame) ? (msg.rsv() | 0x4) : msg.rsv();
    }
    
    @Override
    protected boolean removeFrameTail(final WebSocketFrame msg) {
        return msg.isFinalFragment();
    }
    
    @Override
    protected void encode(final ChannelHandlerContext ctx, final WebSocketFrame msg, final List<Object> out) throws Exception {
        super.encode(ctx, msg, out);
        if (msg.isFinalFragment()) {
            this.compressing = false;
        }
        else if (msg instanceof TextWebSocketFrame || msg instanceof BinaryWebSocketFrame) {
            this.compressing = true;
        }
    }
}
