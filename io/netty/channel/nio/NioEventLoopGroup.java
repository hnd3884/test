package io.netty.channel.nio;

import io.netty.channel.EventLoop;
import java.util.Iterator;
import io.netty.util.concurrent.EventExecutor;
import io.netty.channel.EventLoopTaskQueueFactory;
import io.netty.util.concurrent.RejectedExecutionHandler;
import io.netty.util.concurrent.EventExecutorChooserFactory;
import io.netty.util.concurrent.RejectedExecutionHandlers;
import io.netty.channel.SelectStrategyFactory;
import io.netty.channel.DefaultSelectStrategyFactory;
import java.nio.channels.spi.SelectorProvider;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.Executor;
import io.netty.channel.MultithreadEventLoopGroup;

public class NioEventLoopGroup extends MultithreadEventLoopGroup
{
    public NioEventLoopGroup() {
        this(0);
    }
    
    public NioEventLoopGroup(final int nThreads) {
        this(nThreads, (Executor)null);
    }
    
    public NioEventLoopGroup(final ThreadFactory threadFactory) {
        this(0, threadFactory, SelectorProvider.provider());
    }
    
    public NioEventLoopGroup(final int nThreads, final ThreadFactory threadFactory) {
        this(nThreads, threadFactory, SelectorProvider.provider());
    }
    
    public NioEventLoopGroup(final int nThreads, final Executor executor) {
        this(nThreads, executor, SelectorProvider.provider());
    }
    
    public NioEventLoopGroup(final int nThreads, final ThreadFactory threadFactory, final SelectorProvider selectorProvider) {
        this(nThreads, threadFactory, selectorProvider, DefaultSelectStrategyFactory.INSTANCE);
    }
    
    public NioEventLoopGroup(final int nThreads, final ThreadFactory threadFactory, final SelectorProvider selectorProvider, final SelectStrategyFactory selectStrategyFactory) {
        super(nThreads, threadFactory, new Object[] { selectorProvider, selectStrategyFactory, RejectedExecutionHandlers.reject() });
    }
    
    public NioEventLoopGroup(final int nThreads, final Executor executor, final SelectorProvider selectorProvider) {
        this(nThreads, executor, selectorProvider, DefaultSelectStrategyFactory.INSTANCE);
    }
    
    public NioEventLoopGroup(final int nThreads, final Executor executor, final SelectorProvider selectorProvider, final SelectStrategyFactory selectStrategyFactory) {
        super(nThreads, executor, new Object[] { selectorProvider, selectStrategyFactory, RejectedExecutionHandlers.reject() });
    }
    
    public NioEventLoopGroup(final int nThreads, final Executor executor, final EventExecutorChooserFactory chooserFactory, final SelectorProvider selectorProvider, final SelectStrategyFactory selectStrategyFactory) {
        super(nThreads, executor, chooserFactory, new Object[] { selectorProvider, selectStrategyFactory, RejectedExecutionHandlers.reject() });
    }
    
    public NioEventLoopGroup(final int nThreads, final Executor executor, final EventExecutorChooserFactory chooserFactory, final SelectorProvider selectorProvider, final SelectStrategyFactory selectStrategyFactory, final RejectedExecutionHandler rejectedExecutionHandler) {
        super(nThreads, executor, chooserFactory, new Object[] { selectorProvider, selectStrategyFactory, rejectedExecutionHandler });
    }
    
    public NioEventLoopGroup(final int nThreads, final Executor executor, final EventExecutorChooserFactory chooserFactory, final SelectorProvider selectorProvider, final SelectStrategyFactory selectStrategyFactory, final RejectedExecutionHandler rejectedExecutionHandler, final EventLoopTaskQueueFactory taskQueueFactory) {
        super(nThreads, executor, chooserFactory, new Object[] { selectorProvider, selectStrategyFactory, rejectedExecutionHandler, taskQueueFactory });
    }
    
    public NioEventLoopGroup(final int nThreads, final Executor executor, final EventExecutorChooserFactory chooserFactory, final SelectorProvider selectorProvider, final SelectStrategyFactory selectStrategyFactory, final RejectedExecutionHandler rejectedExecutionHandler, final EventLoopTaskQueueFactory taskQueueFactory, final EventLoopTaskQueueFactory tailTaskQueueFactory) {
        super(nThreads, executor, chooserFactory, new Object[] { selectorProvider, selectStrategyFactory, rejectedExecutionHandler, taskQueueFactory, tailTaskQueueFactory });
    }
    
    public void setIoRatio(final int ioRatio) {
        for (final EventExecutor e : this) {
            ((NioEventLoop)e).setIoRatio(ioRatio);
        }
    }
    
    public void rebuildSelectors() {
        for (final EventExecutor e : this) {
            ((NioEventLoop)e).rebuildSelector();
        }
    }
    
    @Override
    protected EventLoop newChild(final Executor executor, final Object... args) throws Exception {
        final SelectorProvider selectorProvider = (SelectorProvider)args[0];
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
        return new NioEventLoop(this, executor, selectorProvider, selectStrategyFactory.newSelectStrategy(), rejectedExecutionHandler, taskQueueFactory, tailTaskQueueFactory);
    }
}
