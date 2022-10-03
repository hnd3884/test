package org.glassfish.jersey.spi;

import javax.annotation.PreDestroy;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

public class ThreadPoolExecutorProvider extends AbstractThreadPoolProvider<ThreadPoolExecutor> implements ExecutorServiceProvider
{
    private static final long CACHED_POOL_KEEP_ALIVE_DEFAULT_TIMEOUT = 60L;
    
    public ThreadPoolExecutorProvider(final String name) {
        super(name);
    }
    
    @Override
    public ExecutorService getExecutorService() {
        return super.getExecutor();
    }
    
    @Override
    protected final ThreadPoolExecutor createExecutor(final int corePoolSize, final ThreadFactory threadFactory, final RejectedExecutionHandler handler) {
        return this.createExecutor(corePoolSize, this.getMaximumPoolSize(), this.getKeepAliveTime(), this.getWorkQueue(), threadFactory, handler);
    }
    
    protected ThreadPoolExecutor createExecutor(final int corePoolSize, final int maximumPoolSize, final long keepAliveTime, final BlockingQueue<Runnable> workQueue, final ThreadFactory threadFactory, final RejectedExecutionHandler handler) {
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue, threadFactory, handler);
    }
    
    protected int getMaximumPoolSize() {
        return Integer.MAX_VALUE;
    }
    
    protected long getKeepAliveTime() {
        return 60L;
    }
    
    protected BlockingQueue<Runnable> getWorkQueue() {
        return (BlockingQueue<Runnable>)((this.getMaximumPoolSize() == Integer.MAX_VALUE) ? new SynchronousQueue<Object>() : new LinkedBlockingQueue<Object>());
    }
    
    @Override
    public void dispose(final ExecutorService executorService) {
    }
    
    @PreDestroy
    public void preDestroy() {
        this.close();
    }
}
