package com.sun.jndi.toolkit.corba;

import javax.naming.NamingException;
import com.sun.jndi.cosnaming.CNCtx;
import javax.naming.Referenceable;
import javax.naming.Reference;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Hashtable;
import java.rmi.RemoteException;
import java.lang.reflect.InvocationTargetException;
import javax.naming.ConfigurationException;
import org.omg.CORBA.Object;
import org.omg.CORBA.ORB;
import java.rmi.Remote;
import java.lang.reflect.Method;

public class CorbaUtils
{
    private static Method toStubMethod;
    private static Method connectMethod;
    private static Class<?> corbaStubClass;
    
    public static org.omg.CORBA.Object remoteToCorba(final Remote remote, final ORB orb) throws ClassNotFoundException, ConfigurationException {
        synchronized (CorbaUtils.class) {
            if (CorbaUtils.toStubMethod == null) {
                initMethodHandles();
            }
        }
        Object invoke;
        try {
            invoke = CorbaUtils.toStubMethod.invoke(null, remote);
        }
        catch (final InvocationTargetException ex) {
            final Throwable targetException = ex.getTargetException();
            final ConfigurationException ex2 = new ConfigurationException("Problem with PortableRemoteObject.toStub(); object not exported or stub not found");
            ex2.setRootCause(targetException);
            throw ex2;
        }
        catch (final IllegalAccessException rootCause) {
            final ConfigurationException ex3 = new ConfigurationException("Cannot invoke javax.rmi.PortableRemoteObject.toStub(java.rmi.Remote)");
            ex3.setRootCause(rootCause);
            throw ex3;
        }
        if (!CorbaUtils.corbaStubClass.isInstance(invoke)) {
            return null;
        }
        try {
            CorbaUtils.connectMethod.invoke(invoke, orb);
        }
        catch (final InvocationTargetException ex4) {
            final Throwable targetException2 = ex4.getTargetException();
            if (!(targetException2 instanceof RemoteException)) {
                final ConfigurationException ex5 = new ConfigurationException("Problem invoking javax.rmi.CORBA.Stub.connect()");
                ex5.setRootCause(targetException2);
                throw ex5;
            }
        }
        catch (final IllegalAccessException rootCause2) {
            final ConfigurationException ex6 = new ConfigurationException("Cannot invoke javax.rmi.CORBA.Stub.connect()");
            ex6.setRootCause(rootCause2);
            throw ex6;
        }
        return (org.omg.CORBA.Object)invoke;
    }
    
    public static ORB getOrb(final String s, final int n, final Hashtable<?, ?> hashtable) {
        Properties properties;
        if (hashtable != null) {
            if (hashtable instanceof Properties) {
                properties = (Properties)hashtable.clone();
            }
            else {
                properties = new Properties();
                final Enumeration<?> keys = hashtable.keys();
                while (keys.hasMoreElements()) {
                    final String s2 = (String)keys.nextElement();
                    final Object value = hashtable.get(s2);
                    if (value instanceof String) {
                        ((Hashtable<String, Object>)properties).put(s2, value);
                    }
                }
            }
        }
        else {
            properties = new Properties();
        }
        if (s != null) {
            ((Hashtable<String, String>)properties).put("org.omg.CORBA.ORBInitialHost", s);
        }
        if (n >= 0) {
            ((Hashtable<String, String>)properties).put("org.omg.CORBA.ORBInitialPort", "" + n);
        }
        if (hashtable != null) {
            final Object value2 = hashtable.get("java.naming.applet");
            if (value2 != null) {
                return initAppletORB(value2, properties);
            }
        }
        return ORB.init(new String[0], properties);
    }
    
    public static boolean isObjectFactoryTrusted(final Object o) throws NamingException {
        Reference reference = null;
        if (o instanceof Reference) {
            reference = (Reference)o;
        }
        else if (o instanceof Referenceable) {
            reference = ((Referenceable)o).getReference();
        }
        if (reference != null && reference.getFactoryClassLocation() != null && !CNCtx.trustURLCodebase) {
            throw new ConfigurationException("The object factory is untrusted. Set the system property 'com.sun.jndi.cosnaming.object.trustURLCodebase' to 'true'.");
        }
        return true;
    }
    
    private static ORB initAppletORB(final Object o, final Properties properties) {
        try {
            final Class<?> forName = Class.forName("java.applet.Applet", true, null);
            if (!forName.isInstance(o)) {
                throw new ClassCastException(o.getClass().getName());
            }
            return (ORB)ORB.class.getMethod("init", forName, Properties.class).invoke(null, o, properties);
        }
        catch (final ClassNotFoundException ex) {
            throw new ClassCastException(o.getClass().getName());
        }
        catch (final NoSuchMethodException ex2) {
            throw new AssertionError((Object)ex2);
        }
        catch (final InvocationTargetException ex3) {
            final Throwable cause = ex3.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException)cause;
            }
            if (cause instanceof Error) {
                throw (Error)cause;
            }
            throw new AssertionError((Object)ex3);
        }
        catch (final IllegalAccessException ex4) {
            throw new AssertionError((Object)ex4);
        }
    }
    
    private static void initMethodHandles() throws ClassNotFoundException {
        CorbaUtils.corbaStubClass = Class.forName("javax.rmi.CORBA.Stub");
        try {
            CorbaUtils.connectMethod = CorbaUtils.corbaStubClass.getMethod("connect", ORB.class);
        }
        catch (final NoSuchMethodException ex) {
            throw new IllegalStateException("No method definition for javax.rmi.CORBA.Stub.connect(org.omg.CORBA.ORB)");
        }
        final Class<?> forName = Class.forName("javax.rmi.PortableRemoteObject");
        try {
            CorbaUtils.toStubMethod = forName.getMethod("toStub", Remote.class);
        }
        catch (final NoSuchMethodException ex2) {
            throw new IllegalStateException("No method definition for javax.rmi.PortableRemoteObject.toStub(java.rmi.Remote)");
        }
    }
    
    static {
        CorbaUtils.toStubMethod = null;
        CorbaUtils.connectMethod = null;
        CorbaUtils.corbaStubClass = null;
    }
}
