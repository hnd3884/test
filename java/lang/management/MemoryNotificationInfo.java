package java.lang.management;

import sun.management.MemoryNotifInfoCompositeData;
import javax.management.openmbean.CompositeData;

public class MemoryNotificationInfo
{
    private final String poolName;
    private final MemoryUsage usage;
    private final long count;
    public static final String MEMORY_THRESHOLD_EXCEEDED = "java.management.memory.threshold.exceeded";
    public static final String MEMORY_COLLECTION_THRESHOLD_EXCEEDED = "java.management.memory.collection.threshold.exceeded";
    
    public MemoryNotificationInfo(final String poolName, final MemoryUsage usage, final long count) {
        if (poolName == null) {
            throw new NullPointerException("Null poolName");
        }
        if (usage == null) {
            throw new NullPointerException("Null usage");
        }
        this.poolName = poolName;
        this.usage = usage;
        this.count = count;
    }
    
    MemoryNotificationInfo(final CompositeData compositeData) {
        MemoryNotifInfoCompositeData.validateCompositeData(compositeData);
        this.poolName = MemoryNotifInfoCompositeData.getPoolName(compositeData);
        this.usage = MemoryNotifInfoCompositeData.getUsage(compositeData);
        this.count = MemoryNotifInfoCompositeData.getCount(compositeData);
    }
    
    public String getPoolName() {
        return this.poolName;
    }
    
    public MemoryUsage getUsage() {
        return this.usage;
    }
    
    public long getCount() {
        return this.count;
    }
    
    public static MemoryNotificationInfo from(final CompositeData compositeData) {
        if (compositeData == null) {
            return null;
        }
        if (compositeData instanceof MemoryNotifInfoCompositeData) {
            return ((MemoryNotifInfoCompositeData)compositeData).getMemoryNotifInfo();
        }
        return new MemoryNotificationInfo(compositeData);
    }
}
