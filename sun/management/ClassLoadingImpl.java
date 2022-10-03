package sun.management;

import javax.management.ObjectName;
import java.lang.management.ClassLoadingMXBean;

class ClassLoadingImpl implements ClassLoadingMXBean
{
    private final VMManagement jvm;
    
    ClassLoadingImpl(final VMManagement jvm) {
        this.jvm = jvm;
    }
    
    @Override
    public long getTotalLoadedClassCount() {
        return this.jvm.getTotalClassCount();
    }
    
    @Override
    public int getLoadedClassCount() {
        return this.jvm.getLoadedClassCount();
    }
    
    @Override
    public long getUnloadedClassCount() {
        return this.jvm.getUnloadedClassCount();
    }
    
    @Override
    public boolean isVerbose() {
        return this.jvm.getVerboseClass();
    }
    
    @Override
    public void setVerbose(final boolean verboseClass) {
        Util.checkControlAccess();
        setVerboseClass(verboseClass);
    }
    
    static native void setVerboseClass(final boolean p0);
    
    @Override
    public ObjectName getObjectName() {
        return Util.newObjectName("java.lang:type=ClassLoading");
    }
}
