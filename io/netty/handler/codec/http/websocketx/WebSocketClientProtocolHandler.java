package io.netty.handler.codec.http.websocketx;

import java.net.SocketAddress;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelHandler;
import java.util.List;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpHeaders;
import java.net.URI;
import io.netty.util.internal.ObjectUtil;

public class WebSocketClientProtocolHandler extends WebSocketProtocolHandler
{
    private final WebSocketClientHandshaker handshaker;
    private final WebSocketClientProtocolConfig clientConfig;
    
    public WebSocketClientHandshaker handshaker() {
        return this.handshaker;
    }
    
    public WebSocketClientProtocolHandler(final WebSocketClientProtocolConfig clientConfig) {
        super(ObjectUtil.checkNotNull(clientConfig, "clientConfig").dropPongFrames(), clientConfig.sendCloseFrame(), clientConfig.forceCloseTimeoutMillis());
        this.handshaker = WebSocketClientHandshakerFactory.newHandshaker(clientConfig.webSocketUri(), clientConfig.version(), clientConfig.subprotocol(), clientConfig.allowExtensions(), clientConfig.customHeaders(), clientConfig.maxFramePayloadLength(), clientConfig.performMasking(), clientConfig.allowMaskMismatch(), clientConfig.forceCloseTimeoutMillis(), clientConfig.absoluteUpgradeUrl());
        this.clientConfig = clientConfig;
    }
    
    public WebSocketClientProtocolHandler(final URI webSocketURL, final WebSocketVersion version, final String subprotocol, final boolean allowExtensions, final HttpHeaders customHeaders, final int maxFramePayloadLength, final boolean handleCloseFrames, final boolean performMasking, final boolean allowMaskMismatch) {
        this(webSocketURL, version, subprotocol, allowExtensions, customHeaders, maxFramePayloadLength, handleCloseFrames, performMasking, allowMaskMismatch, 10000L);
    }
    
    public WebSocketClientProtocolHandler(final URI webSocketURL, final WebSocketVersion version, final String subprotocol, final boolean allowExtensions, final HttpHeaders customHeaders, final int maxFramePayloadLength, final boolean handleCloseFrames, final boolean performMasking, final boolean allowMaskMismatch, final long handshakeTimeoutMillis) {
        this(WebSocketClientHandshakerFactory.newHandshaker(webSocketURL, version, subprotocol, allowExtensions, customHeaders, maxFramePayloadLength, performMasking, allowMaskMismatch), handleCloseFrames, handshakeTimeoutMillis);
    }
    
    public WebSocketClientProtocolHandler(final URI webSocketURL, final WebSocketVersion version, final String subprotocol, final boolean allowExtensions, final HttpHeaders customHeaders, final int maxFramePayloadLength, final boolean handleCloseFrames) {
        this(webSocketURL, version, subprotocol, allowExtensions, customHeaders, maxFramePayloadLength, handleCloseFrames, 10000L);
    }
    
    public WebSocketClientProtocolHandler(final URI webSocketURL, final WebSocketVersion version, final String subprotocol, final boolean allowExtensions, final HttpHeaders customHeaders, final int maxFramePayloadLength, final boolean handleCloseFrames, final long handshakeTimeoutMillis) {
        this(webSocketURL, version, subprotocol, allowExtensions, customHeaders, maxFramePayloadLength, handleCloseFrames, true, false, handshakeTimeoutMillis);
    }
    
    public WebSocketClientProtocolHandler(final URI webSocketURL, final WebSocketVersion version, final String subprotocol, final boolean allowExtensions, final HttpHeaders customHeaders, final int maxFramePayloadLength) {
        this(webSocketURL, version, subprotocol, allowExtensions, customHeaders, maxFramePayloadLength, 10000L);
    }
    
    public WebSocketClientProtocolHandler(final URI webSocketURL, final WebSocketVersion version, final String subprotocol, final boolean allowExtensions, final HttpHeaders customHeaders, final int maxFramePayloadLength, final long handshakeTimeoutMillis) {
        this(webSocketURL, version, subprotocol, allowExtensions, customHeaders, maxFramePayloadLength, true, handshakeTimeoutMillis);
    }
    
    public WebSocketClientProtocolHandler(final WebSocketClientHandshaker handshaker, final boolean handleCloseFrames) {
        this(handshaker, handleCloseFrames, 10000L);
    }
    
    public WebSocketClientProtocolHandler(final WebSocketClientHandshaker handshaker, final boolean handleCloseFrames, final long handshakeTimeoutMillis) {
        this(handshaker, handleCloseFrames, true, handshakeTimeoutMillis);
    }
    
    public WebSocketClientProtocolHandler(final WebSocketClientHandshaker handshaker, final boolean handleCloseFrames, final boolean dropPongFrames) {
        this(handshaker, handleCloseFrames, dropPongFrames, 10000L);
    }
    
    public WebSocketClientProtocolHandler(final WebSocketClientHandshaker handshaker, final boolean handleCloseFrames, final boolean dropPongFrames, final long handshakeTimeoutMillis) {
        super(dropPongFrames);
        this.handshaker = handshaker;
        this.clientConfig = WebSocketClientProtocolConfig.newBuilder().handleCloseFrames(handleCloseFrames).handshakeTimeoutMillis(handshakeTimeoutMillis).build();
    }
    
    public WebSocketClientProtocolHandler(final WebSocketClientHandshaker handshaker) {
        this(handshaker, 10000L);
    }
    
    public WebSocketClientProtocolHandler(final WebSocketClientHandshaker handshaker, final long handshakeTimeoutMillis) {
        this(handshaker, true, handshakeTimeoutMillis);
    }
    
    @Override
    protected void decode(final ChannelHandlerContext ctx, final WebSocketFrame frame, final List<Object> out) throws Exception {
        if (this.clientConfig.handleCloseFrames() && frame instanceof CloseWebSocketFrame) {
            ctx.close();
            return;
        }
        super.decode(ctx, frame, out);
    }
    
    @Override
    protected WebSocketClientHandshakeException buildHandshakeException(final String message) {
        return new WebSocketClientHandshakeException(message);
    }
    
    @Override
    public void handlerAdded(final ChannelHandlerContext ctx) {
        final ChannelPipeline cp = ctx.pipeline();
        if (cp.get(WebSocketClientProtocolHandshakeHandler.class) == null) {
            ctx.pipeline().addBefore(ctx.name(), WebSocketClientProtocolHandshakeHandler.class.getName(), new WebSocketClientProtocolHandshakeHandler(this.handshaker, this.clientConfig.handshakeTimeoutMillis()));
        }
        if (cp.get(Utf8FrameValidator.class) == null) {
            ctx.pipeline().addBefore(ctx.name(), Utf8FrameValidator.class.getName(), new Utf8FrameValidator());
        }
    }
    
    public enum ClientHandshakeStateEvent
    {
        HANDSHAKE_TIMEOUT, 
        HANDSHAKE_ISSUED, 
        HANDSHAKE_COMPLETE;
    }
}
