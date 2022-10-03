package javax.management.openmbean;

import java.util.Arrays;
import javax.management.MBeanParameterInfo;
import javax.management.ImmutableDescriptor;
import javax.management.Descriptor;
import javax.management.MBeanOperationInfo;

public class OpenMBeanOperationInfoSupport extends MBeanOperationInfo implements OpenMBeanOperationInfo
{
    static final long serialVersionUID = 4996859732565369366L;
    private OpenType<?> returnOpenType;
    private transient Integer myHashCode;
    private transient String myToString;
    
    public OpenMBeanOperationInfoSupport(final String s, final String s2, final OpenMBeanParameterInfo[] array, final OpenType<?> openType, final int n) {
        this(s, s2, array, openType, n, null);
    }
    
    public OpenMBeanOperationInfoSupport(final String s, final String s2, final OpenMBeanParameterInfo[] array, final OpenType<?> returnOpenType, final int n, final Descriptor descriptor) {
        super(s, s2, arrayCopyCast(array), (returnOpenType == null) ? null : returnOpenType.getClassName(), n, ImmutableDescriptor.union(descriptor, (returnOpenType == null) ? null : returnOpenType.getDescriptor()));
        this.myHashCode = null;
        this.myToString = null;
        if (s == null || s.trim().equals("")) {
            throw new IllegalArgumentException("Argument name cannot be null or empty");
        }
        if (s2 == null || s2.trim().equals("")) {
            throw new IllegalArgumentException("Argument description cannot be null or empty");
        }
        if (returnOpenType == null) {
            throw new IllegalArgumentException("Argument returnOpenType cannot be null");
        }
        if (n != 1 && n != 2 && n != 0 && n != 3) {
            throw new IllegalArgumentException("Argument impact can only be one of ACTION, ACTION_INFO, INFO, or UNKNOWN: " + n);
        }
        this.returnOpenType = returnOpenType;
    }
    
    private static MBeanParameterInfo[] arrayCopyCast(final OpenMBeanParameterInfo[] array) {
        if (array == null) {
            return null;
        }
        final MBeanParameterInfo[] array2 = new MBeanParameterInfo[array.length];
        System.arraycopy(array, 0, array2, 0, array.length);
        return array2;
    }
    
    private static OpenMBeanParameterInfo[] arrayCopyCast(final MBeanParameterInfo[] array) {
        if (array == null) {
            return null;
        }
        final OpenMBeanParameterInfo[] array2 = new OpenMBeanParameterInfo[array.length];
        System.arraycopy(array, 0, array2, 0, array.length);
        return array2;
    }
    
    @Override
    public OpenType<?> getReturnOpenType() {
        return this.returnOpenType;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        OpenMBeanOperationInfo openMBeanOperationInfo;
        try {
            openMBeanOperationInfo = (OpenMBeanOperationInfo)o;
        }
        catch (final ClassCastException ex) {
            return false;
        }
        return this.getName().equals(openMBeanOperationInfo.getName()) && Arrays.equals(this.getSignature(), openMBeanOperationInfo.getSignature()) && this.getReturnOpenType().equals(openMBeanOperationInfo.getReturnOpenType()) && this.getImpact() == openMBeanOperationInfo.getImpact();
    }
    
    @Override
    public int hashCode() {
        if (this.myHashCode == null) {
            this.myHashCode = 0 + this.getName().hashCode() + Arrays.asList(this.getSignature()).hashCode() + this.getReturnOpenType().hashCode() + this.getImpact();
        }
        return this.myHashCode;
    }
    
    @Override
    public String toString() {
        if (this.myToString == null) {
            this.myToString = this.getClass().getName() + "(name=" + this.getName() + ",signature=" + Arrays.asList(this.getSignature()).toString() + ",return=" + this.getReturnOpenType().toString() + ",impact=" + this.getImpact() + ",descriptor=" + this.getDescriptor() + ")";
        }
        return this.myToString;
    }
    
    private Object readResolve() {
        if (this.getDescriptor().getFieldNames().length == 0) {
            return new OpenMBeanOperationInfoSupport(this.name, this.description, arrayCopyCast(this.getSignature()), this.returnOpenType, this.getImpact());
        }
        return this;
    }
}
