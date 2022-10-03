package sun.misc;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PermissionCollection;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.concurrent.atomic.AtomicInteger;
import java.security.AccessControlContext;

public final class InnocuousThread extends Thread
{
    private static final Unsafe UNSAFE;
    private static final long THREAD_LOCALS;
    private static final long INHERITABLE_THREAD_LOCALS;
    private static final ThreadGroup INNOCUOUSTHREADGROUP;
    private static final AccessControlContext ACC;
    private static final long INHERITEDACCESSCONTROLCONTEXT;
    private static final long CONTEXTCLASSLOADER;
    private static final AtomicInteger threadNumber;
    private volatile boolean hasRun;
    
    private static String newName() {
        return "InnocuousThread-" + InnocuousThread.threadNumber.getAndIncrement();
    }
    
    public static Thread newSystemThread(final Runnable runnable) {
        return newSystemThread(newName(), runnable);
    }
    
    public static Thread newSystemThread(final String s, final Runnable runnable) {
        return new InnocuousThread(InnocuousThread.INNOCUOUSTHREADGROUP, runnable, s, null);
    }
    
    public InnocuousThread(final Runnable runnable) {
        super(InnocuousThread.INNOCUOUSTHREADGROUP, runnable, newName());
        InnocuousThread.UNSAFE.putOrderedObject(this, InnocuousThread.INHERITEDACCESSCONTROLCONTEXT, InnocuousThread.ACC);
        this.eraseThreadLocals();
    }
    
    private InnocuousThread(final ThreadGroup threadGroup, final Runnable runnable, final String s, final ClassLoader classLoader) {
        super(threadGroup, runnable, s, 0L);
        InnocuousThread.UNSAFE.putOrderedObject(this, InnocuousThread.INHERITEDACCESSCONTROLCONTEXT, InnocuousThread.ACC);
        InnocuousThread.UNSAFE.putOrderedObject(this, InnocuousThread.CONTEXTCLASSLOADER, classLoader);
        this.eraseThreadLocals();
    }
    
    @Override
    public ClassLoader getContextClassLoader() {
        return ClassLoader.getSystemClassLoader();
    }
    
    @Override
    public void setUncaughtExceptionHandler(final UncaughtExceptionHandler uncaughtExceptionHandler) {
    }
    
    @Override
    public void setContextClassLoader(final ClassLoader classLoader) {
        throw new SecurityException("setContextClassLoader");
    }
    
    @Override
    public void run() {
        if (Thread.currentThread() == this && !this.hasRun) {
            this.hasRun = true;
            super.run();
        }
    }
    
    public void eraseThreadLocals() {
        InnocuousThread.UNSAFE.putObject(this, InnocuousThread.THREAD_LOCALS, null);
        InnocuousThread.UNSAFE.putObject(this, InnocuousThread.INHERITABLE_THREAD_LOCALS, null);
    }
    
    static {
        threadNumber = new AtomicInteger(1);
        try {
            ACC = new AccessControlContext(new ProtectionDomain[] { new ProtectionDomain(null, null) });
            UNSAFE = Unsafe.getUnsafe();
            final Class<Thread> clazz = Thread.class;
            final Class<ThreadGroup> clazz2 = ThreadGroup.class;
            THREAD_LOCALS = InnocuousThread.UNSAFE.objectFieldOffset(clazz.getDeclaredField("threadLocals"));
            INHERITABLE_THREAD_LOCALS = InnocuousThread.UNSAFE.objectFieldOffset(clazz.getDeclaredField("inheritableThreadLocals"));
            INHERITEDACCESSCONTROLCONTEXT = InnocuousThread.UNSAFE.objectFieldOffset(clazz.getDeclaredField("inheritedAccessControlContext"));
            CONTEXTCLASSLOADER = InnocuousThread.UNSAFE.objectFieldOffset(clazz.getDeclaredField("contextClassLoader"));
            final long objectFieldOffset = InnocuousThread.UNSAFE.objectFieldOffset(clazz.getDeclaredField("group"));
            final long objectFieldOffset2 = InnocuousThread.UNSAFE.objectFieldOffset(clazz2.getDeclaredField("parent"));
            ThreadGroup threadGroup;
            ThreadGroup threadGroup2;
            for (threadGroup = (ThreadGroup)InnocuousThread.UNSAFE.getObject(Thread.currentThread(), objectFieldOffset); threadGroup != null; threadGroup = threadGroup2) {
                threadGroup2 = (ThreadGroup)InnocuousThread.UNSAFE.getObject(threadGroup, objectFieldOffset2);
                if (threadGroup2 == null) {
                    break;
                }
            }
            INNOCUOUSTHREADGROUP = AccessController.doPrivileged((PrivilegedAction<ThreadGroup>)new PrivilegedAction<ThreadGroup>() {
                @Override
                public ThreadGroup run() {
                    return new ThreadGroup(threadGroup, "InnocuousThreadGroup");
                }
            });
        }
        catch (final Exception ex) {
            throw new Error(ex);
        }
    }
}
