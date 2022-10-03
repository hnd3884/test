package sun.reflect.annotation;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import sun.reflect.generics.tree.TypeSignature;
import sun.reflect.generics.visitor.TypeTreeVisitor;
import sun.reflect.generics.factory.GenericsFactory;
import sun.reflect.generics.visitor.Reifier;
import sun.reflect.generics.scope.Scope;
import java.lang.reflect.GenericDeclaration;
import sun.reflect.generics.factory.CoreReflectionFactory;
import sun.reflect.generics.scope.ClassScope;
import sun.reflect.generics.parser.SignatureParser;
import java.security.AccessController;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.security.PrivilegedAction;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.lang.annotation.RetentionPolicy;
import java.nio.ByteBuffer;
import java.util.LinkedHashMap;
import java.nio.BufferUnderflowException;
import java.lang.annotation.AnnotationFormatError;
import java.util.Collections;
import java.util.Map;
import sun.reflect.ConstantPool;
import java.lang.annotation.Annotation;

public class AnnotationParser
{
    private static final Annotation[] EMPTY_ANNOTATIONS_ARRAY;
    private static final Annotation[] EMPTY_ANNOTATION_ARRAY;
    
    public static Map<Class<? extends Annotation>, Annotation> parseAnnotations(final byte[] array, final ConstantPool constantPool, final Class<?> clazz) {
        if (array == null) {
            return Collections.emptyMap();
        }
        try {
            return parseAnnotations2(array, constantPool, clazz, null);
        }
        catch (final BufferUnderflowException ex) {
            throw new AnnotationFormatError("Unexpected end of annotations.");
        }
        catch (final IllegalArgumentException ex2) {
            throw new AnnotationFormatError(ex2);
        }
    }
    
    @SafeVarargs
    static Map<Class<? extends Annotation>, Annotation> parseSelectAnnotations(final byte[] array, final ConstantPool constantPool, final Class<?> clazz, final Class<? extends Annotation>... array2) {
        if (array == null) {
            return Collections.emptyMap();
        }
        try {
            return parseAnnotations2(array, constantPool, clazz, array2);
        }
        catch (final BufferUnderflowException ex) {
            throw new AnnotationFormatError("Unexpected end of annotations.");
        }
        catch (final IllegalArgumentException ex2) {
            throw new AnnotationFormatError(ex2);
        }
    }
    
    private static Map<Class<? extends Annotation>, Annotation> parseAnnotations2(final byte[] array, final ConstantPool constantPool, final Class<?> clazz, final Class<? extends Annotation>[] array2) {
        final LinkedHashMap linkedHashMap = new LinkedHashMap();
        final ByteBuffer wrap = ByteBuffer.wrap(array);
        for (int n = wrap.getShort() & 0xFFFF, i = 0; i < n; ++i) {
            final Annotation annotation2 = parseAnnotation2(wrap, constantPool, clazz, false, array2);
            if (annotation2 != null) {
                final Class<? extends Annotation> annotationType = annotation2.annotationType();
                if (AnnotationType.getInstance(annotationType).retention() == RetentionPolicy.RUNTIME && linkedHashMap.put(annotationType, annotation2) != null) {
                    throw new AnnotationFormatError("Duplicate annotation for class: " + annotationType + ": " + annotation2);
                }
            }
        }
        return linkedHashMap;
    }
    
    public static Annotation[][] parseParameterAnnotations(final byte[] array, final ConstantPool constantPool, final Class<?> clazz) {
        try {
            return parseParameterAnnotations2(array, constantPool, clazz);
        }
        catch (final BufferUnderflowException ex) {
            throw new AnnotationFormatError("Unexpected end of parameter annotations.");
        }
        catch (final IllegalArgumentException ex2) {
            throw new AnnotationFormatError(ex2);
        }
    }
    
    private static Annotation[][] parseParameterAnnotations2(final byte[] array, final ConstantPool constantPool, final Class<?> clazz) {
        final ByteBuffer wrap = ByteBuffer.wrap(array);
        final int n = wrap.get() & 0xFF;
        final Annotation[][] array2 = new Annotation[n][];
        for (int i = 0; i < n; ++i) {
            final int n2 = wrap.getShort() & 0xFFFF;
            final ArrayList list = new ArrayList(n2);
            for (int j = 0; j < n2; ++j) {
                final Annotation annotation = parseAnnotation(wrap, constantPool, clazz, false);
                if (annotation != null && AnnotationType.getInstance(annotation.annotationType()).retention() == RetentionPolicy.RUNTIME) {
                    list.add((Object)annotation);
                }
            }
            array2[i] = (Annotation[])list.toArray((Object[])AnnotationParser.EMPTY_ANNOTATIONS_ARRAY);
        }
        return array2;
    }
    
