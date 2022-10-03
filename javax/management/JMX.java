package javax.management;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import com.sun.jmx.mbeanserver.Introspector;
import java.lang.reflect.Modifier;

public class JMX
{
    static final JMX proof;
    public static final String DEFAULT_VALUE_FIELD = "defaultValue";
    public static final String IMMUTABLE_INFO_FIELD = "immutableInfo";
    public static final String INTERFACE_CLASS_NAME_FIELD = "interfaceClassName";
    public static final String LEGAL_VALUES_FIELD = "legalValues";
    public static final String MAX_VALUE_FIELD = "maxValue";
    public static final String MIN_VALUE_FIELD = "minValue";
    public static final String MXBEAN_FIELD = "mxbean";
    public static final String OPEN_TYPE_FIELD = "openType";
    public static final String ORIGINAL_TYPE_FIELD = "originalType";
    
    private JMX() {
    }
    
    public static <T> T newMBeanProxy(final MBeanServerConnection mBeanServerConnection, final ObjectName objectName, final Class<T> clazz) {
        return newMBeanProxy(mBeanServerConnection, objectName, clazz, false);
    }
    
    public static <T> T newMBeanProxy(final MBeanServerConnection mBeanServerConnection, final ObjectName objectName, final Class<T> clazz, final boolean b) {
        return createProxy(mBeanServerConnection, objectName, clazz, b, false);
    }
    
    public static <T> T newMXBeanProxy(final MBeanServerConnection mBeanServerConnection, final ObjectName objectName, final Class<T> clazz) {
        return newMXBeanProxy(mBeanServerConnection, objectName, clazz, false);
    }
    
    public static <T> T newMXBeanProxy(final MBeanServerConnection mBeanServerConnection, final ObjectName objectName, final Class<T> clazz, final boolean b) {
        return createProxy(mBeanServerConnection, objectName, clazz, b, true);
    }
    
    public static boolean isMXBeanInterface(final Class<?> clazz) {
        if (!clazz.isInterface()) {
            return false;
        }
        if (!Modifier.isPublic(clazz.getModifiers()) && !Introspector.ALLOW_NONPUBLIC_MBEAN) {
            return false;
        }
        final MXBean mxBean = clazz.getAnnotation(MXBean.class);
        if (mxBean != null) {
            return mxBean.value();
        }
        return clazz.getName().endsWith("MXBean");
    }
    
    private static <T> T createProxy(final MBeanServerConnection mBeanServerConnection, final ObjectName objectName, final Class<T> clazz, final boolean b, final boolean b2) {
        try {
            if (b2) {
                Introspector.testComplianceMXBeanInterface(clazz);
            }
            else {
                Introspector.testComplianceMBeanInterface(clazz);
            }
        }
        catch (final NotCompliantMBeanException ex) {
            throw new IllegalArgumentException(ex);
        }
        final MBeanServerInvocationHandler mBeanServerInvocationHandler = new MBeanServerInvocationHandler(mBeanServerConnection, objectName, b2);
        Class[] array;
        if (b) {
            array = new Class[] { clazz, NotificationEmitter.class };
        }
        else {
            array = new Class[] { clazz };
        }
        return (T)clazz.cast(Proxy.newProxyInstance(clazz.getClassLoader(), array, mBeanServerInvocationHandler));
    }
    
    static {
        proof = new JMX();
    }
}
