package io.netty.channel.pool;

import java.util.concurrent.TimeoutException;
import java.util.concurrent.Callable;
import io.netty.util.concurrent.GlobalEventExecutor;
import java.nio.channels.ClosedChannelException;
import java.util.concurrent.ScheduledFuture;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Future;
import io.netty.channel.Channel;
import io.netty.util.concurrent.Promise;
import java.util.concurrent.TimeUnit;
import io.netty.util.internal.ObjectUtil;
import java.util.ArrayDeque;
import io.netty.bootstrap.Bootstrap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Queue;
import io.netty.util.concurrent.EventExecutor;

public class FixedChannelPool extends SimpleChannelPool
{
    private final EventExecutor executor;
    private final long acquireTimeoutNanos;
    private final Runnable timeoutTask;
    private final Queue<AcquireTask> pendingAcquireQueue;
    private final int maxConnections;
    private final int maxPendingAcquires;
    private final AtomicInteger acquiredChannelCount;
    private int pendingAcquireCount;
    private boolean closed;
    
    public FixedChannelPool(final Bootstrap bootstrap, final ChannelPoolHandler handler, final int maxConnections) {
        this(bootstrap, handler, maxConnections, Integer.MAX_VALUE);
    }
    
    public FixedChannelPool(final Bootstrap bootstrap, final ChannelPoolHandler handler, final int maxConnections, final int maxPendingAcquires) {
        this(bootstrap, handler, ChannelHealthChecker.ACTIVE, null, -1L, maxConnections, maxPendingAcquires);
    }
    
    public FixedChannelPool(final Bootstrap bootstrap, final ChannelPoolHandler handler, final ChannelHealthChecker healthCheck, final AcquireTimeoutAction action, final long acquireTimeoutMillis, final int maxConnections, final int maxPendingAcquires) {
        this(bootstrap, handler, healthCheck, action, acquireTimeoutMillis, maxConnections, maxPendingAcquires, true);
    }
    
    public FixedChannelPool(final Bootstrap bootstrap, final ChannelPoolHandler handler, final ChannelHealthChecker healthCheck, final AcquireTimeoutAction action, final long acquireTimeoutMillis, final int maxConnections, final int maxPendingAcquires, final boolean releaseHealthCheck) {
        this(bootstrap, handler, healthCheck, action, acquireTimeoutMillis, maxConnections, maxPendingAcquires, releaseHealthCheck, true);
    }
    
    public FixedChannelPool(final Bootstrap bootstrap, final ChannelPoolHandler handler, final ChannelHealthChecker healthCheck, final AcquireTimeoutAction action, final long acquireTimeoutMillis, final int maxConnections, final int maxPendingAcquires, final boolean releaseHealthCheck, final boolean lastRecentUsed) {
        super(bootstrap, handler, healthCheck, releaseHealthCheck, lastRecentUsed);
        this.pendingAcquireQueue = new ArrayDeque<AcquireTask>();
        this.acquiredChannelCount = new AtomicInteger();
        ObjectUtil.checkPositive(maxConnections, "maxConnections");
        ObjectUtil.checkPositive(maxPendingAcquires, "maxPendingAcquires");
        if (action == null && acquireTimeoutMillis == -1L) {
            this.timeoutTask = null;
            this.acquireTimeoutNanos = -1L;
        }
        else {
            if (action == null && acquireTimeoutMillis != -1L) {
                throw new NullPointerException("action");
            }
            if (action != null && acquireTimeoutMillis < 0L) {
                throw new IllegalArgumentException("acquireTimeoutMillis: " + acquireTimeoutMillis + " (expected: >= 0)");
            }
            this.acquireTimeoutNanos = TimeUnit.MILLISECONDS.toNanos(acquireTimeoutMillis);
            switch (action) {
                case FAIL: {
                    this.timeoutTask = new TimeoutTask() {
                        @Override
                        public void onTimeout(final AcquireTask task) {
                            task.promise.setFailure(new AcquireTimeoutException());
                        }
                    };
                    break;
                }
                case NEW: {
                    this.timeoutTask = new TimeoutTask() {
                        @Override
                        public void onTimeout(final AcquireTask task) {
                            task.acquired();
                            SimpleChannelPool.this.acquire(task.promise);
                        }
                    };
                    break;
                }
                default: {
                    throw new Error();
                }
            }
        }
        this.executor = bootstrap.config().group().next();
        this.maxConnections = maxConnections;
        this.maxPendingAcquires = maxPendingAcquires;
    }
    
