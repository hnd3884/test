package com.sun.xml.internal.ws.api.pipe;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.Executors;
import com.sun.xml.internal.ws.api.server.ContainerResolver;
import com.sun.xml.internal.ws.api.server.Container;
import java.util.concurrent.Executor;

public class Engine
{
    private volatile Executor threadPool;
    public final String id;
    private final Container container;
    
    String getId() {
        return this.id;
    }
    
    Container getContainer() {
        return this.container;
    }
    
    Executor getExecutor() {
        return this.threadPool;
    }
    
    public Engine(final String id, final Executor threadPool) {
        this(id, ContainerResolver.getDefault().getContainer(), threadPool);
    }
    
    public Engine(final String id, final Container container, final Executor threadPool) {
        this(id, container);
        this.threadPool = ((threadPool != null) ? this.wrap(threadPool) : null);
    }
    
    public Engine(final String id) {
        this(id, ContainerResolver.getDefault().getContainer());
    }
    
    public Engine(final String id, final Container container) {
        this.id = id;
        this.container = container;
    }
    
    public void setExecutor(final Executor threadPool) {
        this.threadPool = ((threadPool != null) ? this.wrap(threadPool) : null);
    }
    
    void addRunnable(final Fiber fiber) {
        if (this.threadPool == null) {
            synchronized (this) {
                this.threadPool = this.wrap(Executors.newCachedThreadPool(new DaemonThreadFactory()));
            }
        }
        this.threadPool.execute(fiber);
    }
    
    private Executor wrap(final Executor ex) {
        return ContainerResolver.getDefault().wrapExecutor(this.container, ex);
    }
    
    public Fiber createFiber() {
        return new Fiber(this);
    }
    
    private static class DaemonThreadFactory implements ThreadFactory
    {
        static final AtomicInteger poolNumber;
        final AtomicInteger threadNumber;
        final String namePrefix;
        
        DaemonThreadFactory() {
            this.threadNumber = new AtomicInteger(1);
            this.namePrefix = "jaxws-engine-" + DaemonThreadFactory.poolNumber.getAndIncrement() + "-thread-";
        }
        
        @Override
        public Thread newThread(final Runnable r) {
            final Thread t = new Thread(null, r, this.namePrefix + this.threadNumber.getAndIncrement(), 0L);
            if (!t.isDaemon()) {
                t.setDaemon(true);
            }
            if (t.getPriority() != 5) {
                t.setPriority(5);
            }
            return t;
        }
        
        static {
            poolNumber = new AtomicInteger(1);
        }
    }
}
