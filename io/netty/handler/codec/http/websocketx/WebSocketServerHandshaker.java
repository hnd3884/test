package io.netty.handler.codec.http.websocketx;

import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.channel.ChannelOutboundInvoker;
import io.netty.util.ReferenceCountUtil;
import java.nio.channels.ClosedChannelException;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Future;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.channel.Channel;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.logging.InternalLogger;

public abstract class WebSocketServerHandshaker
{
    protected static final InternalLogger logger;
    private final String uri;
    private final String[] subprotocols;
    private final WebSocketVersion version;
    private final WebSocketDecoderConfig decoderConfig;
    private String selectedSubprotocol;
    public static final String SUB_PROTOCOL_WILDCARD = "*";
    
    protected WebSocketServerHandshaker(final WebSocketVersion version, final String uri, final String subprotocols, final int maxFramePayloadLength) {
        this(version, uri, subprotocols, WebSocketDecoderConfig.newBuilder().maxFramePayloadLength(maxFramePayloadLength).build());
    }
    
    protected WebSocketServerHandshaker(final WebSocketVersion version, final String uri, final String subprotocols, final WebSocketDecoderConfig decoderConfig) {
        this.version = version;
        this.uri = uri;
        if (subprotocols != null) {
            final String[] subprotocolArray = subprotocols.split(",");
            for (int i = 0; i < subprotocolArray.length; ++i) {
                subprotocolArray[i] = subprotocolArray[i].trim();
            }
            this.subprotocols = subprotocolArray;
        }
        else {
            this.subprotocols = EmptyArrays.EMPTY_STRINGS;
        }
        this.decoderConfig = ObjectUtil.checkNotNull(decoderConfig, "decoderConfig");
    }
    
    public String uri() {
        return this.uri;
    }
    
    public Set<String> subprotocols() {
        final Set<String> ret = new LinkedHashSet<String>();
        Collections.addAll(ret, this.subprotocols);
        return ret;
    }
    
    public WebSocketVersion version() {
        return this.version;
    }
    
    public int maxFramePayloadLength() {
        return this.decoderConfig.maxFramePayloadLength();
    }
    
    public WebSocketDecoderConfig decoderConfig() {
        return this.decoderConfig;
    }
    
    public ChannelFuture handshake(final Channel channel, final FullHttpRequest req) {
        return this.handshake(channel, req, null, channel.newPromise());
    }
    
