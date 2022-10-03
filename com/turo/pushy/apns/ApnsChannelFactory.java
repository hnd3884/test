package com.turo.pushy.apns;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandlerAdapter;
import org.slf4j.LoggerFactory;
import io.netty.util.concurrent.PromiseNotifier;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFactory;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.IdleStateHandler;
import java.util.concurrent.TimeUnit;
import io.netty.handler.flush.FlushConsolidationHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.ChannelInitializer;
import java.net.SocketAddress;
import io.netty.channel.ChannelOption;
import io.netty.resolver.NoopAddressResolverGroup;
import io.netty.resolver.dns.DnsServerAddressStreamProvider;
import io.netty.resolver.dns.RoundRobinDnsAddressResolverGroup;
import io.netty.resolver.dns.DefaultDnsServerAddressStreamProvider;
import io.netty.util.ReferenceCounted;
import io.netty.channel.EventLoopGroup;
import java.net.InetSocketAddress;
import io.netty.handler.codec.http2.Http2FrameLogger;
import com.turo.pushy.apns.proxy.ProxyHandlerFactory;
import com.turo.pushy.apns.auth.ApnsSigningKey;
import org.slf4j.Logger;
import io.netty.util.concurrent.Promise;
import io.netty.util.AttributeKey;
import java.util.concurrent.atomic.AtomicLong;
import io.netty.bootstrap.Bootstrap;
import io.netty.resolver.AddressResolverGroup;
import java.util.concurrent.atomic.AtomicBoolean;
import io.netty.handler.ssl.SslContext;
import java.io.Closeable;
import io.netty.channel.Channel;

class ApnsChannelFactory implements PooledObjectFactory<Channel>, Closeable
{
    private final SslContext sslContext;
    private final AtomicBoolean hasReleasedSslContext;
    private final AddressResolverGroup addressResolverGroup;
    private final Bootstrap bootstrapTemplate;
    private final AtomicLong currentDelaySeconds;
    private static final long MIN_CONNECT_DELAY_SECONDS = 1L;
    private static final long MAX_CONNECT_DELAY_SECONDS = 60L;
    private static final AttributeKey<Promise<Channel>> CHANNEL_READY_PROMISE_ATTRIBUTE_KEY;
    private static final Logger log;
    
