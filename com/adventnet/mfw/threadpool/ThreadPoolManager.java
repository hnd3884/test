package com.adventnet.mfw.threadpool;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.HashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.Map;

public class ThreadPoolManager
{
    private static ThreadPoolManager threadPoolManager;
    private static Map<String, ThreadPoolExecutor> executorsMap;
    public static final String DEFAULT_POOL = "Default";
    public static final int DEFAULT_POOL_SIZE = 5;
    public static final long DEFAULT_ALIVE_TIME = 1000L;
    public static final Integer DEFAULT_MAX_POOL_SIZE;
    public static final int DEFAULT_QUEUE_SIZE = 10;
    
    private ThreadPoolManager() {
    }
    
    public static ThreadPoolManager getInstance() {
        return ThreadPoolManager.threadPoolManager;
    }
    
    private synchronized ThreadPoolExecutor getOrCreate(String poolName, Integer threadCorePoolSize, Integer threadMaxPoolSize, Integer queueSize, Long idleTime) throws ThreadPoolException {
        if (ThreadPoolManager.executorsMap == null) {
            ThreadPoolManager.executorsMap = new HashMap<String, ThreadPoolExecutor>();
        }
        if (poolName == null) {
            poolName = "Default";
        }
        if (ThreadPoolManager.executorsMap.containsKey(poolName)) {
            return ThreadPoolManager.executorsMap.get(poolName);
        }
        threadCorePoolSize = ((threadCorePoolSize != null) ? threadCorePoolSize : 5);
        threadMaxPoolSize = ((threadMaxPoolSize != null) ? threadMaxPoolSize : ThreadPoolManager.DEFAULT_MAX_POOL_SIZE);
        queueSize = ((queueSize != null) ? queueSize : 10);
        idleTime = ((idleTime != null) ? idleTime : 1000L);
        if (threadMaxPoolSize < threadCorePoolSize) {
            throw new ThreadPoolException(ThreadPoolException.ThreadPoolErrorCodes.INVALID_MAXPOOLSIZE);
        }
        final BlockingQueue<Runnable> blockingQueue = (queueSize != null) ? new LinkedBlockingDeque<Runnable>(queueSize) : new LinkedBlockingDeque<Runnable>();
        final ThreadPoolExecutor executor = new ThreadPoolExecutor(threadCorePoolSize, threadMaxPoolSize, idleTime, TimeUnit.MILLISECONDS, blockingQueue);
        executor.setThreadFactory(new ThreadFactoryImpl(poolName));
        ThreadPoolManager.executorsMap.put(poolName, executor);
        return executor;
    }
    
    public ThreadPoolExecutor createDefaultExecutor(final String poolName) throws ThreadPoolException {
        return this.getOrCreate(poolName, null, null, null, null);
    }
    
    public ThreadPoolExecutor createThreadPoolExecutor(final String poolName, final Integer poolSize, final Integer maxPoolSize) throws ThreadPoolException {
        return this.getOrCreate(poolName, poolSize, maxPoolSize, null, null);
    }
    
    public ThreadPoolExecutor createThreadPoolExecutor(final String poolName, final Integer poolSize, final Integer maxPoolSize, final Long aliveTime, final Integer queueSize) throws ThreadPoolException {
        return this.getOrCreate(poolName, poolSize, maxPoolSize, queueSize, aliveTime);
    }
    
    public ThreadPoolExecutor getThreadPoolExecutor(final String poolName) throws ThreadPoolException {
        if (poolName == null) {
            throw new ThreadPoolException(ThreadPoolException.ThreadPoolErrorCodes.INVALID_POOLNAME);
        }
        return ThreadPoolManager.executorsMap.get(poolName);
    }
    
