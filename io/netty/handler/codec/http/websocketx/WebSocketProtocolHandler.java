package io.netty.handler.codec.http.websocketx;

import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;
import io.netty.util.concurrent.PromiseNotifier;
import io.netty.util.concurrent.Promise;
import java.nio.channels.ClosedChannelException;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Future;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import java.util.List;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.handler.codec.MessageToMessageDecoder;

abstract class WebSocketProtocolHandler extends MessageToMessageDecoder<WebSocketFrame> implements ChannelOutboundHandler
{
    private final boolean dropPongFrames;
    private final WebSocketCloseStatus closeStatus;
    private final long forceCloseTimeoutMillis;
    private ChannelPromise closeSent;
    
    WebSocketProtocolHandler() {
        this(true);
    }
    
    WebSocketProtocolHandler(final boolean dropPongFrames) {
        this(dropPongFrames, null, 0L);
    }
    
    WebSocketProtocolHandler(final boolean dropPongFrames, final WebSocketCloseStatus closeStatus, final long forceCloseTimeoutMillis) {
        this.dropPongFrames = dropPongFrames;
        this.closeStatus = closeStatus;
        this.forceCloseTimeoutMillis = forceCloseTimeoutMillis;
    }
    
    @Override
    protected void decode(final ChannelHandlerContext ctx, final WebSocketFrame frame, final List<Object> out) throws Exception {
        if (frame instanceof PingWebSocketFrame) {
            frame.content().retain();
            ctx.channel().writeAndFlush(new PongWebSocketFrame(frame.content()));
            readIfNeeded(ctx);
            return;
        }
        if (frame instanceof PongWebSocketFrame && this.dropPongFrames) {
            readIfNeeded(ctx);
            return;
        }
        out.add(frame.retain());
    }
    
    private static void readIfNeeded(final ChannelHandlerContext ctx) {
        if (!ctx.channel().config().isAutoRead()) {
            ctx.read();
        }
    }
    
    @Override
    public void close(final ChannelHandlerContext ctx, final ChannelPromise promise) throws Exception {
        if (this.closeStatus == null || !ctx.channel().isActive()) {
            ctx.close(promise);
        }
        else {
            if (this.closeSent == null) {
                this.write(ctx, new CloseWebSocketFrame(this.closeStatus), ctx.newPromise());
            }
            this.flush(ctx);
            this.applyCloseSentTimeout(ctx);
            this.closeSent.addListener((GenericFutureListener<? extends Future<? super Void>>)new ChannelFutureListener() {
                @Override
                public void operationComplete(final ChannelFuture future) {
                    ctx.close(promise);
                }
            });
        }
    }
    
    @Override
    public void write(final ChannelHandlerContext ctx, final Object msg, final ChannelPromise promise) throws Exception {
        if (this.closeSent != null) {
            ReferenceCountUtil.release(msg);
            promise.setFailure((Throwable)new ClosedChannelException());
        }
        else if (msg instanceof CloseWebSocketFrame) {
            this.closeSent(promise.unvoid());
            ctx.write(msg).addListener((GenericFutureListener<? extends Future<? super Void>>)new PromiseNotifier<Object, Future<? super Void>>(false, (Promise<?>[])new Promise[] { this.closeSent }));
        }
        else {
            ctx.write(msg, promise);
        }
    }
    
    void closeSent(final ChannelPromise promise) {
        this.closeSent = promise;
    }
    
    private void applyCloseSentTimeout(final ChannelHandlerContext ctx) {
        if (this.closeSent.isDone() || this.forceCloseTimeoutMillis < 0L) {
            return;
        }
        final Future<?> timeoutTask = ctx.executor().schedule((Runnable)new Runnable() {
            @Override
            public void run() {
                if (!WebSocketProtocolHandler.this.closeSent.isDone()) {
                    WebSocketProtocolHandler.this.closeSent.tryFailure(WebSocketProtocolHandler.this.buildHandshakeException("send close frame timed out"));
                }
            }
        }, this.forceCloseTimeoutMillis, TimeUnit.MILLISECONDS);
        this.closeSent.addListener((GenericFutureListener<? extends Future<? super Void>>)new ChannelFutureListener() {
            @Override
            public void operationComplete(final ChannelFuture future) {
                timeoutTask.cancel(false);
            }
        });
    }
    
    protected WebSocketHandshakeException buildHandshakeException(final String message) {
        return new WebSocketHandshakeException(message);
    }
    
    @Override
    public void bind(final ChannelHandlerContext ctx, final SocketAddress localAddress, final ChannelPromise promise) throws Exception {
        ctx.bind(localAddress, promise);
    }
    
    @Override
    public void connect(final ChannelHandlerContext ctx, final SocketAddress remoteAddress, final SocketAddress localAddress, final ChannelPromise promise) throws Exception {
        ctx.connect(remoteAddress, localAddress, promise);
    }
    
    @Override
    public void disconnect(final ChannelHandlerContext ctx, final ChannelPromise promise) throws Exception {
        ctx.disconnect(promise);
    }
    
    @Override
    public void deregister(final ChannelHandlerContext ctx, final ChannelPromise promise) throws Exception {
        ctx.deregister(promise);
    }
    
    @Override
    public void read(final ChannelHandlerContext ctx) throws Exception {
        ctx.read();
    }
    
    @Override
    public void flush(final ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
    
    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
        ctx.fireExceptionCaught(cause);
        ctx.close();
    }
}
