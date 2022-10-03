package sun.management;

import java.lang.reflect.Type;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;
import java.lang.management.LockInfo;

public class LockInfoCompositeData extends LazyCompositeData
{
    private final LockInfo lock;
    private static final CompositeType lockInfoCompositeType;
    private static final String CLASS_NAME = "className";
    private static final String IDENTITY_HASH_CODE = "identityHashCode";
    private static final String[] lockInfoItemNames;
    private static final long serialVersionUID = -6374759159749014052L;
    
    private LockInfoCompositeData(final LockInfo lock) {
        this.lock = lock;
    }
    
    public LockInfo getLockInfo() {
        return this.lock;
    }
    
    public static CompositeData toCompositeData(final LockInfo lockInfo) {
        if (lockInfo == null) {
            return null;
        }
        return new LockInfoCompositeData(lockInfo).getCompositeData();
    }
    
    @Override
    protected CompositeData getCompositeData() {
        final Object[] array = { new String(this.lock.getClassName()), new Integer(this.lock.getIdentityHashCode()) };
        try {
            return new CompositeDataSupport(LockInfoCompositeData.lockInfoCompositeType, LockInfoCompositeData.lockInfoItemNames, array);
        }
        catch (final OpenDataException ex) {
            throw Util.newException(ex);
        }
    }
    
    static CompositeType getLockInfoCompositeType() {
        return LockInfoCompositeData.lockInfoCompositeType;
    }
    
    public static LockInfo toLockInfo(final CompositeData compositeData) {
        if (compositeData == null) {
            throw new NullPointerException("Null CompositeData");
        }
        if (!LazyCompositeData.isTypeMatched(LockInfoCompositeData.lockInfoCompositeType, compositeData.getCompositeType())) {
            throw new IllegalArgumentException("Unexpected composite type for LockInfo");
        }
        return new LockInfo(LazyCompositeData.getString(compositeData, "className"), LazyCompositeData.getInt(compositeData, "identityHashCode"));
    }
    
    static {
        try {
            lockInfoCompositeType = (CompositeType)MappedMXBeanType.toOpenType(LockInfo.class);
        }
        catch (final OpenDataException ex) {
            throw Util.newException(ex);
        }
        lockInfoItemNames = new String[] { "className", "identityHashCode" };
    }
}
