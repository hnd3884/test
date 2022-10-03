package com.sun.corba.se.impl.orbutil.threadpool;

import com.sun.corba.se.spi.orbutil.threadpool.ThreadPoolChooser;
import com.sun.corba.se.spi.orbutil.threadpool.NoSuchThreadPoolException;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.atomic.AtomicInteger;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orbutil.threadpool.ThreadPool;
import com.sun.corba.se.spi.orbutil.threadpool.ThreadPoolManager;

public class ThreadPoolManagerImpl implements ThreadPoolManager
{
    private ThreadPool threadPool;
    private ThreadGroup threadGroup;
    private static final ORBUtilSystemException wrapper;
    private static AtomicInteger tgCount;
    
    public ThreadPoolManagerImpl() {
        this.threadGroup = this.getThreadGroup();
        this.threadPool = new ThreadPoolImpl(this.threadGroup, "default-threadpool");
    }
    
    private ThreadGroup getThreadGroup() {
        ThreadGroup threadGroup;
        try {
            threadGroup = AccessController.doPrivileged((PrivilegedAction<ThreadGroup>)new PrivilegedAction<ThreadGroup>() {
                @Override
                public ThreadGroup run() {
                    ThreadGroup threadGroup2;
                    ThreadGroup threadGroup = threadGroup2 = Thread.currentThread().getThreadGroup();
                    try {
                        while (threadGroup2 != null) {
                            threadGroup = threadGroup2;
                            threadGroup2 = threadGroup.getParent();
                        }
                    }
                    catch (final SecurityException ex) {}
                    return new ThreadGroup(threadGroup, "ORB ThreadGroup " + ThreadPoolManagerImpl.tgCount.getAndIncrement());
                }
            });
        }
        catch (final SecurityException ex) {
            threadGroup = Thread.currentThread().getThreadGroup();
        }
        return threadGroup;
    }
    
    @Override
    public void close() {
        try {
            this.threadPool.close();
        }
        catch (final IOException ex) {
            ThreadPoolManagerImpl.wrapper.threadPoolCloseError();
        }
        try {
            final boolean destroyed = this.threadGroup.isDestroyed();
            final int activeCount = this.threadGroup.activeCount();
            final int activeGroupCount = this.threadGroup.activeGroupCount();
            if (destroyed) {
                ThreadPoolManagerImpl.wrapper.threadGroupIsDestroyed(this.threadGroup);
            }
            else {
                if (activeCount > 0) {
                    ThreadPoolManagerImpl.wrapper.threadGroupHasActiveThreadsInClose(this.threadGroup, activeCount);
                }
                if (activeGroupCount > 0) {
                    ThreadPoolManagerImpl.wrapper.threadGroupHasSubGroupsInClose(this.threadGroup, activeGroupCount);
                }
                this.threadGroup.destroy();
            }
        }
        catch (final IllegalThreadStateException ex2) {
            ThreadPoolManagerImpl.wrapper.threadGroupDestroyFailed(ex2, this.threadGroup);
        }
        this.threadGroup = null;
    }
    
    @Override
    public ThreadPool getThreadPool(final String s) throws NoSuchThreadPoolException {
        return this.threadPool;
    }
    
    @Override
    public ThreadPool getThreadPool(final int n) throws NoSuchThreadPoolException {
        return this.threadPool;
    }
    
    @Override
    public int getThreadPoolNumericId(final String s) {
        return 0;
    }
    
    @Override
    public String getThreadPoolStringId(final int n) {
        return "";
    }
    
    @Override
    public ThreadPool getDefaultThreadPool() {
        return this.threadPool;
    }
    
    @Override
    public ThreadPoolChooser getThreadPoolChooser(final String s) {
        return null;
    }
    
    @Override
    public ThreadPoolChooser getThreadPoolChooser(final int n) {
        return null;
    }
    
    @Override
    public void setThreadPoolChooser(final String s, final ThreadPoolChooser threadPoolChooser) {
    }
    
    @Override
    public int getThreadPoolChooserNumericId(final String s) {
        return 0;
    }
    
    static {
        wrapper = ORBUtilSystemException.get("rpc.transport");
        ThreadPoolManagerImpl.tgCount = new AtomicInteger();
    }
}
