package sun.reflect.annotation;

import sun.misc.Unsafe;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.security.AccessController;
import java.lang.reflect.AccessibleObject;
import java.security.PrivilegedAction;
import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Iterator;
import java.lang.reflect.Array;
import java.lang.annotation.IncompleteAnnotationException;
import java.lang.annotation.AnnotationFormatError;
import java.lang.reflect.Method;
import java.util.Map;
import java.lang.annotation.Annotation;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;

class AnnotationInvocationHandler implements InvocationHandler, Serializable
{
    private static final long serialVersionUID = 6182022883658399397L;
    private final Class<? extends Annotation> type;
    private final Map<String, Object> memberValues;
    private transient volatile Method[] memberMethods;
    
    AnnotationInvocationHandler(final Class<? extends Annotation> type, final Map<String, Object> memberValues) {
        this.memberMethods = null;
        final Class<?>[] interfaces = type.getInterfaces();
        if (!type.isAnnotation() || interfaces.length != 1 || interfaces[0] != Annotation.class) {
            throw new AnnotationFormatError("Attempt to create proxy for a non-annotation type.");
        }
        this.type = type;
        this.memberValues = memberValues;
    }
    
    @Override
    public Object invoke(final Object o, final Method method, final Object[] array) {
        final String name = method.getName();
        final Class<?>[] parameterTypes = method.getParameterTypes();
        if (name.equals("equals") && parameterTypes.length == 1 && parameterTypes[0] == Object.class) {
            return this.equalsImpl(array[0]);
        }
        if (parameterTypes.length != 0) {
            throw new AssertionError((Object)"Too many parameters for an annotation method");
        }
        final String s = name;
        switch (s) {
            case "toString": {
                return this.toStringImpl();
            }
            case "hashCode": {
                return this.hashCodeImpl();
            }
            case "annotationType": {
                return this.type;
            }
            default: {
                Object o2 = this.memberValues.get(name);
                if (o2 == null) {
                    throw new IncompleteAnnotationException(this.type, name);
                }
                if (o2 instanceof ExceptionProxy) {
                    throw ((ExceptionProxy)o2).generateException();
                }
                if (((ExceptionProxy)o2).getClass().isArray() && Array.getLength(o2) != 0) {
                    o2 = this.cloneArray(o2);
                }
                return o2;
            }
        }
    }
    
    private Object cloneArray(final Object o) {
        final Class<?> class1 = o.getClass();
        if (class1 == byte[].class) {
            return ((byte[])o).clone();
        }
        if (class1 == char[].class) {
            return ((char[])o).clone();
        }
        if (class1 == double[].class) {
            return ((double[])o).clone();
        }
        if (class1 == float[].class) {
            return ((float[])o).clone();
        }
        if (class1 == int[].class) {
            return ((int[])o).clone();
        }
        if (class1 == long[].class) {
            return ((long[])o).clone();
        }
        if (class1 == short[].class) {
            return ((short[])o).clone();
        }
        if (class1 == boolean[].class) {
            return ((boolean[])o).clone();
        }
        return ((Object[])o).clone();
    }
    
    private String toStringImpl() {
        final StringBuilder sb = new StringBuilder(128);
        sb.append('@');
        sb.append(this.type.getName());
        sb.append('(');
        int n = 1;
        for (final Map.Entry entry : this.memberValues.entrySet()) {
            if (n != 0) {
                n = 0;
            }
            else {
                sb.append(", ");
            }
            sb.append((String)entry.getKey());
            sb.append('=');
            sb.append(memberValueToString(entry.getValue()));
        }
        sb.append(')');
        return sb.toString();
    }
    
    private static String memberValueToString(final Object o) {
        final Class<?> class1 = o.getClass();
        if (!class1.isArray()) {
            return o.toString();
        }
        if (class1 == byte[].class) {
            return Arrays.toString((byte[])o);
        }
        if (class1 == char[].class) {
            return Arrays.toString((char[])o);
        }
        if (class1 == double[].class) {
            return Arrays.toString((double[])o);
        }
        if (class1 == float[].class) {
            return Arrays.toString((float[])o);
        }
        if (class1 == int[].class) {
            return Arrays.toString((int[])o);
        }
        if (class1 == long[].class) {
            return Arrays.toString((long[])o);
        }
        if (class1 == short[].class) {
            return Arrays.toString((short[])o);
        }
        if (class1 == boolean[].class) {
            return Arrays.toString((boolean[])o);
        }
        return Arrays.toString((Object[])o);
    }
    
