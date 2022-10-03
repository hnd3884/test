package javax.management;

import java.util.Objects;

public class MBeanParameterInfo extends MBeanFeatureInfo implements Cloneable
{
    static final long serialVersionUID = 7432616882776782338L;
    static final MBeanParameterInfo[] NO_PARAMS;
    private final String type;
    
    public MBeanParameterInfo(final String s, final String s2, final String s3) {
        this(s, s2, s3, null);
    }
    
    public MBeanParameterInfo(final String s, final String type, final String s2, final Descriptor descriptor) {
        super(s, s2, descriptor);
        this.type = type;
    }
    
    public Object clone() {
        try {
            return super.clone();
        }
        catch (final CloneNotSupportedException ex) {
            return null;
        }
    }
    
    public String getType() {
        return this.type;
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + "[description=" + this.getDescription() + ", name=" + this.getName() + ", type=" + this.getType() + ", descriptor=" + this.getDescriptor() + "]";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof MBeanParameterInfo)) {
            return false;
        }
        final MBeanParameterInfo mBeanParameterInfo = (MBeanParameterInfo)o;
        return Objects.equals(mBeanParameterInfo.getName(), this.getName()) && Objects.equals(mBeanParameterInfo.getType(), this.getType()) && Objects.equals(mBeanParameterInfo.getDescription(), this.getDescription()) && Objects.equals(mBeanParameterInfo.getDescriptor(), this.getDescriptor());
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.getName(), this.getType());
    }
    
    static {
        NO_PARAMS = new MBeanParameterInfo[0];
    }
}
