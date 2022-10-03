package java.lang.management;

import sun.management.ThreadInfoCompositeData;
import javax.management.openmbean.CompositeData;
import sun.management.ManagementFactoryHelper;

public class ThreadInfo
{
    private String threadName;
    private long threadId;
    private long blockedTime;
    private long blockedCount;
    private long waitedTime;
    private long waitedCount;
    private LockInfo lock;
    private String lockName;
    private long lockOwnerId;
    private String lockOwnerName;
    private boolean inNative;
    private boolean suspended;
    private Thread.State threadState;
    private StackTraceElement[] stackTrace;
    private MonitorInfo[] lockedMonitors;
    private LockInfo[] lockedSynchronizers;
    private static MonitorInfo[] EMPTY_MONITORS;
    private static LockInfo[] EMPTY_SYNCS;
    private static final int MAX_FRAMES = 8;
    private static final StackTraceElement[] NO_STACK_TRACE;
    
    private ThreadInfo(final Thread thread, final int n, final Object o, final Thread thread2, final long n2, final long n3, final long n4, final long n5, final StackTraceElement[] array) {
        this.initialize(thread, n, o, thread2, n2, n3, n4, n5, array, ThreadInfo.EMPTY_MONITORS, ThreadInfo.EMPTY_SYNCS);
    }
    
    private ThreadInfo(final Thread thread, final int n, final Object o, final Thread thread2, final long n2, final long n3, final long n4, final long n5, final StackTraceElement[] array, final Object[] array2, final int[] array3, final Object[] array4) {
        final int n6 = (array2 == null) ? 0 : array2.length;
        MonitorInfo[] empty_MONITORS;
        if (n6 == 0) {
            empty_MONITORS = ThreadInfo.EMPTY_MONITORS;
        }
        else {
            empty_MONITORS = new MonitorInfo[n6];
            for (int i = 0; i < n6; ++i) {
                final Object o2 = array2[i];
                final String name = o2.getClass().getName();
                final int identityHashCode = System.identityHashCode(o2);
                final int n7 = array3[i];
                empty_MONITORS[i] = new MonitorInfo(name, identityHashCode, n7, (n7 >= 0) ? array[n7] : null);
            }
        }
        final int n8 = (array4 == null) ? 0 : array4.length;
        LockInfo[] empty_SYNCS;
        if (n8 == 0) {
            empty_SYNCS = ThreadInfo.EMPTY_SYNCS;
        }
        else {
            empty_SYNCS = new LockInfo[n8];
            for (int j = 0; j < n8; ++j) {
                final Object o3 = array4[j];
                empty_SYNCS[j] = new LockInfo(o3.getClass().getName(), System.identityHashCode(o3));
            }
        }
        this.initialize(thread, n, o, thread2, n2, n3, n4, n5, array, empty_MONITORS, empty_SYNCS);
    }
    
    private void initialize(final Thread thread, final int n, final Object o, final Thread thread2, final long blockedCount, final long blockedTime, final long waitedCount, final long waitedTime, final StackTraceElement[] stackTrace, final MonitorInfo[] lockedMonitors, final LockInfo[] lockedSynchronizers) {
        this.threadId = thread.getId();
        this.threadName = thread.getName();
        this.threadState = ManagementFactoryHelper.toThreadState(n);
        this.suspended = ManagementFactoryHelper.isThreadSuspended(n);
        this.inNative = ManagementFactoryHelper.isThreadRunningNative(n);
        this.blockedCount = blockedCount;
        this.blockedTime = blockedTime;
        this.waitedCount = waitedCount;
        this.waitedTime = waitedTime;
        if (o == null) {
            this.lock = null;
            this.lockName = null;
        }
        else {
            this.lock = new LockInfo(o);
            this.lockName = this.lock.getClassName() + '@' + Integer.toHexString(this.lock.getIdentityHashCode());
        }
        if (thread2 == null) {
            this.lockOwnerId = -1L;
            this.lockOwnerName = null;
        }
        else {
            this.lockOwnerId = thread2.getId();
            this.lockOwnerName = thread2.getName();
        }
        if (stackTrace == null) {
            this.stackTrace = ThreadInfo.NO_STACK_TRACE;
        }
        else {
            this.stackTrace = stackTrace;
        }
        this.lockedMonitors = lockedMonitors;
        this.lockedSynchronizers = lockedSynchronizers;
    }
    