    static Annotation parseAnnotation(final ByteBuffer byteBuffer, final ConstantPool constantPool, final Class<?> clazz, final boolean b) {
        return parseAnnotation2(byteBuffer, constantPool, clazz, b, null);
    }
    
    private static Annotation parseAnnotation2(final ByteBuffer byteBuffer, final ConstantPool constantPool, final Class<?> clazz, final boolean b, final Class<? extends Annotation>[] array) {
        final int n = byteBuffer.getShort() & 0xFFFF;
        String utf8At = "[unknown]";
        Class<?> clazz2;
        try {
            try {
                utf8At = constantPool.getUTF8At(n);
                clazz2 = parseSig(utf8At, clazz);
            }
            catch (final IllegalArgumentException ex) {
                clazz2 = constantPool.getClassAt(n);
            }
        }
        catch (final NoClassDefFoundError noClassDefFoundError) {
            if (b) {
                throw new TypeNotPresentException(utf8At, noClassDefFoundError);
            }
            skipAnnotation(byteBuffer, false);
            return null;
        }
        catch (final TypeNotPresentException ex2) {
            if (b) {
                throw ex2;
            }
            skipAnnotation(byteBuffer, false);
            return null;
        }
        if (array != null && !contains(array, clazz2)) {
            skipAnnotation(byteBuffer, false);
            return null;
        }
        AnnotationType instance;
        try {
            instance = AnnotationType.getInstance((Class<? extends Annotation>)clazz2);
        }
        catch (final IllegalArgumentException ex3) {
            skipAnnotation(byteBuffer, false);
            return null;
        }
        final Map<String, Class<?>> memberTypes = instance.memberTypes();
        final LinkedHashMap linkedHashMap = new LinkedHashMap(instance.memberDefaults());
        for (int n2 = byteBuffer.getShort() & 0xFFFF, i = 0; i < n2; ++i) {
            final String utf8At2 = constantPool.getUTF8At(byteBuffer.getShort() & 0xFFFF);
            final Class clazz3 = memberTypes.get(utf8At2);
            if (clazz3 == null) {
                skipMemberValue(byteBuffer);
            }
            else {
                final Object memberValue = parseMemberValue(clazz3, byteBuffer, constantPool, clazz);
                if (memberValue instanceof AnnotationTypeMismatchExceptionProxy) {
                    ((AnnotationTypeMismatchExceptionProxy)memberValue).setMember(instance.members().get(utf8At2));
                }
                linkedHashMap.put((Object)utf8At2, memberValue);
            }
        }
        return annotationForMap((Class<? extends Annotation>)clazz2, (Map<String, Object>)linkedHashMap);
    }
    
    public static Annotation annotationForMap(final Class<? extends Annotation> clazz, final Map<String, Object> map) {
        return AccessController.doPrivileged((PrivilegedAction<Annotation>)new PrivilegedAction<Annotation>() {
            @Override
            public Annotation run() {
                return (Annotation)Proxy.newProxyInstance(clazz.getClassLoader(), new Class[] { clazz }, new AnnotationInvocationHandler(clazz, map));
            }
        });
    }
    
    public static Object parseMemberValue(final Class<?> clazz, final ByteBuffer byteBuffer, final ConstantPool constantPool, final Class<?> clazz2) {
        final byte value = byteBuffer.get();
        Object o = null;
        switch (value) {
            case 101: {
                return parseEnumValue((Class<? extends Enum>)clazz, byteBuffer, constantPool, clazz2);
            }
            case 99: {
                o = parseClassValue(byteBuffer, constantPool, clazz2);
                break;
            }
            case 64: {
                o = parseAnnotation(byteBuffer, constantPool, clazz2, true);
                break;
            }
            case 91: {
                return parseArray(clazz, byteBuffer, constantPool, clazz2);
            }
            default: {
                o = parseConst(value, byteBuffer, constantPool);
                break;
            }
        }
        if (!(o instanceof ExceptionProxy) && !clazz.isInstance(o)) {
            o = new AnnotationTypeMismatchExceptionProxy(o.getClass() + "[" + o + "]");
        }
        return o;
    }
    
