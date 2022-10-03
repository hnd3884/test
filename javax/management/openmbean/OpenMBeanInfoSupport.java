package javax.management.openmbean;

import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Objects;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanAttributeInfo;
import javax.management.Descriptor;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanInfo;

public class OpenMBeanInfoSupport extends MBeanInfo implements OpenMBeanInfo
{
    static final long serialVersionUID = 4349395935420511492L;
    private transient Integer myHashCode;
    private transient String myToString;
    
    public OpenMBeanInfoSupport(final String s, final String s2, final OpenMBeanAttributeInfo[] array, final OpenMBeanConstructorInfo[] array2, final OpenMBeanOperationInfo[] array3, final MBeanNotificationInfo[] array4) {
        this(s, s2, array, array2, array3, array4, null);
    }
    
    public OpenMBeanInfoSupport(final String s, final String s2, final OpenMBeanAttributeInfo[] array, final OpenMBeanConstructorInfo[] array2, final OpenMBeanOperationInfo[] array3, final MBeanNotificationInfo[] array4, final Descriptor descriptor) {
        super(s, s2, attributeArray(array), constructorArray(array2), operationArray(array3), (MBeanNotificationInfo[])((array4 == null) ? null : ((MBeanNotificationInfo[])array4.clone())), descriptor);
        this.myHashCode = null;
        this.myToString = null;
    }
    
    private static MBeanAttributeInfo[] attributeArray(final OpenMBeanAttributeInfo[] array) {
        if (array == null) {
            return null;
        }
        final MBeanAttributeInfo[] array2 = new MBeanAttributeInfo[array.length];
        System.arraycopy(array, 0, array2, 0, array.length);
        return array2;
    }
    
    private static MBeanConstructorInfo[] constructorArray(final OpenMBeanConstructorInfo[] array) {
        if (array == null) {
            return null;
        }
        final MBeanConstructorInfo[] array2 = new MBeanConstructorInfo[array.length];
        System.arraycopy(array, 0, array2, 0, array.length);
        return array2;
    }
    
    private static MBeanOperationInfo[] operationArray(final OpenMBeanOperationInfo[] array) {
        if (array == null) {
            return null;
        }
        final MBeanOperationInfo[] array2 = new MBeanOperationInfo[array.length];
        System.arraycopy(array, 0, array2, 0, array.length);
        return array2;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        OpenMBeanInfo openMBeanInfo;
        try {
            openMBeanInfo = (OpenMBeanInfo)o;
        }
        catch (final ClassCastException ex) {
            return false;
        }
        return Objects.equals(this.getClassName(), openMBeanInfo.getClassName()) && sameArrayContents(this.getAttributes(), openMBeanInfo.getAttributes()) && sameArrayContents(this.getConstructors(), openMBeanInfo.getConstructors()) && sameArrayContents(this.getOperations(), openMBeanInfo.getOperations()) && sameArrayContents(this.getNotifications(), openMBeanInfo.getNotifications());
    }
    
    private static <T> boolean sameArrayContents(final T[] array, final T[] array2) {
        return new HashSet(Arrays.asList(array)).equals(new HashSet(Arrays.asList(array2)));
    }
    
    @Override
    public int hashCode() {
        if (this.myHashCode == null) {
            int n = 0;
            if (this.getClassName() != null) {
                n += this.getClassName().hashCode();
            }
            this.myHashCode = n + arraySetHash(this.getAttributes()) + arraySetHash(this.getConstructors()) + arraySetHash(this.getOperations()) + arraySetHash(this.getNotifications());
        }
        return this.myHashCode;
    }
    
    private static <T> int arraySetHash(final T[] array) {
        return new HashSet(Arrays.asList(array)).hashCode();
    }
    
    @Override
    public String toString() {
        if (this.myToString == null) {
            this.myToString = this.getClass().getName() + "(mbean_class_name=" + this.getClassName() + ",attributes=" + Arrays.asList(this.getAttributes()).toString() + ",constructors=" + Arrays.asList(this.getConstructors()).toString() + ",operations=" + Arrays.asList(this.getOperations()).toString() + ",notifications=" + Arrays.asList(this.getNotifications()).toString() + ",descriptor=" + this.getDescriptor() + ")";
        }
        return this.myToString;
    }
}
