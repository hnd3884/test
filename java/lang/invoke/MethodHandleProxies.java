package java.lang.invoke;

import java.util.ArrayList;
import sun.reflect.CallerSensitive;
import java.security.AccessController;
import java.lang.reflect.Proxy;
import java.security.PrivilegedAction;
import sun.invoke.WrapperInstance;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationHandler;
import java.util.concurrent.ConcurrentHashMap;
import sun.reflect.misc.ReflectUtil;
import sun.reflect.Reflection;
import java.lang.reflect.Modifier;

public class MethodHandleProxies
{
    private MethodHandleProxies() {
    }
    
    @CallerSensitive
    public static <T> T asInterfaceInstance(final Class<T> clazz, final MethodHandle methodHandle) {
        if (!clazz.isInterface() || !Modifier.isPublic(clazz.getModifiers())) {
            throw MethodHandleStatics.newIllegalArgumentException("not a public interface", clazz.getName());
        }
        MethodHandle methodHandle2;
        if (System.getSecurityManager() != null) {
            final Class<?> callerClass = Reflection.getCallerClass();
            final ClassLoader classLoader = (callerClass != null) ? callerClass.getClassLoader() : null;
            ReflectUtil.checkProxyPackageAccess(classLoader, clazz);
            methodHandle2 = ((classLoader != null) ? bindCaller(methodHandle, callerClass) : methodHandle);
        }
        else {
            methodHandle2 = methodHandle;
        }
        ClassLoader classLoader2 = clazz.getClassLoader();
        if (classLoader2 == null) {
            final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            classLoader2 = ((contextClassLoader != null) ? contextClassLoader : ClassLoader.getSystemClassLoader());
        }
        final Method[] singleNameMethods = getSingleNameMethods(clazz);
        if (singleNameMethods == null) {
            throw MethodHandleStatics.newIllegalArgumentException("not a single-method interface", clazz.getName());
        }
        final MethodHandle[] array = new MethodHandle[singleNameMethods.length];
        for (int i = 0; i < singleNameMethods.length; ++i) {
            final Method method = singleNameMethods[i];
            final MethodType methodType = MethodType.methodType(method.getReturnType(), method.getParameterTypes());
            final MethodHandle type = methodHandle2.asType(methodType);
            array[i] = type.asType(type.type().changeReturnType(Object.class)).asSpreader(Object[].class, methodType.parameterCount());
        }
        final InvocationHandler invocationHandler = new InvocationHandler() {
            final /* synthetic */ ConcurrentHashMap val$defaultMethodMap = hasDefaultMethods(clazz) ? new ConcurrentHashMap() : null;
            
            private Object getArg(final String s) {
                if (s == "getWrapperInstanceTarget") {
                    return methodHandle;
                }
                if (s == "getWrapperInstanceType") {
                    return clazz;
                }
                throw new AssertionError();
            }
            
            @Override
            public Object invoke(final Object o, final Method method, final Object[] array) throws Throwable {
                for (int i = 0; i < singleNameMethods.length; ++i) {
                    if (method.equals(singleNameMethods[i])) {
                        return array[i].invokeExact(array);
                    }
                }
                if (method.getDeclaringClass() == WrapperInstance.class) {
                    return this.getArg(method.getName());
                }
                if (isObjectMethod(method)) {
                    return callObjectMethod(o, method, array);
                }
                if (isDefaultMethod(method)) {
                    return callDefaultMethod(this.val$defaultMethodMap, o, clazz, method, array);
                }
                throw MethodHandleStatics.newInternalError("bad proxy method: " + method);
            }
        };
        Object o;
        if (System.getSecurityManager() != null) {
            o = AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
                @Override
                public Object run() {
                    return Proxy.newProxyInstance(classLoader2, new Class[] { clazz, WrapperInstance.class }, invocationHandler);
                }
            });
        }
        else {
            o = Proxy.newProxyInstance(classLoader2, new Class[] { clazz, WrapperInstance.class }, invocationHandler);
        }
        return (T)clazz.cast(o);
    }
    
    private static MethodHandle bindCaller(final MethodHandle methodHandle, final Class<?> clazz) {
        final MethodHandle bindCaller = MethodHandleImpl.bindCaller(methodHandle, clazz);
        if (methodHandle.isVarargsCollector()) {
            final MethodType type = bindCaller.type();
            return bindCaller.asVarargsCollector(type.parameterType(type.parameterCount() - 1));
        }
        return bindCaller;
    }
    
    public static boolean isWrapperInstance(final Object o) {
        return o instanceof WrapperInstance;
    }
    
    private static WrapperInstance asWrapperInstance(final Object o) {
        try {
            if (o != null) {
                return (WrapperInstance)o;
            }
        }
        catch (final ClassCastException ex) {}
        throw MethodHandleStatics.newIllegalArgumentException("not a wrapper instance");
    }
    
    public static MethodHandle wrapperInstanceTarget(final Object o) {
        return asWrapperInstance(o).getWrapperInstanceTarget();
    }
    
    public static Class<?> wrapperInstanceType(final Object o) {
        return asWrapperInstance(o).getWrapperInstanceType();
    }
    
    private static boolean isObjectMethod(final Method method) {
        final String name = method.getName();
        switch (name) {
            case "toString": {
                return method.getReturnType() == String.class && method.getParameterTypes().length == 0;
            }
            case "hashCode": {
                return method.getReturnType() == Integer.TYPE && method.getParameterTypes().length == 0;
            }
            case "equals": {
                return method.getReturnType() == Boolean.TYPE && method.getParameterTypes().length == 1 && method.getParameterTypes()[0] == Object.class;
            }
            default: {
                return false;
            }
        }
    }
    
    private static Object callObjectMethod(final Object o, final Method method, final Object[] array) {
        assert isObjectMethod(method) : method;
        final String name = method.getName();
        switch (name) {
            case "toString": {
                return o.getClass().getName() + "@" + Integer.toHexString(o.hashCode());
            }
            case "hashCode": {
                return System.identityHashCode(o);
            }
            case "equals": {
                return o == array[0];
            }
            default: {
                return null;
            }
        }
    }
    
    private static Method[] getSingleNameMethods(final Class<?> clazz) {
        final ArrayList list = new ArrayList();
        String s = null;
        for (final Method method : clazz.getMethods()) {
            if (!isObjectMethod(method)) {
                if (Modifier.isAbstract(method.getModifiers())) {
                    final String name = method.getName();
                    if (s == null) {
                        s = name;
                    }
                    else if (!s.equals(name)) {
                        return null;
                    }
                    list.add(method);
                }
            }
        }
        if (s == null) {
            return null;
        }
        return list.toArray(new Method[list.size()]);
    }
    
    private static boolean isDefaultMethod(final Method method) {
        return !Modifier.isAbstract(method.getModifiers());
    }
    
    private static boolean hasDefaultMethods(final Class<?> clazz) {
        for (final Method method : clazz.getMethods()) {
            if (!isObjectMethod(method) && !Modifier.isAbstract(method.getModifiers())) {
                return true;
            }
        }
        return false;
    }
    
    private static Object callDefaultMethod(final ConcurrentHashMap<Method, MethodHandle> concurrentHashMap, final Object o, final Class<?> clazz, final Method method, final Object[] array) throws Throwable {
        assert isDefaultMethod(method) && !isObjectMethod(method) : method;
        return concurrentHashMap.computeIfAbsent(method, method2 -> {
            try {
                return MethodHandles.Lookup.IMPL_LOOKUP.findSpecial(clazz2, method2.getName(), MethodType.methodType(method2.getReturnType(), method2.getParameterTypes()), o2.getClass()).asSpreader(Object[].class, method2.getParameterCount());
            }
            catch (final NoSuchMethodException | IllegalAccessException ex) {
                throw new InternalError((Throwable)ex);
            }
        }).invoke(o, array);
    }
}