    private Boolean equalsImpl(final Object o) {
        if (o == this) {
            return true;
        }
        if (!this.type.isInstance(o)) {
            return false;
        }
        for (final Method method : this.getMemberMethods()) {
            final String name = method.getName();
            final Object value = this.memberValues.get(name);
            final AnnotationInvocationHandler oneOfUs = this.asOneOfUs(o);
            Object o2;
            if (oneOfUs != null) {
                o2 = oneOfUs.memberValues.get(name);
            }
            else {
                try {
                    o2 = method.invoke(o, new Object[0]);
                }
                catch (final InvocationTargetException ex) {
                    return false;
                }
                catch (final IllegalAccessException ex2) {
                    throw new AssertionError((Object)ex2);
                }
            }
            if (!memberValueEquals(value, o2)) {
                return false;
            }
        }
        return true;
    }
    
    private AnnotationInvocationHandler asOneOfUs(final Object o) {
        if (Proxy.isProxyClass(o.getClass())) {
            final InvocationHandler invocationHandler = Proxy.getInvocationHandler(o);
            if (invocationHandler instanceof AnnotationInvocationHandler) {
                return (AnnotationInvocationHandler)invocationHandler;
            }
        }
        return null;
    }
    
    private static boolean memberValueEquals(final Object o, final Object o2) {
        final Class<?> class1 = o.getClass();
        if (!class1.isArray()) {
            return o.equals(o2);
        }
        if (o instanceof Object[] && o2 instanceof Object[]) {
            return Arrays.equals((Object[])o, (Object[])o2);
        }
        if (o2.getClass() != class1) {
            return false;
        }
        if (class1 == byte[].class) {
            return Arrays.equals((byte[])o, (byte[])o2);
        }
        if (class1 == char[].class) {
            return Arrays.equals((char[])o, (char[])o2);
        }
        if (class1 == double[].class) {
            return Arrays.equals((double[])o, (double[])o2);
        }
        if (class1 == float[].class) {
            return Arrays.equals((float[])o, (float[])o2);
        }
        if (class1 == int[].class) {
            return Arrays.equals((int[])o, (int[])o2);
        }
        if (class1 == long[].class) {
            return Arrays.equals((long[])o, (long[])o2);
        }
        if (class1 == short[].class) {
            return Arrays.equals((short[])o, (short[])o2);
        }
        assert class1 == boolean[].class;
        return Arrays.equals((boolean[])o, (boolean[])o2);
    }
    
    private Method[] getMemberMethods() {
        if (this.memberMethods == null) {
            this.memberMethods = AccessController.doPrivileged((PrivilegedAction<Method[]>)new PrivilegedAction<Method[]>() {
                @Override
                public Method[] run() {
                    final Method[] declaredMethods = AnnotationInvocationHandler.this.type.getDeclaredMethods();
                    AnnotationInvocationHandler.this.validateAnnotationMethods(declaredMethods);
                    AccessibleObject.setAccessible(declaredMethods, true);
                    return declaredMethods;
                }
            });
        }
        return this.memberMethods;
    }
    
    private void validateAnnotationMethods(final Method[] array) {
        boolean b = true;
        for (final Method method : array) {
            if (method.getModifiers() != 1025 || method.isDefault() || method.getParameterCount() != 0 || method.getExceptionTypes().length != 0) {
                b = false;
                break;
            }
            Class<?> clazz = method.getReturnType();
            if (clazz.isArray()) {
                clazz = clazz.getComponentType();
                if (clazz.isArray()) {
                    b = false;
                    break;
                }
            }
            if ((!clazz.isPrimitive() || clazz == Void.TYPE) && clazz != String.class && clazz != Class.class && !clazz.isEnum() && !clazz.isAnnotation()) {
                b = false;
                break;
            }
            final String name = method.getName();
            if ((name.equals("toString") && clazz == String.class) || (name.equals("hashCode") && clazz == Integer.TYPE) || (name.equals("annotationType") && clazz == Class.class)) {
                b = false;
                break;
            }
        }
        if (b) {
            return;
        }
        throw new AnnotationFormatError("Malformed method on an annotation type");
    }
    