    public void setThreadName(final String poolName, final String threadName) throws ThreadPoolException {
        final ThreadPoolExecutor executor = this.getThreadPoolExecutor(poolName);
        if (executor == null) {
            throw new ThreadPoolException(ThreadPoolException.ThreadPoolErrorCodes.INVALID_POOLNAME);
        }
        final ThreadFactoryImpl threadFactory = (ThreadFactoryImpl)this.getThreadPoolExecutor(poolName).getThreadFactory();
        threadFactory.setThreadName(threadName);
    }
    
    public Future<?> submit(final String poolName, final Runnable task) throws ThreadPoolException {
        final ThreadPoolExecutor executor = this.getThreadPoolExecutor(poolName);
        if (executor == null) {
            throw new ThreadPoolException(ThreadPoolException.ThreadPoolErrorCodes.INVALID_POOLNAME);
        }
        return executor.submit(task);
    }
    
    public <T> Future<T> submit(final String poolName, final Callable<T> task) throws ThreadPoolException {
        final ThreadPoolExecutor executor = this.getThreadPoolExecutor(poolName);
        if (executor == null) {
            throw new ThreadPoolException(ThreadPoolException.ThreadPoolErrorCodes.INVALID_POOLNAME);
        }
        return executor.submit(task);
    }
    
    public void shutdown(final String poolName) throws ThreadPoolException {
        final ThreadPoolExecutor executor = this.getThreadPoolExecutor(poolName);
        if (executor == null) {
            throw new ThreadPoolException(ThreadPoolException.ThreadPoolErrorCodes.INVALID_POOLNAME);
        }
        ThreadPoolManager.executorsMap.remove(poolName);
        executor.shutdown();
    }
    
    public void awaitTermination(final String poolName, final long timeout, final TimeUnit unit) throws ThreadPoolException, InterruptedException {
        final ThreadPoolExecutor executor = this.getThreadPoolExecutor(poolName);
        if (executor == null) {
            throw new ThreadPoolException(ThreadPoolException.ThreadPoolErrorCodes.INVALID_POOLNAME);
        }
        executor.awaitTermination(timeout, unit);
    }
    
    public List<Runnable> shutdownNow(final String poolName) throws ThreadPoolException {
        final ThreadPoolExecutor executor = this.getThreadPoolExecutor(poolName);
        if (executor == null) {
            throw new ThreadPoolException(ThreadPoolException.ThreadPoolErrorCodes.INVALID_POOLNAME);
        }
        ThreadPoolManager.executorsMap.remove(poolName);
        return executor.shutdownNow();
    }
    
    public boolean isShutdown(final String poolName) throws ThreadPoolException {
        final ThreadPoolExecutor executor = this.getThreadPoolExecutor(poolName);
        if (executor == null) {
            throw new ThreadPoolException(ThreadPoolException.ThreadPoolErrorCodes.INVALID_POOLNAME);
        }
        return executor.isShutdown();
    }
    
    public boolean isTerminated(final String poolName) throws ThreadPoolException {
        final ThreadPoolExecutor executor = this.getThreadPoolExecutor(poolName);
        if (executor == null) {
            throw new ThreadPoolException(ThreadPoolException.ThreadPoolErrorCodes.INVALID_POOLNAME);
        }
        return executor.isTerminated();
    }
    
    public boolean isTerminating(final String poolName) throws ThreadPoolException {
        final ThreadPoolExecutor executor = this.getThreadPoolExecutor(poolName);
        if (executor == null) {
            throw new ThreadPoolException(ThreadPoolException.ThreadPoolErrorCodes.INVALID_POOLNAME);
        }
        return executor.isTerminating();
    }
    
    static {
        ThreadPoolManager.threadPoolManager = new ThreadPoolManager();
        DEFAULT_MAX_POOL_SIZE = 10;
    }
    
    public class ThreadFactoryImpl implements ThreadFactory
    {
        private String threadName;
        
        protected ThreadFactoryImpl(final String name) {
            this.threadName = name;
        }
        
        @Override
        public Thread newThread(final Runnable r) {
            return new Thread(r, this.threadName);
        }
        
        public void setThreadName(final String threadName) {
            this.threadName = threadName;
        }
    }
}
