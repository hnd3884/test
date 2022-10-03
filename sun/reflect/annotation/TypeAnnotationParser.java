package sun.reflect.annotation;

import java.nio.BufferUnderflowException;
import java.lang.annotation.AnnotationFormatError;
import java.lang.annotation.RetentionPolicy;
import java.util.LinkedHashMap;
import java.util.Map;
import java.nio.ByteBuffer;
import sun.misc.JavaLangAccess;
import sun.misc.SharedSecrets;
import java.util.Iterator;
import java.util.List;
import java.lang.reflect.Executable;
import java.lang.annotation.Annotation;
import java.lang.reflect.GenericDeclaration;
import java.util.Arrays;
import java.util.ArrayList;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Type;
import java.lang.reflect.AnnotatedElement;
import sun.reflect.ConstantPool;

public final class TypeAnnotationParser
{
    private static final TypeAnnotation[] EMPTY_TYPE_ANNOTATION_ARRAY;
    private static final byte CLASS_TYPE_PARAMETER = 0;
    private static final byte METHOD_TYPE_PARAMETER = 1;
    private static final byte CLASS_EXTENDS = 16;
    private static final byte CLASS_TYPE_PARAMETER_BOUND = 17;
    private static final byte METHOD_TYPE_PARAMETER_BOUND = 18;
    private static final byte FIELD = 19;
    private static final byte METHOD_RETURN = 20;
    private static final byte METHOD_RECEIVER = 21;
    private static final byte METHOD_FORMAL_PARAMETER = 22;
    private static final byte THROWS = 23;
    private static final byte LOCAL_VARIABLE = 64;
    private static final byte RESOURCE_VARIABLE = 65;
    private static final byte EXCEPTION_PARAMETER = 66;
    private static final byte INSTANCEOF = 67;
    private static final byte NEW = 68;
    private static final byte CONSTRUCTOR_REFERENCE = 69;
    private static final byte METHOD_REFERENCE = 70;
    private static final byte CAST = 71;
    private static final byte CONSTRUCTOR_INVOCATION_TYPE_ARGUMENT = 72;
    private static final byte METHOD_INVOCATION_TYPE_ARGUMENT = 73;
    private static final byte CONSTRUCTOR_REFERENCE_TYPE_ARGUMENT = 74;
    private static final byte METHOD_REFERENCE_TYPE_ARGUMENT = 75;
    
    public static AnnotatedType buildAnnotatedType(final byte[] array, final ConstantPool constantPool, final AnnotatedElement annotatedElement, final Class<?> clazz, final Type type, final TypeAnnotation.TypeAnnotationTarget typeAnnotationTarget) {
        final TypeAnnotation[] typeAnnotations = parseTypeAnnotations(array, constantPool, annotatedElement, clazz);
        final ArrayList list = new ArrayList(typeAnnotations.length);
        for (final TypeAnnotation typeAnnotation : typeAnnotations) {
            if (typeAnnotation.getTargetInfo().getTarget() == typeAnnotationTarget) {
                list.add((Object)typeAnnotation);
            }
        }
        final TypeAnnotation[] array3 = (TypeAnnotation[])list.toArray((Object[])TypeAnnotationParser.EMPTY_TYPE_ANNOTATION_ARRAY);
        return AnnotatedTypeFactory.buildAnnotatedType(type, TypeAnnotation.LocationInfo.BASE_LOCATION, array3, array3, annotatedElement);
    }
    
    public static AnnotatedType[] buildAnnotatedTypes(final byte[] array, final ConstantPool constantPool, final AnnotatedElement annotatedElement, final Class<?> clazz, final Type[] array2, final TypeAnnotation.TypeAnnotationTarget typeAnnotationTarget) {
        final int length = array2.length;
        final AnnotatedType[] array3 = new AnnotatedType[length];
        Arrays.fill(array3, AnnotatedTypeFactory.EMPTY_ANNOTATED_TYPE);
        final ArrayList[] array4 = new ArrayList[length];
        final TypeAnnotation[] typeAnnotations;
        final TypeAnnotation[] array5 = typeAnnotations = parseTypeAnnotations(array, constantPool, annotatedElement, clazz);
        for (final TypeAnnotation typeAnnotation : typeAnnotations) {
            final TypeAnnotation.TypeAnnotationTargetInfo targetInfo = typeAnnotation.getTargetInfo();
            if (targetInfo.getTarget() == typeAnnotationTarget) {
                final int count = targetInfo.getCount();
                if (array4[count] == null) {
                    array4[count] = new ArrayList(array5.length);
                }
                array4[count].add(typeAnnotation);
            }
        }
        for (int j = 0; j < length; ++j) {
            final ArrayList list = array4[j];
            TypeAnnotation[] empty_TYPE_ANNOTATION_ARRAY;
            if (list != null) {
                empty_TYPE_ANNOTATION_ARRAY = list.toArray(new TypeAnnotation[list.size()]);
            }
            else {
                empty_TYPE_ANNOTATION_ARRAY = TypeAnnotationParser.EMPTY_TYPE_ANNOTATION_ARRAY;
            }
            array3[j] = AnnotatedTypeFactory.buildAnnotatedType(array2[j], TypeAnnotation.LocationInfo.BASE_LOCATION, empty_TYPE_ANNOTATION_ARRAY, empty_TYPE_ANNOTATION_ARRAY, annotatedElement);
        }
        return array3;
    }
    
