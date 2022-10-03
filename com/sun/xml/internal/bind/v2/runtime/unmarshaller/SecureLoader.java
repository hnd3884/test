package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import java.security.AccessController;
import java.security.PrivilegedAction;

class SecureLoader
{
    static ClassLoader getContextClassLoader() {
        if (System.getSecurityManager() == null) {
            return Thread.currentThread().getContextClassLoader();
        }
        return AccessController.doPrivileged((PrivilegedAction<ClassLoader>)new PrivilegedAction() {
            @Override
            public Object run() {
                return Thread.currentThread().getContextClassLoader();
            }
        });
    }
    
    static ClassLoader getClassClassLoader(final Class c) {
        if (System.getSecurityManager() == null) {
            return c.getClassLoader();
        }
        return AccessController.doPrivileged((PrivilegedAction<ClassLoader>)new PrivilegedAction() {
            @Override
            public Object run() {
                return c.getClassLoader();
            }
        });
    }
    
    static ClassLoader getSystemClassLoader() {
        if (System.getSecurityManager() == null) {
            return ClassLoader.getSystemClassLoader();
        }
        return AccessController.doPrivileged((PrivilegedAction<ClassLoader>)new PrivilegedAction() {
            @Override
            public Object run() {
                return ClassLoader.getSystemClassLoader();
            }
        });
    }
}
