package sun.management;

import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.SimpleType;
import javax.management.openmbean.OpenType;
import java.lang.management.MemoryUsage;
import com.sun.management.GcInfo;
import javax.management.openmbean.CompositeType;
import java.lang.management.GarbageCollectorMXBean;

public class GcInfoBuilder
{
    private final GarbageCollectorMXBean gc;
    private final String[] poolNames;
    private String[] allItemNames;
    private CompositeType gcInfoCompositeType;
    private final int gcExtItemCount;
    private final String[] gcExtItemNames;
    private final String[] gcExtItemDescs;
    private final char[] gcExtItemTypes;
    
    GcInfoBuilder(final GarbageCollectorMXBean gc, final String[] poolNames) {
        this.gc = gc;
        this.poolNames = poolNames;
        this.gcExtItemCount = this.getNumGcExtAttributes(gc);
        this.gcExtItemNames = new String[this.gcExtItemCount];
        this.gcExtItemDescs = new String[this.gcExtItemCount];
        this.gcExtItemTypes = new char[this.gcExtItemCount];
        this.fillGcAttributeInfo(gc, this.gcExtItemCount, this.gcExtItemNames, this.gcExtItemTypes, this.gcExtItemDescs);
        this.gcInfoCompositeType = null;
    }
    
    GcInfo getLastGcInfo() {
        return this.getLastGcInfo0(this.gc, this.gcExtItemCount, new Object[this.gcExtItemCount], this.gcExtItemTypes, new MemoryUsage[this.poolNames.length], new MemoryUsage[this.poolNames.length]);
    }
    
    public String[] getPoolNames() {
        return this.poolNames;
    }
    
    int getGcExtItemCount() {
        return this.gcExtItemCount;
    }
    
    synchronized CompositeType getGcInfoCompositeType() {
        if (this.gcInfoCompositeType != null) {
            return this.gcInfoCompositeType;
        }
        final String[] baseGcInfoItemNames = GcInfoCompositeData.getBaseGcInfoItemNames();
        final OpenType[] baseGcInfoItemTypes = GcInfoCompositeData.getBaseGcInfoItemTypes();
        final int length = baseGcInfoItemNames.length;
        final int n = length + this.gcExtItemCount;
        this.allItemNames = new String[n];
        final String[] array = new String[n];
        final OpenType[] array2 = new OpenType[n];
        System.arraycopy(baseGcInfoItemNames, 0, this.allItemNames, 0, length);
        System.arraycopy(baseGcInfoItemNames, 0, array, 0, length);
        System.arraycopy(baseGcInfoItemTypes, 0, array2, 0, length);
        if (this.gcExtItemCount > 0) {
            this.fillGcAttributeInfo(this.gc, this.gcExtItemCount, this.gcExtItemNames, this.gcExtItemTypes, this.gcExtItemDescs);
            System.arraycopy(this.gcExtItemNames, 0, this.allItemNames, length, this.gcExtItemCount);
            System.arraycopy(this.gcExtItemDescs, 0, array, length, this.gcExtItemCount);
            int n2 = length;
            for (int i = 0; i < this.gcExtItemCount; ++i) {
                switch (this.gcExtItemTypes[i]) {
                    case 'Z': {
                        array2[n2] = SimpleType.BOOLEAN;
                        break;
                    }
                    case 'B': {
                        array2[n2] = SimpleType.BYTE;
                        break;
                    }
                    case 'C': {
                        array2[n2] = SimpleType.CHARACTER;
                        break;
                    }
                    case 'S': {
                        array2[n2] = SimpleType.SHORT;
                        break;
                    }
                    case 'I': {
                        array2[n2] = SimpleType.INTEGER;
                        break;
                    }
                    case 'J': {
                        array2[n2] = SimpleType.LONG;
                        break;
                    }
                    case 'F': {
                        array2[n2] = SimpleType.FLOAT;
                        break;
                    }
                    case 'D': {
                        array2[n2] = SimpleType.DOUBLE;
                        break;
                    }
                    default: {
                        throw new AssertionError((Object)("Unsupported type [" + this.gcExtItemTypes[n2] + "]"));
                    }
                }
                ++n2;
            }
        }
        CompositeType gcInfoCompositeType;
        try {
            gcInfoCompositeType = new CompositeType("sun.management." + this.gc.getName() + ".GcInfoCompositeType", "CompositeType for GC info for " + this.gc.getName(), this.allItemNames, array, array2);
        }
        catch (final OpenDataException ex) {
            throw Util.newException(ex);
        }
        return this.gcInfoCompositeType = gcInfoCompositeType;
    }
    
    synchronized String[] getItemNames() {
        if (this.allItemNames == null) {
            this.getGcInfoCompositeType();
        }
        return this.allItemNames;
    }
    
    private native int getNumGcExtAttributes(final GarbageCollectorMXBean p0);
    
    private native void fillGcAttributeInfo(final GarbageCollectorMXBean p0, final int p1, final String[] p2, final char[] p3, final String[] p4);
    
    private native GcInfo getLastGcInfo0(final GarbageCollectorMXBean p0, final int p1, final Object[] p2, final char[] p3, final MemoryUsage[] p4, final MemoryUsage[] p5);
}
