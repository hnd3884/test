package java.rmi.server;

import sun.rmi.server.Util;
import java.util.WeakHashMap;
import sun.rmi.server.WeakClassHashMap;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;
import java.io.InvalidObjectException;
import java.rmi.UnexpectedException;
import java.rmi.RemoteException;
import java.rmi.Remote;
import java.lang.reflect.Proxy;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationHandler;

public class RemoteObjectInvocationHandler extends RemoteObject implements InvocationHandler
{
    private static final long serialVersionUID = 2L;
    private static final boolean allowFinalizeInvocation;
    private static final MethodToHash_Maps methodToHash_Maps;
    
    public RemoteObjectInvocationHandler(final RemoteRef remoteRef) {
        super(remoteRef);
        if (remoteRef == null) {
            throw new NullPointerException();
        }
    }
    
    @Override
    public Object invoke(final Object o, final Method method, final Object[] array) throws Throwable {
        if (!Proxy.isProxyClass(o.getClass())) {
            throw new IllegalArgumentException("not a proxy");
        }
        if (Proxy.getInvocationHandler(o) != this) {
            throw new IllegalArgumentException("handler mismatch");
        }
        if (method.getDeclaringClass() == Object.class) {
            return this.invokeObjectMethod(o, method, array);
        }
        if ("finalize".equals(method.getName()) && method.getParameterCount() == 0 && !RemoteObjectInvocationHandler.allowFinalizeInvocation) {
            return null;
        }
        return this.invokeRemoteMethod(o, method, array);
    }
    
    private Object invokeObjectMethod(final Object o, final Method method, final Object[] array) {
        final String name = method.getName();
        if (name.equals("hashCode")) {
            return this.hashCode();
        }
        if (name.equals("equals")) {
            final Object o2 = array[0];
            final InvocationHandler invocationHandler;
            return o == o2 || (o2 != null && Proxy.isProxyClass(o2.getClass()) && (invocationHandler = Proxy.getInvocationHandler(o2)) instanceof RemoteObjectInvocationHandler && this.equals(invocationHandler));
        }
        if (name.equals("toString")) {
            return this.proxyToString(o);
        }
        throw new IllegalArgumentException("unexpected Object method: " + method);
    }
    
    private Object invokeRemoteMethod(final Object o, Method method, final Object[] array) throws Exception {
        try {
            if (!(o instanceof Remote)) {
                throw new IllegalArgumentException("proxy not Remote instance");
            }
            final Class<?> declaringClass = method.getDeclaringClass();
            if (!Remote.class.isAssignableFrom(declaringClass)) {
                throw new RemoteException("Method is not Remote: " + declaringClass + "::" + method);
            }
            return this.ref.invoke((Remote)o, method, array, getMethodHash(method));
        }
        catch (final Exception ex) {
            if (!(ex instanceof RuntimeException)) {
                final Class<?> class1 = o.getClass();
                try {
                    method = class1.getMethod(method.getName(), method.getParameterTypes());
                }
                catch (final NoSuchMethodException ex2) {
                    throw (IllegalArgumentException)new IllegalArgumentException().initCause(ex2);
                }
                final Class<? extends UnexpectedException> class2 = ex.getClass();
                final Class<?>[] exceptionTypes = method.getExceptionTypes();
                for (int length = exceptionTypes.length, i = 0; i < length; ++i) {
                    if (exceptionTypes[i].isAssignableFrom(class2)) {
                        throw ex;
                    }
                }
                ex = new UnexpectedException("unexpected exception", ex);
            }
            throw ex;
        }
    }
    
    private String proxyToString(final Object o) {
        final Class<?>[] interfaces = o.getClass().getInterfaces();
        if (interfaces.length == 0) {
            return "Proxy[" + this + "]";
        }
        String s = interfaces[0].getName();
        if (s.equals("java.rmi.Remote") && interfaces.length > 1) {
            s = interfaces[1].getName();
        }
        final int lastIndex = s.lastIndexOf(46);
        if (lastIndex >= 0) {
            s = s.substring(lastIndex + 1);
        }
        return "Proxy[" + s + "," + this + "]";
    }
    
    private void readObjectNoData() throws InvalidObjectException {
        throw new InvalidObjectException("no data in stream; class: " + this.getClass().getName());
    }
    
    private static long getMethodHash(final Method method) {
        return (long)RemoteObjectInvocationHandler.methodToHash_Maps.get(method.getDeclaringClass()).get(method);
    }
    
    static {
        final String s = AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction<String>() {
            @Override
            public String run() {
                return System.getProperty("sun.rmi.server.invocationhandler.allowFinalizeInvocation");
            }
        });
        if ("".equals(s)) {
            allowFinalizeInvocation = true;
        }
        else {
            allowFinalizeInvocation = Boolean.parseBoolean(s);
        }
        methodToHash_Maps = new MethodToHash_Maps();
    }
    
    private static class MethodToHash_Maps extends WeakClassHashMap<Map<Method, Long>>
    {
        MethodToHash_Maps() {
        }
        
        @Override
        protected Map<Method, Long> computeValue(final Class<?> clazz) {
            return new WeakHashMap<Method, Long>() {
                @Override
                public synchronized Long get(final Object o) {
                    Long value = super.get(o);
                    if (value == null) {
                        final Method method = (Method)o;
                        value = Util.computeMethodHash(method);
                        this.put(method, value);
                    }
                    return value;
                }
            };
        }
    }
}
