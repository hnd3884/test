package io.netty.channel.pool;

import io.netty.bootstrap.AbstractBootstrap;
import java.util.concurrent.Callable;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.concurrent.FutureListener;
import io.netty.channel.EventLoop;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.Future;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import java.util.Deque;
import io.netty.util.AttributeKey;

public class SimpleChannelPool implements ChannelPool
{
    private static final AttributeKey<SimpleChannelPool> POOL_KEY;
    private final Deque<Channel> deque;
    private final ChannelPoolHandler handler;
    private final ChannelHealthChecker healthCheck;
    private final Bootstrap bootstrap;
    private final boolean releaseHealthCheck;
    private final boolean lastRecentUsed;
    
    public SimpleChannelPool(final Bootstrap bootstrap, final ChannelPoolHandler handler) {
        this(bootstrap, handler, ChannelHealthChecker.ACTIVE);
    }
    
    public SimpleChannelPool(final Bootstrap bootstrap, final ChannelPoolHandler handler, final ChannelHealthChecker healthCheck) {
        this(bootstrap, handler, healthCheck, true);
    }
    
    public SimpleChannelPool(final Bootstrap bootstrap, final ChannelPoolHandler handler, final ChannelHealthChecker healthCheck, final boolean releaseHealthCheck) {
        this(bootstrap, handler, healthCheck, releaseHealthCheck, true);
    }
    
