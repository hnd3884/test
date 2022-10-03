package sun.management;

import javax.management.ObjectName;
import java.lang.management.CompilationMXBean;

class CompilationImpl implements CompilationMXBean
{
    private final VMManagement jvm;
    private final String name;
    
    CompilationImpl(final VMManagement jvm) {
        this.jvm = jvm;
        this.name = this.jvm.getCompilerName();
        if (this.name == null) {
            throw new AssertionError((Object)"Null compiler name");
        }
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public boolean isCompilationTimeMonitoringSupported() {
        return this.jvm.isCompilationTimeMonitoringSupported();
    }
    
    @Override
    public long getTotalCompilationTime() {
        if (!this.isCompilationTimeMonitoringSupported()) {
            throw new UnsupportedOperationException("Compilation time monitoring is not supported.");
        }
        return this.jvm.getTotalCompileTime();
    }
    
    @Override
    public ObjectName getObjectName() {
        return Util.newObjectName("java.lang:type=Compilation");
    }
}
