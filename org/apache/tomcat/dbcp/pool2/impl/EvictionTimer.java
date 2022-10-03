package org.apache.tomcat.dbcp.pool2.impl;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadFactory;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;

class EvictionTimer
{
    private static ScheduledThreadPoolExecutor executor;
    private static final HashMap<WeakReference<Runnable>, WeakRunner> taskMap;
    
    private EvictionTimer() {
    }
    
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("EvictionTimer []");
        return builder.toString();
    }
    
    static synchronized void schedule(final BaseGenericObjectPool.Evictor task, final long delay, final long period) {
        if (null == EvictionTimer.executor) {
            (EvictionTimer.executor = new ScheduledThreadPoolExecutor(1, new EvictorThreadFactory())).setRemoveOnCancelPolicy(true);
            EvictionTimer.executor.scheduleAtFixedRate(new Reaper(), delay, period, TimeUnit.MILLISECONDS);
        }
        final WeakReference<Runnable> ref = new WeakReference<Runnable>(task);
        final WeakRunner runner = new WeakRunner((WeakReference)ref);
        final ScheduledFuture<?> scheduledFuture = EvictionTimer.executor.scheduleWithFixedDelay(runner, delay, period, TimeUnit.MILLISECONDS);
        task.setScheduledFuture(scheduledFuture);
        EvictionTimer.taskMap.put(ref, runner);
    }
    
    static synchronized void cancel(final BaseGenericObjectPool.Evictor evictor, final long timeout, final TimeUnit unit, final boolean restarting) {
        if (evictor != null) {
            evictor.cancel();
            remove(evictor);
        }
        if (!restarting && EvictionTimer.executor != null && EvictionTimer.taskMap.isEmpty()) {
            EvictionTimer.executor.shutdown();
            try {
                EvictionTimer.executor.awaitTermination(timeout, unit);
            }
            catch (final InterruptedException ex) {}
            EvictionTimer.executor.setCorePoolSize(0);
            EvictionTimer.executor = null;
        }
    }
    
    private static void remove(final BaseGenericObjectPool.Evictor evictor) {
        for (final Map.Entry<WeakReference<Runnable>, WeakRunner> entry : EvictionTimer.taskMap.entrySet()) {
            if (entry.getKey().get() == evictor) {
                EvictionTimer.executor.remove(entry.getValue());
                EvictionTimer.taskMap.remove(entry.getKey());
                break;
            }
        }
    }
    
    static synchronized int getNumTasks() {
        return EvictionTimer.taskMap.size();
    }
    
    static {
        taskMap = new HashMap<WeakReference<Runnable>, WeakRunner>();
    }
    
    private static class EvictorThreadFactory implements ThreadFactory
    {
        @Override
        public Thread newThread(final Runnable runnable) {
            final Thread thread = new Thread(null, runnable, "commons-pool-evictor-thread");
            thread.setDaemon(true);
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
                @Override
                public Void run() {
                    thread.setContextClassLoader(EvictorThreadFactory.class.getClassLoader());
                    return null;
                }
            });
            return thread;
        }
    }
    
    private static class Reaper implements Runnable
    {
        @Override
        public void run() {
            synchronized (EvictionTimer.class) {
                for (final Map.Entry<WeakReference<Runnable>, WeakRunner> entry : EvictionTimer.taskMap.entrySet()) {
                    if (entry.getKey().get() == null) {
                        EvictionTimer.executor.remove(entry.getValue());
                        EvictionTimer.taskMap.remove(entry.getKey());
                    }
                }
                if (EvictionTimer.taskMap.isEmpty() && EvictionTimer.executor != null) {
                    EvictionTimer.executor.shutdown();
                    EvictionTimer.executor.setCorePoolSize(0);
                    EvictionTimer.executor = null;
                }
            }
        }
    }
    
    private static class WeakRunner implements Runnable
    {
        private final WeakReference<Runnable> ref;
        
        private WeakRunner(final WeakReference<Runnable> ref) {
            this.ref = ref;
        }
        
        @Override
        public void run() {
            final Runnable task = this.ref.get();
            if (task != null) {
                task.run();
            }
            else {
                EvictionTimer.executor.remove(this);
                EvictionTimer.taskMap.remove(this.ref);
            }
        }
    }
}
