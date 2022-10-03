package io.netty.handler.codec.http.websocketx.extensions.compression;

import io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionFilter;

class PerFrameDeflateDecoder extends DeflateDecoder
{
    PerFrameDeflateDecoder(final boolean noContext) {
        super(noContext, WebSocketExtensionFilter.NEVER_SKIP);
    }
    
    PerFrameDeflateDecoder(final boolean noContext, final WebSocketExtensionFilter extensionDecoderFilter) {
        super(noContext, extensionDecoderFilter);
    }
    
    @Override
    public boolean acceptInboundMessage(final Object msg) throws Exception {
        if (!super.acceptInboundMessage(msg)) {
            return false;
        }
        final WebSocketFrame wsFrame = (WebSocketFrame)msg;
        return !this.extensionDecoderFilter().mustSkip(wsFrame) && (msg instanceof TextWebSocketFrame || msg instanceof BinaryWebSocketFrame || msg instanceof ContinuationWebSocketFrame) && (wsFrame.rsv() & 0x4) > 0;
    }
    
    @Override
    protected int newRsv(final WebSocketFrame msg) {
        return msg.rsv() ^ 0x4;
    }
    
    @Override
    protected boolean appendFrameTail(final WebSocketFrame msg) {
        return true;
    }
}
