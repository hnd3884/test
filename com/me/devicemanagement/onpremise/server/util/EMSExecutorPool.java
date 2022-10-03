package com.me.devicemanagement.onpremise.server.util;

import com.zoho.db.scanner.util.ExecutorPool;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.concurrent.Future;
import java.util.concurrent.Callable;
import com.adventnet.mfw.threadpool.ThreadPoolException;
import com.adventnet.mfw.threadpool.ThreadPoolManager;
import java.util.Collection;
import java.util.logging.Logger;

public class EMSExecutorPool implements AutoCloseable
{
    private static final Logger LOGGER;
    private Logger logger;
    private boolean isInterrupted;
    private Collection statusObjects;
    private String poolName;
    private static ThreadPoolManager threadPoolManager;
    
    public EMSExecutorPool(final String poolName, final int workerPoolSize, final int workerMaxPoolSize, final int queueSize, final long idleTimeout, final Logger logger) throws ThreadPoolException {
        this.isInterrupted = Boolean.FALSE;
        this.poolName = poolName;
        EMSExecutorPool.threadPoolManager.createThreadPoolExecutor(poolName, Integer.valueOf(workerPoolSize), Integer.valueOf(workerMaxPoolSize), Long.valueOf(idleTimeout), Integer.valueOf(queueSize));
        this.logger = logger;
    }
    
    public Future submitTaskForWorker(final Callable callable) throws ThreadPoolException {
        return EMSExecutorPool.threadPoolManager.submit(this.poolName, callable);
    }
    
    public void waitForTaskCompletion() throws ThreadPoolException, InterruptedException {
        EMSExecutorPool.LOGGER.info("Waiting for Task completion");
        this.logger.log(Level.INFO, "Waiting for Task completion");
        TimeUnit.SECONDS.sleep(1L);
        int threshold = 0;
        final ThreadPoolExecutor workerPool = EMSExecutorPool.threadPoolManager.getThreadPoolExecutor(this.poolName);
        while (workerPool.getActiveCount() != 0 || workerPool.getQueue().size() != 0 || workerPool.getTaskCount() > workerPool.getCompletedTaskCount()) {
            this.checkForPoolShutdown();
            if (threshold % 30 == 0) {
                EMSExecutorPool.LOGGER.info("Active workers ::: " + workerPool.getActiveCount() + "/" + workerPool.getQueue().size());
                this.logger.info("Active workers ::: " + workerPool.getActiveCount() + "/" + workerPool.getQueue().size());
                threshold = 0;
            }
            ++threshold;
            Thread.sleep(100L);
        }
        EMSExecutorPool.LOGGER.info("Worker pool status " + workerPool.getActiveCount() + "/" + workerPool.getQueue().size());
        this.logger.info("Worker pool status " + workerPool.getActiveCount() + "/" + workerPool.getQueue().size());
        if (this.isInterrupted) {
            throw new InterruptedException();
        }
    }
    
    public boolean hasActiveOrQueuedTasks() throws ThreadPoolException {
        final ThreadPoolExecutor workerPool = EMSExecutorPool.threadPoolManager.getThreadPoolExecutor(this.poolName);
        return workerPool.getActiveCount() != 0 || workerPool.getQueue().size() != 0 || workerPool.getTaskCount() > workerPool.getCompletedTaskCount();
    }
    
    public void waitForTaskCompletionAndShutDown() throws ThreadPoolException, InterruptedException {
        this.waitForTaskCompletion();
        this.close();
        this.checkForPoolShutdown();
    }
    
    public void checkForPoolShutdown() throws ThreadPoolException, InterruptedException {
        if (EMSExecutorPool.threadPoolManager.getThreadPoolExecutor(this.poolName) != null && EMSExecutorPool.threadPoolManager.isShutdown(this.poolName)) {
            this.waitForWorkerPoolShutdown();
            throw new InterruptedException("WorkerPOOL execution terminated forcibly...");
        }
    }
    
    public void waitForWorkerPoolShutdown() throws ThreadPoolException, InterruptedException {
        while (!EMSExecutorPool.threadPoolManager.isTerminated(this.poolName)) {
            TimeUnit.MILLISECONDS.sleep(100L);
        }
    }
    
    @Override
    public void close() throws ThreadPoolException {
        EMSExecutorPool.threadPoolManager.shutdown(this.poolName);
    }
    
    public synchronized boolean interruptPool() throws ThreadPoolException {
        if (!this.isInterrupted) {
            this.isInterrupted = true;
            EMSExecutorPool.threadPoolManager.shutdownNow(this.poolName);
            return true;
        }
        return false;
    }
    
    static {
        LOGGER = Logger.getLogger(ExecutorPool.class.getName());
        EMSExecutorPool.threadPoolManager = ThreadPoolManager.getInstance();
    }
}
