package io.netty.util.internal;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.Executor;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.FastThreadLocal;

public final class ThreadExecutorMap
{
    private static final FastThreadLocal<EventExecutor> mappings;
    
    private ThreadExecutorMap() {
    }
    
    public static EventExecutor currentExecutor() {
        return ThreadExecutorMap.mappings.get();
    }
    
    private static void setCurrentEventExecutor(final EventExecutor executor) {
        ThreadExecutorMap.mappings.set(executor);
    }
    
    public static Executor apply(final Executor executor, final EventExecutor eventExecutor) {
        ObjectUtil.checkNotNull(executor, "executor");
        ObjectUtil.checkNotNull(eventExecutor, "eventExecutor");
        return new Executor() {
            @Override
            public void execute(final Runnable command) {
                executor.execute(ThreadExecutorMap.apply(command, eventExecutor));
            }
        };
    }
    
    public static Runnable apply(final Runnable command, final EventExecutor eventExecutor) {
        ObjectUtil.checkNotNull(command, "command");
        ObjectUtil.checkNotNull(eventExecutor, "eventExecutor");
        return new Runnable() {
            @Override
            public void run() {
                setCurrentEventExecutor(eventExecutor);
                try {
                    command.run();
                }
                finally {
                    setCurrentEventExecutor(null);
                }
            }
        };
    }
    
    public static ThreadFactory apply(final ThreadFactory threadFactory, final EventExecutor eventExecutor) {
        ObjectUtil.checkNotNull(threadFactory, "command");
        ObjectUtil.checkNotNull(eventExecutor, "eventExecutor");
        return new ThreadFactory() {
            @Override
            public Thread newThread(final Runnable r) {
                return threadFactory.newThread(ThreadExecutorMap.apply(r, eventExecutor));
            }
        };
    }
    
    static {
        mappings = new FastThreadLocal<EventExecutor>();
    }
}