    public static AnnotatedType buildAnnotatedSuperclass(final byte[] array, final ConstantPool constantPool, final Class<?> clazz) {
        final Type genericSuperclass = clazz.getGenericSuperclass();
        if (genericSuperclass == null) {
            return AnnotatedTypeFactory.EMPTY_ANNOTATED_TYPE;
        }
        return buildAnnotatedType(array, constantPool, clazz, clazz, genericSuperclass, TypeAnnotation.TypeAnnotationTarget.CLASS_EXTENDS);
    }
    
    public static AnnotatedType[] buildAnnotatedInterfaces(final byte[] array, final ConstantPool constantPool, final Class<?> clazz) {
        if (clazz == Object.class || clazz.isArray() || clazz.isPrimitive() || clazz == Void.TYPE) {
            return AnnotatedTypeFactory.EMPTY_ANNOTATED_TYPE_ARRAY;
        }
        return buildAnnotatedTypes(array, constantPool, clazz, clazz, clazz.getGenericInterfaces(), TypeAnnotation.TypeAnnotationTarget.CLASS_IMPLEMENTS);
    }
    
    public static <D extends GenericDeclaration> Annotation[] parseTypeVariableAnnotations(final D n, final int n2) {
        GenericDeclaration genericDeclaration;
        TypeAnnotation.TypeAnnotationTarget typeAnnotationTarget;
        if (n instanceof Class) {
            genericDeclaration = n;
            typeAnnotationTarget = TypeAnnotation.TypeAnnotationTarget.CLASS_TYPE_PARAMETER;
        }
        else {
            if (!(n instanceof Executable)) {
                throw new AssertionError((Object)("Unknown GenericDeclaration " + n + "\nthis should not happen."));
            }
            genericDeclaration = n;
            typeAnnotationTarget = TypeAnnotation.TypeAnnotationTarget.METHOD_TYPE_PARAMETER;
        }
        final List<TypeAnnotation> filter = TypeAnnotation.filter(parseAllTypeAnnotations(genericDeclaration), typeAnnotationTarget);
        final ArrayList list = new ArrayList(filter.size());
        for (final TypeAnnotation typeAnnotation : filter) {
            if (typeAnnotation.getTargetInfo().getCount() == n2) {
                list.add((Object)typeAnnotation.getAnnotation());
            }
        }
        return (Annotation[])list.toArray((Object[])new Annotation[0]);
    }
    
    public static <D extends GenericDeclaration> AnnotatedType[] parseAnnotatedBounds(final Type[] array, final D n, final int n2) {
        return parseAnnotatedBounds(array, n, n2, TypeAnnotation.LocationInfo.BASE_LOCATION);
    }
    
    private static <D extends GenericDeclaration> AnnotatedType[] parseAnnotatedBounds(final Type[] array, final D n, final int n2, final TypeAnnotation.LocationInfo locationInfo) {
        final List<TypeAnnotation> fetchBounds = fetchBounds(n);
        if (array != null) {
            int n3 = 0;
            final AnnotatedType[] array2 = new AnnotatedType[array.length];
            if (array.length > 0) {
                final Type type = array[0];
                if (!(type instanceof Class)) {
                    n3 = 1;
                }
                else if (((Class)type).isInterface()) {
                    n3 = 1;
                }
            }
            for (int i = 0; i < array.length; ++i) {
                final ArrayList list = new ArrayList(fetchBounds.size());
                for (final TypeAnnotation typeAnnotation : fetchBounds) {
                    final TypeAnnotation.TypeAnnotationTargetInfo targetInfo = typeAnnotation.getTargetInfo();
                    if (targetInfo.getSecondaryIndex() == i + n3 && targetInfo.getCount() == n2) {
                        list.add((Object)typeAnnotation);
                    }
                }
                array2[i] = AnnotatedTypeFactory.buildAnnotatedType(array[i], locationInfo, (TypeAnnotation[])list.toArray((Object[])TypeAnnotationParser.EMPTY_TYPE_ANNOTATION_ARRAY), fetchBounds.toArray(TypeAnnotationParser.EMPTY_TYPE_ANNOTATION_ARRAY), n);
            }
            return array2;
        }
        return new AnnotatedType[0];
    }
    
