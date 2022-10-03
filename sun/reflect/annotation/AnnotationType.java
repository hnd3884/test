package sun.reflect.annotation;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.misc.JavaLangAccess;
import sun.misc.SharedSecrets;
import java.lang.annotation.Annotation;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.util.Map;

public class AnnotationType
{
    private final Map<String, Class<?>> memberTypes;
    private final Map<String, Object> memberDefaults;
    private final Map<String, Method> members;
    private final RetentionPolicy retention;
    private final boolean inherited;
    
    public static AnnotationType getInstance(final Class<? extends Annotation> clazz) {
        final JavaLangAccess javaLangAccess = SharedSecrets.getJavaLangAccess();
        AnnotationType annotationType = javaLangAccess.getAnnotationType(clazz);
        if (annotationType == null) {
            annotationType = new AnnotationType(clazz);
            if (!javaLangAccess.casAnnotationType(clazz, null, annotationType)) {
                annotationType = javaLangAccess.getAnnotationType(clazz);
                assert annotationType != null;
            }
        }
        return annotationType;
    }
    
    private AnnotationType(final Class<? extends Annotation> clazz) {
        if (!clazz.isAnnotation()) {
            throw new IllegalArgumentException("Not an annotation type");
        }
        final Method[] array = AccessController.doPrivileged((PrivilegedAction<Method[]>)new PrivilegedAction<Method[]>() {
            @Override
            public Method[] run() {
                return clazz.getDeclaredMethods();
            }
        });
        this.memberTypes = new HashMap<String, Class<?>>(array.length + 1, 1.0f);
        this.memberDefaults = new HashMap<String, Object>(0);
        this.members = new HashMap<String, Method>(array.length + 1, 1.0f);
        for (final Method method : array) {
            if (Modifier.isPublic(method.getModifiers()) && Modifier.isAbstract(method.getModifiers()) && !method.isSynthetic()) {
                if (method.getParameterTypes().length != 0) {
                    throw new IllegalArgumentException(method + " has params");
                }
                final String name = method.getName();
                this.memberTypes.put(name, invocationHandlerReturnType(method.getReturnType()));
                this.members.put(name, method);
                final Object defaultValue = method.getDefaultValue();
                if (defaultValue != null) {
                    this.memberDefaults.put(name, defaultValue);
                }
            }
        }
        if (clazz != Retention.class && clazz != Inherited.class) {
            final JavaLangAccess javaLangAccess = SharedSecrets.getJavaLangAccess();
            final Map<Class<? extends Annotation>, Annotation> selectAnnotations = AnnotationParser.parseSelectAnnotations(javaLangAccess.getRawClassAnnotations(clazz), javaLangAccess.getConstantPool(clazz), clazz, Retention.class, Inherited.class);
            final Retention retention = selectAnnotations.get(Retention.class);
            this.retention = ((retention == null) ? RetentionPolicy.CLASS : retention.value());
            this.inherited = selectAnnotations.containsKey(Inherited.class);
        }
        else {
            this.retention = RetentionPolicy.RUNTIME;
            this.inherited = false;
        }
    }
    
    public static Class<?> invocationHandlerReturnType(final Class<?> clazz) {
        if (clazz == Byte.TYPE) {
            return Byte.class;
        }
        if (clazz == Character.TYPE) {
            return Character.class;
        }
        if (clazz == Double.TYPE) {
            return Double.class;
        }
        if (clazz == Float.TYPE) {
            return Float.class;
        }
        if (clazz == Integer.TYPE) {
            return Integer.class;
        }
        if (clazz == Long.TYPE) {
            return Long.class;
        }
        if (clazz == Short.TYPE) {
            return Short.class;
        }
        if (clazz == Boolean.TYPE) {
            return Boolean.class;
        }
        return clazz;
    }
    
    public Map<String, Class<?>> memberTypes() {
        return this.memberTypes;
    }
    
    public Map<String, Method> members() {
        return this.members;
    }
    
    public Map<String, Object> memberDefaults() {
        return this.memberDefaults;
    }
    
    public RetentionPolicy retention() {
        return this.retention;
    }
    
    public boolean isInherited() {
        return this.inherited;
    }
    
    @Override
    public String toString() {
        return "Annotation Type:\n   Member types: " + this.memberTypes + "\n   Member defaults: " + this.memberDefaults + "\n   Retention policy: " + this.retention + "\n   Inherited: " + this.inherited;
    }
}
