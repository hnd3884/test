package io.netty.handler.codec.http.websocketx;

import java.util.Locale;
import io.netty.util.NetUtil;
import io.netty.handler.codec.http.HttpScheme;
import java.util.concurrent.TimeUnit;
import io.netty.channel.ChannelOutboundInvoker;
import io.netty.util.ReferenceCountUtil;
import java.nio.channels.ClosedChannelException;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Future;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.channel.ChannelPromise;
import io.netty.util.internal.ObjectUtil;
import io.netty.channel.ChannelFuture;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpHeaders;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.net.URI;

public abstract class WebSocketClientHandshaker
{
    private static final String HTTP_SCHEME_PREFIX;
    private static final String HTTPS_SCHEME_PREFIX;
    protected static final int DEFAULT_FORCE_CLOSE_TIMEOUT_MILLIS = 10000;
    private final URI uri;
    private final WebSocketVersion version;
    private volatile boolean handshakeComplete;
    private volatile long forceCloseTimeoutMillis;
    private volatile int forceCloseInit;
    private static final AtomicIntegerFieldUpdater<WebSocketClientHandshaker> FORCE_CLOSE_INIT_UPDATER;
    private volatile boolean forceCloseComplete;
    private final String expectedSubprotocol;
    private volatile String actualSubprotocol;
    protected final HttpHeaders customHeaders;
    private final int maxFramePayloadLength;
    private final boolean absoluteUpgradeUrl;
    
    protected WebSocketClientHandshaker(final URI uri, final WebSocketVersion version, final String subprotocol, final HttpHeaders customHeaders, final int maxFramePayloadLength) {
        this(uri, version, subprotocol, customHeaders, maxFramePayloadLength, 10000L);
    }
    
    protected WebSocketClientHandshaker(final URI uri, final WebSocketVersion version, final String subprotocol, final HttpHeaders customHeaders, final int maxFramePayloadLength, final long forceCloseTimeoutMillis) {
        this(uri, version, subprotocol, customHeaders, maxFramePayloadLength, forceCloseTimeoutMillis, false);
    }
    
    protected WebSocketClientHandshaker(final URI uri, final WebSocketVersion version, final String subprotocol, final HttpHeaders customHeaders, final int maxFramePayloadLength, final long forceCloseTimeoutMillis, final boolean absoluteUpgradeUrl) {
        this.forceCloseTimeoutMillis = 10000L;
        this.uri = uri;
        this.version = version;
        this.expectedSubprotocol = subprotocol;
        this.customHeaders = customHeaders;
        this.maxFramePayloadLength = maxFramePayloadLength;
        this.forceCloseTimeoutMillis = forceCloseTimeoutMillis;
        this.absoluteUpgradeUrl = absoluteUpgradeUrl;
    }
    
    public URI uri() {
        return this.uri;
    }
    
    public WebSocketVersion version() {
        return this.version;
    }
    
    public int maxFramePayloadLength() {
        return this.maxFramePayloadLength;
    }
    
    public boolean isHandshakeComplete() {
        return this.handshakeComplete;
    }
    
    private void setHandshakeComplete() {
        this.handshakeComplete = true;
    }
    
    public String expectedSubprotocol() {
        return this.expectedSubprotocol;
    }
    
    public String actualSubprotocol() {
        return this.actualSubprotocol;
    }
    
    private void setActualSubprotocol(final String actualSubprotocol) {
        this.actualSubprotocol = actualSubprotocol;
    }
    
    public long forceCloseTimeoutMillis() {
        return this.forceCloseTimeoutMillis;
    }
    
    protected boolean isForceCloseComplete() {
        return this.forceCloseComplete;
    }
    
    public WebSocketClientHandshaker setForceCloseTimeoutMillis(final long forceCloseTimeoutMillis) {
        this.forceCloseTimeoutMillis = forceCloseTimeoutMillis;
        return this;
    }
    
