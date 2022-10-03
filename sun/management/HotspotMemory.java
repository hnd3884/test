package sun.management;

import sun.management.counter.Counter;
import java.util.List;

class HotspotMemory implements HotspotMemoryMBean
{
    private VMManagement jvm;
    private static final String JAVA_GC = "java.gc.";
    private static final String COM_SUN_GC = "com.sun.gc.";
    private static final String SUN_GC = "sun.gc.";
    private static final String GC_COUNTER_NAME_PATTERN = "java.gc.|com.sun.gc.|sun.gc.";
    
    HotspotMemory(final VMManagement jvm) {
        this.jvm = jvm;
    }
    
    @Override
    public List<Counter> getInternalMemoryCounters() {
        return this.jvm.getInternalCounters("java.gc.|com.sun.gc.|sun.gc.");
    }
}
