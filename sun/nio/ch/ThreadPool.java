package sun.nio.ch;

import java.security.PrivilegedAction;
import sun.security.action.GetPropertyAction;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.Executors;
import java.security.AccessController;
import sun.misc.InnocuousThread;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ExecutorService;

public class ThreadPool
{
    private static final String DEFAULT_THREAD_POOL_THREAD_FACTORY = "java.nio.channels.DefaultThreadPool.threadFactory";
    private static final String DEFAULT_THREAD_POOL_INITIAL_SIZE = "java.nio.channels.DefaultThreadPool.initialSize";
    private final ExecutorService executor;
    private final boolean isFixed;
    private final int poolSize;
    
    private ThreadPool(final ExecutorService executor, final boolean isFixed, final int poolSize) {
        this.executor = executor;
        this.isFixed = isFixed;
        this.poolSize = poolSize;
    }
    
    ExecutorService executor() {
        return this.executor;
    }
    
    boolean isFixedThreadPool() {
        return this.isFixed;
    }
    
    int poolSize() {
        return this.poolSize;
    }
    
    static ThreadFactory defaultThreadFactory() {
        if (System.getSecurityManager() == null) {
            return runnable -> {
                final Thread thread = new Thread(runnable);
                thread.setDaemon(true);
                return thread;
            };
        }
        return p0 -> AccessController.doPrivileged(() -> {
            final InnocuousThread innocuousThread = new InnocuousThread(runnable4);
            innocuousThread.setDaemon(true);
            return innocuousThread;
        });
    }
    
    static ThreadPool getDefault() {
        return DefaultThreadPoolHolder.defaultThreadPool;
    }
    
    static ThreadPool createDefault() {
        int n = getDefaultThreadPoolInitialSize();
        if (n < 0) {
            n = Runtime.getRuntime().availableProcessors();
        }
        ThreadFactory threadFactory = getDefaultThreadPoolThreadFactory();
        if (threadFactory == null) {
            threadFactory = defaultThreadFactory();
        }
        return new ThreadPool(Executors.newCachedThreadPool(threadFactory), false, n);
    }
    
    static ThreadPool create(final int n, final ThreadFactory threadFactory) {
        if (n <= 0) {
            throw new IllegalArgumentException("'nThreads' must be > 0");
        }
        return new ThreadPool(Executors.newFixedThreadPool(n, threadFactory), true, n);
    }
    
    public static ThreadPool wrap(final ExecutorService executorService, int availableProcessors) {
        if (executorService == null) {
            throw new NullPointerException("'executor' is null");
        }
        if (executorService instanceof ThreadPoolExecutor) {
            if (((ThreadPoolExecutor)executorService).getMaximumPoolSize() == Integer.MAX_VALUE) {
                if (availableProcessors < 0) {
                    availableProcessors = Runtime.getRuntime().availableProcessors();
                }
                else {
                    availableProcessors = 0;
                }
            }
        }
        else if (availableProcessors < 0) {
            availableProcessors = 0;
        }
        return new ThreadPool(executorService, false, availableProcessors);
    }
    
    private static int getDefaultThreadPoolInitialSize() {
        final String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("java.nio.channels.DefaultThreadPool.initialSize"));
        if (s != null) {
            try {
                return Integer.parseInt(s);
            }
            catch (final NumberFormatException ex) {
                throw new Error("Value of property 'java.nio.channels.DefaultThreadPool.initialSize' is invalid: " + ex);
            }
        }
        return -1;
    }
    
    private static ThreadFactory getDefaultThreadPoolThreadFactory() {
        final String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("java.nio.channels.DefaultThreadPool.threadFactory"));
        if (s != null) {
            try {
                return (ThreadFactory)Class.forName(s, true, ClassLoader.getSystemClassLoader()).newInstance();
            }
            catch (final ClassNotFoundException ex) {
                throw new Error(ex);
            }
            catch (final InstantiationException ex2) {
                throw new Error(ex2);
            }
            catch (final IllegalAccessException ex3) {
                throw new Error(ex3);
            }
        }
        return null;
    }
    
    private static class DefaultThreadPoolHolder
    {
        static final ThreadPool defaultThreadPool;
        
        static {
            defaultThreadPool = ThreadPool.createDefault();
        }
    }
}
