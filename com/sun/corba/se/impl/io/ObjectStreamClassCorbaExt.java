package com.sun.corba.se.impl.io;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.lang.reflect.Method;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.Remote;

class ObjectStreamClassCorbaExt
{
    static final boolean isAbstractInterface(final Class clazz) {
        if (!clazz.isInterface() || Remote.class.isAssignableFrom(clazz)) {
            return false;
        }
        final Method[] methods = clazz.getMethods();
        for (int i = 0; i < methods.length; ++i) {
            final Class<?>[] exceptionTypes = methods[i].getExceptionTypes();
            int n = 0;
            for (int n2 = 0; n2 < exceptionTypes.length && n == 0; ++n2) {
                if (RemoteException.class == exceptionTypes[n2] || Throwable.class == exceptionTypes[n2] || Exception.class == exceptionTypes[n2] || IOException.class == exceptionTypes[n2]) {
                    n = 1;
                }
            }
            if (n == 0) {
                return false;
            }
        }
        return true;
    }
    
    static final boolean isAny(final String s) {
        boolean b = false;
        if (s != null && (s.equals("Ljava/lang/Object;") || s.equals("Ljava/io/Serializable;") || s.equals("Ljava/io/Externalizable;"))) {
            b = true;
        }
        return b;
    }
    
    private static final Method[] getDeclaredMethods(final Class clazz) {
        return AccessController.doPrivileged((PrivilegedAction<Method[]>)new PrivilegedAction() {
            @Override
            public Object run() {
                return clazz.getDeclaredMethods();
            }
        });
    }
}
