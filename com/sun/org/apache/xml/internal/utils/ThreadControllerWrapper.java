package com.sun.org.apache.xml.internal.utils;

public class ThreadControllerWrapper
{
    private static ThreadController m_tpool;
    
    public static Thread runThread(final Runnable runnable, final int priority) {
        return ThreadControllerWrapper.m_tpool.run(runnable, priority);
    }
    
    public static void waitThread(final Thread worker, final Runnable task) throws InterruptedException {
        ThreadControllerWrapper.m_tpool.waitThread(worker, task);
    }
    
    static {
        ThreadControllerWrapper.m_tpool = new ThreadController();
    }
    
    public static class ThreadController
    {
        public Thread run(final Runnable task, final int priority) {
            final Thread t = new SafeThread(task);
            t.start();
            return t;
        }
        
        public void waitThread(final Thread worker, final Runnable task) throws InterruptedException {
            worker.join();
        }
        
        final class SafeThread extends Thread
        {
            private volatile boolean ran;
            
            public SafeThread(final Runnable target) {
                super(target);
                this.ran = false;
            }
            
            @Override
            public final void run() {
                if (Thread.currentThread() != this) {
                    throw new IllegalStateException("The run() method in a SafeThread cannot be called from another thread.");
                }
                synchronized (this) {
                    if (this.ran) {
                        throw new IllegalStateException("The run() method in a SafeThread cannot be called more than once.");
                    }
                    this.ran = true;
                }
                super.run();
            }
        }
    }
}
