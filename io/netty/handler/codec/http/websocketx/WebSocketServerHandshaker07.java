package io.netty.handler.codec.http.websocketx;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.util.CharsetUtil;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.FullHttpRequest;

public class WebSocketServerHandshaker07 extends WebSocketServerHandshaker
{
    public static final String WEBSOCKET_07_ACCEPT_GUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
    
    public WebSocketServerHandshaker07(final String webSocketURL, final String subprotocols, final boolean allowExtensions, final int maxFramePayloadLength) {
        this(webSocketURL, subprotocols, allowExtensions, maxFramePayloadLength, false);
    }
    
    public WebSocketServerHandshaker07(final String webSocketURL, final String subprotocols, final boolean allowExtensions, final int maxFramePayloadLength, final boolean allowMaskMismatch) {
        this(webSocketURL, subprotocols, WebSocketDecoderConfig.newBuilder().allowExtensions(allowExtensions).maxFramePayloadLength(maxFramePayloadLength).allowMaskMismatch(allowMaskMismatch).build());
    }
    
    public WebSocketServerHandshaker07(final String webSocketURL, final String subprotocols, final WebSocketDecoderConfig decoderConfig) {
        super(WebSocketVersion.V07, webSocketURL, subprotocols, decoderConfig);
    }
    
    @Override
    protected FullHttpResponse newHandshakeResponse(final FullHttpRequest req, final HttpHeaders headers) {
        final CharSequence key = req.headers().get(HttpHeaderNames.SEC_WEBSOCKET_KEY);
        if (key == null) {
            throw new WebSocketServerHandshakeException("not a WebSocket request: missing key", req);
        }
        final FullHttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.SWITCHING_PROTOCOLS, req.content().alloc().buffer(0));
        if (headers != null) {
            res.headers().add(headers);
        }
        final String acceptSeed = (Object)key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
        final byte[] sha1 = WebSocketUtil.sha1(acceptSeed.getBytes(CharsetUtil.US_ASCII));
        final String accept = WebSocketUtil.base64(sha1);
        if (WebSocketServerHandshaker07.logger.isDebugEnabled()) {
            WebSocketServerHandshaker07.logger.debug("WebSocket version 07 server handshake key: {}, response: {}.", key, accept);
        }
        res.headers().set(HttpHeaderNames.UPGRADE, HttpHeaderValues.WEBSOCKET).set(HttpHeaderNames.CONNECTION, HttpHeaderValues.UPGRADE).set(HttpHeaderNames.SEC_WEBSOCKET_ACCEPT, accept);
        final String subprotocols = req.headers().get(HttpHeaderNames.SEC_WEBSOCKET_PROTOCOL);
        if (subprotocols != null) {
            final String selectedSubprotocol = this.selectSubprotocol(subprotocols);
            if (selectedSubprotocol == null) {
                if (WebSocketServerHandshaker07.logger.isDebugEnabled()) {
                    WebSocketServerHandshaker07.logger.debug("Requested subprotocol(s) not supported: {}", subprotocols);
                }
            }
            else {
                res.headers().set(HttpHeaderNames.SEC_WEBSOCKET_PROTOCOL, selectedSubprotocol);
            }
        }
        return res;
    }
    
    @Override
    protected WebSocketFrameDecoder newWebsocketDecoder() {
        return new WebSocket07FrameDecoder(this.decoderConfig());
    }
    
    @Override
    protected WebSocketFrameEncoder newWebSocketEncoder() {
        return new WebSocket07FrameEncoder(false);
    }
}
