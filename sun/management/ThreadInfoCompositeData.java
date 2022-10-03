package sun.management;

import javax.management.openmbean.OpenType;
import java.lang.reflect.Type;
import java.lang.management.MonitorInfo;
import java.lang.management.LockInfo;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.CompositeData;
import java.lang.management.ThreadInfo;

public class ThreadInfoCompositeData extends LazyCompositeData
{
    private final ThreadInfo threadInfo;
    private final CompositeData cdata;
    private final boolean currentVersion;
    private static final String THREAD_ID = "threadId";
    private static final String THREAD_NAME = "threadName";
    private static final String THREAD_STATE = "threadState";
    private static final String BLOCKED_TIME = "blockedTime";
    private static final String BLOCKED_COUNT = "blockedCount";
    private static final String WAITED_TIME = "waitedTime";
    private static final String WAITED_COUNT = "waitedCount";
    private static final String LOCK_INFO = "lockInfo";
    private static final String LOCK_NAME = "lockName";
    private static final String LOCK_OWNER_ID = "lockOwnerId";
    private static final String LOCK_OWNER_NAME = "lockOwnerName";
    private static final String STACK_TRACE = "stackTrace";
    private static final String SUSPENDED = "suspended";
    private static final String IN_NATIVE = "inNative";
    private static final String LOCKED_MONITORS = "lockedMonitors";
    private static final String LOCKED_SYNCS = "lockedSynchronizers";
    private static final String[] threadInfoItemNames;
    private static final String[] threadInfoV6Attributes;
    private static final CompositeType threadInfoCompositeType;
    private static final CompositeType threadInfoV5CompositeType;
    private static final CompositeType lockInfoCompositeType;
    private static final long serialVersionUID = 2464378539119753175L;
    
    private ThreadInfoCompositeData(final ThreadInfo threadInfo) {
        this.threadInfo = threadInfo;
        this.currentVersion = true;
        this.cdata = null;
    }
    
    private ThreadInfoCompositeData(final CompositeData cdata) {
        this.threadInfo = null;
        this.currentVersion = isCurrentVersion(cdata);
        this.cdata = cdata;
    }
    
    public ThreadInfo getThreadInfo() {
        return this.threadInfo;
    }
    
    public boolean isCurrentVersion() {
        return this.currentVersion;
    }
    
    public static ThreadInfoCompositeData getInstance(final CompositeData compositeData) {
        validateCompositeData(compositeData);
        return new ThreadInfoCompositeData(compositeData);
    }
    
    public static CompositeData toCompositeData(final ThreadInfo threadInfo) {
        return new ThreadInfoCompositeData(threadInfo).getCompositeData();
    }
    
    @Override
    protected CompositeData getCompositeData() {
        final StackTraceElement[] stackTrace = this.threadInfo.getStackTrace();
        final CompositeData[] array = new CompositeData[stackTrace.length];
        for (int i = 0; i < stackTrace.length; ++i) {
            array[i] = StackTraceElementCompositeData.toCompositeData(stackTrace[i]);
        }
        final CompositeData compositeData = LockInfoCompositeData.toCompositeData(this.threadInfo.getLockInfo());
        final LockInfo[] lockedSynchronizers = this.threadInfo.getLockedSynchronizers();
        final CompositeData[] array2 = new CompositeData[lockedSynchronizers.length];
        for (int j = 0; j < lockedSynchronizers.length; ++j) {
            array2[j] = LockInfoCompositeData.toCompositeData(lockedSynchronizers[j]);
        }
        final MonitorInfo[] lockedMonitors = this.threadInfo.getLockedMonitors();
        final CompositeData[] array3 = new CompositeData[lockedMonitors.length];
        for (int k = 0; k < lockedMonitors.length; ++k) {
            array3[k] = MonitorInfoCompositeData.toCompositeData(lockedMonitors[k]);
        }
        final Object[] array4 = { new Long(this.threadInfo.getThreadId()), this.threadInfo.getThreadName(), this.threadInfo.getThreadState().name(), new Long(this.threadInfo.getBlockedTime()), new Long(this.threadInfo.getBlockedCount()), new Long(this.threadInfo.getWaitedTime()), new Long(this.threadInfo.getWaitedCount()), compositeData, this.threadInfo.getLockName(), new Long(this.threadInfo.getLockOwnerId()), this.threadInfo.getLockOwnerName(), array, new Boolean(this.threadInfo.isSuspended()), new Boolean(this.threadInfo.isInNative()), array3, array2 };
        try {
            return new CompositeDataSupport(ThreadInfoCompositeData.threadInfoCompositeType, ThreadInfoCompositeData.threadInfoItemNames, array4);
        }
        catch (final OpenDataException ex) {
            throw new AssertionError((Object)ex);
        }
    }
    