    private static <D extends GenericDeclaration> List<TypeAnnotation> fetchBounds(final D n) {
        TypeAnnotation.TypeAnnotationTarget typeAnnotationTarget;
        GenericDeclaration genericDeclaration;
        if (n instanceof Class) {
            typeAnnotationTarget = TypeAnnotation.TypeAnnotationTarget.CLASS_TYPE_PARAMETER_BOUND;
            genericDeclaration = n;
        }
        else {
            typeAnnotationTarget = TypeAnnotation.TypeAnnotationTarget.METHOD_TYPE_PARAMETER_BOUND;
            genericDeclaration = n;
        }
        return TypeAnnotation.filter(parseAllTypeAnnotations(genericDeclaration), typeAnnotationTarget);
    }
    
    static TypeAnnotation[] parseAllTypeAnnotations(final AnnotatedElement annotatedElement) {
        final JavaLangAccess javaLangAccess = SharedSecrets.getJavaLangAccess();
        Class<?> declaringClass;
        byte[] array;
        if (annotatedElement instanceof Class) {
            declaringClass = (Class)annotatedElement;
            array = javaLangAccess.getRawClassTypeAnnotations(declaringClass);
        }
        else {
            if (!(annotatedElement instanceof Executable)) {
                return TypeAnnotationParser.EMPTY_TYPE_ANNOTATION_ARRAY;
            }
            declaringClass = ((Executable)annotatedElement).getDeclaringClass();
            array = javaLangAccess.getRawExecutableTypeAnnotations((Executable)annotatedElement);
        }
        return parseTypeAnnotations(array, javaLangAccess.getConstantPool(declaringClass), annotatedElement, declaringClass);
    }
    
    private static TypeAnnotation[] parseTypeAnnotations(final byte[] array, final ConstantPool constantPool, final AnnotatedElement annotatedElement, final Class<?> clazz) {
        if (array == null) {
            return TypeAnnotationParser.EMPTY_TYPE_ANNOTATION_ARRAY;
        }
        final ByteBuffer wrap = ByteBuffer.wrap(array);
        final int n = wrap.getShort() & 0xFFFF;
        final ArrayList list = new ArrayList(n);
        for (int i = 0; i < n; ++i) {
            final TypeAnnotation typeAnnotation = parseTypeAnnotation(wrap, constantPool, annotatedElement, clazz);
            if (typeAnnotation != null) {
                list.add((Object)typeAnnotation);
            }
        }
        return (TypeAnnotation[])list.toArray((Object[])TypeAnnotationParser.EMPTY_TYPE_ANNOTATION_ARRAY);
    }
    
    static Map<Class<? extends Annotation>, Annotation> mapTypeAnnotations(final TypeAnnotation[] array) {
        final LinkedHashMap linkedHashMap = new LinkedHashMap();
        for (int length = array.length, i = 0; i < length; ++i) {
            final Annotation annotation = array[i].getAnnotation();
            final Class<? extends Annotation> annotationType = annotation.annotationType();
            if (AnnotationType.getInstance(annotationType).retention() == RetentionPolicy.RUNTIME && linkedHashMap.put(annotationType, annotation) != null) {
                throw new AnnotationFormatError("Duplicate annotation for class: " + annotationType + ": " + annotation);
            }
        }
        return linkedHashMap;
    }
    
    private static TypeAnnotation parseTypeAnnotation(final ByteBuffer byteBuffer, final ConstantPool constantPool, final AnnotatedElement annotatedElement, final Class<?> clazz) {
        try {
            final TypeAnnotation.TypeAnnotationTargetInfo targetInfo = parseTargetInfo(byteBuffer);
            final TypeAnnotation.LocationInfo locationInfo = TypeAnnotation.LocationInfo.parseLocationInfo(byteBuffer);
            final Annotation annotation = AnnotationParser.parseAnnotation(byteBuffer, constantPool, clazz, false);
            if (targetInfo == null) {
                return null;
            }
            return new TypeAnnotation(targetInfo, locationInfo, annotation, annotatedElement);
        }
        catch (final IllegalArgumentException | BufferUnderflowException ex) {
            throw new AnnotationFormatError((Throwable)ex);
        }
    }
    
