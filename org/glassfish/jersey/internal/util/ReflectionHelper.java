package org.glassfish.jersey.internal.util;

import java.lang.reflect.WildcardType;
import java.net.URL;
import org.osgi.framework.Bundle;
import java.io.IOException;
import org.osgi.framework.FrameworkUtil;
import java.io.InputStream;
import org.glassfish.jersey.internal.OsgiRegistry;
import java.util.HashMap;
import java.lang.reflect.TypeVariable;
import java.util.Iterator;
import org.glassfish.jersey.internal.LocalizationMessages;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.GenericType;
import java.util.Set;
import java.util.Map;
import java.util.IdentityHashMap;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import org.glassfish.jersey.internal.util.collection.ClassTypePair;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Function;
import java.util.Collections;
import java.lang.reflect.Type;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.Collection;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.AccessibleObject;
import java.security.PrivilegedAction;
import java.util.logging.Logger;

public final class ReflectionHelper
{
    private static final Logger LOGGER;
    private static final PrivilegedAction<?> NoOpPrivilegedACTION;
    private static final TypeVisitor<Class> eraser;
    private static final Class<?> bundleReferenceClass;
    
    private ReflectionHelper() {
        throw new AssertionError((Object)"No instances allowed.");
    }
    
    public static Class<?> getDeclaringClass(final AccessibleObject ao) {
        if (ao instanceof Member && (ao instanceof Field || ao instanceof Method || ao instanceof Constructor)) {
            return ((Member)ao).getDeclaringClass();
        }
        throw new IllegalArgumentException("Unsupported accessible object type: " + ao.getClass().getName());
    }
    
    public static String objectToString(final Object o) {
        if (o == null) {
            return "null";
        }
        return o.getClass().getName() + '@' + Integer.toHexString(o.hashCode());
    }
    
    public static String methodInstanceToString(final Object o, final Method m) {
        final StringBuilder sb = new StringBuilder();
        sb.append(o.getClass().getName()).append('@').append(Integer.toHexString(o.hashCode())).append('.').append(m.getName()).append('(');
        final Class[] params = m.getParameterTypes();
        for (int i = 0; i < params.length; ++i) {
            sb.append(getTypeName(params[i]));
            if (i < params.length - 1) {
                sb.append(",");
            }
        }
        sb.append(')');
        return sb.toString();
    }
    
    private static String getTypeName(final Class<?> type) {
        if (type.isArray()) {
            Class<?> cl = type;
            int dimensions = 0;
            while (cl.isArray()) {
                ++dimensions;
                cl = cl.getComponentType();
            }
            final StringBuilder sb = new StringBuilder();
            sb.append(cl.getName());
            for (int i = 0; i < dimensions; ++i) {
                sb.append("[]");
            }
            return sb.toString();
        }
        return type.getName();
    }
    
    public static <T> PrivilegedAction<Class<T>> classForNamePA(final String name) {
        return classForNamePA(name, getContextClassLoader());
    }
    
    public static <T> PrivilegedAction<Class<T>> classForNamePA(final String name, final ClassLoader cl) {
        return new PrivilegedAction<Class<T>>() {
            @Override
            public Class<T> run() {
                if (cl != null) {
                    try {
                        return (Class<T>)Class.forName(name, false, cl);
                    }
                    catch (final ClassNotFoundException ex) {
                        if (ReflectionHelper.LOGGER.isLoggable(Level.FINER)) {
                            ReflectionHelper.LOGGER.log(Level.FINER, "Unable to load class " + name + " using the supplied class loader " + cl.getClass().getName() + ".", ex);
                        }
                    }
                }
                try {
                    return (Class<T>)Class.forName(name);
                }
                catch (final ClassNotFoundException ex) {
                    if (ReflectionHelper.LOGGER.isLoggable(Level.FINER)) {
                        ReflectionHelper.LOGGER.log(Level.FINER, "Unable to load class " + name + " using the current class loader.", ex);
                    }
                    return null;
                }
            }
        };
    }
    
    public static PrivilegedAction<ClassLoader> getClassLoaderPA(final Class<?> clazz) {
        return new PrivilegedAction<ClassLoader>() {
            @Override
            public ClassLoader run() {
                return clazz.getClassLoader();
            }
        };
    }
    
