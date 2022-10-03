package sun.awt;

import java.awt.EventQueue;
import sun.misc.SharedSecrets;
import sun.misc.JavaAWTAccess;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.lang.ref.SoftReference;
import java.util.function.Supplier;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.awt.AWTEvent;
import java.awt.event.InvocationEvent;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.SystemTray;
import java.awt.GraphicsEnvironment;
import java.awt.Window;
import java.util.concurrent.locks.ReentrantLock;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.beans.PropertyChangeSupport;
import java.util.Map;
import sun.util.logging.PlatformLogger;

public final class AppContext
{
    private static final PlatformLogger log;
    public static final Object EVENT_QUEUE_KEY;
    public static final Object EVENT_QUEUE_LOCK_KEY;
    public static final Object EVENT_QUEUE_COND_KEY;
    private static final Map<ThreadGroup, AppContext> threadGroup2appContext;
    private static volatile AppContext mainAppContext;
    private static final Object getAppContextLock;
    private final Map<Object, Object> table;
    private final ThreadGroup threadGroup;
    private PropertyChangeSupport changeSupport;
    public static final String DISPOSED_PROPERTY_NAME = "disposed";
    public static final String GUI_DISPOSED = "guidisposed";
    private volatile State state;
    private static final AtomicInteger numAppContexts;
    private final ClassLoader contextClassLoader;
    private static final ThreadLocal<AppContext> threadAppContext;
    private long DISPOSAL_TIMEOUT;
    private long THREAD_INTERRUPT_TIMEOUT;
    private MostRecentKeyValue mostRecentKeyValue;
    private MostRecentKeyValue shadowMostRecentKeyValue;
    
    public static Set<AppContext> getAppContexts() {
        synchronized (AppContext.threadGroup2appContext) {
            return new HashSet<AppContext>(AppContext.threadGroup2appContext.values());
        }
    }
    
    public boolean isDisposed() {
        return this.state == State.DISPOSED;
    }
    
    AppContext(final ThreadGroup threadGroup) {
        this.table = new HashMap<Object, Object>();
        this.changeSupport = null;
        this.state = State.VALID;
        this.DISPOSAL_TIMEOUT = 5000L;
        this.THREAD_INTERRUPT_TIMEOUT = 1000L;
        this.mostRecentKeyValue = null;
        this.shadowMostRecentKeyValue = null;
        AppContext.numAppContexts.incrementAndGet();
        this.threadGroup = threadGroup;
        AppContext.threadGroup2appContext.put(threadGroup, this);
        this.contextClassLoader = AccessController.doPrivileged((PrivilegedAction<ClassLoader>)new PrivilegedAction<ClassLoader>() {
            @Override
            public ClassLoader run() {
                return Thread.currentThread().getContextClassLoader();
            }
        });
        final ReentrantLock reentrantLock = new ReentrantLock();
        this.put(AppContext.EVENT_QUEUE_LOCK_KEY, reentrantLock);
        this.put(AppContext.EVENT_QUEUE_COND_KEY, reentrantLock.newCondition());
    }
    
