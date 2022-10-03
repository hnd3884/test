package java.beans;

import sun.reflect.misc.ReflectUtil;
import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationTargetException;
import java.security.PrivilegedAction;
import java.lang.reflect.Method;
import sun.reflect.misc.MethodUtil;
import java.security.AccessController;
import java.security.AccessControlContext;
import java.lang.reflect.InvocationHandler;

public class EventHandler implements InvocationHandler
{
    private Object target;
    private String action;
    private final String eventPropertyName;
    private final String listenerMethodName;
    private final AccessControlContext acc;
    
    @ConstructorProperties({ "target", "action", "eventPropertyName", "listenerMethodName" })
    public EventHandler(final Object target, final String action, final String eventPropertyName, final String listenerMethodName) {
        this.acc = AccessController.getContext();
        this.target = target;
        this.action = action;
        if (target == null) {
            throw new NullPointerException("target must be non-null");
        }
        if (action == null) {
            throw new NullPointerException("action must be non-null");
        }
        this.eventPropertyName = eventPropertyName;
        this.listenerMethodName = listenerMethodName;
    }
    
    public Object getTarget() {
        return this.target;
    }
    
    public String getAction() {
        return this.action;
    }
    
    public String getEventPropertyName() {
        return this.eventPropertyName;
    }
    
    public String getListenerMethodName() {
        return this.listenerMethodName;
    }
    
    private Object applyGetters(final Object o, final String s) {
        if (s == null || s.equals("")) {
            return o;
        }
        int n = s.indexOf(46);
        if (n == -1) {
            n = s.length();
        }
        final String substring = s.substring(0, n);
        final String substring2 = s.substring(Math.min(n + 1, s.length()));
        try {
            Method method = null;
            if (o != null) {
                method = Statement.getMethod(o.getClass(), "get" + NameGenerator.capitalize(substring), (Class<?>[])new Class[0]);
                if (method == null) {
                    method = Statement.getMethod(o.getClass(), "is" + NameGenerator.capitalize(substring), (Class<?>[])new Class[0]);
                }
                if (method == null) {
                    method = Statement.getMethod(o.getClass(), substring, (Class<?>[])new Class[0]);
                }
            }
            if (method == null) {
                throw new RuntimeException("No method called: " + substring + " defined on " + o);
            }
            return this.applyGetters(MethodUtil.invoke(method, o, new Object[0]), substring2);
        }
        catch (final Exception ex) {
            throw new RuntimeException("Failed to call method: " + substring + " on " + o, ex);
        }
    }
    
    @Override
    public Object invoke(final Object o, final Method method, final Object[] array) {
        final AccessControlContext acc = this.acc;
        if (acc == null && System.getSecurityManager() != null) {
            throw new SecurityException("AccessControlContext is not set");
        }
        return AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                return EventHandler.this.invokeInternal(o, method, array);
            }
        }, acc);
    }
    
    private Object invokeInternal(final Object o, final Method method, final Object[] array) {
        final String name = method.getName();
        if (method.getDeclaringClass() == Object.class) {
            if (name.equals("hashCode")) {
                return new Integer(System.identityHashCode(o));
            }
            if (name.equals("equals")) {
                return (o == array[0]) ? Boolean.TRUE : Boolean.FALSE;
            }
            if (name.equals("toString")) {
                return o.getClass().getName() + '@' + Integer.toHexString(o.hashCode());
            }
        }
        if (this.listenerMethodName == null || this.listenerMethodName.equals(name)) {
            Object[] array2;
            Class[] array3;
            if (this.eventPropertyName == null) {
                array2 = new Object[0];
                array3 = new Class[0];
            }
            else {
                final Object applyGetters = this.applyGetters(array[0], this.getEventPropertyName());
                array2 = new Object[] { applyGetters };
                array3 = new Class[] { (applyGetters == null) ? null : applyGetters.getClass() };
            }
            try {
                final int lastIndex = this.action.lastIndexOf(46);
                if (lastIndex != -1) {
                    this.target = this.applyGetters(this.target, this.action.substring(0, lastIndex));
                    this.action = this.action.substring(lastIndex + 1);
                }
                Method method2 = Statement.getMethod(this.target.getClass(), this.action, (Class<?>[])array3);
                if (method2 == null) {
                    method2 = Statement.getMethod(this.target.getClass(), "set" + NameGenerator.capitalize(this.action), (Class<?>[])array3);
                }
                if (method2 == null) {
                    throw new RuntimeException("No method called " + this.action + " on " + this.target.getClass() + ((array3.length == 0) ? " with no arguments" : (" with argument " + array3[0])));
                }
                return MethodUtil.invoke(method2, this.target, array2);
            }
            catch (final IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
            catch (final InvocationTargetException ex2) {
                final Throwable targetException = ex2.getTargetException();
                throw (targetException instanceof RuntimeException) ? targetException : new RuntimeException(targetException);
            }
        }
        return null;
    }
    
    public static <T> T create(final Class<T> clazz, final Object o, final String s) {
        return create(clazz, o, s, null, null);
    }
    
    public static <T> T create(final Class<T> clazz, final Object o, final String s, final String s2) {
        return create(clazz, o, s, s2, null);
    }
    
    public static <T> T create(final Class<T> clazz, final Object o, final String s, final String s2, final String s3) {
        final EventHandler eventHandler = new EventHandler(o, s, s2, s3);
        if (clazz == null) {
            throw new NullPointerException("listenerInterface must be non-null");
        }
        return AccessController.doPrivileged((PrivilegedAction<T>)new PrivilegedAction<T>() {
            final /* synthetic */ ClassLoader val$loader = getClassLoader(clazz);
            final /* synthetic */ Class[] val$interfaces = { clazz };
            
            @Override
            public T run() {
                return (T)Proxy.newProxyInstance(this.val$loader, this.val$interfaces, eventHandler);
            }
        });
    }
    
    private static ClassLoader getClassLoader(final Class<?> clazz) {
        ReflectUtil.checkPackageAccess(clazz);
        ClassLoader classLoader = clazz.getClassLoader();
        if (classLoader == null) {
            classLoader = Thread.currentThread().getContextClassLoader();
            if (classLoader == null) {
                classLoader = ClassLoader.getSystemClassLoader();
            }
        }
        return classLoader;
    }
}