    public static PrivilegedAction<Field[]> getDeclaredFieldsPA(final Class<?> clazz) {
        return new PrivilegedAction<Field[]>() {
            @Override
            public Field[] run() {
                return clazz.getDeclaredFields();
            }
        };
    }
    
    public static PrivilegedAction<Field[]> getAllFieldsPA(final Class<?> clazz) {
        return new PrivilegedAction<Field[]>() {
            @Override
            public Field[] run() {
                final List<Field> fields = new ArrayList<Field>();
                this.recurse(clazz, fields);
                return fields.toArray(new Field[fields.size()]);
            }
            
            private void recurse(final Class<?> clazz, final List<Field> fields) {
                fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
                if (clazz.getSuperclass() != null) {
                    this.recurse(clazz.getSuperclass(), fields);
                }
            }
        };
    }
    
    public static PrivilegedAction<Collection<? extends Method>> getDeclaredMethodsPA(final Class<?> clazz) {
        return new PrivilegedAction<Collection<? extends Method>>() {
            @Override
            public Collection<? extends Method> run() {
                return Arrays.asList(clazz.getDeclaredMethods());
            }
        };
    }
    
    public static <T> PrivilegedExceptionAction<Class<T>> classForNameWithExceptionPEA(final String name) throws ClassNotFoundException {
        return classForNameWithExceptionPEA(name, getContextClassLoader());
    }
    
    public static <T> PrivilegedExceptionAction<Class<T>> classForNameWithExceptionPEA(final String name, final ClassLoader cl) throws ClassNotFoundException {
        return new PrivilegedExceptionAction<Class<T>>() {
            @Override
            public Class<T> run() throws ClassNotFoundException {
                if (cl != null) {
                    try {
                        return (Class<T>)Class.forName(name, false, cl);
                    }
                    catch (final ClassNotFoundException ex) {}
                }
                return (Class<T>)Class.forName(name);
            }
        };
    }
    
    public static PrivilegedAction<ClassLoader> getContextClassLoaderPA() {
        return new PrivilegedAction<ClassLoader>() {
            @Override
            public ClassLoader run() {
                return Thread.currentThread().getContextClassLoader();
            }
        };
    }
    
    private static ClassLoader getContextClassLoader() {
        return AccessController.doPrivileged(getContextClassLoaderPA());
    }
    
    public static PrivilegedAction setContextClassLoaderPA(final ClassLoader classLoader) {
        return new PrivilegedAction() {
            @Override
            public Object run() {
                Thread.currentThread().setContextClassLoader(classLoader);
                return null;
            }
        };
    }
    
