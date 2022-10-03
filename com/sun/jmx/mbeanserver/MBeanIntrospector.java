package com.sun.jmx.mbeanserver;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import javax.management.MBeanConstructorInfo;
import javax.management.NotificationBroadcaster;
import javax.management.ImmutableDescriptor;
import java.util.WeakHashMap;
import javax.management.MBeanNotificationInfo;
import java.lang.reflect.Array;
import javax.management.InvalidAttributeValueException;
import javax.management.ReflectionException;
import javax.management.MBeanInfo;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import sun.reflect.misc.ReflectUtil;
import java.util.List;
import javax.management.Descriptor;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.lang.reflect.Method;
import javax.management.NotCompliantMBeanException;

abstract class MBeanIntrospector<M>
{
    abstract PerInterfaceMap<M> getPerInterfaceMap();
    
    abstract MBeanInfoMap getMBeanInfoMap();
    
    abstract MBeanAnalyzer<M> getAnalyzer(final Class<?> p0) throws NotCompliantMBeanException;
    
    abstract boolean isMXBean();
    
    abstract M mFrom(final Method p0);
    
    abstract String getName(final M p0);
    
    abstract Type getGenericReturnType(final M p0);
    
    abstract Type[] getGenericParameterTypes(final M p0);
    
    abstract String[] getSignature(final M p0);
    
    abstract void checkMethod(final M p0);
    
    abstract Object invokeM2(final M p0, final Object p1, final Object[] p2, final Object p3) throws InvocationTargetException, IllegalAccessException, MBeanException;
    
    abstract boolean validParameter(final M p0, final Object p1, final int p2, final Object p3);
    
    abstract MBeanAttributeInfo getMBeanAttributeInfo(final String p0, final M p1, final M p2);
    
    abstract MBeanOperationInfo getMBeanOperationInfo(final String p0, final M p1);
    
    abstract Descriptor getBasicMBeanDescriptor();
    
    abstract Descriptor getMBeanDescriptor(final Class<?> p0);
    
    final List<Method> getMethods(final Class<?> clazz) {
        ReflectUtil.checkPackageAccess(clazz);
        return Arrays.asList(clazz.getMethods());
    }
    
    final PerInterface<M> getPerInterface(final Class<?> clazz) throws NotCompliantMBeanException {
        final PerInterfaceMap<M> perInterfaceMap = this.getPerInterfaceMap();
        synchronized (perInterfaceMap) {
            final WeakReference weakReference = perInterfaceMap.get(clazz);
            PerInterface perInterface = (weakReference == null) ? null : ((PerInterface)weakReference.get());
            if (perInterface == null) {
                try {
                    final MBeanAnalyzer<M> analyzer = this.getAnalyzer(clazz);
                    perInterface = new PerInterface(clazz, (MBeanIntrospector<Object>)this, (MBeanAnalyzer<Object>)analyzer, this.makeInterfaceMBeanInfo(clazz, analyzer));
                    perInterfaceMap.put(clazz, (WeakReference)new WeakReference<PerInterface<M>>(perInterface));
                }
                catch (final Exception ex) {
                    throw Introspector.throwException(clazz, ex);
                }
            }
            return perInterface;
        }
    }
    
    private MBeanInfo makeInterfaceMBeanInfo(final Class<?> clazz, final MBeanAnalyzer<M> mBeanAnalyzer) {
        final MBeanInfoMaker mBeanInfoMaker = new MBeanInfoMaker();
        mBeanAnalyzer.visit(mBeanInfoMaker);
        return mBeanInfoMaker.makeMBeanInfo(clazz, "Information on the management interface of the MBean");
    }
    
    final boolean consistent(final M m, final M i) {
        return m == null || i == null || this.getGenericReturnType(m).equals(this.getGenericParameterTypes(i)[0]);
    }
    
    final Object invokeM(final M m, final Object o, final Object[] array, final Object o2) throws MBeanException, ReflectionException {
        try {
            return this.invokeM2(m, o, array, o2);
        }
        catch (final InvocationTargetException ex) {
            unwrapInvocationTargetException(ex);
            throw new RuntimeException(ex);
        }
        catch (final IllegalAccessException ex2) {
            throw new ReflectionException(ex2, ex2.toString());
        }
    }
    
    final void invokeSetter(final String s, final M m, final Object o, final Object o2, final Object o3) throws MBeanException, ReflectionException, InvalidAttributeValueException {
        try {
            this.invokeM2(m, o, new Object[] { o2 }, o3);
        }
        catch (final IllegalAccessException ex) {
            throw new ReflectionException(ex, ex.toString());
        }
        catch (final RuntimeException ex2) {
            this.maybeInvalidParameter(s, m, o2, o3);
            throw ex2;
        }
        catch (final InvocationTargetException ex3) {
            this.maybeInvalidParameter(s, m, o2, o3);
            unwrapInvocationTargetException(ex3);
        }
    }
    
    private void maybeInvalidParameter(final String s, final M m, final Object o, final Object o2) throws InvalidAttributeValueException {
        if (!this.validParameter(m, o, 0, o2)) {
            throw new InvalidAttributeValueException("Invalid value for attribute " + s + ": " + o);
        }
    }
    
    static boolean isValidParameter(final Method method, final Object o, final int n) {
        final Class<?> clazz = method.getParameterTypes()[n];
        try {
            Array.set(Array.newInstance(clazz, 1), 0, o);
            return true;
        }
        catch (final IllegalArgumentException ex) {
            return false;
        }
    }
    