    private static Object parseConst(final int n, final ByteBuffer byteBuffer, final ConstantPool constantPool) {
        final int n2 = byteBuffer.getShort() & 0xFFFF;
        switch (n) {
            case 66: {
                return (byte)constantPool.getIntAt(n2);
            }
            case 67: {
                return (char)constantPool.getIntAt(n2);
            }
            case 68: {
                return constantPool.getDoubleAt(n2);
            }
            case 70: {
                return constantPool.getFloatAt(n2);
            }
            case 73: {
                return constantPool.getIntAt(n2);
            }
            case 74: {
                return constantPool.getLongAt(n2);
            }
            case 83: {
                return (short)constantPool.getIntAt(n2);
            }
            case 90: {
                return constantPool.getIntAt(n2) != 0;
            }
            case 115: {
                return constantPool.getUTF8At(n2);
            }
            default: {
                throw new AnnotationFormatError("Invalid member-value tag in annotation: " + n);
            }
        }
    }
    
    private static Object parseClassValue(final ByteBuffer byteBuffer, final ConstantPool constantPool, final Class<?> clazz) {
        final int n = byteBuffer.getShort() & 0xFFFF;
        try {
            try {
                return parseSig(constantPool.getUTF8At(n), clazz);
            }
            catch (final IllegalArgumentException ex) {
                return constantPool.getClassAt(n);
            }
        }
        catch (final NoClassDefFoundError noClassDefFoundError) {
            return new TypeNotPresentExceptionProxy("[unknown]", noClassDefFoundError);
        }
        catch (final TypeNotPresentException ex2) {
            return new TypeNotPresentExceptionProxy(ex2.typeName(), ex2.getCause());
        }
    }
    
    private static Class<?> parseSig(final String s, final Class<?> clazz) {
        if (s.equals("V")) {
            return Void.TYPE;
        }
        final TypeSignature typeSig = SignatureParser.make().parseTypeSig(s);
        final Reifier make = Reifier.make(CoreReflectionFactory.make(clazz, ClassScope.make(clazz)));
        typeSig.accept(make);
        return toClass(make.getResult());
    }
    
    static Class<?> toClass(final Type type) {
        if (type instanceof GenericArrayType) {
            return Array.newInstance(toClass(((GenericArrayType)type).getGenericComponentType()), 0).getClass();
        }
        return (Class)type;
    }
    
    private static Object parseEnumValue(final Class<? extends Enum> clazz, final ByteBuffer byteBuffer, final ConstantPool constantPool, final Class<?> clazz2) {
        final String utf8At = constantPool.getUTF8At(byteBuffer.getShort() & 0xFFFF);
        final String utf8At2 = constantPool.getUTF8At(byteBuffer.getShort() & 0xFFFF);
        if (!utf8At.endsWith(";")) {
            if (!clazz.getName().equals(utf8At)) {
                return new AnnotationTypeMismatchExceptionProxy(utf8At + "." + utf8At2);
            }
        }
        else if (clazz != parseSig(utf8At, clazz2)) {
            return new AnnotationTypeMismatchExceptionProxy(utf8At + "." + utf8At2);
        }
        try {
            return Enum.valueOf((Class<Object>)clazz, utf8At2);
        }
        catch (final IllegalArgumentException ex) {
            return new EnumConstantNotPresentExceptionProxy(clazz, utf8At2);
        }
    }
    
    private static Object parseArray(final Class<?> clazz, final ByteBuffer byteBuffer, final ConstantPool constantPool, final Class<?> clazz2) {
        final int n = byteBuffer.getShort() & 0xFFFF;
        final Class<?> componentType = clazz.getComponentType();
        if (componentType == Byte.TYPE) {
            return parseByteArray(n, byteBuffer, constantPool);
        }
        if (componentType == Character.TYPE) {
            return parseCharArray(n, byteBuffer, constantPool);
        }
        if (componentType == Double.TYPE) {
            return parseDoubleArray(n, byteBuffer, constantPool);
        }
        if (componentType == Float.TYPE) {
            return parseFloatArray(n, byteBuffer, constantPool);
        }
        if (componentType == Integer.TYPE) {
            return parseIntArray(n, byteBuffer, constantPool);
        }
        if (componentType == Long.TYPE) {
            return parseLongArray(n, byteBuffer, constantPool);
        }
        if (componentType == Short.TYPE) {
            return parseShortArray(n, byteBuffer, constantPool);
        }
        if (componentType == Boolean.TYPE) {
            return parseBooleanArray(n, byteBuffer, constantPool);
        }
        if (componentType == String.class) {
            return parseStringArray(n, byteBuffer, constantPool);
        }
        if (componentType == Class.class) {
            return parseClassArray(n, byteBuffer, constantPool, clazz2);
        }
        if (componentType.isEnum()) {
            return parseEnumArray(n, (Class<? extends Enum<?>>)componentType, byteBuffer, constantPool, clazz2);
        }
        assert componentType.isAnnotation();
        return parseAnnotationArray(n, (Class<? extends Annotation>)componentType, byteBuffer, constantPool, clazz2);
    }
    
