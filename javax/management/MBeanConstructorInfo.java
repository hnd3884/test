package javax.management;

import java.util.Objects;
import java.util.Arrays;
import java.lang.reflect.AnnotatedElement;
import com.sun.jmx.mbeanserver.Introspector;
import java.lang.reflect.Constructor;

public class MBeanConstructorInfo extends MBeanFeatureInfo implements Cloneable
{
    static final long serialVersionUID = 4433990064191844427L;
    static final MBeanConstructorInfo[] NO_CONSTRUCTORS;
    private final transient boolean arrayGettersSafe;
    private final MBeanParameterInfo[] signature;
    
    public MBeanConstructorInfo(final String s, final Constructor<?> constructor) {
        this(constructor.getName(), s, constructorSignature(constructor), Introspector.descriptorForElement(constructor));
    }
    
    public MBeanConstructorInfo(final String s, final String s2, final MBeanParameterInfo[] array) {
        this(s, s2, array, null);
    }
    
    public MBeanConstructorInfo(final String s, final String s2, MBeanParameterInfo[] no_PARAMS, final Descriptor descriptor) {
        super(s, s2, descriptor);
        if (no_PARAMS == null || no_PARAMS.length == 0) {
            no_PARAMS = MBeanParameterInfo.NO_PARAMS;
        }
        else {
            no_PARAMS = no_PARAMS.clone();
        }
        this.signature = no_PARAMS;
        this.arrayGettersSafe = MBeanInfo.arrayGettersSafe(this.getClass(), MBeanConstructorInfo.class);
    }
    
    public Object clone() {
        try {
            return super.clone();
        }
        catch (final CloneNotSupportedException ex) {
            return null;
        }
    }
    
    public MBeanParameterInfo[] getSignature() {
        if (this.signature.length == 0) {
            return this.signature;
        }
        return this.signature.clone();
    }
    
    private MBeanParameterInfo[] fastGetSignature() {
        if (this.arrayGettersSafe) {
            return this.signature;
        }
        return this.getSignature();
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + "[description=" + this.getDescription() + ", name=" + this.getName() + ", signature=" + Arrays.asList(this.fastGetSignature()) + ", descriptor=" + this.getDescriptor() + "]";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof MBeanConstructorInfo)) {
            return false;
        }
        final MBeanConstructorInfo mBeanConstructorInfo = (MBeanConstructorInfo)o;
        return Objects.equals(mBeanConstructorInfo.getName(), this.getName()) && Objects.equals(mBeanConstructorInfo.getDescription(), this.getDescription()) && Arrays.equals(mBeanConstructorInfo.fastGetSignature(), this.fastGetSignature()) && Objects.equals(mBeanConstructorInfo.getDescriptor(), this.getDescriptor());
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.getName()) ^ Arrays.hashCode(this.fastGetSignature());
    }
    
    private static MBeanParameterInfo[] constructorSignature(final Constructor<?> constructor) {
        return MBeanOperationInfo.parameters(constructor.getParameterTypes(), constructor.getParameterAnnotations());
    }
    
    static {
        NO_CONSTRUCTORS = new MBeanConstructorInfo[0];
    }
}
