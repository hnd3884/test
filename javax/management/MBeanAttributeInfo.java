package javax.management;

import java.security.PrivilegedAction;
import java.security.AccessController;
import com.sun.jmx.mbeanserver.GetPropertyAction;
import java.util.Objects;
import java.lang.reflect.AnnotatedElement;
import com.sun.jmx.mbeanserver.Introspector;
import java.lang.reflect.Method;

public class MBeanAttributeInfo extends MBeanFeatureInfo implements Cloneable
{
    private static final long serialVersionUID;
    static final MBeanAttributeInfo[] NO_ATTRIBUTES;
    private final String attributeType;
    private final boolean isWrite;
    private final boolean isRead;
    private final boolean is;
    
    public MBeanAttributeInfo(final String s, final String s2, final String s3, final boolean b, final boolean b2, final boolean b3) {
        this(s, s2, s3, b, b2, b3, null);
    }
    
    public MBeanAttributeInfo(final String s, final String attributeType, final String s2, final boolean isRead, final boolean isWrite, final boolean is, final Descriptor descriptor) {
        super(s, s2, descriptor);
        this.attributeType = attributeType;
        this.isRead = isRead;
        this.isWrite = isWrite;
        if (is && !isRead) {
            throw new IllegalArgumentException("Cannot have an \"is\" getter for a non-readable attribute");
        }
        if (is && !attributeType.equals("java.lang.Boolean") && !attributeType.equals("boolean")) {
            throw new IllegalArgumentException("Cannot have an \"is\" getter for a non-boolean attribute");
        }
        this.is = is;
    }
    
    public MBeanAttributeInfo(final String s, final String s2, final Method method, final Method method2) throws IntrospectionException {
        this(s, attributeType(method, method2), s2, method != null, method2 != null, isIs(method), ImmutableDescriptor.union(Introspector.descriptorForElement(method), Introspector.descriptorForElement(method2)));
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
        return this.attributeType;
    }
    
    public boolean isReadable() {
        return this.isRead;
    }
    
    public boolean isWritable() {
        return this.isWrite;
    }
    
    public boolean isIs() {
        return this.is;
    }
    
    @Override
    public String toString() {
        String s;
        if (this.isReadable()) {
            if (this.isWritable()) {
                s = "read/write";
            }
            else {
                s = "read-only";
            }
        }
        else if (this.isWritable()) {
            s = "write-only";
        }
        else {
            s = "no-access";
        }
        return this.getClass().getName() + "[description=" + this.getDescription() + ", name=" + this.getName() + ", type=" + this.getType() + ", " + s + ", " + (this.isIs() ? "isIs, " : "") + "descriptor=" + this.getDescriptor() + "]";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof MBeanAttributeInfo)) {
            return false;
        }
        final MBeanAttributeInfo mBeanAttributeInfo = (MBeanAttributeInfo)o;
        return Objects.equals(mBeanAttributeInfo.getName(), this.getName()) && Objects.equals(mBeanAttributeInfo.getType(), this.getType()) && Objects.equals(mBeanAttributeInfo.getDescription(), this.getDescription()) && Objects.equals(mBeanAttributeInfo.getDescriptor(), this.getDescriptor()) && mBeanAttributeInfo.isReadable() == this.isReadable() && mBeanAttributeInfo.isWritable() == this.isWritable() && mBeanAttributeInfo.isIs() == this.isIs();
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.getName(), this.getType());
    }
    
    private static boolean isIs(final Method method) {
        return method != null && method.getName().startsWith("is") && (method.getReturnType().equals(Boolean.TYPE) || method.getReturnType().equals(Boolean.class));
    }
    
    private static String attributeType(final Method method, final Method method2) throws IntrospectionException {
        Class<?> returnType = null;
        if (method != null) {
            if (method.getParameterTypes().length != 0) {
                throw new IntrospectionException("bad getter arg count");
            }
            returnType = method.getReturnType();
            if (returnType == Void.TYPE) {
                throw new IntrospectionException("getter " + method.getName() + " returns void");
            }
        }
        if (method2 != null) {
            final Class<?>[] parameterTypes = method2.getParameterTypes();
            if (parameterTypes.length != 1) {
                throw new IntrospectionException("bad setter arg count");
            }
            if (returnType == null) {
                returnType = parameterTypes[0];
            }
            else if (returnType != parameterTypes[0]) {
                throw new IntrospectionException("type mismatch between getter and setter");
            }
        }
        if (returnType == null) {
            throw new IntrospectionException("getter and setter cannot both be null");
        }
        return returnType.getName();
    }
    
    static {
        long serialVersionUID2 = 8644704819898565848L;
        try {
            if ("1.0".equals(AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("jmx.serial.form")))) {
                serialVersionUID2 = 7043855487133450673L;
            }
        }
        catch (final Exception ex) {}
        serialVersionUID = serialVersionUID2;
        NO_ATTRIBUTES = new MBeanAttributeInfo[0];
    }
}
