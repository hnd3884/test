package org.apache.lucene.util;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ThreadFactory;

public class NamedThreadFactory implements ThreadFactory
{
    private static final AtomicInteger threadPoolNumber;
    private final ThreadGroup group;
    private final AtomicInteger threadNumber;
    private static final String NAME_PATTERN = "%s-%d-thread";
    private final String threadNamePrefix;
    
    public NamedThreadFactory(final String threadNamePrefix) {
        this.threadNumber = new AtomicInteger(1);
        final SecurityManager s = System.getSecurityManager();
        this.group = ((s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup());
        this.threadNamePrefix = String.format(Locale.ROOT, "%s-%d-thread", checkPrefix(threadNamePrefix), NamedThreadFactory.threadPoolNumber.getAndIncrement());
    }
    
    private static String checkPrefix(final String prefix) {
        return (prefix == null || prefix.length() == 0) ? "Lucene" : prefix;
    }
    
    @Override
    public Thread newThread(final Runnable r) {
        final Thread t = new Thread(this.group, r, String.format(Locale.ROOT, "%s-%d", this.threadNamePrefix, this.threadNumber.getAndIncrement()), 0L);
        t.setDaemon(false);
        t.setPriority(5);
        return t;
    }
    
    static {
        threadPoolNumber = new AtomicInteger(1);
    }
}
