package io.netty.handler.codec.http.websocketx;

import io.netty.handler.codec.http.HttpHeaders;
import java.net.SocketAddress;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.channel.ChannelPromise;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.channel.ChannelFutureListener;
import io.netty.buffer.Unpooled;
import java.util.List;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.AttributeKey;

public class WebSocketServerProtocolHandler extends WebSocketProtocolHandler
{
    private static final AttributeKey<WebSocketServerHandshaker> HANDSHAKER_ATTR_KEY;
    private final WebSocketServerProtocolConfig serverConfig;
    
    public WebSocketServerProtocolHandler(final WebSocketServerProtocolConfig serverConfig) {
        super(ObjectUtil.checkNotNull(serverConfig, "serverConfig").dropPongFrames(), serverConfig.sendCloseFrame(), serverConfig.forceCloseTimeoutMillis());
        this.serverConfig = serverConfig;
    }
    
    public WebSocketServerProtocolHandler(final String websocketPath) {
        this(websocketPath, 10000L);
    }
    
    public WebSocketServerProtocolHandler(final String websocketPath, final long handshakeTimeoutMillis) {
        this(websocketPath, false, handshakeTimeoutMillis);
    }
    
    public WebSocketServerProtocolHandler(final String websocketPath, final boolean checkStartsWith) {
        this(websocketPath, checkStartsWith, 10000L);
    }
    
    public WebSocketServerProtocolHandler(final String websocketPath, final boolean checkStartsWith, final long handshakeTimeoutMillis) {
        this(websocketPath, null, false, 65536, false, checkStartsWith, handshakeTimeoutMillis);
    }
    
    public WebSocketServerProtocolHandler(final String websocketPath, final String subprotocols) {
        this(websocketPath, subprotocols, 10000L);
    }
    
    public WebSocketServerProtocolHandler(final String websocketPath, final String subprotocols, final long handshakeTimeoutMillis) {
        this(websocketPath, subprotocols, false, handshakeTimeoutMillis);
    }
    
    public WebSocketServerProtocolHandler(final String websocketPath, final String subprotocols, final boolean allowExtensions) {
        this(websocketPath, subprotocols, allowExtensions, 10000L);
    }
    
    public WebSocketServerProtocolHandler(final String websocketPath, final String subprotocols, final boolean allowExtensions, final long handshakeTimeoutMillis) {
        this(websocketPath, subprotocols, allowExtensions, 65536, handshakeTimeoutMillis);
    }
    
    public WebSocketServerProtocolHandler(final String websocketPath, final String subprotocols, final boolean allowExtensions, final int maxFrameSize) {
        this(websocketPath, subprotocols, allowExtensions, maxFrameSize, 10000L);
    }
    
    public WebSocketServerProtocolHandler(final String websocketPath, final String subprotocols, final boolean allowExtensions, final int maxFrameSize, final long handshakeTimeoutMillis) {
        this(websocketPath, subprotocols, allowExtensions, maxFrameSize, false, handshakeTimeoutMillis);
    }
    
    public WebSocketServerProtocolHandler(final String websocketPath, final String subprotocols, final boolean allowExtensions, final int maxFrameSize, final boolean allowMaskMismatch) {
        this(websocketPath, subprotocols, allowExtensions, maxFrameSize, allowMaskMismatch, 10000L);
    }
    
    public WebSocketServerProtocolHandler(final String websocketPath, final String subprotocols, final boolean allowExtensions, final int maxFrameSize, final boolean allowMaskMismatch, final long handshakeTimeoutMillis) {
        this(websocketPath, subprotocols, allowExtensions, maxFrameSize, allowMaskMismatch, false, handshakeTimeoutMillis);
    }
    
    public WebSocketServerProtocolHandler(final String websocketPath, final String subprotocols, final boolean allowExtensions, final int maxFrameSize, final boolean allowMaskMismatch, final boolean checkStartsWith) {
        this(websocketPath, subprotocols, allowExtensions, maxFrameSize, allowMaskMismatch, checkStartsWith, 10000L);
    }
    
    public WebSocketServerProtocolHandler(final String websocketPath, final String subprotocols, final boolean allowExtensions, final int maxFrameSize, final boolean allowMaskMismatch, final boolean checkStartsWith, final long handshakeTimeoutMillis) {
        this(websocketPath, subprotocols, allowExtensions, maxFrameSize, allowMaskMismatch, checkStartsWith, true, handshakeTimeoutMillis);
    }
    
    public WebSocketServerProtocolHandler(final String websocketPath, final String subprotocols, final boolean allowExtensions, final int maxFrameSize, final boolean allowMaskMismatch, final boolean checkStartsWith, final boolean dropPongFrames) {
        this(websocketPath, subprotocols, allowExtensions, maxFrameSize, allowMaskMismatch, checkStartsWith, dropPongFrames, 10000L);
    }
    
