package org.apache.catalina.startup;

import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.ForkJoinPool;

public class SafeForkJoinWorkerThreadFactory implements ForkJoinPool.ForkJoinWorkerThreadFactory
{
    @Override
    public ForkJoinWorkerThread newThread(final ForkJoinPool pool) {
        return new SafeForkJoinWorkerThread(pool);
    }
    
    private static class SafeForkJoinWorkerThread extends ForkJoinWorkerThread
    {
        protected SafeForkJoinWorkerThread(final ForkJoinPool pool) {
            super(pool);
            this.setContextClassLoader(ForkJoinPool.class.getClassLoader());
        }
    }
}
