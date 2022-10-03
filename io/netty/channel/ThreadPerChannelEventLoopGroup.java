package io.netty.channel;

import io.netty.util.concurrent.GenericFutureListener;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import io.netty.util.internal.ReadOnlyIterator;
import java.util.Iterator;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.GlobalEventExecutor;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Map;
import java.util.Collections;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.concurrent.ThreadPerTaskExecutor;
import java.util.concurrent.ThreadFactory;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.Promise;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Executor;
import io.netty.util.concurrent.AbstractEventExecutorGroup;

@Deprecated
public class ThreadPerChannelEventLoopGroup extends AbstractEventExecutorGroup implements EventLoopGroup
{
    private final Object[] childArgs;
    private final int maxChannels;
    final Executor executor;
    final Set<EventLoop> activeChildren;
    final Queue<EventLoop> idleChildren;
    private final ChannelException tooManyChannels;
    private volatile boolean shuttingDown;
    private final Promise<?> terminationFuture;
    private final FutureListener<Object> childTerminationListener;
    
    protected ThreadPerChannelEventLoopGroup() {
        this(0);
    }
    
    protected ThreadPerChannelEventLoopGroup(final int maxChannels) {
        this(maxChannels, (ThreadFactory)null, new Object[0]);
    }
    
    protected ThreadPerChannelEventLoopGroup(final int maxChannels, final ThreadFactory threadFactory, final Object... args) {
        this(maxChannels, (threadFactory == null) ? null : new ThreadPerTaskExecutor(threadFactory), args);
    }
    
    protected ThreadPerChannelEventLoopGroup(final int maxChannels, Executor executor, final Object... args) {
        this.activeChildren = Collections.newSetFromMap((Map<EventLoop, Boolean>)PlatformDependent.newConcurrentHashMap());
        this.idleChildren = new ConcurrentLinkedQueue<EventLoop>();
        this.terminationFuture = new DefaultPromise<Object>(GlobalEventExecutor.INSTANCE);
        this.childTerminationListener = new FutureListener<Object>() {
            @Override
            public void operationComplete(final Future<Object> future) throws Exception {
                if (ThreadPerChannelEventLoopGroup.this.isTerminated()) {
                    ThreadPerChannelEventLoopGroup.this.terminationFuture.trySuccess(null);
                }
            }
        };
        ObjectUtil.checkPositiveOrZero(maxChannels, "maxChannels");
        if (executor == null) {
            executor = new ThreadPerTaskExecutor(new DefaultThreadFactory(this.getClass()));
        }
        if (args == null) {
            this.childArgs = EmptyArrays.EMPTY_OBJECTS;
        }
        else {
            this.childArgs = args.clone();
        }
        this.maxChannels = maxChannels;
        this.executor = executor;
        this.tooManyChannels = ChannelException.newStatic("too many channels (max: " + maxChannels + ')', ThreadPerChannelEventLoopGroup.class, "nextChild()");
    }
    
    protected EventLoop newChild(final Object... args) throws Exception {
        return new ThreadPerChannelEventLoop(this);
    }
    
    @Override
    public Iterator<EventExecutor> iterator() {
        return new ReadOnlyIterator<EventExecutor>(this.activeChildren.iterator());
    }
    
    @Override
    public EventLoop next() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Future<?> shutdownGracefully(final long quietPeriod, final long timeout, final TimeUnit unit) {
        this.shuttingDown = true;
        for (final EventLoop l : this.activeChildren) {
            l.shutdownGracefully(quietPeriod, timeout, unit);
        }
        for (final EventLoop l : this.idleChildren) {
            l.shutdownGracefully(quietPeriod, timeout, unit);
        }
        if (this.isTerminated()) {
            this.terminationFuture.trySuccess(null);
        }
        return this.terminationFuture();
    }
    
    @Override
    public Future<?> terminationFuture() {
        return this.terminationFuture;
    }
    
    @Deprecated
    @Override
    public void shutdown() {
        this.shuttingDown = true;
        for (final EventLoop l : this.activeChildren) {
            l.shutdown();
        }
        for (final EventLoop l : this.idleChildren) {
            l.shutdown();
        }
        if (this.isTerminated()) {
            this.terminationFuture.trySuccess(null);
        }
    }
    
    @Override
    public boolean isShuttingDown() {
        for (final EventLoop l : this.activeChildren) {
            if (!l.isShuttingDown()) {
                return false;
            }
        }
        for (final EventLoop l : this.idleChildren) {
            if (!l.isShuttingDown()) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public boolean isShutdown() {
        for (final EventLoop l : this.activeChildren) {
            if (!l.isShutdown()) {
                return false;
            }
        }
        for (final EventLoop l : this.idleChildren) {
            if (!l.isShutdown()) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public boolean isTerminated() {
        for (final EventLoop l : this.activeChildren) {
            if (!l.isTerminated()) {
                return false;
            }
        }
        for (final EventLoop l : this.idleChildren) {
            if (!l.isTerminated()) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public boolean awaitTermination(final long timeout, final TimeUnit unit) throws InterruptedException {
        final long deadline = System.nanoTime() + unit.toNanos(timeout);
        for (final EventLoop l : this.activeChildren) {
            long timeLeft;
            do {
                timeLeft = deadline - System.nanoTime();
                if (timeLeft <= 0L) {
                    return this.isTerminated();
                }
            } while (!l.awaitTermination(timeLeft, TimeUnit.NANOSECONDS));
        }
        for (final EventLoop l : this.idleChildren) {
            long timeLeft;
            do {
                timeLeft = deadline - System.nanoTime();
                if (timeLeft <= 0L) {
                    return this.isTerminated();
                }
            } while (!l.awaitTermination(timeLeft, TimeUnit.NANOSECONDS));
        }
        return this.isTerminated();
    }
    
    @Override
    public ChannelFuture register(final Channel channel) {
        ObjectUtil.checkNotNull(channel, "channel");
        try {
            final EventLoop l = this.nextChild();
            return l.register(new DefaultChannelPromise(channel, l));
        }
        catch (final Throwable t) {
            return new FailedChannelFuture(channel, GlobalEventExecutor.INSTANCE, t);
        }
    }
    
    @Override
    public ChannelFuture register(final ChannelPromise promise) {
        try {
            return this.nextChild().register(promise);
        }
        catch (final Throwable t) {
            promise.setFailure(t);
            return promise;
        }
    }
    
    @Deprecated
    @Override
    public ChannelFuture register(final Channel channel, final ChannelPromise promise) {
        ObjectUtil.checkNotNull(channel, "channel");
        try {
            return this.nextChild().register(channel, promise);
        }
        catch (final Throwable t) {
            promise.setFailure(t);
            return promise;
        }
    }
    
    private EventLoop nextChild() throws Exception {
        if (this.shuttingDown) {
            throw new RejectedExecutionException("shutting down");
        }
        EventLoop loop = this.idleChildren.poll();
        if (loop == null) {
            if (this.maxChannels > 0 && this.activeChildren.size() >= this.maxChannels) {
                throw this.tooManyChannels;
            }
            loop = this.newChild(this.childArgs);
            loop.terminationFuture().addListener(this.childTerminationListener);
        }
        this.activeChildren.add(loop);
        return loop;
    }
}