    private static boolean isV5Attribute(final String s) {
        final String[] threadInfoV6Attributes = ThreadInfoCompositeData.threadInfoV6Attributes;
        for (int length = threadInfoV6Attributes.length, i = 0; i < length; ++i) {
            if (s.equals(threadInfoV6Attributes[i])) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isCurrentVersion(final CompositeData compositeData) {
        if (compositeData == null) {
            throw new NullPointerException("Null CompositeData");
        }
        return LazyCompositeData.isTypeMatched(ThreadInfoCompositeData.threadInfoCompositeType, compositeData.getCompositeType());
    }
    
    public long threadId() {
        return LazyCompositeData.getLong(this.cdata, "threadId");
    }
    
    public String threadName() {
        final String string = LazyCompositeData.getString(this.cdata, "threadName");
        if (string == null) {
            throw new IllegalArgumentException("Invalid composite data: Attribute threadName has null value");
        }
        return string;
    }
    
    public Thread.State threadState() {
        return Thread.State.valueOf(LazyCompositeData.getString(this.cdata, "threadState"));
    }
    
    public long blockedTime() {
        return LazyCompositeData.getLong(this.cdata, "blockedTime");
    }
    
    public long blockedCount() {
        return LazyCompositeData.getLong(this.cdata, "blockedCount");
    }
    
    public long waitedTime() {
        return LazyCompositeData.getLong(this.cdata, "waitedTime");
    }
    
    public long waitedCount() {
        return LazyCompositeData.getLong(this.cdata, "waitedCount");
    }
    
    public String lockName() {
        return LazyCompositeData.getString(this.cdata, "lockName");
    }
    
    public long lockOwnerId() {
        return LazyCompositeData.getLong(this.cdata, "lockOwnerId");
    }
    
    public String lockOwnerName() {
        return LazyCompositeData.getString(this.cdata, "lockOwnerName");
    }
    
    public boolean suspended() {
        return LazyCompositeData.getBoolean(this.cdata, "suspended");
    }
    
    public boolean inNative() {
        return LazyCompositeData.getBoolean(this.cdata, "inNative");
    }
    
    public StackTraceElement[] stackTrace() {
        final CompositeData[] array = (CompositeData[])this.cdata.get("stackTrace");
        final StackTraceElement[] array2 = new StackTraceElement[array.length];
        for (int i = 0; i < array.length; ++i) {
            array2[i] = StackTraceElementCompositeData.from(array[i]);
        }
        return array2;
    }
    
    public LockInfo lockInfo() {
        return LockInfo.from((CompositeData)this.cdata.get("lockInfo"));
    }
    
    public MonitorInfo[] lockedMonitors() {
        final CompositeData[] array = (CompositeData[])this.cdata.get("lockedMonitors");
        final MonitorInfo[] array2 = new MonitorInfo[array.length];
        for (int i = 0; i < array.length; ++i) {
            array2[i] = MonitorInfo.from(array[i]);
        }
        return array2;
    }
    
    public LockInfo[] lockedSynchronizers() {
        final CompositeData[] array = (CompositeData[])this.cdata.get("lockedSynchronizers");
        final LockInfo[] array2 = new LockInfo[array.length];
        for (int i = 0; i < array.length; ++i) {
            array2[i] = LockInfo.from(array[i]);
        }
        return array2;
    }
    
    public static void validateCompositeData(final CompositeData compositeData) {
        if (compositeData == null) {
            throw new NullPointerException("Null CompositeData");
        }
        final CompositeType compositeType = compositeData.getCompositeType();
        boolean b = true;
        if (!LazyCompositeData.isTypeMatched(ThreadInfoCompositeData.threadInfoCompositeType, compositeType)) {
            b = false;
            if (!LazyCompositeData.isTypeMatched(ThreadInfoCompositeData.threadInfoV5CompositeType, compositeType)) {
                throw new IllegalArgumentException("Unexpected composite type for ThreadInfo");
            }
        }
        final CompositeData[] array = (CompositeData[])compositeData.get("stackTrace");
        if (array == null) {
            throw new IllegalArgumentException("StackTraceElement[] is missing");
        }
        if (array.length > 0) {
            StackTraceElementCompositeData.validateCompositeData(array[0]);
        }
        if (b) {
            final CompositeData compositeData2 = (CompositeData)compositeData.get("lockInfo");
            if (compositeData2 != null && !LazyCompositeData.isTypeMatched(ThreadInfoCompositeData.lockInfoCompositeType, compositeData2.getCompositeType())) {
                throw new IllegalArgumentException("Unexpected composite type for \"lockInfo\" attribute.");
            }
            final CompositeData[] array2 = (CompositeData[])compositeData.get("lockedMonitors");
            if (array2 == null) {
                throw new IllegalArgumentException("MonitorInfo[] is null");
            }
            if (array2.length > 0) {
                MonitorInfoCompositeData.validateCompositeData(array2[0]);
            }
            final CompositeData[] array3 = (CompositeData[])compositeData.get("lockedSynchronizers");
            if (array3 == null) {
                throw new IllegalArgumentException("LockInfo[] is null");
            }
            if (array3.length > 0 && !LazyCompositeData.isTypeMatched(ThreadInfoCompositeData.lockInfoCompositeType, array3[0].getCompositeType())) {
                throw new IllegalArgumentException("Unexpected composite type for \"lockedSynchronizers\" attribute.");
            }
        }
    }
    
    static {
        threadInfoItemNames = new String[] { "threadId", "threadName", "threadState", "blockedTime", "blockedCount", "waitedTime", "waitedCount", "lockInfo", "lockName", "lockOwnerId", "lockOwnerName", "stackTrace", "suspended", "inNative", "lockedMonitors", "lockedSynchronizers" };
        threadInfoV6Attributes = new String[] { "lockInfo", "lockedMonitors", "lockedSynchronizers" };
        try {
            threadInfoCompositeType = (CompositeType)MappedMXBeanType.toOpenType(ThreadInfo.class);
            final String[] array = ThreadInfoCompositeData.threadInfoCompositeType.keySet().toArray(new String[0]);
            final int n = ThreadInfoCompositeData.threadInfoItemNames.length - ThreadInfoCompositeData.threadInfoV6Attributes.length;
            final String[] array2 = new String[n];
            final String[] array3 = new String[n];
            final OpenType[] array4 = new OpenType[n];
            int n2 = 0;
            for (final String s : array) {
                if (isV5Attribute(s)) {
                    array2[n2] = s;
                    array3[n2] = ThreadInfoCompositeData.threadInfoCompositeType.getDescription(s);
                    array4[n2] = ThreadInfoCompositeData.threadInfoCompositeType.getType(s);
                    ++n2;
                }
            }
            threadInfoV5CompositeType = new CompositeType("java.lang.management.ThreadInfo", "J2SE 5.0 java.lang.management.ThreadInfo", array2, array3, array4);
        }
        catch (final OpenDataException ex) {
            throw new AssertionError((Object)ex);
        }
        final Object o = new Object();
        lockInfoCompositeType = LockInfoCompositeData.toCompositeData(new LockInfo(o.getClass().getName(), System.identityHashCode(o))).getCompositeType();
    }
}