    public ChannelFuture handshake(final Channel channel) {
        ObjectUtil.checkNotNull(channel, "channel");
        return this.handshake(channel, channel.newPromise());
    }
    
    public final ChannelFuture handshake(final Channel channel, final ChannelPromise promise) {
        final ChannelPipeline pipeline = channel.pipeline();
        final HttpResponseDecoder decoder = pipeline.get(HttpResponseDecoder.class);
        if (decoder == null) {
            final HttpClientCodec codec = pipeline.get(HttpClientCodec.class);
            if (codec == null) {
                promise.setFailure((Throwable)new IllegalStateException("ChannelPipeline does not contain an HttpResponseDecoder or HttpClientCodec"));
                return promise;
            }
        }
        final FullHttpRequest request = this.newHandshakeRequest();
        channel.writeAndFlush(request).addListener((GenericFutureListener<? extends Future<? super Void>>)new ChannelFutureListener() {
            @Override
            public void operationComplete(final ChannelFuture future) {
                if (future.isSuccess()) {
                    final ChannelPipeline p = future.channel().pipeline();
                    ChannelHandlerContext ctx = p.context(HttpRequestEncoder.class);
                    if (ctx == null) {
                        ctx = p.context(HttpClientCodec.class);
                    }
                    if (ctx == null) {
                        promise.setFailure((Throwable)new IllegalStateException("ChannelPipeline does not contain an HttpRequestEncoder or HttpClientCodec"));
                        return;
                    }
                    p.addAfter(ctx.name(), "ws-encoder", WebSocketClientHandshaker.this.newWebSocketEncoder());
                    promise.setSuccess();
                }
                else {
                    promise.setFailure(future.cause());
                }
            }
        });
        return promise;
    }
    
    protected abstract FullHttpRequest newHandshakeRequest();
    
    public final void finishHandshake(final Channel channel, final FullHttpResponse response) {
        this.verify(response);
        String receivedProtocol = response.headers().get(HttpHeaderNames.SEC_WEBSOCKET_PROTOCOL);
        receivedProtocol = ((receivedProtocol != null) ? receivedProtocol.trim() : null);
        final String expectedProtocol = (this.expectedSubprotocol != null) ? this.expectedSubprotocol : "";
        boolean protocolValid = false;
        if (expectedProtocol.isEmpty() && receivedProtocol == null) {
            protocolValid = true;
            this.setActualSubprotocol(this.expectedSubprotocol);
        }
        else if (!expectedProtocol.isEmpty() && receivedProtocol != null && !receivedProtocol.isEmpty()) {
            for (final String protocol : expectedProtocol.split(",")) {
                if (protocol.trim().equals(receivedProtocol)) {
                    protocolValid = true;
                    this.setActualSubprotocol(receivedProtocol);
                    break;
                }
            }
        }
        if (!protocolValid) {
            throw new WebSocketClientHandshakeException(String.format("Invalid subprotocol. Actual: %s. Expected one of: %s", receivedProtocol, this.expectedSubprotocol), response);
        }
        this.setHandshakeComplete();
        final ChannelPipeline p = channel.pipeline();
        final HttpContentDecompressor decompressor = p.get(HttpContentDecompressor.class);
        if (decompressor != null) {
            p.remove(decompressor);
        }
        final HttpObjectAggregator aggregator = p.get(HttpObjectAggregator.class);
        if (aggregator != null) {
            p.remove(aggregator);
        }
        ChannelHandlerContext ctx = p.context(HttpResponseDecoder.class);
        if (ctx == null) {
            ctx = p.context(HttpClientCodec.class);
            if (ctx == null) {
                throw new IllegalStateException("ChannelPipeline does not contain an HttpRequestEncoder or HttpClientCodec");
            }
            final HttpClientCodec codec = (HttpClientCodec)ctx.handler();
            codec.removeOutboundHandler();
            p.addAfter(ctx.name(), "ws-decoder", this.newWebsocketDecoder());
            channel.eventLoop().execute(new Runnable() {
                @Override
                public void run() {
                    p.remove(codec);
                }
            });
        }
        else {
            if (p.get(HttpRequestEncoder.class) != null) {
                p.remove(HttpRequestEncoder.class);
            }
            final ChannelHandlerContext context = ctx;
            p.addAfter(context.name(), "ws-decoder", this.newWebsocketDecoder());
            channel.eventLoop().execute(new Runnable() {
                @Override
                public void run() {
                    p.remove(context.handler());
                }
            });
        }
    }
    
