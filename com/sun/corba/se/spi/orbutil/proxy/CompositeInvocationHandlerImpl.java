package com.sun.corba.se.spi.orbutil.proxy;

import java.security.Permission;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import com.sun.corba.se.impl.presentation.rmi.DynamicAccessPermission;
import java.lang.reflect.InvocationHandler;
import java.util.Map;

public class CompositeInvocationHandlerImpl implements CompositeInvocationHandler
{
    private Map classToInvocationHandler;
    private InvocationHandler defaultHandler;
    private static final DynamicAccessPermission perm;
    private static final long serialVersionUID = 4571178305984833743L;
    
    public CompositeInvocationHandlerImpl() {
        this.classToInvocationHandler = new LinkedHashMap();
        this.defaultHandler = null;
    }
    
    @Override
    public void addInvocationHandler(final Class clazz, final InvocationHandler invocationHandler) {
        this.checkAccess();
        this.classToInvocationHandler.put(clazz, invocationHandler);
    }
    
    @Override
    public void setDefaultHandler(final InvocationHandler defaultHandler) {
        this.checkAccess();
        this.defaultHandler = defaultHandler;
    }
    
    @Override
    public Object invoke(final Object o, final Method method, final Object[] array) throws Throwable {
        InvocationHandler defaultHandler = this.classToInvocationHandler.get(method.getDeclaringClass());
        if (defaultHandler == null) {
            if (this.defaultHandler == null) {
                throw ORBUtilSystemException.get("util").noInvocationHandler("\"" + method.toString() + "\"");
            }
            defaultHandler = this.defaultHandler;
        }
        return defaultHandler.invoke(o, method, array);
    }
    
    private void checkAccess() {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(CompositeInvocationHandlerImpl.perm);
        }
    }
    
    static {
        perm = new DynamicAccessPermission("access");
    }
}
