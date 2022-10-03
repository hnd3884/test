package javax.management;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Objects;
import java.util.Arrays;

public class MBeanNotificationInfo extends MBeanFeatureInfo implements Cloneable
{
    static final long serialVersionUID = -3888371564530107064L;
    private static final String[] NO_TYPES;
    static final MBeanNotificationInfo[] NO_NOTIFICATIONS;
    private String[] types;
    private final transient boolean arrayGettersSafe;
    
    public MBeanNotificationInfo(final String[] array, final String s, final String s2) {
        this(array, s, s2, null);
    }
    
    public MBeanNotificationInfo(final String[] array, final String s, final String s2, final Descriptor descriptor) {
        super(s, s2, descriptor);
        this.types = ((array != null && array.length > 0) ? array.clone() : MBeanNotificationInfo.NO_TYPES);
        this.arrayGettersSafe = MBeanInfo.arrayGettersSafe(this.getClass(), MBeanNotificationInfo.class);
    }
    
    public Object clone() {
        try {
            return super.clone();
        }
        catch (final CloneNotSupportedException ex) {
            return null;
        }
    }
    
    public String[] getNotifTypes() {
        if (this.types.length == 0) {
            return MBeanNotificationInfo.NO_TYPES;
        }
        return this.types.clone();
    }
    
    private String[] fastGetNotifTypes() {
        if (this.arrayGettersSafe) {
            return this.types;
        }
        return this.getNotifTypes();
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + "[description=" + this.getDescription() + ", name=" + this.getName() + ", notifTypes=" + Arrays.asList(this.fastGetNotifTypes()) + ", descriptor=" + this.getDescriptor() + "]";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof MBeanNotificationInfo)) {
            return false;
        }
        final MBeanNotificationInfo mBeanNotificationInfo = (MBeanNotificationInfo)o;
        return Objects.equals(mBeanNotificationInfo.getName(), this.getName()) && Objects.equals(mBeanNotificationInfo.getDescription(), this.getDescription()) && Objects.equals(mBeanNotificationInfo.getDescriptor(), this.getDescriptor()) && Arrays.equals(mBeanNotificationInfo.fastGetNotifTypes(), this.fastGetNotifTypes());
    }
    
    @Override
    public int hashCode() {
        int hashCode = this.getName().hashCode();
        for (int i = 0; i < this.types.length; ++i) {
            hashCode ^= this.types[i].hashCode();
        }
        return hashCode;
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        final String[] array = (String[])objectInputStream.readFields().get("types", null);
        this.types = ((array != null && array.length != 0) ? array.clone() : MBeanNotificationInfo.NO_TYPES);
    }
    
    static {
        NO_TYPES = new String[0];
        NO_NOTIFICATIONS = new MBeanNotificationInfo[0];
    }
}
