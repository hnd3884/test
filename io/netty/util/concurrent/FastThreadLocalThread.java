package io.netty.util.concurrent;

import io.netty.util.internal.InternalThreadLocalMap;

public class FastThreadLocalThread extends Thread
{
    private final boolean cleanupFastThreadLocals;
    private InternalThreadLocalMap threadLocalMap;
    
    public FastThreadLocalThread() {
        this.cleanupFastThreadLocals = false;
    }
    
    public FastThreadLocalThread(final Runnable target) {
        super(FastThreadLocalRunnable.wrap(target));
        this.cleanupFastThreadLocals = true;
    }
    
    public FastThreadLocalThread(final ThreadGroup group, final Runnable target) {
        super(group, FastThreadLocalRunnable.wrap(target));
        this.cleanupFastThreadLocals = true;
    }
    
    public FastThreadLocalThread(final String name) {
        super(name);
        this.cleanupFastThreadLocals = false;
    }
    
    public FastThreadLocalThread(final ThreadGroup group, final String name) {
        super(group, name);
        this.cleanupFastThreadLocals = false;
    }
    
    public FastThreadLocalThread(final Runnable target, final String name) {
        super(FastThreadLocalRunnable.wrap(target), name);
        this.cleanupFastThreadLocals = true;
    }
    
    public FastThreadLocalThread(final ThreadGroup group, final Runnable target, final String name) {
        super(group, FastThreadLocalRunnable.wrap(target), name);
        this.cleanupFastThreadLocals = true;
    }
    
    public FastThreadLocalThread(final ThreadGroup group, final Runnable target, final String name, final long stackSize) {
        super(group, FastThreadLocalRunnable.wrap(target), name, stackSize);
        this.cleanupFastThreadLocals = true;
    }
    
    public final InternalThreadLocalMap threadLocalMap() {
        return this.threadLocalMap;
    }
    
    public final void setThreadLocalMap(final InternalThreadLocalMap threadLocalMap) {
        this.threadLocalMap = threadLocalMap;
    }
    
    public boolean willCleanupFastThreadLocals() {
        return this.cleanupFastThreadLocals;
    }
    
    public static boolean willCleanupFastThreadLocals(final Thread thread) {
        return thread instanceof FastThreadLocalThread && ((FastThreadLocalThread)thread).willCleanupFastThreadLocals();
    }
}
