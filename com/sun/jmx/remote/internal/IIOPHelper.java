package com.sun.jmx.remote.internal;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.util.Properties;
import java.io.IOException;

public final class IIOPHelper
{
    private static final String IMPL_CLASS = "com.sun.jmx.remote.protocol.iiop.IIOPProxyImpl";
    private static final IIOPProxy proxy;
    
    private IIOPHelper() {
    }
    
    public static boolean isAvailable() {
        return IIOPHelper.proxy != null;
    }
    
    private static void ensureAvailable() {
        if (IIOPHelper.proxy == null) {
            throw new AssertionError((Object)"Should not here");
        }
    }
    
    public static boolean isStub(final Object o) {
        return IIOPHelper.proxy != null && IIOPHelper.proxy.isStub(o);
    }
    
    public static Object getDelegate(final Object o) {
        ensureAvailable();
        return IIOPHelper.proxy.getDelegate(o);
    }
    
    public static void setDelegate(final Object o, final Object o2) {
        ensureAvailable();
        IIOPHelper.proxy.setDelegate(o, o2);
    }
    
    public static Object getOrb(final Object o) {
        ensureAvailable();
        return IIOPHelper.proxy.getOrb(o);
    }
    
    public static void connect(final Object o, final Object o2) throws IOException {
        if (IIOPHelper.proxy == null) {
            throw new IOException("Connection to ORB failed, RMI/IIOP not available");
        }
        IIOPHelper.proxy.connect(o, o2);
    }
    
    public static boolean isOrb(final Object o) {
        return IIOPHelper.proxy != null && IIOPHelper.proxy.isOrb(o);
    }
    
    public static Object createOrb(final String[] array, final Properties properties) throws IOException {
        if (IIOPHelper.proxy == null) {
            throw new IOException("ORB initialization failed, RMI/IIOP not available");
        }
        return IIOPHelper.proxy.createOrb(array, properties);
    }
    
    public static Object stringToObject(final Object o, final String s) {
        ensureAvailable();
        return IIOPHelper.proxy.stringToObject(o, s);
    }
    
    public static String objectToString(final Object o, final Object o2) {
        ensureAvailable();
        return IIOPHelper.proxy.objectToString(o, o2);
    }
    
    public static <T> T narrow(final Object o, final Class<T> clazz) {
        ensureAvailable();
        return IIOPHelper.proxy.narrow(o, clazz);
    }
    
    public static void exportObject(final Remote remote) throws IOException {
        if (IIOPHelper.proxy == null) {
            throw new IOException("RMI object cannot be exported, RMI/IIOP not available");
        }
        IIOPHelper.proxy.exportObject(remote);
    }
    
    public static void unexportObject(final Remote remote) throws IOException {
        if (IIOPHelper.proxy == null) {
            throw new NoSuchObjectException("Object not exported");
        }
        IIOPHelper.proxy.unexportObject(remote);
    }
    
    public static Remote toStub(final Remote remote) throws IOException {
        if (IIOPHelper.proxy == null) {
            throw new NoSuchObjectException("Object not exported");
        }
        return IIOPHelper.proxy.toStub(remote);
    }
    
    static {
        proxy = AccessController.doPrivileged((PrivilegedAction<IIOPProxy>)new PrivilegedAction<IIOPProxy>() {
            @Override
            public IIOPProxy run() {
                try {
                    return (IIOPProxy)Class.forName("com.sun.jmx.remote.protocol.iiop.IIOPProxyImpl", true, IIOPHelper.class.getClassLoader()).newInstance();
                }
                catch (final ClassNotFoundException ex) {
                    return null;
                }
                catch (final InstantiationException ex2) {
                    throw new AssertionError((Object)ex2);
                }
                catch (final IllegalAccessException ex3) {
                    throw new AssertionError((Object)ex3);
                }
            }
        });
    }
}
