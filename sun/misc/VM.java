package sun.misc;

import java.util.Hashtable;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

public class VM
{
    private static boolean suspended;
    @Deprecated
    public static final int STATE_GREEN = 1;
    @Deprecated
    public static final int STATE_YELLOW = 2;
    @Deprecated
    public static final int STATE_RED = 3;
    private static volatile boolean booted;
    private static final Object lock;
    private static long directMemory;
    private static boolean pageAlignDirectMemory;
    private static boolean defaultAllowArraySyntax;
    private static boolean allowArraySyntax;
    private static final Properties savedProps;
    private static volatile int finalRefCount;
    private static volatile int peakFinalRefCount;
    private static final int JVMTI_THREAD_STATE_ALIVE = 1;
    private static final int JVMTI_THREAD_STATE_TERMINATED = 2;
    private static final int JVMTI_THREAD_STATE_RUNNABLE = 4;
    private static final int JVMTI_THREAD_STATE_BLOCKED_ON_MONITOR_ENTER = 1024;
    private static final int JVMTI_THREAD_STATE_WAITING_INDEFINITELY = 16;
    private static final int JVMTI_THREAD_STATE_WAITING_WITH_TIMEOUT = 32;
    
    @Deprecated
    public static boolean threadsSuspended() {
        return VM.suspended;
    }
    
    public static boolean allowThreadSuspension(final ThreadGroup threadGroup, final boolean b) {
        return threadGroup.allowThreadSuspension(b);
    }
    
    @Deprecated
    public static boolean suspendThreads() {
        return VM.suspended = true;
    }
    
    @Deprecated
    public static void unsuspendThreads() {
        VM.suspended = false;
    }
    
    @Deprecated
    public static void unsuspendSomeThreads() {
    }
    
    @Deprecated
    public static final int getState() {
        return 1;
    }
    
    @Deprecated
    public static void registerVMNotification(final VMNotification vmNotification) {
    }
    
    @Deprecated
    public static void asChange(final int n, final int n2) {
    }
    
    @Deprecated
    public static void asChange_otherthread(final int n, final int n2) {
    }
    
    public static void booted() {
        synchronized (VM.lock) {
            VM.booted = true;
            VM.lock.notifyAll();
        }
    }
    
    public static boolean isBooted() {
        return VM.booted;
    }
    
    public static void awaitBooted() throws InterruptedException {
        synchronized (VM.lock) {
            while (!VM.booted) {
                VM.lock.wait();
            }
        }
    }
    
    public static long maxDirectMemory() {
        return VM.directMemory;
    }
    
    public static boolean isDirectMemoryPageAligned() {
        return VM.pageAlignDirectMemory;
    }
    
    public static boolean allowArraySyntax() {
        return VM.allowArraySyntax;
    }
    
    public static boolean isSystemDomainLoader(final ClassLoader classLoader) {
        return classLoader == null;
    }
    
    public static String getSavedProperty(final String s) {
        if (VM.savedProps.isEmpty()) {
            throw new IllegalStateException("Should be non-empty if initialized");
        }
        return VM.savedProps.getProperty(s);
    }
    
    public static void saveAndRemoveProperties(final Properties properties) {
        if (VM.booted) {
            throw new IllegalStateException("System initialization has completed");
        }
        VM.savedProps.putAll(properties);
        final String s = ((Hashtable<K, String>)properties).remove("sun.nio.MaxDirectMemorySize");
        if (s != null) {
            if (s.equals("-1")) {
                VM.directMemory = Runtime.getRuntime().maxMemory();
            }
            else {
                final long long1 = Long.parseLong(s);
                if (long1 > -1L) {
                    VM.directMemory = long1;
                }
            }
        }
        if ("true".equals(((Hashtable<K, String>)properties).remove("sun.nio.PageAlignDirectMemory"))) {
            VM.pageAlignDirectMemory = true;
        }
        final String property = properties.getProperty("sun.lang.ClassLoader.allowArraySyntax");
        VM.allowArraySyntax = ((property == null) ? VM.defaultAllowArraySyntax : Boolean.parseBoolean(property));
        properties.remove("java.lang.Integer.IntegerCache.high");
        properties.remove("sun.zip.disableMemoryMapping");
        properties.remove("sun.java.launcher.diag");
        properties.remove("sun.cds.enableSharedLookupCache");
        properties.remove("org.openjsse.provider");
        properties.remove("org.legacy8ujsse.provider");
    }
    
    public static void initializeOSEnvironment() {
        if (!VM.booted) {
            OSEnvironment.initialize();
        }
    }
    
    public static int getFinalRefCount() {
        return VM.finalRefCount;
    }
    
    public static int getPeakFinalRefCount() {
        return VM.peakFinalRefCount;
    }
    
    public static void addFinalRefCount(final int n) {
        VM.finalRefCount += n;
        if (VM.finalRefCount > VM.peakFinalRefCount) {
            VM.peakFinalRefCount = VM.finalRefCount;
        }
    }
    
    public static Thread.State toThreadState(final int n) {
        if ((n & 0x4) != 0x0) {
            return Thread.State.RUNNABLE;
        }
        if ((n & 0x400) != 0x0) {
            return Thread.State.BLOCKED;
        }
        if ((n & 0x10) != 0x0) {
            return Thread.State.WAITING;
        }
        if ((n & 0x20) != 0x0) {
            return Thread.State.TIMED_WAITING;
        }
        if ((n & 0x2) != 0x0) {
            return Thread.State.TERMINATED;
        }
        if ((n & 0x1) == 0x0) {
            return Thread.State.NEW;
        }
        return Thread.State.RUNNABLE;
    }
    
    public static native ClassLoader latestUserDefinedLoader0();
    
    public static ClassLoader latestUserDefinedLoader() {
        final ClassLoader latestUserDefinedLoader0 = latestUserDefinedLoader0();
        if (latestUserDefinedLoader0 != null) {
            return latestUserDefinedLoader0;
        }
        try {
            return Launcher.ExtClassLoader.getExtClassLoader();
        }
        catch (final IOException ex) {
            return null;
        }
    }
    
    private static native void initialize();
    
    static {
        VM.suspended = false;
        VM.booted = false;
        lock = new Object();
        VM.directMemory = 67108864L;
        VM.defaultAllowArraySyntax = false;
        VM.allowArraySyntax = VM.defaultAllowArraySyntax;
        savedProps = new Properties();
        VM.finalRefCount = 0;
        VM.peakFinalRefCount = 0;
        initialize();
    }
}
