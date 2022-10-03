package sun.management;

import java.io.InvalidObjectException;
import java.lang.management.MemoryUsage;
import java.util.Map;
import javax.management.openmbean.SimpleType;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.OpenDataException;
import java.security.AccessController;
import java.lang.reflect.Field;
import java.security.PrivilegedAction;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenType;
import com.sun.management.GcInfo;

public class GcInfoCompositeData extends LazyCompositeData
{
    private final GcInfo info;
    private final GcInfoBuilder builder;
    private final Object[] gcExtItemValues;
    private static final String ID = "id";
    private static final String START_TIME = "startTime";
    private static final String END_TIME = "endTime";
    private static final String DURATION = "duration";
    private static final String MEMORY_USAGE_BEFORE_GC = "memoryUsageBeforeGc";
    private static final String MEMORY_USAGE_AFTER_GC = "memoryUsageAfterGc";
    private static final String[] baseGcInfoItemNames;
    private static MappedMXBeanType memoryUsageMapType;
    private static OpenType[] baseGcInfoItemTypes;
    private static CompositeType baseGcInfoCompositeType;
    private static final long serialVersionUID = -5716428894085882742L;
    
    public GcInfoCompositeData(final GcInfo info, final GcInfoBuilder builder, final Object[] gcExtItemValues) {
        this.info = info;
        this.builder = builder;
        this.gcExtItemValues = gcExtItemValues;
    }
    
    public GcInfo getGcInfo() {
        return this.info;
    }
    
    public static CompositeData toCompositeData(final GcInfo gcInfo) {
        return new GcInfoCompositeData(gcInfo, AccessController.doPrivileged((PrivilegedAction<GcInfoBuilder>)new PrivilegedAction<GcInfoBuilder>() {
            @Override
            public GcInfoBuilder run() {
                try {
                    final Field declaredField = Class.forName("com.sun.management.GcInfo").getDeclaredField("builder");
                    declaredField.setAccessible(true);
                    return (GcInfoBuilder)declaredField.get(gcInfo);
                }
                catch (final ClassNotFoundException | NoSuchFieldException | IllegalAccessException ex) {
                    return null;
                }
            }
        }), AccessController.doPrivileged((PrivilegedAction<Object[]>)new PrivilegedAction<Object[]>() {
            @Override
            public Object[] run() {
                try {
                    final Field declaredField = Class.forName("com.sun.management.GcInfo").getDeclaredField("extAttributes");
                    declaredField.setAccessible(true);
                    return (Object[])declaredField.get(gcInfo);
                }
                catch (final ClassNotFoundException | NoSuchFieldException | IllegalAccessException ex) {
                    return null;
                }
            }
        })).getCompositeData();
    }
    
    @Override
    protected CompositeData getCompositeData() {
        Object[] array;
        try {
            array = new Object[] { new Long(this.info.getId()), new Long(this.info.getStartTime()), new Long(this.info.getEndTime()), new Long(this.info.getDuration()), GcInfoCompositeData.memoryUsageMapType.toOpenTypeData(this.info.getMemoryUsageBeforeGc()), GcInfoCompositeData.memoryUsageMapType.toOpenTypeData(this.info.getMemoryUsageAfterGc()) };
        }
        catch (final OpenDataException ex) {
            throw new AssertionError((Object)ex);
        }
        final int gcExtItemCount = this.builder.getGcExtItemCount();
        if (gcExtItemCount == 0 && this.gcExtItemValues != null && this.gcExtItemValues.length != 0) {
            throw new AssertionError((Object)"Unexpected Gc Extension Item Values");
        }
        if (gcExtItemCount > 0 && (this.gcExtItemValues == null || gcExtItemCount != this.gcExtItemValues.length)) {
            throw new AssertionError((Object)"Unmatched Gc Extension Item Values");
        }
        final Object[] array2 = new Object[array.length + gcExtItemCount];
        System.arraycopy(array, 0, array2, 0, array.length);
        if (gcExtItemCount > 0) {
            System.arraycopy(this.gcExtItemValues, 0, array2, array.length, gcExtItemCount);
        }
        try {
            return new CompositeDataSupport(this.builder.getGcInfoCompositeType(), this.builder.getItemNames(), array2);
        }
        catch (final OpenDataException ex2) {
            throw new AssertionError((Object)ex2);
        }
    }
    