    public int acquiredChannelCount() {
        return this.acquiredChannelCount.get();
    }
    
    @Override
    public Future<Channel> acquire(final Promise<Channel> promise) {
        try {
            if (this.executor.inEventLoop()) {
                this.acquire0(promise);
            }
            else {
                this.executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        FixedChannelPool.this.acquire0(promise);
                    }
                });
            }
        }
        catch (final Throwable cause) {
            promise.tryFailure(cause);
        }
        return promise;
    }
    
    private void acquire0(final Promise<Channel> promise) {
        try {
            assert this.executor.inEventLoop();
            if (this.closed) {
                promise.setFailure(new IllegalStateException("FixedChannelPool was closed"));
                return;
            }
            if (this.acquiredChannelCount.get() < this.maxConnections) {
                assert this.acquiredChannelCount.get() >= 0;
                final Promise<Channel> p = this.executor.newPromise();
                final AcquireListener l = new AcquireListener(promise);
                l.acquired();
                p.addListener((GenericFutureListener<? extends Future<? super Channel>>)l);
                super.acquire(p);
            }
            else {
                if (this.pendingAcquireCount >= this.maxPendingAcquires) {
                    this.tooManyOutstanding(promise);
                }
                else {
                    final AcquireTask task = new AcquireTask(promise);
                    if (this.pendingAcquireQueue.offer(task)) {
                        ++this.pendingAcquireCount;
                        if (this.timeoutTask != null) {
                            task.timeoutFuture = this.executor.schedule(this.timeoutTask, this.acquireTimeoutNanos, TimeUnit.NANOSECONDS);
                        }
                    }
                    else {
                        this.tooManyOutstanding(promise);
                    }
                }
                assert this.pendingAcquireCount > 0;
            }
        }
        catch (final Throwable cause) {
            promise.tryFailure(cause);
        }
    }
    
    private void tooManyOutstanding(final Promise<?> promise) {
        promise.setFailure(new IllegalStateException("Too many outstanding acquire operations"));
    }
    
    @Override
    public Future<Void> release(final Channel channel, final Promise<Void> promise) {
        ObjectUtil.checkNotNull(promise, "promise");
        final Promise<Void> p = this.executor.newPromise();
        super.release(channel, p.addListener((GenericFutureListener<? extends Future<? super Void>>)new FutureListener<Void>() {
            @Override
            public void operationComplete(final Future<Void> future) {
                try {
                    assert FixedChannelPool.this.executor.inEventLoop();
                    if (FixedChannelPool.this.closed) {
                        channel.close();
                        promise.setFailure(new IllegalStateException("FixedChannelPool was closed"));
                        return;
                    }
                    if (future.isSuccess()) {
                        FixedChannelPool.this.decrementAndRunTaskQueue();
                        promise.setSuccess(null);
                    }
                    else {
                        final Throwable cause = future.cause();
                        if (!(cause instanceof IllegalArgumentException)) {
                            FixedChannelPool.this.decrementAndRunTaskQueue();
                        }
                        promise.setFailure(future.cause());
                    }
                }
                catch (final Throwable cause) {
                    promise.tryFailure(cause);
                }
            }
        }));
        return promise;
    }
    
    private void decrementAndRunTaskQueue() {
        final int currentCount = this.acquiredChannelCount.decrementAndGet();
        assert currentCount >= 0;
        this.runTaskQueue();
    }
    
    private void runTaskQueue() {
        while (this.acquiredChannelCount.get() < this.maxConnections) {
            final AcquireTask task = this.pendingAcquireQueue.poll();
            if (task == null) {
                break;
            }
            final ScheduledFuture<?> timeoutFuture = task.timeoutFuture;
            if (timeoutFuture != null) {
                timeoutFuture.cancel(false);
            }
            --this.pendingAcquireCount;
            task.acquired();
            super.acquire(task.promise);
        }
        assert this.pendingAcquireCount >= 0;
        assert this.acquiredChannelCount.get() >= 0;
    }
    
    @Override
    public void close() {
        try {
            this.closeAsync().await();
        }
        catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public Future<Void> closeAsync() {
        if (this.executor.inEventLoop()) {
            return this.close0();
        }
        final Promise<Void> closeComplete = this.executor.newPromise();
        this.executor.execute(new Runnable() {
            @Override
            public void run() {
                FixedChannelPool.this.close0().addListener(new FutureListener<Void>() {
                    @Override
                    public void operationComplete(final Future<Void> f) throws Exception {
                        if (f.isSuccess()) {
                            closeComplete.setSuccess(null);
                        }
                        else {
                            closeComplete.setFailure(f.cause());
                        }
                    }
                });
            }
        });
        return closeComplete;
    }
    
    private Future<Void> close0() {
        assert this.executor.inEventLoop();
        if (!this.closed) {
            this.closed = true;
            while (true) {
                final AcquireTask task = this.pendingAcquireQueue.poll();
                if (task == null) {
                    break;
                }
                final ScheduledFuture<?> f = task.timeoutFuture;
                if (f != null) {
                    f.cancel(false);
                }
                task.promise.setFailure(new ClosedChannelException());
            }
            this.acquiredChannelCount.set(0);
            this.pendingAcquireCount = 0;
            return GlobalEventExecutor.INSTANCE.submit((Callable<Void>)new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    SimpleChannelPool.this.close();
                    return null;
                }
            });
        }
        return GlobalEventExecutor.INSTANCE.newSucceededFuture((Void)null);
    }
    
    public enum AcquireTimeoutAction
    {
        NEW, 
        FAIL;
    }
    
    private final class AcquireTask extends AcquireListener
    {
        final Promise<Channel> promise;
        final long expireNanoTime;
        ScheduledFuture<?> timeoutFuture;
        
        AcquireTask(final Promise<Channel> promise) {
            super(promise);
            this.expireNanoTime = System.nanoTime() + FixedChannelPool.this.acquireTimeoutNanos;
            this.promise = FixedChannelPool.this.executor.newPromise().addListener((GenericFutureListener<? extends Future<? super Channel>>)this);
        }
    }
    
    private abstract class TimeoutTask implements Runnable
    {
        @Override
        public final void run() {
            assert FixedChannelPool.this.executor.inEventLoop();
            final long nanoTime = System.nanoTime();
            while (true) {
                final AcquireTask task = FixedChannelPool.this.pendingAcquireQueue.peek();
                if (task == null || nanoTime - task.expireNanoTime < 0L) {
                    break;
                }
                FixedChannelPool.this.pendingAcquireQueue.remove();
                --FixedChannelPool.this.pendingAcquireCount;
                this.onTimeout(task);
            }
        }
        
        public abstract void onTimeout(final AcquireTask p0);
    }
    
    private class AcquireListener implements FutureListener<Channel>
    {
        private final Promise<Channel> originalPromise;
        protected boolean acquired;
        
        AcquireListener(final Promise<Channel> originalPromise) {
            this.originalPromise = originalPromise;
        }
        
        @Override
        public void operationComplete(final Future<Channel> future) throws Exception {
            try {
                assert FixedChannelPool.this.executor.inEventLoop();
                if (FixedChannelPool.this.closed) {
                    if (future.isSuccess()) {
                        future.getNow().close();
                    }
                    this.originalPromise.setFailure(new IllegalStateException("FixedChannelPool was closed"));
                    return;
                }
                if (future.isSuccess()) {
                    this.originalPromise.setSuccess(future.getNow());
                }
                else {
                    if (this.acquired) {
                        FixedChannelPool.this.decrementAndRunTaskQueue();
                    }
                    else {
                        FixedChannelPool.this.runTaskQueue();
                    }
                    this.originalPromise.setFailure(future.cause());
                }
            }
            catch (final Throwable cause) {
                this.originalPromise.tryFailure(cause);
            }
        }
        
        public void acquired() {
            if (this.acquired) {
                return;
            }
            FixedChannelPool.this.acquiredChannelCount.incrementAndGet();
            this.acquired = true;
        }
    }
    
    private static final class AcquireTimeoutException extends TimeoutException
    {
        private AcquireTimeoutException() {
            super("Acquire operation took longer then configured maximum time");
        }
        
        @Override
        public Throwable fillInStackTrace() {
            return this;
        }
    }
}