    public static PrivilegedAction setAccessibleMethodPA(final Method m) {
        if (Modifier.isPublic(m.getModifiers())) {
            return ReflectionHelper.NoOpPrivilegedACTION;
        }
        return new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                if (!m.isAccessible()) {
                    m.setAccessible(true);
                }
                return m;
            }
        };
    }
    
    public static List<Class<?>> getGenericTypeArgumentClasses(final Type type) throws IllegalArgumentException {
        final Type[] types = getTypeArguments(type);
        if (types == null) {
            return Collections.emptyList();
        }
        return Arrays.stream(types).map((Function<? super Type, ?>)ReflectionHelper::erasure).collect((Collector<? super Object, ?, List<Class<?>>>)Collectors.toList());
    }
    
    public static List<ClassTypePair> getTypeArgumentAndClass(final Type type) throws IllegalArgumentException {
        final Type[] types = getTypeArguments(type);
        if (types == null) {
            return Collections.emptyList();
        }
        return Arrays.stream(types).map(type1 -> ClassTypePair.of(erasure(type1), type1)).collect((Collector<? super Object, ?, List<ClassTypePair>>)Collectors.toList());
    }
    
    public static boolean isPrimitive(final Type type) {
        if (type instanceof Class) {
            final Class c = (Class)type;
            return c.isPrimitive();
        }
        return false;
    }
    
    public static Type[] getTypeArguments(final Type type) {
        if (!(type instanceof ParameterizedType)) {
            return null;
        }
        return ((ParameterizedType)type).getActualTypeArguments();
    }
    
    public static Type getTypeArgument(final Type type, final int index) {
        if (type instanceof ParameterizedType) {
            final ParameterizedType p = (ParameterizedType)type;
            return fix(p.getActualTypeArguments()[index]);
        }
        return null;
    }
    
    private static Type fix(final Type t) {
        if (!(t instanceof GenericArrayType)) {
            return t;
        }
        final GenericArrayType gat = (GenericArrayType)t;
        if (gat.getGenericComponentType() instanceof Class) {
            final Class c = (Class)gat.getGenericComponentType();
            return Array.newInstance(c, 0).getClass();
        }
        return t;
    }
    
    public static <T> Class<T> erasure(final Type type) {
        return ReflectionHelper.eraser.visit(type);
    }
    
    public static boolean isSubClassOf(final Type subType, final Type superType) {
        return erasure(superType).isAssignableFrom(erasure(subType));
    }
    
    public static boolean isArray(final Type type) {
        if (type instanceof Class) {
            final Class c = (Class)type;
            return c.isArray();
        }
        return type instanceof GenericArrayType;
    }
    
    public static boolean isArrayOfType(final Type type, final Class<?> componentType) {
        if (type instanceof Class) {
            final Class c = (Class)type;
            return c.isArray() && c != byte[].class;
        }
        if (type instanceof GenericArrayType) {
            final Type arrayComponentType = ((GenericArrayType)type).getGenericComponentType();
            return arrayComponentType == componentType;
        }
        return false;
    }
    
    public static Type getArrayComponentType(final Type type) {
        if (type instanceof Class) {
            final Class c = (Class)type;
            return c.getComponentType();
        }
        if (type instanceof GenericArrayType) {
            return ((GenericArrayType)type).getGenericComponentType();
        }
        throw new IllegalArgumentException();
    }
    
    public static Class<?> getArrayForComponentType(final Class<?> c) {
        try {
            final Object o = Array.newInstance(c, 0);
            return o.getClass();
        }
        catch (final Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    public static PrivilegedAction<Method> getValueOfStringMethodPA(final Class<?> clazz) {
        return getStringToObjectMethodPA(clazz, "valueOf");
    }
    
    public static PrivilegedAction<Method> getFromStringStringMethodPA(final Class<?> clazz) {
        return getStringToObjectMethodPA(clazz, "fromString");
    }
    
    private static PrivilegedAction<Method> getStringToObjectMethodPA(final Class<?> clazz, final String methodName) {
        return new PrivilegedAction<Method>() {
            @Override
            public Method run() {
                try {
                    final Method method = clazz.getDeclaredMethod(methodName, String.class);
                    if (Modifier.isStatic(method.getModifiers()) && method.getReturnType() == clazz) {
                        return method;
                    }
                    return null;
                }
                catch (final NoSuchMethodException nsme) {
                    return null;
                }
            }
        };
    }
    
    public static PrivilegedAction<Constructor> getStringConstructorPA(final Class<?> clazz) {
        return new PrivilegedAction<Constructor>() {
            @Override
            public Constructor run() {
                try {
                    return clazz.getConstructor(String.class);
                }
                catch (final SecurityException e) {
                    throw e;
                }
                catch (final Exception e2) {
                    return null;
                }
            }
        };
    }
    
    public static PrivilegedAction<Constructor<?>[]> getDeclaredConstructorsPA(final Class<?> clazz) {
        return new PrivilegedAction<Constructor<?>[]>() {
            @Override
            public Constructor<?>[] run() {
                return clazz.getDeclaredConstructors();
            }
        };
    }
    
    public static PrivilegedAction<Constructor<?>> getDeclaredConstructorPA(final Class<?> clazz, final Class<?>... params) {
        return new PrivilegedAction<Constructor<?>>() {
            @Override
            public Constructor<?> run() {
                try {
                    return clazz.getDeclaredConstructor((Class<?>[])params);
                }
                catch (final NoSuchMethodException e) {
                    return null;
                }
            }
        };
    }
    
    public static Collection<Class<? extends Annotation>> getAnnotationTypes(final AnnotatedElement annotatedElement, final Class<? extends Annotation> metaAnnotation) {
        final Set<Class<? extends Annotation>> result = Collections.newSetFromMap(new IdentityHashMap<Class<? extends Annotation>, Boolean>());
        for (final Annotation a : annotatedElement.getAnnotations()) {
            final Class<? extends Annotation> aType = a.annotationType();
            if (metaAnnotation == null || aType.getAnnotation(metaAnnotation) != null) {
                result.add(aType);
            }
        }
        return result;
    }
    
    public static boolean isGetter(final Method method) {
        if (method.getParameterTypes().length == 0 && Modifier.isPublic(method.getModifiers())) {
            final String methodName = method.getName();
            if (methodName.startsWith("get") && methodName.length() > 3) {
                return !Void.TYPE.equals(method.getReturnType());
            }
            if (methodName.startsWith("is") && methodName.length() > 2) {
                return Boolean.TYPE.equals(method.getReturnType()) || Boolean.class.equals(method.getReturnType());
            }
        }
        return false;
    }
    
    public static GenericType genericTypeFor(final Object instance) {
        GenericType genericType;
        if (instance instanceof GenericEntity) {
            genericType = new GenericType(((GenericEntity)instance).getType());
        }
        else {
            genericType = ((instance == null) ? null : new GenericType((Type)instance.getClass()));
        }
        return genericType;
    }
    
    public static boolean isSetter(final Method method) {
        return Modifier.isPublic(method.getModifiers()) && Void.TYPE.equals(method.getReturnType()) && method.getParameterTypes().length == 1 && method.getName().startsWith("set");
    }
    
    public static String getPropertyName(final Method method) {
        if (!isGetter(method) && !isSetter(method)) {
            throw new IllegalArgumentException(LocalizationMessages.METHOD_NOT_GETTER_NOR_SETTER());
        }
        final String methodName = method.getName();
        final int offset = methodName.startsWith("is") ? 2 : 3;
        final char[] chars = methodName.toCharArray();
        chars[offset] = Character.toLowerCase(chars[offset]);
        return new String(chars, offset, chars.length - offset);
    }
    
    public static Class<?> theMostSpecificTypeOf(final Set<Type> contractTypes) {
        Class<?> result = null;
        for (final Type t : contractTypes) {
            final Class<?> next = (Class<?>)t;
            if (result == null) {
                result = next;
            }
            else {
                if (!result.isAssignableFrom(next)) {
                    continue;
                }
                result = next;
            }
        }
        return result;
    }
    
    public static Class[] getParameterizedClassArguments(final DeclaringClassInterfacePair p) {
        if (p.genericInterface instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType)p.genericInterface;
            final Type[] as = pt.getActualTypeArguments();
            final Class[] cas = new Class[as.length];
            for (int i = 0; i < as.length; ++i) {
                final Type a = as[i];
                if (a instanceof Class) {
                    cas[i] = (Class)a;
                }
                else if (a instanceof ParameterizedType) {
                    pt = (ParameterizedType)a;
                    cas[i] = (Class)pt.getRawType();
                }
                else if (a instanceof TypeVariable) {
                    final TypeVariable tv = (TypeVariable)a;
                    final ClassTypePair ctp = resolveTypeVariable(p.concreteClass, p.declaringClass, tv);
                    cas[i] = (Class)((ctp != null) ? ctp.rawClass() : tv.getBounds()[0]);
                }
                else if (a instanceof GenericArrayType) {
                    final GenericArrayType gat = (GenericArrayType)a;
                    final Type t = gat.getGenericComponentType();
                    if (t instanceof Class) {
                        cas[i] = getArrayForComponentType((Class<?>)t);
                    }
                }
            }
            return cas;
        }
        return null;
    }
    
    public static Type[] getParameterizedTypeArguments(final DeclaringClassInterfacePair p) {
        if (p.genericInterface instanceof ParameterizedType) {
            final ParameterizedType pt = (ParameterizedType)p.genericInterface;
            final Type[] as = pt.getActualTypeArguments();
            final Type[] ras = new Type[as.length];
            for (int i = 0; i < as.length; ++i) {
                final Type a = as[i];
                if (a instanceof Class) {
                    ras[i] = a;
                }
                else if (a instanceof ParameterizedType) {
                    ras[i] = a;
                }
                else if (a instanceof TypeVariable) {
                    final ClassTypePair ctp = resolveTypeVariable(p.concreteClass, p.declaringClass, (TypeVariable)a);
                    if (ctp == null) {
                        throw new IllegalArgumentException(LocalizationMessages.ERROR_RESOLVING_GENERIC_TYPE_VALUE(p.genericInterface, p.concreteClass));
                    }
                    ras[i] = ctp.type();
                }
            }
            return ras;
        }
        return null;
    }
    
    public static DeclaringClassInterfacePair getClass(final Class<?> concrete, final Class<?> iface) {
        return getClass(concrete, iface, concrete);
    }
    
    private static DeclaringClassInterfacePair getClass(final Class<?> concrete, final Class<?> iface, Class<?> c) {
        final Type[] gis = c.getGenericInterfaces();
        final DeclaringClassInterfacePair p = getType(concrete, iface, c, gis);
        if (p != null) {
            return p;
        }
        c = c.getSuperclass();
        if (c == null || c == Object.class) {
            return null;
        }
        return getClass(concrete, iface, c);
    }
    
    private static DeclaringClassInterfacePair getType(final Class<?> concrete, final Class<?> iface, final Class<?> c, final Type[] ts) {
        for (final Type t : ts) {
            final DeclaringClassInterfacePair p = getType(concrete, iface, c, t);
            if (p != null) {
                return p;
            }
        }
        return null;
    }
    
    private static DeclaringClassInterfacePair getType(final Class<?> concrete, final Class<?> iface, final Class<?> c, final Type t) {
        if (t instanceof Class) {
            if (t == iface) {
                return new DeclaringClassInterfacePair((Class)concrete, (Class)c, t);
            }
            return getClass(concrete, iface, (Class<?>)t);
        }
        else {
            if (!(t instanceof ParameterizedType)) {
                return null;
            }
            final ParameterizedType pt = (ParameterizedType)t;
            if (pt.getRawType() == iface) {
                return new DeclaringClassInterfacePair((Class)concrete, (Class)c, t);
            }
            return getClass(concrete, iface, (Class<?>)pt.getRawType());
        }
    }
    
    public static ClassTypePair resolveGenericType(final Class concreteClass, final Class declaringClass, final Class rawResolvedType, final Type genericResolvedType) {
        if (genericResolvedType instanceof TypeVariable) {
            final ClassTypePair ct = resolveTypeVariable(concreteClass, declaringClass, (TypeVariable)genericResolvedType);
            if (ct != null) {
                return ct;
            }
        }
        else if (genericResolvedType instanceof ParameterizedType) {
            final ParameterizedType pt = (ParameterizedType)genericResolvedType;
            final Type[] ptts = pt.getActualTypeArguments();
            boolean modified = false;
            for (int i = 0; i < ptts.length; ++i) {
                final ClassTypePair ct2 = resolveGenericType(concreteClass, declaringClass, (Class)pt.getRawType(), ptts[i]);
                if (ct2.type() != ptts[i]) {
                    ptts[i] = ct2.type();
                    modified = true;
                }
            }
            if (modified) {
                final ParameterizedType rpt = new ParameterizedType() {
                    @Override
                    public Type[] getActualTypeArguments() {
                        return ptts.clone();
                    }
                    
                    @Override
                    public Type getRawType() {
                        return pt.getRawType();
                    }
                    
                    @Override
                    public Type getOwnerType() {
                        return pt.getOwnerType();
                    }
                };
                return ClassTypePair.of((Class<?>)pt.getRawType(), rpt);
            }
        }
        else if (genericResolvedType instanceof GenericArrayType) {
            final GenericArrayType gat = (GenericArrayType)genericResolvedType;
            final ClassTypePair ct3 = resolveGenericType(concreteClass, declaringClass, null, gat.getGenericComponentType());
            if (gat.getGenericComponentType() != ct3.type()) {
                try {
                    final Class ac = getArrayForComponentType(ct3.rawClass());
                    return ClassTypePair.of(ac);
                }
                catch (final Exception e) {
                    ReflectionHelper.LOGGER.log(Level.FINEST, "", e);
                }
            }
        }
        return ClassTypePair.of(rawResolvedType, genericResolvedType);
    }
    
    public static ClassTypePair resolveTypeVariable(final Class<?> c, final Class<?> dc, final TypeVariable tv) {
        return resolveTypeVariable(c, dc, tv, new HashMap<TypeVariable, Type>());
    }
    
    private static ClassTypePair resolveTypeVariable(final Class<?> c, final Class<?> dc, final TypeVariable tv, final Map<TypeVariable, Type> map) {
        final Type[] genericInterfaces;
        final Type[] gis = genericInterfaces = c.getGenericInterfaces();
        for (final Type gi : genericInterfaces) {
            if (gi instanceof ParameterizedType) {
                final ParameterizedType pt = (ParameterizedType)gi;
                final ClassTypePair ctp = resolveTypeVariable(pt, (Class<?>)pt.getRawType(), dc, tv, map);
                if (ctp != null) {
                    return ctp;
                }
            }
        }
        final Type gsc = c.getGenericSuperclass();
        if (gsc instanceof ParameterizedType) {
            final ParameterizedType pt2 = (ParameterizedType)gsc;
            return resolveTypeVariable(pt2, c.getSuperclass(), dc, tv, map);
        }
        if (gsc instanceof Class) {
            return resolveTypeVariable(c.getSuperclass(), dc, tv, map);
        }
        return null;
    }
    
    private static ClassTypePair resolveTypeVariable(ParameterizedType pt, Class<?> c, final Class<?> dc, final TypeVariable tv, final Map<TypeVariable, Type> map) {
        final Type[] typeArguments = pt.getActualTypeArguments();
        final TypeVariable[] typeParameters = c.getTypeParameters();
        final Map<TypeVariable, Type> subMap = new HashMap<TypeVariable, Type>();
        for (int i = 0; i < typeArguments.length; ++i) {
            final Type typeArgument = typeArguments[i];
            if (typeArgument instanceof TypeVariable) {
                final Type t = map.get(typeArgument);
                subMap.put(typeParameters[i], t);
            }
            else {
                subMap.put(typeParameters[i], typeArgument);
            }
        }
        if (c != dc) {
            return resolveTypeVariable(c, dc, tv, subMap);
        }
        Type t2 = subMap.get(tv);
        if (t2 instanceof Class) {
            return ClassTypePair.of((Class<?>)t2);
        }
        if (t2 instanceof GenericArrayType) {
            final GenericArrayType gat = (GenericArrayType)t2;
            t2 = gat.getGenericComponentType();
            if (t2 instanceof Class) {
                c = (Class)t2;
                try {
                    return ClassTypePair.of(getArrayForComponentType(c));
                }
                catch (final Exception ex) {
                    return null;
                }
            }
            if (t2 instanceof ParameterizedType) {
                final Type rt = ((ParameterizedType)t2).getRawType();
                if (!(rt instanceof Class)) {
                    return null;
                }
                c = (Class)rt;
                try {
                    return ClassTypePair.of(getArrayForComponentType(c), gat);
                }
                catch (final Exception e) {
                    return null;
                }
            }
            return null;
        }
        if (!(t2 instanceof ParameterizedType)) {
            return null;
        }
        pt = (ParameterizedType)t2;
        if (pt.getRawType() instanceof Class) {
            return ClassTypePair.of((Class<?>)pt.getRawType(), pt);
        }
        return null;
    }
    
    public static PrivilegedAction<Method> findMethodOnClassPA(final Class<?> c, final Method m) {
        return new PrivilegedAction<Method>() {
            @Override
            public Method run() {
                try {
                    return c.getMethod(m.getName(), (Class[])m.getParameterTypes());
                }
                catch (final NoSuchMethodException nsme) {
                    for (final Method _m : c.getMethods()) {
                        if (_m.getName().equals(m.getName()) && _m.getParameterTypes().length == m.getParameterTypes().length && compareParameterTypes(m.getGenericParameterTypes(), _m.getGenericParameterTypes())) {
                            return _m;
                        }
                    }
                    return null;
                }
            }
        };
    }
    
    public static PrivilegedAction<Method[]> getMethodsPA(final Class<?> c) {
        return new PrivilegedAction<Method[]>() {
            @Override
            public Method[] run() {
                return c.getMethods();
            }
        };
    }
    
    private static Method[] _getMethods(final Class<?> clazz) {
        return AccessController.doPrivileged(getMethodsPA(clazz));
    }
    
    public static Method findOverridingMethodOnClass(final Class<?> clazz, final Method method) {
        for (final Method _method : _getMethods(clazz)) {
            if (!_method.isBridge() && !Modifier.isAbstract(_method.getModifiers()) && _method.getName().equals(method.getName()) && _method.getParameterTypes().length == method.getParameterTypes().length && compareParameterTypes(_method.getGenericParameterTypes(), method.getGenericParameterTypes())) {
                return _method;
            }
        }
        if (method.isBridge() || Modifier.isAbstract(method.getModifiers())) {
            ReflectionHelper.LOGGER.log(Level.INFO, LocalizationMessages.OVERRIDING_METHOD_CANNOT_BE_FOUND(method, clazz));
        }
        return method;
    }
    
    private static boolean compareParameterTypes(final Type[] ts, final Type[] _ts) {
        for (int i = 0; i < ts.length; ++i) {
            if (!ts[i].equals(_ts[i]) && !compareParameterTypes(ts[i], _ts[i])) {
                return false;
            }
        }
        return true;
    }
    
    private static boolean compareParameterTypes(final Type ts, final Type _ts) {
        if (ts instanceof Class) {
            final Class<?> clazz = (Class<?>)ts;
            if (_ts instanceof Class) {
                return ((Class)_ts).isAssignableFrom(clazz);
            }
            if (_ts instanceof TypeVariable) {
                return checkTypeBounds(clazz, ((TypeVariable)_ts).getBounds());
            }
        }
        return _ts instanceof TypeVariable;
    }
    
    private static boolean checkTypeBounds(final Class type, final Type[] bounds) {
        for (final Type bound : bounds) {
            if (bound instanceof Class && !((Class)bound).isAssignableFrom(type)) {
                return false;
            }
        }
        return true;
    }
    
    public static OsgiRegistry getOsgiRegistryInstance() {
        try {
            if (ReflectionHelper.bundleReferenceClass != null) {
                return OsgiRegistry.getInstance();
            }
        }
        catch (final Exception ex) {}
        return null;
    }
    
    public static InputStream getResourceAsStream(final ClassLoader loader, final Class<?> originClass, final String name) {
        try {
            if (ReflectionHelper.bundleReferenceClass != null && originClass != null && ReflectionHelper.bundleReferenceClass.isInstance(ReflectionHelper.class.getClassLoader())) {
                final Bundle bundle = FrameworkUtil.getBundle((Class)originClass);
                final URL resourceUrl = (bundle != null) ? bundle.getEntry(name) : null;
                if (resourceUrl != null) {
                    return resourceUrl.openStream();
                }
            }
        }
        catch (final IOException ex) {}
        return loader.getResourceAsStream(name);
    }
    
    public static Class<?> getRawClass(final Type type) {
        if (type == null) {
            return null;
        }
        if (type instanceof GenericArrayType) {
            final Type componentType = ((GenericArrayType)type).getGenericComponentType();
            if (!(componentType instanceof ParameterizedType) && !(componentType instanceof Class)) {
                return null;
            }
            final Class<?> rawComponentClass = getRawClass(componentType);
            final String forNameName = "[L" + rawComponentClass.getName() + ";";
            try {
                return Class.forName(forNameName);
            }
            catch (final Throwable th) {
                return null;
            }
        }
        if (type instanceof Class) {
            return (Class)type;
        }
        if (type instanceof ParameterizedType) {
            final Type rawType = ((ParameterizedType)type).getRawType();
            if (rawType instanceof Class) {
                return (Class)rawType;
            }
        }
        return null;
    }
    
    static {
        LOGGER = Logger.getLogger(ReflectionHelper.class.getName());
        NoOpPrivilegedACTION = new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                return null;
            }
        };
        eraser = new TypeVisitor<Class>() {
            @Override
            protected Class onClass(final Class clazz) {
                return clazz;
            }
            
            @Override
            protected Class onParameterizedType(final ParameterizedType type) {
                return this.visit(type.getRawType());
            }
            
            @Override
            protected Class onGenericArray(final GenericArrayType type) {
                return Array.newInstance(((TypeVisitor<Class<?>>)this).visit(type.getGenericComponentType()), 0).getClass();
            }
            
            @Override
            protected Class onVariable(final TypeVariable type) {
                return this.visit(type.getBounds()[0]);
            }
            
            @Override
            protected Class onWildcard(final WildcardType type) {
                return this.visit(type.getUpperBounds()[0]);
            }
            
            @Override
            protected RuntimeException createError(final Type type) {
                return new IllegalArgumentException(LocalizationMessages.TYPE_TO_CLASS_CONVERSION_NOT_SUPPORTED(type));
            }
        };
        bundleReferenceClass = AccessController.doPrivileged(classForNamePA("org.osgi.framework.BundleReference", null));
    }
    
    public static class DeclaringClassInterfacePair
    {
        public final Class<?> concreteClass;
        public final Class<?> declaringClass;
        public final Type genericInterface;
        
        private DeclaringClassInterfacePair(final Class<?> concreteClass, final Class<?> declaringClass, final Type genericInterface) {
            this.concreteClass = concreteClass;
            this.declaringClass = declaringClass;
            this.genericInterface = genericInterface;
        }
    }
}