    private static Object parseByteArray(final int n, final ByteBuffer byteBuffer, final ConstantPool constantPool) {
        final byte[] array = new byte[n];
        boolean b = false;
        int value = 0;
        for (int i = 0; i < n; ++i) {
            value = byteBuffer.get();
            if (value == 66) {
                array[i] = (byte)constantPool.getIntAt(byteBuffer.getShort() & 0xFFFF);
            }
            else {
                skipMemberValue(value, byteBuffer);
                b = true;
            }
        }
        return b ? exceptionProxy(value) : array;
    }
    
    private static Object parseCharArray(final int n, final ByteBuffer byteBuffer, final ConstantPool constantPool) {
        final char[] array = new char[n];
        boolean b = false;
        int value = 0;
        for (int i = 0; i < n; ++i) {
            value = byteBuffer.get();
            if (value == 67) {
                array[i] = (char)constantPool.getIntAt(byteBuffer.getShort() & 0xFFFF);
            }
            else {
                skipMemberValue(value, byteBuffer);
                b = true;
            }
        }
        return b ? exceptionProxy(value) : array;
    }
    
    private static Object parseDoubleArray(final int n, final ByteBuffer byteBuffer, final ConstantPool constantPool) {
        final double[] array = new double[n];
        boolean b = false;
        int value = 0;
        for (int i = 0; i < n; ++i) {
            value = byteBuffer.get();
            if (value == 68) {
                array[i] = constantPool.getDoubleAt(byteBuffer.getShort() & 0xFFFF);
            }
            else {
                skipMemberValue(value, byteBuffer);
                b = true;
            }
        }
        return b ? exceptionProxy(value) : array;
    }
    
    private static Object parseFloatArray(final int n, final ByteBuffer byteBuffer, final ConstantPool constantPool) {
        final float[] array = new float[n];
        boolean b = false;
        int value = 0;
        for (int i = 0; i < n; ++i) {
            value = byteBuffer.get();
            if (value == 70) {
                array[i] = constantPool.getFloatAt(byteBuffer.getShort() & 0xFFFF);
            }
            else {
                skipMemberValue(value, byteBuffer);
                b = true;
            }
        }
        return b ? exceptionProxy(value) : array;
    }
    
    private static Object parseIntArray(final int n, final ByteBuffer byteBuffer, final ConstantPool constantPool) {
        final int[] array = new int[n];
        boolean b = false;
        int value = 0;
        for (int i = 0; i < n; ++i) {
            value = byteBuffer.get();
            if (value == 73) {
                array[i] = constantPool.getIntAt(byteBuffer.getShort() & 0xFFFF);
            }
            else {
                skipMemberValue(value, byteBuffer);
                b = true;
            }
        }
        return b ? exceptionProxy(value) : array;
    }
    
    private static Object parseLongArray(final int n, final ByteBuffer byteBuffer, final ConstantPool constantPool) {
        final long[] array = new long[n];
        boolean b = false;
        int value = 0;
        for (int i = 0; i < n; ++i) {
            value = byteBuffer.get();
            if (value == 74) {
                array[i] = constantPool.getLongAt(byteBuffer.getShort() & 0xFFFF);
            }
            else {
                skipMemberValue(value, byteBuffer);
                b = true;
            }
        }
        return b ? exceptionProxy(value) : array;
    }
    
    private static Object parseShortArray(final int n, final ByteBuffer byteBuffer, final ConstantPool constantPool) {
        final short[] array = new short[n];
        boolean b = false;
        int value = 0;
        for (int i = 0; i < n; ++i) {
            value = byteBuffer.get();
            if (value == 83) {
                array[i] = (short)constantPool.getIntAt(byteBuffer.getShort() & 0xFFFF);
            }
            else {
                skipMemberValue(value, byteBuffer);
                b = true;
            }
        }
        return b ? exceptionProxy(value) : array;
    }
    
    private static Object parseBooleanArray(final int n, final ByteBuffer byteBuffer, final ConstantPool constantPool) {
        final boolean[] array = new boolean[n];
        boolean b = false;
        int value = 0;
        for (int i = 0; i < n; ++i) {
            value = byteBuffer.get();
            if (value == 90) {
                array[i] = (constantPool.getIntAt(byteBuffer.getShort() & 0xFFFF) != 0);
            }
            else {
                skipMemberValue(value, byteBuffer);
                b = true;
            }
        }
        return b ? exceptionProxy(value) : array;
    }
    
