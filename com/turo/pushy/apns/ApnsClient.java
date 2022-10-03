package com.turo.pushy.apns;

import org.slf4j.LoggerFactory;
import io.netty.util.concurrent.SucceededFuture;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.GlobalEventExecutor;
import com.turo.pushy.apns.util.concurrent.PushNotificationResponseListener;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.EventExecutor;
import com.turo.pushy.apns.util.concurrent.PushNotificationFuture;
import io.netty.util.concurrent.OrderedEventExecutor;
import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.http2.Http2FrameLogger;
import com.turo.pushy.apns.proxy.ProxyHandlerFactory;
import com.turo.pushy.apns.auth.ApnsSigningKey;
import io.netty.handler.ssl.SslContext;
import java.net.InetSocketAddress;
import org.slf4j.Logger;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import io.netty.channel.EventLoopGroup;

public class ApnsClient
{
    private final EventLoopGroup eventLoopGroup;
    private final boolean shouldShutDownEventLoopGroup;
    private final ApnsChannelPool channelPool;
    private final ApnsClientMetricsListener metricsListener;
    private final AtomicLong nextNotificationId;
    private final AtomicBoolean isClosed;
    private static final IllegalStateException CLIENT_CLOSED_EXCEPTION;
    private static final Logger log;
    
    protected ApnsClient(final InetSocketAddress apnsServerAddress, final SslContext sslContext, final ApnsSigningKey signingKey, final ProxyHandlerFactory proxyHandlerFactory, final int connectTimeoutMillis, final long idlePingIntervalMillis, final long gracefulShutdownTimeoutMillis, final int concurrentConnections, final ApnsClientMetricsListener metricsListener, final Http2FrameLogger frameLogger, final EventLoopGroup eventLoopGroup) {
        this.nextNotificationId = new AtomicLong(0L);
        this.isClosed = new AtomicBoolean(false);
        if (eventLoopGroup != null) {
            this.eventLoopGroup = eventLoopGroup;
            this.shouldShutDownEventLoopGroup = false;
        }
        else {
            this.eventLoopGroup = (EventLoopGroup)new NioEventLoopGroup(1);
            this.shouldShutDownEventLoopGroup = true;
        }
        this.metricsListener = ((metricsListener != null) ? metricsListener : new NoopApnsClientMetricsListener());
        final ApnsChannelFactory channelFactory = new ApnsChannelFactory(sslContext, signingKey, proxyHandlerFactory, connectTimeoutMillis, idlePingIntervalMillis, gracefulShutdownTimeoutMillis, frameLogger, apnsServerAddress, this.eventLoopGroup);
        final ApnsChannelPoolMetricsListener channelPoolMetricsListener = new ApnsChannelPoolMetricsListener() {
            @Override
            public void handleConnectionAdded() {
                ApnsClient.this.metricsListener.handleConnectionAdded(ApnsClient.this);
            }
            
            @Override
            public void handleConnectionRemoved() {
                ApnsClient.this.metricsListener.handleConnectionRemoved(ApnsClient.this);
            }
            
            @Override
            public void handleConnectionCreationFailed() {
                ApnsClient.this.metricsListener.handleConnectionCreationFailed(ApnsClient.this);
            }
        };
        this.channelPool = new ApnsChannelPool(channelFactory, concurrentConnections, (OrderedEventExecutor)this.eventLoopGroup.next(), channelPoolMetricsListener);
    }
    
