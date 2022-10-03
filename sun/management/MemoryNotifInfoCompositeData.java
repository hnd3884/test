package sun.management;

import java.lang.reflect.Type;
import java.lang.management.MemoryUsage;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;
import java.lang.management.MemoryNotificationInfo;

public class MemoryNotifInfoCompositeData extends LazyCompositeData
{
    private final MemoryNotificationInfo memoryNotifInfo;
    private static final CompositeType memoryNotifInfoCompositeType;
    private static final String POOL_NAME = "poolName";
    private static final String USAGE = "usage";
    private static final String COUNT = "count";
    private static final String[] memoryNotifInfoItemNames;
    private static final long serialVersionUID = -1805123446483771291L;
    
    private MemoryNotifInfoCompositeData(final MemoryNotificationInfo memoryNotifInfo) {
        this.memoryNotifInfo = memoryNotifInfo;
    }
    
    public MemoryNotificationInfo getMemoryNotifInfo() {
        return this.memoryNotifInfo;
    }
    
    public static CompositeData toCompositeData(final MemoryNotificationInfo memoryNotificationInfo) {
        return new MemoryNotifInfoCompositeData(memoryNotificationInfo).getCompositeData();
    }
    
    @Override
    protected CompositeData getCompositeData() {
        final Object[] array = { this.memoryNotifInfo.getPoolName(), MemoryUsageCompositeData.toCompositeData(this.memoryNotifInfo.getUsage()), new Long(this.memoryNotifInfo.getCount()) };
        try {
            return new CompositeDataSupport(MemoryNotifInfoCompositeData.memoryNotifInfoCompositeType, MemoryNotifInfoCompositeData.memoryNotifInfoItemNames, array);
        }
        catch (final OpenDataException ex) {
            throw new AssertionError((Object)ex);
        }
    }
    
    public static String getPoolName(final CompositeData compositeData) {
        final String string = LazyCompositeData.getString(compositeData, "poolName");
        if (string == null) {
            throw new IllegalArgumentException("Invalid composite data: Attribute poolName has null value");
        }
        return string;
    }
    
    public static MemoryUsage getUsage(final CompositeData compositeData) {
        return MemoryUsage.from((CompositeData)compositeData.get("usage"));
    }
    
    public static long getCount(final CompositeData compositeData) {
        return LazyCompositeData.getLong(compositeData, "count");
    }
    
    public static void validateCompositeData(final CompositeData compositeData) {
        if (compositeData == null) {
            throw new NullPointerException("Null CompositeData");
        }
        if (!LazyCompositeData.isTypeMatched(MemoryNotifInfoCompositeData.memoryNotifInfoCompositeType, compositeData.getCompositeType())) {
            throw new IllegalArgumentException("Unexpected composite type for MemoryNotificationInfo");
        }
    }
    
    static {
        try {
            memoryNotifInfoCompositeType = (CompositeType)MappedMXBeanType.toOpenType(MemoryNotificationInfo.class);
        }
        catch (final OpenDataException ex) {
            throw new AssertionError((Object)ex);
        }
        memoryNotifInfoItemNames = new String[] { "poolName", "usage", "count" };
    }
}
