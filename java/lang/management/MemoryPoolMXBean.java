package java.lang.management;

public interface MemoryPoolMXBean extends PlatformManagedObject
{
    String getName();
    
    MemoryType getType();
    
    MemoryUsage getUsage();
    
    MemoryUsage getPeakUsage();
    
    void resetPeakUsage();
    
    boolean isValid();
    
    String[] getMemoryManagerNames();
    
    long getUsageThreshold();
    
    void setUsageThreshold(final long p0);
    
    boolean isUsageThresholdExceeded();
    
    long getUsageThresholdCount();
    
    boolean isUsageThresholdSupported();
    
    long getCollectionUsageThreshold();
    
    void setCollectionUsageThreshold(final long p0);
    
    boolean isCollectionUsageThresholdExceeded();
    
    long getCollectionUsageThresholdCount();
    
    MemoryUsage getCollectionUsage();
    
    boolean isCollectionUsageThresholdSupported();
}
