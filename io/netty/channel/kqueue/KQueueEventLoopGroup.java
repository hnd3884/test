package io.netty.channel.kqueue;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.EventLoop;
import java.util.Iterator;
import io.netty.util.concurrent.EventExecutor;
import io.netty.channel.EventLoopTaskQueueFactory;
import io.netty.util.concurrent.RejectedExecutionHandler;
import io.netty.util.concurrent.EventExecutorChooserFactory;
import io.netty.util.concurrent.RejectedExecutionHandlers;
import io.netty.channel.DefaultSelectStrategyFactory;
import java.util.concurrent.Executor;
import io.netty.channel.SelectStrategyFactory;
import java.util.concurrent.ThreadFactory;
import io.netty.channel.MultithreadEventLoopGroup;

public final class KQueueEventLoopGroup extends MultithreadEventLoopGroup
{
    public KQueueEventLoopGroup() {
        this(0);
    }
    
    public KQueueEventLoopGroup(final int nThreads) {
        this(nThreads, (ThreadFactory)null);
    }
    
    public KQueueEventLoopGroup(final ThreadFactory threadFactory) {
        this(0, threadFactory, 0);
    }
    
    public KQueueEventLoopGroup(final int nThreads, final SelectStrategyFactory selectStrategyFactory) {
        this(nThreads, (ThreadFactory)null, selectStrategyFactory);
    }
    
    public KQueueEventLoopGroup(final int nThreads, final ThreadFactory threadFactory) {
        this(nThreads, threadFactory, 0);
    }
    
    public KQueueEventLoopGroup(final int nThreads, final Executor executor) {
        this(nThreads, executor, DefaultSelectStrategyFactory.INSTANCE);
    }
    
    public KQueueEventLoopGroup(final int nThreads, final ThreadFactory threadFactory, final SelectStrategyFactory selectStrategyFactory) {
        this(nThreads, threadFactory, 0, selectStrategyFactory);
    }
    
    @Deprecated
    public KQueueEventLoopGroup(final int nThreads, final ThreadFactory threadFactory, final int maxEventsAtOnce) {
        this(nThreads, threadFactory, maxEventsAtOnce, DefaultSelectStrategyFactory.INSTANCE);
    }
    
    @Deprecated
    public KQueueEventLoopGroup(final int nThreads, final ThreadFactory threadFactory, final int maxEventsAtOnce, final SelectStrategyFactory selectStrategyFactory) {
        super(nThreads, threadFactory, new Object[] { maxEventsAtOnce, selectStrategyFactory, RejectedExecutionHandlers.reject() });
        KQueue.ensureAvailability();
    }
    
    public KQueueEventLoopGroup(final int nThreads, final Executor executor, final SelectStrategyFactory selectStrategyFactory) {
        super(nThreads, executor, new Object[] { 0, selectStrategyFactory, RejectedExecutionHandlers.reject() });
        KQueue.ensureAvailability();
    }
    
    public KQueueEventLoopGroup(final int nThreads, final Executor executor, final EventExecutorChooserFactory chooserFactory, final SelectStrategyFactory selectStrategyFactory) {
        super(nThreads, executor, chooserFactory, new Object[] { 0, selectStrategyFactory, RejectedExecutionHandlers.reject() });
        KQueue.ensureAvailability();
    }
    
    public KQueueEventLoopGroup(final int nThreads, final Executor executor, final EventExecutorChooserFactory chooserFactory, final SelectStrategyFactory selectStrategyFactory, final RejectedExecutionHandler rejectedExecutionHandler) {
        super(nThreads, executor, chooserFactory, new Object[] { 0, selectStrategyFactory, rejectedExecutionHandler });
        KQueue.ensureAvailability();
    }
    
    public KQueueEventLoopGroup(final int nThreads, final Executor executor, final EventExecutorChooserFactory chooserFactory, final SelectStrategyFactory selectStrategyFactory, final RejectedExecutionHandler rejectedExecutionHandler, final EventLoopTaskQueueFactory queueFactory) {
        super(nThreads, executor, chooserFactory, new Object[] { 0, selectStrategyFactory, rejectedExecutionHandler, queueFactory });
        KQueue.ensureAvailability();
    }
    
    public KQueueEventLoopGroup(final int nThreads, final Executor executor, final EventExecutorChooserFactory chooserFactory, final SelectStrategyFactory selectStrategyFactory, final RejectedExecutionHandler rejectedExecutionHandler, final EventLoopTaskQueueFactory taskQueueFactory, final EventLoopTaskQueueFactory tailTaskQueueFactory) {
        super(nThreads, executor, chooserFactory, new Object[] { 0, selectStrategyFactory, rejectedExecutionHandler, taskQueueFactory, tailTaskQueueFactory });
        KQueue.ensureAvailability();
    }
    
    public void setIoRatio(final int ioRatio) {
        for (final EventExecutor e : this) {
            ((KQueueEventLoop)e).setIoRatio(ioRatio);
        }
    }
    
    @Override
    protected EventLoop newChild(final Executor executor, final Object... args) throws Exception {
        final Integer maxEvents = (Integer)args[0];
        final SelectStrategyFactory selectStrategyFactory = (SelectStrategyFactory)args[1];
        final RejectedExecutionHandler rejectedExecutionHandler = (RejectedExecutionHandler)args[2];
        EventLoopTaskQueueFactory taskQueueFactory = null;
        EventLoopTaskQueueFactory tailTaskQueueFactory = null;
        final int argsLength = args.length;
        if (argsLength > 3) {
            taskQueueFactory = (EventLoopTaskQueueFactory)args[3];
        }
        if (argsLength > 4) {
            tailTaskQueueFactory = (EventLoopTaskQueueFactory)args[4];
        }
        return new KQueueEventLoop(this, executor, maxEvents, selectStrategyFactory.newSelectStrategy(), rejectedExecutionHandler, taskQueueFactory, tailTaskQueueFactory);
    }
}
