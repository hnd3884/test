package javax.management;

import java.util.Arrays;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import com.sun.jmx.mbeanserver.MXBeanProxy;
import java.lang.ref.WeakReference;
import java.util.WeakHashMap;
import java.lang.reflect.InvocationHandler;

public class MBeanServerInvocationHandler implements InvocationHandler
{
    private static final WeakHashMap<Class<?>, WeakReference<MXBeanProxy>> mxbeanProxies;
    private final MBeanServerConnection connection;
    private final ObjectName objectName;
    private final boolean isMXBean;
    
    public MBeanServerInvocationHandler(final MBeanServerConnection mBeanServerConnection, final ObjectName objectName) {
        this(mBeanServerConnection, objectName, false);
    }
    
    public MBeanServerInvocationHandler(final MBeanServerConnection connection, final ObjectName objectName, final boolean isMXBean) {
        if (connection == null) {
            throw new IllegalArgumentException("Null connection");
        }
        if (Proxy.isProxyClass(connection.getClass()) && MBeanServerInvocationHandler.class.isAssignableFrom(Proxy.getInvocationHandler(connection).getClass())) {
            throw new IllegalArgumentException("Wrapping MBeanServerInvocationHandler");
        }
        if (objectName == null) {
            throw new IllegalArgumentException("Null object name");
        }
        this.connection = connection;
        this.objectName = objectName;
        this.isMXBean = isMXBean;
    }
    
    public MBeanServerConnection getMBeanServerConnection() {
        return this.connection;
    }
    
    public ObjectName getObjectName() {
        return this.objectName;
    }
    
    public boolean isMXBean() {
        return this.isMXBean;
    }
    
    public static <T> T newProxyInstance(final MBeanServerConnection mBeanServerConnection, final ObjectName objectName, final Class<T> clazz, final boolean b) {
        return JMX.newMBeanProxy(mBeanServerConnection, objectName, clazz, b);
    }
    
    @Override
    public Object invoke(final Object o, final Method method, final Object[] array) throws Throwable {
        final Class<?> declaringClass = method.getDeclaringClass();
        if (declaringClass.equals(NotificationBroadcaster.class) || declaringClass.equals(NotificationEmitter.class)) {
            return this.invokeBroadcasterMethod(o, method, array);
        }
        if (this.shouldDoLocally(o, method)) {
            return this.doLocally(o, method, array);
        }
        try {
            if (this.isMXBean()) {
                return findMXBeanProxy(declaringClass).invoke(this.connection, this.objectName, method, array);
            }
            final String name = method.getName();
            final Class<?>[] parameterTypes = method.getParameterTypes();
            final Class<?> returnType = method.getReturnType();
            final int n = (array == null) ? 0 : array.length;
            if (name.startsWith("get") && name.length() > 3 && n == 0 && !returnType.equals(Void.TYPE)) {
                return this.connection.getAttribute(this.objectName, name.substring(3));
            }
            if (name.startsWith("is") && name.length() > 2 && n == 0 && (returnType.equals(Boolean.TYPE) || returnType.equals(Boolean.class))) {
                return this.connection.getAttribute(this.objectName, name.substring(2));
            }
            if (name.startsWith("set") && name.length() > 3 && n == 1 && returnType.equals(Void.TYPE)) {
                this.connection.setAttribute(this.objectName, new Attribute(name.substring(3), array[0]));
                return null;
            }
            final String[] array2 = new String[parameterTypes.length];
            for (int i = 0; i < parameterTypes.length; ++i) {
                array2[i] = parameterTypes[i].getName();
            }
            return this.connection.invoke(this.objectName, name, array, array2);
        }
        catch (final MBeanException ex) {
            throw ex.getTargetException();
        }
        catch (final RuntimeMBeanException ex2) {
            throw ex2.getTargetException();
        }
        catch (final RuntimeErrorException ex3) {
            throw ex3.getTargetError();
        }
    }
    