    public final ChannelFuture handshake(final Channel channel, final FullHttpRequest req, final HttpHeaders responseHeaders, final ChannelPromise promise) {
        if (WebSocketServerHandshaker.logger.isDebugEnabled()) {
            WebSocketServerHandshaker.logger.debug("{} WebSocket version {} server handshake", channel, this.version());
        }
        final FullHttpResponse response = this.newHandshakeResponse(req, responseHeaders);
        final ChannelPipeline p = channel.pipeline();
        if (p.get(HttpObjectAggregator.class) != null) {
            p.remove(HttpObjectAggregator.class);
        }
        if (p.get(HttpContentCompressor.class) != null) {
            p.remove(HttpContentCompressor.class);
        }
        ChannelHandlerContext ctx = p.context(HttpRequestDecoder.class);
        String encoderName;
        if (ctx == null) {
            ctx = p.context(HttpServerCodec.class);
            if (ctx == null) {
                promise.setFailure((Throwable)new IllegalStateException("No HttpDecoder and no HttpServerCodec in the pipeline"));
                return promise;
            }
            p.addBefore(ctx.name(), "wsencoder", this.newWebSocketEncoder());
            p.addBefore(ctx.name(), "wsdecoder", this.newWebsocketDecoder());
            encoderName = ctx.name();
        }
        else {
            p.replace(ctx.name(), "wsdecoder", this.newWebsocketDecoder());
            encoderName = p.context(HttpResponseEncoder.class).name();
            p.addBefore(encoderName, "wsencoder", this.newWebSocketEncoder());
        }
        channel.writeAndFlush(response).addListener((GenericFutureListener<? extends Future<? super Void>>)new ChannelFutureListener() {
            @Override
            public void operationComplete(final ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    final ChannelPipeline p = future.channel().pipeline();
                    p.remove(encoderName);
                    promise.setSuccess();
                }
                else {
                    promise.setFailure(future.cause());
                }
            }
        });
        return promise;
    }
    
    public ChannelFuture handshake(final Channel channel, final HttpRequest req) {
        return this.handshake(channel, req, null, channel.newPromise());
    }
    
    public final ChannelFuture handshake(final Channel channel, final HttpRequest req, final HttpHeaders responseHeaders, final ChannelPromise promise) {
        if (req instanceof FullHttpRequest) {
            return this.handshake(channel, (FullHttpRequest)req, responseHeaders, promise);
        }
        if (WebSocketServerHandshaker.logger.isDebugEnabled()) {
            WebSocketServerHandshaker.logger.debug("{} WebSocket version {} server handshake", channel, this.version());
        }
        final ChannelPipeline p = channel.pipeline();
        ChannelHandlerContext ctx = p.context(HttpRequestDecoder.class);
        if (ctx == null) {
            ctx = p.context(HttpServerCodec.class);
            if (ctx == null) {
                promise.setFailure((Throwable)new IllegalStateException("No HttpDecoder and no HttpServerCodec in the pipeline"));
                return promise;
            }
        }
        final String aggregatorName = "httpAggregator";
        p.addAfter(ctx.name(), aggregatorName, new HttpObjectAggregator(8192));
        p.addAfter(aggregatorName, "handshaker", new SimpleChannelInboundHandler<FullHttpRequest>() {
            @Override
            protected void channelRead0(final ChannelHandlerContext ctx, final FullHttpRequest msg) throws Exception {
                ctx.pipeline().remove(this);
                WebSocketServerHandshaker.this.handshake(channel, msg, responseHeaders, promise);
            }
            
            @Override
            public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
                ctx.pipeline().remove(this);
                promise.tryFailure(cause);
                ctx.fireExceptionCaught(cause);
            }
            
            @Override
            public void channelInactive(final ChannelHandlerContext ctx) throws Exception {
                if (!promise.isDone()) {
                    promise.tryFailure(new ClosedChannelException());
                }
                ctx.fireChannelInactive();
            }
        });
        try {
            ctx.fireChannelRead((Object)ReferenceCountUtil.retain(req));
        }
        catch (final Throwable cause) {
            promise.setFailure(cause);
        }
        return promise;
    }
    
    protected abstract FullHttpResponse newHandshakeResponse(final FullHttpRequest p0, final HttpHeaders p1);
    
    public ChannelFuture close(final Channel channel, final CloseWebSocketFrame frame) {
        ObjectUtil.checkNotNull(channel, "channel");
        return this.close(channel, frame, channel.newPromise());
    }
    
    public ChannelFuture close(final Channel channel, final CloseWebSocketFrame frame, final ChannelPromise promise) {
        return this.close0(channel, frame, promise);
    }
    
    public ChannelFuture close(final ChannelHandlerContext ctx, final CloseWebSocketFrame frame) {
        ObjectUtil.checkNotNull(ctx, "ctx");
        return this.close(ctx, frame, ctx.newPromise());
    }
    
    public ChannelFuture close(final ChannelHandlerContext ctx, final CloseWebSocketFrame frame, final ChannelPromise promise) {
        ObjectUtil.checkNotNull(ctx, "ctx");
        return this.close0(ctx, frame, promise).addListener((GenericFutureListener<? extends Future<? super Void>>)ChannelFutureListener.CLOSE);
    }
    
    private ChannelFuture close0(final ChannelOutboundInvoker invoker, final CloseWebSocketFrame frame, final ChannelPromise promise) {
        return invoker.writeAndFlush(frame, promise).addListener((GenericFutureListener<? extends Future<? super Void>>)ChannelFutureListener.CLOSE);
    }
    
    protected String selectSubprotocol(final String requestedSubprotocols) {
        if (requestedSubprotocols == null || this.subprotocols.length == 0) {
            return null;
        }
        final String[] split;
        final String[] requestedSubprotocolArray = split = requestedSubprotocols.split(",");
        for (final String p : split) {
            final String requestedSubprotocol = p.trim();
            for (final String supportedSubprotocol : this.subprotocols) {
                if ("*".equals(supportedSubprotocol) || requestedSubprotocol.equals(supportedSubprotocol)) {
                    return this.selectedSubprotocol = requestedSubprotocol;
                }
            }
        }
        return null;
    }
    
    public String selectedSubprotocol() {
        return this.selectedSubprotocol;
    }
    
    protected abstract WebSocketFrameDecoder newWebsocketDecoder();
    
    protected abstract WebSocketFrameEncoder newWebSocketEncoder();
    
    static {
        logger = InternalLoggerFactory.getInstance(WebSocketServerHandshaker.class);
    }
}
