package org.apache.tomcat.util.threads;

import java.security.PrivilegedAction;
import java.security.AccessController;
import org.apache.tomcat.util.security.PrivilegedSetTccl;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ThreadFactory;

public class TaskThreadFactory implements ThreadFactory
{
    private final ThreadGroup group;
    private final AtomicInteger threadNumber;
    private final String namePrefix;
    private final boolean daemon;
    private final int threadPriority;
    
    public TaskThreadFactory(final String namePrefix, final boolean daemon, final int priority) {
        this.threadNumber = new AtomicInteger(1);
        final SecurityManager s = System.getSecurityManager();
        this.group = ((s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup());
        this.namePrefix = namePrefix;
        this.daemon = daemon;
        this.threadPriority = priority;
    }
    
    @Override
    public Thread newThread(final Runnable r) {
        final TaskThread t = new TaskThread(this.group, r, this.namePrefix + this.threadNumber.getAndIncrement());
        t.setDaemon(this.daemon);
        t.setPriority(this.threadPriority);
        if (Constants.IS_SECURITY_ENABLED) {
            final PrivilegedAction<Void> pa = new PrivilegedSetTccl(t, this.getClass().getClassLoader());
            AccessController.doPrivileged(pa);
        }
        else {
            t.setContextClassLoader(this.getClass().getClassLoader());
        }
        return t;
    }
}
