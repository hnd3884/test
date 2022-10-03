package com.adventnet.tools.update;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.Vector;
import java.util.logging.Logger;

public class ClassLoaderUtil
{
    private static final Logger LOGGER;
    
    public static void unloadNativeLibraries() {
        try {
            final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            final Field field = ClassLoader.class.getDeclaredField("nativeLibraries");
            field.setAccessible(true);
            final Vector<Object> libs = (Vector<Object>)field.get(classLoader);
            for (final Object o : libs) {
                final Method finalize = o.getClass().getDeclaredMethod("finalize", (Class<?>[])new Class[0]);
                finalize.setAccessible(true);
                finalize.invoke(o, new Object[0]);
            }
        }
        catch (final Throwable t) {
            ClassLoaderUtil.LOGGER.log(Level.SEVERE, "Error unloading native resources.", t);
        }
    }
    
    static {
        LOGGER = Logger.getLogger(ClassLoader.class.getName());
    }
}
