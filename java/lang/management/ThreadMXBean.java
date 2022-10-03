package java.lang.management;

public interface ThreadMXBean extends PlatformManagedObject
{
    int getThreadCount();
    
    int getPeakThreadCount();
    
    long getTotalStartedThreadCount();
    
    int getDaemonThreadCount();
    
    long[] getAllThreadIds();
    
    ThreadInfo getThreadInfo(final long p0);
    
    ThreadInfo[] getThreadInfo(final long[] p0);
    
    ThreadInfo getThreadInfo(final long p0, final int p1);
    
    ThreadInfo[] getThreadInfo(final long[] p0, final int p1);
    
    boolean isThreadContentionMonitoringSupported();
    
    boolean isThreadContentionMonitoringEnabled();
    
    void setThreadContentionMonitoringEnabled(final boolean p0);
    
    long getCurrentThreadCpuTime();
    
    long getCurrentThreadUserTime();
    
    long getThreadCpuTime(final long p0);
    
    long getThreadUserTime(final long p0);
    
    boolean isThreadCpuTimeSupported();
    
    boolean isCurrentThreadCpuTimeSupported();
    
    boolean isThreadCpuTimeEnabled();
    
    void setThreadCpuTimeEnabled(final boolean p0);
    
    long[] findMonitorDeadlockedThreads();
    
    void resetPeakThreadCount();
    
    long[] findDeadlockedThreads();
    
    boolean isObjectMonitorUsageSupported();
    
    boolean isSynchronizerUsageSupported();
    
    ThreadInfo[] getThreadInfo(final long[] p0, final boolean p1, final boolean p2);
    
    ThreadInfo[] dumpAllThreads(final boolean p0, final boolean p1);
}
