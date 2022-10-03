package io.netty.handler.codec.http.websocketx;

import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import java.net.URI;
import io.netty.util.internal.logging.InternalLogger;

public class WebSocketClientHandshaker07 extends WebSocketClientHandshaker
{
    private static final InternalLogger logger;
    public static final String MAGIC_GUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
    private String expectedChallengeResponseString;
    private final boolean allowExtensions;
    private final boolean performMasking;
    private final boolean allowMaskMismatch;
    
    public WebSocketClientHandshaker07(final URI webSocketURL, final WebSocketVersion version, final String subprotocol, final boolean allowExtensions, final HttpHeaders customHeaders, final int maxFramePayloadLength) {
        this(webSocketURL, version, subprotocol, allowExtensions, customHeaders, maxFramePayloadLength, true, false);
    }
    
    public WebSocketClientHandshaker07(final URI webSocketURL, final WebSocketVersion version, final String subprotocol, final boolean allowExtensions, final HttpHeaders customHeaders, final int maxFramePayloadLength, final boolean performMasking, final boolean allowMaskMismatch) {
        this(webSocketURL, version, subprotocol, allowExtensions, customHeaders, maxFramePayloadLength, performMasking, allowMaskMismatch, 10000L);
    }
    
    public WebSocketClientHandshaker07(final URI webSocketURL, final WebSocketVersion version, final String subprotocol, final boolean allowExtensions, final HttpHeaders customHeaders, final int maxFramePayloadLength, final boolean performMasking, final boolean allowMaskMismatch, final long forceCloseTimeoutMillis) {
        this(webSocketURL, version, subprotocol, allowExtensions, customHeaders, maxFramePayloadLength, performMasking, allowMaskMismatch, forceCloseTimeoutMillis, false);
    }
    
    WebSocketClientHandshaker07(final URI webSocketURL, final WebSocketVersion version, final String subprotocol, final boolean allowExtensions, final HttpHeaders customHeaders, final int maxFramePayloadLength, final boolean performMasking, final boolean allowMaskMismatch, final long forceCloseTimeoutMillis, final boolean absoluteUpgradeUrl) {
        super(webSocketURL, version, subprotocol, customHeaders, maxFramePayloadLength, forceCloseTimeoutMillis, absoluteUpgradeUrl);
        this.allowExtensions = allowExtensions;
        this.performMasking = performMasking;
        this.allowMaskMismatch = allowMaskMismatch;
    }
    
    @Override
    protected FullHttpRequest newHandshakeRequest() {
        final URI wsURL = this.uri();
        final byte[] nonce = WebSocketUtil.randomBytes(16);
        final String key = WebSocketUtil.base64(nonce);
        final String acceptSeed = key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
        final byte[] sha1 = WebSocketUtil.sha1(acceptSeed.getBytes(CharsetUtil.US_ASCII));
        this.expectedChallengeResponseString = WebSocketUtil.base64(sha1);
        if (WebSocketClientHandshaker07.logger.isDebugEnabled()) {
            WebSocketClientHandshaker07.logger.debug("WebSocket version 07 client handshake key: {}, expected response: {}", key, this.expectedChallengeResponseString);
        }
        final FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, this.upgradeUrl(wsURL), Unpooled.EMPTY_BUFFER);
        final HttpHeaders headers = request.headers();
        if (this.customHeaders != null) {
            headers.add(this.customHeaders);
            if (!headers.contains(HttpHeaderNames.HOST)) {
                headers.set(HttpHeaderNames.HOST, WebSocketClientHandshaker.websocketHostValue(wsURL));
            }
        }
        else {
            headers.set(HttpHeaderNames.HOST, WebSocketClientHandshaker.websocketHostValue(wsURL));
        }
        headers.set(HttpHeaderNames.UPGRADE, HttpHeaderValues.WEBSOCKET).set(HttpHeaderNames.CONNECTION, HttpHeaderValues.UPGRADE).set(HttpHeaderNames.SEC_WEBSOCKET_KEY, key);
        if (!headers.contains(HttpHeaderNames.SEC_WEBSOCKET_ORIGIN)) {
            headers.set(HttpHeaderNames.SEC_WEBSOCKET_ORIGIN, WebSocketClientHandshaker.websocketOriginValue(wsURL));
        }
        final String expectedSubprotocol = this.expectedSubprotocol();
        if (expectedSubprotocol != null && !expectedSubprotocol.isEmpty()) {
            headers.set(HttpHeaderNames.SEC_WEBSOCKET_PROTOCOL, expectedSubprotocol);
        }
        headers.set(HttpHeaderNames.SEC_WEBSOCKET_VERSION, this.version().toAsciiString());
        return request;
    }
    
    @Override
    protected void verify(final FullHttpResponse response) {
        final HttpResponseStatus status = response.status();
        if (!HttpResponseStatus.SWITCHING_PROTOCOLS.equals(status)) {
            throw new WebSocketClientHandshakeException("Invalid handshake response getStatus: " + status, response);
        }
        final HttpHeaders headers = response.headers();
        final CharSequence upgrade = headers.get(HttpHeaderNames.UPGRADE);
        if (!HttpHeaderValues.WEBSOCKET.contentEqualsIgnoreCase(upgrade)) {
            throw new WebSocketClientHandshakeException("Invalid handshake response upgrade: " + (Object)upgrade, response);
        }
        if (!headers.containsValue(HttpHeaderNames.CONNECTION, HttpHeaderValues.UPGRADE, true)) {
            throw new WebSocketClientHandshakeException("Invalid handshake response connection: " + headers.get(HttpHeaderNames.CONNECTION), response);
        }
        final CharSequence accept = headers.get(HttpHeaderNames.SEC_WEBSOCKET_ACCEPT);
        if (accept == null || !accept.equals(this.expectedChallengeResponseString)) {
            throw new WebSocketClientHandshakeException(String.format("Invalid challenge. Actual: %s. Expected: %s", accept, this.expectedChallengeResponseString), response);
        }
    }
    
    @Override
    protected WebSocketFrameDecoder newWebsocketDecoder() {
        return new WebSocket07FrameDecoder(false, this.allowExtensions, this.maxFramePayloadLength(), this.allowMaskMismatch);
    }
    
    @Override
    protected WebSocketFrameEncoder newWebSocketEncoder() {
        return new WebSocket07FrameEncoder(this.performMasking);
    }
    
    @Override
    public WebSocketClientHandshaker07 setForceCloseTimeoutMillis(final long forceCloseTimeoutMillis) {
        super.setForceCloseTimeoutMillis(forceCloseTimeoutMillis);
        return this;
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(WebSocketClientHandshaker07.class);
    }
}
