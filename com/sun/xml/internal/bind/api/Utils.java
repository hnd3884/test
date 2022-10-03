package com.sun.xml.internal.bind.api;

import java.util.logging.Level;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import java.util.logging.Logger;

final class Utils
{
    private static final Logger LOGGER;
    static final Navigator<Type, Class, Field, Method> REFLECTION_NAVIGATOR;
    
    private Utils() {
    }
    
    static {
        LOGGER = Logger.getLogger(Utils.class.getName());
        try {
            final Class refNav = Class.forName("com.sun.xml.internal.bind.v2.model.nav.ReflectionNavigator");
            final Method getInstance = AccessController.doPrivileged((PrivilegedAction<Method>)new PrivilegedAction<Method>() {
                @Override
                public Method run() {
                    try {
                        final Method getInstance = refNav.getDeclaredMethod("getInstance", (Class[])new Class[0]);
                        getInstance.setAccessible(true);
                        return getInstance;
                    }
                    catch (final NoSuchMethodException e) {
                        throw new IllegalStateException("ReflectionNavigator.getInstance can't be found");
                    }
                }
            });
            REFLECTION_NAVIGATOR = (Navigator)getInstance.invoke(null, new Object[0]);
        }
        catch (final ClassNotFoundException e) {
            throw new IllegalStateException("Can't find ReflectionNavigator class");
        }
        catch (final InvocationTargetException e2) {
            throw new IllegalStateException("ReflectionNavigator.getInstance throws the exception");
        }
        catch (final IllegalAccessException e3) {
            throw new IllegalStateException("ReflectionNavigator.getInstance method is inaccessible");
        }
        catch (final SecurityException e4) {
            Utils.LOGGER.log(Level.FINE, "Unable to access ReflectionNavigator.getInstance", e4);
            throw e4;
        }
    }
}
