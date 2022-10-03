package com.turo.pushy.apns.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandlerAdapter;
import org.slf4j.LoggerFactory;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPipeline;
import javax.net.ssl.SSLSession;
import io.netty.handler.ssl.SslHandler;
import io.netty.channel.Channel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.ChannelInitializer;
import io.netty.util.concurrent.EventExecutor;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.ReferenceCounted;
import io.netty.channel.EventLoopGroup;
import org.slf4j.Logger;
import io.netty.channel.group.ChannelGroup;
import io.netty.bootstrap.ServerBootstrap;
import java.util.concurrent.atomic.AtomicBoolean;
import io.netty.handler.ssl.SslContext;

abstract class BaseHttp2Server
{
    private final SslContext sslContext;
    private final AtomicBoolean hasReleasedSslContext;
    private final ServerBootstrap bootstrap;
    private final boolean shouldShutDownEventLoopGroup;
    private final ChannelGroup allChannels;
    private static final Logger log;
    
    BaseHttp2Server(final SslContext sslContext, final EventLoopGroup eventLoopGroup) {
        this.hasReleasedSslContext = new AtomicBoolean(false);
        this.sslContext = sslContext;
        if (this.sslContext instanceof ReferenceCounted) {
            ((ReferenceCounted)this.sslContext).retain();
        }
        this.bootstrap = new ServerBootstrap();
        if (eventLoopGroup != null) {
            this.bootstrap.group(eventLoopGroup);
            this.shouldShutDownEventLoopGroup = false;
        }
        else {
            this.bootstrap.group((EventLoopGroup)new NioEventLoopGroup(1));
            this.shouldShutDownEventLoopGroup = true;
        }
        this.allChannels = (ChannelGroup)new DefaultChannelGroup((EventExecutor)this.bootstrap.config().group().next());
        this.bootstrap.channel((Class)ServerChannelClassUtil.getServerSocketChannelClass(this.bootstrap.config().group()));
        this.bootstrap.childHandler((ChannelHandler)new ChannelInitializer<SocketChannel>() {
            protected void initChannel(final SocketChannel channel) {
                final SslHandler sslHandler = sslContext.newHandler(channel.alloc());
                channel.pipeline().addLast(new ChannelHandler[] { (ChannelHandler)sslHandler });
                channel.pipeline().addLast(new ChannelHandler[] { (ChannelHandler)ConnectionNegotiationErrorHandler.INSTANCE });
                sslHandler.handshakeFuture().addListener((GenericFutureListener)new GenericFutureListener<Future<Channel>>() {
                    public void operationComplete(final Future<Channel> handshakeFuture) throws Exception {
                        if (handshakeFuture.isSuccess()) {
                            BaseHttp2Server.this.addHandlersToPipeline(sslHandler.engine().getSession(), channel.pipeline());
                            channel.pipeline().remove((ChannelHandler)ConnectionNegotiationErrorHandler.INSTANCE);
                            BaseHttp2Server.this.allChannels.add((Object)channel);
                        }
                        else {
                            BaseHttp2Server.log.debug("TLS handshake failed.", handshakeFuture.cause());
                        }
                    }
                });
            }
        });
    }
    
    protected abstract void addHandlersToPipeline(final SSLSession p0, final ChannelPipeline p1) throws Exception;
    
    public Future<Void> start(final int port) {
        final ChannelFuture channelFuture = this.bootstrap.bind(port);
        this.allChannels.add((Object)channelFuture.channel());
        return (Future<Void>)channelFuture;
    }
    
    public Future<Void> shutdown() {
        final Future<Void> channelCloseFuture = (Future<Void>)this.allChannels.close();
        Future<Void> disconnectFuture;
        if (this.shouldShutDownEventLoopGroup) {
            channelCloseFuture.addListener((GenericFutureListener)new GenericFutureListener<Future<Void>>() {
                public void operationComplete(final Future<Void> future) throws Exception {
                    BaseHttp2Server.this.bootstrap.config().group().shutdownGracefully();
                }
            });
            disconnectFuture = (Future<Void>)new DefaultPromise((EventExecutor)GlobalEventExecutor.INSTANCE);
            this.bootstrap.config().group().terminationFuture().addListener((GenericFutureListener)new GenericFutureListener() {
                public void operationComplete(final Future future) throws Exception {
                    ((Promise)disconnectFuture).trySuccess((Object)null);
                }
            });
        }
        else {
            disconnectFuture = channelCloseFuture;
        }
        disconnectFuture.addListener((GenericFutureListener)new GenericFutureListener<Future<Void>>() {
            public void operationComplete(final Future<Void> future) throws Exception {
                if (BaseHttp2Server.this.sslContext instanceof ReferenceCounted && BaseHttp2Server.this.hasReleasedSslContext.compareAndSet(false, true)) {
                    ((ReferenceCounted)BaseHttp2Server.this.sslContext).release();
                }
            }
        });
        return disconnectFuture;
    }
    
    static {
        log = LoggerFactory.getLogger((Class)BaseHttp2Server.class);
    }
    
    @ChannelHandler.Sharable
    private static class ConnectionNegotiationErrorHandler extends ChannelHandlerAdapter
    {
        static final ConnectionNegotiationErrorHandler INSTANCE;
        
        public void exceptionCaught(final ChannelHandlerContext context, final Throwable cause) {
            BaseHttp2Server.log.debug("Server caught an exception before establishing an HTTP/2 connection.", cause);
        }
        
        static {
            INSTANCE = new ConnectionNegotiationErrorHandler();
        }
    }
}