    private static MXBeanProxy findMXBeanProxy(final Class<?> clazz) {
        synchronized (MBeanServerInvocationHandler.mxbeanProxies) {
            final WeakReference weakReference = MBeanServerInvocationHandler.mxbeanProxies.get(clazz);
            MXBeanProxy mxBeanProxy = (weakReference == null) ? null : ((MXBeanProxy)weakReference.get());
            if (mxBeanProxy == null) {
                try {
                    mxBeanProxy = new MXBeanProxy(clazz);
                }
                catch (final IllegalArgumentException ex) {
                    final IllegalArgumentException ex2 = new IllegalArgumentException("Cannot make MXBean proxy for " + clazz.getName() + ": " + ex.getMessage(), ex.getCause());
                    ex2.setStackTrace(ex.getStackTrace());
                    throw ex2;
                }
                MBeanServerInvocationHandler.mxbeanProxies.put(clazz, new WeakReference<MXBeanProxy>(mxBeanProxy));
            }
            return mxBeanProxy;
        }
    }
    
    private Object invokeBroadcasterMethod(final Object o, final Method method, final Object[] array) throws Exception {
        final String name = method.getName();
        final int n = (array == null) ? 0 : array.length;
        if (name.equals("addNotificationListener")) {
            if (n != 3) {
                throw new IllegalArgumentException("Bad arg count to addNotificationListener: " + n);
            }
            this.connection.addNotificationListener(this.objectName, (NotificationListener)array[0], (NotificationFilter)array[1], array[2]);
            return null;
        }
        else if (name.equals("removeNotificationListener")) {
            final NotificationListener notificationListener = (NotificationListener)array[0];
            switch (n) {
                case 1: {
                    this.connection.removeNotificationListener(this.objectName, notificationListener);
                    return null;
                }
                case 3: {
                    this.connection.removeNotificationListener(this.objectName, notificationListener, (NotificationFilter)array[1], array[2]);
                    return null;
                }
                default: {
                    throw new IllegalArgumentException("Bad arg count to removeNotificationListener: " + n);
                }
            }
        }
        else {
            if (!name.equals("getNotificationInfo")) {
                throw new IllegalArgumentException("Bad method name: " + name);
            }
            if (array != null) {
                throw new IllegalArgumentException("getNotificationInfo has args");
            }
            return this.connection.getMBeanInfo(this.objectName).getNotifications();
        }
    }
    
    private boolean shouldDoLocally(final Object o, final Method method) {
        final String name = method.getName();
        return ((name.equals("hashCode") || name.equals("toString")) && method.getParameterTypes().length == 0 && isLocal(o, method)) || (name.equals("equals") && Arrays.equals(method.getParameterTypes(), new Class[] { Object.class }) && isLocal(o, method)) || (name.equals("finalize") && method.getParameterTypes().length == 0);
    }
    
    private Object doLocally(final Object o, final Method method, final Object[] array) {
        final String name = method.getName();
        if (name.equals("equals")) {
            if (this == array[0]) {
                return true;
            }
            if (!(array[0] instanceof Proxy)) {
                return false;
            }
            final InvocationHandler invocationHandler = Proxy.getInvocationHandler(array[0]);
            if (invocationHandler == null || !(invocationHandler instanceof MBeanServerInvocationHandler)) {
                return false;
            }
            final MBeanServerInvocationHandler mBeanServerInvocationHandler = (MBeanServerInvocationHandler)invocationHandler;
            return this.connection.equals(mBeanServerInvocationHandler.connection) && this.objectName.equals(mBeanServerInvocationHandler.objectName) && o.getClass().equals(array[0].getClass());
        }
        else {
            if (name.equals("toString")) {
                return (this.isMXBean() ? "MX" : "M") + "BeanProxy(" + this.connection + "[" + this.objectName + "])";
            }
            if (name.equals("hashCode")) {
                return this.objectName.hashCode() + this.connection.hashCode();
            }
            if (name.equals("finalize")) {
                return null;
            }
            throw new RuntimeException("Unexpected method name: " + name);
        }
    }
    
    private static boolean isLocal(final Object o, final Method method) {
        final Class<?>[] interfaces = o.getClass().getInterfaces();
        if (interfaces == null) {
            return true;
        }
        final String name = method.getName();
        final Class<?>[] parameterTypes = method.getParameterTypes();
        final Class<?>[] array = interfaces;
        final int length = array.length;
        int i = 0;
        while (i < length) {
            final Class<?> clazz = array[i];
            try {
                clazz.getMethod(name, parameterTypes);
                return false;
            }
            catch (final NoSuchMethodException ex) {
                ++i;
                continue;
            }
            break;
        }
        return true;
    }
    
    static {
        mxbeanProxies = new WeakHashMap<Class<?>, WeakReference<MXBeanProxy>>();
    }
}