    private static TypeAnnotation.TypeAnnotationTargetInfo parseTargetInfo(final ByteBuffer byteBuffer) {
        final int n = byteBuffer.get() & 0xFF;
        switch (n) {
            case 0:
            case 1: {
                final int n2 = byteBuffer.get() & 0xFF;
                TypeAnnotation.TypeAnnotationTargetInfo typeAnnotationTargetInfo;
                if (n == 0) {
                    typeAnnotationTargetInfo = new TypeAnnotation.TypeAnnotationTargetInfo(TypeAnnotation.TypeAnnotationTarget.CLASS_TYPE_PARAMETER, n2);
                }
                else {
                    typeAnnotationTargetInfo = new TypeAnnotation.TypeAnnotationTargetInfo(TypeAnnotation.TypeAnnotationTarget.METHOD_TYPE_PARAMETER, n2);
                }
                return typeAnnotationTargetInfo;
            }
            case 16: {
                final short short1 = byteBuffer.getShort();
                if (short1 == -1) {
                    return new TypeAnnotation.TypeAnnotationTargetInfo(TypeAnnotation.TypeAnnotationTarget.CLASS_EXTENDS);
                }
                if (short1 >= 0) {
                    return new TypeAnnotation.TypeAnnotationTargetInfo(TypeAnnotation.TypeAnnotationTarget.CLASS_IMPLEMENTS, short1);
                }
                break;
            }
            case 17: {
                return parse2ByteTarget(TypeAnnotation.TypeAnnotationTarget.CLASS_TYPE_PARAMETER_BOUND, byteBuffer);
            }
            case 18: {
                return parse2ByteTarget(TypeAnnotation.TypeAnnotationTarget.METHOD_TYPE_PARAMETER_BOUND, byteBuffer);
            }
            case 19: {
                return new TypeAnnotation.TypeAnnotationTargetInfo(TypeAnnotation.TypeAnnotationTarget.FIELD);
            }
            case 20: {
                return new TypeAnnotation.TypeAnnotationTargetInfo(TypeAnnotation.TypeAnnotationTarget.METHOD_RETURN);
            }
            case 21: {
                return new TypeAnnotation.TypeAnnotationTargetInfo(TypeAnnotation.TypeAnnotationTarget.METHOD_RECEIVER);
            }
            case 22: {
                return new TypeAnnotation.TypeAnnotationTargetInfo(TypeAnnotation.TypeAnnotationTarget.METHOD_FORMAL_PARAMETER, byteBuffer.get() & 0xFF);
            }
            case 23: {
                return parseShortTarget(TypeAnnotation.TypeAnnotationTarget.THROWS, byteBuffer);
            }
            case 64:
            case 65: {
                for (short short2 = byteBuffer.getShort(), n3 = 0; n3 < short2; ++n3) {
                    byteBuffer.getShort();
                    byteBuffer.getShort();
                    byteBuffer.getShort();
                }
                return null;
            }
            case 66: {
                byteBuffer.get();
                return null;
            }
            case 67:
            case 68:
            case 69:
            case 70: {
                byteBuffer.getShort();
                return null;
            }
            case 71:
            case 72:
            case 73:
            case 74:
            case 75: {
                byteBuffer.getShort();
                byteBuffer.get();
                return null;
            }
        }
        throw new AnnotationFormatError("Could not parse bytes for type annotations");
    }
    
    private static TypeAnnotation.TypeAnnotationTargetInfo parseShortTarget(final TypeAnnotation.TypeAnnotationTarget typeAnnotationTarget, final ByteBuffer byteBuffer) {
        return new TypeAnnotation.TypeAnnotationTargetInfo(typeAnnotationTarget, byteBuffer.getShort() & 0xFFFF);
    }
    
    private static TypeAnnotation.TypeAnnotationTargetInfo parse2ByteTarget(final TypeAnnotation.TypeAnnotationTarget typeAnnotationTarget, final ByteBuffer byteBuffer) {
        return new TypeAnnotation.TypeAnnotationTargetInfo(typeAnnotationTarget, byteBuffer.get() & 0xFF, byteBuffer.get() & 0xFF);
    }
    
    static {
        EMPTY_TYPE_ANNOTATION_ARRAY = new TypeAnnotation[0];
    }
}
