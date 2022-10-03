package io.netty.handler.codec.http.websocketx;

public class WebSocket07FrameDecoder extends WebSocket08FrameDecoder
{
    public WebSocket07FrameDecoder(final boolean expectMaskedFrames, final boolean allowExtensions, final int maxFramePayloadLength) {
        this(WebSocketDecoderConfig.newBuilder().expectMaskedFrames(expectMaskedFrames).allowExtensions(allowExtensions).maxFramePayloadLength(maxFramePayloadLength).build());
    }
    
    public WebSocket07FrameDecoder(final boolean expectMaskedFrames, final boolean allowExtensions, final int maxFramePayloadLength, final boolean allowMaskMismatch) {
        this(WebSocketDecoderConfig.newBuilder().expectMaskedFrames(expectMaskedFrames).allowExtensions(allowExtensions).maxFramePayloadLength(maxFramePayloadLength).allowMaskMismatch(allowMaskMismatch).build());
    }
    
    public WebSocket07FrameDecoder(final WebSocketDecoderConfig decoderConfig) {
        super(decoderConfig);
    }
}
