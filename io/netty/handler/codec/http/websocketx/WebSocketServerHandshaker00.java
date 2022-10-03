package io.netty.handler.codec.http.websocketx;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;
import io.netty.channel.Channel;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.FullHttpRequest;
import java.util.regex.Pattern;

public class WebSocketServerHandshaker00 extends WebSocketServerHandshaker
{
    private static final Pattern BEGINNING_DIGIT;
    private static final Pattern BEGINNING_SPACE;
    
    public WebSocketServerHandshaker00(final String webSocketURL, final String subprotocols, final int maxFramePayloadLength) {
        this(webSocketURL, subprotocols, WebSocketDecoderConfig.newBuilder().maxFramePayloadLength(maxFramePayloadLength).build());
    }
    
    public WebSocketServerHandshaker00(final String webSocketURL, final String subprotocols, final WebSocketDecoderConfig decoderConfig) {
        super(WebSocketVersion.V00, webSocketURL, subprotocols, decoderConfig);
    }
    
    @Override
    protected FullHttpResponse newHandshakeResponse(final FullHttpRequest req, final HttpHeaders headers) {
        if (!req.headers().containsValue(HttpHeaderNames.CONNECTION, HttpHeaderValues.UPGRADE, true) || !HttpHeaderValues.WEBSOCKET.contentEqualsIgnoreCase(req.headers().get(HttpHeaderNames.UPGRADE))) {
            throw new WebSocketServerHandshakeException("not a WebSocket handshake request: missing upgrade", req);
        }
        final boolean isHixie76 = req.headers().contains(HttpHeaderNames.SEC_WEBSOCKET_KEY1) && req.headers().contains(HttpHeaderNames.SEC_WEBSOCKET_KEY2);
        final String origin = req.headers().get(HttpHeaderNames.ORIGIN);
        if (origin == null && !isHixie76) {
            throw new WebSocketServerHandshakeException("Missing origin header, got only " + req.headers().names(), req);
        }
        final FullHttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, new HttpResponseStatus(101, isHixie76 ? "WebSocket Protocol Handshake" : "Web Socket Protocol Handshake"), req.content().alloc().buffer(0));
        if (headers != null) {
            res.headers().add(headers);
        }
        res.headers().set(HttpHeaderNames.UPGRADE, HttpHeaderValues.WEBSOCKET).set(HttpHeaderNames.CONNECTION, HttpHeaderValues.UPGRADE);
        if (isHixie76) {
            res.headers().add(HttpHeaderNames.SEC_WEBSOCKET_ORIGIN, origin);
            res.headers().add(HttpHeaderNames.SEC_WEBSOCKET_LOCATION, this.uri());
            final String subprotocols = req.headers().get(HttpHeaderNames.SEC_WEBSOCKET_PROTOCOL);
            if (subprotocols != null) {
                final String selectedSubprotocol = this.selectSubprotocol(subprotocols);
                if (selectedSubprotocol == null) {
                    if (WebSocketServerHandshaker00.logger.isDebugEnabled()) {
                        WebSocketServerHandshaker00.logger.debug("Requested subprotocol(s) not supported: {}", subprotocols);
                    }
                }
                else {
                    res.headers().set(HttpHeaderNames.SEC_WEBSOCKET_PROTOCOL, selectedSubprotocol);
                }
            }
            final String key1 = req.headers().get(HttpHeaderNames.SEC_WEBSOCKET_KEY1);
            final String key2 = req.headers().get(HttpHeaderNames.SEC_WEBSOCKET_KEY2);
            final int a = (int)(Long.parseLong(WebSocketServerHandshaker00.BEGINNING_DIGIT.matcher(key1).replaceAll("")) / WebSocketServerHandshaker00.BEGINNING_SPACE.matcher(key1).replaceAll("").length());
            final int b = (int)(Long.parseLong(WebSocketServerHandshaker00.BEGINNING_DIGIT.matcher(key2).replaceAll("")) / WebSocketServerHandshaker00.BEGINNING_SPACE.matcher(key2).replaceAll("").length());
            final long c = req.content().readLong();
            final ByteBuf input = Unpooled.wrappedBuffer(new byte[16]).setIndex(0, 0);
            input.writeInt(a);
            input.writeInt(b);
            input.writeLong(c);
            res.content().writeBytes(WebSocketUtil.md5(input.array()));
        }
        else {
            res.headers().add(HttpHeaderNames.WEBSOCKET_ORIGIN, origin);
            res.headers().add(HttpHeaderNames.WEBSOCKET_LOCATION, this.uri());
            final String protocol = req.headers().get(HttpHeaderNames.WEBSOCKET_PROTOCOL);
            if (protocol != null) {
                res.headers().set(HttpHeaderNames.WEBSOCKET_PROTOCOL, this.selectSubprotocol(protocol));
            }
        }
        return res;
    }
    
    @Override
    public ChannelFuture close(final Channel channel, final CloseWebSocketFrame frame, final ChannelPromise promise) {
        return channel.writeAndFlush(frame, promise);
    }
    
    @Override
    public ChannelFuture close(final ChannelHandlerContext ctx, final CloseWebSocketFrame frame, final ChannelPromise promise) {
        return ctx.writeAndFlush(frame, promise);
    }
    
    @Override
    protected WebSocketFrameDecoder newWebsocketDecoder() {
        return new WebSocket00FrameDecoder(this.decoderConfig());
    }
    
    @Override
    protected WebSocketFrameEncoder newWebSocketEncoder() {
        return new WebSocket00FrameEncoder();
    }
    
    static {
        BEGINNING_DIGIT = Pattern.compile("[^0-9]");
        BEGINNING_SPACE = Pattern.compile("[^ ]");
    }
}
