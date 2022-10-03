package com.sun.management;

import java.lang.management.ThreadInfo;
import jdk.Exported;

@Exported
public interface ThreadMXBean extends java.lang.management.ThreadMXBean
{
    long[] getThreadCpuTime(final long[] p0);
    
    long[] getThreadUserTime(final long[] p0);
    
    default long getCurrentThreadAllocatedBytes() {
        return this.getThreadAllocatedBytes(Thread.currentThread().getId());
    }
    
    long getThreadAllocatedBytes(final long p0);
    
    long[] getThreadAllocatedBytes(final long[] p0);
    
    boolean isThreadAllocatedMemorySupported();
    
    boolean isThreadAllocatedMemoryEnabled();
    
    void setThreadAllocatedMemoryEnabled(final boolean p0);
    
    default ThreadInfo[] getThreadInfo(final long[] array, final boolean b, final boolean b2, final int n) {
        throw new UnsupportedOperationException();
    }
    
    default ThreadInfo[] dumpAllThreads(final boolean b, final boolean b2, final int n) {
        throw new UnsupportedOperationException();
    }
}
