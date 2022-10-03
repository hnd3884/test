package sun.nio.ch;

import sun.security.action.GetIntegerAction;
import java.security.AccessControlContext;
import java.security.Permission;
import java.io.IOException;
import java.io.FileDescriptor;
import java.nio.channels.Channel;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.RejectedExecutionException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.nio.channels.spi.AsynchronousChannelProvider;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Queue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.Executor;
import java.nio.channels.AsynchronousChannelGroup;

abstract class AsynchronousChannelGroupImpl extends AsynchronousChannelGroup implements Executor
{
    private static final int internalThreadCount;
    private final ThreadPool pool;
    private final AtomicInteger threadCount;
    private ScheduledThreadPoolExecutor timeoutExecutor;
    private final Queue<Runnable> taskQueue;
    private final AtomicBoolean shutdown;
    private final Object shutdownNowLock;
    private volatile boolean terminateInitiated;
    
    AsynchronousChannelGroupImpl(final AsynchronousChannelProvider asynchronousChannelProvider, final ThreadPool pool) {
        super(asynchronousChannelProvider);
        this.threadCount = new AtomicInteger();
        this.shutdown = new AtomicBoolean();
        this.shutdownNowLock = new Object();
        this.pool = pool;
        if (pool.isFixedThreadPool()) {
            this.taskQueue = new ConcurrentLinkedQueue<Runnable>();
        }
        else {
            this.taskQueue = null;
        }
        (this.timeoutExecutor = (ScheduledThreadPoolExecutor)Executors.newScheduledThreadPool(1, ThreadPool.defaultThreadFactory())).setRemoveOnCancelPolicy(true);
    }
    
    final ExecutorService executor() {
        return this.pool.executor();
    }
    
    final boolean isFixedThreadPool() {
        return this.pool.isFixedThreadPool();
    }
    
    final int fixedThreadCount() {
        if (this.isFixedThreadPool()) {
            return this.pool.poolSize();
        }
        return this.pool.poolSize() + AsynchronousChannelGroupImpl.internalThreadCount;
    }
    
    private Runnable bindToGroup(final Runnable runnable) {
        return new Runnable() {
            @Override
            public void run() {
                Invoker.bindToGroup(AsynchronousChannelGroupImpl.this);
                runnable.run();
            }
        };
    }
    
    private void startInternalThread(final Runnable runnable) {
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                ThreadPool.defaultThreadFactory().newThread(runnable).start();
                return null;
            }
        });
    }
    
    protected final void startThreads(Runnable bindToGroup) {
        if (!this.isFixedThreadPool()) {
            for (int i = 0; i < AsynchronousChannelGroupImpl.internalThreadCount; ++i) {
                this.startInternalThread(bindToGroup);
                this.threadCount.incrementAndGet();
            }
        }
        if (this.pool.poolSize() > 0) {
            bindToGroup = this.bindToGroup(bindToGroup);
            try {
                for (int j = 0; j < this.pool.poolSize(); ++j) {
                    this.pool.executor().execute(bindToGroup);
                    this.threadCount.incrementAndGet();
                }
            }
            catch (final RejectedExecutionException ex) {}
        }
    }
    
    final int threadCount() {
        return this.threadCount.get();
    }
    
    final int threadExit(final Runnable runnable, final boolean b) {
        if (b) {
            try {
                if (Invoker.isBoundToAnyGroup()) {
                    this.pool.executor().execute(this.bindToGroup(runnable));
                }
                else {
                    this.startInternalThread(runnable);
                }
                return this.threadCount.get();
            }
            catch (final RejectedExecutionException ex) {}
        }
        return this.threadCount.decrementAndGet();
    }
    
    abstract void executeOnHandlerTask(final Runnable p0);
    
    final void executeOnPooledThread(final Runnable runnable) {
        if (this.isFixedThreadPool()) {
            this.executeOnHandlerTask(runnable);
        }
        else {
            this.pool.executor().execute(this.bindToGroup(runnable));
        }
    }
    
    final void offerTask(final Runnable runnable) {
        this.taskQueue.offer(runnable);
    }
    
    final Runnable pollTask() {
        return (this.taskQueue == null) ? null : this.taskQueue.poll();
    }
    
    final Future<?> schedule(final Runnable runnable, final long n, final TimeUnit timeUnit) {
        try {
            return this.timeoutExecutor.schedule(runnable, n, timeUnit);
        }
        catch (final RejectedExecutionException ex) {
            if (this.terminateInitiated) {
                return null;
            }
            throw new AssertionError((Object)ex);
        }
    }
    
    @Override
    public final boolean isShutdown() {
        return this.shutdown.get();
    }
    
    @Override
    public final boolean isTerminated() {
        return this.pool.executor().isTerminated();
    }
    
    abstract boolean isEmpty();
    
    abstract Object attachForeignChannel(final Channel p0, final FileDescriptor p1) throws IOException;
    
    abstract void detachForeignChannel(final Object p0);
    
    abstract void closeAllChannels() throws IOException;
    
    abstract void shutdownHandlerTasks();
    
    private void shutdownExecutors() {
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                AsynchronousChannelGroupImpl.this.pool.executor().shutdown();
                AsynchronousChannelGroupImpl.this.timeoutExecutor.shutdown();
                return null;
            }
        }, null, new RuntimePermission("modifyThread"));
    }
    
    @Override
    public final void shutdown() {
        if (this.shutdown.getAndSet(true)) {
            return;
        }
        if (!this.isEmpty()) {
            return;
        }
        synchronized (this.shutdownNowLock) {
            if (!this.terminateInitiated) {
                this.terminateInitiated = true;
                this.shutdownHandlerTasks();
                this.shutdownExecutors();
            }
        }
    }
    
    @Override
    public final void shutdownNow() throws IOException {
        this.shutdown.set(true);
        synchronized (this.shutdownNowLock) {
            if (!this.terminateInitiated) {
                this.terminateInitiated = true;
                this.closeAllChannels();
                this.shutdownHandlerTasks();
                this.shutdownExecutors();
            }
        }
    }
    
    final void detachFromThreadPool() {
        if (this.shutdown.getAndSet(true)) {
            throw new AssertionError((Object)"Already shutdown");
        }
        if (!this.isEmpty()) {
            throw new AssertionError((Object)"Group not empty");
        }
        this.shutdownHandlerTasks();
    }
    
    @Override
    public final boolean awaitTermination(final long n, final TimeUnit timeUnit) throws InterruptedException {
        return this.pool.executor().awaitTermination(n, timeUnit);
    }
    
    @Override
    public final void execute(Runnable runnable) {
        if (System.getSecurityManager() != null) {
            runnable = new Runnable() {
                final /* synthetic */ AccessControlContext val$acc = AccessController.getContext();
                
                @Override
                public void run() {
                    AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
                        @Override
                        public Void run() {
                            runnable.run();
                            return null;
                        }
                    }, this.val$acc);
                }
            };
        }
        this.executeOnPooledThread(runnable);
    }
    
    static {
        internalThreadCount = AccessController.doPrivileged((PrivilegedAction<Integer>)new GetIntegerAction("sun.nio.ch.internalThreadPoolSize", 1));
    }
}
