package com.sun.jmx.mbeanserver;

import javax.management.InstanceAlreadyExistsException;
import java.security.PrivilegedAction;
import java.security.AccessController;
import java.lang.reflect.InvocationHandler;
import javax.management.openmbean.OpenDataException;
import javax.management.MBeanServerInvocationHandler;
import java.lang.reflect.Proxy;
import javax.management.JMX;
import java.lang.ref.WeakReference;
import java.util.Map;
import javax.management.ObjectName;
import javax.management.MBeanServerConnection;

public class MXBeanLookup
{
    private static final ThreadLocal<MXBeanLookup> currentLookup;
    private final MBeanServerConnection mbsc;
    private final WeakIdentityHashMap<Object, ObjectName> mxbeanToObjectName;
    private final Map<ObjectName, WeakReference<Object>> objectNameToProxy;
    private static final WeakIdentityHashMap<MBeanServerConnection, WeakReference<MXBeanLookup>> mbscToLookup;
    
    private MXBeanLookup(final MBeanServerConnection mbsc) {
        this.mxbeanToObjectName = WeakIdentityHashMap.make();
        this.objectNameToProxy = Util.newMap();
        this.mbsc = mbsc;
    }
    
    static MXBeanLookup lookupFor(final MBeanServerConnection mBeanServerConnection) {
        synchronized (MXBeanLookup.mbscToLookup) {
            final WeakReference weakReference = MXBeanLookup.mbscToLookup.get(mBeanServerConnection);
            MXBeanLookup mxBeanLookup = (weakReference == null) ? null : ((MXBeanLookup)weakReference.get());
            if (mxBeanLookup == null) {
                mxBeanLookup = new MXBeanLookup(mBeanServerConnection);
                MXBeanLookup.mbscToLookup.put(mBeanServerConnection, new WeakReference<MXBeanLookup>(mxBeanLookup));
            }
            return mxBeanLookup;
        }
    }
    
    synchronized <T> T objectNameToMXBean(final ObjectName objectName, final Class<T> clazz) {
        final WeakReference weakReference = this.objectNameToProxy.get(objectName);
        if (weakReference != null) {
            final Object value = weakReference.get();
            if (clazz.isInstance(value)) {
                return (T)clazz.cast(value);
            }
        }
        final T mxBeanProxy = JMX.newMXBeanProxy(this.mbsc, objectName, clazz);
        this.objectNameToProxy.put(objectName, new WeakReference<Object>(mxBeanProxy));
        return mxBeanProxy;
    }
    
    synchronized ObjectName mxbeanToObjectName(final Object o) throws OpenDataException {
        String s;
        if (o instanceof Proxy) {
            final InvocationHandler invocationHandler = Proxy.getInvocationHandler(o);
            if (invocationHandler instanceof MBeanServerInvocationHandler) {
                final MBeanServerInvocationHandler mBeanServerInvocationHandler = (MBeanServerInvocationHandler)invocationHandler;
                if (mBeanServerInvocationHandler.getMBeanServerConnection().equals(this.mbsc)) {
                    return mBeanServerInvocationHandler.getObjectName();
                }
                s = "proxy for a different MBeanServer";
            }
            else {
                s = "not a JMX proxy";
            }
        }
        else {
            final ObjectName objectName = this.mxbeanToObjectName.get(o);
            if (objectName != null) {
                return objectName;
            }
            s = "not an MXBean registered in this MBeanServer";
        }
        throw new OpenDataException("Could not convert " + ((o == null) ? "null" : ("object of type " + o.getClass().getName())) + " to an ObjectName: " + s);
    }
    
    synchronized void addReference(final ObjectName objectName, final Object o) throws InstanceAlreadyExistsException {
        final ObjectName objectName2 = this.mxbeanToObjectName.get(o);
        if (objectName2 != null && !"true".equalsIgnoreCase(AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("jmx.mxbean.multiname")))) {
            throw new InstanceAlreadyExistsException("MXBean already registered with name " + objectName2);
        }
        this.mxbeanToObjectName.put(o, objectName);
    }
    
    synchronized boolean removeReference(final ObjectName objectName, final Object o) {
        if (objectName.equals(this.mxbeanToObjectName.get(o))) {
            this.mxbeanToObjectName.remove(o);
            return true;
        }
        return false;
    }
    
    static MXBeanLookup getLookup() {
        return MXBeanLookup.currentLookup.get();
    }
    
    static void setLookup(final MXBeanLookup mxBeanLookup) {
        MXBeanLookup.currentLookup.set(mxBeanLookup);
    }
    
    static {
        currentLookup = new ThreadLocal<MXBeanLookup>();
        mbscToLookup = WeakIdentityHashMap.make();
    }
}
