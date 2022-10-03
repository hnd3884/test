package javax.management;

import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.Arrays;
import java.lang.reflect.AnnotatedElement;
import com.sun.jmx.mbeanserver.Introspector;
import java.lang.reflect.Method;

public class MBeanOperationInfo extends MBeanFeatureInfo implements Cloneable
{
    static final long serialVersionUID = -6178860474881375330L;
    static final MBeanOperationInfo[] NO_OPERATIONS;
    public static final int INFO = 0;
    public static final int ACTION = 1;
    public static final int ACTION_INFO = 2;
    public static final int UNKNOWN = 3;
    private final String type;
    private final MBeanParameterInfo[] signature;
    private final int impact;
    private final transient boolean arrayGettersSafe;
    
    public MBeanOperationInfo(final String s, final Method method) {
        this(method.getName(), s, methodSignature(method), method.getReturnType().getName(), 3, Introspector.descriptorForElement(method));
    }
    
    public MBeanOperationInfo(final String s, final String s2, final MBeanParameterInfo[] array, final String s3, final int n) {
        this(s, s2, array, s3, n, null);
    }
    
    public MBeanOperationInfo(final String s, final String s2, MBeanParameterInfo[] no_PARAMS, final String type, final int impact, final Descriptor descriptor) {
        super(s, s2, descriptor);
        if (no_PARAMS == null || no_PARAMS.length == 0) {
            no_PARAMS = MBeanParameterInfo.NO_PARAMS;
        }
        else {
            no_PARAMS = no_PARAMS.clone();
        }
        this.signature = no_PARAMS;
        this.type = type;
        this.impact = impact;
        this.arrayGettersSafe = MBeanInfo.arrayGettersSafe(this.getClass(), MBeanOperationInfo.class);
    }
    
    public Object clone() {
        try {
            return super.clone();
        }
        catch (final CloneNotSupportedException ex) {
            return null;
        }
    }
    
    public String getReturnType() {
        return this.type;
    }
    
    public MBeanParameterInfo[] getSignature() {
        if (this.signature == null) {
            return MBeanParameterInfo.NO_PARAMS;
        }
        if (this.signature.length == 0) {
            return this.signature;
        }
        return this.signature.clone();
    }
    
    private MBeanParameterInfo[] fastGetSignature() {
        if (!this.arrayGettersSafe) {
            return this.getSignature();
        }
        if (this.signature == null) {
            return MBeanParameterInfo.NO_PARAMS;
        }
        return this.signature;
    }
    
    public int getImpact() {
        return this.impact;
    }
    
    @Override
    public String toString() {
        String string = null;
        switch (this.getImpact()) {
            case 1: {
                string = "action";
                break;
            }
            case 2: {
                string = "action/info";
                break;
            }
            case 0: {
                string = "info";
                break;
            }
            case 3: {
                string = "unknown";
                break;
            }
            default: {
                string = "(" + this.getImpact() + ")";
                break;
            }
        }
        return this.getClass().getName() + "[description=" + this.getDescription() + ", name=" + this.getName() + ", returnType=" + this.getReturnType() + ", signature=" + Arrays.asList(this.fastGetSignature()) + ", impact=" + string + ", descriptor=" + this.getDescriptor() + "]";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof MBeanOperationInfo)) {
            return false;
        }
        final MBeanOperationInfo mBeanOperationInfo = (MBeanOperationInfo)o;
        return Objects.equals(mBeanOperationInfo.getName(), this.getName()) && Objects.equals(mBeanOperationInfo.getReturnType(), this.getReturnType()) && Objects.equals(mBeanOperationInfo.getDescription(), this.getDescription()) && mBeanOperationInfo.getImpact() == this.getImpact() && Arrays.equals(mBeanOperationInfo.fastGetSignature(), this.fastGetSignature()) && Objects.equals(mBeanOperationInfo.getDescriptor(), this.getDescriptor());
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.getName(), this.getReturnType());
    }
    
    private static MBeanParameterInfo[] methodSignature(final Method method) {
        return parameters(method.getParameterTypes(), method.getParameterAnnotations());
    }
    
    static MBeanParameterInfo[] parameters(final Class<?>[] array, final Annotation[][] array2) {
        final MBeanParameterInfo[] array3 = new MBeanParameterInfo[array.length];
        assert array.length == array2.length;
        for (int i = 0; i < array.length; ++i) {
            array3[i] = new MBeanParameterInfo("p" + (i + 1), array[i].getName(), "", Introspector.descriptorForAnnotations(array2[i]));
        }
        return array3;
    }
    
    static {
        NO_OPERATIONS = new MBeanOperationInfo[0];
    }
}
