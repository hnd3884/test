package sun.management;

import java.lang.reflect.Type;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.CompositeDataSupport;
import java.lang.management.LockInfo;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;
import java.lang.management.MonitorInfo;

public class MonitorInfoCompositeData extends LazyCompositeData
{
    private final MonitorInfo lock;
    private static final CompositeType monitorInfoCompositeType;
    private static final String[] monitorInfoItemNames;
    private static final String CLASS_NAME = "className";
    private static final String IDENTITY_HASH_CODE = "identityHashCode";
    private static final String LOCKED_STACK_FRAME = "lockedStackFrame";
    private static final String LOCKED_STACK_DEPTH = "lockedStackDepth";
    private static final long serialVersionUID = -5825215591822908529L;
    
    private MonitorInfoCompositeData(final MonitorInfo lock) {
        this.lock = lock;
    }
    
    public MonitorInfo getMonitorInfo() {
        return this.lock;
    }
    
    public static CompositeData toCompositeData(final MonitorInfo monitorInfo) {
        return new MonitorInfoCompositeData(monitorInfo).getCompositeData();
    }
    
    @Override
    protected CompositeData getCompositeData() {
        final int length = MonitorInfoCompositeData.monitorInfoItemNames.length;
        final Object[] array = new Object[length];
        final CompositeData compositeData = LockInfoCompositeData.toCompositeData(this.lock);
        for (int i = 0; i < length; ++i) {
            final String s = MonitorInfoCompositeData.monitorInfoItemNames[i];
            if (s.equals("lockedStackFrame")) {
                final StackTraceElement lockedStackFrame = this.lock.getLockedStackFrame();
                array[i] = ((lockedStackFrame != null) ? StackTraceElementCompositeData.toCompositeData(lockedStackFrame) : null);
            }
            else if (s.equals("lockedStackDepth")) {
                array[i] = new Integer(this.lock.getLockedStackDepth());
            }
            else {
                array[i] = compositeData.get(s);
            }
        }
        try {
            return new CompositeDataSupport(MonitorInfoCompositeData.monitorInfoCompositeType, MonitorInfoCompositeData.monitorInfoItemNames, array);
        }
        catch (final OpenDataException ex) {
            throw new AssertionError((Object)ex);
        }
    }
    
    static CompositeType getMonitorInfoCompositeType() {
        return MonitorInfoCompositeData.monitorInfoCompositeType;
    }
    
    public static String getClassName(final CompositeData compositeData) {
        return LazyCompositeData.getString(compositeData, "className");
    }
    
    public static int getIdentityHashCode(final CompositeData compositeData) {
        return LazyCompositeData.getInt(compositeData, "identityHashCode");
    }
    
    public static StackTraceElement getLockedStackFrame(final CompositeData compositeData) {
        final CompositeData compositeData2 = (CompositeData)compositeData.get("lockedStackFrame");
        if (compositeData2 != null) {
            return StackTraceElementCompositeData.from(compositeData2);
        }
        return null;
    }
    
    public static int getLockedStackDepth(final CompositeData compositeData) {
        return LazyCompositeData.getInt(compositeData, "lockedStackDepth");
    }
    
    public static void validateCompositeData(final CompositeData compositeData) {
        if (compositeData == null) {
            throw new NullPointerException("Null CompositeData");
        }
        if (!LazyCompositeData.isTypeMatched(MonitorInfoCompositeData.monitorInfoCompositeType, compositeData.getCompositeType())) {
            throw new IllegalArgumentException("Unexpected composite type for MonitorInfo");
        }
    }
    
    static {
        try {
            monitorInfoCompositeType = (CompositeType)MappedMXBeanType.toOpenType(MonitorInfo.class);
            monitorInfoItemNames = MonitorInfoCompositeData.monitorInfoCompositeType.keySet().toArray(new String[0]);
        }
        catch (final OpenDataException ex) {
            throw new AssertionError((Object)ex);
        }
    }
}