    static String[] getBaseGcInfoItemNames() {
        return GcInfoCompositeData.baseGcInfoItemNames;
    }
    
    static synchronized OpenType[] getBaseGcInfoItemTypes() {
        if (GcInfoCompositeData.baseGcInfoItemTypes == null) {
            final OpenType<?> openType = GcInfoCompositeData.memoryUsageMapType.getOpenType();
            GcInfoCompositeData.baseGcInfoItemTypes = new OpenType[] { SimpleType.LONG, SimpleType.LONG, SimpleType.LONG, SimpleType.LONG, openType, openType };
        }
        return GcInfoCompositeData.baseGcInfoItemTypes;
    }
    
    public static long getId(final CompositeData compositeData) {
        return LazyCompositeData.getLong(compositeData, "id");
    }
    
    public static long getStartTime(final CompositeData compositeData) {
        return LazyCompositeData.getLong(compositeData, "startTime");
    }
    
    public static long getEndTime(final CompositeData compositeData) {
        return LazyCompositeData.getLong(compositeData, "endTime");
    }
    
    public static Map<String, MemoryUsage> getMemoryUsageBeforeGc(final CompositeData compositeData) {
        try {
            return cast(GcInfoCompositeData.memoryUsageMapType.toJavaTypeData(compositeData.get("memoryUsageBeforeGc")));
        }
        catch (final InvalidObjectException | OpenDataException ex) {
            throw new AssertionError(ex);
        }
    }
    
    public static Map<String, MemoryUsage> cast(final Object o) {
        return (Map)o;
    }
    
    public static Map<String, MemoryUsage> getMemoryUsageAfterGc(final CompositeData compositeData) {
        try {
            return cast(GcInfoCompositeData.memoryUsageMapType.toJavaTypeData(compositeData.get("memoryUsageAfterGc")));
        }
        catch (final InvalidObjectException | OpenDataException ex) {
            throw new AssertionError(ex);
        }
    }
    
    public static void validateCompositeData(final CompositeData compositeData) {
        if (compositeData == null) {
            throw new NullPointerException("Null CompositeData");
        }
        if (!LazyCompositeData.isTypeMatched(getBaseGcInfoCompositeType(), compositeData.getCompositeType())) {
            throw new IllegalArgumentException("Unexpected composite type for GcInfo");
        }
    }
    
    static synchronized CompositeType getBaseGcInfoCompositeType() {
        if (GcInfoCompositeData.baseGcInfoCompositeType == null) {
            try {
                GcInfoCompositeData.baseGcInfoCompositeType = new CompositeType("sun.management.BaseGcInfoCompositeType", "CompositeType for Base GcInfo", getBaseGcInfoItemNames(), getBaseGcInfoItemNames(), getBaseGcInfoItemTypes());
            }
            catch (final OpenDataException ex) {
                throw Util.newException(ex);
            }
        }
        return GcInfoCompositeData.baseGcInfoCompositeType;
    }
    
    static {
        baseGcInfoItemNames = new String[] { "id", "startTime", "endTime", "duration", "memoryUsageBeforeGc", "memoryUsageAfterGc" };
        try {
            GcInfoCompositeData.memoryUsageMapType = MappedMXBeanType.getMappedType(GcInfo.class.getMethod("getMemoryUsageBeforeGc", (Class<?>[])new Class[0]).getGenericReturnType());
        }
        catch (final NoSuchMethodException | OpenDataException ex) {
            throw new AssertionError(ex);
        }
        GcInfoCompositeData.baseGcInfoItemTypes = null;
        GcInfoCompositeData.baseGcInfoCompositeType = null;
    }
}