    private static void unwrapInvocationTargetException(final InvocationTargetException ex) throws MBeanException {
        final Throwable cause = ex.getCause();
        if (cause instanceof RuntimeException) {
            throw (RuntimeException)cause;
        }
        if (cause instanceof Error) {
            throw (Error)cause;
        }
        throw new MBeanException((Exception)cause, (cause == null) ? null : cause.toString());
    }
    
    final MBeanInfo getMBeanInfo(final Object o, final PerInterface<M> perInterface) {
        final MBeanInfo classMBeanInfo = this.getClassMBeanInfo(o.getClass(), perInterface);
        final MBeanNotificationInfo[] notifications = findNotifications(o);
        if (notifications == null || notifications.length == 0) {
            return classMBeanInfo;
        }
        return new MBeanInfo(classMBeanInfo.getClassName(), classMBeanInfo.getDescription(), classMBeanInfo.getAttributes(), classMBeanInfo.getConstructors(), classMBeanInfo.getOperations(), notifications, classMBeanInfo.getDescriptor());
    }
    
    final MBeanInfo getClassMBeanInfo(final Class<?> clazz, final PerInterface<M> perInterface) {
        final MBeanInfoMap mBeanInfoMap = this.getMBeanInfoMap();
        synchronized (mBeanInfoMap) {
            WeakHashMap weakHashMap = ((WeakHashMap<K, WeakHashMap>)mBeanInfoMap).get(clazz);
            if (weakHashMap == null) {
                weakHashMap = new WeakHashMap();
                ((WeakHashMap<K, WeakHashMap>)mBeanInfoMap).put((K)clazz, weakHashMap);
            }
            final Class<?> mBeanInterface = perInterface.getMBeanInterface();
            MBeanInfo mBeanInfo = (MBeanInfo)weakHashMap.get(mBeanInterface);
            if (mBeanInfo == null) {
                final MBeanInfo mBeanInfo2 = perInterface.getMBeanInfo();
                mBeanInfo = new MBeanInfo(clazz.getName(), mBeanInfo2.getDescription(), mBeanInfo2.getAttributes(), findConstructors(clazz), mBeanInfo2.getOperations(), null, ImmutableDescriptor.union(mBeanInfo2.getDescriptor(), this.getMBeanDescriptor(clazz)));
                weakHashMap.put(mBeanInterface, mBeanInfo);
            }
            return mBeanInfo;
        }
    }
    
    static MBeanNotificationInfo[] findNotifications(final Object o) {
        if (!(o instanceof NotificationBroadcaster)) {
            return null;
        }
        final MBeanNotificationInfo[] notificationInfo = ((NotificationBroadcaster)o).getNotificationInfo();
        if (notificationInfo == null) {
            return null;
        }
        final MBeanNotificationInfo[] array = new MBeanNotificationInfo[notificationInfo.length];
        for (int i = 0; i < notificationInfo.length; ++i) {
            MBeanNotificationInfo mBeanNotificationInfo = notificationInfo[i];
            if (mBeanNotificationInfo.getClass() != MBeanNotificationInfo.class) {
                mBeanNotificationInfo = (MBeanNotificationInfo)mBeanNotificationInfo.clone();
            }
            array[i] = mBeanNotificationInfo;
        }
        return array;
    }
    
    private static MBeanConstructorInfo[] findConstructors(final Class<?> clazz) {
        final Constructor<?>[] constructors = clazz.getConstructors();
        final MBeanConstructorInfo[] array = new MBeanConstructorInfo[constructors.length];
        for (int i = 0; i < constructors.length; ++i) {
            array[i] = new MBeanConstructorInfo("Public constructor of the MBean", constructors[i]);
        }
        return array;
    }
    
    static final class PerInterfaceMap<M> extends WeakHashMap<Class<?>, WeakReference<PerInterface<M>>>
    {
    }
    
    private class MBeanInfoMaker implements MBeanAnalyzer.MBeanVisitor<M>
    {
        private final List<MBeanAttributeInfo> attrs;
        private final List<MBeanOperationInfo> ops;
        
        private MBeanInfoMaker() {
            this.attrs = Util.newList();
            this.ops = Util.newList();
        }
        
        @Override
        public void visitAttribute(final String s, final M m, final M i) {
            this.attrs.add(MBeanIntrospector.this.getMBeanAttributeInfo(s, m, i));
        }
        
        @Override
        public void visitOperation(final String s, final M m) {
            this.ops.add(MBeanIntrospector.this.getMBeanOperationInfo(s, m));
        }
        
        MBeanInfo makeMBeanInfo(final Class<?> clazz, final String s) {
            return new MBeanInfo(clazz.getName(), s, this.attrs.toArray(new MBeanAttributeInfo[0]), null, this.ops.toArray(new MBeanOperationInfo[0]), null, DescriptorCache.getInstance().union(new ImmutableDescriptor(new String[] { "interfaceClassName=" + clazz.getName() }), MBeanIntrospector.this.getBasicMBeanDescriptor(), Introspector.descriptorForElement(clazz)));
        }
    }
    
    static class MBeanInfoMap extends WeakHashMap<Class<?>, WeakHashMap<Class<?>, MBeanInfo>>
    {
    }
}
