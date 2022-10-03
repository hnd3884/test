package org.glassfish.jersey.spi;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;
import java.security.PrivilegedAction;
import java.security.AccessController;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutorService;
import org.glassfish.jersey.process.JerseyProcessingUncaughtExceptionHandler;
import org.glassfish.jersey.internal.guava.ThreadFactoryBuilder;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import org.glassfish.jersey.internal.LocalizationMessages;
import org.glassfish.jersey.internal.util.collection.Values;
import org.glassfish.jersey.internal.util.collection.LazyValue;
import java.util.concurrent.atomic.AtomicBoolean;
import org.glassfish.jersey.internal.util.ExtendedLogger;
import java.util.concurrent.ThreadPoolExecutor;

public abstract class AbstractThreadPoolProvider<E extends ThreadPoolExecutor> implements AutoCloseable
{
    private static final ExtendedLogger LOGGER;
    public static final int DEFAULT_TERMINATION_TIMEOUT = 5000;
    private final String name;
    private final AtomicBoolean closed;
    private final LazyValue<E> lazyExecutorServiceProvider;
    
    protected AbstractThreadPoolProvider(final String name) {
        this.closed = new AtomicBoolean(false);
        this.lazyExecutorServiceProvider = Values.lazy(() -> this.createExecutor(this.getCorePoolSize(), this.createThreadFactory(), this.getRejectedExecutionHandler()));
        this.name = name;
    }
    
    protected final E getExecutor() {
        if (this.isClosed()) {
            throw new IllegalStateException(LocalizationMessages.THREAD_POOL_EXECUTOR_PROVIDER_CLOSED());
        }
        return this.lazyExecutorServiceProvider.get();
    }
    
    protected abstract E createExecutor(final int p0, final ThreadFactory p1, final RejectedExecutionHandler p2);
    
    protected int getTerminationTimeout() {
        return 5000;
    }
    
    protected int getCorePoolSize() {
        return Runtime.getRuntime().availableProcessors();
    }
    
    protected RejectedExecutionHandler getRejectedExecutionHandler() {
        return (r, executor) -> {};
    }
    
    protected ThreadFactory getBackingThreadFactory() {
        return null;
    }
    
    private ThreadFactory createThreadFactory() {
        final ThreadFactoryBuilder factoryBuilder = new ThreadFactoryBuilder().setNameFormat(this.name + "-%d").setUncaughtExceptionHandler(new JerseyProcessingUncaughtExceptionHandler());
        final ThreadFactory backingThreadFactory = this.getBackingThreadFactory();
        if (backingThreadFactory != null) {
            factoryBuilder.setThreadFactory(backingThreadFactory);
        }
        return factoryBuilder.build();
    }
    
    public final boolean isClosed() {
        return this.closed.get();
    }
    
    protected void onClose() {
    }
    
    @Override
    public final void close() {
        if (!this.closed.compareAndSet(false, true)) {
            return;
        }
        try {
            this.onClose();
        }
        finally {
            if (this.lazyExecutorServiceProvider.isInitialized()) {
                AccessController.doPrivileged(shutdownExecutor(this.name, this.lazyExecutorServiceProvider.get(), this.getTerminationTimeout(), TimeUnit.MILLISECONDS));
            }
        }
    }
    
    private static PrivilegedAction<?> shutdownExecutor(final String executorName, final ExecutorService executorService, final int terminationTimeout, final TimeUnit terminationTimeUnit) {
        return () -> {
            if (!executorService.isShutdown()) {
                executorService.shutdown();
            }
            if (executorService.isTerminated()) {
                return null;
            }
            else {
                boolean terminated = false;
                boolean interrupted = false;
                try {
                    terminated = executorService.awaitTermination(terminationTimeout, terminationTimeUnit);
                }
                catch (final InterruptedException e) {
                    if (AbstractThreadPoolProvider.LOGGER.isDebugLoggable()) {
                        AbstractThreadPoolProvider.LOGGER.log(AbstractThreadPoolProvider.LOGGER.getDebugLevel(), "Interrupted while waiting for thread pool executor " + executorName + " to shutdown.", e);
                    }
                    interrupted = true;
                }
                try {
                    if (!terminated) {
                        final List<Runnable> cancelledTasks = executorService.shutdownNow();
                        cancelledTasks.iterator();
                        final Iterator iterator;
                        while (iterator.hasNext()) {
                            final Runnable cancelledTask = iterator.next();
                            if (cancelledTask instanceof Future) {
                                ((Future)cancelledTask).cancel(true);
                            }
                        }
                        if (AbstractThreadPoolProvider.LOGGER.isDebugLoggable()) {
                            AbstractThreadPoolProvider.LOGGER.debugLog("Thread pool executor {0} forced-shut down. List of cancelled tasks: {1}", executorName, cancelledTasks);
                        }
                    }
                }
                finally {
                    if (interrupted) {
                        Thread.currentThread().interrupt();
                    }
                }
                return null;
            }
        };
    }
    
    static {
        LOGGER = new ExtendedLogger(Logger.getLogger(AbstractThreadPoolProvider.class.getName()), Level.FINEST);
    }
}
