package java.beans;

import com.sun.beans.finder.MethodFinder;
import java.lang.reflect.Executable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Constructor;
import sun.reflect.misc.MethodUtil;
import java.lang.reflect.Method;
import com.sun.beans.finder.ConstructorFinder;
import java.lang.reflect.Array;
import com.sun.beans.finder.ClassFinder;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.AccessController;
import java.security.AccessControlContext;

public class Statement
{
    private static Object[] emptyArray;
    static ExceptionListener defaultExceptionListener;
    private final AccessControlContext acc;
    private final Object target;
    private final String methodName;
    private final Object[] arguments;
    ClassLoader loader;
    
    @ConstructorProperties({ "target", "methodName", "arguments" })
    public Statement(final Object target, final String methodName, final Object[] array) {
        this.acc = AccessController.getContext();
        this.target = target;
        this.methodName = methodName;
        this.arguments = ((array == null) ? Statement.emptyArray : array.clone());
    }
    
    public Object getTarget() {
        return this.target;
    }
    
    public String getMethodName() {
        return this.methodName;
    }
    
    public Object[] getArguments() {
        return this.arguments.clone();
    }
    
    public void execute() throws Exception {
        this.invoke();
    }
    
    Object invoke() throws Exception {
        final AccessControlContext acc = this.acc;
        if (acc == null && System.getSecurityManager() != null) {
            throw new SecurityException("AccessControlContext is not set");
        }
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Object>() {
                @Override
                public Object run() throws Exception {
                    return Statement.this.invokeInternal();
                }
            }, acc);
        }
        catch (final PrivilegedActionException ex) {
            throw ex.getException();
        }
    }
    
    private Object invokeInternal() throws Exception {
        final Object target = this.getTarget();
        String methodName = this.getMethodName();
        if (target == null || methodName == null) {
            throw new NullPointerException(((target == null) ? "target" : "methodName") + " should not be null");
        }
        Object[] array = this.getArguments();
        if (array == null) {
            array = Statement.emptyArray;
        }
        if (target == Class.class && methodName.equals("forName")) {
            return ClassFinder.resolveClass((String)array[0], this.loader);
        }
        final Class[] array2 = new Class[array.length];
        for (int i = 0; i < array.length; ++i) {
            array2[i] = ((array[i] == null) ? null : array[i].getClass());
        }
        Executable executable = null;
        if (target instanceof Class) {
            if (methodName.equals("new")) {
                methodName = "newInstance";
            }
            if (methodName.equals("newInstance") && ((Class<Class>)target).isArray()) {
                final Object instance = Array.newInstance(((Class<Class>)target).getComponentType(), array.length);
                for (int j = 0; j < array.length; ++j) {
                    Array.set(instance, j, array[j]);
                }
                return instance;
            }
            if (methodName.equals("newInstance") && array.length != 0) {
                if (target == Character.class && array.length == 1 && array2[0] == String.class) {
                    return new Character(((String)array[0]).charAt(0));
                }
                try {
                    executable = ConstructorFinder.findConstructor((Class<?>)target, (Class<?>[])array2);
                }
                catch (final NoSuchMethodException ex) {
                    executable = null;
                }
            }
            if (executable == null && target != Class.class) {
                executable = getMethod((Class<?>)target, methodName, (Class<?>[])array2);
            }
            if (executable == null) {
                executable = getMethod(Class.class, methodName, (Class<?>[])array2);
            }
        }
        else if (((Class<Class>)target).getClass().isArray() && (methodName.equals("set") || methodName.equals("get"))) {
            final int intValue = (int)array[0];
            if (methodName.equals("get")) {
                return Array.get(target, intValue);
            }
            Array.set(target, intValue, array[1]);
            return null;
        }
        else {
            executable = getMethod(((Class<Class>)target).getClass(), methodName, (Class<?>[])array2);
        }
        if (executable != null) {
            try {
                if (executable instanceof Method) {
                    return MethodUtil.invoke((Method)executable, target, array);
                }
                return ((Constructor<Object>)executable).newInstance(array);
            }
            catch (final IllegalAccessException ex2) {
                throw new Exception("Statement cannot invoke: " + methodName + " on " + ((Class<Class>)target).getClass(), ex2);
            }
            catch (final InvocationTargetException ex3) {
                final Throwable targetException = ex3.getTargetException();
                if (targetException instanceof Exception) {
                    throw (Exception)targetException;
                }
                throw ex3;
            }
        }
        throw new NoSuchMethodException(this.toString());
    }
    
    String instanceName(final Object o) {
        if (o == null) {
            return "null";
        }
        if (o.getClass() == String.class) {
            return "\"" + (String)o + "\"";
        }
        return NameGenerator.unqualifiedClassName(o.getClass());
    }
    
    @Override
    public String toString() {
        final Object target = this.getTarget();
        final String methodName = this.getMethodName();
        Object[] array = this.getArguments();
        if (array == null) {
            array = Statement.emptyArray;
        }
        final StringBuffer sb = new StringBuffer(this.instanceName(target) + "." + methodName + "(");
        for (int length = array.length, i = 0; i < length; ++i) {
            sb.append(this.instanceName(array[i]));
            if (i != length - 1) {
                sb.append(", ");
            }
        }
        sb.append(");");
        return sb.toString();
    }
    
    static Method getMethod(final Class<?> clazz, final String s, final Class<?>... array) {
        try {
            return MethodFinder.findMethod(clazz, s, array);
        }
        catch (final NoSuchMethodException ex) {
            return null;
        }
    }
    
    static {
        Statement.emptyArray = new Object[0];
        Statement.defaultExceptionListener = new ExceptionListener() {
            @Override
            public void exceptionThrown(final Exception ex) {
                System.err.println(ex);
                System.err.println("Continuing ...");
            }
        };
    }
}
