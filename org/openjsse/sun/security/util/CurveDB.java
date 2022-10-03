package org.openjsse.sun.security.util;

import java.lang.reflect.InvocationTargetException;
import java.security.spec.ECParameterSpec;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.Optional;

public class CurveDB
{
    private static Optional<Method> lookupByName;
    private static Optional<Method> lookupByParam;
    private static Object lookupByNameLock;
    private static Object lookupByParamLock;
    
    private static void makeAccessible(final AccessibleObject o) {
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                o.setAccessible(true);
                return null;
            }
        });
    }
    
    public static ECParameterSpec lookup(final String name) {
        synchronized (CurveDB.lookupByNameLock) {
            if (CurveDB.lookupByName == null) {
                CurveDB.lookupByName = AccessController.doPrivileged((PrivilegedAction<Optional<Method>>)new PrivilegedAction<Object>() {
                    @Override
                    public Optional<Method> run() {
                        Optional<Method> lookupByName = null;
                        try {
                            Class clazz = null;
                            try {
                                clazz = Class.forName("sun.security.ec.CurveDB");
                            }
                            catch (final ClassNotFoundException cnfe) {
                                clazz = Class.forName("sun.security.util.CurveDB");
                            }
                            lookupByName = Optional.ofNullable(clazz.getDeclaredMethod("lookup", String.class));
                            makeAccessible(lookupByName.get());
                            return lookupByName;
                        }
                        catch (final ClassNotFoundException | NoSuchMethodException | SecurityException e) {
                            lookupByName = Optional.empty();
                            return lookupByName;
                        }
                    }
                });
            }
        }
        if (CurveDB.lookupByName.isPresent()) {
            try {
                return (ECParameterSpec)CurveDB.lookupByName.get().invoke(null, name);
            }
            catch (final IllegalAccessException | InvocationTargetException ex) {}
        }
        return null;
    }
    
    public static ECParameterSpec lookup(final ECParameterSpec params) {
        synchronized (CurveDB.lookupByParamLock) {
            if (CurveDB.lookupByParam == null) {
                CurveDB.lookupByParam = AccessController.doPrivileged((PrivilegedAction<Optional<Method>>)new PrivilegedAction<Object>() {
                    @Override
                    public Optional<Method> run() {
                        Optional<Method> lookupByParam = null;
                        try {
                            Class clazz = null;
                            try {
                                clazz = Class.forName("sun.security.ec.CurveDB");
                            }
                            catch (final ClassNotFoundException cnfe) {
                                clazz = Class.forName("sun.security.util.CurveDB");
                            }
                            lookupByParam = Optional.ofNullable(clazz.getDeclaredMethod("lookup", ECParameterSpec.class));
                            makeAccessible(lookupByParam.get());
                        }
                        catch (final ClassNotFoundException | NoSuchMethodException | SecurityException e) {
                            lookupByParam = Optional.empty();
                        }
                        return lookupByParam;
                    }
                });
            }
        }
        if (CurveDB.lookupByParam.isPresent()) {
            try {
                return (ECParameterSpec)CurveDB.lookupByParam.get().invoke(null, params);
            }
            catch (final IllegalAccessException | InvocationTargetException ex) {}
        }
        return null;
    }
    
    static {
        CurveDB.lookupByName = null;
        CurveDB.lookupByParam = null;
        CurveDB.lookupByNameLock = new Object();
        CurveDB.lookupByParamLock = new Object();
    }
}