    public <T extends ApnsPushNotification> PushNotificationFuture<T, PushNotificationResponse<T>> sendNotification(final T notification) {
        PushNotificationFuture<T, PushNotificationResponse<T>> responseFuture;
        if (!this.isClosed.get()) {
            final PushNotificationPromise<T, PushNotificationResponse<T>> responsePromise = new PushNotificationPromise<T, PushNotificationResponse<T>>((EventExecutor)this.eventLoopGroup.next(), notification);
            final long notificationId = this.nextNotificationId.getAndIncrement();
            this.channelPool.acquire().addListener((GenericFutureListener)new GenericFutureListener<Future<Channel>>() {
                public void operationComplete(final Future<Channel> acquireFuture) throws Exception {
                    if (acquireFuture.isSuccess()) {
                        final Channel channel = (Channel)acquireFuture.getNow();
                        channel.writeAndFlush((Object)responsePromise).addListener((GenericFutureListener)new GenericFutureListener<ChannelFuture>() {
                            public void operationComplete(final ChannelFuture future) throws Exception {
                                if (future.isSuccess()) {
                                    ApnsClient.this.metricsListener.handleNotificationSent(ApnsClient.this, notificationId);
                                }
                            }
                        });
                        ApnsClient.this.channelPool.release(channel);
                    }
                    else {
                        responsePromise.tryFailure(acquireFuture.cause());
                    }
                }
            });
            responsePromise.addListener((GenericFutureListener)new PushNotificationResponseListener<T>() {
                public void operationComplete(final PushNotificationFuture<T, PushNotificationResponse<T>> future) throws Exception {
                    if (future.isSuccess()) {
                        final PushNotificationResponse response = (PushNotificationResponse)future.getNow();
                        if (response.isAccepted()) {
                            ApnsClient.this.metricsListener.handleNotificationAccepted(ApnsClient.this, notificationId);
                        }
                        else {
                            ApnsClient.this.metricsListener.handleNotificationRejected(ApnsClient.this, notificationId);
                        }
                    }
                    else {
                        ApnsClient.this.metricsListener.handleWriteFailure(ApnsClient.this, notificationId);
                    }
                }
            });
            responseFuture = responsePromise;
        }
        else {
            final PushNotificationPromise<T, PushNotificationResponse<T>> failedPromise = new PushNotificationPromise<T, PushNotificationResponse<T>>((EventExecutor)GlobalEventExecutor.INSTANCE, notification);
            failedPromise.setFailure((Throwable)ApnsClient.CLIENT_CLOSED_EXCEPTION);
            responseFuture = failedPromise;
        }
        return responseFuture;
    }
    
    public Future<Void> close() {
        ApnsClient.log.info("Shutting down.");
        Future<Void> closeFuture;
        if (this.isClosed.compareAndSet(false, true)) {
            final Promise<Void> closePromise = (Promise<Void>)new DefaultPromise((EventExecutor)GlobalEventExecutor.INSTANCE);
            this.channelPool.close().addListener((GenericFutureListener)new GenericFutureListener<Future<Void>>() {
                public void operationComplete(final Future<Void> closePoolFuture) throws Exception {
                    if (ApnsClient.this.shouldShutDownEventLoopGroup) {
                        ApnsClient.this.eventLoopGroup.shutdownGracefully().addListener((GenericFutureListener)new GenericFutureListener() {
                            public void operationComplete(final Future future) throws Exception {
                                closePromise.trySuccess((Object)null);
                            }
                        });
                    }
                    else {
                        closePromise.trySuccess((Object)null);
                    }
                }
            });
            closeFuture = (Future<Void>)closePromise;
        }
        else {
            closeFuture = (Future<Void>)new SucceededFuture((EventExecutor)GlobalEventExecutor.INSTANCE, (Object)null);
        }
        return closeFuture;
    }
    
    static {
        CLIENT_CLOSED_EXCEPTION = new IllegalStateException("Client has been closed and can no longer send push notifications.");
        log = LoggerFactory.getLogger((Class)ApnsClient.class);
    }
    
    private static class NoopApnsClientMetricsListener implements ApnsClientMetricsListener
    {
        @Override
        public void handleWriteFailure(final ApnsClient apnsClient, final long notificationId) {
        }
        
        @Override
        public void handleNotificationSent(final ApnsClient apnsClient, final long notificationId) {
        }
        
        @Override
        public void handleNotificationAccepted(final ApnsClient apnsClient, final long notificationId) {
        }
        
        @Override
        public void handleNotificationRejected(final ApnsClient apnsClient, final long notificationId) {
        }
        
        @Override
        public void handleConnectionAdded(final ApnsClient apnsClient) {
        }
        
        @Override
        public void handleConnectionRemoved(final ApnsClient apnsClient) {
        }
        
        @Override
        public void handleConnectionCreationFailed(final ApnsClient apnsClient) {
        }
    }
}
