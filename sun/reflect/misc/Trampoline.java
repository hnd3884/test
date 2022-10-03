package sun.reflect.misc;

import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.lang.reflect.Method;

class Trampoline
{
    private static void ensureInvocableMethod(final Method method) throws InvocationTargetException {
        final Class<?> declaringClass = method.getDeclaringClass();
        if (declaringClass.equals(AccessController.class) || declaringClass.equals(Method.class) || declaringClass.getName().startsWith("java.lang.invoke.")) {
            throw new InvocationTargetException(new UnsupportedOperationException("invocation not supported"));
        }
    }
    
    private static Object invoke(final Method method, final Object o, final Object[] array) throws InvocationTargetException, IllegalAccessException {
        ensureInvocableMethod(method);
        return method.invoke(o, array);
    }
    
    static {
        if (Trampoline.class.getClassLoader() == null) {
            throw new Error("Trampoline must not be defined by the bootstrap classloader");
        }
    }
}
