package io.netty.handler.codec.http.websocketx;

import io.netty.util.concurrent.FutureListener;
import java.util.concurrent.TimeUnit;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.ssl.SslHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Future;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.internal.ObjectUtil;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

class WebSocketServerProtocolHandshakeHandler extends ChannelInboundHandlerAdapter
{
    private final WebSocketServerProtocolConfig serverConfig;
    private ChannelHandlerContext ctx;
    private ChannelPromise handshakePromise;
    
    WebSocketServerProtocolHandshakeHandler(final WebSocketServerProtocolConfig serverConfig) {
        this.serverConfig = ObjectUtil.checkNotNull(serverConfig, "serverConfig");
    }
    
    @Override
    public void handlerAdded(final ChannelHandlerContext ctx) {
        this.ctx = ctx;
        this.handshakePromise = ctx.newPromise();
    }
    
    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        final FullHttpRequest req = (FullHttpRequest)msg;
        if (!this.isWebSocketPath(req)) {
            ctx.fireChannelRead(msg);
            return;
        }
        try {
            if (!HttpMethod.GET.equals(req.method())) {
                sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN, ctx.alloc().buffer(0)));
                return;
            }
            final WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(getWebSocketLocation(ctx.pipeline(), req, this.serverConfig.websocketPath()), this.serverConfig.subprotocols(), this.serverConfig.decoderConfig());
            final WebSocketServerHandshaker handshaker = wsFactory.newHandshaker(req);
            final ChannelPromise localHandshakePromise = this.handshakePromise;
            if (handshaker == null) {
                WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
            }
            else {
                WebSocketServerProtocolHandler.setHandshaker(ctx.channel(), handshaker);
                ctx.pipeline().remove(this);
                final ChannelFuture handshakeFuture = handshaker.handshake(ctx.channel(), req);
                handshakeFuture.addListener((GenericFutureListener<? extends Future<? super Void>>)new ChannelFutureListener() {
                    @Override
                    public void operationComplete(final ChannelFuture future) {
                        if (!future.isSuccess()) {
                            localHandshakePromise.tryFailure(future.cause());
                            ctx.fireExceptionCaught(future.cause());
                        }
                        else {
                            localHandshakePromise.trySuccess();
                            ctx.fireUserEventTriggered((Object)WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE);
                            ctx.fireUserEventTriggered((Object)new WebSocketServerProtocolHandler.HandshakeComplete(req.uri(), req.headers(), handshaker.selectedSubprotocol()));
                        }
                    }
                });
                this.applyHandshakeTimeout();
            }
        }
        finally {
            req.release();
        }
    }
    
    private boolean isWebSocketPath(final FullHttpRequest req) {
        final String websocketPath = this.serverConfig.websocketPath();
        final String uri = req.uri();
        final boolean checkStartUri = uri.startsWith(websocketPath);
        final boolean checkNextUri = "/".equals(websocketPath) || this.checkNextUri(uri, websocketPath);
        return this.serverConfig.checkStartsWith() ? (checkStartUri && checkNextUri) : uri.equals(websocketPath);
    }
    
    private boolean checkNextUri(final String uri, final String websocketPath) {
        final int len = websocketPath.length();
        if (uri.length() > len) {
            final char nextUri = uri.charAt(len);
            return nextUri == '/' || nextUri == '?';
        }
        return true;
    }
    
    private static void sendHttpResponse(final ChannelHandlerContext ctx, final HttpRequest req, final HttpResponse res) {
        final ChannelFuture f = ctx.channel().writeAndFlush(res);
        if (!HttpUtil.isKeepAlive(req) || res.status().code() != 200) {
            f.addListener((GenericFutureListener<? extends Future<? super Void>>)ChannelFutureListener.CLOSE);
        }
    }
    
    private static String getWebSocketLocation(final ChannelPipeline cp, final HttpRequest req, final String path) {
        String protocol = "ws";
        if (cp.get(SslHandler.class) != null) {
            protocol = "wss";
        }
        final String host = req.headers().get(HttpHeaderNames.HOST);
        return protocol + "://" + host + path;
    }
    
    private void applyHandshakeTimeout() {
        final ChannelPromise localHandshakePromise = this.handshakePromise;
        final long handshakeTimeoutMillis = this.serverConfig.handshakeTimeoutMillis();
        if (handshakeTimeoutMillis <= 0L || localHandshakePromise.isDone()) {
            return;
        }
        final Future<?> timeoutFuture = this.ctx.executor().schedule((Runnable)new Runnable() {
            @Override
            public void run() {
                if (!localHandshakePromise.isDone() && localHandshakePromise.tryFailure(new WebSocketServerHandshakeException("handshake timed out"))) {
                    WebSocketServerProtocolHandshakeHandler.this.ctx.flush().fireUserEventTriggered((Object)WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_TIMEOUT).close();
                }
            }
        }, handshakeTimeoutMillis, TimeUnit.MILLISECONDS);
        localHandshakePromise.addListener((GenericFutureListener<? extends Future<? super Void>>)new FutureListener<Void>() {
            @Override
            public void operationComplete(final Future<Void> f) {
                timeoutFuture.cancel(false);
            }
        });
    }
}