    private int hashCodeImpl() {
        int n = 0;
        for (final Map.Entry entry : this.memberValues.entrySet()) {
            n += (127 * ((String)entry.getKey()).hashCode() ^ memberValueHashCode(entry.getValue()));
        }
        return n;
    }
    
    private static int memberValueHashCode(final Object o) {
        final Class<?> class1 = o.getClass();
        if (!class1.isArray()) {
            return o.hashCode();
        }
        if (class1 == byte[].class) {
            return Arrays.hashCode((byte[])o);
        }
        if (class1 == char[].class) {
            return Arrays.hashCode((char[])o);
        }
        if (class1 == double[].class) {
            return Arrays.hashCode((double[])o);
        }
        if (class1 == float[].class) {
            return Arrays.hashCode((float[])o);
        }
        if (class1 == int[].class) {
            return Arrays.hashCode((int[])o);
        }
        if (class1 == long[].class) {
            return Arrays.hashCode((long[])o);
        }
        if (class1 == short[].class) {
            return Arrays.hashCode((short[])o);
        }
        if (class1 == boolean[].class) {
            return Arrays.hashCode((boolean[])o);
        }
        return Arrays.hashCode((Object[])o);
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        final ObjectInputStream.GetField fields = objectInputStream.readFields();
        final Class clazz = (Class)fields.get("type", null);
        final Map map = (Map)fields.get("memberValues", null);
        AnnotationType instance;
        try {
            instance = AnnotationType.getInstance(clazz);
        }
        catch (final IllegalArgumentException ex) {
            throw new InvalidObjectException("Non-annotation type in annotation serial stream");
        }
        final Map<String, Class<?>> memberTypes = instance.memberTypes();
        final LinkedHashMap linkedHashMap = new LinkedHashMap();
        for (final Map.Entry entry : map.entrySet()) {
            final String s = (String)entry.getKey();
            Object o = null;
            final Class clazz2 = memberTypes.get(s);
            if (clazz2 != null) {
                o = entry.getValue();
                if (!clazz2.isInstance(o) && !(o instanceof ExceptionProxy)) {
                    o = new AnnotationTypeMismatchExceptionProxy(o.getClass() + "[" + o + "]").setMember(instance.members().get(s));
                }
            }
            linkedHashMap.put(s, o);
        }
        UnsafeAccessor.setType(this, clazz);
        UnsafeAccessor.setMemberValues(this, linkedHashMap);
    }
    
    private static class UnsafeAccessor
    {
        private static final Unsafe unsafe;
        private static final long typeOffset;
        private static final long memberValuesOffset;
        
        static void setType(final AnnotationInvocationHandler annotationInvocationHandler, final Class<? extends Annotation> clazz) {
            UnsafeAccessor.unsafe.putObject(annotationInvocationHandler, UnsafeAccessor.typeOffset, clazz);
        }
        
        static void setMemberValues(final AnnotationInvocationHandler annotationInvocationHandler, final Map<String, Object> map) {
            UnsafeAccessor.unsafe.putObject(annotationInvocationHandler, UnsafeAccessor.memberValuesOffset, map);
        }
        
        static {
            try {
                unsafe = Unsafe.getUnsafe();
                typeOffset = UnsafeAccessor.unsafe.objectFieldOffset(AnnotationInvocationHandler.class.getDeclaredField("type"));
                memberValuesOffset = UnsafeAccessor.unsafe.objectFieldOffset(AnnotationInvocationHandler.class.getDeclaredField("memberValues"));
            }
            catch (final Exception ex) {
                throw new ExceptionInInitializerError(ex);
            }
        }
    }
}
