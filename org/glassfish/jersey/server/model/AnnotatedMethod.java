package org.glassfish.jersey.server.model;

import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.CookieParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Encoded;
import javax.ws.rs.core.Context;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.Path;
import javax.ws.rs.HttpMethod;
import java.security.PrivilegedAction;
import java.security.AccessController;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collection;
import java.util.Arrays;
import java.util.HashSet;
import java.lang.reflect.Method;
import java.lang.annotation.Annotation;
import java.util.Set;
import java.lang.reflect.AnnotatedElement;

public final class AnnotatedMethod implements AnnotatedElement
{
    private static final Set<Class<? extends Annotation>> METHOD_META_ANNOTATIONS;
    private static final Set<Class<? extends Annotation>> METHOD_ANNOTATIONS;
    private static final Set<Class<? extends Annotation>> PARAMETER_ANNOTATIONS;
    private final Method m;
    private final Method am;
    private final Annotation[] methodAnnotations;
    private final Annotation[][] parameterAnnotations;
    
    @SafeVarargs
    private static Set<Class<? extends Annotation>> getSet(final Class<? extends Annotation>... cs) {
        final Set<Class<? extends Annotation>> s = new HashSet<Class<? extends Annotation>>();
        s.addAll(Arrays.asList(cs));
        return s;
    }
    
    public AnnotatedMethod(final Method method) {
        this.m = method;
        this.am = findAnnotatedMethod(method);
        if (method.equals(this.am)) {
            this.methodAnnotations = method.getAnnotations();
            this.parameterAnnotations = method.getParameterAnnotations();
        }
        else {
            this.methodAnnotations = mergeMethodAnnotations(method, this.am);
            this.parameterAnnotations = mergeParameterAnnotations(method, this.am);
        }
    }
    
    public Method getMethod() {
        return this.am;
    }
    
    Method getDeclaredMethod() {
        return this.m;
    }
    
    public Annotation[][] getParameterAnnotations() {
        return this.parameterAnnotations.clone();
    }
    
    public Class<?>[] getParameterTypes() {
        return this.am.getParameterTypes();
    }
    
    public TypeVariable<Method>[] getTypeParameters() {
        return this.am.getTypeParameters();
    }
    
    public Type[] getGenericParameterTypes() {
        return this.am.getGenericParameterTypes();
    }
    
    public <T extends Annotation> List<T> getMetaMethodAnnotations(final Class<T> annotation) {
        final List<T> ma = new ArrayList<T>();
        for (final Annotation a : this.methodAnnotations) {
            final T metaAnnotation = a.annotationType().getAnnotation(annotation);
            if (metaAnnotation != null) {
                ma.add(metaAnnotation);
            }
        }
        return ma;
    }
    
    @Override
    public String toString() {
        return this.m.toString();
    }
    