    private static Object parseStringArray(final int n, final ByteBuffer byteBuffer, final ConstantPool constantPool) {
        final String[] array = new String[n];
        boolean b = false;
        int value = 0;
        for (int i = 0; i < n; ++i) {
            value = byteBuffer.get();
            if (value == 115) {
                array[i] = constantPool.getUTF8At(byteBuffer.getShort() & 0xFFFF);
            }
            else {
                skipMemberValue(value, byteBuffer);
                b = true;
            }
        }
        return b ? exceptionProxy(value) : array;
    }
    
    private static Object parseClassArray(final int n, final ByteBuffer byteBuffer, final ConstantPool constantPool, final Class<?> clazz) {
        final Class[] array = new Class[n];
        boolean b = false;
        int value = 0;
        for (int i = 0; i < n; ++i) {
            value = byteBuffer.get();
            if (value == 99) {
                array[i] = (Class)parseClassValue(byteBuffer, constantPool, clazz);
            }
            else {
                skipMemberValue(value, byteBuffer);
                b = true;
            }
        }
        return b ? exceptionProxy(value) : array;
    }
    
    private static Object parseEnumArray(final int n, final Class<? extends Enum<?>> clazz, final ByteBuffer byteBuffer, final ConstantPool constantPool, final Class<?> clazz2) {
        final Object[] array = (Object[])Array.newInstance(clazz, n);
        boolean b = false;
        int value = 0;
        for (int i = 0; i < n; ++i) {
            value = byteBuffer.get();
            if (value == 101) {
                array[i] = parseEnumValue(clazz, byteBuffer, constantPool, clazz2);
            }
            else {
                skipMemberValue(value, byteBuffer);
                b = true;
            }
        }
        return b ? exceptionProxy(value) : array;
    }
    
    private static Object parseAnnotationArray(final int n, final Class<? extends Annotation> clazz, final ByteBuffer byteBuffer, final ConstantPool constantPool, final Class<?> clazz2) {
        final Object[] array = (Object[])Array.newInstance(clazz, n);
        boolean b = false;
        int value = 0;
        for (int i = 0; i < n; ++i) {
            value = byteBuffer.get();
            if (value == 64) {
                array[i] = parseAnnotation(byteBuffer, constantPool, clazz2, true);
            }
            else {
                skipMemberValue(value, byteBuffer);
                b = true;
            }
        }
        return b ? exceptionProxy(value) : array;
    }
    
    private static ExceptionProxy exceptionProxy(final int n) {
        return new AnnotationTypeMismatchExceptionProxy("Array with component tag: " + n);
    }
    
    private static void skipAnnotation(final ByteBuffer byteBuffer, final boolean b) {
        if (b) {
            byteBuffer.getShort();
        }
        for (int n = byteBuffer.getShort() & 0xFFFF, i = 0; i < n; ++i) {
            byteBuffer.getShort();
            skipMemberValue(byteBuffer);
        }
    }
    
    private static void skipMemberValue(final ByteBuffer byteBuffer) {
        skipMemberValue(byteBuffer.get(), byteBuffer);
    }
    
    private static void skipMemberValue(final int n, final ByteBuffer byteBuffer) {
        switch (n) {
            case 101: {
                byteBuffer.getInt();
                break;
            }
            case 64: {
                skipAnnotation(byteBuffer, true);
                break;
            }
            case 91: {
                skipArray(byteBuffer);
                break;
            }
            default: {
                byteBuffer.getShort();
                break;
            }
        }
    }
    
    private static void skipArray(final ByteBuffer byteBuffer) {
        for (int n = byteBuffer.getShort() & 0xFFFF, i = 0; i < n; ++i) {
            skipMemberValue(byteBuffer);
        }
    }
    
    private static boolean contains(final Object[] array, final Object o) {
        for (int length = array.length, i = 0; i < length; ++i) {
            if (array[i] == o) {
                return true;
            }
        }
        return false;
    }
    
    public static Annotation[] toArray(final Map<Class<? extends Annotation>, Annotation> map) {
        return map.values().toArray(AnnotationParser.EMPTY_ANNOTATION_ARRAY);
    }
    
    static Annotation[] getEmptyAnnotationArray() {
        return AnnotationParser.EMPTY_ANNOTATION_ARRAY;
    }
    
    static {
        EMPTY_ANNOTATIONS_ARRAY = new Annotation[0];
        EMPTY_ANNOTATION_ARRAY = new Annotation[0];
    }
}
