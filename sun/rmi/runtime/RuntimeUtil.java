package sun.rmi.runtime;

import sun.security.action.GetIntegerAction;
import java.util.logging.Level;
import java.security.PrivilegedAction;
import java.security.AccessController;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.security.Permission;

public final class RuntimeUtil
{
    private static final Log runtimeLog;
    private static final int schedulerThreads;
    private static final Permission GET_INSTANCE_PERMISSION;
    private static final RuntimeUtil instance;
    private final ScheduledThreadPoolExecutor scheduler;
    
    private RuntimeUtil() {
        this.scheduler = new ScheduledThreadPoolExecutor(RuntimeUtil.schedulerThreads, new ThreadFactory() {
            private final AtomicInteger count = new AtomicInteger(0);
            
            @Override
            public Thread newThread(final Runnable runnable) {
                try {
                    return AccessController.doPrivileged((PrivilegedAction<Thread>)new NewThreadAction(runnable, "Scheduler(" + this.count.getAndIncrement() + ")", true));
                }
                catch (final Throwable t) {
                    RuntimeUtil.runtimeLog.log(Level.WARNING, "scheduler thread factory throws", t);
                    return null;
                }
            }
        });
    }
    
    private static RuntimeUtil getInstance() {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(RuntimeUtil.GET_INSTANCE_PERMISSION);
        }
        return RuntimeUtil.instance;
    }
    
    public ScheduledThreadPoolExecutor getScheduler() {
        return this.scheduler;
    }
    
    static {
        runtimeLog = Log.getLog("sun.rmi.runtime", null, false);
        schedulerThreads = AccessController.doPrivileged((PrivilegedAction<Integer>)new GetIntegerAction("sun.rmi.runtime.schedulerThreads", 1));
        GET_INSTANCE_PERMISSION = new RuntimePermission("sun.rmi.runtime.RuntimeUtil.getInstance");
        instance = new RuntimeUtil();
    }
    
    public static class GetInstanceAction implements PrivilegedAction<RuntimeUtil>
    {
        @Override
        public RuntimeUtil run() {
            return getInstance();
        }
    }
}
