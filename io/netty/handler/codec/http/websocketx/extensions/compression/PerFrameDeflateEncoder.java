package io.netty.handler.codec.http.websocketx.extensions.compression;

import io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionFilter;

class PerFrameDeflateEncoder extends DeflateEncoder
{
    PerFrameDeflateEncoder(final int compressionLevel, final int windowSize, final boolean noContext) {
        super(compressionLevel, windowSize, noContext, WebSocketExtensionFilter.NEVER_SKIP);
    }
    
    PerFrameDeflateEncoder(final int compressionLevel, final int windowSize, final boolean noContext, final WebSocketExtensionFilter extensionEncoderFilter) {
        super(compressionLevel, windowSize, noContext, extensionEncoderFilter);
    }
    
    @Override
    public boolean acceptOutboundMessage(final Object msg) throws Exception {
        if (!super.acceptOutboundMessage(msg)) {
            return false;
        }
        final WebSocketFrame wsFrame = (WebSocketFrame)msg;
        return !this.extensionEncoderFilter().mustSkip(wsFrame) && (msg instanceof TextWebSocketFrame || msg instanceof BinaryWebSocketFrame || msg instanceof ContinuationWebSocketFrame) && wsFrame.content().readableBytes() > 0 && (wsFrame.rsv() & 0x4) == 0x0;
    }
    
    @Override
    protected int rsv(final WebSocketFrame msg) {
        return msg.rsv() | 0x4;
    }
    
    @Override
    protected boolean removeFrameTail(final WebSocketFrame msg) {
        return true;
    }
}
