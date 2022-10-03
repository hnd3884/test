package com.sun.org.glassfish.gmbal;

import javax.management.ObjectName;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.lang.reflect.Method;
import com.sun.org.glassfish.gmbal.util.GenericConstructor;

public final class ManagedObjectManagerFactory
{
    private static GenericConstructor<ManagedObjectManager> objectNameCons;
    private static GenericConstructor<ManagedObjectManager> stringCons;
    
    private ManagedObjectManagerFactory() {
    }
    
    public static Method getMethod(final Class<?> cls, final String name, final Class<?>... types) {
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<Method>)new PrivilegedExceptionAction<Method>() {
                @Override
                public Method run() throws Exception {
                    return cls.getDeclaredMethod(name, (Class[])types);
                }
            });
        }
        catch (final PrivilegedActionException ex) {
            throw new GmbalException("Unexpected exception", ex);
        }
        catch (final SecurityException exc) {
            throw new GmbalException("Unexpected exception", exc);
        }
    }
    
    public static ManagedObjectManager createStandalone(final String domain) {
        final ManagedObjectManager result = ManagedObjectManagerFactory.stringCons.create(domain);
        if (result == null) {
            return ManagedObjectManagerNOPImpl.self;
        }
        return result;
    }
    
    public static ManagedObjectManager createFederated(final ObjectName rootParentName) {
        final ManagedObjectManager result = ManagedObjectManagerFactory.objectNameCons.create(rootParentName);
        if (result == null) {
            return ManagedObjectManagerNOPImpl.self;
        }
        return result;
    }
    
    public static ManagedObjectManager createNOOP() {
        return ManagedObjectManagerNOPImpl.self;
    }
    
    static {
        ManagedObjectManagerFactory.objectNameCons = new GenericConstructor<ManagedObjectManager>(ManagedObjectManager.class, "com.sun.org.glassfish.gmbal.impl.ManagedObjectManagerImpl", (Class<?>[])new Class[] { ObjectName.class });
        ManagedObjectManagerFactory.stringCons = new GenericConstructor<ManagedObjectManager>(ManagedObjectManager.class, "com.sun.org.glassfish.gmbal.impl.ManagedObjectManagerImpl", (Class<?>[])new Class[] { String.class });
    }
}
