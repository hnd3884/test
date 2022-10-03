package sun.management;

import sun.management.counter.Counter;
import java.util.List;
import java.util.HashMap;
import java.util.Collections;
import java.util.Map;

class HotspotThread implements HotspotThreadMBean
{
    private VMManagement jvm;
    private static final String JAVA_THREADS = "java.threads.";
    private static final String COM_SUN_THREADS = "com.sun.threads.";
    private static final String SUN_THREADS = "sun.threads.";
    private static final String THREADS_COUNTER_NAME_PATTERN = "java.threads.|com.sun.threads.|sun.threads.";
    
    HotspotThread(final VMManagement jvm) {
        this.jvm = jvm;
    }
    
    @Override
    public native int getInternalThreadCount();
    
    @Override
    public Map<String, Long> getInternalThreadCpuTimes() {
        final int internalThreadCount = this.getInternalThreadCount();
        if (internalThreadCount == 0) {
            return Collections.emptyMap();
        }
        final String[] array = new String[internalThreadCount];
        final long[] array2 = new long[internalThreadCount];
        final int internalThreadTimes0 = this.getInternalThreadTimes0(array, array2);
        final HashMap hashMap = new HashMap(internalThreadTimes0);
        for (int i = 0; i < internalThreadTimes0; ++i) {
            hashMap.put((Object)array[i], (Object)new Long(array2[i]));
        }
        return (Map<String, Long>)hashMap;
    }
    
    public native int getInternalThreadTimes0(final String[] p0, final long[] p1);
    
    @Override
    public List<Counter> getInternalThreadingCounters() {
        return this.jvm.getInternalCounters("java.threads.|com.sun.threads.|sun.threads.");
    }
}
