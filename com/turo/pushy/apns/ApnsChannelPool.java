package com.turo.pushy.apns;

import org.slf4j.LoggerFactory;
import java.util.Iterator;
import java.io.Closeable;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.channel.group.DefaultChannelGroup;
import java.util.HashSet;
import java.util.ArrayDeque;
import org.slf4j.Logger;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.Future;
import java.util.Set;
import java.util.Queue;
import io.netty.channel.group.ChannelGroup;
import io.netty.util.concurrent.OrderedEventExecutor;
import io.netty.channel.Channel;

class ApnsChannelPool
{
    private final PooledObjectFactory<Channel> channelFactory;
    private final OrderedEventExecutor executor;
    private final int capacity;
    private final ApnsChannelPoolMetricsListener metricsListener;
    private final ChannelGroup allChannels;
    private final Queue<Channel> idleChannels;
    private final Set<Future<Channel>> pendingCreateChannelFutures;
    private final Queue<Promise<Channel>> pendingAcquisitionPromises;
    private boolean isClosed;
    private static final Exception POOL_CLOSED_EXCEPTION;
    private static final Logger log;
    
    ApnsChannelPool(final PooledObjectFactory<Channel> channelFactory, final int capacity, final OrderedEventExecutor executor, final ApnsChannelPoolMetricsListener metricsListener) {
        this.idleChannels = new ArrayDeque<Channel>();
        this.pendingCreateChannelFutures = new HashSet<Future<Channel>>();
        this.pendingAcquisitionPromises = new ArrayDeque<Promise<Channel>>();
        this.isClosed = false;
        this.channelFactory = channelFactory;
        this.capacity = capacity;
        this.executor = executor;
        this.metricsListener = ((metricsListener != null) ? metricsListener : new NoopChannelPoolMetricsListener());
        this.allChannels = (ChannelGroup)new DefaultChannelGroup((EventExecutor)this.executor, true);
    }
    
    Future<Channel> acquire() {
        final Promise<Channel> acquirePromise = (Promise<Channel>)new DefaultPromise((EventExecutor)this.executor);
        if (this.executor.inEventLoop()) {
            this.acquireWithinEventExecutor(acquirePromise);
        }
        else {
            this.executor.submit((Runnable)new Runnable() {
                @Override
                public void run() {
                    ApnsChannelPool.this.acquireWithinEventExecutor(acquirePromise);
                }
            }).addListener((GenericFutureListener)new GenericFutureListener() {
                public void operationComplete(final Future future) throws Exception {
                    if (!future.isSuccess()) {
                        acquirePromise.tryFailure(future.cause());
                    }
                }
            });
        }
        return (Future<Channel>)acquirePromise;
    }
    
    private void acquireWithinEventExecutor(final Promise<Channel> acquirePromise) {
        assert this.executor.inEventLoop();
        if (!this.isClosed) {
            if (this.allChannels.size() + this.pendingCreateChannelFutures.size() < this.capacity) {
                final Future<Channel> createChannelFuture = this.channelFactory.create((io.netty.util.concurrent.Promise<Channel>)this.executor.newPromise());
                this.pendingCreateChannelFutures.add(createChannelFuture);
                createChannelFuture.addListener((GenericFutureListener)new GenericFutureListener<Future<Channel>>() {
                    public void operationComplete(final Future<Channel> future) {
                        ApnsChannelPool.this.pendingCreateChannelFutures.remove(createChannelFuture);
                        if (future.isSuccess()) {
                            final Channel channel = (Channel)future.getNow();
                            ApnsChannelPool.this.allChannels.add((Object)channel);
                            ApnsChannelPool.this.metricsListener.handleConnectionAdded();
                            acquirePromise.trySuccess((Object)channel);
                        }
                        else {
                            ApnsChannelPool.this.metricsListener.handleConnectionCreationFailed();
                            acquirePromise.tryFailure(future.cause());
                            ApnsChannelPool.this.handleNextAcquisition();
                        }
                    }
                });
            }
            else {
                final Channel channelFromIdlePool = this.idleChannels.poll();
                if (channelFromIdlePool != null) {
                    if (channelFromIdlePool.isActive()) {
                        acquirePromise.trySuccess((Object)channelFromIdlePool);
                    }
                    else {
                        this.discardChannel(channelFromIdlePool);
                        this.acquireWithinEventExecutor(acquirePromise);
                    }
                }
                else {
                    this.pendingAcquisitionPromises.add(acquirePromise);
                }
            }
        }
        else {
            acquirePromise.tryFailure((Throwable)ApnsChannelPool.POOL_CLOSED_EXCEPTION);
        }
    }
    
    void release(final Channel channel) {
        if (this.executor.inEventLoop()) {
            this.releaseWithinEventExecutor(channel);
        }
        else {
            this.executor.submit((Runnable)new Runnable() {
                @Override
                public void run() {
                    ApnsChannelPool.this.releaseWithinEventExecutor(channel);
                }
            });
        }
    }
    
    private void releaseWithinEventExecutor(final Channel channel) {
        assert this.executor.inEventLoop();
        this.idleChannels.add(channel);
        this.handleNextAcquisition();
    }
    
    private void handleNextAcquisition() {
        assert this.executor.inEventLoop();
        if (!this.pendingAcquisitionPromises.isEmpty()) {
            this.acquireWithinEventExecutor(this.pendingAcquisitionPromises.poll());
        }
    }
    
    private void discardChannel(final Channel channel) {
        assert this.executor.inEventLoop();
        this.idleChannels.remove(channel);
        this.allChannels.remove((Object)channel);
        this.metricsListener.handleConnectionRemoved();
        this.channelFactory.destroy(channel, (Promise<Void>)this.executor.newPromise()).addListener((GenericFutureListener)new GenericFutureListener<Future<Void>>() {
            public void operationComplete(final Future<Void> destroyFuture) throws Exception {
                if (!destroyFuture.isSuccess()) {
                    ApnsChannelPool.log.warn("Failed to destroy channel.", destroyFuture.cause());
                }
            }
        });
    }
    
    public Future<Void> close() {
        return (Future<Void>)this.allChannels.close().addListener((GenericFutureListener)new GenericFutureListener<Future<Void>>() {
            public void operationComplete(final Future<Void> future) throws Exception {
                ApnsChannelPool.this.isClosed = true;
                if (ApnsChannelPool.this.channelFactory instanceof Closeable) {
                    ((Closeable)ApnsChannelPool.this.channelFactory).close();
                }
                for (final Promise<Channel> acquisitionPromise : ApnsChannelPool.this.pendingAcquisitionPromises) {
                    acquisitionPromise.tryFailure((Throwable)ApnsChannelPool.POOL_CLOSED_EXCEPTION);
                }
            }
        });
    }
    
    static {
        POOL_CLOSED_EXCEPTION = new IllegalStateException("Channel pool has closed and no more channels may be acquired.");
        log = LoggerFactory.getLogger((Class)ApnsChannelPool.class);
    }
    
    private static class NoopChannelPoolMetricsListener implements ApnsChannelPoolMetricsListener
    {
        @Override
        public void handleConnectionAdded() {
        }
        
        @Override
        public void handleConnectionRemoved() {
        }
        
        @Override
        public void handleConnectionCreationFailed() {
        }
    }
}
