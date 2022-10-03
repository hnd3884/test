package sun.management;

import java.lang.reflect.Type;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;
import java.lang.management.MemoryUsage;

public class MemoryUsageCompositeData extends LazyCompositeData
{
    private final MemoryUsage usage;
    private static final CompositeType memoryUsageCompositeType;
    private static final String INIT = "init";
    private static final String USED = "used";
    private static final String COMMITTED = "committed";
    private static final String MAX = "max";
    private static final String[] memoryUsageItemNames;
    private static final long serialVersionUID = -8504291541083874143L;
    
    private MemoryUsageCompositeData(final MemoryUsage usage) {
        this.usage = usage;
    }
    
    public MemoryUsage getMemoryUsage() {
        return this.usage;
    }
    
    public static CompositeData toCompositeData(final MemoryUsage memoryUsage) {
        return new MemoryUsageCompositeData(memoryUsage).getCompositeData();
    }
    
    @Override
    protected CompositeData getCompositeData() {
        final Object[] array = { new Long(this.usage.getInit()), new Long(this.usage.getUsed()), new Long(this.usage.getCommitted()), new Long(this.usage.getMax()) };
        try {
            return new CompositeDataSupport(MemoryUsageCompositeData.memoryUsageCompositeType, MemoryUsageCompositeData.memoryUsageItemNames, array);
        }
        catch (final OpenDataException ex) {
            throw new AssertionError((Object)ex);
        }
    }
    
    static CompositeType getMemoryUsageCompositeType() {
        return MemoryUsageCompositeData.memoryUsageCompositeType;
    }
    
    public static long getInit(final CompositeData compositeData) {
        return LazyCompositeData.getLong(compositeData, "init");
    }
    
    public static long getUsed(final CompositeData compositeData) {
        return LazyCompositeData.getLong(compositeData, "used");
    }
    
    public static long getCommitted(final CompositeData compositeData) {
        return LazyCompositeData.getLong(compositeData, "committed");
    }
    
    public static long getMax(final CompositeData compositeData) {
        return LazyCompositeData.getLong(compositeData, "max");
    }
    
    public static void validateCompositeData(final CompositeData compositeData) {
        if (compositeData == null) {
            throw new NullPointerException("Null CompositeData");
        }
        if (!LazyCompositeData.isTypeMatched(MemoryUsageCompositeData.memoryUsageCompositeType, compositeData.getCompositeType())) {
            throw new IllegalArgumentException("Unexpected composite type for MemoryUsage");
        }
    }
    
    static {
        try {
            memoryUsageCompositeType = (CompositeType)MappedMXBeanType.toOpenType(MemoryUsage.class);
        }
        catch (final OpenDataException ex) {
            throw new AssertionError((Object)ex);
        }
        memoryUsageItemNames = new String[] { "init", "used", "committed", "max" };
    }
}
