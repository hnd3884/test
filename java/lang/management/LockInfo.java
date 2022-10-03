package java.lang.management;

import sun.management.LockInfoCompositeData;
import javax.management.openmbean.CompositeData;

public class LockInfo
{
    private String className;
    private int identityHashCode;
    
    public LockInfo(final String className, final int identityHashCode) {
        if (className == null) {
            throw new NullPointerException("Parameter className cannot be null");
        }
        this.className = className;
        this.identityHashCode = identityHashCode;
    }
    
    LockInfo(final Object o) {
        this.className = o.getClass().getName();
        this.identityHashCode = System.identityHashCode(o);
    }
    
    public String getClassName() {
        return this.className;
    }
    
    public int getIdentityHashCode() {
        return this.identityHashCode;
    }
    
    public static LockInfo from(final CompositeData compositeData) {
        if (compositeData == null) {
            return null;
        }
        if (compositeData instanceof LockInfoCompositeData) {
            return ((LockInfoCompositeData)compositeData).getLockInfo();
        }
        return LockInfoCompositeData.toLockInfo(compositeData);
    }
    
    @Override
    public String toString() {
        return this.className + '@' + Integer.toHexString(this.identityHashCode);
    }
}
