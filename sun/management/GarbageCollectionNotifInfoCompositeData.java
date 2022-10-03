package sun.management;

import com.sun.management.GcInfo;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.SimpleType;
import javax.management.openmbean.OpenType;
import java.security.AccessController;
import java.lang.reflect.Field;
import java.security.PrivilegedAction;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;
import java.util.HashMap;
import com.sun.management.GarbageCollectionNotificationInfo;

public class GarbageCollectionNotifInfoCompositeData extends LazyCompositeData
{
    private final GarbageCollectionNotificationInfo gcNotifInfo;
    private static final String GC_NAME = "gcName";
    private static final String GC_ACTION = "gcAction";
    private static final String GC_CAUSE = "gcCause";
    private static final String GC_INFO = "gcInfo";
    private static final String[] gcNotifInfoItemNames;
    private static HashMap<GcInfoBuilder, CompositeType> compositeTypeByBuilder;
    private static CompositeType baseGcNotifInfoCompositeType;
    private static final long serialVersionUID = -1805123446483771292L;
    
    public GarbageCollectionNotifInfoCompositeData(final GarbageCollectionNotificationInfo gcNotifInfo) {
        this.gcNotifInfo = gcNotifInfo;
    }
    
    public GarbageCollectionNotificationInfo getGarbageCollectionNotifInfo() {
        return this.gcNotifInfo;
    }
    
    public static CompositeData toCompositeData(final GarbageCollectionNotificationInfo garbageCollectionNotificationInfo) {
        return new GarbageCollectionNotifInfoCompositeData(garbageCollectionNotificationInfo).getCompositeData();
    }
    
    private CompositeType getCompositeTypeByBuilder() {
        final GcInfoBuilder gcInfoBuilder = AccessController.doPrivileged((PrivilegedAction<GcInfoBuilder>)new PrivilegedAction<GcInfoBuilder>() {
            @Override
            public GcInfoBuilder run() {
                try {
                    final Field declaredField = Class.forName("com.sun.management.GcInfo").getDeclaredField("builder");
                    declaredField.setAccessible(true);
                    return (GcInfoBuilder)declaredField.get(GarbageCollectionNotifInfoCompositeData.this.gcNotifInfo.getGcInfo());
                }
                catch (final ClassNotFoundException | NoSuchFieldException | IllegalAccessException ex) {
                    return null;
                }
            }
        });
        CompositeType compositeType = null;
        synchronized (GarbageCollectionNotifInfoCompositeData.compositeTypeByBuilder) {
            compositeType = GarbageCollectionNotifInfoCompositeData.compositeTypeByBuilder.get(gcInfoBuilder);
            if (compositeType == null) {
                final OpenType[] array = { SimpleType.STRING, SimpleType.STRING, SimpleType.STRING, gcInfoBuilder.getGcInfoCompositeType() };
                try {
                    compositeType = new CompositeType("sun.management.GarbageCollectionNotifInfoCompositeType", "CompositeType for GC notification info", GarbageCollectionNotifInfoCompositeData.gcNotifInfoItemNames, GarbageCollectionNotifInfoCompositeData.gcNotifInfoItemNames, array);
                    GarbageCollectionNotifInfoCompositeData.compositeTypeByBuilder.put(gcInfoBuilder, compositeType);
                }
                catch (final OpenDataException ex) {
                    throw Util.newException(ex);
                }
            }
        }
        return compositeType;
    }
    
    @Override
    protected CompositeData getCompositeData() {
        final Object[] array = { this.gcNotifInfo.getGcName(), this.gcNotifInfo.getGcAction(), this.gcNotifInfo.getGcCause(), GcInfoCompositeData.toCompositeData(this.gcNotifInfo.getGcInfo()) };
        final CompositeType compositeTypeByBuilder = this.getCompositeTypeByBuilder();
        try {
            return new CompositeDataSupport(compositeTypeByBuilder, GarbageCollectionNotifInfoCompositeData.gcNotifInfoItemNames, array);
        }
        catch (final OpenDataException ex) {
            throw new AssertionError((Object)ex);
        }
    }
    
    public static String getGcName(final CompositeData compositeData) {
        final String string = LazyCompositeData.getString(compositeData, "gcName");
        if (string == null) {
            throw new IllegalArgumentException("Invalid composite data: Attribute gcName has null value");
        }
        return string;
    }
    
    public static String getGcAction(final CompositeData compositeData) {
        final String string = LazyCompositeData.getString(compositeData, "gcAction");
        if (string == null) {
            throw new IllegalArgumentException("Invalid composite data: Attribute gcAction has null value");
        }
        return string;
    }
    
    public static String getGcCause(final CompositeData compositeData) {
        final String string = LazyCompositeData.getString(compositeData, "gcCause");
        if (string == null) {
            throw new IllegalArgumentException("Invalid composite data: Attribute gcCause has null value");
        }
        return string;
    }
    
    public static GcInfo getGcInfo(final CompositeData compositeData) {
        return GcInfo.from((CompositeData)compositeData.get("gcInfo"));
    }
    
    public static void validateCompositeData(final CompositeData compositeData) {
        if (compositeData == null) {
            throw new NullPointerException("Null CompositeData");
        }
        if (!LazyCompositeData.isTypeMatched(getBaseGcNotifInfoCompositeType(), compositeData.getCompositeType())) {
            throw new IllegalArgumentException("Unexpected composite type for GarbageCollectionNotificationInfo");
        }
    }
    
    private static synchronized CompositeType getBaseGcNotifInfoCompositeType() {
        if (GarbageCollectionNotifInfoCompositeData.baseGcNotifInfoCompositeType == null) {
            try {
                GarbageCollectionNotifInfoCompositeData.baseGcNotifInfoCompositeType = new CompositeType("sun.management.BaseGarbageCollectionNotifInfoCompositeType", "CompositeType for Base GarbageCollectionNotificationInfo", GarbageCollectionNotifInfoCompositeData.gcNotifInfoItemNames, GarbageCollectionNotifInfoCompositeData.gcNotifInfoItemNames, new OpenType[] { SimpleType.STRING, SimpleType.STRING, SimpleType.STRING, GcInfoCompositeData.getBaseGcInfoCompositeType() });
            }
            catch (final OpenDataException ex) {
                throw Util.newException(ex);
            }
        }
        return GarbageCollectionNotifInfoCompositeData.baseGcNotifInfoCompositeType;
    }
    
    static {
        gcNotifInfoItemNames = new String[] { "gcName", "gcAction", "gcCause", "gcInfo" };
        GarbageCollectionNotifInfoCompositeData.compositeTypeByBuilder = new HashMap<GcInfoBuilder, CompositeType>();
        GarbageCollectionNotifInfoCompositeData.baseGcNotifInfoCompositeType = null;
    }
}