    public WebSocketServerProtocolHandler(final String websocketPath, final String subprotocols, final boolean allowExtensions, final int maxFrameSize, final boolean allowMaskMismatch, final boolean checkStartsWith, final boolean dropPongFrames, final long handshakeTimeoutMillis) {
        this(websocketPath, subprotocols, checkStartsWith, dropPongFrames, handshakeTimeoutMillis, WebSocketDecoderConfig.newBuilder().maxFramePayloadLength(maxFrameSize).allowMaskMismatch(allowMaskMismatch).allowExtensions(allowExtensions).build());
    }
    
    public WebSocketServerProtocolHandler(final String websocketPath, final String subprotocols, final boolean checkStartsWith, final boolean dropPongFrames, final long handshakeTimeoutMillis, final WebSocketDecoderConfig decoderConfig) {
        this(WebSocketServerProtocolConfig.newBuilder().websocketPath(websocketPath).subprotocols(subprotocols).checkStartsWith(checkStartsWith).handshakeTimeoutMillis(handshakeTimeoutMillis).dropPongFrames(dropPongFrames).decoderConfig(decoderConfig).build());
    }
    
    @Override
    public void handlerAdded(final ChannelHandlerContext ctx) {
        final ChannelPipeline cp = ctx.pipeline();
        if (cp.get(WebSocketServerProtocolHandshakeHandler.class) == null) {
            cp.addBefore(ctx.name(), WebSocketServerProtocolHandshakeHandler.class.getName(), new WebSocketServerProtocolHandshakeHandler(this.serverConfig));
        }
        if (this.serverConfig.decoderConfig().withUTF8Validator() && cp.get(Utf8FrameValidator.class) == null) {
            cp.addBefore(ctx.name(), Utf8FrameValidator.class.getName(), new Utf8FrameValidator());
        }
    }
    
    @Override
    protected void decode(final ChannelHandlerContext ctx, final WebSocketFrame frame, final List<Object> out) throws Exception {
        if (this.serverConfig.handleCloseFrames() && frame instanceof CloseWebSocketFrame) {
            final WebSocketServerHandshaker handshaker = getHandshaker(ctx.channel());
            if (handshaker != null) {
                frame.retain();
                final ChannelPromise promise = ctx.newPromise();
                this.closeSent(promise);
                handshaker.close(ctx, (CloseWebSocketFrame)frame, promise);
            }
            else {
                ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener((GenericFutureListener<? extends Future<? super Void>>)ChannelFutureListener.CLOSE);
            }
            return;
        }
        super.decode(ctx, frame, out);
    }
    
    @Override
    protected WebSocketServerHandshakeException buildHandshakeException(final String message) {
        return new WebSocketServerHandshakeException(message);
    }
    
    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
        if (cause instanceof WebSocketHandshakeException) {
            final FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST, Unpooled.wrappedBuffer(cause.getMessage().getBytes()));
            ctx.channel().writeAndFlush(response).addListener((GenericFutureListener<? extends Future<? super Void>>)ChannelFutureListener.CLOSE);
        }
        else {
            ctx.fireExceptionCaught(cause);
            ctx.close();
        }
    }
    
    static WebSocketServerHandshaker getHandshaker(final Channel channel) {
        return channel.attr(WebSocketServerProtocolHandler.HANDSHAKER_ATTR_KEY).get();
    }
    
    static void setHandshaker(final Channel channel, final WebSocketServerHandshaker handshaker) {
        channel.attr(WebSocketServerProtocolHandler.HANDSHAKER_ATTR_KEY).set(handshaker);
    }
    
    static {
        HANDSHAKER_ATTR_KEY = AttributeKey.valueOf(WebSocketServerHandshaker.class, "HANDSHAKER");
    }
    
    public enum ServerHandshakeStateEvent
    {
        @Deprecated
        HANDSHAKE_COMPLETE, 
        HANDSHAKE_TIMEOUT;
    }
    
    public static final class HandshakeComplete
    {
        private final String requestUri;
        private final HttpHeaders requestHeaders;
        private final String selectedSubprotocol;
        
        HandshakeComplete(final String requestUri, final HttpHeaders requestHeaders, final String selectedSubprotocol) {
            this.requestUri = requestUri;
            this.requestHeaders = requestHeaders;
            this.selectedSubprotocol = selectedSubprotocol;
        }
        
        public String requestUri() {
            return this.requestUri;
        }
        
        public HttpHeaders requestHeaders() {
            return this.requestHeaders;
        }
        
        public String selectedSubprotocol() {
            return this.selectedSubprotocol;
        }
    }
}
