package com.sun.beans.finder;

import java.lang.reflect.Executable;
import com.sun.beans.TypeResolver;
import java.util.Arrays;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.Modifier;
import sun.reflect.misc.ReflectUtil;
import com.sun.beans.util.Cache;
import java.lang.reflect.Method;

public final class MethodFinder extends AbstractFinder<Method>
{
    private static final Cache<Signature, Method> CACHE;
    private final String name;
    
    public static Method findMethod(final Class<?> clazz, final String s, final Class<?>... array) throws NoSuchMethodException {
        if (s == null) {
            throw new IllegalArgumentException("Method name is not set");
        }
        PrimitiveWrapperMap.replacePrimitivesWithWrappers(array);
        final Signature signature = new Signature(clazz, s, array);
        try {
            final Method method = MethodFinder.CACHE.get(signature);
            return (method == null || ReflectUtil.isPackageAccessible(method.getDeclaringClass())) ? method : MethodFinder.CACHE.create(signature);
        }
        catch (final SignatureException ex) {
            throw ex.toNoSuchMethodException("Method '" + s + "' is not found");
        }
    }
    
    public static Method findInstanceMethod(final Class<?> clazz, final String s, final Class<?>... array) throws NoSuchMethodException {
        final Method method = findMethod(clazz, s, array);
        if (Modifier.isStatic(method.getModifiers())) {
            throw new NoSuchMethodException("Method '" + s + "' is static");
        }
        return method;
    }
    
    public static Method findStaticMethod(final Class<?> clazz, final String s, final Class<?>... array) throws NoSuchMethodException {
        final Method method = findMethod(clazz, s, array);
        if (!Modifier.isStatic(method.getModifiers())) {
            throw new NoSuchMethodException("Method '" + s + "' is not static");
        }
        return method;
    }
    
    public static Method findAccessibleMethod(final Method method) throws NoSuchMethodException {
        final Class<?> declaringClass = method.getDeclaringClass();
        if (Modifier.isPublic(declaringClass.getModifiers()) && ReflectUtil.isPackageAccessible(declaringClass)) {
            return method;
        }
        if (Modifier.isStatic(method.getModifiers())) {
            throw new NoSuchMethodException("Method '" + method.getName() + "' is not accessible");
        }
        final Type[] genericInterfaces = declaringClass.getGenericInterfaces();
        final int length = genericInterfaces.length;
        int i = 0;
        while (i < length) {
            final Type type = genericInterfaces[i];
            try {
                return findAccessibleMethod(method, type);
            }
            catch (final NoSuchMethodException ex) {
                ++i;
                continue;
            }
            break;
        }
        return findAccessibleMethod(method, declaringClass.getGenericSuperclass());
    }
    
    private static Method findAccessibleMethod(final Method method, final Type type) throws NoSuchMethodException {
        final String name = method.getName();
        final Class<?>[] parameterTypes = method.getParameterTypes();
        if (type instanceof Class) {
            return findAccessibleMethod(((Class)type).getMethod(name, (Class[])parameterTypes));
        }
        if (type instanceof ParameterizedType) {
            final ParameterizedType parameterizedType = (ParameterizedType)type;
            for (final Method method2 : ((Class)parameterizedType.getRawType()).getMethods()) {
                if (method2.getName().equals(name)) {
                    final Class<?>[] parameterTypes2 = method2.getParameterTypes();
                    if (parameterTypes2.length == parameterTypes.length) {
                        if (Arrays.equals(parameterTypes, parameterTypes2)) {
                            return findAccessibleMethod(method2);
                        }
                        final Type[] genericParameterTypes = method2.getGenericParameterTypes();
                        if (parameterTypes.length == genericParameterTypes.length && Arrays.equals(parameterTypes, TypeResolver.erase(TypeResolver.resolve(parameterizedType, genericParameterTypes)))) {
                            return findAccessibleMethod(method2);
                        }
                    }
                }
            }
        }
        throw new NoSuchMethodException("Method '" + name + "' is not accessible");
    }
    
    private MethodFinder(final String name, final Class<?>[] array) {
        super(array);
        this.name = name;
    }
    
    @Override
    protected boolean isValid(final Method method) {
        return super.isValid(method) && method.getName().equals(this.name);
    }
    
    static {
        CACHE = new Cache<Signature, Method>(Cache.Kind.SOFT, Cache.Kind.SOFT) {
            @Override
            public Method create(final Signature signature) {
                try {
                    return MethodFinder.findAccessibleMethod(new MethodFinder(signature.getName(), signature.getArgs(), null).find(signature.getType().getMethods()));
                }
                catch (final Exception ex) {
                    throw new SignatureException(ex);
                }
            }
        };
    }
}
