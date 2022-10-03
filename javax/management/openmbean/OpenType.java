package javax.management.openmbean;

import java.util.Collections;
import java.util.Arrays;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import javax.management.ImmutableDescriptor;
import java.security.PrivilegedAction;
import java.security.AccessController;
import com.sun.jmx.mbeanserver.GetPropertyAction;
import javax.management.Descriptor;
import java.util.List;
import java.io.Serializable;

public abstract class OpenType<T> implements Serializable
{
    static final long serialVersionUID = -9195195325186646468L;
    public static final List<String> ALLOWED_CLASSNAMES_LIST;
    @Deprecated
    public static final String[] ALLOWED_CLASSNAMES;
    private String className;
    private String description;
    private String typeName;
    private transient boolean isArray;
    private transient Descriptor descriptor;
    
    protected OpenType(final String s, final String s2, final String s3) throws OpenDataException {
        this.isArray = false;
        this.checkClassNameOverride();
        this.typeName = valid("typeName", s2);
        this.description = valid("description", s3);
        this.className = validClassName(s);
        this.isArray = (this.className != null && this.className.startsWith("["));
    }
    
    OpenType(final String s, final String s2, final String s3, final boolean isArray) {
        this.isArray = false;
        this.className = valid("className", s);
        this.typeName = valid("typeName", s2);
        this.description = valid("description", s3);
        this.isArray = isArray;
    }
    
    private void checkClassNameOverride() throws SecurityException {
        if (this.getClass().getClassLoader() == null) {
            return;
        }
        if (overridesGetClassName(this.getClass()) && AccessController.doPrivileged((PrivilegedAction<Object>)new GetPropertyAction("jmx.extend.open.types")) == null) {
            throw new SecurityException("Cannot override getClassName() unless -Djmx.extend.open.types");
        }
    }
    
    private static boolean overridesGetClassName(final Class<?> clazz) {
        return AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                try {
                    return clazz.getMethod("getClassName", (Class[])new Class[0]).getDeclaringClass() != OpenType.class;
                }
                catch (final Exception ex) {
                    return true;
                }
            }
        });
    }
    
    private static String validClassName(String valid) throws OpenDataException {
        int n;
        for (valid = valid("className", valid), n = 0; valid.startsWith("[", n); ++n) {}
        boolean b = false;
        String s;
        if (n > 0) {
            if (valid.startsWith("L", n) && valid.endsWith(";")) {
                s = valid.substring(n + 1, valid.length() - 1);
            }
            else {
                if (n != valid.length() - 1) {
                    throw new OpenDataException("Argument className=\"" + valid + "\" is not a valid class name");
                }
                s = valid.substring(n, valid.length());
                b = true;
            }
        }
        else {
            s = valid;
        }
        boolean b2;
        if (b) {
            b2 = ArrayType.isPrimitiveContentType(s);
        }
        else {
            b2 = OpenType.ALLOWED_CLASSNAMES_LIST.contains(s);
        }
        if (!b2) {
            throw new OpenDataException("Argument className=\"" + valid + "\" is not one of the allowed Java class names for open data.");
        }
        return valid;
    }
    
    private static String valid(final String s, String trim) {
        if (trim == null || (trim = trim.trim()).equals("")) {
            throw new IllegalArgumentException("Argument " + s + " cannot be null or empty");
        }
        return trim;
    }
    
    synchronized Descriptor getDescriptor() {
        if (this.descriptor == null) {
            this.descriptor = new ImmutableDescriptor(new String[] { "openType" }, new Object[] { this });
        }
        return this.descriptor;
    }
    
    public String getClassName() {
        return this.className;
    }
    
    String safeGetClassName() {
        return this.className;
    }
    
    public String getTypeName() {
        return this.typeName;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public boolean isArray() {
        return this.isArray;
    }
    
    public abstract boolean isValue(final Object p0);
    
    boolean isAssignableFrom(final OpenType<?> openType) {
        return this.equals(openType);
    }
    
    @Override
    public abstract boolean equals(final Object p0);
    
    @Override
    public abstract int hashCode();
    
    @Override
    public abstract String toString();
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        this.checkClassNameOverride();
        final ObjectInputStream.GetField fields = objectInputStream.readFields();
        String validClassName;
        String valid;
        String valid2;
        try {
            validClassName = validClassName((String)fields.get("className", null));
            valid = valid("description", (String)fields.get("description", null));
            valid2 = valid("typeName", (String)fields.get("typeName", null));
        }
        catch (final Exception ex) {
            final InvalidObjectException ex2 = new InvalidObjectException(ex.getMessage());
            ex2.initCause(ex);
            throw ex2;
        }
        this.className = validClassName;
        this.description = valid;
        this.typeName = valid2;
        this.isArray = this.className.startsWith("[");
    }
    
    static {
        ALLOWED_CLASSNAMES_LIST = Collections.unmodifiableList((List<? extends String>)Arrays.asList("java.lang.Void", "java.lang.Boolean", "java.lang.Character", "java.lang.Byte", "java.lang.Short", "java.lang.Integer", "java.lang.Long", "java.lang.Float", "java.lang.Double", "java.lang.String", "java.math.BigDecimal", "java.math.BigInteger", "java.util.Date", "javax.management.ObjectName", CompositeData.class.getName(), TabularData.class.getName()));
        ALLOWED_CLASSNAMES = OpenType.ALLOWED_CLASSNAMES_LIST.toArray(new String[0]);
    }
}