    private static final void initMainAppContext() {
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
                for (ThreadGroup threadGroup2 = threadGroup.getParent(); threadGroup2 != null; threadGroup2 = threadGroup.getParent()) {
                    threadGroup = threadGroup2;
                }
                AppContext.mainAppContext = SunToolkit.createNewAppContext(threadGroup);
                return null;
            }
        });
    }
    
    public static final AppContext getAppContext() {
        if (AppContext.numAppContexts.get() == 1 && AppContext.mainAppContext != null) {
            return AppContext.mainAppContext;
        }
        AppContext appContext = AppContext.threadAppContext.get();
        if (null == appContext) {
            appContext = AccessController.doPrivileged((PrivilegedAction<AppContext>)new PrivilegedAction<AppContext>() {
                @Override
                public AppContext run() {
                    ThreadGroup threadGroup2;
                    final ThreadGroup threadGroup = threadGroup2 = Thread.currentThread().getThreadGroup();
                    synchronized (AppContext.getAppContextLock) {
                        if (AppContext.numAppContexts.get() == 0) {
                            if (System.getProperty("javaplugin.version") == null && System.getProperty("javawebstart.version") == null) {
                                initMainAppContext();
                            }
                            else if (System.getProperty("javafx.version") != null && threadGroup2.getParent() != null) {
                                SunToolkit.createNewAppContext();
                            }
                        }
                    }
                    AppContext appContext;
                    for (appContext = AppContext.threadGroup2appContext.get(threadGroup2); appContext == null; appContext = AppContext.threadGroup2appContext.get(threadGroup2)) {
                        threadGroup2 = threadGroup2.getParent();
                        if (threadGroup2 == null) {
                            final SecurityManager securityManager = System.getSecurityManager();
                            if (securityManager != null) {
                                final ThreadGroup threadGroup3 = securityManager.getThreadGroup();
                                if (threadGroup3 != null) {
                                    return (AppContext)AppContext.threadGroup2appContext.get(threadGroup3);
                                }
                            }
                            return null;
                        }
                    }
                    for (ThreadGroup parent = threadGroup; parent != threadGroup2; parent = parent.getParent()) {
                        AppContext.threadGroup2appContext.put(parent, appContext);
                    }
                    AppContext.threadAppContext.set(appContext);
                    return appContext;
                }
            });
        }
        return appContext;
    }
    
    public static final boolean isMainContext(final AppContext appContext) {
        return appContext != null && appContext == AppContext.mainAppContext;
    }
    
    private static final AppContext getExecutionAppContext() {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null && securityManager instanceof AWTSecurityManager) {
            return ((AWTSecurityManager)securityManager).getAppContext();
        }
        return null;
    }
    
    public void dispose() throws IllegalThreadStateException {
        if (this.threadGroup.parentOf(Thread.currentThread().getThreadGroup())) {
            throw new IllegalThreadStateException("Current Thread is contained within AppContext to be disposed.");
        }
        synchronized (this) {
            if (this.state != State.VALID) {
                return;
            }
            this.state = State.BEING_DISPOSED;
        }
        final PropertyChangeSupport changeSupport = this.changeSupport;
        if (changeSupport != null) {
            changeSupport.firePropertyChange("disposed", false, true);
        }
        final Object o = new Object();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                for (final Window window : Window.getOwnerlessWindows()) {
                    try {
                        window.dispose();
                    }
                    catch (final Throwable t) {
                        AppContext.log.finer("exception occurred while disposing app context", t);
                    }
                }
                AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
                    @Override
                    public Void run() {
                        if (!GraphicsEnvironment.isHeadless() && SystemTray.isSupported()) {
                            final SystemTray systemTray = SystemTray.getSystemTray();
                            final TrayIcon[] trayIcons = systemTray.getTrayIcons();
                            for (int length = trayIcons.length, i = 0; i < length; ++i) {
                                systemTray.remove(trayIcons[i]);
                            }
                        }
                        return null;
                    }
                });
                if (changeSupport != null) {
                    changeSupport.firePropertyChange("guidisposed", false, true);
                }
                synchronized (o) {
                    o.notifyAll();
                }
            }
        };
        synchronized (o) {
            SunToolkit.postEvent(this, new InvocationEvent(Toolkit.getDefaultToolkit(), runnable));
            try {
                o.wait(this.DISPOSAL_TIMEOUT);
            }
            catch (final InterruptedException ex) {}
        }
        final Runnable runnable2 = new Runnable() {
            @Override
            public void run() {
                synchronized (o) {
                    o.notifyAll();
                }
            }
        };
        synchronized (o) {
            SunToolkit.postEvent(this, new InvocationEvent(Toolkit.getDefaultToolkit(), runnable2));
            try {
                o.wait(this.DISPOSAL_TIMEOUT);
            }
            catch (final InterruptedException ex2) {}
        }
        synchronized (this) {
            this.state = State.DISPOSED;
        }
        this.threadGroup.interrupt();
        final long n = System.currentTimeMillis() + this.THREAD_INTERRUPT_TIMEOUT;
        while (this.threadGroup.activeCount() > 0 && System.currentTimeMillis() < n) {
            try {
                Thread.sleep(10L);
            }
            catch (final InterruptedException ex3) {}
        }
        this.threadGroup.stop();
        final long n2 = System.currentTimeMillis() + this.THREAD_INTERRUPT_TIMEOUT;
        while (this.threadGroup.activeCount() > 0 && System.currentTimeMillis() < n2) {
            try {
                Thread.sleep(10L);
            }
            catch (final InterruptedException ex4) {}
        }
        final int activeGroupCount = this.threadGroup.activeGroupCount();
        if (activeGroupCount > 0) {
            final ThreadGroup[] array = new ThreadGroup[activeGroupCount];
            for (int enumerate = this.threadGroup.enumerate(array), i = 0; i < enumerate; ++i) {
                AppContext.threadGroup2appContext.remove(array[i]);
            }
        }
        AppContext.threadGroup2appContext.remove(this.threadGroup);
        AppContext.threadAppContext.set(null);
        try {
            this.threadGroup.destroy();
        }
        catch (final IllegalThreadStateException ex5) {}
        synchronized (this.table) {
            this.table.clear();
        }
        AppContext.numAppContexts.decrementAndGet();
        this.mostRecentKeyValue = null;
    }
    
    static void stopEventDispatchThreads() {
        for (final AppContext appContext : getAppContexts()) {
            if (appContext.isDisposed()) {
                continue;
            }
            final PostShutdownEventRunnable postShutdownEventRunnable = new PostShutdownEventRunnable(appContext);
            if (appContext != getAppContext()) {
                AccessController.doPrivileged((PrivilegedAction<Thread>)new CreateThreadAction(appContext, postShutdownEventRunnable)).start();
            }
            else {
                postShutdownEventRunnable.run();
            }
        }
    }
    
    public Object get(final Object o) {
        synchronized (this.table) {
            final MostRecentKeyValue mostRecentKeyValue = this.mostRecentKeyValue;
            if (mostRecentKeyValue != null && mostRecentKeyValue.key == o) {
                return mostRecentKeyValue.value;
            }
            final Object value = this.table.get(o);
            if (this.mostRecentKeyValue == null) {
                this.mostRecentKeyValue = new MostRecentKeyValue(o, value);
                this.shadowMostRecentKeyValue = new MostRecentKeyValue(o, value);
            }
            else {
                final MostRecentKeyValue mostRecentKeyValue2 = this.mostRecentKeyValue;
                this.shadowMostRecentKeyValue.setPair(o, value);
                this.mostRecentKeyValue = this.shadowMostRecentKeyValue;
                this.shadowMostRecentKeyValue = mostRecentKeyValue2;
            }
            return value;
        }
    }
    
    public Object put(final Object o, final Object value) {
        synchronized (this.table) {
            final MostRecentKeyValue mostRecentKeyValue = this.mostRecentKeyValue;
            if (mostRecentKeyValue != null && mostRecentKeyValue.key == o) {
                mostRecentKeyValue.value = value;
            }
            return this.table.put(o, value);
        }
    }
    
    public Object remove(final Object o) {
        synchronized (this.table) {
            final MostRecentKeyValue mostRecentKeyValue = this.mostRecentKeyValue;
            if (mostRecentKeyValue != null && mostRecentKeyValue.key == o) {
                mostRecentKeyValue.value = null;
            }
            return this.table.remove(o);
        }
    }
    
    public ThreadGroup getThreadGroup() {
        return this.threadGroup;
    }
    
    public ClassLoader getContextClassLoader() {
        return this.contextClassLoader;
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + "[threadGroup=" + this.threadGroup.getName() + "]";
    }
    
    public synchronized PropertyChangeListener[] getPropertyChangeListeners() {
        if (this.changeSupport == null) {
            return new PropertyChangeListener[0];
        }
        return this.changeSupport.getPropertyChangeListeners();
    }
    
    public synchronized void addPropertyChangeListener(final String s, final PropertyChangeListener propertyChangeListener) {
        if (propertyChangeListener == null) {
            return;
        }
        if (this.changeSupport == null) {
            this.changeSupport = new PropertyChangeSupport(this);
        }
        this.changeSupport.addPropertyChangeListener(s, propertyChangeListener);
    }
    
    public synchronized void removePropertyChangeListener(final String s, final PropertyChangeListener propertyChangeListener) {
        if (propertyChangeListener == null || this.changeSupport == null) {
            return;
        }
        this.changeSupport.removePropertyChangeListener(s, propertyChangeListener);
    }
    
    public synchronized PropertyChangeListener[] getPropertyChangeListeners(final String s) {
        if (this.changeSupport == null) {
            return new PropertyChangeListener[0];
        }
        return this.changeSupport.getPropertyChangeListeners(s);
    }
    
    public static <T> T getSoftReferenceValue(final Object o, final Supplier<T> supplier) {
        final AppContext appContext = getAppContext();
        final SoftReference softReference = (SoftReference)appContext.get(o);
        if (softReference != null) {
            final Object value = softReference.get();
            if (value != null) {
                return (T)value;
            }
        }
        final T value2 = supplier.get();
        appContext.put(o, new SoftReference(value2));
        return value2;
    }
    
    static {
        log = PlatformLogger.getLogger("sun.awt.AppContext");
        EVENT_QUEUE_KEY = new StringBuffer("EventQueue");
        EVENT_QUEUE_LOCK_KEY = new StringBuilder("EventQueue.Lock");
        EVENT_QUEUE_COND_KEY = new StringBuilder("EventQueue.Condition");
        threadGroup2appContext = Collections.synchronizedMap(new IdentityHashMap<ThreadGroup, AppContext>());
        AppContext.mainAppContext = null;
        getAppContextLock = new GetAppContextLock();
        numAppContexts = new AtomicInteger(0);
        threadAppContext = new ThreadLocal<AppContext>();
        SharedSecrets.setJavaAWTAccess(new JavaAWTAccess() {
            private boolean hasRootThreadGroup(final AppContext appContext) {
                return AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction<Boolean>() {
                    @Override
                    public Boolean run() {
                        return appContext.threadGroup.getParent() == null;
                    }
                });
            }
            
            @Override
            public Object getAppletContext() {
                if (AppContext.numAppContexts.get() == 0) {
                    return null;
                }
                AppContext access$900 = getExecutionAppContext();
                if (AppContext.numAppContexts.get() > 0) {
                    access$900 = ((access$900 != null) ? access$900 : AppContext.getAppContext());
                }
                return (access$900 == null || AppContext.mainAppContext == access$900 || (AppContext.mainAppContext == null && this.hasRootThreadGroup(access$900))) ? null : access$900;
            }
        });
    }
    
    private static class GetAppContextLock
    {
    }
    
    private enum State
    {
        VALID, 
        BEING_DISPOSED, 
        DISPOSED;
    }
    
    static final class PostShutdownEventRunnable implements Runnable
    {
        private final AppContext appContext;
        
        public PostShutdownEventRunnable(final AppContext appContext) {
            this.appContext = appContext;
        }
        
        @Override
        public void run() {
            final EventQueue eventQueue = (EventQueue)this.appContext.get(AppContext.EVENT_QUEUE_KEY);
            if (eventQueue != null) {
                eventQueue.postEvent(AWTAutoShutdown.getShutdownEvent());
            }
        }
    }
    
    static final class CreateThreadAction implements PrivilegedAction<Thread>
    {
        private final AppContext appContext;
        private final Runnable runnable;
        
        public CreateThreadAction(final AppContext appContext, final Runnable runnable) {
            this.appContext = appContext;
            this.runnable = runnable;
        }
        
        @Override
        public Thread run() {
            final Thread thread = new Thread(this.appContext.getThreadGroup(), this.runnable);
            thread.setContextClassLoader(this.appContext.getContextClassLoader());
            thread.setPriority(6);
            thread.setDaemon(true);
            return thread;
        }
    }
}
