package javax.management.openmbean;

import java.util.Arrays;
import javax.management.MBeanParameterInfo;
import javax.management.Descriptor;
import javax.management.MBeanConstructorInfo;

public class OpenMBeanConstructorInfoSupport extends MBeanConstructorInfo implements OpenMBeanConstructorInfo
{
    static final long serialVersionUID = -4400441579007477003L;
    private transient Integer myHashCode;
    private transient String myToString;
    
    public OpenMBeanConstructorInfoSupport(final String s, final String s2, final OpenMBeanParameterInfo[] array) {
        this(s, s2, array, null);
    }
    
    public OpenMBeanConstructorInfoSupport(final String s, final String s2, final OpenMBeanParameterInfo[] array, final Descriptor descriptor) {
        super(s, s2, arrayCopyCast(array), descriptor);
        this.myHashCode = null;
        this.myToString = null;
        if (s == null || s.trim().equals("")) {
            throw new IllegalArgumentException("Argument name cannot be null or empty");
        }
        if (s2 == null || s2.trim().equals("")) {
            throw new IllegalArgumentException("Argument description cannot be null or empty");
        }
    }
    
    private static MBeanParameterInfo[] arrayCopyCast(final OpenMBeanParameterInfo[] array) {
        if (array == null) {
            return null;
        }
        final MBeanParameterInfo[] array2 = new MBeanParameterInfo[array.length];
        System.arraycopy(array, 0, array2, 0, array.length);
        return array2;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        OpenMBeanConstructorInfo openMBeanConstructorInfo;
        try {
            openMBeanConstructorInfo = (OpenMBeanConstructorInfo)o;
        }
        catch (final ClassCastException ex) {
            return false;
        }
        return this.getName().equals(openMBeanConstructorInfo.getName()) && Arrays.equals(this.getSignature(), openMBeanConstructorInfo.getSignature());
    }
    
    @Override
    public int hashCode() {
        if (this.myHashCode == null) {
            this.myHashCode = 0 + this.getName().hashCode() + Arrays.asList(this.getSignature()).hashCode();
        }
        return this.myHashCode;
    }
    
    @Override
    public String toString() {
        if (this.myToString == null) {
            this.myToString = this.getClass().getName() + "(name=" + this.getName() + ",signature=" + Arrays.asList(this.getSignature()).toString() + ",descriptor=" + this.getDescriptor() + ")";
        }
        return this.myToString;
    }
}