    private ThreadInfo(final CompositeData compositeData) {
        final ThreadInfoCompositeData instance = ThreadInfoCompositeData.getInstance(compositeData);
        this.threadId = instance.threadId();
        this.threadName = instance.threadName();
        this.blockedTime = instance.blockedTime();
        this.blockedCount = instance.blockedCount();
        this.waitedTime = instance.waitedTime();
        this.waitedCount = instance.waitedCount();
        this.lockName = instance.lockName();
        this.lockOwnerId = instance.lockOwnerId();
        this.lockOwnerName = instance.lockOwnerName();
        this.threadState = instance.threadState();
        this.suspended = instance.suspended();
        this.inNative = instance.inNative();
        this.stackTrace = instance.stackTrace();
        if (instance.isCurrentVersion()) {
            this.lock = instance.lockInfo();
            this.lockedMonitors = instance.lockedMonitors();
            this.lockedSynchronizers = instance.lockedSynchronizers();
        }
        else {
            if (this.lockName != null) {
                final String[] split = this.lockName.split("@");
                if (split.length == 2) {
                    this.lock = new LockInfo(split[0], Integer.parseInt(split[1], 16));
                }
                else {
                    assert split.length == 2;
                    this.lock = null;
                }
            }
            else {
                this.lock = null;
            }
            this.lockedMonitors = ThreadInfo.EMPTY_MONITORS;
            this.lockedSynchronizers = ThreadInfo.EMPTY_SYNCS;
        }
    }
    
    public long getThreadId() {
        return this.threadId;
    }
    
    public String getThreadName() {
        return this.threadName;
    }
    
    public Thread.State getThreadState() {
        return this.threadState;
    }
    
    public long getBlockedTime() {
        return this.blockedTime;
    }
    
    public long getBlockedCount() {
        return this.blockedCount;
    }
    
    public long getWaitedTime() {
        return this.waitedTime;
    }
    
    public long getWaitedCount() {
        return this.waitedCount;
    }
    
    public LockInfo getLockInfo() {
        return this.lock;
    }
    
    public String getLockName() {
        return this.lockName;
    }
    
    public long getLockOwnerId() {
        return this.lockOwnerId;
    }
    
    public String getLockOwnerName() {
        return this.lockOwnerName;
    }
    
    public StackTraceElement[] getStackTrace() {
        return this.stackTrace;
    }
    
    public boolean isSuspended() {
        return this.suspended;
    }
    
    public boolean isInNative() {
        return this.inNative;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("\"" + this.getThreadName() + "\" Id=" + this.getThreadId() + " " + this.getThreadState());
        if (this.getLockName() != null) {
            sb.append(" on " + this.getLockName());
        }
        if (this.getLockOwnerName() != null) {
            sb.append(" owned by \"" + this.getLockOwnerName() + "\" Id=" + this.getLockOwnerId());
        }
        if (this.isSuspended()) {
            sb.append(" (suspended)");
        }
        if (this.isInNative()) {
            sb.append(" (in native)");
        }
        sb.append('\n');
        int n;
        for (n = 0; n < this.stackTrace.length && n < 8; ++n) {
            sb.append("\tat " + this.stackTrace[n].toString());
            sb.append('\n');
            if (n == 0 && this.getLockInfo() != null) {
                switch (this.getThreadState()) {
                    case BLOCKED: {
                        sb.append("\t-  blocked on " + this.getLockInfo());
                        sb.append('\n');
                        break;
                    }
                    case WAITING: {
                        sb.append("\t-  waiting on " + this.getLockInfo());
                        sb.append('\n');
                        break;
                    }
                    case TIMED_WAITING: {
                        sb.append("\t-  waiting on " + this.getLockInfo());
                        sb.append('\n');
                        break;
                    }
                }
            }
            for (final MonitorInfo monitorInfo : this.lockedMonitors) {
                if (monitorInfo.getLockedStackDepth() == n) {
                    sb.append("\t-  locked " + monitorInfo);
                    sb.append('\n');
                }
            }
        }
        if (n < this.stackTrace.length) {
            sb.append("\t...");
            sb.append('\n');
        }
        final LockInfo[] lockedSynchronizers = this.getLockedSynchronizers();
        if (lockedSynchronizers.length > 0) {
            sb.append("\n\tNumber of locked synchronizers = " + lockedSynchronizers.length);
            sb.append('\n');
            final LockInfo[] array = lockedSynchronizers;
            for (int length2 = array.length, j = 0; j < length2; ++j) {
                sb.append("\t- " + array[j]);
                sb.append('\n');
            }
        }
        sb.append('\n');
        return sb.toString();
    }
    
    public static ThreadInfo from(final CompositeData compositeData) {
        if (compositeData == null) {
            return null;
        }
        if (compositeData instanceof ThreadInfoCompositeData) {
            return ((ThreadInfoCompositeData)compositeData).getThreadInfo();
        }
        return new ThreadInfo(compositeData);
    }
    
    public MonitorInfo[] getLockedMonitors() {
        return this.lockedMonitors;
    }
    
    public LockInfo[] getLockedSynchronizers() {
        return this.lockedSynchronizers;
    }
    
    static {
        ThreadInfo.EMPTY_MONITORS = new MonitorInfo[0];
        ThreadInfo.EMPTY_SYNCS = new LockInfo[0];
        NO_STACK_TRACE = new StackTraceElement[0];
    }
}
