package sun.management;

import javax.management.ObjectName;
import java.util.Arrays;
import java.util.Objects;
import java.lang.management.ThreadInfo;
import com.sun.management.ThreadMXBean;

class ThreadImpl implements ThreadMXBean
{
    private final VMManagement jvm;
    private boolean contentionMonitoringEnabled;
    private boolean cpuTimeEnabled;
    private boolean allocatedMemoryEnabled;
    
    ThreadImpl(final VMManagement jvm) {
        this.contentionMonitoringEnabled = false;
        this.jvm = jvm;
        this.cpuTimeEnabled = this.jvm.isThreadCpuTimeEnabled();
        this.allocatedMemoryEnabled = this.jvm.isThreadAllocatedMemoryEnabled();
    }
    
    @Override
    public int getThreadCount() {
        return this.jvm.getLiveThreadCount();
    }
    
    @Override
    public int getPeakThreadCount() {
        return this.jvm.getPeakThreadCount();
    }
    
    @Override
    public long getTotalStartedThreadCount() {
        return this.jvm.getTotalThreadCount();
    }
    
    @Override
    public int getDaemonThreadCount() {
        return this.jvm.getDaemonThreadCount();
    }
    
    @Override
    public boolean isThreadContentionMonitoringSupported() {
        return this.jvm.isThreadContentionMonitoringSupported();
    }
    
    @Override
    public synchronized boolean isThreadContentionMonitoringEnabled() {
        if (!this.isThreadContentionMonitoringSupported()) {
            throw new UnsupportedOperationException("Thread contention monitoring is not supported.");
        }
        return this.contentionMonitoringEnabled;
    }
    
    @Override
    public boolean isThreadCpuTimeSupported() {
        return this.jvm.isOtherThreadCpuTimeSupported();
    }
    
    @Override
    public boolean isCurrentThreadCpuTimeSupported() {
        return this.jvm.isCurrentThreadCpuTimeSupported();
    }
    
    @Override
    public boolean isThreadAllocatedMemorySupported() {
        return this.jvm.isThreadAllocatedMemorySupported();
    }
    
    @Override
    public boolean isThreadCpuTimeEnabled() {
        if (!this.isThreadCpuTimeSupported() && !this.isCurrentThreadCpuTimeSupported()) {
            throw new UnsupportedOperationException("Thread CPU time measurement is not supported");
        }
        return this.cpuTimeEnabled;
    }
    
    private void ensureThreadAllocatedMemorySupported() {
        if (!this.isThreadAllocatedMemorySupported()) {
            throw new UnsupportedOperationException("Thread allocated memory measurement is not supported.");
        }
    }
    
    @Override
    public boolean isThreadAllocatedMemoryEnabled() {
        this.ensureThreadAllocatedMemorySupported();
        return this.allocatedMemoryEnabled;
    }
    
    @Override
    public long[] getAllThreadIds() {
        Util.checkMonitorAccess();
        final Thread[] threads = getThreads();
        final int length = threads.length;
        final long[] array = new long[length];
        for (int i = 0; i < length; ++i) {
            array[i] = threads[i].getId();
        }
        return array;
    }
    
    @Override
    public ThreadInfo getThreadInfo(final long n) {
        return this.getThreadInfo(new long[] { n }, 0)[0];
    }
    
    @Override
    public ThreadInfo getThreadInfo(final long n, final int n2) {
        return this.getThreadInfo(new long[] { n }, n2)[0];
    }
    
    @Override
    public ThreadInfo[] getThreadInfo(final long[] array) {
        return this.getThreadInfo(array, 0);
    }
    
    private void verifyThreadId(final long n) {
        if (n <= 0L) {
            throw new IllegalArgumentException("Invalid thread ID parameter: " + n);
        }
    }
    
    private void verifyThreadIds(final long[] array) {
        Objects.requireNonNull(array);
        for (int i = 0; i < array.length; ++i) {
            this.verifyThreadId(array[i]);
        }
    }
    
    @Override
    public ThreadInfo[] getThreadInfo(final long[] array, final int n) {
        this.verifyThreadIds(array);
        if (n < 0) {
            throw new IllegalArgumentException("Invalid maxDepth parameter: " + n);
        }
        if (array.length == 0) {
            return new ThreadInfo[0];
        }
        Util.checkMonitorAccess();
        final ThreadInfo[] array2 = new ThreadInfo[array.length];
        if (n == Integer.MAX_VALUE) {
            getThreadInfo1(array, -1, array2);
        }
        else {
            getThreadInfo1(array, n, array2);
        }
        return array2;
    }
    
    @Override
    public void setThreadContentionMonitoringEnabled(final boolean b) {
        if (!this.isThreadContentionMonitoringSupported()) {
            throw new UnsupportedOperationException("Thread contention monitoring is not supported");
        }
        Util.checkControlAccess();
        synchronized (this) {
            if (this.contentionMonitoringEnabled != b) {
                if (b) {
                    resetContentionTimes0(0L);
                }
                setThreadContentionMonitoringEnabled0(b);
                this.contentionMonitoringEnabled = b;
            }
        }
    }
    
