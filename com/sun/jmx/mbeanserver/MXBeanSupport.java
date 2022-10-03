package com.sun.jmx.mbeanserver;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServer;
import java.util.Iterator;
import java.util.Set;
import javax.management.JMX;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

public class MXBeanSupport extends MBeanSupport<ConvertingMethod>
{
    private final Object lock;
    private MXBeanLookup mxbeanLookup;
    private ObjectName objectName;
    
    public <T> MXBeanSupport(final T t, final Class<T> clazz) throws NotCompliantMBeanException {
        super(t, clazz);
        this.lock = new Object();
    }
    
    @Override
    MBeanIntrospector<ConvertingMethod> getMBeanIntrospector() {
        return MXBeanIntrospector.getInstance();
    }
    
    @Override
    Object getCookie() {
        return this.mxbeanLookup;
    }
    
    static <T> Class<? super T> findMXBeanInterface(final Class<T> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Null resource class");
        }
        final Set<Class<?>> transitiveInterfaces = transitiveInterfaces(clazz);
        final Set<Object> set = Util.newSet();
        for (final Class clazz2 : transitiveInterfaces) {
            if (JMX.isMXBeanInterface(clazz2)) {
                set.add(clazz2);
            }
        }
    Label_0070:
        while (set.size() > 1) {
            for (final Class clazz3 : set) {
                final Iterator iterator3 = set.iterator();
                while (iterator3.hasNext()) {
                    final Class clazz4 = (Class)iterator3.next();
                    if (clazz3 != clazz4 && clazz4.isAssignableFrom(clazz3)) {
                        iterator3.remove();
                        continue Label_0070;
                    }
                }
            }
            throw new IllegalArgumentException("Class " + clazz.getName() + " implements more than one MXBean interface: " + set);
        }
        if (set.iterator().hasNext()) {
            return (Class<? super T>)Util.cast(set.iterator().next());
        }
        throw new IllegalArgumentException("Class " + clazz.getName() + " is not a JMX compliant MXBean");
    }
    
    private static Set<Class<?>> transitiveInterfaces(final Class<?> clazz) {
        final Set<Object> set = Util.newSet();
        transitiveInterfaces(clazz, (Set<Class<?>>)set);
        return (Set<Class<?>>)set;
    }
    
    private static void transitiveInterfaces(final Class<?> clazz, final Set<Class<?>> set) {
        if (clazz == null) {
            return;
        }
        if (clazz.isInterface()) {
            set.add(clazz);
        }
        transitiveInterfaces(clazz.getSuperclass(), set);
        final Class[] interfaces = clazz.getInterfaces();
        for (int length = interfaces.length, i = 0; i < length; ++i) {
            transitiveInterfaces(interfaces[i], set);
        }
    }
    
    @Override
    public void register(final MBeanServer mBeanServer, final ObjectName objectName) throws InstanceAlreadyExistsException {
        if (objectName == null) {
            throw new IllegalArgumentException("Null object name");
        }
        synchronized (this.lock) {
            (this.mxbeanLookup = MXBeanLookup.lookupFor(mBeanServer)).addReference(objectName, this.getResource());
            this.objectName = objectName;
        }
    }
    
    @Override
    public void unregister() {
        synchronized (this.lock) {
            if (this.mxbeanLookup != null && this.mxbeanLookup.removeReference(this.objectName, this.getResource())) {
                this.objectName = null;
            }
        }
    }
}
