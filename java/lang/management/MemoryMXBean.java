package java.lang.management;

public interface MemoryMXBean extends PlatformManagedObject
{
    int getObjectPendingFinalizationCount();
    
    MemoryUsage getHeapMemoryUsage();
    
    MemoryUsage getNonHeapMemoryUsage();
    
    boolean isVerbose();
    
    void setVerbose(final boolean p0);
    
    void gc();
}