    private boolean verifyCurrentThreadCpuTime() {
        if (!this.isCurrentThreadCpuTimeSupported()) {
            throw new UnsupportedOperationException("Current thread CPU time measurement is not supported.");
        }
        return this.isThreadCpuTimeEnabled();
    }
    
    @Override
    public long getCurrentThreadCpuTime() {
        if (this.verifyCurrentThreadCpuTime()) {
            return getThreadTotalCpuTime0(0L);
        }
        return -1L;
    }
    
    @Override
    public long getThreadCpuTime(final long n) {
        return this.getThreadCpuTime(new long[] { n })[0];
    }
    
    private boolean verifyThreadCpuTime(final long[] array) {
        this.verifyThreadIds(array);
        if (!this.isThreadCpuTimeSupported() && !this.isCurrentThreadCpuTimeSupported()) {
            throw new UnsupportedOperationException("Thread CPU time measurement is not supported.");
        }
        if (!this.isThreadCpuTimeSupported()) {
            for (int i = 0; i < array.length; ++i) {
                if (array[i] != Thread.currentThread().getId()) {
                    throw new UnsupportedOperationException("Thread CPU time measurement is only supported for the current thread.");
                }
            }
        }
        return this.isThreadCpuTimeEnabled();
    }
    
    @Override
    public long[] getThreadCpuTime(final long[] array) {
        final boolean verifyThreadCpuTime = this.verifyThreadCpuTime(array);
        final int length = array.length;
        final long[] array2 = new long[length];
        Arrays.fill(array2, -1L);
        if (verifyThreadCpuTime) {
            if (length == 1) {
                long n = array[0];
                if (n == Thread.currentThread().getId()) {
                    n = 0L;
                }
                array2[0] = getThreadTotalCpuTime0(n);
            }
            else {
                getThreadTotalCpuTime1(array, array2);
            }
        }
        return array2;
    }
    
    @Override
    public long getCurrentThreadUserTime() {
        if (this.verifyCurrentThreadCpuTime()) {
            return getThreadUserCpuTime0(0L);
        }
        return -1L;
    }
    
    @Override
    public long getThreadUserTime(final long n) {
        return this.getThreadUserTime(new long[] { n })[0];
    }
    
    @Override
    public long[] getThreadUserTime(final long[] array) {
        final boolean verifyThreadCpuTime = this.verifyThreadCpuTime(array);
        final int length = array.length;
        final long[] array2 = new long[length];
        Arrays.fill(array2, -1L);
        if (verifyThreadCpuTime) {
            if (length == 1) {
                long n = array[0];
                if (n == Thread.currentThread().getId()) {
                    n = 0L;
                }
                array2[0] = getThreadUserCpuTime0(n);
            }
            else {
                getThreadUserCpuTime1(array, array2);
            }
        }
        return array2;
    }
    
    @Override
    public void setThreadCpuTimeEnabled(final boolean b) {
        if (!this.isThreadCpuTimeSupported() && !this.isCurrentThreadCpuTimeSupported()) {
            throw new UnsupportedOperationException("Thread CPU time measurement is not supported");
        }
        Util.checkControlAccess();
        synchronized (this) {
            if (this.cpuTimeEnabled != b) {
                setThreadCpuTimeEnabled0(b);
                this.cpuTimeEnabled = b;
            }
        }
    }
    
    @Override
    public long getCurrentThreadAllocatedBytes() {
        if (this.isThreadAllocatedMemoryEnabled()) {
            return getThreadAllocatedMemory0(0L);
        }
        return -1L;
    }
    
    private boolean verifyThreadAllocatedMemory(final long n) {
        this.verifyThreadId(n);
        return this.isThreadAllocatedMemoryEnabled();
    }
    
    @Override
    public long getThreadAllocatedBytes(final long n) {
        if (this.verifyThreadAllocatedMemory(n)) {
            return getThreadAllocatedMemory0((Thread.currentThread().getId() == n) ? 0L : n);
        }
        return -1L;
    }
    
    private boolean verifyThreadAllocatedMemory(final long[] array) {
        this.verifyThreadIds(array);
        return this.isThreadAllocatedMemoryEnabled();
    }
    
    @Override
    public long[] getThreadAllocatedBytes(final long[] array) {
        Objects.requireNonNull(array);
        if (array.length == 1) {
            return new long[] { this.getThreadAllocatedBytes(array[0]) };
        }
        final boolean verifyThreadAllocatedMemory = this.verifyThreadAllocatedMemory(array);
        final long[] array2 = new long[array.length];
        Arrays.fill(array2, -1L);
        if (verifyThreadAllocatedMemory) {
            getThreadAllocatedMemory1(array, array2);
        }
        return array2;
    }
    
