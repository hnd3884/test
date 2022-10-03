package io.netty.handler.codec.http.websocketx;

import io.netty.util.concurrent.FutureListener;
import java.util.concurrent.TimeUnit;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Future;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.internal.ObjectUtil;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

class WebSocketClientProtocolHandshakeHandler extends ChannelInboundHandlerAdapter
{
    private static final long DEFAULT_HANDSHAKE_TIMEOUT_MS = 10000L;
    private final WebSocketClientHandshaker handshaker;
    private final long handshakeTimeoutMillis;
    private ChannelHandlerContext ctx;
    private ChannelPromise handshakePromise;
    
    WebSocketClientProtocolHandshakeHandler(final WebSocketClientHandshaker handshaker) {
        this(handshaker, 10000L);
    }
    
    WebSocketClientProtocolHandshakeHandler(final WebSocketClientHandshaker handshaker, final long handshakeTimeoutMillis) {
        this.handshaker = handshaker;
        this.handshakeTimeoutMillis = ObjectUtil.checkPositive(handshakeTimeoutMillis, "handshakeTimeoutMillis");
    }
    
    @Override
    public void handlerAdded(final ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        this.handshakePromise = ctx.newPromise();
    }
    
    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.handshaker.handshake(ctx.channel()).addListener((GenericFutureListener<? extends Future<? super Void>>)new ChannelFutureListener() {
            @Override
            public void operationComplete(final ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {
                    WebSocketClientProtocolHandshakeHandler.this.handshakePromise.tryFailure(future.cause());
                    ctx.fireExceptionCaught(future.cause());
                }
                else {
                    ctx.fireUserEventTriggered((Object)WebSocketClientProtocolHandler.ClientHandshakeStateEvent.HANDSHAKE_ISSUED);
                }
            }
        });
        this.applyHandshakeTimeout();
    }
    
    @Override
    public void channelInactive(final ChannelHandlerContext ctx) throws Exception {
        if (!this.handshakePromise.isDone()) {
            this.handshakePromise.tryFailure(new WebSocketClientHandshakeException("channel closed with handshake in progress"));
        }
        super.channelInactive(ctx);
    }
    
    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        if (!(msg instanceof FullHttpResponse)) {
            ctx.fireChannelRead(msg);
            return;
        }
        final FullHttpResponse response = (FullHttpResponse)msg;
        try {
            if (!this.handshaker.isHandshakeComplete()) {
                this.handshaker.finishHandshake(ctx.channel(), response);
                this.handshakePromise.trySuccess();
                ctx.fireUserEventTriggered((Object)WebSocketClientProtocolHandler.ClientHandshakeStateEvent.HANDSHAKE_COMPLETE);
                ctx.pipeline().remove(this);
                return;
            }
            throw new IllegalStateException("WebSocketClientHandshaker should have been non finished yet");
        }
        finally {
            response.release();
        }
    }
    
    private void applyHandshakeTimeout() {
        final ChannelPromise localHandshakePromise = this.handshakePromise;
        if (this.handshakeTimeoutMillis <= 0L || localHandshakePromise.isDone()) {
            return;
        }
        final Future<?> timeoutFuture = this.ctx.executor().schedule((Runnable)new Runnable() {
            @Override
            public void run() {
                if (localHandshakePromise.isDone()) {
                    return;
                }
                if (localHandshakePromise.tryFailure(new WebSocketClientHandshakeException("handshake timed out"))) {
                    WebSocketClientProtocolHandshakeHandler.this.ctx.flush().fireUserEventTriggered((Object)WebSocketClientProtocolHandler.ClientHandshakeStateEvent.HANDSHAKE_TIMEOUT).close();
                }
            }
        }, this.handshakeTimeoutMillis, TimeUnit.MILLISECONDS);
        localHandshakePromise.addListener((GenericFutureListener<? extends Future<? super Void>>)new FutureListener<Void>() {
            @Override
            public void operationComplete(final Future<Void> f) throws Exception {
                timeoutFuture.cancel(false);
            }
        });
    }
    
    ChannelFuture getHandshakeFuture() {
        return this.handshakePromise;
    }
}
