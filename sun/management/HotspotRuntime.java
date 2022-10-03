package sun.management;

import sun.management.counter.Counter;
import java.util.List;

class HotspotRuntime implements HotspotRuntimeMBean
{
    private VMManagement jvm;
    private static final String JAVA_RT = "java.rt.";
    private static final String COM_SUN_RT = "com.sun.rt.";
    private static final String SUN_RT = "sun.rt.";
    private static final String JAVA_PROPERTY = "java.property.";
    private static final String COM_SUN_PROPERTY = "com.sun.property.";
    private static final String SUN_PROPERTY = "sun.property.";
    private static final String RT_COUNTER_NAME_PATTERN = "java.rt.|com.sun.rt.|sun.rt.|java.property.|com.sun.property.|sun.property.";
    
    HotspotRuntime(final VMManagement jvm) {
        this.jvm = jvm;
    }
    
    @Override
    public long getSafepointCount() {
        return this.jvm.getSafepointCount();
    }
    
    @Override
    public long getTotalSafepointTime() {
        return this.jvm.getTotalSafepointTime();
    }
    
    @Override
    public long getSafepointSyncTime() {
        return this.jvm.getSafepointSyncTime();
    }
    
    @Override
    public List<Counter> getInternalRuntimeCounters() {
        return this.jvm.getInternalCounters("java.rt.|com.sun.rt.|sun.rt.|java.property.|com.sun.property.|sun.property.");
    }
}
