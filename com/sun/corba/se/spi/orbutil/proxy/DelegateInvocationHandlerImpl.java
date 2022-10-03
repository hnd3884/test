package com.sun.corba.se.spi.orbutil.proxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.Permission;
import com.sun.corba.se.impl.presentation.rmi.DynamicAccessPermission;
import java.lang.reflect.InvocationHandler;

public abstract class DelegateInvocationHandlerImpl
{
    private DelegateInvocationHandlerImpl() {
    }
    
    public static InvocationHandler create(final Object o) {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new DynamicAccessPermission("access"));
        }
        return new InvocationHandler() {
            @Override
            public Object invoke(final Object o, final Method method, final Object[] array) throws Throwable {
                try {
                    return method.invoke(o, array);
                }
                catch (final InvocationTargetException ex) {
                    throw ex.getCause();
                }
            }
        };
    }
}