    ApnsChannelFactory(final SslContext sslContext, final ApnsSigningKey signingKey, final ProxyHandlerFactory proxyHandlerFactory, final int connectTimeoutMillis, final long idlePingIntervalMillis, final long gracefulShutdownTimeoutMillis, final Http2FrameLogger frameLogger, final InetSocketAddress apnsServerAddress, final EventLoopGroup eventLoopGroup) {
        this.hasReleasedSslContext = new AtomicBoolean(false);
        this.currentDelaySeconds = new AtomicLong(0L);
        this.sslContext = sslContext;
        if (this.sslContext instanceof ReferenceCounted) {
            ((ReferenceCounted)this.sslContext).retain();
        }
        this.addressResolverGroup = (AddressResolverGroup)((proxyHandlerFactory == null) ? new RoundRobinDnsAddressResolverGroup((Class)ClientChannelClassUtil.getDatagramChannelClass(eventLoopGroup), (DnsServerAddressStreamProvider)DefaultDnsServerAddressStreamProvider.INSTANCE) : NoopAddressResolverGroup.INSTANCE);
        (this.bootstrapTemplate = new Bootstrap()).group(eventLoopGroup);
        this.bootstrapTemplate.option(ChannelOption.TCP_NODELAY, (Object)true);
        this.bootstrapTemplate.remoteAddress((SocketAddress)apnsServerAddress);
        this.bootstrapTemplate.resolver(this.addressResolverGroup);
        if (connectTimeoutMillis > 0) {
            this.bootstrapTemplate.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (Object)connectTimeoutMillis);
        }
        this.bootstrapTemplate.handler((ChannelHandler)new ChannelInitializer<SocketChannel>() {
            protected void initChannel(final SocketChannel channel) {
                final ChannelPipeline pipeline = channel.pipeline();
                if (proxyHandlerFactory != null) {
                    pipeline.addFirst(new ChannelHandler[] { (ChannelHandler)proxyHandlerFactory.createProxyHandler() });
                }
                final SslHandler sslHandler = sslContext.newHandler(channel.alloc());
                sslHandler.handshakeFuture().addListener((GenericFutureListener)new GenericFutureListener<Future<Channel>>() {
                    public void operationComplete(final Future<Channel> handshakeFuture) {
                        if (handshakeFuture.isSuccess()) {
                            final String authority = channel.remoteAddress().getHostName();
                            ApnsClientHandler.ApnsClientHandlerBuilder clientHandlerBuilder;
                            if (signingKey != null) {
                                clientHandlerBuilder = new TokenAuthenticationApnsClientHandler.TokenAuthenticationApnsClientHandlerBuilder().signingKey(signingKey).authority(authority).idlePingIntervalMillis(idlePingIntervalMillis);
                            }
                            else {
                                clientHandlerBuilder = new ApnsClientHandler.ApnsClientHandlerBuilder().authority(authority).idlePingIntervalMillis(idlePingIntervalMillis);
                            }
                            if (frameLogger != null) {
                                clientHandlerBuilder.frameLogger(frameLogger);
                            }
                            final ApnsClientHandler apnsClientHandler = clientHandlerBuilder.build();
                            if (gracefulShutdownTimeoutMillis > 0L) {
                                apnsClientHandler.gracefulShutdownTimeoutMillis(gracefulShutdownTimeoutMillis);
                            }
                            pipeline.addLast(new ChannelHandler[] { (ChannelHandler)new FlushConsolidationHandler(256, true) });
                            pipeline.addLast(new ChannelHandler[] { (ChannelHandler)new IdleStateHandler(idlePingIntervalMillis, 0L, 0L, TimeUnit.MILLISECONDS) });
                            pipeline.addLast(new ChannelHandler[] { (ChannelHandler)apnsClientHandler });
                            pipeline.remove((ChannelHandler)ConnectionNegotiationErrorHandler.INSTANCE);
                            ((Promise)channel.attr(ApnsChannelFactory.CHANNEL_READY_PROMISE_ATTRIBUTE_KEY).get()).trySuccess((Object)channel);
                        }
                        else {
                            tryFailureAndLogRejectedCause((Promise<?>)channel.attr(ApnsChannelFactory.CHANNEL_READY_PROMISE_ATTRIBUTE_KEY).get(), handshakeFuture.cause());
                        }
                    }
                });
                pipeline.addLast(new ChannelHandler[] { (ChannelHandler)sslHandler });
                pipeline.addLast(new ChannelHandler[] { (ChannelHandler)ConnectionNegotiationErrorHandler.INSTANCE });
            }
        });
    }
    
    @Override
    public Future<Channel> create(final Promise<Channel> channelReadyPromise) {
        final long delay = this.currentDelaySeconds.get();
        channelReadyPromise.addListener((GenericFutureListener)new GenericFutureListener<Future<Channel>>() {
            public void operationComplete(final Future<Channel> future) {
                final long updatedDelay = future.isSuccess() ? 0L : Math.max(Math.min(delay * 2L, 60L), 1L);
                ApnsChannelFactory.this.currentDelaySeconds.compareAndSet(delay, updatedDelay);
            }
        });
        this.bootstrapTemplate.config().group().schedule((Runnable)new Runnable() {
            @Override
            public void run() {
                final Bootstrap bootstrap = (Bootstrap)ApnsChannelFactory.this.bootstrapTemplate.clone().channelFactory((ChannelFactory)new AugmentingReflectiveChannelFactory((Class<? extends Channel>)ClientChannelClassUtil.getSocketChannelClass(ApnsChannelFactory.this.bootstrapTemplate.config().group()), (io.netty.util.AttributeKey<Object>)ApnsChannelFactory.CHANNEL_READY_PROMISE_ATTRIBUTE_KEY, channelReadyPromise));
                final ChannelFuture connectFuture = bootstrap.connect();
                connectFuture.addListener((GenericFutureListener)new GenericFutureListener<ChannelFuture>() {
                    public void operationComplete(final ChannelFuture future) {
                        if (!future.isSuccess()) {
                            tryFailureAndLogRejectedCause(channelReadyPromise, future.cause());
                        }
                    }
                });
                connectFuture.channel().closeFuture().addListener((GenericFutureListener)new GenericFutureListener<ChannelFuture>() {
                    public void operationComplete(final ChannelFuture future) {
                        channelReadyPromise.tryFailure((Throwable)new IllegalStateException("Channel closed before HTTP/2 preface completed."));
                    }
                });
            }
        }, delay, TimeUnit.SECONDS);
        return (Future<Channel>)channelReadyPromise;
    }
    
    @Override
    public Future<Void> destroy(final Channel channel, final Promise<Void> promise) {
        channel.close().addListener((GenericFutureListener)new PromiseNotifier(new Promise[] { promise }));
        return (Future<Void>)promise;
    }
    
    @Override
    public void close() {
        this.addressResolverGroup.close();
        if (this.sslContext instanceof ReferenceCounted && this.hasReleasedSslContext.compareAndSet(false, true)) {
            ((ReferenceCounted)this.sslContext).release();
        }
    }
    
    private static void tryFailureAndLogRejectedCause(final Promise<?> promise, final Throwable cause) {
        if (!promise.tryFailure(cause)) {
            ApnsChannelFactory.log.warn("Tried to mark promise as \"failed,\" but it was already done.", cause);
        }
    }
    
    static {
        CHANNEL_READY_PROMISE_ATTRIBUTE_KEY = AttributeKey.valueOf((Class)ApnsChannelFactory.class, "channelReadyPromise");
        log = LoggerFactory.getLogger((Class)ApnsChannelFactory.class);
    }
    
    @ChannelHandler.Sharable
    private static class ConnectionNegotiationErrorHandler extends ChannelHandlerAdapter
    {
        static final ConnectionNegotiationErrorHandler INSTANCE;
        
        public void exceptionCaught(final ChannelHandlerContext context, final Throwable cause) {
            tryFailureAndLogRejectedCause((Promise<?>)context.channel().attr(ApnsChannelFactory.CHANNEL_READY_PROMISE_ATTRIBUTE_KEY).get(), cause);
        }
        
        static {
            INSTANCE = new ConnectionNegotiationErrorHandler();
        }
    }
}
