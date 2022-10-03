package com.sun.jmx.mbeanserver;

import javax.management.NotificationBroadcasterSupport;
import javax.management.NotificationBroadcaster;
import javax.management.ImmutableDescriptor;
import javax.management.Descriptor;
import javax.management.MBeanOperationInfo;
import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import java.lang.reflect.InvocationTargetException;
import sun.reflect.misc.MethodUtil;
import java.lang.reflect.Type;
import javax.management.NotCompliantMBeanException;
import java.util.WeakHashMap;
import java.lang.reflect.Method;

class StandardMBeanIntrospector extends MBeanIntrospector<Method>
{
    private static final StandardMBeanIntrospector instance;
    private static final WeakHashMap<Class<?>, Boolean> definitelyImmutable;
    private static final PerInterfaceMap<Method> perInterfaceMap;
    private static final MBeanInfoMap mbeanInfoMap;
    
    static StandardMBeanIntrospector getInstance() {
        return StandardMBeanIntrospector.instance;
    }
    
    @Override
    PerInterfaceMap<Method> getPerInterfaceMap() {
        return StandardMBeanIntrospector.perInterfaceMap;
    }
    
    @Override
    MBeanInfoMap getMBeanInfoMap() {
        return StandardMBeanIntrospector.mbeanInfoMap;
    }
    
    @Override
    MBeanAnalyzer<Method> getAnalyzer(final Class<?> clazz) throws NotCompliantMBeanException {
        return MBeanAnalyzer.analyzer(clazz, (MBeanIntrospector<Method>)this);
    }
    
    @Override
    boolean isMXBean() {
        return false;
    }
    
    @Override
    Method mFrom(final Method method) {
        return method;
    }
    
    @Override
    String getName(final Method method) {
        return method.getName();
    }
    
    @Override
    Type getGenericReturnType(final Method method) {
        return method.getGenericReturnType();
    }
    
    @Override
    Type[] getGenericParameterTypes(final Method method) {
        return method.getGenericParameterTypes();
    }
    
    @Override
    String[] getSignature(final Method method) {
        final Class<?>[] parameterTypes = method.getParameterTypes();
        final String[] array = new String[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; ++i) {
            array[i] = parameterTypes[i].getName();
        }
        return array;
    }
    
    @Override
    void checkMethod(final Method method) {
    }
    
    @Override
    Object invokeM2(final Method method, final Object o, final Object[] array, final Object o2) throws InvocationTargetException, IllegalAccessException, MBeanException {
        return MethodUtil.invoke(method, o, array);
    }
    
    @Override
    boolean validParameter(final Method method, final Object o, final int n, final Object o2) {
        return MBeanIntrospector.isValidParameter(method, o, n);
    }
    
    @Override
    MBeanAttributeInfo getMBeanAttributeInfo(final String s, final Method method, final Method method2) {
        try {
            return new MBeanAttributeInfo(s, "Attribute exposed for management", method, method2);
        }
        catch (final IntrospectionException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    @Override
    MBeanOperationInfo getMBeanOperationInfo(final String s, final Method method) {
        return new MBeanOperationInfo("Operation exposed for management", method);
    }
    
    @Override
    Descriptor getBasicMBeanDescriptor() {
        return ImmutableDescriptor.EMPTY_DESCRIPTOR;
    }
    
    @Override
    Descriptor getMBeanDescriptor(final Class<?> clazz) {
        return new ImmutableDescriptor(new String[] { "mxbean=false", "immutableInfo=" + isDefinitelyImmutableInfo(clazz) });
    }
    
    static boolean isDefinitelyImmutableInfo(final Class<?> clazz) {
        if (!NotificationBroadcaster.class.isAssignableFrom(clazz)) {
            return true;
        }
        synchronized (StandardMBeanIntrospector.definitelyImmutable) {
            Boolean b = StandardMBeanIntrospector.definitelyImmutable.get(clazz);
            if (b == null) {
                final Class<NotificationBroadcasterSupport> clazz2 = NotificationBroadcasterSupport.class;
                Label_0087: {
                    if (clazz2.isAssignableFrom(clazz)) {
                        try {
                            b = (clazz.getMethod("getNotificationInfo", (Class[])new Class[0]).getDeclaringClass() == clazz2);
                            break Label_0087;
                        }
                        catch (final Exception ex) {
                            return false;
                        }
                    }
                    b = false;
                }
                StandardMBeanIntrospector.definitelyImmutable.put(clazz, b);
            }
            return b;
        }
    }
    
    static {
        instance = new StandardMBeanIntrospector();
        definitelyImmutable = new WeakHashMap<Class<?>, Boolean>();
        perInterfaceMap = new PerInterfaceMap<Method>();
        mbeanInfoMap = new MBeanInfoMap();
    }
}
