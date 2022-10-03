package sun.rmi.runtime;

import java.security.AccessController;
import java.security.Permission;
import sun.security.util.SecurityConstants;
import java.security.PrivilegedAction;

public final class NewThreadAction implements PrivilegedAction<Thread>
{
    static final ThreadGroup systemThreadGroup;
    static final ThreadGroup userThreadGroup;
    private final ThreadGroup group;
    private final Runnable runnable;
    private final String name;
    private final boolean daemon;
    
    NewThreadAction(final ThreadGroup group, final Runnable runnable, final String name, final boolean daemon) {
        this.group = group;
        this.runnable = runnable;
        this.name = name;
        this.daemon = daemon;
    }
    
    public NewThreadAction(final Runnable runnable, final String s, final boolean b) {
        this(NewThreadAction.systemThreadGroup, runnable, s, b);
    }
    
    public NewThreadAction(final Runnable runnable, final String s, final boolean b, final boolean b2) {
        this(b2 ? NewThreadAction.userThreadGroup : NewThreadAction.systemThreadGroup, runnable, s, b);
    }
    
    @Override
    public Thread run() {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(SecurityConstants.GET_CLASSLOADER_PERMISSION);
        }
        final Thread thread = new Thread(this.group, this.runnable, "RMI " + this.name);
        thread.setContextClassLoader(ClassLoader.getSystemClassLoader());
        thread.setDaemon(this.daemon);
        return thread;
    }
    
    static {
        systemThreadGroup = AccessController.doPrivileged((PrivilegedAction<ThreadGroup>)new PrivilegedAction<ThreadGroup>() {
            @Override
            public ThreadGroup run() {
                ThreadGroup threadGroup;
                ThreadGroup parent;
                for (threadGroup = Thread.currentThread().getThreadGroup(); (parent = threadGroup.getParent()) != null; threadGroup = parent) {}
                return threadGroup;
            }
        });
        userThreadGroup = AccessController.doPrivileged((PrivilegedAction<ThreadGroup>)new PrivilegedAction<ThreadGroup>() {
            @Override
            public ThreadGroup run() {
                return new ThreadGroup(NewThreadAction.systemThreadGroup, "RMI Runtime");
            }
        });
    }
}