    public final ChannelFuture processHandshake(final Channel channel, final HttpResponse response) {
        return this.processHandshake(channel, response, channel.newPromise());
    }
    
    public final ChannelFuture processHandshake(final Channel channel, final HttpResponse response, final ChannelPromise promise) {
        if (response instanceof FullHttpResponse) {
            try {
                this.finishHandshake(channel, (FullHttpResponse)response);
                promise.setSuccess();
            }
            catch (final Throwable cause) {
                promise.setFailure(cause);
            }
        }
        else {
            final ChannelPipeline p = channel.pipeline();
            ChannelHandlerContext ctx = p.context(HttpResponseDecoder.class);
            if (ctx == null) {
                ctx = p.context(HttpClientCodec.class);
                if (ctx == null) {
                    return promise.setFailure((Throwable)new IllegalStateException("ChannelPipeline does not contain an HttpResponseDecoder or HttpClientCodec"));
                }
            }
            final String aggregatorName = "httpAggregator";
            p.addAfter(ctx.name(), aggregatorName, new HttpObjectAggregator(8192));
            p.addAfter(aggregatorName, "handshaker", new SimpleChannelInboundHandler<FullHttpResponse>() {
                @Override
                protected void channelRead0(final ChannelHandlerContext ctx, final FullHttpResponse msg) throws Exception {
                    ctx.pipeline().remove(this);
                    try {
                        WebSocketClientHandshaker.this.finishHandshake(channel, msg);
                        promise.setSuccess();
                    }
                    catch (final Throwable cause) {
                        promise.setFailure(cause);
                    }
                }
                
                @Override
                public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
                    ctx.pipeline().remove(this);
                    promise.setFailure(cause);
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
                ctx.fireChannelRead((Object)ReferenceCountUtil.retain(response));
            }
            catch (final Throwable cause2) {
                promise.setFailure(cause2);
            }
        }
        return promise;
    }
    
    protected abstract void verify(final FullHttpResponse p0);
    
    protected abstract WebSocketFrameDecoder newWebsocketDecoder();
    
    protected abstract WebSocketFrameEncoder newWebSocketEncoder();
    
    public ChannelFuture close(final Channel channel, final CloseWebSocketFrame frame) {
        ObjectUtil.checkNotNull(channel, "channel");
        return this.close(channel, frame, channel.newPromise());
    }
    
    public ChannelFuture close(final Channel channel, final CloseWebSocketFrame frame, final ChannelPromise promise) {
        ObjectUtil.checkNotNull(channel, "channel");
        return this.close0(channel, channel, frame, promise);
    }
    
    public ChannelFuture close(final ChannelHandlerContext ctx, final CloseWebSocketFrame frame) {
        ObjectUtil.checkNotNull(ctx, "ctx");
        return this.close(ctx, frame, ctx.newPromise());
    }
    
    public ChannelFuture close(final ChannelHandlerContext ctx, final CloseWebSocketFrame frame, final ChannelPromise promise) {
        ObjectUtil.checkNotNull(ctx, "ctx");
        return this.close0(ctx, ctx.channel(), frame, promise);
    }
    
    private ChannelFuture close0(final ChannelOutboundInvoker invoker, final Channel channel, final CloseWebSocketFrame frame, final ChannelPromise promise) {
        invoker.writeAndFlush(frame, promise);
        final long forceCloseTimeoutMillis = this.forceCloseTimeoutMillis;
        final WebSocketClientHandshaker handshaker = this;
        if (forceCloseTimeoutMillis <= 0L || !channel.isActive() || this.forceCloseInit != 0) {
            return promise;
        }
        promise.addListener((GenericFutureListener<? extends Future<? super Void>>)new ChannelFutureListener() {
            @Override
            public void operationComplete(final ChannelFuture future) {
                if (future.isSuccess() && channel.isActive() && WebSocketClientHandshaker.FORCE_CLOSE_INIT_UPDATER.compareAndSet(handshaker, 0, 1)) {
                    final java.util.concurrent.Future<?> forceCloseFuture = channel.eventLoop().schedule((Runnable)new Runnable() {
                        @Override
                        public void run() {
                            if (channel.isActive()) {
                                invoker.close();
                                WebSocketClientHandshaker.this.forceCloseComplete = true;
                            }
                        }
                    }, forceCloseTimeoutMillis, TimeUnit.MILLISECONDS);
                    channel.closeFuture().addListener((GenericFutureListener<? extends Future<? super Void>>)new ChannelFutureListener() {
                        @Override
                        public void operationComplete(final ChannelFuture future) throws Exception {
                            forceCloseFuture.cancel(false);
                        }
                    });
                }
            }
        });
        return promise;
    }
    
    protected String upgradeUrl(final URI wsURL) {
        if (this.absoluteUpgradeUrl) {
            return wsURL.toString();
        }
        String path = wsURL.getRawPath();
        path = ((path == null || path.isEmpty()) ? "/" : path);
        final String query = wsURL.getRawQuery();
        return (query != null && !query.isEmpty()) ? (path + '?' + query) : path;
    }
    
    static CharSequence websocketHostValue(final URI wsURL) {
        final int port = wsURL.getPort();
        if (port == -1) {
            return wsURL.getHost();
        }
        final String host = wsURL.getHost();
        final String scheme = wsURL.getScheme();
        if (port == HttpScheme.HTTP.port()) {
            return (HttpScheme.HTTP.name().contentEquals(scheme) || WebSocketScheme.WS.name().contentEquals(scheme)) ? host : NetUtil.toSocketAddressString(host, port);
        }
        if (port == HttpScheme.HTTPS.port()) {
            return (HttpScheme.HTTPS.name().contentEquals(scheme) || WebSocketScheme.WSS.name().contentEquals(scheme)) ? host : NetUtil.toSocketAddressString(host, port);
        }
        return NetUtil.toSocketAddressString(host, port);
    }
    
    static CharSequence websocketOriginValue(final URI wsURL) {
        final String scheme = wsURL.getScheme();
        final int port = wsURL.getPort();
        String schemePrefix;
        int defaultPort;
        if (WebSocketScheme.WSS.name().contentEquals(scheme) || HttpScheme.HTTPS.name().contentEquals(scheme) || (scheme == null && port == WebSocketScheme.WSS.port())) {
            schemePrefix = WebSocketClientHandshaker.HTTPS_SCHEME_PREFIX;
            defaultPort = WebSocketScheme.WSS.port();
        }
        else {
            schemePrefix = WebSocketClientHandshaker.HTTP_SCHEME_PREFIX;
            defaultPort = WebSocketScheme.WS.port();
        }
        final String host = wsURL.getHost().toLowerCase(Locale.US);
        if (port != defaultPort && port != -1) {
            return schemePrefix + NetUtil.toSocketAddressString(host, port);
        }
        return schemePrefix + host;
    }
    
    static {
        HTTP_SCHEME_PREFIX = HttpScheme.HTTP + "://";
        HTTPS_SCHEME_PREFIX = HttpScheme.HTTPS + "://";
        FORCE_CLOSE_INIT_UPDATER = AtomicIntegerFieldUpdater.newUpdater(WebSocketClientHandshaker.class, "forceCloseInit");
    }
}