    @Override
    public boolean isAnnotationPresent(final Class<? extends Annotation> annotationType) {
        for (final Annotation ma : this.methodAnnotations) {
            if (ma.annotationType() == annotationType) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public <T extends Annotation> T getAnnotation(final Class<T> annotationType) {
        for (final Annotation ma : this.methodAnnotations) {
            if (ma.annotationType() == annotationType) {
                return annotationType.cast(ma);
            }
        }
        return this.am.getAnnotation(annotationType);
    }
    
    @Override
    public Annotation[] getAnnotations() {
        return this.methodAnnotations.clone();
    }
    
    @Override
    public Annotation[] getDeclaredAnnotations() {
        return this.getAnnotations();
    }
    
    private static Annotation[] mergeMethodAnnotations(final Method m, final Method am) {
        final List<Annotation> al = asList(m.getAnnotations());
        for (final Annotation a : am.getAnnotations()) {
            if (!m.isAnnotationPresent(a.getClass())) {
                al.add(a);
            }
        }
        return al.toArray(new Annotation[al.size()]);
    }
    
    private static Annotation[][] mergeParameterAnnotations(final Method m, final Method am) {
        final Annotation[][] methodParamAnnotations = m.getParameterAnnotations();
        final Annotation[][] annotatedMethodParamAnnotations = am.getParameterAnnotations();
        final List<List<Annotation>> methodParamAnnotationsList = new ArrayList<List<Annotation>>();
        for (int i = 0; i < methodParamAnnotations.length; ++i) {
            final List<Annotation> al = asList(methodParamAnnotations[i]);
            for (final Annotation a : annotatedMethodParamAnnotations[i]) {
                if (annotationNotInList(a.getClass(), al)) {
                    al.add(a);
                }
            }
            methodParamAnnotationsList.add(al);
        }
        final Annotation[][] mergedAnnotations = new Annotation[methodParamAnnotations.length][];
        for (int j = 0; j < methodParamAnnotations.length; ++j) {
            final List<Annotation> paramAnnotations = methodParamAnnotationsList.get(j);
            mergedAnnotations[j] = paramAnnotations.toArray(new Annotation[paramAnnotations.size()]);
        }
        return mergedAnnotations;
    }
    
    private static boolean annotationNotInList(final Class<? extends Annotation> ca, final List<Annotation> la) {
        for (final Annotation a : la) {
            if (ca == a.getClass()) {
                return false;
            }
        }
        return true;
    }
    
    private static Method findAnnotatedMethod(final Method m) {
        final Method am = findAnnotatedMethod(m.getDeclaringClass(), m);
        return (am != null) ? am : m;
    }
    
    private static Method findAnnotatedMethod(final Class<?> c, Method m) {
        if (c == Object.class) {
            return null;
        }
        m = AccessController.doPrivileged((PrivilegedAction<Method>)ReflectionHelper.findMethodOnClassPA((Class)c, m));
        if (m == null) {
            return null;
        }
        if (hasAnnotations(m)) {
            return m;
        }
        final Class<?> sc = c.getSuperclass();
        if (sc != null && sc != Object.class) {
            final Method sm = findAnnotatedMethod(sc, m);
            if (sm != null) {
                return sm;
            }
        }
        for (final Class<?> ic : c.getInterfaces()) {
            final Method im = findAnnotatedMethod(ic, m);
            if (im != null) {
                return im;
            }
        }
        return null;
    }
    
    private static boolean hasAnnotations(final Method m) {
        return hasMetaMethodAnnotations(m) || hasMethodAnnotations(m) || hasParameterAnnotations(m);
    }
    
    private static boolean hasMetaMethodAnnotations(final Method m) {
        for (final Class<? extends Annotation> ac : AnnotatedMethod.METHOD_META_ANNOTATIONS) {
            for (final Annotation a : m.getAnnotations()) {
                if (a.annotationType().getAnnotation(ac) != null) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private static boolean hasMethodAnnotations(final Method m) {
        for (final Class<? extends Annotation> ac : AnnotatedMethod.METHOD_ANNOTATIONS) {
            if (m.isAnnotationPresent(ac)) {
                return true;
            }
        }
        return false;
    }
    
    private static boolean hasParameterAnnotations(final Method m) {
        for (final Annotation[] array : m.getParameterAnnotations()) {
            final Annotation[] as = array;
            for (final Annotation a : array) {
                if (AnnotatedMethod.PARAMETER_ANNOTATIONS.contains(a.annotationType())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @SafeVarargs
    private static <T> List<T> asList(final T... ts) {
        final List<T> l = new ArrayList<T>();
        l.addAll((Collection<? extends T>)Arrays.asList(ts));
        return l;
    }
    
    static {
        METHOD_META_ANNOTATIONS = getSet(HttpMethod.class);
        METHOD_ANNOTATIONS = getSet(Path.class, Produces.class, Consumes.class);
        PARAMETER_ANNOTATIONS = getSet(Context.class, Encoded.class, DefaultValue.class, MatrixParam.class, QueryParam.class, CookieParam.class, HeaderParam.class, PathParam.class, FormParam.class);
    }
}
