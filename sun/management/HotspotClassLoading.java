package sun.management;

import sun.management.counter.Counter;
import java.util.List;

class HotspotClassLoading implements HotspotClassLoadingMBean
{
    private VMManagement jvm;
    private static final String JAVA_CLS = "java.cls.";
    private static final String COM_SUN_CLS = "com.sun.cls.";
    private static final String SUN_CLS = "sun.cls.";
    private static final String CLS_COUNTER_NAME_PATTERN = "java.cls.|com.sun.cls.|sun.cls.";
    
    HotspotClassLoading(final VMManagement jvm) {
        this.jvm = jvm;
    }
    
    @Override
    public long getLoadedClassSize() {
        return this.jvm.getLoadedClassSize();
    }
    
    @Override
    public long getUnloadedClassSize() {
        return this.jvm.getUnloadedClassSize();
    }
    
    @Override
    public long getClassLoadingTime() {
        return this.jvm.getClassLoadingTime();
    }
    
    @Override
    public long getMethodDataSize() {
        return this.jvm.getMethodDataSize();
    }
    
    @Override
    public long getInitializedClassCount() {
        return this.jvm.getInitializedClassCount();
    }
    
    @Override
    public long getClassInitializationTime() {
        return this.jvm.getClassInitializationTime();
    }
    
    @Override
    public long getClassVerificationTime() {
        return this.jvm.getClassVerificationTime();
    }
    
    @Override
    public List<Counter> getInternalClassLoadingCounters() {
        return this.jvm.getInternalCounters("java.cls.|com.sun.cls.|sun.cls.");
    }
}