    public SimpleChannelPool(final Bootstrap bootstrap, final ChannelPoolHandler handler, final ChannelHealthChecker healthCheck, final boolean releaseHealthCheck, final boolean lastRecentUsed) {
        this.deque = PlatformDependent.newConcurrentDeque();
        this.handler = ObjectUtil.checkNotNull(handler, "handler");
        this.healthCheck = ObjectUtil.checkNotNull(healthCheck, "healthCheck");
        this.releaseHealthCheck = releaseHealthCheck;
        (this.bootstrap = ObjectUtil.checkNotNull(bootstrap, "bootstrap").clone()).handler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(final Channel ch) throws Exception {
                assert ch.eventLoop().inEventLoop();
                handler.channelCreated(ch);
            }
        });
        this.lastRecentUsed = lastRecentUsed;
    }
    
    protected Bootstrap bootstrap() {
        return this.bootstrap;
    }
    
    protected ChannelPoolHandler handler() {
        return this.handler;
    }
    
    protected ChannelHealthChecker healthChecker() {
        return this.healthCheck;
    }
    
    protected boolean releaseHealthCheck() {
        return this.releaseHealthCheck;
    }
    
    @Override
    public final Future<Channel> acquire() {
        return this.acquire(this.bootstrap.config().group().next().newPromise());
    }
    
    @Override
    public Future<Channel> acquire(final Promise<Channel> promise) {
        return this.acquireHealthyFromPoolOrNew(ObjectUtil.checkNotNull(promise, "promise"));
    }
    
    private Future<Channel> acquireHealthyFromPoolOrNew(final Promise<Channel> promise) {
        try {
            final Channel ch = this.pollChannel();
            if (ch == null) {
                final Bootstrap bs = this.bootstrap.clone();
                ((AbstractBootstrap<AbstractBootstrap, Channel>)bs).attr(SimpleChannelPool.POOL_KEY, this);
                final ChannelFuture f = this.connectChannel(bs);
                if (f.isDone()) {
                    this.notifyConnect(f, promise);
                }
                else {
                    f.addListener((GenericFutureListener<? extends Future<? super Void>>)new ChannelFutureListener() {
                        @Override
                        public void operationComplete(final ChannelFuture future) throws Exception {
                            SimpleChannelPool.this.notifyConnect(future, promise);
                        }
                    });
                }
            }
            else {
                final EventLoop loop = ch.eventLoop();
                if (loop.inEventLoop()) {
                    this.doHealthCheck(ch, promise);
                }
                else {
                    loop.execute(new Runnable() {
                        @Override
                        public void run() {
                            SimpleChannelPool.this.doHealthCheck(ch, promise);
                        }
                    });
                }
            }
        }
        catch (final Throwable cause) {
            promise.tryFailure(cause);
        }
        return promise;
    }
    
    private void notifyConnect(final ChannelFuture future, final Promise<Channel> promise) {
        Channel channel = null;
        try {
            if (future.isSuccess()) {
                channel = future.channel();
                this.handler.channelAcquired(channel);
                if (!promise.trySuccess(channel)) {
                    this.release(channel);
                }
            }
            else {
                promise.tryFailure(future.cause());
            }
        }
        catch (final Throwable cause) {
            this.closeAndFail(channel, cause, promise);
        }
    }
    
    private void doHealthCheck(final Channel channel, final Promise<Channel> promise) {
        try {
            assert channel.eventLoop().inEventLoop();
            final Future<Boolean> f = this.healthCheck.isHealthy(channel);
            if (f.isDone()) {
                this.notifyHealthCheck(f, channel, promise);
            }
            else {
                f.addListener(new FutureListener<Boolean>() {
                    @Override
                    public void operationComplete(final Future<Boolean> future) {
                        SimpleChannelPool.this.notifyHealthCheck(future, channel, promise);
                    }
                });
            }
        }
        catch (final Throwable cause) {
            this.closeAndFail(channel, cause, promise);
        }
    }
    
    private void notifyHealthCheck(final Future<Boolean> future, final Channel channel, final Promise<Channel> promise) {
        try {
            assert channel.eventLoop().inEventLoop();
            if (future.isSuccess() && future.getNow()) {
                channel.attr(SimpleChannelPool.POOL_KEY).set(this);
                this.handler.channelAcquired(channel);
                promise.setSuccess(channel);
            }
            else {
                this.closeChannel(channel);
                this.acquireHealthyFromPoolOrNew(promise);
            }
        }
        catch (final Throwable cause) {
            this.closeAndFail(channel, cause, promise);
        }
    }
    
    protected ChannelFuture connectChannel(final Bootstrap bs) {
        return bs.connect();
    }
    
    @Override
    public final Future<Void> release(final Channel channel) {
        return this.release(channel, channel.eventLoop().newPromise());
    }
    
    @Override
    public Future<Void> release(final Channel channel, final Promise<Void> promise) {
        try {
            ObjectUtil.checkNotNull(channel, "channel");
            ObjectUtil.checkNotNull(promise, "promise");
            final EventLoop loop = channel.eventLoop();
            if (loop.inEventLoop()) {
                this.doReleaseChannel(channel, promise);
            }
            else {
                loop.execute(new Runnable() {
                    @Override
                    public void run() {
                        SimpleChannelPool.this.doReleaseChannel(channel, promise);
                    }
                });
            }
        }
        catch (final Throwable cause) {
            this.closeAndFail(channel, cause, promise);
        }
        return promise;
    }
    
    private void doReleaseChannel(final Channel channel, final Promise<Void> promise) {
        try {
            assert channel.eventLoop().inEventLoop();
            if (channel.attr(SimpleChannelPool.POOL_KEY).getAndSet(null) != this) {
                this.closeAndFail(channel, new IllegalArgumentException("Channel " + channel + " was not acquired from this ChannelPool"), promise);
            }
            else if (this.releaseHealthCheck) {
                this.doHealthCheckOnRelease(channel, promise);
            }
            else {
                this.releaseAndOffer(channel, promise);
            }
        }
        catch (final Throwable cause) {
            this.closeAndFail(channel, cause, promise);
        }
    }
    
    private void doHealthCheckOnRelease(final Channel channel, final Promise<Void> promise) throws Exception {
        final Future<Boolean> f = this.healthCheck.isHealthy(channel);
        if (f.isDone()) {
            this.releaseAndOfferIfHealthy(channel, promise, f);
        }
        else {
            f.addListener(new FutureListener<Boolean>() {
                @Override
                public void operationComplete(final Future<Boolean> future) throws Exception {
                    SimpleChannelPool.this.releaseAndOfferIfHealthy(channel, promise, f);
                }
            });
        }
    }
    
    private void releaseAndOfferIfHealthy(final Channel channel, final Promise<Void> promise, final Future<Boolean> future) {
        try {
            if (future.getNow()) {
                this.releaseAndOffer(channel, promise);
            }
            else {
                this.handler.channelReleased(channel);
                promise.setSuccess(null);
            }
        }
        catch (final Throwable cause) {
            this.closeAndFail(channel, cause, promise);
        }
    }
    
    private void releaseAndOffer(final Channel channel, final Promise<Void> promise) throws Exception {
        if (this.offerChannel(channel)) {
            this.handler.channelReleased(channel);
            promise.setSuccess(null);
        }
        else {
            this.closeAndFail(channel, new ChannelPoolFullException(), promise);
        }
    }
    
    private void closeChannel(final Channel channel) throws Exception {
        channel.attr(SimpleChannelPool.POOL_KEY).getAndSet(null);
        channel.close();
    }
    
    private void closeAndFail(final Channel channel, final Throwable cause, final Promise<?> promise) {
        if (channel != null) {
            try {
                this.closeChannel(channel);
            }
            catch (final Throwable t) {
                promise.tryFailure(t);
            }
        }
        promise.tryFailure(cause);
    }
    
    protected Channel pollChannel() {
        return this.lastRecentUsed ? this.deque.pollLast() : this.deque.pollFirst();
    }
    
    protected boolean offerChannel(final Channel channel) {
        return this.deque.offer(channel);
    }
    
    @Override
    public void close() {
        while (true) {
            final Channel channel = this.pollChannel();
            if (channel == null) {
                break;
            }
            channel.close().awaitUninterruptibly();
        }
    }
    
    public Future<Void> closeAsync() {
        return GlobalEventExecutor.INSTANCE.submit((Callable<Void>)new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                SimpleChannelPool.this.close();
                return null;
            }
        });
    }
    
    static {
        POOL_KEY = AttributeKey.newInstance("io.netty.channel.pool.SimpleChannelPool");
    }
    
    private static final class ChannelPoolFullException extends IllegalStateException
    {
        private ChannelPoolFullException() {
            super("ChannelPool full");
        }
        
        @Override
        public Throwable fillInStackTrace() {
            return this;
        }
    }
}
