package io.netty.handler.codec.http.websocketx;

import io.netty.util.internal.PlatformDependent;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.buffer.Unpooled;
import java.nio.ByteBuffer;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import java.net.URI;
import io.netty.buffer.ByteBuf;

public class WebSocketClientHandshaker00 extends WebSocketClientHandshaker
{
    private ByteBuf expectedChallengeResponseBytes;
    
    public WebSocketClientHandshaker00(final URI webSocketURL, final WebSocketVersion version, final String subprotocol, final HttpHeaders customHeaders, final int maxFramePayloadLength) {
        this(webSocketURL, version, subprotocol, customHeaders, maxFramePayloadLength, 10000L);
    }
    
    public WebSocketClientHandshaker00(final URI webSocketURL, final WebSocketVersion version, final String subprotocol, final HttpHeaders customHeaders, final int maxFramePayloadLength, final long forceCloseTimeoutMillis) {
        this(webSocketURL, version, subprotocol, customHeaders, maxFramePayloadLength, forceCloseTimeoutMillis, false);
    }
    
    WebSocketClientHandshaker00(final URI webSocketURL, final WebSocketVersion version, final String subprotocol, final HttpHeaders customHeaders, final int maxFramePayloadLength, final long forceCloseTimeoutMillis, final boolean absoluteUpgradeUrl) {
        super(webSocketURL, version, subprotocol, customHeaders, maxFramePayloadLength, forceCloseTimeoutMillis, absoluteUpgradeUrl);
    }
    
    @Override
    protected FullHttpRequest newHandshakeRequest() {
        final int spaces1 = WebSocketUtil.randomNumber(1, 12);
        final int spaces2 = WebSocketUtil.randomNumber(1, 12);
        final int max1 = Integer.MAX_VALUE / spaces1;
        final int max2 = Integer.MAX_VALUE / spaces2;
        final int number1 = WebSocketUtil.randomNumber(0, max1);
        final int number2 = WebSocketUtil.randomNumber(0, max2);
        final int product1 = number1 * spaces1;
        final int product2 = number2 * spaces2;
        String key1 = Integer.toString(product1);
        String key2 = Integer.toString(product2);
        key1 = insertRandomCharacters(key1);
        key2 = insertRandomCharacters(key2);
        key1 = insertSpaces(key1, spaces1);
        key2 = insertSpaces(key2, spaces2);
        final byte[] key3 = WebSocketUtil.randomBytes(8);
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putInt(number1);
        final byte[] number1Array = buffer.array();
        buffer = ByteBuffer.allocate(4);
        buffer.putInt(number2);
        final byte[] number2Array = buffer.array();
        final byte[] challenge = new byte[16];
        System.arraycopy(number1Array, 0, challenge, 0, 4);
        System.arraycopy(number2Array, 0, challenge, 4, 4);
        System.arraycopy(key3, 0, challenge, 8, 8);
        this.expectedChallengeResponseBytes = Unpooled.wrappedBuffer(WebSocketUtil.md5(challenge));
        final URI wsURL = this.uri();
        final FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, this.upgradeUrl(wsURL), Unpooled.wrappedBuffer(key3));
        final HttpHeaders headers = request.headers();
        if (this.customHeaders != null) {
            headers.add(this.customHeaders);
        }
        headers.set(HttpHeaderNames.UPGRADE, HttpHeaderValues.WEBSOCKET).set(HttpHeaderNames.CONNECTION, HttpHeaderValues.UPGRADE).set(HttpHeaderNames.HOST, WebSocketClientHandshaker.websocketHostValue(wsURL)).set(HttpHeaderNames.SEC_WEBSOCKET_KEY1, key1).set(HttpHeaderNames.SEC_WEBSOCKET_KEY2, key2);
        if (!headers.contains(HttpHeaderNames.ORIGIN)) {
            headers.set(HttpHeaderNames.ORIGIN, WebSocketClientHandshaker.websocketOriginValue(wsURL));
        }
        final String expectedSubprotocol = this.expectedSubprotocol();
        if (expectedSubprotocol != null && !expectedSubprotocol.isEmpty()) {
            headers.set(HttpHeaderNames.SEC_WEBSOCKET_PROTOCOL, expectedSubprotocol);
        }
        headers.set(HttpHeaderNames.CONTENT_LENGTH, key3.length);
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
        final ByteBuf challenge = response.content();
        if (!challenge.equals(this.expectedChallengeResponseBytes)) {
            throw new WebSocketClientHandshakeException("Invalid challenge", response);
        }
    }
    
    private static String insertRandomCharacters(String key) {
        final int count = WebSocketUtil.randomNumber(1, 12);
        final char[] randomChars = new char[count];
        for (int randCount = 0; randCount < count; ++randCount) {
            final int rand = PlatformDependent.threadLocalRandom().nextInt(126) + 33;
            if ((33 < rand && rand < 47) || (58 < rand && rand < 126)) {
                randomChars[randCount] = (char)rand;
            }
        }
        for (int i = 0; i < count; ++i) {
            final int split = WebSocketUtil.randomNumber(0, key.length());
            final String part1 = key.substring(0, split);
            final String part2 = key.substring(split);
            key = part1 + randomChars[i] + part2;
        }
        return key;
    }
    
    private static String insertSpaces(String key, final int spaces) {
        for (int i = 0; i < spaces; ++i) {
            final int split = WebSocketUtil.randomNumber(1, key.length() - 1);
            final String part1 = key.substring(0, split);
            final String part2 = key.substring(split);
            key = part1 + ' ' + part2;
        }
        return key;
    }
    
    @Override
    protected WebSocketFrameDecoder newWebsocketDecoder() {
        return new WebSocket00FrameDecoder(this.maxFramePayloadLength());
    }
    
    @Override
    protected WebSocketFrameEncoder newWebSocketEncoder() {
        return new WebSocket00FrameEncoder();
    }
    
    @Override
    public WebSocketClientHandshaker00 setForceCloseTimeoutMillis(final long forceCloseTimeoutMillis) {
        super.setForceCloseTimeoutMillis(forceCloseTimeoutMillis);
        return this;
    }
}