    @Override
    public void setThreadAllocatedMemoryEnabled(final boolean b) {
        this.ensureThreadAllocatedMemorySupported();
        Util.checkControlAccess();
        synchronized (this) {
            if (this.allocatedMemoryEnabled != b) {
                setThreadAllocatedMemoryEnabled0(b);
                this.allocatedMemoryEnabled = b;
            }
        }
    }
    
    @Override
    public long[] findMonitorDeadlockedThreads() {
        Util.checkMonitorAccess();
        final Thread[] monitorDeadlockedThreads0 = findMonitorDeadlockedThreads0();
        if (monitorDeadlockedThreads0 == null) {
            return null;
        }
        final long[] array = new long[monitorDeadlockedThreads0.length];
        for (int i = 0; i < monitorDeadlockedThreads0.length; ++i) {
            array[i] = monitorDeadlockedThreads0[i].getId();
        }
        return array;
    }
    
    @Override
    public long[] findDeadlockedThreads() {
        if (!this.isSynchronizerUsageSupported()) {
            throw new UnsupportedOperationException("Monitoring of Synchronizer Usage is not supported.");
        }
        Util.checkMonitorAccess();
        final Thread[] deadlockedThreads0 = findDeadlockedThreads0();
        if (deadlockedThreads0 == null) {
            return null;
        }
        final long[] array = new long[deadlockedThreads0.length];
        for (int i = 0; i < deadlockedThreads0.length; ++i) {
            array[i] = deadlockedThreads0[i].getId();
        }
        return array;
    }
    
    @Override
    public void resetPeakThreadCount() {
        Util.checkControlAccess();
        resetPeakThreadCount0();
    }
    
    @Override
    public boolean isObjectMonitorUsageSupported() {
        return this.jvm.isObjectMonitorUsageSupported();
    }
    
    @Override
    public boolean isSynchronizerUsageSupported() {
        return this.jvm.isSynchronizerUsageSupported();
    }
    
    private void verifyDumpThreads(final boolean b, final boolean b2) {
        if (b && !this.isObjectMonitorUsageSupported()) {
            throw new UnsupportedOperationException("Monitoring of Object Monitor Usage is not supported.");
        }
        if (b2 && !this.isSynchronizerUsageSupported()) {
            throw new UnsupportedOperationException("Monitoring of Synchronizer Usage is not supported.");
        }
        Util.checkMonitorAccess();
    }
    
    @Override
    public ThreadInfo[] getThreadInfo(final long[] array, final boolean b, final boolean b2) {
        return dumpThreads0(array, b, b2, Integer.MAX_VALUE);
    }
    
    @Override
    public ThreadInfo[] getThreadInfo(final long[] array, final boolean b, final boolean b2, final int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Invalid maxDepth parameter: " + n);
        }
        this.verifyThreadIds(array);
        if (array.length == 0) {
            return new ThreadInfo[0];
        }
        this.verifyDumpThreads(b, b2);
        return dumpThreads0(array, b, b2, n);
    }
    
    @Override
    public ThreadInfo[] dumpAllThreads(final boolean b, final boolean b2) {
        return this.dumpAllThreads(b, b2, Integer.MAX_VALUE);
    }
    
    @Override
    public ThreadInfo[] dumpAllThreads(final boolean b, final boolean b2, final int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Invalid maxDepth parameter: " + n);
        }
        this.verifyDumpThreads(b, b2);
        return dumpThreads0(null, b, b2, n);
    }
    
    private static native Thread[] getThreads();
    
    private static native void getThreadInfo1(final long[] p0, final int p1, final ThreadInfo[] p2);
    
    private static native long getThreadTotalCpuTime0(final long p0);
    
    private static native void getThreadTotalCpuTime1(final long[] p0, final long[] p1);
    
    private static native long getThreadUserCpuTime0(final long p0);
    
    private static native void getThreadUserCpuTime1(final long[] p0, final long[] p1);
    
    private static native long getThreadAllocatedMemory0(final long p0);
    
    private static native void getThreadAllocatedMemory1(final long[] p0, final long[] p1);
    
    private static native void setThreadCpuTimeEnabled0(final boolean p0);
    
    private static native void setThreadAllocatedMemoryEnabled0(final boolean p0);
    
    private static native void setThreadContentionMonitoringEnabled0(final boolean p0);
    
    private static native Thread[] findMonitorDeadlockedThreads0();
    
    private static native Thread[] findDeadlockedThreads0();
    
    private static native void resetPeakThreadCount0();
    
    private static native ThreadInfo[] dumpThreads0(final long[] p0, final boolean p1, final boolean p2, final int p3);
    
    private static native void resetContentionTimes0(final long p0);
    
    @Override
    public ObjectName getObjectName() {
        return Util.newObjectName("java.lang:type=Threading");
    }
}
