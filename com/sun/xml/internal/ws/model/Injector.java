package com.sun.xml.internal.ws.model;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.net.URL;
import java.lang.reflect.InvocationTargetException;
import javax.xml.ws.WebServiceException;
import java.util.logging.Level;
import java.lang.reflect.Method;
import java.util.logging.Logger;

final class Injector
{
    private static final Logger LOGGER;
    private static final Method defineClass;
    private static final Method resolveClass;
    private static final Method getPackage;
    private static final Method definePackage;
    
    static synchronized Class inject(final ClassLoader cl, final String className, final byte[] image) {
        try {
            return cl.loadClass(className);
        }
        catch (final ClassNotFoundException ex) {
            try {
                final int packIndex = className.lastIndexOf(46);
                if (packIndex != -1) {
                    final String pkgname = className.substring(0, packIndex);
                    final Package pkg = (Package)Injector.getPackage.invoke(cl, pkgname);
                    if (pkg == null) {
                        Injector.definePackage.invoke(cl, pkgname, null, null, null, null, null, null, null);
                    }
                }
                final Class c = (Class)Injector.defineClass.invoke(cl, className.replace('/', '.'), image, 0, image.length);
                Injector.resolveClass.invoke(cl, c);
                return c;
            }
            catch (final IllegalAccessException e) {
                Injector.LOGGER.log(Level.FINE, "Unable to inject " + className, e);
                throw new WebServiceException(e);
            }
            catch (final InvocationTargetException e2) {
                Injector.LOGGER.log(Level.FINE, "Unable to inject " + className, e2);
                throw new WebServiceException(e2);
            }
        }
    }
    
    static {
        LOGGER = Logger.getLogger(Injector.class.getName());
        try {
            defineClass = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, Integer.TYPE, Integer.TYPE);
            resolveClass = ClassLoader.class.getDeclaredMethod("resolveClass", Class.class);
            getPackage = ClassLoader.class.getDeclaredMethod("getPackage", String.class);
            definePackage = ClassLoader.class.getDeclaredMethod("definePackage", String.class, String.class, String.class, String.class, String.class, String.class, String.class, URL.class);
        }
        catch (final NoSuchMethodException e) {
            throw new NoSuchMethodError(e.getMessage());
        }
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                Injector.defineClass.setAccessible(true);
                Injector.resolveClass.setAccessible(true);
                Injector.getPackage.setAccessible(true);
                Injector.definePackage.setAccessible(true);
                return null;
            }
        });
    }
}
