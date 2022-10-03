package org.glassfish.jersey.spi;

import java.util.concurrent.ThreadPoolExecutor;
import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class ScheduledThreadPoolExecutorProvider extends AbstractThreadPoolProvider<ScheduledThreadPoolExecutor> implements ScheduledExecutorServiceProvider
{
    public ScheduledThreadPoolExecutorProvider(final String name) {
        super(name);
    }
    
    @Override
    public ScheduledExecutorService getExecutorService() {
        return super.getExecutor();
    }
    
    @Override
    protected ScheduledThreadPoolExecutor createExecutor(final int corePoolSize, final ThreadFactory threadFactory, final RejectedExecutionHandler handler) {
        return new ScheduledThreadPoolExecutor(corePoolSize, threadFactory, handler);
    }
    
    @Override
    public void dispose(final ExecutorService executorService) {
    }
    
    @PreDestroy
    public void preDestroy() {
        this.close();
    }
}
