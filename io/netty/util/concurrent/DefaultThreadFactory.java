package io.netty.util.concurrent;

import java.util.Locale;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.ObjectUtil;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ThreadFactory;

public class DefaultThreadFactory implements ThreadFactory
{
    private static final AtomicInteger poolId;
    private final AtomicInteger nextId;
    private final String prefix;
    private final boolean daemon;
    private final int priority;
    protected final ThreadGroup threadGroup;
    
    public DefaultThreadFactory(final Class<?> poolType) {
        this(poolType, false, 5);
    }
    
    public DefaultThreadFactory(final String poolName) {
        this(poolName, false, 5);
    }
    
    public DefaultThreadFactory(final Class<?> poolType, final boolean daemon) {
        this(poolType, daemon, 5);
    }
    
    public DefaultThreadFactory(final String poolName, final boolean daemon) {
        this(poolName, daemon, 5);
    }
    
    public DefaultThreadFactory(final Class<?> poolType, final int priority) {
        this(poolType, false, priority);
    }
    
    public DefaultThreadFactory(final String poolName, final int priority) {
        this(poolName, false, priority);
    }
    
    public DefaultThreadFactory(final Class<?> poolType, final boolean daemon, final int priority) {
        this(toPoolName(poolType), daemon, priority);
    }
    
    public static String toPoolName(final Class<?> poolType) {
        ObjectUtil.checkNotNull(poolType, "poolType");
        final String poolName = StringUtil.simpleClassName(poolType);
        switch (poolName.length()) {
            case 0: {
                return "unknown";
            }
            case 1: {
                return poolName.toLowerCase(Locale.US);
            }
            default: {
                if (Character.isUpperCase(poolName.charAt(0)) && Character.isLowerCase(poolName.charAt(1))) {
                    return Character.toLowerCase(poolName.charAt(0)) + poolName.substring(1);
                }
                return poolName;
            }
        }
    }
    
    public DefaultThreadFactory(final String poolName, final boolean daemon, final int priority, final ThreadGroup threadGroup) {
        this.nextId = new AtomicInteger();
        ObjectUtil.checkNotNull(poolName, "poolName");
        if (priority < 1 || priority > 10) {
            throw new IllegalArgumentException("priority: " + priority + " (expected: Thread.MIN_PRIORITY <= priority <= Thread.MAX_PRIORITY)");
        }
        this.prefix = poolName + '-' + DefaultThreadFactory.poolId.incrementAndGet() + '-';
        this.daemon = daemon;
        this.priority = priority;
        this.threadGroup = threadGroup;
    }
    
    public DefaultThreadFactory(final String poolName, final boolean daemon, final int priority) {
        this(poolName, daemon, priority, null);
    }
    
    @Override
    public Thread newThread(final Runnable r) {
        final Thread t = this.newThread(FastThreadLocalRunnable.wrap(r), this.prefix + this.nextId.incrementAndGet());
        try {
            if (t.isDaemon() != this.daemon) {
                t.setDaemon(this.daemon);
            }
            if (t.getPriority() != this.priority) {
                t.setPriority(this.priority);
            }
        }
        catch (final Exception ex) {}
        return t;
    }
    
    protected Thread newThread(final Runnable r, final String name) {
        return new FastThreadLocalThread(this.threadGroup, r, name);
    }
    
    static {
        poolId = new AtomicInteger();
    }
}
