package com.zoho.db.scanner.util;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.Collection;
import java.util.logging.Logger;
import java.util.concurrent.ExecutorService;

public class ExecutorPool implements AutoCloseable
{
    private ExecutorService workerPool;
    private static final Logger LOGGER;
    private int workerPoolSize;
    private boolean isInterrupted;
    private Collection statusObjects;
    private Throwable cause;
    
    public ExecutorPool() {
        this.workerPool = null;
        this.workerPoolSize = -1;
        this.isInterrupted = Boolean.FALSE;
        this.workerPool = Executors.newCachedThreadPool();
    }
    
    public ExecutorPool(final int poolSize) {
        this.workerPool = null;
        this.workerPoolSize = -1;
        this.isInterrupted = Boolean.FALSE;
        this.workerPoolSize = poolSize;
        this.workerPool = Executors.newFixedThreadPool(poolSize);
        ExecutorPool.LOGGER.info("Thread pool initialized with fixed size " + poolSize);
    }
    
    public ExecutorService getWorkerPool() {
        return this.workerPool;
    }
    
    public void submitTaskForWorker(final Callable callable) {
        this.workerPool.submit((Callable<Object>)callable);
    }
    
    public void waitForTaskCompletion() throws Exception {
        ExecutorPool.LOGGER.info("Waiting for Task completion");
        TimeUnit.SECONDS.sleep(1L);
        int threshold = 0;
        while (((ThreadPoolExecutor)this.getWorkerPool()).getActiveCount() != 0 || ((ThreadPoolExecutor)this.getWorkerPool()).getQueue().size() != 0 || ((ThreadPoolExecutor)this.getWorkerPool()).getTaskCount() > ((ThreadPoolExecutor)this.getWorkerPool()).getCompletedTaskCount()) {
            this.checkForPoolShutdown();
            if (threshold % 30 == 0) {
                ExecutorPool.LOGGER.info("Active workers ::: " + ((ThreadPoolExecutor)this.getWorkerPool()).getActiveCount() + "/" + ((ThreadPoolExecutor)this.getWorkerPool()).getQueue().size());
                threshold = 0;
            }
            ++threshold;
            Thread.sleep(100L);
        }
        ExecutorPool.LOGGER.info("Worker pool status " + ((ThreadPoolExecutor)this.getWorkerPool()).getActiveCount() + "/" + ((ThreadPoolExecutor)this.getWorkerPool()).getQueue().size());
        if (this.isInterrupted()) {
            throw new Exception(this.getCause());
        }
    }
    
    public void checkForPoolShutdown() throws InterruptedException {
        if (this.getWorkerPool().isShutdown()) {
            this.waitForWorkerPoolShutdown();
            throw new InterruptedException("WorkerPOOL execution terminated forcibly...");
        }
    }
    
    public void waitForWorkerPoolShutdown() throws InterruptedException {
        while (!this.getWorkerPool().isTerminated()) {
            Thread.sleep(100L);
        }
    }
    
    @Override
    public void close() throws Exception {
        this.workerPool.shutdown();
    }
    
    public boolean isInterrupted() {
        return this.isInterrupted;
    }
    
    protected void setInterrupted(final boolean isInterrupted) {
        this.isInterrupted = isInterrupted;
    }
    
    public Collection getStatusObjects() {
        return this.statusObjects;
    }
    
    public void setStatusObjects(final Object statusObjects) {
        this.statusObjects.add(statusObjects);
    }
    
    public Throwable getCause() {
        return this.cause;
    }
    
    protected void setCause(final Throwable cause) {
        this.cause = cause;
    }
    
    public synchronized boolean interruptPool(final Throwable cause) {
        if (!this.isInterrupted()) {
            this.setInterrupted(true);
            this.setCause(cause);
            this.getWorkerPool().shutdownNow();
            return true;
        }
        return false;
    }
    
    static {
        LOGGER = Logger.getLogger(ExecutorPool.class.getName());
    }
}
