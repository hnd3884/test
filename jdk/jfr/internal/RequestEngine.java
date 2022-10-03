package jdk.jfr.internal;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import java.util.Collection;
import java.util.ArrayList;
import jdk.jfr.Event;
import java.util.Iterator;
import java.util.Objects;
import java.security.AccessControlContext;
import java.util.List;

public final class RequestEngine
{
    private static final JVM jvm;
    private static final List<RequestHook> entries;
    private static long lastTimeMillis;
    
    public static void addHook(final AccessControlContext accessControlContext, final PlatformEventType platformEventType, final Runnable runnable) {
        Objects.requireNonNull(accessControlContext);
        addHookInternal(accessControlContext, platformEventType, runnable);
    }
    
    private static void addHookInternal(final AccessControlContext accessControlContext, final PlatformEventType platformEventType, final Runnable runnable) {
        final RequestHook requestHook = new RequestHook(accessControlContext, platformEventType, runnable);
        final Iterator<RequestHook> iterator = RequestEngine.entries.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().hook == runnable) {
                throw new IllegalArgumentException("Hook has already been added");
            }
        }
        requestHook.type.setEventHook(true);
        RequestEngine.entries.add(requestHook);
        logHook("Added", platformEventType);
    }
    
    public static void addTrustedJDKHook(final Class<? extends Event> clazz, final Runnable runnable) {
        if (clazz.getClassLoader() != null) {
            throw new SecurityException("Hook can only be registered for event classes that are loaded by the bootstrap class loader");
        }
        if (runnable.getClass().getClassLoader() != null) {
            throw new SecurityException("Runnable hook class must be loaded by the bootstrap class loader");
        }
        addHookInternal(null, PrivateAccess.getInstance().getPlatformEventType(MetadataRepository.getInstance().getEventType(clazz)), runnable);
    }
    
    private static void logHook(final String s, final PlatformEventType platformEventType) {
        if (platformEventType.isJDK() || platformEventType.isJVM()) {
            Logger.log(LogTag.JFR_SYSTEM_EVENT, LogLevel.INFO, s + " periodic hook for " + platformEventType.getLogName());
        }
        else {
            Logger.log(LogTag.JFR_EVENT, LogLevel.INFO, s + " periodic hook for " + platformEventType.getLogName());
        }
    }
    
    public static boolean removeHook(final Runnable runnable) {
        for (final RequestHook requestHook : RequestEngine.entries) {
            if (requestHook.hook == runnable) {
                RequestEngine.entries.remove(requestHook);
                requestHook.type.setEventHook(false);
                logHook("Removed", requestHook.type);
                return true;
            }
        }
        return false;
    }
    
    static void addHooks(final List<RequestHook> list) {
        final ArrayList list2 = new ArrayList();
        for (final RequestHook requestHook : list) {
            requestHook.type.setEventHook(true);
            list2.add(requestHook);
            logHook("Added", requestHook.type);
        }
        RequestEngine.entries.addAll(list);
    }
    
    static void doChunkEnd() {
        doChunk(platformEventType -> platformEventType.isEndChunk());
    }
    
    static void doChunkBegin() {
        doChunk(platformEventType -> platformEventType.isBeginChunk());
    }
    
    private static void doChunk(final Predicate<PlatformEventType> predicate) {
        for (final RequestHook requestHook : RequestEngine.entries) {
            final PlatformEventType access$200 = requestHook.type;
            if (access$200.isEnabled() && predicate.test(access$200)) {
                requestHook.execute();
            }
        }
    }
    
    static long doPeriodic() {
        return run_requests(RequestEngine.entries);
    }
    
    private static long run_requests(final Collection<RequestHook> collection) {
        long lastTimeMillis = RequestEngine.lastTimeMillis;
        final long currentTimeMillis = System.currentTimeMillis();
        long n = 0L;
        if (lastTimeMillis == 0L) {
            lastTimeMillis = currentTimeMillis;
        }
        final long n2 = currentTimeMillis - lastTimeMillis;
        if (n2 < 0L) {
            RequestEngine.lastTimeMillis = currentTimeMillis;
            return 0L;
        }
        for (final RequestHook requestHook : collection) {
            final PlatformEventType access$200 = requestHook.type;
            if (access$200.isEnabled()) {
                if (access$200.isEveryChunk()) {
                    continue;
                }
                final long period = access$200.getPeriod();
                long n3 = requestHook.delta + n2;
                if (n3 >= period) {
                    n3 = 0L;
                    requestHook.execute();
                }
                long n4 = period - n3;
                if (n4 < 0L) {
                    n4 = 0L;
                }
                requestHook.delta = n3;
                if (n != 0L && n4 >= n) {
                    continue;
                }
                n = n4;
            }
        }
        RequestEngine.lastTimeMillis = currentTimeMillis;
        return n;
    }
    
    static {
        jvm = JVM.getJVM();
        entries = new CopyOnWriteArrayList<RequestHook>();
    }
    
    static final class RequestHook
    {
        private final Runnable hook;
        private final PlatformEventType type;
        private final AccessControlContext accessControllerContext;
        private long delta;
        
        private RequestHook(final AccessControlContext accessControllerContext, final PlatformEventType type, final Runnable hook) {
            this.hook = hook;
            this.type = type;
            this.accessControllerContext = accessControllerContext;
        }
        
        RequestHook(final PlatformEventType platformEventType) {
            this(null, platformEventType, null);
        }
        
        private void execute() {
            try {
                if (this.accessControllerContext == null) {
                    if (this.type.isJDK()) {
                        this.hook.run();
                    }
                    else {
                        RequestEngine.jvm.emitEvent(this.type.getId(), JVM.counterTime(), 0L);
                    }
                    Logger.log(LogTag.JFR_SYSTEM_EVENT, LogLevel.DEBUG, () -> "Executed periodic hook for " + this.type.getLogName());
                }
                else {
                    this.executeSecure();
                }
            }
            catch (final Throwable t) {
                Logger.log(LogTag.JFR_SYSTEM_EVENT, LogLevel.WARN, "Exception occured during execution of period hook for " + this.type.getLogName());
            }
        }
        
        private void executeSecure() {
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
                @Override
                public Void run() {
                    try {
                        RequestHook.this.hook.run();
                        Logger.log(LogTag.JFR_EVENT, LogLevel.DEBUG, () -> "Executed periodic hook for " + RequestHook.this.type.getLogName());
                    }
                    catch (final Throwable t) {
                        Logger.log(LogTag.JFR_EVENT, LogLevel.WARN, "Exception occured during execution of period hook for " + RequestHook.this.type.getLogName());
                    }
                    return null;
                }
            }, this.accessControllerContext);
        }
    }
}
