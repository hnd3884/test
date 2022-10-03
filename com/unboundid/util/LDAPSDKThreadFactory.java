package com.unboundid.util;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.ThreadFactory;

@Mutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class LDAPSDKThreadFactory implements ThreadFactory
{
    private final AtomicLong threadCounter;
    private final boolean daemon;
    private final String baseName;
    private final ThreadGroup threadGroup;
    
    public LDAPSDKThreadFactory(final String baseName, final boolean daemon) {
        this(baseName, daemon, null);
    }
    
    public LDAPSDKThreadFactory(final String baseName, final boolean daemon, final ThreadGroup threadGroup) {
        this.baseName = baseName;
        this.daemon = daemon;
        this.threadGroup = threadGroup;
        this.threadCounter = new AtomicLong(1L);
    }
    
    @Override
    public Thread newThread(final Runnable r) {
        final String name = this.baseName + ' ' + this.threadCounter.getAndIncrement();
        final Thread t = new Thread(this.threadGroup, r, name);
        t.setDaemon(this.daemon);
        return t;
    }
}
