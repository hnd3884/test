package sun.reflect.annotation;

import sun.misc.SharedSecrets;
import java.lang.annotation.AnnotationFormatError;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Iterator;
import java.lang.annotation.Repeatable;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Arrays;
import java.util.ArrayList;
import java.lang.annotation.Annotation;
import java.util.Map;
import sun.misc.JavaLangAccess;

public final class AnnotationSupport
{
    private static final JavaLangAccess LANG_ACCESS;
    
    public static <A extends Annotation> A[] getDirectlyAndIndirectlyPresent(final Map<Class<? extends Annotation>, Annotation> map, final Class<A> clazz) {
        final ArrayList list = new ArrayList();
        final Annotation annotation = map.get(clazz);
        if (annotation != null) {
            list.add(annotation);
        }
        final Annotation[] indirectlyPresent = getIndirectlyPresent(map, (Class<A>)clazz);
        if (indirectlyPresent != null && indirectlyPresent.length != 0) {
            list.addAll((annotation != null && !containerBeforeContainee(map, (Class<A>)clazz)) ? 1 : 0, Arrays.asList(indirectlyPresent));
        }
        return (A[])list.toArray((Object[])Array.newInstance(clazz, list.size()));
    }
    
    private static <A extends Annotation> A[] getIndirectlyPresent(final Map<Class<? extends Annotation>, Annotation> map, final Class<A> clazz) {
        final Repeatable repeatable = clazz.getDeclaredAnnotation(Repeatable.class);
        if (repeatable == null) {
            return null;
        }
        final Annotation annotation = map.get(repeatable.value());
        if (annotation == null) {
            return null;
        }
        final Annotation[] valueArray = getValueArray(annotation);
        checkTypes((A[])valueArray, annotation, clazz);
        return (A[])valueArray;
    }
    
    private static <A extends Annotation> boolean containerBeforeContainee(final Map<Class<? extends Annotation>, Annotation> map, final Class<A> clazz) {
        final Class<? extends Annotation> value = clazz.getDeclaredAnnotation(Repeatable.class).value();
        for (final Class clazz2 : map.keySet()) {
            if (clazz2 == value) {
                return true;
            }
            if (clazz2 == clazz) {
                return false;
            }
        }
        return false;
    }
    
    public static <A extends Annotation> A[] getAssociatedAnnotations(final Map<Class<? extends Annotation>, Annotation> map, final Class<?> clazz, final Class<A> clazz2) {
        Objects.requireNonNull(clazz);
        A[] array = getDirectlyAndIndirectlyPresent(map, clazz2);
        if (AnnotationType.getInstance(clazz2).isInherited()) {
            for (Class<?> clazz3 = clazz.getSuperclass(); array.length == 0 && clazz3 != null; array = getDirectlyAndIndirectlyPresent(AnnotationSupport.LANG_ACCESS.getDeclaredAnnotationMap(clazz3), clazz2), clazz3 = clazz3.getSuperclass()) {}
        }
        return array;
    }
    
    private static <A extends Annotation> A[] getValueArray(final Annotation annotation) {
        try {
            final AnnotationType instance = AnnotationType.getInstance(annotation.annotationType());
            if (instance == null) {
                throw invalidContainerException(annotation, null);
            }
            final Method method = instance.members().get("value");
            if (method == null) {
                throw invalidContainerException(annotation, null);
            }
            method.setAccessible(true);
            return (A[])method.invoke(annotation, new Object[0]);
        }
        catch (final IllegalAccessException | IllegalArgumentException | InvocationTargetException | ClassCastException ex) {
            throw invalidContainerException(annotation, (Throwable)ex);
        }
    }
    
    private static AnnotationFormatError invalidContainerException(final Annotation annotation, final Throwable t) {
        return new AnnotationFormatError(annotation + " is an invalid container for repeating annotations", t);
    }
    
    private static <A extends Annotation> void checkTypes(final A[] array, final Annotation annotation, final Class<A> clazz) {
        for (int length = array.length, i = 0; i < length; ++i) {
            if (!clazz.isInstance(array[i])) {
                throw new AnnotationFormatError(String.format("%s is an invalid container for repeating annotations of type: %s", annotation, clazz));
            }
        }
    }
    
    static {
        LANG_ACCESS = SharedSecrets.getJavaLangAccess();
    }
}
